(ns com.ncpenterprises.clomosy.engines.simple)

(defn ^:private update-module [module midi-frame inputs dt]
  "runs the update function for a module and returns the new state"
  (if (nil? (:update module))
    module
    (assoc module :state
                  ((:update module)
                    (:state module)
                    midi-frame
                    inputs
                    dt))))

(defn ^:private update-module-after
  "runs the update-after function for a module and returns the new state"
  [module midi-frame inputs dt]
  (if (nil? (:update-after module))
    module
    (assoc module :state
                  ((:update-after module)
                    (:state module)
                    midi-frame
                    inputs
                    dt))))


(defn ^:private get-outputs
  "Gets the output values for the module for a frame "
  [outputs module midi-frame inputs dt]
  (reduce
    (fn [outputs key]
      (assoc outputs [(:id module) key] ((get (:outputs module) key) (:state module) midi-frame inputs dt)))
    outputs
    (keys (:outputs module))))

(defn ^:private get-patch
  "Gets a patch "
  [patches module_id key]
  (get patches [module_id key]))

(defn get-inputs [module patches outputs]
  (let [module_id (:id module)]
    (reduce
      (fn [inputs key]
        ;(println patches)
        ;(println key)
        ;(println [module_id key])
        (let [output (get-patch patches module_id key)]
          ;(println output)
          (assoc inputs key (get outputs output))
          )
        )
      {}
      (seq (:inputs module))
      )))

(defn evaluate [state
                patches
                order
                midi-frame
                dt]
    (reduce (fn [state module_id]
            (let [
                  modules (:modules state)
                  module (module_id (:modules state))
                  inputs (get-inputs module patches (:outputs state))
                  module (update-module module midi-frame inputs dt)
                  outputs (get-outputs (:outputs state) module midi-frame inputs dt)
                  modules (assoc (:modules state) module_id module)
                  ]
              (-> state
                  (assoc :modules modules)
                  (assoc :outputs outputs)
                  )
              ))
          state
          order))

(defn update-after [state
                    patches
                    order
                    midi-frame
                    dt]
  (reduce (fn [state module_id]
            (let [module (module_id (:modules state))
                  inputs (get-inputs module patches (:outputs state))
                  module (update-module-after module midi-frame inputs dt)
                  modules (assoc (:modules state) module_id module)
                  ]
              (assoc state :modules modules)
              )
            )
          state
          order))

