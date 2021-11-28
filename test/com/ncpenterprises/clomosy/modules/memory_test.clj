(ns com.ncpenterprises.clomosy.modules.memory-test
  (:require [clojure.test :refer :all] [com.ncpenterprises.clomosy.modules.memory :refer :all]))

(deftest test-memory-cell-out-value
  (testing "Stored value is retrieved"
    (is (= 0.0 (memory-cell-out {:value 0.0} nil nil nil)))))

(deftest test-delay-line-out-value
  (testing "Value for the expected delay time is retrieved"
    (is (= (delay-line-out {:values (vec (range 1 100))}
                 nil
                 {:delay-time 1.0}
                 0.1
                 )
               (+ 10 1)
               )
             )))

(deftest test-delay-line-update-state-after
  (testing "The input value is added to the delay line and the last value removed"
    (is (= (let [state {:values [2.0 1.0]}
                 inputs {:in 3.0}]
             (delay-line-update-state-after state nil inputs nil))
           {:values [3.0 2.0]})))
  )