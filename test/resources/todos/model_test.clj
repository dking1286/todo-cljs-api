(ns resources.todos.model-test
  (:require [clojure.test :refer :all]
            [test-helpers.fixtures :refer [with-dummies]]
            [db.core :as db]
            [resources.todos.model :as model]))

(use-fixtures :each (with-dummies "todos"))

(deftest test-get-by-id
  (testing "should be able to get a todo by id"
    (is (= {:id 1 :title "Hello" :body "World" :done? false}
           (first (db/query model/get-by-id 1))))))

(deftest test-list
  (testing "should be able to get all todos"
    (is (= '({:id 1 :title "Hello" :body "World" :done? false}
             {:id 2 :title "Another" :body "todo" :done? true})
           (db/query model/list)))))

(deftest test-create
  (let [returned (db/query model/create {:title "New" :body "Todo"})]
    (testing "create should create a new todo row in the database"
      (is (= (count (db/query model/list))
             3)))
    (testing "create should return the created row"
      (let [new-row (db/query model/get-by-id 3)]
        (is (= new-row returned))))))

(deftest test-update-by-id
  (let [returned (db/query model/update-by-id 1 {:title "New title"})]
    (testing "update-by-id should change the indicated row in the database"
      (is (= {:id 1 :title "New title" :body "World" :done? false}
             (first (db/query model/get-by-id 1)))))
    (testing "update-by-id should return the modified row"
      (is (= (db/query model/get-by-id 1)
             returned)))))

(deftest test-delete-by-id
  (db/execute! model/delete-by-id 1)
  (testing "delete-by-id should delete the indicated todo"
    (is (= (map :id (db/query model/list))
           '(2)))))