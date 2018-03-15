(ns repl.seed
  (:require [clojure.spec.alpha :as s]
            [clojure.edn :as edn]
            [clojure.java.jdbc :as jdbc]
            [db.core :as db]))

(defn- has-create?
  [model-sym]
  (require model-sym)
  (-> (ns-publics model-sym) (get 'create)))

(s/def ::seed-set (s/coll-of ::seed :kind vector?))
(s/def ::seed (s/keys :req-un [::model ::rows]))
(s/def ::model (s/and symbol? has-create?))
(s/def ::rows (s/coll-of map? :kind vector?))

(defn- get-seed-set
  [name]
  {:pre [(string? name)]}
  (-> (str "./resources/seeds/" name ".edn")
      slurp
      edn/read-string))

(defn- insert-one-seed-set!
  [seed-set]
  {:pre [(s/valid? ::seed-set seed-set)]}
  (doseq [seed seed-set]
    (let [{:keys [model rows]} seed]
      (require model)
      (let [create-seed? (some-> (ns-publics model) (get 'create-seed?) deref)
            create (-> (ns-publics model) (get 'create) deref)]
        (doseq [row rows]
          (when (or (not create-seed?) (db/query create-seed? row))
            (db/query create row)))))))

(defn- insert-seed-sets!
  [& seed-sets]
  (doseq [seed-set seed-sets]
    (insert-one-seed-set! seed-set)))

(defn seed!
  [& seed-set-names]
  (apply insert-seed-sets! (map get-seed-set seed-set-names)))