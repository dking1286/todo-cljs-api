(ns routes.access-tokens-test
  (:require [clojure.test :refer :all]
            [test-helpers.fixtures :refer [with-dummies]]
            [clojure.java.jdbc :as jdbc]
            [db.core :as db]
            [utils.oauth2 :as oauth2]
            [todo-cljs-api.core :refer [handler]]))

(use-fixtures :each (with-dummies "clients" "users"))

(deftest test-login-user
  (testing "POST /oauth2/token"
    (with-redefs [oauth2/create-token (fn [] "55555555555555555555555555555555")]
      (let [request {:request-method :post
                     :uri "/oauth2/token"
                     :body {:username "daniel.oliver.king@gmail.com"
                            :password "super secret"
                            :client-id "00000000000000000000000000000000"
                            :grant-type "password"}}
            response (handler request)]
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