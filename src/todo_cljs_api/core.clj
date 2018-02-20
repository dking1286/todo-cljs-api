(ns todo-cljs-api.core
  (:require [environ.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.lint :refer [wrap-lint]]
            [middleware.development :refer [dev-only]]
            [routes.core :refer [root-handler]]))

(def middleware-stack
  (comp
    (fn [handler]
      (fn [req]
        (println req)
        (handler req)))
    (dev-only wrap-lint)))

(def app (middleware-stack root-handler))
