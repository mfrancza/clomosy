(ns com.ncpenterprises.clomosy.modules.v2.midi
  (:require [com.ncpenterprises.clomosy.engines.simplev2 :as simple-v2-engine]
            [clojure.core.async :as async]
            [com.ncpenterprises.clomosy.io.midi :as midi])
  (:import (javax.sound.midi MidiDeviceTransmitter)))

(defn- gate [state]
  (if (empty? (:notes-on state)) 0 1))

(defn- note [state]
  (if (nil? (first (:notes-on state))) 0 (first (:notes-on state))))

(defn- trigger [midi-event]
  (not (nil? midi-event)))

(defn initial-state-fn []
  (let [midi-queue (async/chan 100)
        _ (println midi-queue)
        midi-in (midi/getTransmitter)
        _ (println midi-in)]
    (.setReceiver ^MidiDeviceTransmitter midi-in (midi/queue-receiver midi-queue))
    {:midi-queue midi-queue}))

(defn mono-keyboard-update-fn [inputs state dt]
  (let [midi-queue (:midi-queue state)
        midi-event (async/poll! midi-queue)
        updated-state (midi/apply-message state midi-event)]
    {:state updated-state
     :outputs {:gate (gate updated-state)
               :note (note updated-state)
               :trigger (trigger midi-event)}}))

(defn monophonic-keyboard
  "returns a monophonic MIDI keyboard module which outputs the lowest note that is on and gate and trigger signals"
  []
  (simple-v2-engine/map->Module {:initial-state-fn initial-state-fn
                                 :update-fn mono-keyboard-update-fn
                                 :output-names [:gate
                                                :note
                                                :trigger]}))

(defn printer-update-fn [inputs state dt]
  (let [midi-queue (:midi-queue state)
        midi-event (async/poll! midi-queue)]
    (when (not (nil? midi-event))
      (println midi-event))
    {:state state}))

(defn printer
  "prints MIDI messages received"
  []
  (simple-v2-engine/map->Module {:initial-state-fn initial-state-fn
                                 :update-fn printer-update-fn}))