(ns api.main
  (:gen-class)
  (:require [api.cron]
            [api.db :as db]
            [api.routes :as routes]
            [aero.core :as aero]
            [ring.adapter.jetty :as jetty]
            [muuntaja.core :as m]
            [reitit.ring.coercion :as rrc]
            [reitit.coercion.spec]
            [reitit.ring.middleware.exception :as exm]
            [reitit.ring.middleware.parameters :as parm]
            [reitit.ring.middleware.muuntaja :as muu]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.coercion.schema]
            [reitit.ring :as ring]
            [integrant.core :as ig]
            [clojure.tools.logging :as log]))

(defn read-config [profile]
  (aero/read-config "config.edn" {:profile profile}))

(defn resolve-github-secret [config]
  (or (-> config :api/config :github-token)
      (System/getenv "GITHUB_TOKEN")))

(def system-config
  {:api/config  {}
   :api/jetty   {:port    8080
                 :join?   false
                 :handler (ig/ref :api/handler)}
   :api/handler {:config (ig/ref :api/config)}
   :api/tasks   {:config (ig/ref :api/config)}})

(defmethod ig/init-key :api/config [_ _]
  ;; can be set :prod or :test to load diff configs at runtime
  (read-config (or (-> "ENVIRONMENT" (System/getenv) keyword) :dev)))

(defmethod ig/init-key :api/jetty [_ {:keys [port join? handler]}]
  (log/info (format "server listening on port: %d" port))
  (jetty/run-jetty handler {:port port :join? join?}))

;; side-effect-al
(defmethod ig/halt-key! :api/jetty [_ server]
  (.stop server))

(defmethod ig/init-key :api/handler [_ {:keys [config]}]
  (ring/ring-handler
   (ring/router
    [routes/ping
     routes/swagger
     routes/api]
    {:data {:github-token (resolve-github-secret config)
            :coercion reitit.coercion.schema/coercion
            :muuntaja    m/instance
            :middleware  [parm/parameters-middleware
                          muu/format-middleware
                          exm/exception-middleware
                          rrc/coerce-exceptions-middleware
                          rrc/coerce-request-middleware
                          rrc/coerce-response-middleware]}})
   (ring/routes
    (ring/redirect-trailing-slash-handler)
    (swagger-ui/create-swagger-ui-handler
     {:path "/"})
    (ring/create-default-handler
     {:not-found (constantly {:status 404 :body "route not found"})}))))

(defmethod ig/init-key :api/tasks [_ {:keys [config]}]
  (api.cron/start (resolve-github-secret config)
                  (-> config :api/config :refresh-interval)))

(defn run-migrations-up []
  (db/create-releases-table! db/config))

(defmethod ig/prep-key :api/jetty [_ config]
   (merge {:port 8080} config))

(defn -main []
  (-> system-config ig/prep ig/init))