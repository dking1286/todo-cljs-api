(ns middleware.logging
  (:require [ring.logger :refer [wrap-with-logger]]
            [environ.core :refer [env]]))

(defn wrap-logging
  [handler]
  (condp = (:environment env)
    "test" handler
    (wrap-with-logger handler)))