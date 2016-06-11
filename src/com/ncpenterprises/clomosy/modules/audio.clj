(ns com.ncpenterprises.clomosy.modules.audio
  (:require [com.ncpenterprises.clomosy.io.audio :as io]))


(defn mono-output [id line buffer-size]
  {

   :id     id

   :inputs #{
            :audio
            }

   :state  {
            :buffer []
            :line line
            :buffer-size buffer-size
            }


   :update (fn [state midi-frame inputs dt]
             (assoc state :buffer (io/output-frame
                                    (:line state)
                                    (:buffer state)
                                    (:buffer-size state)
                                    (* 126.0 (:audio inputs))
                                    )

                          )
             )
   }
  )

