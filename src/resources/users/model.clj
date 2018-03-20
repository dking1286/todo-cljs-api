(ns resources.users.model
  (:refer-clojure :exclude [update])
  (:require [clojure.spec.alpha :as s]
            [honeysql.helpers :refer :all]
            [lib.honeysql :refer [returning]]
            [db.model :refer [query
                              defquery
                              defentity
                              IExposedAttributes
                              IQueryValidation
                              IQuery
                              IPostQuery]]
            [db.errors :refer [validation-error]]
            [utils.auth :refer [hash-string]]))

(s/def ::first-name string?)
(s/def ::last-name string?)
(s/def ::email string?)
(s/def ::password string?)

(s/def ::user-data
  (s/keys :req-un [::first-name ::last-name ::email ::password]))

(s/def ::user-attrs-scope #{:public :self})

(defentity User [id first-name last-name email password]
  IExposedAttributes
  (exposed-attributes
    [this scope]
    {:pre [(s/valid? ::user-attrs-scope scope)]}
    (condp = scope
      :public #{:id :first-name :last-name :email}
      :self #{:id :first-name :last-name :email})))

(defmacro defuserquery
  [name-sym & forms]
  (if (some #{'IPostQuery} forms)
    `(defquery ~name-sym ~@forms)
    `(defquery ~name-sym
      ~@forms
      IPostQuery
      (post-query
        [this# results#]
        (if (sequential? results#)
          (map map->User results#)
          (map->User results#))))))

(defn- hash-password
  [user-data]
  (let [password (:password user-data)]
    (if (nil? password)
      user-data
      (assoc user-data :password (hash-string password)))))

(defuserquery create
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

(defuserquery get-by-token
  IQuery
  (query
   [this token]
   (-> (select :users.* :access_tokens.token)
       (from :users)
       (join :access_tokens [:= :users.id :access_tokens.user_id])
       (where [:= :access_tokens.token token]))))

(defuserquery get-by-email
  IQuery
  (query
   [_ email]
   (-> (select :*)
       (from :users)
       (where [:= :email email]))))

(defuserquery create-seed?
  IQuery
  (query
   [_ {:keys [email]}]
   (query get-by-email email))
  IPostQuery
  (post-query
   [_ results]
   (= (count results) 0)))