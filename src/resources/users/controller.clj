(ns resources.users.controller
  (:require [ring.util.response :as r]
            [utils.response :refer [not-found bad-request conflict]]
            [db.core :as db]
            [db.errors :refer [conflict-error? validation-error?]]
            [resources.users.model :as users-model]))

(defn create-one
  [req]
  (try
    (let [users (db/query users-model/create (-> req :body))]
      (-> (r/response {:data (first users)})
          (r/status 201)))
    (catch Exception e
      (cond
        (validation-error? e)
        (bad-request "Cannot create user with the provided data")

        (conflict-error? e)
        (conflict "Conflicting user already exists")
        
        :else
        (throw e)))))