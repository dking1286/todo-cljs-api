(ns middleware.authorization
  (:require [clojure.spec.alpha :as s]
            [utils.http-exceptions :refer [unauthorized-error]]))

(s/def ::scope #{:anonymous :authenticated})

(defn wrap-authorization
  [scope]
  {:pre [(s/valid? ::scope scope)]}
  (fn [handler]
    (fn [req]
      (condp = scope
        :anonymous (handler req)
        :authenticated (if-not (req :identity)
                         (throw (unauthorized-error))
                         (handler req))))))