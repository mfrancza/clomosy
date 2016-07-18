(ns com.ncpenterprises.clomosy.modules.composite)

(defn evaluate-subsynth [state midi-frame inputs dt]  )


(defn composite [id
                 sub-synth
                 sub-inputs
                 sub-outputs
                 ]
  {
   {
    :id id

    :inputs #{
              :in_1
              :in_2
              }

    :outputs {
              :out
              }
    }

   :update evaluate-subsynth
   }
  )