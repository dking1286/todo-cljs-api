(ns resources.users.model
  (:refer-clojure :exclude [update])
  (:require [honeysql.helpers :refer :all]
            [buddy.hashers :as hashers]
            [lib.honeysql :refer [returning]]))

(defn- hash-password
  [user-data]
  (let [password (:password user-data)]
    (if (nil? password)
      user-data
      (assoc user-data :password (hashers/derive password)))))

(defn create
  [data]
  (let [prepared-data (hash-password data)]
    (as-> (insert-into :users) $
          (apply columns $ (keys prepared-data))
          (values $ [(vals prepared-data)])
          (returning $ :*))))

(defn get-by-token
  [token]
  (-> (select :users.* :access_tokens.token)
      (from :users)
      (join :access_tokens [:= :users.id :access_tokens.user_id])
      (where [:= :access_tokens.token token])))