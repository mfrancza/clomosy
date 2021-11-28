(ns com.ncpenterprises.clomosy.modules.midi
  (:require [com.ncpenterprises.clomosy.io.midi :as midi]))

(defn gate [state midi-frame inputs dt]
  (if (empty? (:notes-on state)) 0 1))

(defn note [state midi-frame inputs dt]
  (if (nil? (first (:notes-on state))) 0 (first (:notes-on state))))

(defn trigger [state midi-frame inputs dt]
  (not (nil? midi-frame)))

(defn update-state [state midi-frame inputs dt]
  (midi/apply-message state midi-frame))

(defn initial-state []
  {
   :notes-on ()
   }
  )


(defn monophonic-keyboard [id]
  {

   :id      id
   :inputs  {}
   :outputs {
             :gate    gate
             :note    note
             :trigger trigger
             }
   :state   (initial-state)
   :update  update-state
   }
  )


