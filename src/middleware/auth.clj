(ns middleware.auth
  (:require [buddy.auth.middleware :refer [wrap-authentication]]
            [buddy.auth.backends :as backends]
            [db.core :as db]
            [resources.users.model :as users]))

(defn- get-user-by-token
  [req token]
  (if (nil? token)
    nil
    (first (db/query users/get-by-token token))))

(defn wrap-token-auth
  [handler]
  (wrap-authentication
    handler (backends/token {:authfn get-user-by-token})))