(ns utils.oauth2-test
  (:require [clojure.test :refer :all]
            [test-helpers.fixtures :refer [with-dummies]]
            [utils.oauth2 :refer [check-credentials]]))

(use-fixtures :each (with-dummies "clients" "users"))

(deftest test-oauth2-check-credentials
  (testing "utils.oauth2/check-credentials"
    (testing "password grant type"
      (testing "should return true if the credentials match"
        (is (true? (check-credentials {:grant-type "password"
                                       :username "daniel.oliver.king@gmail.com"
                                       :password "super secret"}))))
      (testing "should return falsy if no user exists with the username"
        (is (not (check-credentials {:grant-type "password"
                                     :username "blah@blah.com"
                                     :password "super secret"}))))
      (testing "should return falsy if the user exists but the password does not match"
        (is (not (check-credentials {:grant-type "password"
                                     :username "daniel.oliver.king@gmail.com"
                                     :password "wrong password"})))))
    (testing "client credentials grant type"
      (testing "should return true if the client id and client secret match and the client is trusted"
        (is (true? (check-credentials {:grant-type "client_credentials"
                                       :client-id "11111111111111111111111111111111"
                                       :client-secret "11111111111111111111111111111111"}))))
      (testing "should return falsy if the client id does not match"
        (is (not (check-credentials {:grant-type "client_credentials"
                                     :client-id "22222222222222222222222222222222"
                                     :client-secret "11111111111111111111111111111111"}))))
      (testing "should return falsy if the client secret does not match"
        (is (not (check-credentials {:grant-type "client_credentials"
                                     :client-id "11111111111111111111111111111111"
                                     :client-secret "00000000000000000000000000000000"}))))
      (testing "should return falsy if the client id and secret match, but the client is untrusted"
        (is (not (check-credentials {:grant-type "client_credentials"
                                     :client-id "00000000000000000000000000000000"
                                     :client-secret "00000000000000000000000000000000"})))))
    (testing "invalid grant type"
      (testing "should throw an error"
        (is (thrown-with-msg? clojure.lang.ExceptionInfo #"Invalid grant type"
                              (check-credentials {:grant-type "invalid grant"})))))))