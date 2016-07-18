(ns com.ncpenterprises.clomosy.modules.mixer
  (:require [clojure.test :as test]))


(test/with-test
  (defn output [state midi-frame inputs dt]
    (+ (:in_1 inputs) (:in_2 inputs)))

  (test/is (=
             (output
               {}
               nil
               {:in_1 1.0
                :in_2 2.0
                }
               0.1
               )
             (+ 1.0 2.0)
             )
           )
  )


(defn mixer [id]
  {

   :id id

   :inputs #{
             :in_1
             :in_2
             }

   :outputs {
             :out output
             }
   }
  )

