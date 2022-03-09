(ns com.ncpenterprises.clomosy.io.audio
  "functions for interacting with audio IO via the javax.sound.sampled interface"
  (:import (javax.sound.sampled AudioSystem DataLine$Info AudioFormat SourceDataLine AudioFormat$Encoding DataLine)))

(def not-specified
  (. AudioSystem NOT_SPECIFIED))

(def pcm-unsigned
  (. AudioFormat$Encoding PCM_UNSIGNED))

(def pcm-signed
  (. AudioFormat$Encoding PCM_SIGNED))

(defn ^AudioFormat map->AudioFormat
  "creates an AudioFormat based on the provided values"
  [audio-format-map]
  (let [get-or-not-specified #(get audio-format-map % not-specified)
        encoding (get audio-format-map :encoding pcm-signed)
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

(defn get-output-line
  "returns a SourceDataLine matching the criteria supplied for AudioFormat and buffer-size"
  (^SourceDataLine [audio-format]
  (get-output-line audio-format not-specified))
  (^SourceDataLine [audio-format buffer-size]
   (AudioSystem/getLine (DataLine$Info. SourceDataLine audio-format buffer-size))))

(defn get-encoding-fn
  "returns a function which takes a double and outputs a byte array corresponding to the encoded representation"
  [audio-format]
  (cond
    (= (.getEncoding audio-format) pcm-signed)  (fn [^double input]
                                                     (byte-array [(* input 127.0)]))
    :else (throw (new IllegalArgumentException (str "No encoding function found for " audio-format)))))

(defn write-frame [^SourceDataLine line frame]
  "writes the provided byte array containing a frame to the line"
  (.write line frame 0 (alength frame)))

(defn open-output-line [^SourceDataLine line ^AudioFormat audio-format buffer-size]
  "Opens the line with the specified format and buffer size"
  (.open line audio-format buffer-size))

(defn start-output-line [^SourceDataLine line]
  "Starts an open line"
  (.start line))

(defn get-line-format [^DataLine line]
  "Returns the AudioFormat describing the line"
  (.getFormat line))
