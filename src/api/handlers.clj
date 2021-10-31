(ns api.handlers
  (:require [api.db :as db]
            [api.github :as github]
            [clojure.data.json :as json]))

(defn get-repos [_]
  (let [repos []]
    {:status 200 :body {:data repos}}))

(defn search-repos [req]
  (let [token (-> req :reitit.core/match :data :github-token)
        query (-> req :parameters :query :q)
        per-page (get-in req [:parameters :query :per-page] 1)
        repos (github/search-repos token query per-page)]
    {:status 200 :body {:query query
                        :repos repos
                        :per-page per-page}}))