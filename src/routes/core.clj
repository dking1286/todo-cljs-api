(ns routes.core
  (:require [clojure.edn :as edn]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [middleware.authorization :refer [wrap-authorization]]
            [middleware.exposed-attributes :refer [wrap-exposed-attributes]]
            [resources.access-tokens.controller :as access-tokens-controller]
            [resources.todos.controller :as todos-controller]
            [resources.users.controller :as users-controller]))

(defroutes root-handler
  (GET "/" []
    "Running")
  (context "/oauth2" []
    (POST "/token" []
      (let [middleware (comp (wrap-authorization :anonymous)
                             (wrap-exposed-attributes :public))]
        (middleware access-tokens-controller/create-access-token)))
    (POST "/revoke" []
      (let [middleware (comp (wrap-authorization :authenticated))]
        (middleware access-tokens-controller/revoke-access-token))))
  (context "/users" []
    (POST "/" []
      (let [middleware (comp (wrap-authorization :anonymous)
                             (wrap-exposed-attributes :public))]
        (middleware users-controller/create-one))))
  (context "/todos" []
    (GET "/" []
      (let [middleware (comp (wrap-authorization :anonymous))]
        (middleware todos-controller/get-all)))
    (POST "/" []
      (let [middleware (comp (wrap-authorization :anonymous))]
        (middleware todos-controller/create-one)))
    (GET "/:id" [id]
      (let [middleware (comp (wrap-authorization :anonymous))]
        (middleware (partial todos-controller/get-one (edn/read-string id)))))
    (PATCH "/:id" [id]
      (let [middleware (comp (wrap-authorization :anonymous))]
        (middleware (partial todos-controller/update-one (edn/read-string id)))))
    (DELETE "/:id" [id]
      (let [middleware (comp (wrap-authorization :anonymous))]
        (middleware (partial todos-controller/delete-one (edn/read-string id))))))
  (route/not-found {:status 404 :body "Not found"}))