(ns resources.clients.model-test
  (:require [clojure.test :refer :all]
            [db.model :refer [validate]]
            [resources.clients.model :as clients]))

(deftest clients-create-validation
  (testing "create validation"
    (testing "should report valid if the inupt data passes the specs"
      (let [client-data {:name "test-client"
                         :client-id "00000000000000000000000000000000"
                         :client-secret "00000000000000000000000000000000"}]
        (is (nil? (validate clients/create client-data)))))
    (testing "should report valid if :trusted? is present and a boolean"
      (let [client-data {:name "test-client"
                         :client-id "00000000000000000000000000000000"
                         :client-secret "00000000000000000000000000000000"
                         :trusted? true}]
        (is (nil? (validate clients/create client-data)))))
    (testing "should report invalid if trusted? is present but not a boolean"
      (let [client-data {:name "test-client"
                         :client-id "00000000000000000000000000000000"
                         :client-secret "00000000000000000000000000000000"
                         :trusted? "blah"}]
        (is (not (nil? (validate clients/create client-data))))))
    (testing "should report invalid if name is not present"
      (let [client-data {:client-id "00000000000000000000000000000000"
                         :client-secret "00000000000000000000000000000000"
                         :trusted? true}]
        (is (not (nil? (validate clients/create client-data))))))
    (testing "should report invalid if name is not a string"
      (let [client-data {:name 1
                         :client-id "00000000000000000000000000000000"
                         :client-secret "00000000000000000000000000000000"
                         :trusted? true}]
        (is (not (nil? (validate clients/create client-data))))))
    (testing "should report invalid if client-id is not 32 characters long"
      (let [client-data {:name "test-client"
                         :client-id "0000000000000000000000000000"
                         :client-secret "00000000000000000000000000000000"
                         :trusted? true}]
        (is (not (nil? (validate clients/create client-data))))))
    (testing "should report invalid if client-secret is not 32 characters long"
      (let [client-data {:name "test-client"
                         :client-id "00000000000000000000000000000000"
                         :client-secret "0000000000000000000000000000"
                         :trusted? true}]
        (is (not (nil? (validate clients/create client-data))))))))