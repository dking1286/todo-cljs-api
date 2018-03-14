(ns middleware.authentication-test
  (:require [clojure.test :refer :all]
            [test-helpers.authentication :refer [with-access-token]]
            [test-helpers.fixtures :refer [with-dummies]]
            [middleware.authentication :refer [wrap-token-authentication]]))

(use-fixtures :each (with-dummies "clients" "users" "access_tokens"))

(deftest test-wrap-token-authentication
  (testing "should make :identity nil if no token is provided"
    (let [handler (fn [req] req)
          wrapped-handler (wrap-token-authentication handler)
          req (wrapped-handler {:headers {}})]
      (is (nil? (get req :identity)))))
  (testing "should make :identity nil if an invalid authorization header is provided"
    (let [handler (fn [req] req)
          wrapped-handler (wrap-token-authentication handler)
          req (wrapped-handler {:headers {"Authorization" "Blah"}})]
      (is (nil? (get req :identity)))))
  (testing "should make :identity nil if a non-existant token is provided"
    (let [handler (fn [req] req)
          wrapped-handler (wrap-token-authentication handler)
          req (wrapped-handler (with-access-token {} 5))]
      (is (nil? (get req :identity)))))
  (testing "should make :identity the user map corresponding to the provided access token"
    (let [handler (fn [req] req)
          wrapped-handler (wrap-token-authentication handler)
          req (wrapped-handler (with-access-token {} 0))]
      (is (= "daniel.oliver.king@gmail.com"
              (get-in req [:identity :email]))))))