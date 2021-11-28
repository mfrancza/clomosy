(ns com.ncpenterprises.clomosy.main
  (:require [com.ncpenterprises.clomosy.io.midi :as midi]
            [clojure.core.async :as async]
            [com.ncpenterprises.clomosy.io.audio :as io]
            [com.ncpenterprises.clomosy.modules.audio :as audio-mod]
            [com.ncpenterprises.clomosy.modules.midi :as midi-mod]
            [com.ncpenterprises.clomosy.modules.oscillator :as osc-mod]
            [com.ncpenterprises.clomosy.modules.amplification :as amp-mod]
            [com.ncpenterprises.clomosy.modules.intonation :as int-mod]
            [com.ncpenterprises.clomosy.modules.memory :as mem-mod]
            [com.ncpenterprises.clomosy.modules.constant :as const-mod]
            [com.ncpenterprises.clomosy.engines.simple :as engine]
            )
  (:import (javax.sound.sampled AudioFormat SourceDataLine)
           (javax.sound.midi MidiDeviceTransmitter)))

(defn add-module [modules module-to-add]
  (assoc modules (:id module-to-add) module-to-add)
  )

(defn test-synth [synth-def sample_rate]
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

        (let [;_ (println order)
              state (engine/evaluate state patches order midi-frame dt)
              state (engine/update-after state patches order midi-frame dt)
              ]
          ;(if (> (- (System/nanoTime) start-time ) (* dt 1E9))
          ;  (println (- (System/nanoTime) start-time ) (* dt 1E9))
          ;  )
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
    ;(println "closing receiver")
    ;(.close (.getReceiver midi-in))

    )
  )