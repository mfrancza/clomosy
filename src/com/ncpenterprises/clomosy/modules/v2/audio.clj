(ns com.ncpenterprises.clomosy.modules.v2.audio
  (:require [com.ncpenterprises.clomosy.engines.simplev2 :as simple-v2-engine]
            [com.ncpenterprises.clomosy.io.audio :as audio])
  (:import (javax.sound.sampled SourceDataLine AudioFormat)))

(defn get-mono-output-initial-state-fn
  [sample-rate]
  (fn []
    (let [line (audio/get-output-line sample-rate 8 1)
          _ (.open ^SourceDataLine line (AudioFormat. sample-rate 8 1 true true) (/ sample-rate 10))
          _ (.start line)
          buffer-size (/ (.getBufferSize line) 10)
          buffer []]
      {:line line
       :buffer buffer
       :buffer-size buffer-size})))

(defn mono-output-update-fn
  [inputs state]
  (let [line (:line state)
        buffer (:buffer state)
        buffer-size (:buffer-size state)
        output (* 126 (:output inputs))]
    {:state (assoc state :buffer (audio/output-frame line buffer buffer-size output))}))

(defn mono-output [frame-rate]
  "returns a Module that sends the output value to a mono audio line"
  (simple-v2-engine/map->Module {:initial-state-fn (get-mono-output-initial-state-fn frame-rate)
                                 :update-fn        mono-output-update-fn
                                 :input-names      [:output]}))