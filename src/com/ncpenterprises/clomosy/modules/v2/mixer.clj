(ns com.ncpenterprises.clomosy.modules.v2.mixer
  (:require [com.ncpenterprises.clomosy.engines.simplev2 :as simple-v2-engine]))

(defn get-input-names
  [channels]
  (map #(keyword (str "input-" %)) (range 1 (+ channels 1))))

(defn get-mixer-update-fn
  [mixing-fn input-names]
  (fn [inputs state]
    (let [input-values (map #(% inputs) input-names)]
      {:outputs {:out (apply mixing-fn input-values)}})))

(defn mixer
  [channels mixing-fn]
  "returns a Module with the specified number of input channels as input-n starting with input-1 which outputs the mixing-fn applied to the values of the inputs"
  (let [input-names (get-input-names channels)]
    (simple-v2-engine/map->Module {:update-fn    (get-mixer-update-fn mixing-fn input-names)
                                   :input-names  input-names
                                   :output-names [:out]})))