(ns clomosy.modules.midi
  (:require [clomosy.engines.simple :as simple-engine]
            [clojure.core.async :as async]
            [clomosy.io.midi :as midi]
            [clomosy.io.midi.messages :as midi-messages])
  (:import (javax.sound.midi Transmitter)))

(defn update-notes-on
  "Updates the notes-on map of the note number to the note event with the midi-message"
  [notes-on midi-message]
  (cond
    (nil? midi-message) notes-on
    (midi-messages/note-on? midi-message) (if (= (midi-messages/velocity midi-message) 0)
                                            (dissoc notes-on (midi-messages/note-number midi-message))
                                            (assoc notes-on (midi-messages/note-number midi-message) midi-message))
    (midi-messages/note-off? midi-message) (dissoc notes-on (midi-messages/note-number midi-message))))

(defn gate [notes-on]
  (if (empty? notes-on) 0.0 1.0))

(defn note [notes-on]
  (if (empty? notes-on)
    0.0
    (apply min (keys notes-on))))

(defn trigger [midi-message]
  (if (and (not (nil? midi-message)) (midi-messages/note-on? midi-message)) 1.0 0.0))

(defn initial-state-fn []
  (let [midi-channel (async/chan 10)
        transmitter (midi/midi-transmitter)
        receiver (midi/channel-receiver midi-channel)]
    (.setReceiver ^Transmitter transmitter receiver)
    {:midi-channel midi-channel}))

(defn mono-keyboard-update-fn [inputs state]
  (let [midi-channel (:midi-channel state)
        midi-message (async/poll! midi-channel)
        notes-on (update-notes-on (:notes-on state) midi-message)]
    {:state {:midi-channel midi-channel
             :notes-on notes-on}
     :outputs {:gate (gate notes-on)
               :note (note notes-on)
               :trigger (trigger midi-message)}}))

(defn monophonic-keyboard
  "returns a monophonic MIDI keyboard Module which outputs the lowest note that is on and gate and trigger signals"
  []
  (simple-engine/map->Module {:initial-state-fn initial-state-fn
                                 :update-fn     mono-keyboard-update-fn
                                 :output-names  [:gate
                                                :note
                                                :trigger]}))

(defn printer-update-fn [inputs state]
  (let [midi-queue (:midi-queue state)
        midi-event (async/poll! midi-queue)]
    (when (not (nil? midi-event))
      (println midi-event))
    {:state state}))

(defn printer
  "returns a Module which prints MIDI messages received"
  []
  (simple-engine/map->Module {:initial-state-fn initial-state-fn
                                 :update-fn     printer-update-fn}))