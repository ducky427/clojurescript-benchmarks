(ns bench.core)

(defmacro benchmark-suite
  [bindings expr iterations]
  (let [title    (str (pr-str bindings) " " (pr-str expr))]
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
             (.run))))))

(defmacro simple-benchmark
  [bindings expr iterations]
  (let [title    (str (pr-str bindings) " " (pr-str expr))]
    `(let ~bindings
       (let [start#   (cljs.core/system-time)]
         (dotimes [_# ~iterations]
           ~expr)
         (~'println ~title " || "
                    {"mean" (/ (- (cljs.core/system-time) start#) ~iterations)})
         (~'println ~title ~iterations)))))