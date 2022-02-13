(ns com.ncpenterprises.clomosy.core
  (:require [com.ncpenterprises.clomosy.io.midi :as midi]
            [clojure.core.async :as async]
            [com.ncpenterprises.clomosy.io.audio :as io]
            [com.ncpenterprises.clomosy.engines.simple :as engine]
            [com.ncpenterprises.clomosy.engines.simplev2 :as engine-v2]
            )
  (:import (javax.sound.sampled AudioFormat SourceDataLine)
           (javax.sound.midi MidiDeviceTransmitter))
  (:gen-class)
  )


(defn add-module [modules module-to-add]
  (assoc modules (:id module-to-add) module-to-add)
  )

(defn run-synth [synth-def sample_rate]
  (println "Synth fn" synth-def)
  (println "Sample rate" sample_rate)
  (let [line (io/getOutputLine sample_rate 8 1)
        _ (println line)
        midi-queue (async/chan 100)
        _ (println midi-queue)
        midi-in (midi/getTransmitter)
        _ (println midi-in)
        ]
    (println line)
    (.open ^SourceDataLine line (AudioFormat. sample_rate 8 1 true true) (/ sample_rate 10))
    (println (.getFormat line))
    (.start line)
    (.setReceiver ^MidiDeviceTransmitter midi-in (midi/queue-receiver midi-queue))
    (println (.getBufferSize line))
    (let [dt  (/ 1.0 sample_rate)
          buffer-size (/ (.getBufferSize line) 10)
          iterations (* 5 sample_rate)
          synth (synth-def line buffer-size dt)
          ]


      (loop [n 0
             state {:modules (:modules synth)
                    :outputs {}
                    }
             order  (:order synth)
             midi-frame (async/poll! midi-queue)
             patches (:patches synth)
             ]

        (let [
              state (engine/evaluate state patches order midi-frame dt)
              state (engine/update-after state patches order midi-frame dt)
              ]
          (if (< n iterations) (recur
                                 (inc n)
                                 state
                                 order
                                 (async/poll! midi-queue)
                                 patches
                                 )
                               )
          )
        )
      )
    (println "draining line")
    (.drain line)
    (println "closing line")
    (.close line)
    (println "closing midi-queue")
    (async/poll! midi-queue)
    (async/close! midi-queue)
    (println "closing midi-in")
    (.close midi-in)
    )
  )

(defn run-synth-v2
  [synth-def frame-rate]
  (println "synth fn" synth-def)
  (println "frame rate" frame-rate)
  (let [dt (/ 1.0 frame-rate)
        synth (synth-def dt)
        modules (:modules synth)
        patches (:patches synth)
        order (:order synth)
        initial-state (engine-v2/initial-state modules)]
    (loop [previous-state initial-state]
      (let [result (engine-v2/evaluate modules previous-state patches order dt)
            updated-state (:state result)
            ]
        (recur updated-state)))))

(defn -main
  [& args]
  (let [configuration (read-string (first args))
        synth-def (requiring-resolve (:synth-def configuration))
        frame-rate (:frame-rate configuration)]
    (when (nil? synth-def) (throw (RuntimeException. (str "No method found for " synth-def))))
    (run-synth synth-def frame-rate)))