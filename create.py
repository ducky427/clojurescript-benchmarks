
import csv
import json
import sys

from collections import defaultdict
from itertools import groupby
from jinja2 import Environment, FileSystemLoader

#https://stackoverflow.com/questions/3482297/how-can-i-make-a-simple-counter-with-jinja2-templates
class _Counter(object):
  def __init__(self, start_value=1):
    self.value = start_value

  def current(self):
    return self.value

  def next(self):
    v= self.value
    self.value += 1
    return v

def main(items, debug):
    env = Environment(loader=FileSystemLoader('.'))
    env.globals['counter']=_Counter
    template = env.get_template('graph.tmpl')

    with open('graph.html', 'wb') as f:
        f.write(template.render(items=items, debug=debug))

def verify():
    with open('data.csv') as f:
        reader = csv.DictReader(f)
        rows = list(reader)
        assert (len(rows) % 3) == 0
        first_version = rows[0]['Version']
        first_engine = rows[0]['Engine']
        rows_to_consider = [x for x in rows if (x['Version'] == first_version) and (x['Engine'] == first_engine)]
        res = []
        for k, g in groupby(rows_to_consider, key=lambda x:x['Section']):
            res.append([k, [x['Name'] for x in g]])

        return res

fields = 'Version,Engine,Section,Name,Mean'.split(',')
fields_w_header = 'Version,FileSize,Engine,Section,Name,Mean'.split(',')

def jsonify():
    with open('data.csv', 'r') as csvfile:
        reader = csv.reader(csvfile)
        header = reader.next()
        header_present = 'FileSize' in header

        file_sizes = []
        file_versions_added = set()

        rows = []
        for r in reader:
            if header_present:
                row = dict(zip(fields_w_header, r[:6]))
            else:
                row = dict(zip(fields, r[:5]))

            if header_present and (row['Version'] not in file_versions_added):
                file_versions_added.add(row['Version'])
                file_sizes.append({"Version": row['Version'], "FileSize": int(row['FileSize'])})

            row['Mean'] = float(row['Mean'])
            rows.append(row)

    result = defaultdict(list)
    for r in rows:
        k = r['Name']
        r.pop('Name', None)
        result[k].append(r)

    with open('data.json', 'w') as jsonfile:
        out = json.dumps(result)
        jsonfile.write(out)

    if header_present:
        with open('sizes.json', 'w') as jsonfile:
            jsonfile.write(json.dumps(file_sizes))



if __name__ == '__main__':
    debug = sys.argv[1] == "true"
    items = verify()
    main(items, debug)
    jsonify()
