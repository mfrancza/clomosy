(ns com.ncpenterprises.clomosy.modules.memory)

(defn out [state midi-frame inputs dt]
  (:value state))

(defn update-state-after [state midi-frame inputs dt]
  (assoc state :value (:in inputs)))

(defn memory-cell [id initial-value]
  {

   :id id

   :inputs #{
            :in
            }

   :outputs {
             :out out
             }

   :state   {
             :value initial-value
             }

   :update-after update-state-after
   }
  )


