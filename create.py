
import csv
from jinja2 import Environment, FileSystemLoader

def main():
    env = Environment(loader=FileSystemLoader('.'))
    template = env.get_template('index.tmpl')
    items = ["[x 1] (identity x)", "[] (symbol (quote foo))"]
    with open('index.html', 'wb') as f:
        f.write(template.render(items=items))

if __name__ == '__main__':
    main()