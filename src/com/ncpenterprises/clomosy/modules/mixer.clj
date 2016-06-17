(ns com.ncpenterprises.clomosy.modules.mixer)

(defn output [state midi-frame inputs dt]
  (+ (:input_1 inputs) (:input_2 inputs)))

(defn mixer [id]
  {

   :id id

   :inputs #{
             :input_1
             :input_2
             }

   :outputs {
             :output
             }
   }
  )
