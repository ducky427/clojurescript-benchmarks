(ns bench.core
  (:refer-clojure :exclude [println simple-benchmark])
  (:require-macros [bench.core :refer [benchmark-suite simple-benchmark]])
  (:require [goog.object :as gobj]
            [cljsjs.benchmark]))

(def println print)
(set! *print-fn* js/print)

(def ^boolean quick? true)

(def metrics ["mean" "deviation" "moe" "rme" "sem"])

(def Suite (.-Suite js/Benchmark))

(defn get-metrics
  [results]
  (into {}
        (map (fn [x]
               [x (gobj/get results x)])
             metrics)))

(defprotocol IFoo (foo [x]))

#_(defn init!
  []
  (benchmark-suite [x 1] (identity x))

  ;; array-reduce & ci-reduce
  (let [arr  (array)
        sum  (fn [a b] (+ a b))]
    (dotimes [i 10000]
      (.push arr i))
    (benchmark-suite [coll (seq arr)] (ci-reduce coll + 0))
    (benchmark-suite [coll (seq arr)] (ci-reduce coll sum 0))
    (benchmark-suite [coll arr] (array-reduce coll + 0))
    (benchmark-suite [coll arr] (array-reduce coll sum 0)))


  )

(defn init!
  []
  (println ";; identity")
  (simple-benchmark [x 1] (identity x) 100000000)

  )

(init!)
