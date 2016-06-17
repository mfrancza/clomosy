(ns com.ncpenterprises.clomosy.modules.audio
  (:require [com.ncpenterprises.clomosy.io.audio :as io]))


(defn update-state [state midi-frame inputs dt]
  (assoc state :buffer (io/output-frame
                         (:line state)
                         (:buffer state)
                         (:buffer-size state)
                         (* 126.0 (:audio inputs))
                         )

               )
  )

(defn initial-state [line buffer-size]
  {
   :buffer []
   :line line
   :buffer-size buffer-size
   }
  )

(defn mono-output [id line buffer-size]
  {

   :id     id

   :inputs #{
            :audio
            }

   :state  (initial-state line buffer-size)


   :update update-state
   }
  )

