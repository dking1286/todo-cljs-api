(ns test-helpers.fixtures
  (:require [clojure.java.io]
            [test-helpers.db :refer [reset-with-dummies!]]))

(defn with-dummies
  [& dummy-names]
  (fn [test]
    ;; Suppress the migration notifications written to stdout
    (with-out-str
      (apply reset-with-dummies! dummy-names))
    (test)))