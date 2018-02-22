(ns middleware.cors
  (:require [clojure.string :as string]
            [environ.core :refer [env]]
            [ring.util.response :as r]))

(def cors-headers
  {"Access-Control-Allow-Origin" (:frontend-url env)
    "Access-Control-Allow-Headers" ["content-type"]
    "Access-Control-Allow-Methods" ["GET" "POST" "PATCH" "DELETE"]})

(def serialized-cors-headers
  (into {}
        (map (fn [[name val]]
               [name (if (vector? val) (string/join "," val) val)]))
        cors-headers))

(defn- with-cors-headers
  [res]
  (reduce #(apply r/header %1 %2) res serialized-cors-headers))

(defn wrap-cors
  [handler]
  (fn [req]
    (let [response (if (= (-> req :request-method) :options)
                     (r/response nil)
                     (handler req))]
      (with-cors-headers response))))