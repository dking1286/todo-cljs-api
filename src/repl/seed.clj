(ns repl.seed
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [clojure.edn :as edn]
            [clojure.java.jdbc :as jdbc]
            [buddy.core.nonce :as nonce]
            [db.core :as db]
            [resources.clients.model :as clients]))

(defn- has-create?
  [model-str]
  {:pre [(string? model-str)]
   :post [(boolean? %)]}
  (let [model-sym (symbol model-str)]
    (require model-sym)
    (-> (ns-publics model-sym) (get 'create) boolean)))

(s/def ::seed (s/keys :req-un [::model ::rows]))
(s/def ::model (s/and string? has-create?))
(s/def ::rows (s/coll-of map? :kind vector?))

(s/def ::seed-set (s/coll-of ::seed :kind vector?))
(s/def ::seed-set-name (s/keys :req-un [::name] :opt-un [::args]))
(s/def ::name string?)
(s/def ::args vector?)

(defn- get-seed-creator
  [name]
  {:pre [(string? name)]}
  (-> (str "./resources/seeds/" name ".edn")
      slurp
      edn/read-string
      eval))

(defn- create-seed-set
  [{:keys [name args]}]
  (apply (get-seed-creator name) args))

(defn- insert-one-seed-set!
  [seed-set]
  {:pre [(s/valid? ::seed-set seed-set)]}
  (doseq [seed seed-set]
    (let [{:keys [model rows]} seed
          model-sym (symbol model)]
      (require model-sym)
      (let [create-seed? (some-> (ns-publics model-sym) (get 'create-seed?) deref)
            create (-> (ns-publics model-sym) (get 'create) deref)]
        (doseq [row rows]
          (when (or (not create-seed?) (db/query create-seed? row))
            (db/query create row)))))))

(defn- insert-seed-sets!
  [& seed-sets]
  (doseq [seed-set seed-sets]
    (insert-one-seed-set! seed-set)))

(defn seed!
  [& seed-set-names]
  {:pre [(every? #(s/valid? ::seed-set-name %) seed-set-names)]}
  (apply insert-seed-sets! (map create-seed-set seed-set-names)))

(defn seed-for-development!
  []
  (let [web-client-id (string/join "" (take 32 (repeat "0")))
        web-client-secret (string/join "" (take 32 (repeat "0")))
        web-server-client-id (string/join "" (take 32 (repeat "1")))
        web-server-client-secret (string/join "" (take 32 (repeat "1")))]
    (seed! {:name "common/web_client"
            :args [web-client-id web-client-secret]}
           {:name "common/web_server_client"
            :args [web-server-client-id web-server-client-secret]})))

(defn seed-for-production!
  []
  (let [web-client-id (clients/generate-client-id)
        web-client-secret (clients/generate-client-id)
        web-server-client-id (clients/generate-client-id)
        web-server-client-secret (clients/generate-client-id)]
    (seed! {:name "common/web_client"
            :args [web-client-id web-client-secret]}
           {:name "common/web_server_client"
            :args [web-server-client-id web-server-client-secret]})))