# Clojurescript Benchmarks

This project contains code to run certain micro benchmarks against various version of ClojureScript on different JavaScript engines.

A good article about benchmarking JS code is [here](http://calendar.perfplanet.com/2010/bulletproof-javascript-benchmarks/).

## How to run these benchmarks

1. Install the 3 JavaScript engines - JavaScriptCore, v8 and SpiderMonkey. Installation notes for [Linux](https://github.com/ducky427/clojurescript-benchmarks/wiki/Installation-Notes) and [OS X](https://github.com/clojure/clojurescript/wiki/Running-the-tests). Also set the appropriate environment variables - `V8_HOME`, `SPIDERMONKEY_HOME` and making sure `jsc` is on the path.

1. Clone this repo.

1. To run full benchmarks using [Benchmark.js](https://benchmarkjs.com/), change `quick?` in `src/bench/core.clj` to `false`.

1. To select which versions to run the benchmarks against, edit `versions` variable in `run.py`. By default, it'll run it against all releases since 1.7+.

1. Run `python run.py`. This will produce a file `data_YYYYMMDD_HHmmSS.csv` which has all the results.

### Inspiration

- https://jafingerhut.github.io/clojure-benchmarks-results/Clojure-expression-benchmarks.html
- https://github.com/netguy204/cljs-bench

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
