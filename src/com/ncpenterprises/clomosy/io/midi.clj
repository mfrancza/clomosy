(ns com.ncpenterprises.clomosy.io.midi
  "functions for interacting with MIDI IO via the javax.sound.midi interface"
  (:require [clojure.core.async :as async])
  (:import (javax.sound.midi MidiSystem Receiver ShortMessage MidiDevice)))

(defn midi-device-info
  "Returns the MidiDevice$Info for the MidiDevices in the MidiSystem"
  []
  (MidiSystem/getMidiDeviceInfo))

(defn midi-device
  "Returns a MIDI device matching the supplied MidiDevice$Info"
  [midi-device-info]
  (MidiSystem/getMidiDevice midi-device-info))

(defn midi-transmitter
  "Obtains a Transmitter from the system or provided MidiDevice"
  ([]
   (MidiSystem/getTransmitter))
  ([midi-device]
   (.getTransmitter ^MidiDevice midi-device)))

(defn midi-receiver
  "creates a Receiver which calls the provided functions for it's methods"
  [send-fn close-fn]
  (reify Receiver
    (send [this message time-stamp]
      (send-fn this message time-stamp))
    (close [this]
      (close-fn this))))

(defn channel-receiver
  "creates a Receiver which sends MidiMessages to midi-channel and closes it when the device closes"
  [midi-channel]
  (midi-receiver
    (fn [this message time-stamp]
      (async/>!! midi-channel message))
    (fn [this]
      (async/close! midi-channel))))

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