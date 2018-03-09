(ns resources.access-tokens.controller
  (:require [ring.util.response :as r]
            [db.core :as db]
            [resources.clients.model :as clients]
            [resources.access-tokens.model :as access-tokens]
            [utils.http-exceptions :refer [unauthorized-error]]
            [utils.oauth2 :refer [get-identity-by-credentials
                                  create-token]]))

(defn create-access-token
  [req]
  (let [identity (get-identity-by-credentials (req :body))]
    (if (nil? identity)
      (throw (unauthorized-error "Invalid credentials"))
      (let [{:keys [client user]} identity
            token (first (db/query access-tokens/create
                                   {:client-id (client :id)
                                    :user-id (user :id)
                                    :token (create-token)}))]
        (r/response {:data token})))))