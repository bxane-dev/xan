import { chmod, mkdir, rename, readFile, writeFile } from "node:fs/promises";
import { resolve } from "node:path";
import { createHash } from "node:crypto";

const asset = process.platform === "win32" ? "yt-dlp.exe" : process.platform === "darwin" ? "yt-dlp_macos" : "yt-dlp_linux";
const target = resolve("bin", process.platform === "win32" ? "yt-dlp.exe" : "yt-dlp");
const base = "https://github.com/yt-dlp/yt-dlp/releases/latest/download";
const response = await fetch(`${base}/SHA2-256SUMS`);
if (!response.ok) throw new Error(`yt-dlp checksum lookup failed: ${response.status}`);
const checksum = (await response.text()).split(/\r?\n/).find(line => line.endsWith(`  ${asset}`))?.split(/\s+/)[0];
if (!checksum) throw new Error(`Missing checksum for ${asset}`);
const hash = bytes => createHash("sha256").update(bytes).digest("hex");
let existing;
try { existing = await readFile(target); } catch {}
if (!existing || hash(existing) !== checksum) {
  const binary = await fetch(`${base}/${asset}`);
  if (!binary.ok) throw new Error(`yt-dlp download failed: ${binary.status}`);
  const bytes = Buffer.from(await binary.arrayBuffer());
  if (hash(bytes) !== checksum) throw new Error("yt-dlp checksum mismatch");
  await mkdir(resolve("bin"), { recursive: true });
  await writeFile(`${target}.tmp`, bytes);
  await rename(`${target}.tmp`, target);
}
if (process.platform !== "win32") await chmod(target, 0o755);
console.log(`Verified ${asset} ready`);
