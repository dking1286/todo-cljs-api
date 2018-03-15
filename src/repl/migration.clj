(ns repl.migration
  (:refer-clojure :exclude [update])
  (:require [ragtime.repl :as repl]
            [clojure.java.io :as io]
            [clojure.java.jdbc :as jdbc]
            [clojure.string :as string]
            [clojure.edn :as edn]
            [honeysql.core :as sql]
            [honeysql.helpers :refer :all]
            [db.core :as db]
            [db.migration :as migration]))

(defn- left-pad-zeros
  [s num-digits]
  (let [num-zeros (- num-digits (count s))]
    (str (apply str (repeat (max 0 num-zeros) "0")) s)))

(defn- remove-left-pad-zeros
  [s]
  (string/replace s #"^0+" ""))

(defn- inc-migration-tag
  [migration-tag]
  (-> migration-tag
      remove-left-pad-zeros
      edn/read-string
      inc
      str
      (left-pad-zeros 3)))

(defn- get-next-migration-tag
  []
  (let [files (.listFiles (io/file migration/migrations-dir))]
    (if (empty? files)
      "001"
      (->> files
           (map #(.getName %))
           (map #(string/split % #"-"))
           (map first)
           sort
           last
           inc-migration-tag))))

(defn- get-migration-count
  []
  (let [q (-> (select :%count.id)
              (from :ragtime_migrations)
              sql/format)]
    (-> (jdbc/query db/connection q)
        first
        :count)))

(defn create-migration!
  [name]
  (let [migration-tag (get-next-migration-tag)
        filename (str migration-tag "-" name)]
    (spit (str migration/migrations-dir "/" filename ".up.sql") "")
    (spit (str migration/migrations-dir "/" filename ".down.sql") "")))

(defn migrate!
  []
  (repl/migrate (migration/get-config)))

(defn rollback!
  []
  (repl/rollback (migration/get-config)))

(defn migrate-down!
  []
  (dorun (get-migration-count) (repeatedly rollback!)))