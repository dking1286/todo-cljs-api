(ns utils.postgresql
  (:require [clojure.string :as string]))

(defn conflict-error?
  [e]
  (and (instance? org.postgresql.util.PSQLException e)
       (string/includes? (.getMessage e)
                         "duplicate key value violates unique constraint")))