(ns com.ncpenterprises.clomosy.io.midi
  (:require [com.ncpenterprises.clomosy.io.audio :as io]
            [clojure.core.async :as async])
  (:import (javax.sound.midi MidiSystem Receiver ShortMessage)))

(defn getTransmitter []
  (MidiSystem/getTransmitter)
  )

(defn print-receiver []
  (reify Receiver
          (send [this message time-stamp]
             (println message time-stamp))))

(defn queue-receiver [queue]
  (reify Receiver
    (send [this message time-stamp]
      (async/>!! queue message))))


(defmulti apply-message
         (fn [midi-state message] (class message) ))

(defmethod apply-message :default [midi-state message]
  midi-state)

(defmethod apply-message ShortMessage [midi-state message]
  (println midi-state)
  (if (= ShortMessage/NOTE_ON (.getCommand message))
    (assoc midi-state :notes-on (conj (:notes-on midi-state) (.getData1 message)))
    (if (= ShortMessage/NOTE_OFF (.getCommand message))
      (assoc midi-state :notes-on (remove #(= % (.getData1 message)) (:notes-on midi-state)))
      midi-state)))