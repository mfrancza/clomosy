(ns com.ncpenterprises.clomosy.modules.intonation)


(defn twelve-tone-equal-temperment [id]
  {

   :id id

   :inputs #{
             :note
             }

   :outputs {
             :frequency   (fn [state midi-frame inputs dt]
                      (* 440
                         (Math/pow 2
                                   (/ (-
                                        (:note inputs)
                                        69)
                                      12))))
             }

   :state   {
             }


   :update (fn [state midi-frame inputs dt]
             {})
   }
  )