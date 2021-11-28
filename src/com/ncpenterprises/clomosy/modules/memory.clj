(ns com.ncpenterprises.clomosy.modules.memory
  (:require [clojure.test :as test]))


(defn memory-cell-out [state midi-frame inputs dt]
    (:value state))

(defn memory-cell-update-state-after [state midi-frame inputs dt]
  (assoc state :value (:in inputs)))

(defn memory-cell [id initial-value]
  {

   :id id

   :inputs #{
            :in
            }

   :outputs {
             :out memory-cell-out
             }

   :state   {
             :value initial-value
             }

   :update-after memory-cell-update-state-after
   }
  )

(defn delay-line-out [state midi-frame inputs dt]
  (let [
        delay-time (:delay-time inputs)
        index (long (/ delay-time dt))
        values (:values state)
        ]
      (get values index))
    )

(test/with-test
  (defn delay-line-update-state-after [state midi-frame inputs dt]
    (assoc state :values
                           (into [(:in inputs) ]
                            (pop (:values state))
                            )
                           ))

)

(defn delay-line [id initial-value max-delay dt]
  {

   :id id

   :inputs #{
             :in
             :delay-time
             }

   :outputs {
             :out delay-line-out
             }

   :state   {
             :values (vec (repeat (long (/ max-delay dt)) initial-value))
             }

   :update-after delay-line-update-state-after
   }
  )


