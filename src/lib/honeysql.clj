(ns lib.honeysql
  (:require [honeysql.format :as fmt]
            [honeysql.helpers :refer [defhelper]]))

(defmethod fmt/format-clause :returning
  [[op v] sqlmap]
  (str "RETURNING " (fmt/to-sql v)))

(defhelper returning
  [m args]
  (assoc m :returning (first args)))

