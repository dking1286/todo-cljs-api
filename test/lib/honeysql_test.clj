(ns lib.honeysql-test
  (:require [clojure.test :refer :all]
            [honeysql.core :as sql]
            [honeysql.helpers :refer :all]
            [lib.honeysql :refer [returning]]))

(deftest returning-test
  (testing "lib.honeysql/returning"
    (testing "should append a 'RETURNING' clause to the end of the generated sql"
      (is (= ["INSERT INTO todos (title, body) VALUES (?, ?) RETURNING *" "Hello" "world"]
             (-> (insert-into :todos)
               (columns :title :body)
               (values [["Hello" "world"]])
               (returning :*)
               sql/format))))))