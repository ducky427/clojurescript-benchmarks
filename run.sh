
echo "V8"
"${V8_HOME}/d8" resources/public/js/compiled/bench.js

echo "SM"
${SPIDERMONKEY_HOME}/js -f resources/public/js/compiled/bench.js

echo "JSC"
${JSC_HOME}/jsc -f resources/public/js/compiled/bench.js