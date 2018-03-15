(ns db.core
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.core :as sql]
            [environ.core :refer [env]]
            [db.model :refer [IQuery IQueryValidation IQueryOnError IPostQuery]]
            [utils.formatting :refer [db->clj clj->db]]))

(def connection
  {:dbtype (:db-type env)
   :dbname (:db-name env)
   :host (:db-host env)
   :user (:db-user env)
   :password (:db-password env)})

(defn param->db-style
  [param]
  (if (map? param)
    (clj->db param)
    param))

(defn- do-validation!
  [q & params]
  {:pre [(satisfies? IQuery q)]}
  (when (satisfies? IQueryValidation q)
    (when-let [error (apply db.model/validate q params)]
      (throw error))))

(defn- do-query!
  [q & params]
  {:pre [(satisfies? IQuery q)]}
  (->> (map param->db-style params)
       (apply db.model/query q)
       sql/format
       (jdbc/query connection)
       (map db->clj)))

(defn- post-process
  [q results]
  (if (satisfies? IPostQuery q)
    (db.model/post-query q results)
    results))

(defn query
  [q & params]
  {:pre [(satisfies? IQuery q)]}
  (apply do-validation! q params)
  (try
    (->> (apply do-query! q params)
         (post-process q))
    (catch Exception e
      (if (satisfies? IQueryOnError q)
        (db.model/on-error q e)
        (throw e)))))

(defn execute!
  [q & params]
  {:pre [(satisfies? IQuery q)]}
  (when (satisfies? IQueryValidation q)
    (apply db.model/validate q params))
  (try
    (->> (map param->db-style params)
         (apply db.model/query q)
         sql/format
         (jdbc/execute! connection))
    (catch Exception e
      (if (satisfies? IQueryOnError)
        (db.model/on-error q e)
        (throw e)))))