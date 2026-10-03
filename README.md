<div align="center">

# XAN 🎵

### Your music. Your library. Your way.

**Fast · Fluid · Personal**

<br>

<a href="https://github.com/bxane-dev/xan/releases/latest/download/xan.apk">
  <img src="https://img.shields.io/badge/⚡%20DOWNLOAD-LATEST%20APK-2ea44f?style=for-the-badge&logo=android" alt="Download Latest APK">
</a>

<br><br>

<a href="https://github.com/bxane-dev/xan/releases/latest">
  <img src="https://img.shields.io/github/v/release/bxane-dev/xan?style=for-the-badge&label=LATEST" alt="Latest Release">
</a>
<a href="https://github.com/bxane-dev/xan/releases">
  <img src="https://img.shields.io/github/downloads/bxane-dev/xan/total?style=for-the-badge" alt="Downloads">
</a>
<a href="https://github.com/bxane-dev/xan">
  <img src="https://img.shields.io/github/stars/bxane-dev/xan?style=for-the-badge" alt="Stars">
</a>
<a href="https://github.com/bxane-dev/xan/blob/main/LICENCE">
  <img src="https://img.shields.io/github/license/bxane-dev/xan?style=for-the-badge" alt="License">
</a>

<br><br>

<a href="#-download">Download</a>
 ·  <a href="#-features">Features</a>
 ·  <a href="#-downloads">Downloads</a>
 ·  <a href="#-building">Build</a>
 ·  <a href="#-contributing">Contribute</a>
 ·  <a href="#-security">Security</a>

</div>

---

## 🎧 What is XAN?

**XAN** is an Android music player designed to make your music library feel **fast, fluid, and personal**.

Built around your listening experience, XAN brings playback, discovery, lyrics, playlists, downloads, visual customization, and powerful integrations together in one place.

> **Your music. Your library. Your way.**

---

# 🚀 Download

<div align="center">

### ⚡ Get XAN

<a href="https://github.com/bxane-dev/xan/releases/latest/download/xan.apk">
  <img src="https://img.shields.io/badge/⬇%20DOWNLOAD%20LATEST%20APK-2ea44f?style=for-the-badge" alt="Download Latest APK">
</a>

<br><br>

<a href="https://github.com/bxane-dev/xan/releases/latest">
  Latest Release
</a>
&nbsp;·&nbsp;
<a href="https://github.com/bxane-dev/xan/releases">
  All Releases
</a>

</div>

The latest stable version is always available through GitHub Releases.

---

# ✨ Features

## 🎵 Music

* 🎶 Local music playback
* 🌐 Online music sources
* ▶️ Background playback
* ⏭️ Next / previous controls
* 🔀 Shuffle
* 🔁 Repeat
* 📋 Queue management
* 🎧 Bluetooth support
* 🎛️ Headset and media-button controls
* 🔒 Android lock-screen controls
* 🔔 Notification media controls

---

## 📚 Your Library

Keep everything you listen to in one place.

* 🎵 Songs
* 💿 Albums
* 👤 Artists
* 📑 Playlists
* ❤️ Liked songs
* 📋 Queue
* 🔎 Search
* 🕘 Listening history
* 🌟 Music discovery

---

## 📝 Lyrics

Follow along while you listen.

* 📝 Synced lyrics
* 📄 Plain lyrics
* 🌍 Lyrics translation
* 🔌 Multiple lyric providers
* 🎤 Lyrics-focused playback experience

---

## 🔎 Discover

Find music without leaving XAN.

* 🔍 Search songs
* 👤 Search artists
* 💿 Search albums
* 🎵 Discover tracks
* 🎤 Shazam integration
* 🌐 Online music discovery
* 🎨 Artist and album visuals

---

# 📥 Downloads

### One-click music downloads.

XAN makes supported downloads simple.

### 🎵 Songs

Download individual tracks directly from supported sources.

### 💿 Albums

Save supported albums without downloading every track individually.

### 👤 Artists

Open an artist and download supported releases directly from the artist experience.

### 📚 Playlists

Save supported playlists for offline listening.

### ⚡ One Click

**Find → Download → Listen.**

No complicated workflow.

---

# 🎨 Make It Yours

XAN is built to feel personal.

Customize your listening experience with:

* 🎨 Custom appearance
* 🖼️ Dynamic artwork
* 👤 Artist visuals
* 🎬 Video / canvas-style visuals
* 🌈 Personalized player experience
* ⚙️ Playback customization

Your player should feel like **your** player.

---

# 🔌 Integrations

XAN connects with services you already use.

* 🟢 Spotify playlist importing
* 📊 Last.fm
* 🎤 Shazam
* 💬 Discord integration
* 📝 External lyrics providers
* 🎵 External music sources

Some integrations may require network access, an account, regional availability, or user-provided API credentials.

---

# ⚡ Built to Feel Fast

XAN is designed around a simple principle:

> **The interface should get out of the way of the music.**

Search it.

Play it.

Download it.

Queue it.

Customize it.

Listen.

---

# 🛠️ Building

## Requirements

* Android Studio
* JDK 21 or newer
* Android SDK
* Git
* Internet access for Gradle dependencies

## Clone

```bash
git clone https://github.com/bxane-dev/xan.git
cd xan
```

Open the repository in Android Studio and allow Gradle to synchronize.

## Build

### Windows

```powershell
.\gradlew.bat :app:assembleDebug
```

### Linux / macOS

```bash
./gradlew :app:assembleDebug
```

The debug APK is generated under:

```text
app/build/outputs/apk/debug/
```

---

# 📁 Project Structure

```text
xan/
├── app/                    # Main Android application
│
├── modules/
│   ├── music-sources/      # Music and metadata providers
│   ├── lyrics/             # Lyrics providers
│   ├── services/           # External service integrations
│   └── visuals/            # Visual and canvas providers
│
├── branding/               # XAN branding assets
├── docs/                   # Documentation
├── tools/                  # Development utilities
│
├── BUILD.cmd               # Windows build helper
├── BUILD.ps1               # PowerShell build helper
└── version.properties      # XAN version information
```

---

# 🤝 Contributing

XAN is open source.

Contributions of all kinds are welcome.

### You can help with:

* 🐛 Bug fixes
* ⚡ Performance improvements
* 🎨 UI / UX improvements
* 🎵 Playback improvements
* 📥 Download improvements
* 🔌 New integrations
* 🌍 Translations
* 🧪 Testing
* 📚 Documentation
* 🧹 Refactoring
* 💡 Ideas and improvements

Please read [`CONTRIBUTING.md`](CONTRIBUTING.md) before contributing.

---

# 🔐 Security

Security matters.

**Please do not publicly disclose security vulnerabilities through GitHub Issues.**

If you discover a vulnerability that could affect XAN or its users, please report it privately.

### [🔒 Read the Security Policy](SECURITY.md)

### [GitHub Security](https://github.com/bxane-dev/xan/security)

Security reports should contain enough information to understand and reproduce the issue.

Please never include passwords, private API keys, signing keys, or other sensitive credentials in a report.

---

# 📜 License

XAN is distributed under the **MIT License**.

**SPDX-License-Identifier: MIT**

See [`LICENCE`](LICENCE) for the complete license text.

---

# 🌐 Community & Links

<div align="center">

<a href="https://github.com/bxane-dev/xan">
  <img src="https://img.shields.io/badge/GitHub-Source-181717?style=for-the-badge&logo=github" alt="GitHub">
</a>

<a href="https://github.com/bxane-dev/xan/releases">
  <img src="https://img.shields.io/badge/Releases-Download-181717?style=for-the-badge&logo=github" alt="Releases">
</a>

<a href="https://guns.lol/bxane">
  <img src="https://img.shields.io/badge/bxane-Profile-5865F2?style=for-the-badge" alt="bxane">
</a>

<a href="https://ko-fi.com/bxane">
  <img src="https://img.shields.io/badge/Ko--fi-Support-FF5E5B?style=for-the-badge&logo=ko-fi" alt="Ko-fi">
</a>

</div>

---

<div align="center">

# 🎵 XAN

### Fast. Fluid. Personal.

**Built for people who care about their music.**

<br>

Made with ♥ by **bxane**

</div>
