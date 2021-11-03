(ns api.cron
  (:require
   [api.db :as db]
   [api.github :as github]
   [clj-time.format :as f]
   [clj-time.core :as time]
   [clojure.core.async :refer [<! timeout go]]
   [clojure.tools.logging :as log]))

(def running (atom true))

(defn now []
  (f/unparse (f/formatters :date-hour-minute-second) (time/now)))

(defn hours
  "Converts hours -> milliseconds"
  [n] (* n 3.6e+6))

(defn task-wrapper [token]
  (let [releases (db/get-releases db/config)]
    (when (> (count releases) 0)
      (doseq [{:keys [owner repo]} releases]
        (let [result? (github/latest-release token owner repo)]
          (if-let [[release] (:result result?)]
            (let [result? (db/create-release-record! owner repo release)]
              (if (:result result?)
                (log/info (str "updated release: " owner "/" repo))
                (log/error (:error result?))))
            (log/error (:error result?))))))))

(defn start
  [token interval]
  (log/info (format "refreshing every %s hours" interval))
  (go
    (log/info (format "cronjob scheduled: %s" (now)))
    (while @running
      (<! (timeout (hours interval)))
      (log/info (format "cronjob started: %s" (now)))
      (task-wrapper token)
      (log/info (format "cronjob completed: %s" (now))))))
