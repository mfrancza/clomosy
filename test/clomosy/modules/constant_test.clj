(ns clomosy.modules.constant-test
  (:require [clojure.test :refer :all]
            [clomosy.modules.constant :as constant]))

(deftest constant-update-fn-test
  (testing "the module outputs the specified value"
    (let [update-fn (constant/get-constant-update-fn 1.23)
          result (update-fn {} nil)]
      (is (= (:value (:outputs result)) 1.23)))))
