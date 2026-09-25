# XAN

**Current version: 0.1.0**

XAN is an Android music app for phones, tablets, Android TV, and Android Auto. The app uses a single XAN launcher identity across supported Android surfaces.

**Author · Lead Dev · Owner:** bxane

- Repository: https://github.com/bxane-dev/xan
- Releases, app version, updates, and changelog: https://github.com/bxane-dev/xan/releases
- Creator: https://guns.lol/bxane
- Support: https://discord.gg/h4Wsu824Uf

> The Android app reads the Discord invite from this README at runtime. Change the
> `Support:` Discord URL here to update the invite used by builds that can access
> the public README. The APK also carries the same invite as an offline fallback.


## Quick access

Windows build and repository helpers live in `tools/windows/`. Branding hot-swap files live in `branding/`.

- `branding/replace-logo.ps1 <png> [round-png]` — replace the XAN mark and round launcher variant.
- `BUILD-RELEASE.bat` — build a versioned release APK.
- `BUILD-DEBUG.bat` — build an installable debug APK.
- `UPDATE-REPO.bat` — update a local clone to the latest `main`.
- `UPDATE-DEPENDENCIES.bat` — update dependencies, migrate source compatibility, and compile-check.
- `UPDATE-SOURCE.bat` — run source compatibility migrations and compile-check only.
- `CHANGELOG.md` — release oriented changes for this build.

Finished local APKs are copied to the root `dist/` folder.

## Install and build

XAN supports Android 8.0 (API 26) and newer. Install a signed release APK on the device, or open this directory in Android Studio. On Windows, run `build-release.bat` for the default FOSS release or `build-release.bat v0.1.1` for another version. Run `build-debug.bat` for a debug APK. The builders need Android Studio or a JDK 21+ and an Android SDK with the project's compile SDK installed. The first release build generates a `bxane` signing key if one is not already present. Back up `app/keystore/release.keystore` and `local.properties` securely: future updates must use the same key.

The current version is `0.1.0`, defined in `version.properties`. Both Gradle and the Windows builders read it; `XAN_RELEASE_TAG` can override it for a new release. Artifacts appear in `dist/`.

## App experience

- Home, search, library, playlists, queue, lyrics, and a full player are part of the Android UI.
- Liquid Glass and solid UI modes can be selected in Appearance. The app remembers the choice and the accent color.
- Spotify Web opens visibly in the app's login window. Spotify authentication and content access remain subject to Spotify's service.
- ListenBrainz can submit playing and completed tracks after the user supplies a token. The connection needs network access.
- Local audio playback and supported source downloads are available. Liked songs are queued for download by default where the current source supplies a downloadable stream; availability and storage use depend on the source and device.
- XAN checks GitHub Releases for updates by default. Update checks need the release repository to be reachable.
- Community: [Discord](https://discord.gg/h4Wsu824Uf). Developer: [bxane](https://github.com/bxane-dev). [Supporter page](https://guns.lol/bxane).

## Privacy and limits

XAN is designed with local controls and no embedded release secrets. Music, lyrics, artwork, scrobbling, Spotify Web, and update features contact their respective services when used. ListenBrainz and Spotify credentials are user supplied. Keep your release signing material private. Apple Music lyrics and artwork are not offered as a default because this source does not include an authorized Apple Music API integration. Third-party sources can be unavailable or change without notice. If a stream fails, the player can skip to the next track by default. The `0.1.5` source still contains some legacy Listen Together and lyric translation code; those are not represented as completed XAN features here.

For build failures, verify that `JAVA_HOME` points to a JDK, `ANDROID_HOME` points to an SDK, and the SDK contains the compile platform declared in `app/build.gradle.kts`. Keep the Gradle wrapper and version catalog in the source archive. Check `dist/` for the output and the console for any missing dependency or signing error.

## Repository layout

```text
xan/
├─ app/                         Main Android application
├─ branding/                    Single-source logo/artwork hot-swap
├─ modules/
│  ├─ music-sources/           Playback/search source integrations
│  ├─ lyrics/                  Lyrics providers
│  ├─ services/                Last.fm, Shazam, Discord/RPC integrations
│  ├─ visuals/                 Canvas/video visual modules
│  └─ experimental/            Preserved modules not currently in the build
├─ tools/
│  ├─ windows/                 Build/update BAT helpers
│  └─ scripts/                 Maintenance utilities
├─ docs/                       Project, module, tool, security and contributor docs
├─ gradle/                     Gradle version catalog and wrapper support
├─ .github/                    CI, templates, actions and repository automation
└─ build.gradle.kts            Root Gradle configuration
```

Gradle module names remain unchanged even though their source folders are grouped under `modules/`. Existing code can continue using dependencies such as `project(":spotify")` and `project(":canvas")`.

Build/service configuration files such as `crowdin.yml`, `renovate.json`, `gradle.properties`, and the Gradle wrapper stay at the repository root because external tools expect conventional locations.

Documentation is collected under `docs/`. Start with `docs/README.md` for the index.

## License and credits

The project retains its GPL-3.0 license and required third-party notices. When distributing a modified build, preserve the applicable license and provide the corresponding source as required by GPL-3.0. Please also credit the original XAN project at https://github.com/bxane-dev/xan.

## Dependency automation

XAN includes a dependency + source compatibility updater. The repository runs `.github/workflows/dependency-updater.yml` every Monday and can also be run manually. It updates dependency versions, applies registered source migrations for known breaking APIs, compile-checks `:app:compileUniversalGmsDebugKotlin`, and only then opens or refreshes the `automation/dependency-updates` pull request. Unknown future API breaks stop the workflow instead of publishing a broken update branch.

For a local Windows clone, run:

```powershell
.\tools\windows\UPDATE-DEPENDENCIES.bat
```

Use `UPDATE-DEPENDENCIES.bat major` only when you intentionally want major-version upgrades.
