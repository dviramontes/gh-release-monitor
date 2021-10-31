(ns api.github
  (:require [org.httpkit.client :as http]
            [clojure.data.json :as json]))

(def endpoints
  {:repo-search
   "https://api.github.com/search/repositories?q=%s&per_page=%d"})

(defn with-headers [token]
  {"Authorization" (str "token " token)
   "Accept" "application/vnd.github.v3+json"})

(defn search-repos
  [token query page-size]
  (let [opts {:headers (with-headers token)}
        endpoint (format (endpoints :repo-search) query page-size)
        {:keys [body error status]} @(http/get endpoint opts)]
    (if (= status 200)
      (json/read-str body :key-fn keyword)
      error)))
