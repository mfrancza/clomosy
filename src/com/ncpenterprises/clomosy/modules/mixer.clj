(ns com.ncpenterprises.clomosy.modules.mixer
  (:require [clojure.test :as test]))


(defn output [state midi-frame inputs dt]
    (+ (:in_1 inputs) (:in_2 inputs)))

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

