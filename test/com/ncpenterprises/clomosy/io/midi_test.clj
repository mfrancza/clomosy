(ns com.ncpenterprises.clomosy.io.midi-test
  (:require [clojure.test :refer :all]
            [com.ncpenterprises.clomosy.io.midi :as midi]
            [clojure.core.async :as async])
  (:import (javax.sound.midi ShortMessage)))

(deftest channel-receiver-test
  (testing "that the channel receiver sends messages and closes to the provided channel"
    (let [midi-channel (async/chan 10)
          test-message (ShortMessage. ShortMessage/NOTE_ON 1 64 64)
          receiver (midi/channel-receiver midi-channel)]
      (.send receiver test-message 0)
      (is (= (async/<!! midi-channel) test-message))
      (.close receiver)
      (is (= (async/poll! midi-channel) nil)))))