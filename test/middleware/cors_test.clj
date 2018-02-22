(ns middleware.cors-test
  (:require [clojure.test :refer :all]
            [middleware.cors :refer [wrap-cors]]))

(deftest test-wrap-cors
  (testing "wrap-cors"
    (testing "should cause the function to return an empty response if the request method is :options"
      (let [wrapped-handler (wrap-cors (fn [req] {:body {:not "empty"}}))
            response (wrapped-handler {:request-method :options})]
        (is (nil? (-> response :body)))))
    (testing "should cause the function to add cors headers to the response if the request method is :options"
      (let [wrapped-handler (wrap-cors (fn [req] {:body {:not "empty"}}))
            response (wrapped-handler {:request-method :options})]
        (is (not (nil? (-> response :headers (get "Access-Control-Allow-Origin")))))))
    (testing "should cause the function to return the response unchanged if the request method is not :options"
      (let [wrapped-handler (wrap-cors (fn [req] {:body {:not "empty"}}))
            response (wrapped-handler {:request-method :get})]
        (is (= {:not "empty"}
               (-> response :body)))))))