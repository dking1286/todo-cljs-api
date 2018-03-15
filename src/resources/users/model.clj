(ns resources.users.model
  (:refer-clojure :exclude [update])
  (:require [clojure.spec.alpha :as s]
            [honeysql.helpers :refer :all]
            [lib.honeysql :refer [returning]]
            [db.model :refer [query defquery IQueryValidation IQuery IPostQuery]]
            [db.errors :refer [validation-error]]
            [utils.auth :refer [hash-string]]))

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
      (assoc user-data :password (hash-string password)))))

(defquery create
  IQueryValidation
  (validate
   [this data]
   (when-not (s/valid? ::user-data data)
     (validation-error
      "Cannot create user with the provided data"
      {:details (with-out-str (s/explain ::user-data data))})))
  IQuery
  (query
   [this data]
   (let [prepared-data (hash-password data)]
     (as-> (insert-into :users) $
       (apply columns $ (keys prepared-data))
       (values $ [(vals prepared-data)])
       (returning $ :*)))))

(defquery get-by-token
  IQuery
  (query
   [this token]
   (-> (select :users.* :access_tokens.token)
       (from :users)
       (join :access_tokens [:= :users.id :access_tokens.user_id])
       (where [:= :access_tokens.token token]))))

(defquery get-by-email
  IQuery
  (query
   [_ email]
   (-> (select :*)
       (from :users)
       (where [:= :email email]))))

(defquery create-seed?
  IQuery
  (query
   [_ {:keys [email]}]
   (query get-by-email email))
  IPostQuery
  (post-query
   [_ results]
   (= (count results) 0)))