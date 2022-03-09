(ns com.ncpenterprises.clomosy.synths.v2.audio-tools
  (:require [com.ncpenterprises.clomosy.modules.v2.constant :as constant-modules]
            [com.ncpenterprises.clomosy.modules.v2.oscillator :as oscillator-modules]
            [com.ncpenterprises.clomosy.modules.v2.mixer :as mixer-modules]
            [com.ncpenterprises.clomosy.modules.v2.audio :as audio-modules]))


(defn test-tone
  [frame-rate]
  {:modules {:frequency     (constant-modules/constant 440.0)
             :oscillator    (oscillator-modules/sampled-wave-fn frame-rate oscillator-modules/sine-wave-fn)
             :output        (audio-modules/mono-output-line {:sample-rate frame-rate :sample-size-in-bits 8 :channels 1} (/ frame-rate 10))}
   :order   [:frequency
             :oscillator
             :output]
   :patches {[:oscillator :frequency]  [:frequency :value]
             [:output :output]         [:oscillator :amplitude]}})