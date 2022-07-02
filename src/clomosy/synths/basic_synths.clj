(ns clomosy.synths.basic-synths
  (:require [clomosy.modules.midi :as midi-modules]
            [clomosy.modules.intonation :as intonation-modules]
            [clomosy.modules.oscillator :as oscillator-modules]
            [clomosy.modules.mixer :as mixer-modules]
            [clomosy.modules.audio :as audio-modules]
            [clomosy.modules.constant :as constant-modules]))

(defn monophonic-sine-wave
  [frame-rate]
  {:modules {:keyboard      (midi-modules/monophonic-keyboard)
             :concert-a     (constant-modules/constant 430.0)
             :intonation    (intonation-modules/twelve-tone-equal-temperament)
             :oscillator    (oscillator-modules/sampled-wave-fn frame-rate oscillator-modules/sine-wave-fn)
             :amplification (mixer-modules/mixer 2 *)
             :output        (audio-modules/mono-output-line {:sample-rate frame-rate :sample-size-in-bits 8 :channels 1} (/ frame-rate 10))}
   :order   [:keyboard
             :concert-a
             :intonation
             :oscillator
             :amplification
             :output]
   :patches {[:intonation :concert-a]  [:concert-a :value]
             [:intonation :note]       [:keyboard :note]
             [:oscillator :frequency]  [:intonation :frequency]
             [:amplification :input-1] [:oscillator :amplitude]
             [:amplification :input-2] [:keyboard :gate]
             [:output :output]         [:amplification :out]}})