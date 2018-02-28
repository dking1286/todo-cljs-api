(ns todo-cljs-api.core
  (:require [environ.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.lint :refer [wrap-lint]]
            [middleware.development :refer [dev-only]]
            [middleware.logging :refer [wrap-logging]]
            [middleware.cors :refer [wrap-cors]]
            [middleware.request :refer [wrap-json-request-body]]
            [middleware.response :refer [wrap-json-response-body]]
            [middleware.auth :refer [wrap-token-auth]]
            [routes.core :refer [root-handler]]))

(def middleware-stack
  (comp
    (dev-only wrap-lint)
    wrap-logging
    wrap-cors
    wrap-token-auth
    wrap-json-request-body
    wrap-json-response-body))

(def app (middleware-stack root-handler))
