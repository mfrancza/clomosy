(ns com.ncpenterprises.clomosy.modules.memory)

(defn memory-cell [id initial-value]
  {

   :id id

   :inputs #{
            :in
            }

   :outputs {
             :out (fn [state midi-frame inputs dt]
                        (:value state))
             }

   :state   {
             :value initial-value
             }

   :update (fn [state midi-frame inputs dt] state
             )

   :update-after (fn [state midi-frame inputs dt]
             (assoc state :value (:in inputs)))
   }
  )


