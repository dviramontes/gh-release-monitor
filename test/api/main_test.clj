(ns api.main-test
  (:require
   [api.main :as api]
   [api.handlers :as handlers]
   [api.db :as db]
   [clojure.test :refer :all]
   [ring.mock.request :as mock]))

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

  (testing "retrieve releases from releases table"
    (let [_ (db/create-release! db/config test-release)
          releases (db/get-releases db/config)]
      (is (= (count releases) 1))))

  (testing "retrieve releases via endpoint"
    (let [_ (db/create-release! db/config test-release)
          resp (handlers/get-releases (mock/request :get "/api/releases"))]
      (is (= (:status resp) 200))
      (let [release (-> resp :body :releases first)]
        (is (= (release :id) 1))
        (is (= (release :owner) "facebook"))
        (is (= (release :repo) "react"))
        (is (not (empty? (release :details)))))))

  (testing "create a release to follow via endpoint"
    (with-redefs [api.github/latest-release (fn [_ _ _] {:result [{:owner "lispyclouds" :repo "contajners" :tag-name "1.1.1"}]})]
      (let [resp (handlers/follow-release {:parameters {:body {:owner "lispyclouds" :repo  "contajners"}}})
            releases (db/get-releases db/config)]
        (is (= (:status resp) 200))
        (let [release (second releases)]
          (is (= (release :owner) "lispyclouds"))
          (is (= (release :repo) "contajners"))
          (is (not (empty? (release :details))))
          (is (= (get-in release [:details "tag-name"]) "1.1.1"))))))

  (testing "remove a release via endpoint (unfollow)"
    (let [resp (handlers/unfollow-release {:parameters {:path {:id 1}}})
          releases (db/get-releases db/config)]
      (is (= (:status resp) 200))
      (is (= (count releases) 1))))

  (testing "search a repo via endpoint"
    (with-redefs [api.github/search-repos (fn [_ _ _] {:result [{:owner "lispyclouds" :repo "contajners"}]})]
      (let [resp (handlers/search-repos {:parameters {:query {:q "lispyclouds"}}})]
        (is (= (:status resp) 200))
        (is (= resp {:status 200
                     :body
                     {:query "lispyclouds"
                      :repos [{:owner "lispyclouds", :repo "contajners"}]
                      :per-page 1}}))))))
