(ns com.ncpenterprises.clomosy.oscillators
  (:require [com.ncpenterprises.clomosy.io.audio :as io])
  (:import (javax.sound.sampled AudioFormat))
  (:import (javax.sound.sampled AudioSystem DataLine$Info AudioFormat SourceDataLine)))


(defn pulse-wave [phase duty-cycle]
  (if (< phase (* 2 Math/PI duty-cycle)) 1 -1)
  )

(defn triangle-wave [phase]
  (let [ratio (/ phase 2 Math/PI)]
    (if (< ratio 0.5)
      (if (< ratio 0.25)
        (* ratio 4)
        (* (- ratio 0.5) -4))
      (if (< ratio 0.75)
        (* (- ratio 0.5) -4)
        (* (- ratio 1) 4)
        )
      )
    )
  )

(defn sine-wave [phase]
  (Math/sin phase)
  )

(defn testwave [freq waveform]
  (let [line (io/get-output-line 44100 8 1)]
    (println line)
    (.open ^SourceDataLine line (AudioFormat. 44100 8 1 true true))
    (println (.getFormat line))
    (.start line)
    (println (.getBufferSize line))
    (let [dt (/ 1 44100)
          buffer-size (/ (.getBufferSize line) 10)
          iterations 104100
          ]
      (loop [buffer [] n 0]
        (let [phase (rem (* n freq dt 2 Math/PI) (* 2 Math/PI))
              amplitude (* 100 (waveform phase))
              ]
          ;;(println "n=" n "phase=" phase " amplitude=" amplitude)
          (if (< n iterations) (recur (io/output-frame line buffer buffer-size amplitude) (inc n))
                               )
          )
        )
      )
    (.drain line)
    (.close line)
    )
  )