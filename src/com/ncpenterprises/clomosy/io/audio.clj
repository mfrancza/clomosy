(ns com.ncpenterprises.clomosy.io.audio
  "functions for interacting with audio IO via the javax.sound.sampled interface"
  (:import (javax.sound.sampled AudioSystem DataLine$Info AudioFormat SourceDataLine AudioFormat$Encoding)))

(def not-specified
  (. AudioSystem NOT_SPECIFIED))

(defn ^AudioFormat map->AudioFormat
  "creates an AudioFormat based on the provided values"
  [audio-format-map]
  (let [get-or-not-specified #(get audio-format-map % not-specified)
        encoding (get audio-format-map :encoding (. AudioFormat$Encoding PCM_UNSIGNED))
        sample-rate (get-or-not-specified :sample-rate)
        sample-size-in-bits (get-or-not-specified :sample-size-in-bits)
        channels (get-or-not-specified :channels)
        frame-size (get audio-format-map
                        :frame-size
                        (if (or (= channels not-specified) (= sample-size-in-bits not-specified))
                          not-specified
                          (let [sample-size (int (/ (+ sample-size-in-bits 7) 8))]
                            (* sample-size channels))))
        frame-rate (get audio-format-map :frame-rate sample-rate)
        big-endian (get audio-format-map :big-endian false)
        properties (get audio-format-map :properties {})]
    (AudioFormat. encoding sample-rate sample-size-in-bits channels frame-size frame-rate big-endian properties)))


(defn get-output-line [sample-rate sample-size channels]
  (AudioSystem/getLine (DataLine$Info. SourceDataLine
                                       (map->AudioFormat {:sample-rate sample-rate
                                                          :sample-size-in-bits sample-size
                                                          :channels channels}))))

(defn write-buffer [^SourceDataLine line buffer buffer-size]
  (.write line (byte-array buffer) 0 buffer-size))

(defn output-frame [^SourceDataLine line buffer buffer-size frame]
  (if (< (count buffer) buffer-size)
    (conj buffer frame)
    (let [_ (write-buffer line buffer buffer-size)]
      (vector frame))))



