# Modules

XAN is a Gradle multi-module Android project. Modules are grouped by purpose while keeping their existing Gradle project names stable.

## music-sources

- `innertube` — YouTube Music / InnerTube integration.
- `kugou` — Kugou integration.
- `spotify` — Spotify integration.

## lyrics

- `lrclib`
- `betterlyrics`
- `simpmusic`
- `youlyplus`
- `paxsenixlyrics`
- `musixmatchlyrics`

## services

- `lastfm`
- `shazamkit`
- `kizzy`

## visuals

- `canvas`
- `artistvideo`
- `applecanvas`
- `xancanvas`

## experimental

`lyricsProvider` is preserved here because it exists as a Gradle-style module but is not currently included by `settings.gradle.kts`.
