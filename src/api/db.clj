(ns api.db
  (:require
   [clojure.tools.logging :as log]
   [try-let :refer [try-let]]
   [hugsql.core :as hugsql]))

;; avoids lint warnings of unresolved symbol by clj-kondo
(declare create-releases-table!)
(declare drop-releases-table!)
(declare create-release!)
(declare get-releases)

(def config
  {:classname   "org.postgresql.Driver"
   :subprotocol "postgres"
   :subname     "//127.0.0.1:5432/github-release-monitor"
   :user        "postgres"
   :password    "postgres"})

;; bootstrap SQL functions
(hugsql/def-db-fns "sql/releases.sql")

(defn create-release-record!
  "convenience wrapper for create-release!, attempts to handle
  SQL exception and returns either a {:result result} | {:error error}"
  [owner repo]
  (try-let
   [result (create-release! config {:owner owner :repo repo})]
   {:result result}
   (catch
    Exception e (str "ERROR: duplicate key value violates unique constraint" (.getMessage e))
    (log/warn "duplicate key constrain")
    {:error (.getMessage e)})
   (catch Exception e
     (log/error e "watch out!")
     {:error e})))