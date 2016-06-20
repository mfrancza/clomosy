(ns com.ncpenterprises.clomosy.modules.memory
  (:require [clojure.test :as test]))


(test/with-test
  (defn memory-cell-out [state midi-frame inputs dt]
    (:value state))
  (test/is (= 0.0 (memory-cell-out {:value 0.0} nil nil nil)))
)

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

(test/with-test
  (defn delay-line-out [state midi-frame inputs dt]
    (let [
          delay-time (:delay-time inputs)
          ;_ (println delay-time)
          index (long (/ delay-time dt))
         ;_ (println index)
          values (:values state)
          ;_ (println values)
          ;_ (println (count values))
          ]
        (get values index))
      )

  (test/is (=
           (delay-line-out
             {:values (vec (range 1 100))}
             nil
             {:delay-time 1.0}
             0.1
             )
           (+ 10 1)
           )
           )
)

(test/with-test
  (defn delay-line-update-state-after [state midi-frame inputs dt]
    (assoc state :values
                           (into [(:in inputs) ]
                            (pop (:values state))
                            )
                           ))
  (test/is
    (=
      (let [state {:values [2.0 1.0]}
            inputs {:in 3.0}
            ]
        (delay-line-update-state-after state nil inputs nil)
        )
      {:values [3.0 2.0]}
      )
    )
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


