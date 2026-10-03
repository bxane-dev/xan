# 🔐 Security Policy

Security matters to **XAN**.

We appreciate responsible security research and encourage anyone who discovers a vulnerability to report it privately so it can be investigated and addressed safely.

---

## 🚨 Reporting a Vulnerability

**Please do not report security vulnerabilities through public GitHub Issues, discussions, or pull requests.**

Use GitHub's private security reporting functionality for the **XAN** repository:

**[Report a vulnerability privately](https://github.com/bxane-dev/xan/security/advisories/new)**

Private reports help prevent vulnerabilities from being publicly exposed before a fix is available.

---

## 📋 What to Include

A useful security report should contain as much of the following information as possible:

* A clear description of the vulnerability
* The affected component or functionality
* Steps to reproduce the issue
* The expected behavior
* The actual behavior
* The potential security impact
* A proof of concept, when appropriate
* XAN version affected
* Android version and device information when relevant
* Relevant logs, screenshots, or other technical details

Please avoid including real credentials, private information, or other sensitive data in the report.

---

## 🎯 Scope

Security reports involving XAN may include issues such as:

### 🔑 Authentication & Authorization

* Unauthorized access
* Privilege escalation
* Authentication bypasses
* Improper permission handling

### 🔒 Sensitive Information

* Exposure of personal information
* Credential leakage
* Token or session leakage
* Insecure local storage

### 🌐 Network & Communication

* Insecure network communication
* Certificate validation issues
* Man-in-the-middle vulnerabilities
* Unsafe handling of remote data

### 📦 Downloads & Media

* Malicious file handling
* Path traversal
* Unsafe file extraction
* Arbitrary file access
* Remote content leading to unintended code execution

### 🧩 Dependencies & Integrations

* Vulnerable third-party dependencies
* Unsafe provider integrations
* Improper handling of untrusted API responses
* Injection vulnerabilities

### ⚙️ Application Security

* Remote code execution
* Arbitrary code execution
* Intent or component abuse
* WebView vulnerabilities
* Unsafe deserialization
* Other issues that could compromise the application or user data

---

## 🧪 Responsible Testing

Only test security issues against systems, accounts, services, and data that you are authorized to test.

Please avoid:

* Accessing another person's private information
* Modifying or deleting other users' data
* Disrupting services
* Performing destructive tests
* Social engineering users or contributors
* Publicly disclosing an unfixed vulnerability

Security research should minimize impact and avoid unnecessary exposure of user data.

---

## 🤝 Responsible Disclosure

When a vulnerability is reported privately, the goal is to:

1. Understand and reproduce the issue.
2. Determine its security impact.
3. Develop an appropriate fix or mitigation.
4. Validate the fix.
5. Coordinate public disclosure when appropriate.

Disclosure timing may depend on the severity of the issue, availability of a fix, and the potential risk to users.

---

## 📢 Public Disclosure

Please do not publicly disclose a security vulnerability until there has been reasonable opportunity to address it.

Once an issue has been resolved, relevant security information may be published so users and contributors can understand the impact and the available mitigation.

---

## 🛡️ Supported Versions

Security fixes are generally focused on actively maintained versions of XAN.

Older or unsupported releases may not receive security updates.

For the latest supported release, see:

**[XAN Releases](https://github.com/bxane-dev/xan/releases)**

---

## 🔄 Keep XAN Updated

Using an up-to-date version of XAN helps ensure that available fixes and security improvements are applied.

**[Download the latest release](https://github.com/bxane-dev/xan/releases/latest)**

---

## ❤️ Thank You

Responsible security researchers make open-source software safer for everyone.

Thank you for taking the time to report vulnerabilities responsibly and for helping protect **XAN** and its users.

<div align="center">

**🔐 Stay safe. Keep XAN secure. 🎵**

</div>

The XAN project is licensed under the [MIT License](LICENCE).
