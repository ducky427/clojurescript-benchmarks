
import csv
from jinja2 import Environment, FileSystemLoader

def main(items):
    env = Environment(loader=FileSystemLoader('.'))
    template = env.get_template('index.tmpl')

    with open('index.html', 'wb') as f:
        f.write(template.render(items=items))

def verify():
    with open('data.csv') as f:
        reader = csv.DictReader(f)
        rows = list(reader)
        assert (len(rows) % 3) == 0
        first_version = rows[0]['Version']
        first_engine = rows[0]['Engine']
        return [x['Name'] for x in rows if (x['Version'] == first_version) and (x['Engine'] == first_engine)]

if __name__ == '__main__':
    items = verify()
    main(items)