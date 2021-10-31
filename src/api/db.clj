(ns api.db
  (:require
    [clj-time.core :as time]
    [hugsql.core :as hugsql]))

;; avoids lint warnings of unresolved symbol by clj-kondo
(declare create-repos-table!)
(declare drop-repos-table!)

(def config
  {:classname   "org.postgresql.Driver"
   :subprotocol "postgres"
   :subname     "//127.0.0.1:5432/github-release-monitor"
   :user        "postgres"
   :password    "postgres"})

(hugsql/def-db-fns "sql/repos.sql")
