(ns clomosy.modules.oscillator-test
  (:require [clojure.test :refer :all]
            [clomosy.modules.oscillator :as oscillator]))

(deftest sampled-wave-fn-initial-state-fn-test
  (testing "the oscillator is initialized with phase of 0"
    (is (= (:phase (oscillator/sampled-wave-fn-initial-state-fn)) 0.0) "the phase in the initial state is 0.0")))

(deftest sampled-wave-fn-update-fn
  (testing "the phase is updated for each frame and the amplitude output "
    (let [update-fn (oscillator/get-update-fn 2.0 (fn [phase] (if (>= phase Math/PI) 1.0 0.0)))
          frequency 1.0
          result (update-fn {:frequency frequency} {:phase 0.0})
          state-phase (:phase (:state result))
          output-phase (:phase (:outputs result))
          output-amplitude (:amplitude (:outputs result))]
      (is (= state-phase output-phase) "the state and output phase match")
      (is (= output-phase Math/PI) "the new phase is updated by Pi since sample rate = 2 * freq")
      (is (= output-amplitude 1.0) "the amplitude is equal to the expected value at Pi for the sampled wave"))))