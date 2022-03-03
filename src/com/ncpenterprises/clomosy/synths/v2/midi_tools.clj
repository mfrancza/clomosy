(ns com.ncpenterprises.clomosy.synths.v2.midi-tools
  (:require [com.ncpenterprises.clomosy.modules.v2.midi :as midi-modules]))

(defn midi-printer
  [frame-rate]
  {:modules {:midi-printer (midi-modules/printer)}
   :order [:midi-printer]})
