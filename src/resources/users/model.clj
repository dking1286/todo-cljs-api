(ns resources.users.model
  (:refer-clojure :exclude [update])
  (:require [honeysql.helpers :refer :all]
            [lib.honeysql :refer [returning]]))

(defn create
  [data]
  (as-> (insert-into :users) $
        (apply columns $ (keys data))
        (values $ [(vals data)])
        (returning $ :*)))

(defn get-by-token
  [token]
  (-> (select :users.* :access_tokens.token)
      (from :users)
      (join :access_tokens [:= :users.id :access_tokens.user_id])
      (where [:= :access_tokens.token token])))