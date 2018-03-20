(ns middleware.spec
  (:require [clojure.spec.alpha :as s]))

(s/def ::data (s/or :single map? :multiple list?))
(s/def ::response-body (s/keys :req-un [::data]))

(defn wrap-spec
  [handler]
  (fn [req]
    (let [response (handler req)]
      (s/assert ::response-body (response :body))
      response)))