(ns utils.http-exceptions)

(defn bad-request-error
  [message]
  (ex-info message {:status 400}))

(defn conflict-error
  [message]
  (ex-info message {:status 409}))

(defn internal-server-error
  ([]
    (internal-server-error "Something went wrong. Please try again later."))
  ([message]
    (ex-info message {:status 500})))

(defn unauthorized-error
  ([]
    (unauthorized-error "Unauthorized"))
  ([message]
    (ex-info message {:status 401})))