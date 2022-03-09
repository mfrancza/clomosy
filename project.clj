(defproject clomosy "0.1.0-SNAPSHOT"
  :description "A modular synthesizer implemented in Clojure"
  :url "https://github.com/mfrancza/clomosy"
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/core.async "1.5.640"]
                 [mockery "0.1.4"]]
  :plugins [[lein-ancient "1.0.0-RC4-SNAPSHOT"]]
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo}
  :main ^:skip-aot com.ncpenterprises.clomosy.core)
