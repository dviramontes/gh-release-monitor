(ns api.routes
  (:require
   [api.handlers :as handlers]
   [reitit.coercion.schema]
   [schema.core :as s]
   [reitit.swagger :as swagger]))

(def swagger
  ["/swagger.json"
   {:get {:handler (swagger/create-swagger-handler)
          :no-doc true
          :swagger {:title "GH Release Monitor API"
                    :description "Monitors repos for releases via GitHub API"}}}])

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

(def api
  ["/api"
   repos
   releases])