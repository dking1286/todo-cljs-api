(ns utils.formatting-test
  (:require [clojure.test :refer :all]
            [utils.formatting :refer :all]))

(deftest to-db
  (testing "utils.formatting/clj->db"
    (testing "should strip the '?' from boolean identifiers and prepend 'is_'"
      (is (= {:is_done true}
             (clj->db {:done? true}))))
    (testing "should transform kebab-case into snake_case"
      (is (= {:first_name "Daniel" :last_name "King"}
             (clj->db {:first-name "Daniel" :last-name "King"}))))
    (testing "should transform a boolean identifier with kebab case correctly"
      (is (= {:is_healthcare_provider true}
             (clj->db {:healthcare-provider? true}))))))