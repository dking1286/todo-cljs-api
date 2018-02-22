(ns resources.todos.model
  (:refer-clojure :exclude [update list])
  (:require [honeysql.helpers :refer :all]
            [lib.honeysql :refer [returning]]))

(defn get-by-id
  [id]
  (-> (select :*)
      (from :todos)
      (where [:= :id id])))

(defn list
  []
  (-> (select :*)
      (from :todos)))

(defn create
  [data]
  (as-> (insert-into :todos) $
        (apply columns $ (keys data))
        (values $ [(vals data)])
        (returning $ :*)))

(defn update-by-id
  [id data]
  (-> (update :todos)
      (sset data)
      (where [:= :id id])
      (returning :*)))

(defn delete-by-id
  [id]
  (-> (delete-from :todos)
      (where [:= :id id])))