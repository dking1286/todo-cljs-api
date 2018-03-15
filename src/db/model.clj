(ns db.model)

(defmacro defquery
  [name & forms]
  (let [num-symbols (atom 0)
        symbols-seen (fn [x]
                       (when (symbol? x) (swap! num-symbols inc))
                       @num-symbols)
        protocol-impls (partition-by symbols-seen forms)]
    (when-not (every? #(symbol? (first %)) protocol-impls)
      (throw (Error. "Invalid arguments to defquery")))
      ;; TODO: Add better validation for defquery
    `(def ~name (reify ~@forms))))

(defprotocol IQueryValidation
  (validate [a] [a b]))

(defprotocol IQuery
  (query [a] [a b] [a b c]))

(defprotocol IQueryOnError
  (on-error [this e]))

(defprotocol IPostQuery
  (post-query [a] [a b]))