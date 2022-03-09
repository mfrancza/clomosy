(ns com.ncpenterprises.clomosy.modules.v2.audio
  (:require [com.ncpenterprises.clomosy.engines.simplev2 :as simple-v2-engine]
            [com.ncpenterprises.clomosy.io.audio :as audio]))

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
  (simple-v2-engine/map->Module {:initial-state-fn (get-output-line-initial-state-fn (audio/map->AudioFormat audio-format) buffer-size)
                                 :update-fn        mono-output-line-update-fn
                                 :input-names      [:output]}))