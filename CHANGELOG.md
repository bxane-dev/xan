# Changelog

## 0.6.7

- Reduced the installed launcher mark by about 20% and the APK installer mark by 50% across adaptive, themed, and legacy icons.

## 0.6.6

### Navigation
- Added **Appearance → Navigation style** with two choices:
  - **Magnet** (default): uses the XAN navigation magnet.
  - **Basic / Legacy**: restores the classic floating navigation buttons.
- The Magnet is shown only when Magnet navigation is selected.
- The Magnet position preference continues to support bottom-left or bottom-right placement.

### Player
- Fixed the mini-player disappearing in the collapsed state. A downward swipe still dismisses it until the track changes.
- Widened the Magnet-mode mini-player to fill the space beside the Magnet, leaving a three-pixel gap on either side setting.
- Hide the Magnet while the full-screen player is open and restore it when the player collapses.
- Magnet mode now centers the mini-player vertically with the 56dp Magnet button.
- In Magnet mode the mini-player is anchored to the side opposite the Magnet with a reserved touch zone, preventing overlap.
- Swiping the mini-player downward dismisses it in both Magnet and Basic / Legacy styles.
- Basic / Legacy mode restores the 0.6.5 player spacing and placement alongside the classic navigation buttons.
- Switching navigation style changes the navigation and player layout together to avoid mixed layouts.

### App icon
- Split launcher and installer icon resources so their sizes can be tuned independently.
- Balanced the installed launcher and Play Protect / installer icon sizes across adaptive and legacy icons.

### Release
- Version: **0.6.6**
