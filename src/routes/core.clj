(ns routes.core
  (:require [clojure.edn :as edn]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [resources.access-tokens.controller :as access-tokens-controller]
            [resources.todos.controller :as todos-controller]
            [resources.users.controller :as users-controller]))

(defroutes root-handler
  (GET "/" []
    "Running")
  (context "/oauth2" []
    (POST "/token" []
      access-tokens-controller/create-access-token))
  (context "/users" []
    (POST "/" []
      users-controller/create-one))
  (context "/todos" []
    (GET "/" []
      todos-controller/get-all)
    (POST "/" []
      todos-controller/create-one)
    (GET "/:id" [id]
      (partial todos-controller/get-one (edn/read-string id)))
    (PATCH "/:id" [id]
      (partial todos-controller/update-one (edn/read-string id)))
    (DELETE "/:id" [id]
      (partial todos-controller/delete-one (edn/read-string id))))
  (route/not-found {:status 404 :body "Not found"}))