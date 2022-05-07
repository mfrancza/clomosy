(ns com.ncpenterprises.clomosy.modules.v2.midi-test
  (:require [clojure.test :refer :all]
            [com.ncpenterprises.clomosy.modules.v2.midi :as midi-modules]
            [com.ncpenterprises.clomosy.io.midi.messages :as midi-messages])
  (:import (javax.sound.midi ShortMessage)))

(deftest update-notes-on-test
  (testing "update-notes-on adds Note On messages to the map"
    (let [message (ShortMessage. ShortMessage/NOTE_ON 1 64 64)
          notes-on (midi-modules/update-notes-on {} message)]
      (is (= (get notes-on (midi-messages/note-number message)) message))))
  (testing "update-notes-on removes Note On messages from the map when a matching Note Off is applied"
    (let [message (ShortMessage. ShortMessage/NOTE_OFF 1 64 64)
          notes-on (midi-modules/update-notes-on {64 (ShortMessage. ShortMessage/NOTE_ON 1 64 64)} message)]
      (is (nil? (get notes-on (midi-messages/note-number message))))))
  (testing "update-notes-on removes Note On messages from the map when a matching Note On with a velocity of 0 is applied"
    (let [message (ShortMessage. ShortMessage/NOTE_ON 1 64 0)
          notes-on (midi-modules/update-notes-on {64 (ShortMessage. ShortMessage/NOTE_ON 1 64 64)} message)]
      (is (nil? (get notes-on (midi-messages/note-number message)))))))