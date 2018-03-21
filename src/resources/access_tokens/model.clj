(ns resources.access-tokens.model
  (:refer-clojure :exclude [update])
  (:require [clojure.spec.alpha :as s]
            [honeysql.helpers :refer :all]
            [lib.honeysql :refer [returning]]
            [utils.spec :refer [length-32?]]
            [db.model :refer [query defquery defentity
                              IQuery IQueryValidation IPostQuery
                              IExposedAttributes]]
            [db.errors :refer [validation-error]]))

(s/def ::token (s/and string? #(> (count %) 16)))
(s/def ::client-id integer?)
(s/def ::user-id integer?)

(s/def ::access-token-data
  (s/keys :req-un [::token ::client-id]
          :opt-un [::user-id]))

(s/def ::access-tokens-attrs-scope #{:public})

(defentity AccessToken [id token client-id user-id]
  IExposedAttributes
  (exposed-attributes
    [_ scope]
    {:pre [(s/valid? ::access-tokens-attrs-scope scope)]}
    (condp = scope
      :public #{:token})))

(defmacro defaccesstokenquery
  [name-sym & forms]
  (if (some #{'IPostQuery} forms)
    `(defquery ~name-sym ~@forms)
    `(defquery ~name-sym
      ~@forms
      IPostQuery
      (post-query
        [this# results#]
        (if (sequential? results#)
          (map map->AccessToken results#)
          (map->AccessToken results#))))))

(defaccesstokenquery create
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

(defaccesstokenquery delete-by-token
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

(defaccesstokenquery get-by-token
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

(defaccesstokenquery create-seed?
  IQuery
  (query
   [_ {:keys [token]}]
   (query get-by-token token))
  IPostQuery
  (post-query
   [_ results]
   (= (count results) 0)))
