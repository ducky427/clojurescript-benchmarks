(ns bench.core
  (:refer-clojure :exclude [println simple-benchmark])
  (:require-macros [bench.core :refer [simple-benchmark]])
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
  (println ";; identity")
  (simple-benchmark [x 1] (identity x) 100000000)

  (println ";; symbol construction")
  (simple-benchmark [] (symbol 'foo) 1000000)

  (println ";; array-reduce & ci-reduce")
  (let [arr  (array)
        sum  (fn [a b] (+ a b))]
    (dotimes [i 10000]
      (.push arr i))
    (simple-benchmark [coll (seq arr)] (ci-reduce coll + 0) 100)
    (simple-benchmark [coll (seq arr)] (ci-reduce coll sum 0) 100)
    (simple-benchmark [coll arr] (array-reduce coll + 0) 100)
    (simple-benchmark [coll arr] (array-reduce coll sum 0) 100))

  (println ";;; instance?")
  ;; WARNING: will get compiled away under advanced
  (simple-benchmark [coll []] (instance? PersistentVector coll) 1000000)

  (println ";;; satisfies?")
  (simple-benchmark [coll (list 1 2 3)] (satisfies? ISeq coll) 1000000)
  (simple-benchmark [coll [1 2 3]] (satisfies? ISeq coll) 1000000)

  (println ";;; array & string ops")
  (simple-benchmark [coll (array 1 2 3)] (seq coll) 1000000)
  (simple-benchmark [coll "foobar"] (seq coll) 1000000)
  (simple-benchmark [coll (array 1 2 3)] (first coll) 1000000)
  (simple-benchmark [coll "foobar"] (first coll) 1000000)
  (simple-benchmark [coll (array 1 2 3)] (nth coll 2) 1000000)
  (simple-benchmark [coll "foobar"] (nth coll 2) 1000000)

  (println ";;; cloning & specify")
  (simple-benchmark [coll [1 2 3]] (clone coll) 1000000)
  (simple-benchmark [coll [1 2 3]] (specify coll IFoo (foo [_] :bar)) 1000000)
  (simple-benchmark [coll (specify [1 2 3] IFoo (foo [_] :bar))] (foo coll) 1000000)

  (println ";;; list ops")
  (simple-benchmark [coll (list 1 2 3)] (first coll) 1000000)
  (simple-benchmark [coll (list 1 2 3)] (-first coll) 1000000)
  (simple-benchmark [coll (list 1 2 3)] (rest coll) 1000000)
  (simple-benchmark [coll (list 1 2 3)] (-rest coll) 1000000)
  (simple-benchmark [] (list) 1000000)
  (simple-benchmark [] (list 1 2 3) 1000000)

  (println ";;; vector ops")
  (simple-benchmark [] [] 1000000)
  (simple-benchmark [[a b c] (take 3 (repeatedly #(rand-int 10)))] (-count [a b c]) 1000000)
  (simple-benchmark [[a b c] (take 3 (repeatedly #(rand-int 10)))] (-count (vec #js [a b c])) 1000000)
  (simple-benchmark [[a b c] (take 3 (repeatedly #(rand-int 10)))] (-count (vector a b c)) 1000000)
  (simple-benchmark [coll [1 2 3]] (transient coll) 100000)
  (simple-benchmark [coll [1 2 3]] (nth coll 0) 1000000)
  (simple-benchmark [coll [1 2 3]] (-nth coll 0) 1000000)
  (simple-benchmark [coll [1 2 3]] (-nth ^not-native coll 0) 1000000)
  (simple-benchmark [coll [1 2 3]] (coll 0) 1000000)
  (simple-benchmark [coll [1 2 3]] (conj coll 4) 1000000)
  (simple-benchmark [coll [1 2 3]] (-conj coll 4) 1000000)
  (simple-benchmark [coll []] (-conj ^not-native coll 1) 1000000)
  (simple-benchmark [coll [1]] (-conj ^not-native coll 2) 1000000)
  (simple-benchmark [coll [1 2]] (-conj ^not-native coll 3) 1000000)
  (simple-benchmark [coll [1 2 3]] (seq coll) 1000000)
  (simple-benchmark [coll [1 2 3]] (-seq coll) 1000000)
  (simple-benchmark [coll (seq [1 2 3])] (first coll) 1000000)
  (simple-benchmark [coll (seq [1 2 3])] (-first coll) 1000000)
  (simple-benchmark [coll (seq [1 2 3])] (rest coll) 1000000)
  (simple-benchmark [coll (seq [1 2 3])] (-rest coll) 1000000)
  (simple-benchmark [coll (seq [1 2 3])] (next coll) 1000000)

  (println ";;; large vector ops")
  (simple-benchmark [] (reduce conj [] (range 40000)) 10)
  (simple-benchmark [coll (reduce conj [] (range (+ 32768 32)))] (conj coll :foo) 100000)
  (simple-benchmark [coll (reduce conj [] (range 40000))] (assoc coll 123 :foo) 100000)
  (simple-benchmark [coll (reduce conj [] (range (+ 32768 33)))] (pop coll) 100000)

  )

(init!)
