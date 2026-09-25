# 🤝 Contributing to xan

Thanks for considering a contribution to **xan**. This document is the real, current process — if something here goes stale, please send a PR fixing it rather than adding a second guide.

## 🛠️ Project Layout

xan is a single Android Studio project (Gradle multi-module). The pieces you'll actually touch:

* **`app/`** — the app itself. Compose UI lives under `app/src/main/kotlin/app/xan/music/ui/`, screens under `ui/screens/`, `viewmodels/` per screen, navigation wired in `ui/screens/NavigationBuilder.kt`.
* **`modules/music-sources/`** — source integrations such as `innertube`, `kugou`, and `spotify`.
* **`modules/lyrics/`** — lyrics integrations such as `lrclib`, `betterlyrics`, `simpmusic`, and the other provider-specific modules.
* **`modules/services/`** — integrations such as Last.fm, Shazam, and Kizzy.
* **`modules/visuals/`** — canvas/video modules including `canvas`, `artistvideo`, `applecanvas`, and `xancanvas`.
* **`tools/`** — local build/update helpers and maintenance scripts.

Gradle project names are intentionally unchanged. For example, the source for `:innertube` lives in `modules/music-sources/innertube/`, while dependencies still use `project(":innertube")`.

## 🖥️ Building locally

* **JDK 21**, latest stable **Android Studio**.
* `compileSdk 37`, `minSdk 26` — install those platforms via the SDK Manager if Android Studio prompts you to.
* Clone and open the repository root in Android Studio, let Gradle sync, then run/debug the `app` module as normal.
* CLI example: `./gradlew :app:assembleUniversalFossDebug`.
* Windows helper scripts are under `tools/windows/`.

## 🌿 Branches & Commits

* Branch names: `feature/short-description`, `fix/short-description`.
* Commit messages: a short type prefix (`feat:`, `fix:`, `refactor:`, `docs:`) followed by an imperative summary — see `git log` for the house style. Explain *why* in the body when it isn't obvious from the diff.

## 🚀 Pull Requests

1. Fork the repo and branch off `main`.
2. Keep the diff scoped to the PR's stated purpose — avoid drive-by reformatting or unrelated refactors, they make review harder.
3. Actually run the change on a device/emulator before opening the PR; this is a UI-heavy app and most regressions here are visual, not compile-time.
4. Describe *what* changed and *why* in the PR description; screenshots/recordings are expected for anything UI-visible.

## 🐞 Reporting Bugs

Open an issue with repro steps, your Android version/device, and `adb logcat` output if it's a crash. For anything real-time (playback glitches, Listen Together sync), a screen recording helps a lot.
