(ns routes.todos-test
  (:require [clojure.test :refer :all]
            [test-helpers.fixtures :refer [with-dummies]]
            [db.core :as db]
            [resources.todos.model :as todos-model]
            [routes.core :refer [root-handler]]))

(use-fixtures :each (with-dummies "todos"))

(deftest test-get-todos
  (testing "GET /todos"
    (testing "should respond with status 200"
      (is (= 200
             (-> (root-handler {:request-method :get :uri "/todos"})
                 :status))))
    (testing "should respond with all todos"
      (is (= [{:id 1 :title "Hello" :body "World" :done? false}
              {:id 2 :title "Another" :body "todo" :done? true}]
             (-> (root-handler {:request-method :get :uri "/todos"})
                 :body
                 :data))))))

(deftest test-get-one-todo
  (testing "GET /todos/:id"
    (testing "should respond with status 200 if the todo exists"
      (is (= 200
             (-> (root-handler {:request-method :get :uri "/todos/1"})
                 :status))))
    (testing "should respond with the todo"
      (is (= {:id 1 :title "Hello" :body "World" :done? false}
             (-> (root-handler {:request-method :get :uri "/todos/1"})
                 :body
                 :data))))
    (testing "should respond with 404 if the todo does not exist"
      (is (= 404
             (-> (root-handler {:request-method :get :uri "/todos/10"})
                 :status))))
    (testing "should respond with 400 if the id is not an integer"
      (is (= 400
             (-> (root-handler {:request-method :get :uri "/todos/blah"})
                 :status))))))

(deftest test-create-todo
  (testing "POST /todos"
    (let [response (root-handler {:request-method :post
                                  :uri "/todos"
                                  :body {:title "Awesome reminder"
                                         :body "Be awesome"}})]
      (testing "should respond with status 201"
        (is (= 201
               (:status response))))
      (testing "should create a new todo"
        (is (= 3
               (count (db/query todos-model/list)))))
      (testing "should respond with the newly created todo"
        (is (= {:id 3 :title "Awesome reminder" :body "Be awesome" :done? false}
               (-> response :body :data)))))
    (testing "should respond with 400 if the title is missing"
      (let [response (root-handler {:request-method :post
                                    :uri "/todos"
                                    :body {:body "Be awesome"}})]
        (is (= 400
               (-> response :status)))))
    (testing "should respond with 400 if the body is missing"
      (let [response (root-handler {:request-method :post
                                    :uri "/todos"
                                    :body {:title "Awesome reminder"}})]
        (is (= 400
               (-> response :status)))))))

(deftest test-update-todo
  (testing "PATCH /todos/:id"
    (let [response (root-handler {:request-method :patch
                                  :uri "/todos/1"
                                  :body {:title "Goodbye"}})]
      (testing "should respond with status 200"
        (is (= 200
               (-> response :status))))
      (testing "should modify the todo"
        (is (= "Goodbye"
               (-> (db/query todos-model/get-by-id 1) first :title))))
      (testing "should respond with the modified todo"
        (is (= "Goodbye"
               (-> response :body :data :title)))))
    (testing "should respond with 404 if the todo does not exist"
      (let [response (root-handler {:request-method :patch
                                    :uri "/todos/10"
                                    :body {:title "Goodbye"}})]
        (is (= 404
               (-> response :status)))))
    (testing "should respond with 400 if the id is not an integer"
      (let [response (root-handler {:request-method :patch
                                    :uri "/todos/blah"
                                    :body {:title "Goodbye"}})]
        (is (= 400
               (-> response :status)))))))

(deftest test-delete-todo
  (testing "DELETE /todos/:id"
    (let [response (root-handler {:request-method :delete
                                  :uri "/todos/1"})]
      (testing "should respond with status 204"
        (is (= 204
               (-> response :status))))
      (testing "should delete the todo"
        (is (empty? (db/query todos-model/get-by-id 1))))
      (testing "should have no data in the response body"
        (is (nil? (-> response :body)))))
    (testing "should respond with 404 if the todo does not exist"
      (let [response (root-handler {:request-method :delete
                                    :uri "/todos/10"})]
        (is (= 404
               (-> response :status)))))
    (testing "should respond with 400 if the id is not an integer"
      (let [response (root-handler {:request-method :delete
                                    :uri "/todos/blah"})]
        (is (= 400
               (-> response :status)))))))