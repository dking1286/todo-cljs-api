(ns middleware.request
  (:require [cognitect.transit :as transit]
            [utils.response :refer [bad-request]]))

(defn- get-reader-type
  "Determines the type of transit reader needed
  to handle a request with the given `content-type`"
  [content-type]
  (condp = content-type
    "application/json" :json-verbose
    "application/transit+json" :json
    :else :json-verbose))

(defn- parse-request-body
  "Parses the request `body` into a clojure data structure."
  [body reader-type]
  (transit/read (transit/reader body reader-type)))

(defn wrap-json-request-body
  "Middleware that parses the request body using Transit"
  [handler]
  (fn [req]
    (cond
      (or (= (:request-method req) :get)
          (= (:request-method req) :delete))
      (handler req)
      
      (nil? (-> req :headers (get "content-type")))
      (bad-request "Missing content-type header")
      
      :else
      (let [reader-type (get-reader-type (-> req :headers (get "content-type")))
            parsed-body (parse-request-body (-> req :body) reader-type)
            wrapped-req (assoc req :body parsed-body)]
        (handler wrapped-req)))))