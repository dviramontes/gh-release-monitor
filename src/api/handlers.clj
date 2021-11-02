(ns api.handlers
  (:require [api.db :as db]
            [api.github :as github]
            [clojure.data.json :as json]))

(defn get-releases [_]
  (let [releases []]
    {:status 200 :body {:data releases}}))

(defn search-repos [req]
  (let [token (-> req :reitit.core/match :data :github-token)
        query (-> req :parameters :query :q)
        per-page (get-in req [:parameters :query :per-page] 1)
        repos (github/search-repos token query per-page)]
    {:status 200 :body {:query query
                        :repos repos
                        :per-page per-page}}))

(defn latest-release [req]
  (let [token (-> req :reitit.core/match :data :github-token)
        owner (-> req :parameters :path :owner)
        repo (-> req :parameters :path :repo)
        latest (github/latest-release token owner repo)]
    {:status 200 :body {:owner owner
                        :repo repo
                        :latest latest}}))

;; TODO: create a release entry in db
(defn follow-repo [req]
  (let [token (-> req :reitit.core/match :data :github-token)
        owner (-> req :parameters :path :owner)
        repo (-> req :parameters :path :repo)]
    {:status 200 :body {:owner owner
                        :repo repo
                        :data []}}))

;; TODO: add webhook handler
(defn update-releases [req]
  (let [token (-> req :reitit.core/match :data :github-token)
        params (-> req :parameters :body)]
    (prn req)
    {:status 200 :body nil}))



