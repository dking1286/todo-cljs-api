(ns middleware.development
  (:require [environ.core :refer [env]]))

(defn dev-only
  [middleware]
  (if (= "development" (:environment env)) middleware identity))