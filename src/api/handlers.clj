(ns api.handlers
  (:require [api.db :as db]
            [api.github :as github]
            [clojure.data.json :as json]
            [clojure.tools.logging :as log]))

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
    (if-let [latest (:result result?)]
      {:status 200 :body {:owner owner
                          :repo repo
                          :latest latest}}
      {:status 500 :body {:error (:error result?)}})))

(defn follow-releases [req]
  (let [token (-> req :reitit.core/match :data :github-token)
        owner (-> req :parameters :path :owner)
        repo (-> req :parameters :path :repo)
        create-webhook-request (github/create-webhook token callback-url owner repo)]
    ;; if webhook-callback-url; issue create webhook url
    (prn create-webhook-request)
    (let [result? (db/create-release-record! owner repo)]
      (if-let [[release] (:result result?)]
        {:status 200 :body {:owner owner
                            :repo repo
                            :release release}}
        {:status 500 :body {:error (:error result?)}}))
    ))

(defn unfollow-releases [req]
  (let [token (-> req :reitit.core/match :data :github-token)
        id (-> req :parameters :path :id)]
    (if-let [{:keys [owner repo webhook-id]} (db/get-release-by-id db/config {:id id})]
      (let [result? (github/delete-webhook token owner repo webhook-id)]
        (if (:result result?)
          (case (db/delete-release! db/config {:id id})
            1 {:status 200 :body (format "release id: %d removed successfully" id)}
            0 {:status 404 :body (format "release id: %d not found" id)})
          {:status (:status result?) :body {:error (:message result?)}}))
      {:status 404 :body (format "release id: %d not found" id)})))

(defn update-releases [req]
  (let [github-event (get-in req [:headers "x-github-event"])
        github-hook-id (-> (get-in req [:headers "x-github-hook-id"]) Integer/parseInt)]
    (case github-event
      ;; tell GitHub we're alive and well
      "ping" {:status 200 :body nil}
      ;; this one has the goods :)
      "release"
      (let [body (json/read-str req :key-fn keyword)]
        (log/info body "update-releases from webhook")
        ;; update record with release info
        {:status 200 :body nil}))))



