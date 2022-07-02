(ns clomosy.synths.midi-tools
  (:require [clomosy.modules.midi :as midi-modules]))

(defn midi-printer
  [frame-rate]
  {:modules {:midi-printer (midi-modules/printer)}
   :order [:midi-printer]})
