(ns com.ncpenterprises.clomosy.engines.simplev2-test
  (:require [clojure.test :refer :all]
            [com.ncpenterprises.clomosy.engines.simplev2 :refer :all]))

(deftest get-patch-test
  (testing "returns [module-id output] mapped to the input or nil if no module is ampped"
    (let [patches {[:module-2 :input-1] [:module-1 :output-1]
                   [:module-2 :input-2] [:module-1 :output-2]}]
      (is (= (get-patch patches :module-2 :input-1) [:module-1 :output-1]))
      (is (= (get-patch patches :module-2 :input-3) nil)))))

(deftest get-inputs-test
  (testing "returns a map of the inputs names to values, with 0.0 as the default if there is no patch to the input"
    (let [patches {[:module-3 :input-1] [:module-1 :output-1]
                   [:module-3 :input-2] [:module-2 :output-2]}
          outputs {:module-1 {:output-1 1.0}
                   :module-2 {:output-1 2.1
                              :output-2 2.2}}]
      (is (= (get-inputs :module-3 [:input-1 :input-2 :input-3] patches outputs)
             {:input-1 1.0
              :input-2 2.2
              :input-3 0.0})))))

(deftest evaluate-module-test
  (testing "returns the output values and updated state for the module"
    (let [module (map->Module {:id :module-2
                               :input-names [:add]
                               :update-fn (fn [inputs state dt]
                                            (let [add (:add inputs)
                                                  sum (+ (:sum state) add)]
                                              {:state {:sum sum}
                                               :output {:sum sum
                                                        :added add}}))})
          input-state {:module-2 {:sum 10.0}}
          outputs {:module-1 {:output-1 1.0}}
          patches {[:module-2 :add] [:module-1 :output-1]}
          dt 1.0
          output (evaluate-module module patches outputs input-state dt)]
      (is (= output {:state {:sum 11.0}
                     :output {:sum 11.0
                              :added 1.0}})))))

(deftest evaluate-test
  (testing "evaluates the modules in the expected order, updating the states"
    (let [constant-module (map->Module {:id :constant-module
                                        :update-fn (fn [inputs state dt]
                                                     {:output {:value 5.0}}) })
          incrementing-module (map->Module {:id :incrementing-module
                                            :update-fn (fn [inputs state dt]
                                                         (let [value (+ (:value state) 1.0)]
                                                           {:state {:value value}
                                                            :output {:value value}}))})
          summing-module (map->Module {:id :summing-module
                                       :input-names [:input-1
                                                    :input-2]
                                       :update-fn (fn [inputs state dt]
                                                   (let [value (+ (:input-1 inputs) (:input-2 inputs))]
                                                     {:output {:sum value}}))})
          modules {(:id constant-module) constant-module
                   (:id incrementing-module) incrementing-module
                   (:id summing-module) summing-module}
          order [:constant-module :incrementing-module :summing-module]
          initial-state {(:id incrementing-module) {:value 0.0}}
          patches {[:summing-module :input-1] [:constant-module :value]
                   [:summing-module :input-2] [:incrementing-module :value]}
          dt 1.0
          result (evaluate modules initial-state patches order dt)
          state (:state result)
          outputs (:outputs result)]
      (is (= state {(:id incrementing-module) {:value 1.0}}))
      (is (= outputs {(:id constant-module) {:value 5.0}
                      (:id incrementing-module) {:value 1.0}
                      (:id summing-module) {:sum 6.0}})))))

(deftest initial-state-test
  (testing "each module's initial state fn value is loaded into the initial state map"
    (let [module-1 (map->Module {:id :module-1
                                 :initial-state-fn (fn [] 1.0) })
          module-2 (map->Module {:id :module-2
                                 :initial-state-fn (fn [] 2.0) })
          modules {(:id module-1) module-1
                   (:id module-2) module-2}
          initial-state (initial-state modules)]
      (is (= initial-state {(:id module-1) 1.0
                            (:id module-2) 2.0})))))