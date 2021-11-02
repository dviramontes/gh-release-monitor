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
   ["/search"
    {:get
     {:parameters {:query
                   {:q    s/Str
                    (s/optional-key :per-page) s/Int}}
      :handler handlers/search-repos}}]])

(def releases
  ["/releases"
   ["" {:get handlers/get-releases}]
   ["/follow/:owner/:repo"
    {:parameters {:path {:owner s/Str :repo s/Str}}
     :post       handlers/follow-releases}]
   ["/unfollow/:id"
    {:parameters {:path {:id s/Int}}
     :delete       handlers/unfollow-releases}]
   ["/latest/:owner/:repo"
    {:parameters {:path {:owner s/Str :repo s/Str}}
     :post handlers/latest-release}]])

(def webhooks
  ["/webhooks"
   ["/releases"
    {:post  {:handler handlers/update-releases}}]])

(def api
  ["/api"
   repos
   releases
   webhooks])