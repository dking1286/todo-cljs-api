(ns utils.formatting
  (:require [clojure.string :as string]))

(defn- boolean-name->db
  [kw]
  (if-not (string/ends-with? kw "?")
    kw
    (keyword (str "is_" (string/replace (name kw) #"\?$" "")))))

(defn- db->boolean-name
  [kw]
  (if-not (string/starts-with? (name kw) "is_")
    kw
    (keyword (str (string/replace (name kw) #"^is_" "") "?"))))

(defn kebab->pascal
  [s]
  (->> (string/split s #"-")
       (map string/capitalize)
       (apply str)))

(defn- kebab->snake
  [kw]
  (keyword (string/replace (name kw) #"-" "_")))

(defn- snake->kebab
  [kw]
  (keyword (string/replace (name kw) #"_" "-")))

(defn clj->db
  [m]
  (into {}
        (comp (map (fn [[k v]] [(boolean-name->db k) v]))
              (map (fn [[k v]] [(kebab->snake k) v])))
        m))

(defn db->clj
  [m]
  (into {}
        (comp (map (fn [[k v]] [(db->boolean-name k) v]))
              (map (fn [[k v]] [(snake->kebab k) v])))
        m))