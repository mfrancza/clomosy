(ns com.ncpenterprises.clomosy.modules.amplification)

(defn out [state midi-frame inputs dt]
  (* (:in inputs) (:gain inputs)))

(defn linear-amplifier [id]
  {

   :id id

   :inputs #{
             :in
             :gain
            }

   :outputs {
             :out out
             }

   }
  )