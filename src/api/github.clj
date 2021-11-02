(ns api.github
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]))

(def endpoints
  {:repo-search
   "https://api.github.com/search/repositories?q=%s&per_page=%d"
   :latest-release
   "https://api.github.com/repos/%s/%s/releases?per_page=1"
   :create-webhook
   "https://api.github.com/repos/seancorfield/honeysql/releases?per_page=1"})

(def content-type-header
  {"Accept" "application/vnd.github.v3+json"})

(defn with-auth-header [token]
  {"Authorization" (str "token " token)})

(defn search-repos
  [token query page-size]
  (let [opts {:headers (merge content-type-header (with-auth-header token))}
        endpoint (format (endpoints :repo-search) query page-size)
        {:keys [body error status]} @(http/get endpoint opts)]
    (if (= status 200)
      (json/read-str body :key-fn keyword)
      error)))

(defn latest-release
  [token owner repo]
  (let [opts {:headers (merge content-type-header (with-auth-header token))}
        endpoint (format (endpoints :latest-release) owner repo)
        {:keys [body error status]} @(http/get endpoint opts)]
    (if (= status 200)
      (json/read-str body :key-fn keyword)
      error)))

(defn create-webhook [token]
  (let [opts {:headers (merge content-type-header (with-auth-header token))
              :body    (json/write-str {:name "web"
                                        :config {}})}
        endpoint (format (endpoints :create-webhook))
        {:keys [body error status]} @(http/post endpoint opts)]
    (if (= status 200)
      (json/read-str body :key-fn keyword)
      error)))