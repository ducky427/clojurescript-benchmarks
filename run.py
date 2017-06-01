
import csv
import os
import os.path
import subprocess
import sys
import time
import traceback

# Broken versions: '1.7.145', '1.7.122', '1.7.107',

versions = ['1.7.10', '1.7.28', '1.7.48', '1.7.58', '1.7.166',
            '1.7.170', '1.7.189', '1.7.228', '1.8.34', '1.8.40',
            '1.8.51', '1.9.14', '1.9.35', '1.9.36', '1.9.76',
            '1.9.89', '1.9.198', '1.9.211', '1.9.216', '1.9.225',
            '1.9.227', '1.9.229', '1.9.293', '1.9.456', '1.9.473',
            '1.9.493', '1.9.494', '1.9.518', '1.9.521', '1.9.542',
            '1.9.562']

# versions = ['1.9.562']

def get_stats(line):
    parts = line.split("||")
    title = parts[0].strip()
    data = parts[1].strip()[1:-1].split(",")
    res = {}
    for x in data:
        k,v = x.strip().split(" ")
        res[k] = float(v)
    return [title, res]


def runProcess(exe):
    p = subprocess.Popen(exe, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, shell=True)
    while True:
      retcode = p.poll()
      line = p.stdout.readline()
      yield line.strip()
      if(retcode is not None):
        break

def compile(cljs_version):
    os.environ["CLJS_VERSION"] = cljs_version
    cmd = 'lein build'
    start = time.time()
    for line in runProcess(cmd):
        if line:
            print line
    end = time.time()
    return end - start

metrics = ["mean", "deviation", "moe", "rme", "sem"]

def benchmark(writer, version, compile_time):
    engine = None
    section = None
    file_size = os.path.getsize('resources/public/js/compiled/bench.js')
    for line in runProcess('sh run.sh'):
        if not line:
            continue
        if line in ["V8", "JSC", "SM"]:
            engine = line
        elif line.startswith(";;"):
            section = line.replace(';', '').strip()
            print engine, section
        elif "||" in line:
            assert engine is not None, "No engine found!"
            assert section is not None, "No section found!"
            try:
                title, stats = get_stats(line)
                writer.writerow([version, compile_time, file_size, engine, section, title] + [stats.get(m) for m in metrics])
            except Exception as e:
                traceback.print_exc()
        else:
            print "Unrecognised: ", line

def main(writer):
    num_versions = len(versions)
    for i, v in enumerate(versions):
        print "Running: {0} ({1} of {2})".format(v, (i+1), num_versions)
        compile_time = compile(v)
        benchmark(writer, v, compile_time)

if __name__ == '__main__':
    filename = 'data_{0}.csv'.format(time.strftime("%Y%m%d_%H%M%S"))
    with open(filename, 'wb') as f:
        writer = csv.writer(f)
        writer.writerow(["Version", "CompileTime", "FileSize" ,"Engine", "Section", "Name",
                         "Mean", "Deviation", "MOE", "RME", "SEM"])
        main(writer)
