(ns com.ncpenterprises.clomosy.modules.oscillator
  (:require [com.ncpenterprises.clomosy.oscillators :as osc])
  )

(defn inc-phase [phase frequency dt]
  (rem (+ (* frequency dt 2 Math/PI) phase) (* 2 Math/PI))
  )

(defn phase [state midi-frame inputs dt]
  (inc-phase (:phase inputs) (:frequency inputs) dt))

(defn sine-amplitude [state midi-frame inputs dt]
  (osc/sine-wave (:phase inputs)))

(defn sine-wave [id]
  {

   :id id

   :inputs #{
            :phase :frequency
            }

   :outputs {
             :phase phase
             :amplitude sine-amplitude
             }
   }
  )

(defn triangle-amplitude [state midi-frame inputs dt]
  (osc/triangle-wave (:phase inputs)))

(defn triangle-wave [id]
  {

   :id id

   :inputs #{
             :phase :frequency
             }

   :outputs {
             :phase   phase
             :amplitude triangle-amplitude
             }

   }
  )

(defn pulse-amplitude [state midi-frame inputs dt]
  (osc/pulse-wave (:phase inputs) (:duty-cycle inputs)))

(defn pulse-wave [id]
  {

   :id id

   :inputs #{
             :phase
             :frequency
             :duty-cycle
             }

   :outputs {
             :phase   phase
             :amplitude pulse-amplitude
             }
   }
  )

