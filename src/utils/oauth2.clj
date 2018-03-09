(ns utils.oauth2
  (:require [environ.core :refer [env]]
            [buddy.core.codecs :as codecs]
            [buddy.core.kdf :as kdf]
            [buddy.core.nonce :as nonce]
            [db.core :as db]
            [resources.clients.model :as clients]
            [resources.users.model :as users]
            [utils.http-exceptions :refer [bad-request-error
                                           unauthorized-error]]
            [utils.auth :refer [hash-matches?]]))

(defmulti get-identity-by-credentials :grant-type)

(defmethod get-identity-by-credentials "password"
  [{:keys [client-id username password]}]
  (when-let [client (first (db/query clients/get-by-id client-id))]
    (when-let [user (first (db/query users/get-by-email username))]
      (when (hash-matches? password (user :password))
        {:user user :client client}))))

(defmethod get-identity-by-credentials "client_credentials"
  [{:keys [client-id client-secret]}]
  (when-let [client (first (db/query clients/get-by-id client-id))]
    (when (and (client :trusted?)
               (= (client :client-secret) client-secret))
      {:user nil :client client})))

(defmethod get-identity-by-credentials :default
  [data]
  (throw (bad-request-error (str "Invalid grant type " (data :grant-type)))))

(def hkdf
  (kdf/engine {:alg :hkdf+sha256
               :key (get env :token-generation-key)
               :salt (nonce/random-bytes 8)}))

(defn create-token
  []
  (-> (kdf/get-bytes hkdf 16)
      codecs/bytes->hex))