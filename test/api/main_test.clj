(ns api.main-test
  (:require
   [api.main :as api]
   [api.handlers :as handlers]
   [api.db :as db]
   [clojure.test :refer :all]
   [ring.mock.request :as mock]
   [clj-time.core :as time]))

(def test-release
  {:owner "facebook"
   :repo "react"
   :details
   {:id 123
    :tag-name "0.0.1"
    :url "https://api.github.com/repos/facebook/react/releases/52532241"}})

(defn db-setup [f]
  (db/create-releases-table! db/config)
  (f)
  (db/drop-releases-table! db/config))

(use-fixtures :once db-setup)

(deftest releases-api

  (testing "retrieving releases from releases table"
    (let [release (db/create-release! db/config test-release)
          releases (db/get-releases db/config)]
      (is (= (count releases) 1)))))

