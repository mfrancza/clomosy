(ns clomosy.io.midi.messages
  "protocols for interacting with MIDI messages"
  (:import (javax.sound.midi ShortMessage)))

(defprotocol ChannelMessage
  "Protocol for interacting with MIDI Channel Messages"
  (channel-message? [message] "if the message is a Channel Message")
  (channel [message] "the Channel the message is related to"))

(defprotocol NoteOff
  "Protocol for the NoteOff message "
  (note-off? [message] "if the message is a Note Off message")
  (note-number [message] "the note number of the note being turned off")
  (velocity [message] "the velocity the note is being turned off with"))

(defprotocol NoteOn
  (note-on? [message] "if the message is a Note On message")
  (note-number [message] "the note number of the note being turned on")
  (velocity [message] "the velocity the note is being turned on with"))

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
