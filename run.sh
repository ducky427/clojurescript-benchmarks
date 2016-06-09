
echo "V8"
"${V8_HOME}/d8" resources/public/js/compiled/bench.js

echo "SM"
${SPIDERMONKEY_HOME}/js -f resources/public/js/compiled/bench.js

echo "JSC"
jsc -f resources/public/js/compiled/bench.js