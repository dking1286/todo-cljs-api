(ns db.model)

(defprotocol IQueryValidation
  (validate [a] [a b]))

(defprotocol IQuery
  (query [a] [a b] [a b c]))

(defprotocol IQueryOnError
  (on-error [this e]))
