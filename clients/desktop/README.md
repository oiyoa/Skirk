# Skirk Desktop App

This is the Windows-first portable Skirk client.

Stack:

- Tauri 2
- React
- Go `skirk` sidecar

What it does:

- imports a generated `client.json`;
- stores profiles in app-local or portable data;
- starts/stops the Go Skirk SOCKS client;
- shows the SOCKS address, process status, and logs;
- supports a portable Windows folder layout.

## Development

Build the Skirk sidecar first:

```bash
make build
make build-windows
```

Install frontend dependencies:

```bash
cd clients/desktop
npm install
```

Run the app:

```bash
npm run tauri dev
```

## Portable Windows Layout

Ship a zip/folder like:

```text
Skirk.exe
skirk-portable
portable-data/
sidecars/windows/skirk.exe
```

Portable mode activates when `portable-data/` or `skirk-portable` exists beside the app executable.

The app stores:

- `portable-data/config/profiles.json`
- `portable-data/config/settings.json`
- imported profile configs as `portable-data/config/profile-*.json`
- logs under `portable-data/logs/`

## Production Notes

The desktop app intentionally only manages the local SOCKS client. It does not set the Windows system proxy or install a TUN driver yet. That keeps the first Windows release portable and low-risk. A future Windows tunnel mode can add a sidecar such as sing-box or wintun after the SOCKS client has enough real-user validation.
