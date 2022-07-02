(ns com.ncpenterprises.clomosy.modules.constant
  (:require [com.ncpenterprises.clomosy.engines.simple :as simple-engine]))

(defn get-constant-update-fn
  [value]
  (fn [inputs state] {:outputs {:value value}}))

(defn constant [value]
  "returns a Module that always outputs the provided value"
  (simple-engine/map->Module {:update-fn       (get-constant-update-fn value)
                                 :output-names [:value]}))

