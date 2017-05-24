(defproject bench "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript ~(System/getenv "CLJS_VERSION")]
                 [cljsjs/benchmark "2.1.4-0"]]

  :plugins [[lein-cljfmt "0.5.6"]
            [lein-cljsbuild "1.1.6" :exclusions [[org.clojure/clojure]]]
            [lein-ancient "0.6.10"]]

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :cljsbuild {:builds
              [{:id "dev"
                :source-paths ["src"]

                :compiler {:main bench.core
                           :asset-path "js/compiled/out"
                           :output-to "resources/public/js/compiled/bench.js"
                           :output-dir "resources/public/js/compiled/out"
                           :source-map-timestamp true}}
               {:id "min"
                :source-paths ["src"]
                :compiler {:output-to "resources/public/js/compiled/bench.js"
                           :main bench.core
                           :output-wrapper true
                           :parallel-build true
                           :optimizations :advanced
                           :pretty-print false}}]}

  :aliases {"build" ["do" "clean" ["cljsbuild" "once" "min"]]})
