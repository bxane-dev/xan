# XAN changelog

## 0.1.1

### Fixed

- Reworked Shazam capture to use a stable PCM16 resampler independent of Media3's changing `AudioProcessor` flush API.
- Added microphone initialization, read-error, and minimum-capture validation for more reliable recognition.
- Fixed Spotify Web login navigation, cookie clearing, and WebView compatibility; added loading and retry feedback.
- Added the supplied transparent XAN PNG to the starter experience at a 30% smaller display size.
- Updated the app and launcher branding to the supplied XAN WebP mark.

## 0.1.0

### Changed

- Built the Android app from the XAN `0.1.5` UI source.
- Applied the supplied XAN launcher art, round launcher art, and bxane profile photo.
- Made liked-song downloads and skip-on-playback-error enabled by default.
- Set the About developer label to bxane and linked the supplied community and supporter pages.
- Set the app's release version to 0.1.0 and added root Windows release and debug builders.

### Fixed

- Fixed a startup/player crash on Android 16 caused by passing XML layer-list fallbacks to Compose `painterResource`; the UI now uses the bundled PNG artwork.
- Added a build check that rejects unsupported XML drawable types at Compose image call sites.
- Removed stale JioSaavn menu references that prevented Kotlin compilation.
- Corrected the selected-songs download action's media type conversion.
- Pointed update fallbacks to the actual release page instead of a guessed APK filename.
