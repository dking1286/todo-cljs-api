(ns routes.core
  (:require [clojure.edn :as edn]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [middleware.authorization :refer [wrap-authorization]]
            [resources.access-tokens.controller :as access-tokens-controller]
            [resources.todos.controller :as todos-controller]
            [resources.users.controller :as users-controller]))

(defroutes root-handler
  (GET "/" []
    "Running")
  (context "/oauth2" []
    (POST "/token" []
      (wrap-authorization
       :anonymous
       access-tokens-controller/create-access-token))
    (POST "/revoke" []
      (wrap-authorization
       :authenticated
       access-tokens-controller/revoke-access-token)))
  (context "/users" []
    (POST "/" []
      (wrap-authorization
       :anonymous
       users-controller/create-one)))
  (context "/todos" []
    (GET "/" []
      (wrap-authorization
       :anonymous ;; TODO: Fix authentication here
       todos-controller/get-all))
    (POST "/" []
      (wrap-authorization
       :anonymous ;; TODO: Here too
       todos-controller/create-one))
    (GET "/:id" [id]
      (wrap-authorization
       :anonymous ;; TODO: Here
       (partial todos-controller/get-one (edn/read-string id))))
    (PATCH "/:id" [id]
      (wrap-authorization
       :anonymous ;; TODO: Here
       (partial todos-controller/update-one (edn/read-string id))))
    (DELETE "/:id" [id]
      (wrap-authorization
       :anonymous ;; TODO: Here
       (partial todos-controller/delete-one (edn/read-string id)))))
  (route/not-found {:status 404 :body "Not found"}))