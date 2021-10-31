.PHONY: ci run repl jar up down test lint migrations format

ci:
	clj -T:build ci

run:
	clj -M:run-main

repl:
	clj -M:repl

jar:
	clj -T:build jar

up:
	docker-compose up -d

down:
	docker-compose down

test:
	clj -T:build test

lint:
	clj -M:clj-kondo --lint src

migrations:
	@echo "FIX ME"

format:
	clj -M:cljfmt fix
