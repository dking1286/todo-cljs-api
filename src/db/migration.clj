(ns db.migration
  (:require [ragtime.jdbc :as jdbc]
            [clojure.string :as string]
            [db.core :refer [connection]]))

(def migrations-dir
  "resources/migrations")

(defn get-config
  []
  {:datastore
   (jdbc/sql-database connection)

   :migrations
   (jdbc/load-resources (string/replace migrations-dir "resources/" ""))})
