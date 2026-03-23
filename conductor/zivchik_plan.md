# Implementation Plan: Zivchik Gavno

## Objective
Develop a hybrid Android VPN client based on **sing-box** supporting VLESS and Amnezia WireGuard (AWG).

## Key Components
- **Core:** sing-box (libbox) supporting VLESS + Reality/gRPC and AWG.
- **Service:** Android VpnService implementation.
- **Updater:** Background hourly Worker for VLESS (GitHub) and AWG (Warp).
- **UI:** Simplified Compose UI with "Whitelist Mode" toggle for UAV-threat periods.

## Implementation Steps
1.  **Phase 1:** Project initialization and GitHub Actions (.github/workflows).
2.  **Phase 2:** Integration of sing-box core and VpnService logic.
3.  **Phase 3:** Database (Room) and Update logic (WorkManager).
4.  **Phase 4:** Jetpack Compose UI (Main, Advanced, Settings).
5.  **Phase 5:** Health checks and auto-ping filtering.

## Verification
- GitHub Action successfully produces an APK artifact.
- App connects to VLESS/AWG endpoints and routes traffic.
- Hourly updates successfully fetch new configurations.
