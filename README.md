# Skirk

Skirk is a Go-first restricted-network transport that uses Google Drive as the encrypted data lane and Google Sheets as the control lane. It is designed for the case where ordinary endpoints fail but Google APIs can still be reached, including through Google-fronted TLS routing.

## Current Status

- Production path: Go CLI in `cmd/skirk`.
- Transport: encrypted Drive chunks + Sheets control rows.
- Client UX: one generated `client.json`; no client-side Google login required.
- Exit UX: run `skirk serve-exit` anywhere with normal internet egress.
- Client mode: local SOCKS5 proxy on Linux today; Windows and Android clients can consume the same config format.

Skirk does not require a VPS for protocol reasons. It requires an exit machine with working internet egress. A VPS is the most reliable exit because it stays online, but a laptop or home server also works while it is awake and connected.

## Quick Start

Build:

```bash
make build
```

Open the operator menu:

```bash
./bin/skirk
```

Create a Google-backed kit:

```bash
./bin/skirk setup init --out skirk-kit
```

If Google login is needed, Skirk runs `gcloud auth login --no-launch-browser --enable-gdrive-access --update-adc --force` and prints the browser URL/code flow.

Run the exit on a VPS, laptop, or server with normal internet:

```bash
./bin/skirk serve-exit --config skirk-kit/exit.json
```

Run the client SOCKS5 proxy:

```bash
./bin/skirk serve-client --config skirk-kit/client.json --listen 127.0.0.1:18080
curl --socks5-hostname 127.0.0.1:18080 http://example.com/
```

Optional: run the desktop dashboard on Windows or a desktop Linux machine with a browser:

```bash
./bin/skirk client-ui --config skirk-kit/client.json --socks 127.0.0.1:18080 --ui 127.0.0.1:18280
```

Preferred Windows app:

```bash
cd clients/desktop
npm install
npm run tauri dev
```

## Cleanup

Delete the Google Sheet and Drive folder created by setup:

```bash
./bin/skirk workspace delete --config skirk-kit/exit.json --delete-drive-folder
```

To invalidate all configs generated from the same OAuth login, revoke the Google app access from the account security page.

## Security Model

The Google account sees encrypted chunks and control metadata. The exit sees target addresses and plaintext for non-TLS application traffic, like any proxy or VPN exit. HTTPS payloads remain protected by the target site's TLS.

Generated configs contain a Google refresh token and the Skirk tunnel secret. Treat `client.json` and `exit.json` like passwords.

## Documentation

- [Setup Guide](docs/setup.md)
- [Client Guide](docs/clients.md)
- [Go CLI Notes](docs/go_skirk.md)
- [Drive + Sheets Architecture](docs/skirk_drive_sheets_architecture.md)
- [Modes](docs/skirk_modes.md)
- [Latest Throughput Notes](docs/optimized_throughput_2026_05_02.md)
