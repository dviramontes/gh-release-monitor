(ns user
  (:require [api.main :as api]
            [api.db :as db]
            [integrant.repl :as ig-repl]))

(ig-repl/set-prep! (constantly api/system-config))

;; aliases for the repl
(def go ig-repl/go)

(def halt ig-repl/halt)

(def reset ig-repl/reset)

(comment
  (go)
  (halt)
  (reset))

(comment
  (db/create-releases-table! db/config)
  (db/drop-releases-table! db/config))
