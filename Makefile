
dev:
	python create.py true
	python -m SimpleHTTPServer 8000

prod:
	python create.py false
	uglifyjs script.js -o script.min.js