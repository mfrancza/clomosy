(ns com.ncpenterprises.clomosy.io.audio-test
  (:require [clojure.test :refer :all]
            [com.ncpenterprises.clomosy.io.audio :as io-audio])
  (:import (javax.sound.sampled AudioFormat$Encoding)))

(deftest map->AudioFormat-test
  (testing "the map is translated to an equivalent AudioFormat"
    (let [returned-format (io-audio/map->AudioFormat
                            {:encoding (. AudioFormat$Encoding PCM_SIGNED)
                             :sample-rate 40000
                             :sample-size-in-bits 8
                             :channels 2
                             :frame-size 4
                             :frame-rate 20000
                             :big-endian true})]
      (is (= (. returned-format getEncoding) (. AudioFormat$Encoding PCM_SIGNED)))
      (is (= (. returned-format getSampleRate) (float 40000)))
      (is (= (. returned-format getSampleSizeInBits) 8))
      (is (= (. returned-format getChannels) 2))
      (is (= (. returned-format getFrameSize) 4))
      (is (= (. returned-format getFrameRate) (float 20000)))
      (is (= (. returned-format isBigEndian) true))))
  (testing "when no values are specified, the returned AudioFormat is little-endian linear PCM with AudioSystem.NOT_SPECIFIED for all other parameters"
    (let [returned-format (io-audio/map->AudioFormat {})]
      (is (= (. returned-format getEncoding) (. AudioFormat$Encoding PCM_UNSIGNED)))
      (is (= (. returned-format getSampleRate) (float io-audio/not-specified) ))
      (is (= (. returned-format getSampleSizeInBits) io-audio/not-specified))
      (is (= (. returned-format getChannels) io-audio/not-specified))
      (is (= (. returned-format getFrameSize) io-audio/not-specified))
      (is (= (. returned-format getFrameRate) (float io-audio/not-specified)))
      (is (= (. returned-format isBigEndian) false))))
    (testing "when :frame-rate is not provided, the :sample-rate value is used"
      (let [returned-format (io-audio/map->AudioFormat {:sample-rate 40000})]
        (is (= (. returned-format getSampleRate) (float 40000)))
        (is (= (. returned-format getFrameRate) (float 40000)))))
    (testing "when :frame-size is not provided, but :sample-size-in-bits and :channels are, the product of the channels and sample size is used"
      (let [returned-format (io-audio/map->AudioFormat {:sample-size-in-bits 16 :channels 4})]
        (is (= (. returned-format getSampleSizeInBits) 16))
        (is (= (. returned-format getChannels) 4))
        (is (= (. returned-format getFrameSize) 8)))))