(ns com.ncpenterprises.clomosy.modules.amplification)


(defn linear-amplifier [id]
  {

   :id id

   :inputs #{
             :in
             :gain
            }

   :outputs {
             :out   (fn [state midi-frame inputs dt]
                        (* (:in inputs) (:gain inputs)))
             }

   :state   {
             }


   :update (fn [state midi-frame inputs dt]
             {})
   }
  )