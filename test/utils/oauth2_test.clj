(ns utils.oauth2-test
  (:require [clojure.test :refer :all]
            [test-helpers.fixtures :refer [with-dummies]]
            [utils.oauth2 :refer [get-identity-by-credentials]]))

(use-fixtures :each (with-dummies "clients" "users"))

(deftest test-oauth2-get-identity-by-credentials
  (testing "utils.oauth2/get-identity-by-credentials"
    (testing "password grant type"
      (testing "should return a truthy if the credentials match"
        (is (get-identity-by-credentials
                {:grant-type "password"
                 :client-id "00000000000000000000000000000000"
                 :username "daniel.oliver.king@gmail.com"
                 :password "super secret"})))
      (testing "should return falsy if no user exists with the username"
        (is (not (get-identity-by-credentials
                  {:grant-type "password"
                   :client-id "00000000000000000000000000000000"
                   :username "blah@blah.com"
                   :password "super secret"}))))
      (testing "should return falsy if the user exists but the password does not match"
        (is (not (get-identity-by-credentials
                  {:grant-type "password"
                   :client-id "00000000000000000000000000000000"
                   :username "daniel.oliver.king@gmail.com"
                   :password "wrong password"})))))
    (testing "client credentials grant type"
      (testing "should return truthy if the client id and client secret match and the client is trusted"
        (is (get-identity-by-credentials
                {:grant-type "client_credentials"
                 :client-id "11111111111111111111111111111111"
                 :client-secret "11111111111111111111111111111111"})))
      (testing "should return falsy if the client id does not match"
        (is (not (get-identity-by-credentials
                  {:grant-type "client_credentials"
                   :client-id "22222222222222222222222222222222"
                   :client-secret "11111111111111111111111111111111"}))))
      (testing "should return falsy if the client secret does not match"
        (is (not (get-identity-by-credentials
                  {:grant-type "client_credentials"
                   :client-id "11111111111111111111111111111111"
                   :client-secret "00000000000000000000000000000000"}))))
      (testing "should return falsy if the client id and secret match, but the client is untrusted"
        (is (not (get-identity-by-credentials
                  {:grant-type "client_credentials"
                   :client-id "00000000000000000000000000000000"
                   :client-secret "00000000000000000000000000000000"})))))
    (testing "invalid grant type"
      (testing "should throw an error"
        (is (thrown-with-msg? clojure.lang.ExceptionInfo #"Invalid grant type"
                              (get-identity-by-credentials
                               {:grant-type "invalid grant"})))))))