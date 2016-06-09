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

(defprotocol IFoo (foo [x]))

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
    (benchmark-suite [coll arr] (array-reduce coll + 0))
    (benchmark-suite [coll arr] (array-reduce coll sum 0)))

  ;; instance?
  (benchmark-suite [coll []] (instance? PersistentVector coll))

  ;; satisfies?
  (benchmark-suite [coll (list 1 2 3)] (satisfies? ISeq coll))
  (benchmark-suite [coll [1 2 3]] (satisfies? ISeq coll))

  ;; array & string ops
  (benchmark-suite [coll (array 1 2 3)] (seq coll))
  (benchmark-suite [coll "foobar"] (seq coll))
  (benchmark-suite [coll (array 1 2 3)] (first coll))
  (benchmark-suite [coll "foobar"] (first coll))
  (benchmark-suite [coll (array 1 2 3)] (nth coll 2))
  (benchmark-suite [coll "foobar"] (nth coll 2))

  ;; cloning & specify
  (benchmark-suite [coll [1 2 3]] (clone coll))
  (benchmark-suite [coll [1 2 3]] (specify coll IFoo (foo [_] :bar)))
  (benchmark-suite [coll (specify [1 2 3] IFoo (foo [_] :bar))] (foo coll))

  ;; list ops
  (benchmark-suite [coll (list 1 2 3)] (first coll))
  (benchmark-suite [coll (list 1 2 3)] (-first coll))
  (benchmark-suite [coll (list 1 2 3)] (rest coll))
  (benchmark-suite [coll (list 1 2 3)] (-rest coll))
  (benchmark-suite [] (list))
  (benchmark-suite [] (list 1 2 3))

  ;; vector ops
  (benchmark-suite [] [])
  (benchmark-suite [[a b c] (take 3 (repeatedly #(rand-int 10)))] (-count [a b c]))
  (benchmark-suite [[a b c] (take 3 (repeatedly #(rand-int 10)))] (-count (vec #js [a b c])))
  (benchmark-suite [[a b c] (take 3 (repeatedly #(rand-int 10)))] (-count (vector a b c)))
  (benchmark-suite [coll [1 2 3]] (transient coll))
  (benchmark-suite [coll [1 2 3]] (nth coll 0))
  (benchmark-suite [coll [1 2 3]] (-nth coll 0))
  (benchmark-suite [coll [1 2 3]] (-nth ^not-native coll 0))
  (benchmark-suite [coll [1 2 3]] (coll 0))
  (benchmark-suite [coll [1 2 3]] (conj coll 4))
  (benchmark-suite [coll [1 2 3]] (-conj coll 4))
  (benchmark-suite [coll []] (-conj ^not-native coll 1))
  (benchmark-suite [coll [1]] (-conj ^not-native coll 2))
  (benchmark-suite [coll [1 2]] (-conj ^not-native coll 3))
  (benchmark-suite [coll [1 2 3]] (seq coll))
  (benchmark-suite [coll [1 2 3]] (-seq coll))
  (benchmark-suite [coll (seq [1 2 3])] (first coll))
  (benchmark-suite [coll (seq [1 2 3])] (-first coll))
  (benchmark-suite [coll (seq [1 2 3])] (rest coll))
  (benchmark-suite [coll (seq [1 2 3])] (-rest coll))
  (benchmark-suite [coll (seq [1 2 3])] (next coll))

  )

(init!)
