(ns bench.core)

(def quick? true)

(defmacro simple-benchmark
  [bindings expr iterations]
  (let [title    (str (pr-str bindings) " " (pr-str expr))]
    (if quick?
      `(let ~bindings
         (let [start#   (.getTime (js/Date.))]
           (dotimes [_# ~iterations]
             ~expr)
           (~'println ~title " || "
            {"mean" (/ (- (.getTime (js/Date.)) start#) ~iterations)})))
      `(let ~bindings
         (let [suite# (bench.core/Suite.)]
           (-> suite#
               (.add ~title
                     (fn [] ~expr))
               (.on "cycle" (fn [event#]
                              (let [b#    (-> event# .-target)
                                    stat# (-> b# .-stats)]
                                (~'println (.-name b#) (-> b# .-times .-elapsed) (.-count b#))
                                (~'println (.-name b#) " || " (bench.core/get-metrics stat#)))))
               (.run)))))))
