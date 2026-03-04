.PHONY: build test lint run migrate-up migrate-down

build:
	go build -o bin/api ./cmd/api

test:
	go test ./...

lint:
	golangci-lint run ./...

run:
	go run ./cmd/api

migrate-up:
	migrate -path migrations -database "$${DATABASE_URL}" up

migrate-down:
	migrate -path migrations -database "$${DATABASE_URL}" down
