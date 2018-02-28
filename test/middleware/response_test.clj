(ns middleware.response-test
  (:import [java.io ByteArrayOutputStream])
  (:require [clojure.test :refer :all]
            [cognitect.transit :as transit]
            [middleware.response :refer [wrap-json-response-body]]))

(defn handler
  [req]
  {:body {:hello "world"}})

(def wrapped-handler (wrap-json-response-body handler))

(defn serialize
  [writer-type data]
  (let [out (ByteArrayOutputStream.)
        writer (transit/writer out writer-type)]
    (transit/write writer data)
    (.toString out)))

(deftest test-wrap-json-response-body
  (testing "should serialize the response body as json verbose if there is no 'accept' header"
    (is (= (serialize :json-verbose {:hello "world"})
           (get (wrapped-handler {:headers {}}) :body))))
  (testing "should serialize the response body as json if 'accept' is 'application/transit+json'"
    (is (= (serialize :json {:hello "world"})
           (get (wrapped-handler {:headers {"accept" "application/transit+json"}}) :body))))
  (testing "should serialize the response body as json verbose if 'accept' is 'applicaiton/json'"
    (is (= (serialize :json-verbose {:hello "world"})
           (get (wrapped-handler {:headers {"accept" "application/json"}}) :body)))))