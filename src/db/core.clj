(ns db.core
  (:require [clojure.java.jdbc :as jdbc]
            [honeysql.core :as sql]
            [environ.core :refer [env]]
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
  [query-fn & params]
  (->> (map param->db-style params)
       (apply query-fn)
       sql/format
       (jdbc/query connection)
       (map db->clj)))

(defn execute!
  [query-fn & params]
  (->> (map param->db-style params)
       (apply query-fn)
       sql/format
       (jdbc/execute! connection)))