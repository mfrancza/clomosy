(ns com.ncpenterprises.clomosy.modules.oscillator
  (:require [com.ncpenterprises.clomosy.engines.simple :as simple-engine]))

(defn sampled-wave-fn-initial-state-fn
  []
  {:phase 0.0})

(defn get-update-fn
  [sample-rate wave-fn]
  (fn [inputs state]
    (let [frequency (:frequency inputs)
          phase (-> (:phase state)
                    (+ (/ (* 2 Math/PI frequency) sample-rate))
                    (mod (* 2 Math/PI)))
          amplitude (wave-fn phase)]
      {:state {:phase phase}
       :outputs {:amplitude amplitude
                 :phase phase}})))

(defn sampled-wave-fn [sample-rate wave-fn]
  "returns a Module which tracks the phase of an arbitrary wave function.
  updates the phase based on the frequency from the input and outputs the phase and amplitude based on the provided wave function"
  (simple-engine/map->Module {:initial-state-fn sampled-wave-fn-initial-state-fn
                                 :update-fn     (get-update-fn sample-rate wave-fn)
                                 :input-names   [:frequency]
                                 :output-names  [:amplitude
                                                :phase]}))

(defn sine-wave-fn
  [phase]
  (Math/sin phase))

(defn sawtooth-wave-fn
  [phase]
  (* 2
     (- (/ phase (* 2.0 Math/PI))
        (Math/floor (+ 0.5 (/ phase (* 2.0 Math/PI)))))))

(defn triangle-wave-fn
  [phase]
  (- (* 2 (Math/abs ^double (sawtooth-wave-fn phase))) 1))