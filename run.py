
import csv
import os
import subprocess
import sys

from collections import defaultdict

versions = ['1.9.36', '1.9.35', '1.9.14', '1.8.51', '1.8.40', '1.8.34',
            '1.7.228', '1.7.189', '1.7.170', '1.7.166', '1.7.145', '1.7.122',
            '1.7.107', '1.7.58', '1.7.48',  '1.7.28', '1.7.10']

versions = ['1.9.36']

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
    for line in runProcess(cmd):
        print line

metrics = ["mean", "deviation", "moe", "rme", "sem"]

def benchmark(writer, version):
    engine = None
    for line in runProcess('sh run.sh'):
        if not line:
            continue
        if line in ["V8", "JSC", "SM"]:
            engine = line
        else:
            assert engine is not None, "No engine found!"
            try:
                title, stats = get_stats(line)
                writer.writerow([version, engine, title] + [stats[m] for m in metrics])
            except:
                print sys.exc_info()[0]

def main(writer):
    for v in versions:
        print "Running: {0}".format(v)
        compile(v)
        benchmark(writer, v)

if __name__ == '__main__':
    with open('data.csv', 'wb') as f:
        writer = csv.writer(f)
        writer.writerow(["Version", "Engine", "Name", "Mean", "Deviation", "MOE", "RME", "SEM"])
        main(writer)