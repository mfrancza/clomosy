(ns com.ncpenterprises.clomosy.main
  (:require [com.ncpenterprises.clomosy.io.midi :as midi]
            [clojure.core.async :as async]
            [com.ncpenterprises.clomosy.io.audio :as io]
            [com.ncpenterprises.clomosy.modules.audio :as audio-mod]
            [com.ncpenterprises.clomosy.modules.midi :as midi-mod]
            [com.ncpenterprises.clomosy.modules.oscillator :as osc-mod]
            [com.ncpenterprises.clomosy.modules.amplification :as amp-mod]
            )
  (:import (javax.sound.sampled AudioFormat SourceDataLine)))

(defn test-midi-module [waveform]
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
          iterations 80000
          ]
      (loop [;buffer []
             n 0
             freq 80
             midi-module (midi-mod/monophonic-keyboard :keyboard)
             output-module (audio-mod/mono-output :output line buffer-size)
             ]


        (let [midi-frame (async/poll! midi-queue)
              phase (rem (* n freq dt 2 Math/PI) (* 2 Math/PI))
              amplitude (* 100 (waveform phase)
                           (if (== 0
                                   ((:gate (:outputs midi-module))
                                     (:state midi-module)
                                     midi-frame
                                     {}
                                     dt
                                     )
                                   )
                             0 1)
                           )
              ]


          (if (< n iterations) (recur
                                 ;(io/output-frame line buffer buffer-size amplitude)
                                 (inc n)
                                 (if-not (empty? (:notes-on (:state midi-module)))
                                   (* 440
                                      (Math/pow 2
                                                (/ (-
                                                     ((:note (:outputs midi-module))
                                                       (:state midi-module)
                                                       midi-frame
                                                       {}
                                                       dt
                                                       )
                                                     69)
                                                   12)))
                                   freq
                                   )
                                 (assoc midi-module :state
                                                    ((:update midi-module) (:state midi-module) midi-frame {} dt))
                                 (assoc output-module :state
                                                      ((:update output-module) (:state output-module) midi-frame {:audio amplitude} dt))
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
          iterations 80000
          ]
      (loop [n 0
             freq 80
             midi-module (midi-mod/monophonic-keyboard :keyboard)
             output-module (audio-mod/mono-output :output line buffer-size)
             osc-module (osc-mod/sine-wave :oscillator)
             amp-module (amp-mod/linear-amplifier :amp)
             midi-frame (async/poll! midi-queue)
             phase 0
             amplitude 0
             ]



          (if (< n iterations) (recur
                                 ;(io/output-frame line buffer buffer-size amplitude)
                                 (inc n)
                                 (if-not (empty? (:notes-on (:state midi-module)))
                                   (* 440
                                      (Math/pow 2
                                                (/ (-
                                                     ((:note (:outputs midi-module))
                                                       (:state midi-module)
                                                       midi-frame
                                                       {}
                                                       dt
                                                       )
                                                     69)
                                                   12)))
                                   freq
                                   )
                                 (assoc midi-module :state
                                                    ((:update midi-module) (:state midi-module) midi-frame {} dt))
                                 (assoc output-module :state
                                                      ((:update output-module)
                                                        (:state output-module)
                                                        midi-frame
                                                        {:audio amplitude}
                                                        dt))
                                 (assoc osc-module :state
                                                   ((:update osc-module) (:state osc-module) midi-frame {} dt))
                                 (assoc amp-module :state
                                                   ((:update amp-module) (:state amp-module) midi-frame {} dt))
                                 (async/poll! midi-queue)
                                 ((:phase (:outputs osc-module)) (:state osc-module) midi-frame {:phase phase :frequency freq} dt)
                                 ((:out (:outputs amp-module))
                                   ()
                                   midi-frame
                                   {
                                    :in ((:amplitude (:outputs osc-module))
                                          (:state osc-module)
                                          midi-frame
                                          {:phase phase :frequency freq}
                                          dt)
                                    :gain ((:gate (:outputs midi-module))
                                            (:state midi-module)
                                            midi-frame
                                            {}
                                            dt
                                            )
                                    }
                                   dt)
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