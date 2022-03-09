(ns com.ncpenterprises.clomosy.modules.v2.audio-test
  (:require [clojure.test :refer :all]
            [mockery.core :as mockery]
            [com.ncpenterprises.clomosy.io.audio :as io-audio]
            [com.ncpenterprises.clomosy.modules.v2.audio :as audio])
  (:import (javax.sound.sampled SourceDataLine)))

(deftest initial-state-fn-test
  (testing "creates a line and stores it in the initial state"
    (let [line-format (io-audio/map->AudioFormat {:encoding io-audio/pcm-signed :sample-size-in-bits 8})
          stub-line (reify SourceDataLine)]
      (mockery/with-mocks
        [get-output-line {:target ::io-audio/get-output-line :return stub-line}
         start-output-line {:target ::io-audio/start-output-line}
         open-output-line {:target ::io-audio/open-output-line}
         get-line-format {:target ::io-audio/get-line-format :return line-format}]
        (let [initial-state-fn (audio/get-output-line-initial-state-fn line-format 100)
              initial-state (initial-state-fn)
              encoding-fn (:encoding-fn initial-state)]
          (is (= (:call-count @open-output-line) 1))
          (is (= (:call-count @start-output-line) 1))
          (is (= (aget (encoding-fn 0.0) 0) 0))
          (is (= (:line initial-state) stub-line)))))))

(deftest mono-output-line-update-fn-test
  (testing "the input is encoded and written to the output line"
    (let [stub-line (reify SourceDataLine)
          encoding-fn (fn [input] (byte-array [2]))
          state {:line stub-line :encoding-fn encoding-fn}
          inputs {:output 1.0}]
      (mockery/with-mocks
        [write-frame {:target ::io-audio/write-frame}]
        (let [result (audio/mono-output-line-update-fn inputs state)]
          (is (= (:state result) state))
          (is (= (:call-count @write-frame) 1))
          (let [args (:call-args @write-frame)
                line (first args)
                output (second args)]
            (is (= line stub-line))
            (is (= (aget output 0) 2))))))))

