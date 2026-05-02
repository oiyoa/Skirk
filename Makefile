.PHONY: test build build-linux build-windows build-all desktop-sidecars desktop-build

test:
	go test ./...
	pytest -q

build:
	mkdir -p bin
	go build -o bin/skirk ./cmd/skirk

build-linux:
	mkdir -p bin
	GOOS=linux GOARCH=amd64 go build -o bin/skirk-linux-amd64 ./cmd/skirk
	GOOS=linux GOARCH=arm64 go build -o bin/skirk-linux-arm64 ./cmd/skirk

build-windows:
	mkdir -p bin
	GOOS=windows GOARCH=amd64 go build -o bin/skirk-windows-amd64.exe ./cmd/skirk

build-all: build build-linux build-windows

desktop-sidecars:
	clients/desktop/scripts/stage_sidecars.sh

desktop-build: desktop-sidecars
	cd clients/desktop && npm install && npm run build
