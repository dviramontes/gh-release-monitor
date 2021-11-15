.PHONY: ci run repl jar run-infra stop-infra test lint migrations format build deps
.PHONY: docker.build docker.run

ci:
	clj -T:build ci

run:
	clj -M:run-main

repl:
	clj -M:repl

docker.run:
	docker run -it -p 8080:8080 --env ENVIRONMENT=${ENVIRONMENT} --env GITHUB_TOKEN=${GITHUB_TOKEN} gh-release-monitor:latest

docker.build:
	docker build --no-cache --build-arg ENVIRONMENT --build-arg GITHUB_TOKEN -t gh-release-monitor:latest .

build:
	@make jar
	@make docker.build

jar:
	clj -T:build jar

run-infra:
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