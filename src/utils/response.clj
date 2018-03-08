(ns utils.response
  (:require [ring.util.response :as r]))

(defn not-found
  []
  (-> (r/response "Not found")
      (r/status 404)))

(defn bad-request
  ([] (bad-request nil))
  ([message]
    (-> (r/response (or message "Bad request"))
        (r/status 400))))

(defn conflict
  ([] (conflict nil))
  ([message]
    (-> (r/response (or message "Conflict"))
        (r/status 409))))