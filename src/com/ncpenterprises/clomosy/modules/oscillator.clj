(ns com.ncpenterprises.clomosy.modules.oscillator)

(defn sine-wave [id]
  {

   :id id

   :inputs {

            }

   :outputs {
             :phase   (fn [state midi-frame inputs dt]
                        (rem (+ (* (:frequency inputs) dt 2 Math/PI) (:phase inputs) ) (* 2 Math/PI)))
             :amplitude (fn [state midi-frame inputs dt]
                          (rem (+ (* (:frequency inputs) dt 2 Math/PI) (:phase inputs) ) (* 2 Math/PI)))
             }

   :state   {
             :notes-on ()
             }


   :update (fn [state midi-frame inputs dt]
             nil)
   }
  )
