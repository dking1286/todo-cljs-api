(ns middleware.request-test
  (:import [java.io ByteArrayOutputStream ByteArrayInputStream])
  (:require [clojure.test :refer :all]
            [cognitect.transit :as transit]
            [middleware.request :refer [wrap-json-request-body]]))

(defn setup
  []
  (let [handler (wrap-json-request-body (fn [req] req))
        out (ByteArrayOutputStream. 4096)
        writer (transit/writer out :json-verbose)]
    (transit/write writer {:some "data"})
    (let [body (ByteArrayInputStream. (.toByteArray out))]
      [handler body])))

(defmacro with-setup
  [[handler-sym body-sym] & forms]
  `(let [[~handler-sym ~body-sym] (setup)]
    ~@forms))

(deftest test-wrap-json-request-body
  (testing "wrap-json-request-body"
    (testing "should return a function"
      (is (ifn? (wrap-json-request-body (fn [])))))
    (testing "should cause the handler to parse the request body from the request if the method is POST"
      (with-setup [handler body]
        (let [req {:request-method :post
                   :headers {"content-type" "application/json"}
                   :body body}]
          (is (= {:some "data"}
                 (-> (handler req) :body))))))
    (testing "should cause the handler to parse the request body from the request if the method is PATCH"
      (with-setup [handler body]
        (let [req {:request-method :patch
                   :headers {"content-type" "application/json"}
                   :body body}]
          (is (= {:some "data"}
                 (-> (handler req) :body))))))
    (testing "should not change the behavior of the handler if the method is GET"
      (with-setup [handler body]
        (let [req {:request-method :get
                   :headers {"content-type" "application/json"}
                   :body body}]
          (is (instance? ByteArrayInputStream (-> (handler req) :body))))))
    (testing "should not change the behavior of the handler if the method is DELETE"
      (with-setup [handler body]
        (let [req {:request-method :delete
                  :headers {"content-type" "application/json"}
                  :body body}]
          (is (instance? ByteArrayInputStream (-> (handler req) :body))))))
    (testing "should cause the handler to return a bad request response if the content-type header is missing"
      (with-setup [handler body]
        (let [req {:request-method :post
                   :headers {}
                   :body body}]
          (is (= 400 (-> (handler req) :status))))))))

