(ns com.ncpenterprises.clomosy.synths.v2.basic-synths
  (:require [com.ncpenterprises.clomosy.modules.v2.midi :as midi-modules]
            [com.ncpenterprises.clomosy.modules.v2.intonation :as intonation-modules]
            [com.ncpenterprises.clomosy.modules.v2.oscillator :as oscillator-modules]
            [com.ncpenterprises.clomosy.modules.v2.amplification :as amplification-modules]
            [com.ncpenterprises.clomosy.modules.v2.audio :as audio-modules]))

(defn monophonic-sine-wave
  [frame-rate]
  {:modules {:keyboard (midi-modules/monophonic-keyboard)
             :intonation (intonation-modules/twelve-tone-equal-temperament)
             :oscillator (oscillator-modules/sampled-wave-fn frame-rate oscillator-modules/sine-wave-fn)
             :amplification (amplification-modules/linear-amplifier)
             :output (audio-modules/mono-output frame-rate)}
   :order   [:keyboard
             :intonation
             :oscillator
             :amplification
             :output]
   :patches {[:intonation :note] [:keyboard :note]
             [:oscillator :frequency] [:intonation :frequency]
             [:amplification :in] [:oscillator :amplitude]
             [:amplification :gain] [:keyboard :gate]
             [:output :output] [:amplification :out]}})