(ns com.ncpenterprises.clomosy.modules.mixer)

(defn mixer [id]
  {

   :id id

   :inputs #{
             :input_1
             :input_2
             }

   :outputs {
             :output   (fn [state midi-frame inputs dt]
                         (+ (:input_1 inputs) (:input_2 inputs)))
             }

   :state   {
             }


   :update (fn [state midi-frame inputs dt]
             {})
   }
  )
