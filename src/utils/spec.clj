(ns utils.spec)

(defn length-32?
  [s]
  (= 32 (count s)))

(defn length-greater-than?
  [num]
  (fn [s]
    (> num (count s))))