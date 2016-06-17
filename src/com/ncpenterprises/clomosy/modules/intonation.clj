(ns com.ncpenterprises.clomosy.modules.intonation)

(defn frequency [state midi-frame inputs dt]
  (* 440
     (Math/pow 2
               (/ (-
                    (:note inputs)
                    69)
                  12))))

(defn twelve-tone-equal-temperment [id]
  {

   :id id

   :inputs #{
             :note
             }

   :outputs {
             :frequency frequency
             }

   }
  )