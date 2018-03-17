(ns test-helpers.fixtures
  (:require [clojure.java.io]
            [test-helpers.db :refer [reset-with-seeds!]]))

(defn with-dummies
  [& dummy-names]
  (fn [test]
    ;; Use seeds from the test/ folder by default
    (let [full-names (->> dummy-names
                          (map #(str "test/" %))
                          (map (fn [name] {:name name :args []})))]
      ;; Suppress the migration notifications written to stdout
      (with-out-str
        (apply reset-with-seeds! full-names))
      (test))))