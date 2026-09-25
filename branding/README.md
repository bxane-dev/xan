# XAN Branding Hot-Swap

This folder contains the XAN master mark, its supplied round variant, and the bundled bxane profile photo.

## One-file swap

Replace exactly this file:

`branding/hotswap/android/res/drawable-nodpi/xan_mark.webp`

`xan_round_mark.webp` is used for the round launcher icon. `bxane_profile.jpg` is used in About.

Keep the filename `xan_mark.webp`, then rebuild the APK. PNG is also supported.

Recommended source:
- transparent PNG or WebP
- square canvas
- 512×512 or 1024×1024
- centered mark with safe padding
- no baked-in launcher background

The Android project imports the hot-swap resource folder directly. There is no manual copy step and no second SVG/vector copy to keep in sync.

## Surfaces driven by the master logo

The shared `@drawable/xan_mark` resource is used for:
- launcher/app icon
- APK/install icon
- round launcher icon
- adaptive launcher foreground
- Android themed/monochrome icon
- starting/onboarding experience
- in-app XAN branding
- missing song artwork
- missing album artwork
- playlist/artwork fallbacks
- notification icon
- Android TV banner
- widget fallback art

A user-picked playlist cover or real song/album artwork still overrides the fallback, as expected.

## Quick replacement on Windows

From the repository root:

```powershell
.\branding\replace-logo.ps1 "C:\path\to\new-logo.png"
```

Then rebuild the APK.


## Rule

Use `xan_mark.webp` for general branding and artwork fallbacks. Use `xan_round_mark.webp` only for the round launcher shape.
