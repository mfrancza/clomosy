(ns com.ncpenterprises.clomosy.io.midi.messages-test
  (:require [clojure.test :refer :all]
            [com.ncpenterprises.clomosy.io.midi.messages :as messages])
  (:import (javax.sound.midi ShortMessage)))

(deftest short-message-test
  (testing "implements ChannelMessage"
    (testing "channel-message?"
      (is (not (messages/channel-message? (ShortMessage. ShortMessage/TIMING_CLOCK)))
      (is (messages/channel-message? (ShortMessage. ShortMessage/NOTE_ON 1 64 64)))))
    (testing "channel"
      (= (messages/channel (ShortMessage. ShortMessage/NOTE_ON 5 64 64)) 5)))
  (testing "implements NoteOff"
    (testing "note-off?"
      (is (not (messages/note-off? (ShortMessage. ShortMessage/NOTE_ON 1 64 64))))
      (is (messages/note-off? (ShortMessage. ShortMessage/NOTE_OFF 1 64 64))))
    (testing "note-number"
      (= (messages/note-number (ShortMessage. ShortMessage/NOTE_OFF 5 32 64)) 32))
    (testing "velocity"
      (= (messages/note-number (ShortMessage. ShortMessage/NOTE_OFF 5 32 64)) 64)))
  (testing "implements NoteOn"
    (testing "note-on?"
      (is (not (messages/note-on? (ShortMessage. ShortMessage/NOTE_OFF 1 64 64))))
      (is (messages/note-on? (ShortMessage. ShortMessage/NOTE_ON 1 64 64))))
    (testing "note-number"
      (= (messages/note-number (ShortMessage. ShortMessage/NOTE_ON 5 31 64)) 31))
    (testing "velocity"
      (= (messages/velocity (ShortMessage. ShortMessage/NOTE_ON 5 32 65)) 65))))
