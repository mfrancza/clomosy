(ns clomosy.modules.intonation
  (:require [clomosy.engines.simple :as simple-engine]))

(defn to-frequency ^double [note-number concert-a]
  (* concert-a
     (Math/pow 2
               (/ (-
                    note-number
                    69)
                  12))))

(defn twelve-tone-equal-temperament-update-fn
  [inputs state]
  {:outputs {:frequency (to-frequency (:note inputs) (:concert-a inputs))}})

(defn twelve-tone-equal-temperament
  "Translates a MIDI note number to a frequency in twelve-tone equal temperament with A4 = the :concert-a input"
  []
  (simple-engine/map->Module {:update-fn       twelve-tone-equal-temperament-update-fn
                                 :input-names  [:note
                                                :concert-a]
                                 :output-names [:frequency]}))

