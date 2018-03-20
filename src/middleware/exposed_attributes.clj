(ns middleware.exposed-attributes
  (:require [db.model :refer [IExposedAttributes
                              exposed-attributes]]))

(defn- filter-exposed-attributes
  [scope x]
  (cond
    (sequential? x)
    (map (partial filter-exposed-attributes scope) x)

    (satisfies? IExposedAttributes x)
    (let [attrs (exposed-attributes x scope)]
      (into {} (filter (fn [[k v]] (attrs k))) x))
      
    :else x))

(defn wrap-exposed-attributes
  [scope]
  (fn [handler]
    (fn [req]
      (let [response (handler req)]
        (if (associative? (response :body))
          (update-in response
                     [:body :data]
                     (partial filter-exposed-attributes scope))
          response)))))