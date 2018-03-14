(ns routes.access-tokens-test
  (:require [clojure.test :refer :all]
            [test-helpers.fixtures :refer [with-dummies]]
            [clojure.java.jdbc :as jdbc]
            [db.core :as db]
            [utils.oauth2 :as oauth2]
            [todo-cljs-api.core :refer [app]]))

(use-fixtures :each (with-dummies "clients" "users" "access_tokens"))

(deftest test-login-user
  (testing "POST /oauth2/token"
    (with-redefs [oauth2/create-token (fn [] "55555555555555555555555555555555")]
      (let [request {:request-method :post
                     :uri "/oauth2/token"
                     :body {:username "daniel.oliver.king@gmail.com"
                            :password "super secret"
                            :client-id "00000000000000000000000000000000"
                            :grant-type "password"}}
            response (app request)]
        (testing "should respond with status 200"
          (is (= 200 (response :status))))
        (testing "should respond with the access token in the body"
          (is (= "55555555555555555555555555555555"
                 (-> response :body :data :token))))
        (testing "should insert the access token into the database"
          (let [token (first (jdbc/query db/connection
                                         ["SELECT * FROM access_tokens WHERE token = ?"
                                          "55555555555555555555555555555555"]))]
            (is (not (nil? token)))))))))

(deftest test-logout-user
  (testing "POST /oauth2/revoke"
    (let [request {:request-method :post
                   :uri "/oauth2/revoke"
                   :headers {"Authorization" "Token 00000000000000000000000000000000"}
                   :body {}}
          response (app request)]
      (testing "should return status 204"
        (is (= 204 (response :status))))
      (testing "should remove the access token from the database"
        (let [token (first (jdbc/query db/connection
                                       ["SELECT * FROM access_tokens WHERE token = ?"
                                        "00000000000000000000000000000000"]))]
          (is (nil? token)))))
    (testing "should return status 401 if the user is not already logged in"
      (let [request {:request-method :post
                     :uri "/oauth2/revoke"
                     :headers {}
                     :body {}}
            response (app request)]
        (is (= 401 (response :status)))))))