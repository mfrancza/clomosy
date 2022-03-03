(ns com.ncpenterprises.clomosy.engines.simplev2)

(defrecord Module
  [update-fn
   initial-state-fn
   input-names
   output-names])

(defn get-patch
  "Gets the output patched to the input as [module-id output]"
  [patches module-id input]
  (get patches [module-id input]))

(defn get-inputs
  "Gets the input values for a module from the outputs of the other modules based on the patches; if no patch exists for the input, 0.0 is used as the value"
  [module-id inputs patches outputs]
  (reduce
    (fn [inputs input]
      (let [module-output (get-patch patches module-id input)
            output (if (not (nil? module-output))
                     (get-in outputs module-output)
                     0.0)]
        (assoc inputs input output)))
    {}
    (seq inputs)))

(defn evaluate-module
  "Evaluates a module in the synthesizer and returns its outputs and new state"
  [module-id module patches outputs state]
  (let [inputs (get-inputs module-id (:input-names module) patches outputs)
        update-fn (:update-fn module)]
    (update-fn inputs (module-id state))))

(defn initial-state [modules]
  "Initializes the state map based on the :initial-state-fn values of each module"
  (reduce (fn [state [module-id module]]
            (let [initial-state-fn (:initial-state-fn module)]
              (if (not (nil? initial-state-fn))
                (assoc state module-id (initial-state-fn))
                state)))
          {}
          modules))

(defn evaluate [modules previous-state patches order]
  "Evaluates a frame of the synthesizer.
  modules is a map of module-id to Module.
  previous-state is the value of :state from a previous output or nil
  patches is a map of [module-id input] to [module-id and output]
  order is a vector of module-ids in the order they should be evaluated
  midi-frame is the midi event to evaluate in this frame or nil if one has not occurred
  dt is the duration of the frame (1/sample rate)"
  (reduce (fn [frame module-id]
            (let [state (:state frame)
                  outputs (:outputs frame)
                  module (module-id modules)
                  module-update (evaluate-module module-id module patches outputs state)
                  module-state (:state module-update)
                  module-output (:outputs module-update)
                  state (if (not (nil? module-state))
                          (assoc state module-id module-state)
                          state)
                  outputs (if (not (nil? module-output))
                            (assoc outputs module-id module-output)
                            outputs)]
              {:state state
               :outputs outputs}))
          {:state previous-state
           :outputs {}}
          order))