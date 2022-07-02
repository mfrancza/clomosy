(ns clomosy.synths.basic-synths
  (:require [com.ncpenterprises.clomosy.modules.midi :as midi-modules]
            [com.ncpenterprises.clomosy.modules.intonation :as intonation-modules]
            [com.ncpenterprises.clomosy.modules.oscillator :as oscillator-modules]
            [com.ncpenterprises.clomosy.modules.mixer :as mixer-modules]
            [com.ncpenterprises.clomosy.modules.audio :as audio-modules]))

(defn monophonic-sine-wave
  [frame-rate]
  {:modules {:keyboard (midi-modules/monophonic-keyboard)
             :intonation (intonation-modules/twelve-tone-equal-temperament)
             :oscillator (oscillator-modules/sampled-wave-fn frame-rate oscillator-modules/sine-wave-fn)
             :amplification (mixer-modules/mixer 2 *)
             :output (audio-modules/mono-output-line {:sample-rate frame-rate :sample-size-in-bits 8 :channels 1} (/ frame-rate 10))}
   :order   [:keyboard
             :intonation
             :oscillator
             :amplification
             :output]
   :patches {[:intonation :note] [:keyboard :note]
             [:oscillator :frequency] [:intonation :frequency]
             [:amplification :input-1] [:oscillator :amplitude]
             [:amplification :input-2] [:keyboard :gate]
             [:output :output] [:amplification :out]}})