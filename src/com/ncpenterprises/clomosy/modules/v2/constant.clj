(ns com.ncpenterprises.clomosy.modules.v2.constant
  (:require [com.ncpenterprises.clomosy.engines.simplev2 :as simple-v2-engine]))

(defn get-constant-update-fn
  [value]
  (fn [inputs state] {:outputs {:value value}}))

(defn constant [value]
  "returns a Module that always outputs the provided value"
  (simple-v2-engine/map->Module {:update-fn (get-constant-update-fn value)
                                 :output-names [:value]}))

