(ns com.ncpenterprises.clomosy.synths.basictests
  (:require [com.ncpenterprises.clomosy.modules.audio :as audio-mod]
            [com.ncpenterprises.clomosy.modules.midi :as midi-mod]
            [com.ncpenterprises.clomosy.modules.intonation :as int-mod]
            [com.ncpenterprises.clomosy.modules.oscillator :as osc-mod]
            [com.ncpenterprises.clomosy.modules.memory :as mem-mod]
            [com.ncpenterprises.clomosy.modules.amplification :as amp-mod]
            [com.ncpenterprises.clomosy.core :as core]
            [com.ncpenterprises.clomosy.modules.constant :as const-mod]
            [com.ncpenterprises.clomosy.modules.mixer :as mixer-mod]))

(defn simple-triangle [line buffer-size dt]
  {
   :modules (-> {}
                (core/add-module (midi-mod/monophonic-keyboard :keyboard))
                (core/add-module (int-mod/twelve-tone-equal-temperment :intonation))
                (core/add-module (osc-mod/triangle-wave :oscillator))
                (core/add-module (mem-mod/memory-cell :phase 0))
                (core/add-module (amp-mod/linear-amplifier :amp))
                (core/add-module (audio-mod/mono-output :output line buffer-size)))

  :patches {
            [:intonation :note]      [:keyboard :note]
            [:oscillator :frequency] [:intonation :frequency]
            [:oscillator :phase]     [:phase :out]
            [:phase :in]             [:oscillator :phase]
            [:amp :in]               [:oscillator :amplitude]
            [:amp :gain]             [:keyboard :gate]
            [:output :audio]         [:amp :out]
            }


  :order  [:keyboard
          :intonation
          :phase
          :oscillator
          :amp
          :output]
   }
  )

(defn delay-line-triangle [line buffer-size dt]
  {
   :modules (-> {}
                (core/add-module (midi-mod/monophonic-keyboard :keyboard))
                (core/add-module (int-mod/twelve-tone-equal-temperment :intonation))
                (core/add-module (osc-mod/triangle-wave :oscillator))
                (core/add-module (mem-mod/memory-cell :phase 0))
                (core/add-module (amp-mod/linear-amplifier :amp))
                (core/add-module (const-mod/constant :delay-time 0.5))
                (core/add-module (mem-mod/delay-line :delay 0.0 1.0 dt))
                (core/add-module (const-mod/constant :feedback 0.5))
                (core/add-module (amp-mod/linear-amplifier :feedback-amp))
                (core/add-module (mixer-mod/mixer :mixer))
                (core/add-module (const-mod/constant :volume 0.5))
                (core/add-module (amp-mod/linear-amplifier :output-amp))
                (core/add-module (audio-mod/mono-output :output line buffer-size)))

   :patches {
             [:intonation :note]      [:keyboard :note]
             [:oscillator :frequency] [:intonation :frequency]
             [:oscillator :phase]     [:phase :out]
             [:phase :in]             [:oscillator :phase]
             [:amp :in]               [:oscillator :amplitude]
             [:amp :gain]             [:keyboard :gate]
             [:mixer :in_1]   [:amp :out]
             [:mixer :in_2] [:feedback-amp :out]
             [:feedback-amp :in] [:delay :out]
             [:feedback-amp :gain] [:feedback :value]
             [:delay :delay-time]  [:delay-time :value]
             [:delay :in]         [:mixer :out]
             [:output-amp :in] [:mixer :out]
             [:output-amp :gain] [:volume :value]
             [:output :audio]         [:output-amp :out]
             }


   :order  [:keyboard
            :intonation
            :phase
            :oscillator
            :amp
            :delay-time
            :delay
            :feedback
            :feedback-amp
            :mixer
            :volume
            :output-amp
            :output]
   }
  )

(defn vibrato-triangle [line buffer-size dt]
  {
   :modules (-> {}
                (core/add-module (midi-mod/monophonic-keyboard :keyboard))
                (core/add-module (int-mod/twelve-tone-equal-temperment :intonation))

                (core/add-module (const-mod/constant :lfo-freq 5))
                (core/add-module (osc-mod/sine-wave :lfo))
                (core/add-module (mem-mod/memory-cell :lfo-phase 0))

                (core/add-module (amp-mod/linear-amplifier :freq-amp))

                (core/add-module (osc-mod/triangle-wave :oscillator))
                (core/add-module (mem-mod/memory-cell :phase 0))
                (core/add-module (amp-mod/linear-amplifier :amp))
                (core/add-module (audio-mod/mono-output :output line buffer-size)))

   :patches {
             [:intonation :note]      [:keyboard :note]

             [:lfo :frequency] [:lfo-freq :value]
             [:lfo :phase] [:lfo-phase :out]
             [:lfo-phase :in] [:lfo :phase]

             [:freq-amp :in] [:intonation :frequency]
             [:freq-amp :gain] [:lfo :amplitude]


             [:oscillator :frequency] [:freq-amp :out]
             [:oscillator :phase]     [:phase :out]
             [:phase :in]             [:oscillator :phase]
             [:amp :in]               [:oscillator :amplitude]
             [:amp :gain]             [:keyboard :gate]
             [:output :audio]         [:amp :out]
             }


   :order   [:keyboard
             :intonation
             :lfo-freq
             :lfo-phase
             :lfo
             :freq-amp
             :phase
             :oscillator
             :amp
             :output]
   }
  )