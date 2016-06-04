(ns com.ncpenterprises.clomosy.io.audio
  (:import (javax.sound.sampled AudioSystem DataLine$Info AudioFormat SourceDataLine)))

(defn getOutputLine [sample-rate sample-size channels]
  (AudioSystem/getLine (DataLine$Info. SourceDataLine
                                       (AudioFormat. sample-rate sample-size channels true true)
                                       ))
  )

    (defn output-frame [^SourceDataLine line buffer buffer-size frame]
      ;(println line)
      (if (< (count buffer) buffer-size)
        (conj buffer frame)
        (let [_ (.write line (byte-array buffer) 0 buffer-size)]
          (vector frame)
          ))
      )


