(ns resources.todos.controller
  (:require [ring.util.response :as r]
            [utils.response :refer [not-found bad-request]]
            [db.core :as db]
            [resources.todos.model :as todos-model]))

(defn get-all
  [req]
  (let [todos (db/query todos-model/list)]
    (-> (r/response {:data todos})
        (r/status 200))))

(defn create-one
  [req]
  (cond
    (nil? (-> req :body :title)) (bad-request "Missing title parameter")
    (nil? (-> req :body :body)) (bad-request "Missing body parameter")
    :else
    (let [todos (db/query todos-model/create (-> req :body))]
      (-> (r/response {:data (first todos)})
          (r/status 201)))))

(defn get-one
  [id req]
  (if-not (integer? id)
    (bad-request (str "Non-integer id " id " provided"))
    (let [todos (db/query todos-model/get-by-id id)]
      (if (empty? todos)
        (not-found)
        (-> (r/response {:data (first todos)})
            (r/status 200))))))

(defn update-one
  [id req]
  (if-not (integer? id)
    (bad-request (str "Non-integer id " id " provided"))
    (let [todos (db/query todos-model/update-by-id id (:body req))]
      (if (empty? todos)
        (not-found)
        (-> (r/response {:data (first todos)})
            (r/status 200))))))

(defn delete-one
  [id req]
  (if-not (integer? id)
    (bad-request (str "Non-integer id " id " provided"))
    (let [num-deleted (first (db/execute! todos-model/delete-by-id id))]
      (if (zero? num-deleted)
        (not-found)
        (-> (r/response nil)
          (r/status 204))))))