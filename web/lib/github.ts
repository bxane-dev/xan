const LATEST_RELEASE_URL =
  "https://api.github.com/repos/bxane-dev/xan/releases/latest";

export const RELEASES_PAGE =
  "https://github.com/bxane-dev/xan/releases/latest";

type GitHubAsset = {
  name?: unknown;
  browser_download_url?: unknown;
  size?: unknown;
};

type GitHubRelease = {
  tag_name?: unknown;
  name?: unknown;
  assets?: unknown;
};

export type ReleaseInfo = {
  version: string;
  releaseName: string;
  downloadUrl: string;
  filename: string;
  size: number | null;
  platform: "Android";
};

function isApkAsset(asset: GitHubAsset) {
  if (
    typeof asset.name !== "string" ||
    typeof asset.browser_download_url !== "string"
  ) {
    return false;
  }

  const name = asset.name.toLowerCase();

  return (
    name.endsWith(".apk") &&
    !/(source|sources|checksum|checksums|sha256|sha512)/i.test(name)
  );
}

export async function getLatestRelease(): Promise<ReleaseInfo> {
  const response = await fetch(LATEST_RELEASE_URL, {
    headers: {
      Accept: "application/vnd.github+json",
      "User-Agent": "xanapkweb",
      "X-GitHub-Api-Version": "2022-11-28",
    },
    next: { revalidate: 300 },
  });

  if (!response.ok) {
    throw new Error(`GitHub release lookup failed: ${response.status}`);
  }

  const release = (await response.json()) as GitHubRelease;

  if (typeof release.tag_name !== "string" || !release.tag_name.trim()) {
    throw new Error("Latest release is missing a version tag.");
  }

  if (!Array.isArray(release.assets)) {
    throw new Error("Latest release has no asset list.");
  }

  const assets = release.assets.filter(
    (asset): asset is GitHubAsset =>
      Boolean(asset) && typeof asset === "object",
  );

  const apk = assets.find(isApkAsset);

  if (
    !apk ||
    typeof apk.name !== "string" ||
    typeof apk.browser_download_url !== "string"
  ) {
    throw new Error("Latest release has no installable Android APK.");
  }

  return {
    version: release.tag_name,
    releaseName:
      typeof release.name === "string" && release.name.trim()
        ? release.name
        : release.tag_name,
    downloadUrl: apk.browser_download_url,
    filename: apk.name,
    size: typeof apk.size === "number" ? apk.size : null,
    platform: "Android",
  };
}
