(ns test-helpers.db
  (:require [clojure.edn :as edn]
            [clojure.java.jdbc :as jdbc]
            [repl.migration :as migration]
            [db.core :as db]
            [honeysql.core :as sql]
            [honeysql.helpers :refer :all]))

(defn- get-migration-count
  []
  (let [q (-> (select :%count.id)
              (from :ragtime_migrations)
              sql/format)]
    (-> (jdbc/query db/connection q)
        first
        :count)))

(defn- migrate-down!
  []
  (dorun (get-migration-count) (repeatedly migration/rollback!)))

(defn- get-dummies!
  [dummy-name]
  (-> (str "./test/test_helpers/dummy_data/" dummy-name ".edn")
      slurp
      edn/read-string))

(defn- insert-one-dummy!
  [dummy]
  (doseq [table-values dummy]
    (let [{:keys [table rows]} table-values
          queries (map (fn [row]
                         (as-> (insert-into table) $
                           (apply columns $ (keys row))
                           (values $ [(vals row)])))
                       rows)]
      (doseq [q queries]
        (jdbc/execute! db/connection (sql/format q))))))

(defn- insert-dummies!
  [& dummies]
  (doseq [dummy dummies]
    (insert-one-dummy! dummy)))

(defn reset!
  []
  (migrate-down!)
  (migration/migrate!))

(defn reset-with-dummies!
  [& dummy-names]
  (reset!)
  (apply insert-dummies! (map get-dummies! dummy-names)))