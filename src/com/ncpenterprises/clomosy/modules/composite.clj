(ns com.ncpenterprises.clomosy.modules.composite)

(defn evaluate-subsynth [state midi-frame inputs dt]

  )

(defn build-output [])


(defn composite [id
                 sub-synth
                 sub-inputs
                 sub-outputs
                 ]
   {
    :id id

    :inputs (keys sub-inputs)

    :outputs (reduce (fn [
                          output-functions
                          [composite-out module-out]
                          ]
                       (assoc
                         output-functions
                         composite-out
                         (fn [state midi-frame inputs dt]
                           (module-out (:sub-synth-state state))
                           )
                         )
                       )
                     {}
                     (seq sub-outputs)
                     )



   :state   {
             :sub-synth-state {}
             }

    :update evaluate-subsynth
   }
  )