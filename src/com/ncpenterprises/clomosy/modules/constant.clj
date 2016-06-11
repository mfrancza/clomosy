(ns com.ncpenterprises.clomosy.modules.constant)

(defn constant [id value]
  {

   :id id

   :inputs #{
             }

   :outputs {
             :value (fn [state midi-frame inputs dt]
                      value)
             }

   :state   {
             }


   :update (fn [state midi-frame inputs dt]
             {})
   }
  )