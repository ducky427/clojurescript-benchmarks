(ns bench.core
  (:refer-clojure :exclude [println])
  (:require-macros [bench.core :refer [benchmark-suite]])
  (:require [goog.object :as gobj]
            [cljsjs.benchmark]))

(def println print)
(set! *print-fn* js/print)

(def metrics ["mean" "deviation" "moe" "rme" "sem"])

(def Suite (.-Suite js/Benchmark))

(defn get-metrics
  [results]
  (into {}
        (map (fn [x]
               [x (gobj/get results x)])
             metrics)))

(defn init!
  []
  (benchmark-suite [x 1] (identity x))
  (benchmark-suite [] (symbol 'foo)))

(init!)
