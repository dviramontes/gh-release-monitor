(ns api.routes)

;; obligatory health check
(def ping
  ["/ping"
   {:get (fn [_] {:status 200 :body "pong"})}])

(def repos
  ["/repos"])

(def api
  ["/api"
   repos])