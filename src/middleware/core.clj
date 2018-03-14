(ns middleware.core
  (:require [clojure.spec.alpha :as s]
            [environ.core :refer [env]]))

(s/def ::environment #{"development" "test" "production"})
(s/def ::envs (s/coll-of ::environment :kind set?))

(defn in-env
  [envs middleware]
  {:pre [(s/valid? ::envs envs)
         (fn? middleware)]}
  (if (contains? envs (get env :environment))
    middleware
    identity))