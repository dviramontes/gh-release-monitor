.PHONY: ci run repl jar run-infra stop-infra test lint migrations format build deps

ci:
	clj -T:build ci

run:
	clj -M:run-main

repl:
	clj -M:repl

build:
	@make jar

jar:
	clj -T:build jar

run-infra:
	docker-compose up -d

stop-infra:
	docker-compose down

test:
	clj -T:build test

lint:
	clj -M:clj-kondo --lint src

migrations:
	@echo "FIX ME"

format:
	clj -M:cljfmt fix

deps:
	clj -X:deps prep