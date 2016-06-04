(ns com.ncpenterprises.clomosy.modules.midi
  (:require [com.ncpenterprises.clomosy.io.audio :as io]
            [clojure.core.async :as async]
            [com.ncpenterprises.clomosy.io.midi :as midi]
            [com.ncpenterprises.clomosy.modules.audio :as audio-mod]
            )
  (:import (javax.sound.sampled SourceDataLine AudioFormat)))

  (defn monophonic-keyboard [id]
    {

     :id id

     :outputs {
               :gate    (fn [state midi-frame inputs dt]
                          (if (empty? (:notes-on state)) 0 1))
               :note    (fn [state midi-frame inputs dt]
                          (first (:notes-on state)))
               :trigger (fn [state midi-frame inputs dt]
                          (not (nil? midi-frame)))
               }

     :state   {
               :notes-on ()
               }


     :update (fn [state midi-frame inputs dt]
               (midi/apply-message state midi-frame))
     }
    )

(defn test-keyboard-state []
  (let [keyboard (monophonic-keyboard :test)]
    (-> keyboard
        (:state )
        ((:update keyboard) 1)
        ((:update keyboard) 2)
        ((:update keyboard) 1)
        ((:update keyboard) 1)
        )
    ))


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
          iterations 800000
          ]
      (loop [;buffer []
             n 0
             freq 80
             midi-module (monophonic-keyboard :keyboard)
             output-module (audio-mod/mono-output :output line buffer-size )
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



