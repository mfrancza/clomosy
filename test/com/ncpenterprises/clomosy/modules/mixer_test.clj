(ns com.ncpenterprises.clomosy.modules.mixer-test
  (:require [clojure.test :refer :all] [com.ncpenterprises.clomosy.modules.mixer :refer :all]))

(deftest test-output
  (testing "The output is the sum of the input values"
    (is (= (output {} nil {:in_1 1.0 :in_2 2.0} 0.1)
           (+ 1.0 2.0)))
    ))