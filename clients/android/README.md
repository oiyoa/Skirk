# Skirk Android Client

This directory is the native Android client scaffold. Android requires a `VpnService` for whole-device routing, so it is not the same runtime shape as the Linux/Windows SOCKS client.

Current scope:

- Compose Material 3 UI for importing a Skirk `client.json`;
- native `VpnService` permission declaration;
- service boundary for the tunnel engine;
- same config contract as desktop clients.

Not production-complete yet:

- the Go tunnel engine is not bound into the APK;
- packet forwarding from the TUN interface to Skirk Drive/Sheets is not implemented.

Standard next step:

1. Export the Go tunnel core behind a small mobile API with `gomobile bind`, or implement the protocol engine natively in Kotlin.
2. Connect `SkirkVpnService` to that engine.
3. Add emulator integration tests for import, VPN permission, connect, disconnect, and revoke flows.
