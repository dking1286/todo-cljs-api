(ns middleware.error-handling
  (:require [ring.util.response :as r]
            [utils.http-exceptions :as errors]))

(defn- get-printable-stack-trace
  [e]
  (apply str (interpose "\n" (.getStackTrace e))))

(defn- handle-error
  [e]
  (if-not (-> e ex-data :status)
    (do 
      (println e)
      (println "An error occurred:")
      (println (.getMessage e))
      (println (get-printable-stack-trace e))
      (handle-error (errors/internal-server-error)))
    (-> (r/response (.getMessage e))
        (r/status (-> e ex-data :status)))))

(defn wrap-error-handling
  [handler]
  (fn [req]
    (try
      (handler req)
      (catch Exception e
        (handle-error e)))))