(ns api.main
  (:require  [aero.core :as aero]))

(defn read-config [profile]
  (aero/read-config "config.edn" {:profile profile}))

(defn -main [& args]
  (let [config (read-config :dev)]
    (prn (-> config :api/config))))