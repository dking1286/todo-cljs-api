(ns resources.clients.model
  (:refer-clojure :exclude [update])
  (:require [clojure.spec.alpha :as s]
            [honeysql.helpers :refer :all]
            [lib.honeysql :refer [returning]]
            [db.model :refer [defquery IQuery IQueryValidation]]
            [db.errors :refer [validation-error]]))

(defn- length-32?
  [c]
  (= 32 (count c)))

(s/def ::name string?)
(s/def ::client-id (s/and string? length-32?))
(s/def ::client-secret (s/and string? length-32?))
(s/def ::trusted? boolean?)

(s/def ::client-data
  (s/keys :req-un [::name ::client-id ::client-secret]
          :opt-un [::trusted?]))

(defquery create
  IQueryValidation
  (validate [_ data]
    (when-not (s/valid? ::client-data data)
      (validation-error
       "Cannot create client with the provided data"
       {:details (with-out-str (s/explain ::client-data data))})))
  IQuery
  (query [_ data]
    (as-> (insert-into :clients) $
          (apply columns $ (keys data))
          (values $ [(vals data)])
          (returning $ :*))))

(defquery get-by-id
  IQueryValidation
  (validate [_ client-id]
    (when-not (s/valid? ::client-id client-id)
      (validation-error
       (str "Invalid client-id " client-id " provided"))))
  IQuery
  (query [_ client-id]
    (-> (select :*)
        (from :clients)
        (where [:= :client_id client-id]))))
