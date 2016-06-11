(ns com.ncpenterprises.clomosy.modules.oscillator
  (:require [com.ncpenterprises.clomosy.oscillators :as osc])
  )

(defn inc-phase [phase frequency dt]
  (rem (+ (* frequency dt 2 Math/PI) phase) (* 2 Math/PI))
  )

(defn sine-wave [id]
  {

   :id id

   :inputs #{
            :phase :frequency
            }

   :outputs {
             :phase   (fn [state midi-frame inputs dt]
                        (inc-phase (:phase inputs) (:frequency inputs) dt))
             :amplitude (fn [state midi-frame inputs dt]
                          (osc/sine-wave (:phase inputs)))
             }

   :state   {
             }


   :update (fn [state midi-frame inputs dt]
             {})
   }
  )

(defn triangle-wave [id]
  {

   :id id

   :inputs #{
             :phase :frequency
             }

   :outputs {
             :phase   (fn [state midi-frame inputs dt]
                        (inc-phase (:phase inputs) (:frequency inputs) dt))
             :amplitude (fn [state midi-frame inputs dt]
                          (osc/triangle-wave (:phase inputs)))
             }

   :state   {
             }


   :update (fn [state midi-frame inputs dt]
             {})
   }
  )

(defn pulse-wave [id]
  {

   :id id

   :inputs #{
             :phase
             :frequency
             :duty-cycle
             }

   :outputs {
             :phase   (fn [state midi-frame inputs dt]
                        (inc-phase (:phase inputs) (:frequency inputs) dt))
             :amplitude (fn [state midi-frame inputs dt]
                          (osc/pulse-wave (:phase inputs) (:duty-cycle inputs)))
             }

   :state   {
             }


   :update (fn [state midi-frame inputs dt]
             {})
   }
  )

