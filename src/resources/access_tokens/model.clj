(ns resources.access-tokens.model
  (:refer-clojure :exclude [update])
  (:require [clojure.spec.alpha :as s]
            [honeysql.helpers :refer :all]
            [lib.honeysql :refer [returning]]
            [utils.spec :refer [length-32?]]
            [db.model :refer [query defquery
                              IQuery IQueryValidation IPostQuery]]
            [db.errors :refer [validation-error]]))

(s/def ::token (s/and string? #(> (count %) 16)))
(s/def ::client-id integer?)
(s/def ::user-id integer?)

(s/def ::access-token-data
  (s/keys :req-un [::token ::client-id]
          :opt-un [::user-id]))

(defquery create
  IQueryValidation
  (validate
   [_ data]
   (when-not (s/valid? ::access-token-data data)
     (validation-error
      "Cannot create access-token with the provided data"
      {:details (with-out-str (s/explain ::access-token-data data))})))
  IQuery
  (query
   [_ data]
   (as-> (insert-into :access_tokens) $
     (apply columns $ (keys data))
     (values $ [(vals data)])
     (returning $ :*))))

(defquery delete-by-token
  IQueryValidation
  (validate
   [_ token]
   (when-not (s/valid? ::token token)
     (validation-error
      "Invalid token provided"
      {:details (with-out-str (s/explain ::token token))})))
  IQuery
  (query
   [_ token]
   (-> (delete-from :access_tokens)
       (where [:= :token token]))))

(defquery get-by-token
  IQueryValidation
  (validate
   [_ token]
   (when-not (s/valid? ::token token)
     (validation-error
      "Invalid token provided"
      {:details (with-out-str (s/explain ::token token))})))
  IQuery
  (query
   [_ token]
   (-> (select :*)
       (from :access_tokens)
       (where [:= :token token]))))

(defquery create-seed?
  IQuery
  (query
   [_ {:keys [token]}]
   (query get-by-token token))
  IPostQuery
  (post-query
   [_ results]
   (= (count results) 0)))
