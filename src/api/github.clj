(ns api.github
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]
            [clojure.tools.logging :as log]))

(def endpoints
  {:repo-search
   "https://api.github.com/search/repositories?q=%s&per_page=%d"
   :latest-release
   "https://api.github.com/repos/%s/%s/releases?per_page=1"
   :create-webhook
   "https://api.github.com/repos/%s/%s/hooks"
   :delete-webhook
   "https://api.github.com/repos/%s/%s/hooks/%d"})

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
      {:result (json/read-str body :key-fn keyword)}
      (do
        (log/error error "failed to search-repos:")
        {:error error}))))

(defn latest-release
  [token owner repo]
  (let [opts {:headers (merge content-type-header (with-auth-header token))}
        endpoint (format (endpoints :latest-release) owner repo)
        {:keys [body error status]} @(http/get endpoint opts)]
    (if (= status 200)
      {:result (json/read-str body :key-fn keyword)}
      (do
        (log/error error "failed to retrieve latest release:")
        {:error error}))))

(defn create-webhook
  [token callback-url owner repo]
  (let [opts {:headers (merge content-type-header (with-auth-header token))
              :body    (json/write-str {:name "web"
                                        :config
                                        {:url (format "%s/api/webhooks/releases" callback-url)}
                                        :events ["release"]})}
        endpoint (format (endpoints :create-webhook) owner repo)
        {:keys [body error status]} @(http/post endpoint opts)]
    (prn body)
    (prn error)
    (prn status)
    (if (= status 200)
      (json/read-str body :key-fn keyword)
      error)))

(defn delete-webhook
  [token owner repo id]
  (let [opts {:headers (merge content-type-header (with-auth-header token))}
        endpoint (format (endpoints :delete-webhook) owner repo id)
        {:keys [body error status]} @(http/delete endpoint opts)]
    (case status
      (200 201 204) {:result status}
      (let [body (json/read-str body :key-fn keyword)]
        {:error error :status status :message (:message body)}))))