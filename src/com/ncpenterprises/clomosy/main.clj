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


(defn update-module [module midi-frame inputs dt]
  (if (nil? (:update module))
    module
    (assoc module :state
                ((:update module)
                  (:state module)
                  midi-frame
                  inputs
                  dt))
    )

  )

(defn update-module-after [module midi-frame inputs dt]
  (if (nil? (:update-after module))
    module
    (assoc module :state
                  ((:update-after module)
                    (:state module)
                    midi-frame
                    inputs
                    dt))
    )
  )

(defn get-outputs [outputs module midi-frame inputs dt]
  (reduce
    (fn [outputs key]
      (assoc outputs [(:id module) key] ((get (:outputs module) key) (:state module) midi-frame inputs dt)))
    outputs
    (keys (:outputs module)))
  )

(defn get-patch [patches module_id key]
  (get patches [module_id key])
  )

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
      )
    )
  )

(defn test-osc-module [sample_rate]
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
    (.setReceiver midi-in (midi/queue-receiver midi-queue))
    (println (.getBufferSize line))
    (let [dt  (/ 1.0 sample_rate)
          buffer-size (/ (.getBufferSize line) 10)
          iterations (* 5 sample_rate)
          ]
      (loop [n 0
             midi-module (midi-mod/monophonic-keyboard :keyboard)
             intonation-module (int-mod/twelve-tone-equal-temperment :intonation)
             osc-module (osc-mod/sine-wave :oscillator)
             memory-module (mem-mod/memory-cell :phase 0)
             amp-module (amp-mod/linear-amplifier :amp)
             output-module (audio-mod/mono-output :output line buffer-size)
             midi-frame (async/poll! midi-queue)
             patches {
                      [:intonation :note] [:keyboard :note]
                      [:oscillator :frequency] [:intonation :frequency]
                      [:oscillator :phase] [:phase :out]
                      [:phase :in] [:oscillator :phase]
                      [:amp :in] [:oscillator :amplitude]
                      [:amp :gain] [:keyboard :gate]
                      [:output :audio] [:amp :out]
                      }
             ]
        (let [
              ;_ (println "-")
              ;_ (println (System/currentTimeMillis))
              outputs {}
              inputs (get-inputs midi-module patches outputs)
              midi-module (update-module midi-module midi-frame inputs dt)
              outputs (get-outputs outputs midi-module midi-frame inputs dt)
              ;_ (println outputs)

              inputs (get-inputs intonation-module patches outputs)
              ;_ (println inputs)
              intonation-module (update-module intonation-module midi-frame inputs dt)
              outputs (get-outputs outputs intonation-module midi-frame inputs dt)

              inputs (get-inputs memory-module patches outputs)
              outputs (get-outputs outputs memory-module midi-frame inputs dt)

              ;_ (println outputs)
              inputs (get-inputs osc-module patches outputs)
              ;inputs (assoc inputs :phase phase)
              ;_ (println inputs)
              osc-module (update-module osc-module midi-frame inputs dt)
              outputs (get-outputs outputs osc-module midi-frame inputs dt)

              inputs (get-inputs amp-module patches outputs)
              ;inputs (assoc inputs :phase phase)
              ;_ (println inputs)
              amp-module (update-module amp-module midi-frame inputs dt)
              outputs (get-outputs outputs amp-module midi-frame inputs dt)

              inputs (get-inputs output-module patches outputs)
              ;inputs (assoc inputs :phase phase)
              ;_ (println inputs)
              output-module (update-module output-module midi-frame inputs dt)
              outputs (get-outputs outputs output-module midi-frame inputs dt)

              inputs (get-inputs memory-module patches outputs)
              memory-module (update-module-after memory-module midi-frame inputs dt)
              ;_ (println (System/currentTimeMillis))
              ]

          ;(if (> (- (System/nanoTime) start-time ) (* dt 1E9))
          ;  (println (- (System/nanoTime) start-time ) (* dt 1E9))
          ;  )
          (if (< n iterations) (recur
                                 (inc n)
                                 midi-module
                                 intonation-module
                                 osc-module
                                 memory-module
                                 amp-module
                                 output-module
                                 (async/poll! midi-queue)
                                 patches
                                 )

                               (println midi-module intonation-module osc-module memory-module
                                        amp-module output-module)
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

(defn add-module [modules module-to-add]
  (assoc modules (:id module-to-add) module-to-add)
  )


(defn test-modules [sample_rate]
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
    (.setReceiver midi-in (midi/queue-receiver midi-queue))
    (println (.getBufferSize line))
    (let [dt  (/ 1.0 sample_rate)
          buffer-size (/ (.getBufferSize line) 10)
          iterations (* 5 sample_rate)

          ]


      (loop [n 0
             state {:modules (-> {}
                                   (add-module (midi-mod/monophonic-keyboard :keyboard))
                                   (add-module (int-mod/twelve-tone-equal-temperment :intonation))
                                   (add-module (osc-mod/triangle-wave :oscillator))
                                   (add-module (mem-mod/memory-cell :phase 0))
                                   (add-module (amp-mod/linear-amplifier :amp))
                                   (add-module (audio-mod/mono-output :output line buffer-size))
                         )
                      :outputs {}
                      }
             order  [:keyboard
                     :intonation
                     :phase
                     :oscillator
                     :amp
                     :output]
             midi-frame (async/poll! midi-queue)
             patches {
                      [:intonation :note] [:keyboard :note]
                      [:oscillator :frequency] [:intonation :frequency]
                      [:oscillator :phase] [:phase :out]
                      [:phase :in] [:oscillator :phase]
                      [:amp :in] [:oscillator :amplitude]
                      [:amp :gain] [:keyboard :gate]
                      [:output :audio] [:amp :out]
                      }
             ]

        (let [;_ (println state)
              state (reduce (fn [state module_id]
                              (let [
                                    modules (:modules state)
                                    module (module_id (:modules state))
                                    inputs (get-inputs module patches (:outputs state))
                                    ;_ (println module_id)
                                    ;_ (println modules)
                                    module (update-module module midi-frame inputs dt)
                                    ;_ (println (:outputs state))
                                    ;_ (println inputs)
                                    outputs (get-outputs (:outputs state) module midi-frame inputs dt)
                                    modules (assoc (:modules state) module_id module)
                                    ]
                                (-> state
                                    (assoc :modules modules)
                                    (assoc :outputs outputs)
                                    )
                                ))
                            state
                            order
                            )
              state (reduce (fn [state module_id]
                              (let [module (module_id (:modules state))
                                    inputs (get-inputs module patches (:outputs state))
                                    module (update-module-after module midi-frame inputs dt)
                                    modules (assoc (:modules state) module_id module)
                                    ]
                                    (assoc state :modules modules)
                                    )
                                )
                            state
                            order
                            )
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

(defn test-protocol [synth-def sample_rate]
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
    (.setReceiver midi-in (midi/queue-receiver midi-queue))
    (println (.getBufferSize line))
    (let [dt  (/ 1.0 sample_rate)
          buffer-size (/ (.getBufferSize line) 10)
          iterations (* 5 sample_rate)
          synth (synth-def line buffer-size)
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
              state (reduce (fn [state module_id]
                              (let [
                                    modules (:modules state)
                                    module (module_id (:modules state))
                                    inputs (get-inputs module patches (:outputs state))
                                    ;_ (println module_id)
                                    ;_ (println modules)
                                    module (update-module module midi-frame inputs dt)
                                    ;_ (println (:outputs state))
                                    ;_ (println inputs)
                                    outputs (get-outputs (:outputs state) module midi-frame inputs dt)
                                    modules (assoc (:modules state) module_id module)
                                    ]
                                (-> state
                                    (assoc :modules modules)
                                    (assoc :outputs outputs)
                                    )
                                ))
                            state
                            order
                            )
              state (reduce (fn [state module_id]
                              (let [module (module_id (:modules state))
                                    inputs (get-inputs module patches (:outputs state))
                                    module (update-module-after module midi-frame inputs dt)
                                    modules (assoc (:modules state) module_id module)
                                    ]
                                (assoc state :modules modules)
                                )
                              )
                            state
                            order
                            )
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