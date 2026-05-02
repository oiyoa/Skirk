#!/usr/bin/env bash
set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../.." && pwd)"
desktop_root="$repo_root/clients/desktop"

mkdir -p "$desktop_root/src-tauri/resources/sidecars/linux"
mkdir -p "$desktop_root/src-tauri/resources/sidecars/windows"

GOOS=linux GOARCH=amd64 go build -C "$repo_root" -o "$desktop_root/src-tauri/resources/sidecars/linux/skirk" ./cmd/skirk
GOOS=windows GOARCH=amd64 go build -C "$repo_root" -o "$desktop_root/src-tauri/resources/sidecars/windows/skirk.exe" ./cmd/skirk

chmod +x "$desktop_root/src-tauri/resources/sidecars/linux/skirk"
