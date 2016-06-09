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

  ;; Symbol contruction
  (benchmark-suite [] (symbol 'foo))

  ;; array-reduce & ci-reduce
  (let [arr  (array)
        sum  (fn [a b] (+ a b))]
    (dotimes [i 10000]
      (.push arr i))
    (benchmark-suite [coll (seq arr)] (ci-reduce coll + 0))
    (benchmark-suite [coll (seq arr)] (ci-reduce coll sum 0))
    (benchmark-suite [coll arr] (ci-reduce coll + 0))
    (benchmark-suite [coll arr] (ci-reduce coll sum 0)))

  )

(init!)
