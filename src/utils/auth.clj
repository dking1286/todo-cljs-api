(ns utils.auth
  (:require [buddy.hashers :as hashers]))

(defn hash-string
  [string]
  (hashers/derive string))

(defn hash-matches?
  [string hashed]
  (hashers/check string hashed))