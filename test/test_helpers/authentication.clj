(ns test-helpers.authentication
  (:require [clojure.string :as string]))

(defn- get-access-token
  [id]
  (if (> id 9)
    (throw (Exception. "Ids greater than 9 not supported"))
    (string/join "" (repeat 32 (str id)))))

(defn- attach-auth-header
  [req token]
  (-> req
      (update :headers (fn [headers] (or headers {})))
    (assoc-in [:headers "Authorization"] (str "Token " token))))

(defn with-access-token
  [req id]
  (attach-auth-header req (get-access-token id)))