# Windows Tools

These helper scripts are intended for Windows users working with a local XAN clone.

- `BUILD-RELEASE.bat` — builds the signed `universalGmsRelease` APK and copies it to the repository's `dist\` folder.
- `BUILD-DEBUG.bat` — builds an installable `universalGmsDebug` APK and copies it to `dist\`.
- `UPDATE-REPO.bat` — safely updates the clone to the latest `origin/main` and updates submodules.
- `UPDATE-DEPENDENCIES.bat` — updates dependencies, applies registered source-code migrations, then compile-checks the app.
- `UPDATE-SOURCE.bat` — applies source compatibility migrations and compile-checks without changing dependency versions.

All scripts automatically locate the repository root, so they can be double-clicked directly from this folder.

## Flexible versions

The version you enter is the version used throughout the build.

Supported examples:

```text
v0.5.2
v0.1.6.1
v1.0
v12.34.56.78
```

The leading `v` is optional in the BAT files; they add it automatically.

That one value controls:

- Android `versionName`
- Android `versionCode`
- `BuildConfig.VERSION_NAME`
- updater version comparison
- the output APK filename
- GitHub Release tag builds

The last three numeric components must each be between `0` and `99`. The major component may be `0` through `2099`.

Examples of generated Android version codes:

```text
v0.5.2     -> 50200
v0.1.6.1   -> 10601
v1.0.0     -> 1000000
```

## Release build

Double-click `BUILD-RELEASE.bat` and enter a version, or run:

```powershell
.\tools\windows\BUILD-RELEASE.bat v0.1.6.1
```

The finished APK is placed in:

```text
dist\xan-v0.1.6.1-release.apk
```

## Debug build

With no version argument, debug builds use `dev`. You can also stamp a specific version:

```powershell
.\tools\windows\BUILD-DEBUG.bat v0.1.6.1
```

## Update the repository

Double-click `UPDATE-REPO.bat` or run:

```powershell
.\tools\windows\UPDATE-REPO.bat
```

The updater refuses to overwrite tracked local changes.


## Update dependencies + source

Safe mode keeps dependency updates on the same major version:

```powershell
.\tools\windows\UPDATE-DEPENDENCIES.bat
```

To allow major-version updates too:

```powershell
.\tools\windows\UPDATE-DEPENDENCIES.bat major
```

The updater checks Maven Central, Google Maven, JitPack, and the Gradle Plugin Portal. It then:

1. updates `gradle/libs.versions.toml` and the Gradle wrapper when required;
2. runs `tools/scripts/update_source.ps1` for registered AndroidX/source migrations;
3. compile-checks `:app:compileUniversalGmsDebugKotlin`.

If an upstream dependency introduces an unknown breaking API, the compile check fails and the changes are left for inspection instead of being treated as a safe update.

## Update source compatibility only

```powershell
.\tools\windows\UPDATE-SOURCE.bat
```

This runs registered source migrations against the dependency versions already in the version catalog and verifies the Kotlin compile.
