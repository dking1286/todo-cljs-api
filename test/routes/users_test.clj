(ns routes.users-test
  (:require [clojure.test :refer :all]
            [test-helpers.fixtures :refer [with-dummies]]
            [db.core :as db]
            [todo-cljs-api.core :refer [handler]]))

(use-fixtures :each (with-dummies "clients" "users" "access_tokens" "todos"))

(deftest test-create-user
  (testing "POST /users"
    (let [request {:request-method :post
                   :uri "/users"
                   :body {:first-name "Daniel"
                          :last-name "King"
                          :email "dking@gmail.com"
                          :password "super secret"}}
          response (handler request)]
      (testing "should respond with status 201"
        (is (= 201 (response :status))))
      (testing "should respond with the newly-created user"
        (is (= "Daniel" (-> response :body :data :first-name)))
        (is (= "King" (-> response :body :data :last-name)))
        (is (= "dking@gmail.com" (-> response :body :data :email))))
      (testing "should hash the given password"
        (is (not= "super secret" (-> response :body :data :password)))))
    (testing "should respond with 400 if the email is missing"
      (let [request {:request-method :post
                     :uri "/users"
                     :body {:first-name "Daniel"
                            :last-name "King"
                            :password "super secret"}}
            response (handler request)]
        (is (= 400 (response :status)))))
    (testing "should respond with 400 if the password is missing"
      (let [request {:request-method :post
                     :uri "/users"
                     :body {:first-name "Daniel"
                            :last-name "King"
                            :email "dking@gmail.com"}}
            response (handler request)]
            (is (= 400 (response :status)))))
    (testing "should respond with 400 if the first-name is missing"
      (let [request {:request-method :post :uri "/users"
                     :body {:last-name "King" :email "dking@gmail.com"
                            :password "super secret"}}
            response (handler request)]
        (is (= 400 (response :status)))))
    (testing "should respond with 400 if the last-name is missing"
      (let [request {:request-method :post :uri "/users"
                     :body {:first-name "Daniel"
                            :email "dking@gmail.com"
                            :password "super secret"}}
            response (handler request)]
        (is (= 400 (response :status)))))
    (testing "should respond with 409 if the email is already in use by another user"
      (let [request {:request-method :post :uri "/users"
                     :body {:first-name "Daniel"
                            :last-name "King"
                            :email "daniel.oliver.king@gmail.com"
                            :password "super secret"}}
            response (handler request)]
        (is (= 409 (response :status)))))))
