(ns db.errors
  (:require [clojure.string :as string]))

(defn validation-error
  ([message]
    (ex-info message {}))
  ([message meta]
    (ex-info message (merge {:type "Validation"} meta))))

(defn validation-error?
  [e]
  (= "Validation" (get (ex-data e) :type)))

(defn conflict-error?
  [e]
  (and (instance? org.postgresql.util.PSQLException e)
        (string/includes? (.getMessage e)
                          "duplicate key value violates unique constraint")))