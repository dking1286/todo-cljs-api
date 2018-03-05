(ns middleware.cors
  (:require [clojure.string :as string]
            [environ.core :refer [env]]
            [ring.util.response :as r]))

(defn- with-cors-headers
  [res]
  (-> res
      (r/header "Access-Control-Allow-Origin" (:frontend-url env))
      (r/header "Access-Control-Allow-Headers" "content-type")
      (r/header "Access-Control-Allow-Methods" "GET,POST,PATCH,DELETE")))

(defn wrap-cors
  [handler]
  (fn [req]
    (let [response (if (= (-> req :request-method) :options)
                     (r/response nil)
                     (handler req))]
      (with-cors-headers response))))