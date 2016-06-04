(ns com.ncpenterprises.clomosy.io.midi
  (:require [com.ncpenterprises.clomosy.io.audio :as io]
            [clojure.core.async :as async])
  (:import (javax.sound.midi MidiSystem Receiver ShortMessage)
           (javax.sound.sampled SourceDataLine AudioFormat)))

(defn getTransmitter []
  (MidiSystem/getTransmitter)
  )

(defn print-receiver []
  (reify Receiver
          (send [this message time-stamp]
             (println message time-stamp)
            )
          )
  )


(defn queue-receiver-old [queue]
  (reify Receiver
    (send [this message time-stamp]
      (swap! queue conj message)
      )

    )
  )

(defn test-midi-old [waveform]
  (let [line (io/getOutputLine 44100 8 1)
        midi-queue (atom ())
        ]
    (println line)
    (.open ^SourceDataLine line (AudioFormat. 44100 8 1 true true) 441)
    (println (.getFormat line))
    (.start line)
    (.setReceiver (getTransmitter) (queue-receiver-old midi-queue))
    (println (.getBufferSize line))
    (let [dt (/ 1 44100)
          buffer-size (/ (.getBufferSize line) 1)
          iterations 204100
          ]
      (loop [buffer []
             n 0
             freq 80
             ]
        (let [phase (rem (* n freq dt 2 Math/PI) (* 2 Math/PI))
              amplitude (* 100 (waveform phase))
              ]
          ;;(println "n=" n "phase=" phase " amplitude=" amplitude)
          (if (< n iterations) (recur
                                 (io/output-frame line buffer buffer-size amplitude)
                                 (inc n)
                                 (let [last-message (first (deref midi-queue))]
                                   (if last-message
                                     (* 440 (Math/pow 2 (/ (- (.getData1 last-message) 69) 12)))
                                     freq
                                     )

                                   )

                                 )
                               )
          )
        )
      )
    (.drain line)
    (.close line)
    )
  )

(defn queue-receiver [queue]
  (reify Receiver
    (send [this message time-stamp]
      (println "waiting to send")
      (async/>!! queue message)
      (println "done sending")
      )

    )
  )


(defmulti apply-message
         (fn [midi-state message] (class message) ))

(defmethod apply-message :default [midi-state message]
  midi-state
  )

(defmethod apply-message ShortMessage [midi-state message]
  (println midi-state)
  (if (= ShortMessage/NOTE_ON (.getCommand message))
    (assoc midi-state :notes-on (conj (:notes-on midi-state) (.getData1 message)))
    (if (= ShortMessage/NOTE_OFF (.getCommand message))
      (assoc midi-state :notes-on (remove #(= % (.getData1 message)) (:notes-on midi-state)))
      midi-state
      )
    )

  )



(defn test-midi [waveform]
  (let [line (io/getOutputLine 44100 8 1)
        _ (println line)
        midi-queue (async/chan 100)
        _ (println midi-queue)
        midi-in (getTransmitter)
        _ (println midi-in)
        ]
    (println line)
    (.open ^SourceDataLine line (AudioFormat. 44100 8 1 true true) 4410)
    (println (.getFormat line))
    (.start line)
    (.setReceiver midi-in (queue-receiver midi-queue))
    (println (.getBufferSize line))
    (let [dt (/ 1 44100)
          buffer-size (/ (.getBufferSize line) 1)
          iterations 204100
          ]
      (loop [buffer []
             n 0
             freq 80
             old-midi-state {
                             :notes-on #{}
                             }
             ]


        (let [phase (rem (* n freq dt 2 Math/PI) (* 2 Math/PI))
              amplitude (* 100 (waveform phase) (if (empty? (:notes-on old-midi-state)) 0 1))
              midi-state (apply-message old-midi-state (async/poll! midi-queue))

              ]
          ;;(println "n=" n "phase=" phase " amplitude=" amplitude)
          (if (< n iterations) (recur
                                 (io/output-frame line buffer buffer-size amplitude)
                                 (inc n)
                                 (if-not (empty? (:notes-on midi-state))
                                    (* 440 (Math/pow 2 (/ (- (first (:notes-on midi-state)) 69) 12)))
                                     freq
                                     )
                                 midi-state
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
    (async/close! midi-queue)
    (println "closing midi-in")
    (.close midi-in)
    ;(println "closing receiver")
    ;(.close (.getReceiver midi-in))

    )
  )