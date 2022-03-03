(ns com.ncpenterprises.clomosy.modules.v2.amplification
  (:require [com.ncpenterprises.clomosy.engines.simplev2 :as simple-v2-engine]))

(defn linear-amplifier-update-fn
  [inputs state]
  (let [in (:in inputs)
        gain (:gain inputs)]
    {:outputs {:out (* in gain)}}))

(defn linear-amplifier []
  "returns a Module that outputs the product of the in and gain inputs"
  (simple-v2-engine/map->Module {:update-fn        linear-amplifier-update-fn
                                 :input-names      [:in
                                                    :gain]
                                 :output-names     [:out]}))