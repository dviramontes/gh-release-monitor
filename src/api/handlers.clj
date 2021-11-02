(ns api.handlers
  (:require [api.db :as db]
            [api.github :as github]))

(defn get-releases [_]
  {:status 200 :body {:releases (db/get-releases db/config)}})

(defn search-repos [req]
  (let [token (-> req :reitit.core/match :data :github-token)
        query (-> req :parameters :query :q)
        per-page (get-in req [:parameters :query :per-page] 1)]
    (if (seq query)
      (let [result? (github/search-repos token query per-page)]
        (if-let [repos (:result result?)]
          {:status 200 :body {:query query
                              :repos repos
                              :per-page per-page}}
          {:status 500 :body {:error (:error result?)}}))
      {:status 400 :body {:error "query cannot be empty"}})))

(defn latest-release [req]
  (let [token (-> req :reitit.core/match :data :github-token)
        owner (-> req :parameters :path :owner)
        repo (-> req :parameters :path :repo)
        result? (github/latest-release token owner repo)]
    (prn result?)
    (if-let [latest (:result result?)]
      {:status 200 :body {:owner owner
                          :repo repo
                          :latest latest}}
      {:status 500 :body {:error (:error result?)}})))

;; TODO: create webhook (sender)
(defn follow-releases [req]
  (let [owner (-> req :parameters :path :owner)
        repo (-> req :parameters :path :repo)
        result? (db/create-release-record! owner repo)]
    (if-let [[release] (:result result?)]
      {:status 200 :body {:owner owner
                          :repo repo
                          :release release}}
      {:status 500 :body {:error (:error result?)}})))

;; TODO: add webhook handler (receiver)
(defn update-releases [req]
  (let [token (-> req :reitit.core/match :data :github-token)
        params (-> req :parameters :body)]
    (prn req)
    {:status 200 :body nil}))



