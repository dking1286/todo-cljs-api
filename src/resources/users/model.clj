(ns resources.users.model
  (:refer-clojure :exclude [update])
  (:require [clojure.spec.alpha :as s]
            [honeysql.helpers :refer :all]
            [buddy.hashers :as hashers]
            [db.model :refer [IQueryValidation IQuery IQueryOnError]]
            [lib.honeysql :refer [returning]]
            [utils.http-exceptions :as errors]
            [utils.postgresql :as psql]))

(s/def ::first-name string?)
(s/def ::last-name string?)
(s/def ::email string?)
(s/def ::password string?)

(s/def ::user-data
  (s/keys :req-un [::first-name ::last-name ::email ::password]))

(defn- hash-password
  [user-data]
  (let [password (:password user-data)]
    (if (nil? password)
      user-data
      (assoc user-data :password (hashers/derive password)))))

(def create
  (reify
    IQueryValidation
    (validate [this data]
      (when-not (s/valid? ::user-data data)
        (throw (errors/bad-request-error
                "Cannot create user with the provided data"))))
    IQuery
    (query [this data]
      (let [prepared-data (hash-password data)]
        (as-> (insert-into :users) $
              (apply columns $ (keys prepared-data))
              (values $ [(vals prepared-data)])
              (returning $ :*))))
    IQueryOnError
    (on-error [this e]
      (if (psql/conflict-error? e)
        (throw (errors/conflict-error (.getMessage e)))
        (throw e)))))

(def get-by-token
  (reify
    IQuery
    (query [this token]
      (-> (select :users.* :access_tokens.token)
          (from :users)
          (join :access_tokens [:= :users.id :access_tokens.user_id])
          (where [:= :access_tokens.token token])))))
