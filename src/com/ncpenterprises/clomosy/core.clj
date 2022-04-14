(ns com.ncpenterprises.clomosy.core
  (:require [com.ncpenterprises.clomosy.engines.simplev2 :as engine-v2])
  (:gen-class))


(defn add-module [modules module-to-add]
  (assoc modules (:id module-to-add) module-to-add)
  )

(defn run-synth-v2
  [synth-def frame-rate]
  (println "synth fn" synth-def)
  (println "frame rate" frame-rate)
  (let [synth (synth-def frame-rate)
        modules (:modules synth)
        patches (:patches synth)
        order (:order synth)
        initial-state (engine-v2/initial-state modules)]
    (loop [previous-state initial-state]
      (let [result (engine-v2/evaluate modules previous-state patches order)
            updated-state (:state result)]
        (recur updated-state)))))

(defn -main
  [& args]
  (let [configuration (read-string (first args))
        synth-def (requiring-resolve (:synth-def configuration))
        frame-rate (:frame-rate configuration)]
    (when (nil? synth-def) (throw (RuntimeException. (str "No method found for " synth-def))))
    (run-synth-v2 synth-def frame-rate)))