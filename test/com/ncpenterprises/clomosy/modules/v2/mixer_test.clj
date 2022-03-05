(ns com.ncpenterprises.clomosy.modules.v2.mixer-test
  (:require [clojure.test :refer :all]
            [com.ncpenterprises.clomosy.modules.v2.mixer :as mixer]))

(deftest get-input-names-test
  (testing "the input names are input-1 to input-n"
    (is (= (mixer/get-input-names 3) [:input-1 :input-2 :input-3]))))

(deftest get-mixer-update-fn-test
  (testing "the update-fn sums the expected number of inputs"
    (let [inputs {:input-1 1.0 :input-2 10.0 :input-3 100.0}
          update-fn (mixer/get-mixer-update-fn + (mixer/get-input-names 3))
          outputs (:outputs (update-fn inputs nil))]
      (is (= outputs {:out 111.0})))))