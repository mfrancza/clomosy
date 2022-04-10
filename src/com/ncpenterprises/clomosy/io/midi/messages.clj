(ns com.ncpenterprises.clomosy.io.midi.messages
  (:import (javax.sound.midi ShortMessage)))

(defprotocol ChannelMessage
  "Protocol for interacting with MIDI Channel Messages"
  (channel-message? [message] "returns if the message is a Channel Message")
  (channel [message] "returns the Channel the message is related to"))

(defprotocol NoteOff
  "Protocol for the NoteOff message "
  (note-off? [message] "returns if the message is a Note Off Message ()")
  (note-number [message])
  (velocity [message]))

(defprotocol NoteOn
  (note-on? [message])
  (note-number [message])
  (velocity [message]))

(extend-type ShortMessage
  ChannelMessage
  (channel-message? [message]
    (< (.getStatus message) 0xF0))
  (channel [message]
    (.getChannel message))

  NoteOff
  (note-off? [message]
    (= (.getCommand message) ShortMessage/NOTE_OFF))
  (note-number [message]
    (.getData1 message))
  (velocity [message]
    (.getData2 message))

  NoteOn
  (note-on? [message]
    (= (.getCommand message) ShortMessage/NOTE_ON))
  (note-number [message]
    (.getData1 message))
  (velocity [message]
    (.getData2 message)))
