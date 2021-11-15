.PHONY: ci run repl jar run-infra stop-infra test lint migrations format build deps
.PHONY: docker.build docker.run backend.shell

ci:
	clj -T:build ci

run:
	clj -M:run-main

repl:
	clj -M:repl

backend.shell:
	docker-compose run backend bash

docker.run:
	docker run -it --rm -f ./path/file -p 8080:8080 --env ENVIRONMENT=${ENVIRONMENT} --env GITHUB_TOKEN=${GITHUB_TOKEN} gh-release-monitor

docker.build:
	docker build --no-cache --build-arg ENVIRONMENT --build-arg GITHUB_TOKEN -t gh-release-monitor .

build:
	@make jar
	@make docker.build

jar:
	clj -T:build jar

run-infra:
	docker-compose build --no-cache
	docker-compose up -d

stop-infra:
	docker-compose down

test:
	docker-compose -f ./docker-compose.test.yaml build
	docker-compose -f ./docker-compose.test.yaml up -d && clj -T:build test
	docker-compose -f ./docker-compose.test.yaml down

lint:
	clj -M:clj-kondo --lint src

migrations:
	@echo "running migrations"
	clj -X:run-migrate

format:
	clj -M:cljfmt fix

deps:
	clj -X:deps prep