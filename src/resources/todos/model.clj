(ns resources.todos.model
  (:refer-clojure :exclude [update list])
  (:require [honeysql.helpers :refer :all]
            [lib.honeysql :refer [returning]]
            [db.model :refer [IQuery]]))

(def get-by-id
  (reify
    IQuery
    (query [_ id]
      (-> (select :*)
          (from :todos)
          (where [:= :id id])))))

(def list
  (reify
    IQuery
    (query [_]
      (-> (select :*)
          (from :todos)))))

(def create
  (reify
    IQuery
    (query [_ data]
      (as-> (insert-into :todos) $
            (apply columns $ (keys data))
            (values $ [(vals data)])
            (returning $ :*)))))

(def update-by-id
  (reify
    IQuery
    (query [_ id data]
      (-> (update :todos)
          (sset data)
          (where [:= :id id])
          (returning :*)))))

(def delete-by-id
  (reify
    IQuery
    (query [_ id]
      (-> (delete-from :todos)
          (where [:= :id id])))))