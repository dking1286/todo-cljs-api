(ns middleware.response
  (:import (java.io ByteArrayOutputStream))
  (:require [cognitect.transit :as transit]))

(defn- get-writer-type
  [accept]
  (condp = accept
    "application/json" :json-verbose
    "application/transit+json" :json
    :json-verbose))

(defn serialize-response-body
  [body writer-type]
  (let [out-stream (ByteArrayOutputStream.)
        writer (transit/writer out-stream writer-type)]
    (transit/write writer body)
    (.toString out-stream)))

(defn wrap-json-response-body
  [handler]
  (fn [req]
    (let [response (handler req)]
      (if (nil? (:body response))
        response
        (let [writer-type (get-writer-type (-> req :headers (get "accept")))
              serialized-body (serialize-response-body (:body response)
                                                       writer-type)
              wrapped-response (assoc response :body serialized-body)]
          wrapped-response)))))

