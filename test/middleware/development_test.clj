(ns middleware.development-test
  (:require [clojure.test :refer :all]
            [middleware.development :refer [dev-only]]))

(deftest test-dev-only
  (testing "dev-only"
    (testing "should return the passed-in function if the environment is dev"
      (with-redefs [environ.core/env {:environment "development"}]
        (let [hello (fn [] "hello world")]
          (is (= hello
                 (dev-only hello))))))
    (testing "should return identity if the environment is not development"
      (with-redefs [environ.core/env {:environment "production"}]
        (let [hello (fn [] "hello world")]
          (is (= identity
                 (dev-only hello))))))))