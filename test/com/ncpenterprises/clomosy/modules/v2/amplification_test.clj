(ns com.ncpenterprises.clomosy.modules.v2.amplification-test
  (:require [clojure.test :refer :all]
            [com.ncpenterprises.clomosy.modules.v2.amplification :as amplification]))

(deftest linear-amplifier-update-fn-test
  (testing "out is product of in and gain"
    (let [inputs {:in 3.0 :gain 15.0}
          result (amplification/linear-amplifier-update-fn inputs nil)
          out (:out (:outputs result))]
      (is (= out 45.0)))))