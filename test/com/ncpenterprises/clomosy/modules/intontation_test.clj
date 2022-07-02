(ns com.ncpenterprises.clomosy.modules.intontation-test
  (:require [clojure.test :refer :all]
            [com.ncpenterprises.clomosy.modules.intonation :as intonation]))

(deftest twelve-tone-even-temperament-update-fn-test
  (testing "That A4/MIDI note 69 is 440.00Hz"
    (let [inputs {:note 69}
          result (intonation/twelve-tone-equal-temperament-update-fn inputs nil)
          frequency (get-in result [:outputs :frequency])]
      (is (< (Math/abs (- (double frequency) (double 440))) 0.01))))
  (testing "That C4/MIDI note 60 is 261.63Hz"
    (let [inputs {:note 60}
          result (intonation/twelve-tone-equal-temperament-update-fn inputs nil)
          frequency (get-in result [:outputs :frequency])]
      (is (< (Math/abs (- (double frequency) (double 261.63))) 0.01)))))