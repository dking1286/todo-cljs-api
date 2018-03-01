(ns resources.clients.model
  (:refer-clojure :exclude [update])
  (:require [honeysql.helpers :refer :all]
            [lib.honeysql :refer [returning]]))

(defn create
  [data]
  (as-> (insert-into :clients) $
        (apply columns $ (keys data))
        (values $ [(vals data)])
        (returning $ :*)))