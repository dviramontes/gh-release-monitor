(ns api.routes
  (:require
   [api.handlers :as handlers]
   [reitit.coercion.schema]
   [schema.core :as s]))

;; obligatory health check
(def ping
  ["/ping"
   {:get (fn [_] {:status 200 :body "pong"})}])

(def repos
  ["/repos"
   ["" {:get handlers/get-repos}]])

(def search
  ["/search"
   {:get
    {:parameters {:query
                  {:q    s/Str
                   (s/optional-key :per-page) s/Int}}
     :handler handlers/search-repos}}])

(def api
  ["/api"
   repos
   search])