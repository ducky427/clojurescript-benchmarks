
import csv
from jinja2 import Environment, FileSystemLoader

items = ["[x 1] (identity x)",
         "[] (symbol (quote foo))",
         "[coll (seq arr)] (ci-reduce coll + 0)",
         "[coll (seq arr)] (ci-reduce coll sum 0)",
         "[coll arr] (ci-reduce coll + 0)",
         "[coll arr] (ci-reduce coll sum 0)"]

def main():
    env = Environment(loader=FileSystemLoader('.'))
    template = env.get_template('index.tmpl')

    with open('index.html', 'wb') as f:
        f.write(template.render(items=items))

if __name__ == '__main__':
    main()