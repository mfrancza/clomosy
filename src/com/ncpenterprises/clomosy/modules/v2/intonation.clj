(ns com.ncpenterprises.clomosy.modules.v2.intonation
  (:require [com.ncpenterprises.clomosy.engines.simplev2 :as simple-v2-engine]))

(defn to-frequency ^double [note-number]
  (* 440
     (Math/pow 2
               (/ (-
                    note-number
                    69)
                  12))))

(defn twelve-tone-equal-temperament-update-fn
  [inputs state]
  {:outputs {:frequency (to-frequency (:note inputs))}})

(defn twelve-tone-equal-temperament
  "Translates a MIDI note number to a frequency in twelve-tone equal temperament with A4 = 440Hz"
  []
  (simple-v2-engine/map->Module {:update-fn twelve-tone-equal-temperament-update-fn
                                 :input-names [:note]
                                 :output-names [:frequency]}))

