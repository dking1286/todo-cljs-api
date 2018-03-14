(ns todo-cljs-api.core
  (:require [environ.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.lint :refer [wrap-lint]]
            [middleware.core :refer [in-env]]
            [middleware.logging :refer [wrap-logging]]
            [middleware.cors :refer [wrap-cors]]
            [middleware.request :refer [wrap-json-request-body]]
            [middleware.response :refer [wrap-json-response-body]]
            [middleware.authentication :refer [wrap-token-authentication]]
            [middleware.error-handling :refer [wrap-error-handling]]
            [routes.core :refer [root-handler]]))

(def middleware-stack
  (comp
   (in-env #{"development"} wrap-lint)
   (in-env #{"development" "production"} wrap-logging)
   (in-env #{"development" "test" "production"} wrap-cors)
   (in-env #{"development" "test" "production"} wrap-token-authentication)
   (in-env #{"development" "production"} wrap-json-request-body)
   (in-env #{"development" "production"} wrap-json-response-body)
   (in-env #{"development" "test" "production"} wrap-error-handling)))

(def app (middleware-stack root-handler))
