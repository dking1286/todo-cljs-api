(ns utils.response
  (:require [ring.util.response :as r]))

(defn not-found
  []
  (-> (r/response "Not found")
      (r/status 404)))

(defn bad-request
  [message]
  (-> (r/response (or message "Bad request"))
      (r/status 400)))