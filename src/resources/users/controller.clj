(ns resources.users.controller
  (:require [ring.util.response :as r]
            [utils.response :refer [not-found bad-request]]
            [db.core :as db]
            [resources.users.model :as users-model]))

(defn create-one
  [req]
  (let [users (db/query users-model/create (-> req :body))]
    (-> (r/response {:data (first users)})
        (r/status 201))))