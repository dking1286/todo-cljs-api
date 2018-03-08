(ns utils.oauth2
  (:require [clojure.spec.alpha :as s]
            [db.core :as db]
            [resources.clients.model :as clients]
            [resources.users.model :as users]
            [utils.http-exceptions :refer [bad-request-error
                                           unauthorized-error]]
            [utils.auth :refer [hash-matches?]]))

(defmulti check-credentials :grant-type)

(defmethod check-credentials "password"
  [{:keys [username password]}]
  (when-let [user (first (db/query users/get-by-email username))]
    (when (hash-matches? password (user :password))
      true)))

(defmethod check-credentials "client_credentials"
  [{:keys [client-id client-secret]}]
  (when-let [client (first (db/query clients/get-by-id client-id))]
    (when (and (client :trusted?)
               (= (client :client-secret) client-secret))
      true)))

(defmethod check-credentials :default
  [data]
  (throw (bad-request-error (str "Invalid grant type " (data :grant-type)))))