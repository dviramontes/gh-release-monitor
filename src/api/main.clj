(ns api.main
  (:require  [aero.core :as aero]
             [api.routes :as routes]
             [ring.adapter.jetty :as jetty]
             [muuntaja.core :as m]
             [reitit.ring.coercion :as rrc]
             [reitit.coercion.spec]
             [reitit.ring.middleware.exception :as exm]
             [reitit.ring.middleware.parameters :as parm]
             [reitit.ring.middleware.muuntaja :as muu]
             [reitit.ring :as ring]
             [integrant.core :as ig]))

(defn read-config [profile]
  (aero/read-config "config.edn" {:profile profile}))

(def system-config
  {:api/config  {}
   :api/jetty   {:port    4000
                 :join?   false
                 :handler (ig/ref :api/handler)}
   :api/handler {:config (ig/ref :api/config)}})

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
     routes/api]
    {:data {:github-token (-> config :api/config :github-token)
            :muuntaja    m/instance
            :middleware  [parm/parameters-middleware
                          muu/format-negotiate-middleware
                          muu/format-response-middleware
                          exm/exception-middleware
                          muu/format-request-middleware
                          rrc/coerce-exceptions-middleware
                          rrc/coerce-request-middleware
                          rrc/coerce-response-middleware]}})
   (ring/routes
    (ring/redirect-trailing-slash-handler)
    (ring/create-default-handler
     {:not-found (constantly {:status 404 :body "route not found"})}))))

(defn -main [& args]
  (ig/init system-config))