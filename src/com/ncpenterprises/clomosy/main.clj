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
            )
  (:import (javax.sound.sampled AudioFormat SourceDataLine)))


(defn update-module [module midi-frame inputs dt]
  (assoc module :state
                ((:update module)
                  (:state module)
                  midi-frame
                  inputs
                  dt))
  )

(defn get-outputs [module midi-frame inputs dt]
  (reduce
    (fn [outputs key]
      (assoc outputs [(:id module) key] ((get (:outputs module) key) (:state module) midi-frame inputs dt)))
    {}
    (keys (:outputs module)))
  )

(defn get-inputs [module patches outputs]
  (reduce
    (fn [inputs key]
      ;(println patches)
      ;(println [(:id module) key])

      (assoc inputs key (get outputs (get patches [(:id module) key]))))
    {}
    (:inputs module)
    )
  )

(defn test-osc-module []
  (let [line (io/getOutputLine 44100 8 1)
        _ (println line)
        midi-queue (async/chan 100)
        _ (println midi-queue)
        midi-in (midi/getTransmitter)
        _ (println midi-in)
        ]
    (println line)
    (.open ^SourceDataLine line (AudioFormat. 44100 8 1 true true) 4410)
    (println (.getFormat line))
    (.start line)
    (.setReceiver midi-in (midi/queue-receiver midi-queue))
    (println (.getBufferSize line))
    (let [dt (/ 1 44100)
          buffer-size (/ (.getBufferSize line) 1)
          iterations 400000
          ]
      (loop [n 0
             midi-module (midi-mod/monophonic-keyboard :keyboard)
             intonation-module (int-mod/twelve-tone-equal-temperment :intonation)
             osc-module (osc-mod/sine-wave :oscillator)
             memory-module (mem-mod :phase)
             amp-module (amp-mod/linear-amplifier :amp)
             output-module (audio-mod/mono-output :output line buffer-size)
             midi-frame (async/poll! midi-queue)
             phase 0
             patches {
                      [:intonation :note] [:keyboard :note]
                      [:oscillator :frequency] [:intonation :frequency]
                      [:oscillator :phase] [:phase :phase]
                      [:amp :in] [:oscillator :amplitude]
                      [:amp :gain] [:keyboard :gate]
                      [:output :audio] [:amp :out]
                      }
             ]
        (let [
              outputs {}
              inputs (get-inputs midi-module patches outputs)
              midi-module (update-module midi-module midi-frame inputs dt)
              outputs (merge outputs (get-outputs midi-module midi-frame inputs dt))
              ;_ (println outputs)

              inputs (get-inputs intonation-module patches outputs)
              ;_ (println inputs)
              intonation-module (update-module intonation-module midi-frame inputs dt)
              outputs (merge outputs (get-outputs intonation-module midi-frame inputs dt))

              inputs (get-inputs memory-module patches outputs)
              outputs (merge outputs (get-outputs memory-module midi-frame inputs dt))

              ;_ (println outputs)
              inputs (get-inputs osc-module patches outputs)
              inputs (assoc inputs :phase phase)
              ;_ (println inputs)
              osc-module (update-module osc-module midi-frame inputs dt)
              outputs (merge outputs (get-outputs osc-module midi-frame inputs dt))

              inputs (get-inputs amp-module patches outputs)
              ;inputs (assoc inputs :phase phase)
              ;_ (println inputs)
              amp-module (update-module amp-module midi-frame inputs dt)
              outputs (merge outputs (get-outputs amp-module midi-frame inputs dt))

              inputs (get-inputs amp-module patches outputs)
              ;inputs (assoc inputs :phase phase)
              ;_ (println inputs)
              amp-module (update-module amp-module midi-frame inputs dt)
              outputs (merge outputs (get-outputs amp-module midi-frame inputs dt))
              ]
          (if (< n iterations) (recur
                                 ;(io/output-frame line buffer buffer-size amplitude)
                                 (inc n)
                                 midi-module
                                 intonation-module
                                 osc-module
                                 amp-module
                                 output-module
                                 (async/poll! midi-queue)
                                 ((:phase (:outputs osc-module)) (:state osc-module) midi-frame {:phase phase :frequency freq} dt)
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

