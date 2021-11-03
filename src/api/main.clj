(ns api.main
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
            [integrant.core :as ig]))

(defn read-config [profile]
  (aero/read-config "config.edn" {:profile profile}))

(def system-config
  {:api/config  {}
   :api/jetty   {:port    4000
                 :join?   false
                 :handler (ig/ref :api/handler)}
   :api/handler {:config (ig/ref :api/config)}
   :api/tasks   {:config (ig/ref :api/config)}})

(defmethod ig/init-key :api/config [_ _]
  ;; can be set :prod or :test to load diff configs at runtime
  (read-config :dev))

(defmethod ig/init-key :api/jetty [_ {:keys [port join? handler]}]
  (println (format "server listening on port: %d" port))
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
    {:data {:github-token (-> config :api/config :github-token)
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
  (api.cron/start (-> config :api/config :github-token)))

(defn run-migrations [args]
  (case (:direction args)
    "up" (db/create-releases-table! db/config)
    "down" (db/drop-releases-table! db/config)))

(defn -main []
  (ig/init system-config))