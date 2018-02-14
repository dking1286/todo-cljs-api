(ns repl.migration
  (:require [ragtime.repl :as repl]
            [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.edn :as edn]
            [db.migration :as migration]
            [utils.string :as string-util]))

(defn migrate!
  []
  (repl/migrate (migration/get-config)))

(defn rollback!
  []
  (repl/rollback (migration/get-config)))

(defn- inc-migration-tag
  [migration-tag]
  (-> migration-tag
      string-util/remove-left-pad-zeros
      edn/read-string
      inc
      str
      (string-util/left-pad-zeros 3)))

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

(defn create-migration!
  [name]
  (let [migration-tag (get-next-migration-tag)
        filename (str migration-tag "-" name)]
    (spit (str migration/migrations-dir "/" filename ".up.sql") "")
    (spit (str migration/migrations-dir "/" filename ".down.sql") "")))