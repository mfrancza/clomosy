(ns clomosy.modules.audio
  (:require [clomosy.engines.simple :as simple-engine]
            [clomosy.io.audio :as audio]))

(defn get-output-line-initial-state-fn
  [audio-format buffer-size]
  (fn []
    (let [line (audio/get-output-line audio-format buffer-size)]
      (audio/open-output-line line audio-format buffer-size)
      (audio/start-output-line line)
      {:encoding-fn (audio/get-encoding-fn (audio/get-line-format line))
       :line line})))

(defn mono-output-line-update-fn
  [inputs state]
  (let [line (:line state)
        encoding-fn (:encoding-fn state)
        output (encoding-fn (:output inputs))]
    (audio/write-frame line output)
    {:state state}))

(defn mono-output-line [audio-format buffer-size]
  "returns a Module that sends the output value to a mono audio line"
  (simple-engine/map->Module {:initial-state-fn (get-output-line-initial-state-fn (audio/map->AudioFormat audio-format) buffer-size)
                                 :update-fn        mono-output-line-update-fn
                                 :input-names      [:output]}))