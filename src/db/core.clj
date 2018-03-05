(ns db.core
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.core :as sql]
            [environ.core :refer [env]]
            [db.model :refer [IQuery IQueryValidation IQueryOnError]]
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

(defn query
  [q & params]
  {:pre [(satisfies? IQuery q)]}
  (when (satisfies? IQueryValidation q)
    (apply db.model/validate q params))
  (try
    (->> (map param->db-style params)
         (apply db.model/query q)
         sql/format
         (jdbc/query connection)
         (map db->clj))
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