(ns test-helpers.db
  (:refer-clojure :exclude [update reset!])
  (:require [repl.migration :as migration]
            [repl.seed :as seed]))

(defn reset!
  []
  (migration/migrate!)
  (migration/migrate-down!)
  (migration/migrate!))

(defn reset-with-seeds!
  [& seed-set-names]
  (reset!)
  (apply seed/seed! seed-set-names))
