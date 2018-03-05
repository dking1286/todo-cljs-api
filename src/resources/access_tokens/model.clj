(ns resources.access-tokens.model
  (:refer-clojure :exclude [update])
  (:require [honeysql.helpers :refer :all]
            [lib.honeysql :refer [returning]]
            [db.model :refer [IQuery]]))

(def create
  (reify
    IQuery
    (query [_ data]
      (as-> (insert-into :access_tokens) $
            (apply columns $ (keys data))
            (values $ [(vals data)])
            (returning $ :*)))))
