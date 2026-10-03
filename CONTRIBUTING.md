# Contributing to XAN 🤝

Thank you for wanting to contribute to **XAN**.

XAN is built in the open, and every contribution helps — whether you're fixing a small bug, improving the UI, adding a feature, translating the app, or improving the documentation.

> **Build something useful. Make it better. Share it.**

---

# 🔄 Contributor Workflow

The recommended workflow is:

```text
Issue / Idea
     ↓
Discuss
     ↓
Fork / Branch
     ↓
Implement
     ↓
Test
     ↓
Commit
     ↓
Pull Request
     ↓
Review
     ↓
Changes
     ↓
Merge
```

## 1. Find Something to Work On

Start by checking the existing:

* [Open Issues](https://github.com/bxane-dev/xan/issues)
* [Pull Requests](https://github.com/bxane-dev/xan/pulls)
* Documentation
* Existing discussions

If you're planning a larger change, discussing the idea before implementing it can help avoid duplicated work.

For small fixes, you can generally start working immediately.

---

## 2. Get the Code

Fork the repository on GitHub and clone your fork:

```bash
git clone https://github.com/YOUR_USERNAME/xan.git
cd xan
```

Add the upstream repository:

```bash
git remote add upstream https://github.com/bxane-dev/xan.git
```

Verify your remotes:

```bash
git remote -v
```

---

## 3. Create a Branch

Create a dedicated branch for your contribution.

```bash
git checkout -b feature/my-change
```

Examples:

```text
feature/lyrics-cache
feature/artist-downloads
fix/player-crash
fix/download-error
ui/player-redesign
docs/contributing
i18n/german
```

Keep branch names short and descriptive.

---

## 4. Make Your Changes

Implement the change while keeping the scope focused.

Try to:

* Follow existing project patterns.
* Reuse existing components where appropriate.
* Keep code readable.
* Avoid unrelated refactoring.
* Avoid introducing unnecessary dependencies.
* Keep user-facing behavior predictable.

If you discover another unrelated problem while working, consider opening a separate issue rather than expanding the current change.

---

## 5. Test Your Changes

Before committing, build the project:

### Windows

```powershell
.\gradlew.bat :app:assembleDebug
```

### Linux / macOS

```bash
./gradlew :app:assembleDebug
```

Then test the affected functionality on an emulator or physical Android device when possible.

Check:

* Normal behavior
* Edge cases
* Error states
* Existing functionality
* Different screen sizes when relevant
* Dark/light themes when relevant
* Offline behavior when relevant

For bug fixes, make sure you can reproduce the original problem before the fix and verify that it no longer occurs afterward.

---

## 6. Review Your Changes

Before committing:

```bash
git status
```

Review the actual diff:

```bash
git diff
```

Make sure you haven't accidentally included:

* Secrets
* API keys
* Passwords
* Signing credentials
* Build artifacts
* IDE files
* Temporary files
* Unrelated changes

---

## 7. Commit

Use a clear commit message describing the change.

Examples:

```text
Fix player notification controls
```

```text
Add artist download support
```

```text
Improve lyrics loading
```

```text
Add German translations
```

Avoid vague messages such as:

```text
fix stuff
```

or:

```text
changes
```

Keep commits reasonably focused so the history remains understandable.

---

## 8. Push Your Branch

Push your branch to your fork:

```bash
git push -u origin feature/my-change
```

GitHub will provide an option to open a pull request.

---

## 9. Open a Pull Request

Create a pull request against the XAN repository.

Your pull request should explain:

### What changed?

Briefly describe the implementation.

### Why?

Explain the problem or motivation.

### How was it tested?

Mention the device, emulator, build command, or tests used.

### Screenshots

For UI changes, include screenshots or a short recording when useful.

---

## 10. Review

A contribution may receive review comments or requests for changes.

That's normal.

If changes are requested:

1. Make the requested changes on the same branch.
2. Test them again.
3. Commit the updates.
4. Push the branch again.

The pull request will update automatically.

You do **not** need to create a new pull request for every review change.

---

## 11. Keep Your Branch Updated

If your branch becomes outdated, update it with the latest upstream changes.

```bash
git fetch upstream
```

Then update your branch using the project's preferred Git workflow.

Resolve any conflicts carefully and test the project again afterward.

---

## 12. Merge 🎉

Once the pull request has been reviewed and approved, it can be merged into XAN.

After your contribution is merged, you can safely remove your local feature branch:

```bash
git branch -d feature/my-change
```

And remove the remote branch from your fork if desired:

```bash
git push origin --delete feature/my-change
```

---

# 🌟 What You Can Contribute

There are many ways to help XAN grow.

### 🐛 Bug Fixes

* Fix crashes
* Fix playback issues
* Fix UI problems
* Fix download issues
* Fix synchronization problems
* Improve error handling

### ✨ Features

* New playback features
* New download functionality
* New integrations
* New customization options
* New discovery features
* Better library management

### 🎨 UI / UX

* Improve layouts
* Improve animations
* Improve accessibility
* Improve navigation
* Improve player controls
* Improve responsive behavior
* Improve visual consistency

### 🌍 Translations

* Add new languages
* Improve existing translations
* Fix incorrect translations
* Improve wording and consistency

### ⚡ Performance

* Reduce unnecessary work
* Improve startup time
* Reduce memory usage
* Improve playback performance
* Optimize network operations
* Improve scrolling and rendering

### 📚 Documentation

* Improve setup instructions
* Fix incorrect information
* Add examples
* Improve developer documentation
* Fix typos

---

# 🛠️ Getting Started

## Requirements

You'll generally need:

* Android Studio
* JDK 21+
* Android SDK
* Git

Clone the repository:

```bash
git clone https://github.com/YOUR_USERNAME/xan.git
cd xan
```

Open the project in Android Studio and allow Gradle to synchronize.

---

# 🎨 UI Contributions

When changing the interface, try to keep XAN's visual language consistent.

Consider:

* Dark and light themes
* Different screen sizes
* Accessibility
* Touch targets
* Text scaling
* Loading states
* Empty states
* Error states
* Animations and transitions
* Performance

Avoid adding visual complexity simply for the sake of adding it.

---

# 🔌 Integrations & Providers

When contributing a new integration or provider:

* Follow the existing architecture.
* Keep provider-specific logic isolated.
* Handle unavailable services gracefully.
* Handle network failures properly.
* Avoid exposing credentials.
* Respect the terms and policies of the service being integrated.

---

# 🔐 Security

**Do not report security vulnerabilities through public GitHub Issues.**

If you discover a security vulnerability, please follow the process described in [`SECURITY.md`](SECURITY.md).

Never commit:

* API keys
* Passwords
* Access tokens
* Signing keys
* Private certificates
* Personal information
* Other secrets

---

# 💬 Issues

Before opening an issue, check whether a similar issue already exists.

### Bug Reports

Include:

* What happened
* What you expected
* Steps to reproduce
* XAN version
* Android version
* Device
* Relevant logs or screenshots

### Feature Requests

Explain:

* What you'd like to see
* Why it would be useful
* How you imagine it working

Clear information makes issues much easier to understand.

---

# 📦 Pull Request Checklist

Before submitting your pull request:

* [ ] I created a focused branch.
* [ ] I tested my changes.
* [ ] The project builds successfully.
* [ ] I checked the complete diff.
* [ ] I didn't commit secrets or credentials.
* [ ] I updated documentation where necessary.
* [ ] I added screenshots for relevant UI changes.
* [ ] I followed existing project conventions.
* [ ] My pull request has a clear description.
* [ ] I checked for unrelated changes.

---

# 🧭 Code Style

Follow the conventions already used in the part of the project you're changing.

Prefer:

* Clear names
* Small focused functions
* Readable code
* Existing project patterns
* Minimal unnecessary abstraction

Avoid:

* Unrelated refactoring
* Large formatting-only changes
* Duplicate implementations
* Hard-coded secrets
* Unnecessary dependencies

---

# ❤️ Community

Please be respectful when contributing.

Good contributions can come from people with very different levels of experience.

Constructive feedback is encouraged.

Harassment, personal attacks, discrimination, and intentionally disruptive behavior are not welcome.

---

# 📜 License

By contributing to XAN, you agree that your contributions are provided under the project's **MIT License**.

See [`LICENCE`](LICENCE) for the complete license text.

---

<div align="center">

## 🎵 Thank You

Every contribution matters.

Whether you submit a pull request, report a bug, improve a translation, or help someone else use XAN — **thank you.**

**Build something useful.**

**Make it better.**

**Keep the music playing.**

</div>
