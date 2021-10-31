(ns user
  (:require [api.main :as api]
            [api.db :as db]))

(comment
  (db/create-repos-table! db/config)
  (db/drop-repos-table! db/config))
