(ns resources.todos.model
  (:refer-clojure :exclude [update list])
  (:require [honeysql.helpers :refer :all]
            [lib.honeysql :refer [returning]]
            [db.model :refer [defquery IQuery]]))

(defquery get-by-id
  IQuery
  (query
   [_ id]
   (-> (select :*)
       (from :todos)
       (where [:= :id id]))))

(defquery list
  IQuery
  (query
   [_]
   (-> (select :*)
       (from :todos))))

(defquery create
  IQuery
  (query
   [_ data]
   (as-> (insert-into :todos) $
     (apply columns $ (keys data))
     (values $ [(vals data)])
     (returning $ :*))))

(defquery update-by-id
  IQuery
  (query
   [_ id data]
   (-> (update :todos)
       (sset data)
       (where [:= :id id])
       (returning :*))))

(defquery delete-by-id
  IQuery
  (query
   [_ id]
   (-> (delete-from :todos)
       (where [:= :id id]))))