"use client";

import { useEffect, useState } from "react";

type ReleaseInfo = {
  version: string;
  filename: string;
  size: number | null;
  platform: "Android";
};

function formatSize(bytes: number | null) {
  if (!bytes) return null;
  const mb = bytes / 1024 / 1024;
  return `${mb.toFixed(mb >= 10 ? 0 : 1)} MB`;
}

export function DownloadButton() {
  const [release, setRelease] = useState<ReleaseInfo | null>(null);
  const [failed, setFailed] = useState(false);

  useEffect(() => {
    const controller = new AbortController();

    fetch("/api/release", { signal: controller.signal })
      .then(async (response) => {
        if (!response.ok) throw new Error("release lookup failed");
        return (await response.json()) as ReleaseInfo;
      })
      .then((data) => setRelease(data))
      .catch((error) => {
        if (error?.name !== "AbortError") setFailed(true);
      });

    return () => controller.abort();
  }, []);

  const size = release ? formatSize(release.size) : null;

  return (
    <div className="flex flex-col items-center gap-4 sm:items-start">
      <div className="flex flex-col gap-3 sm:flex-row">
        <a
          id="download"
          href="/api/download"
          className="download-button group"
          aria-label="Download the latest XAN APK"
        >
          <span>Download APK</span>
          <svg
            viewBox="0 0 24 24"
            aria-hidden="true"
            className="h-4 w-4 transition-transform duration-300 group-hover:translate-y-0.5"
          >
            <path
              d="M12 3v12m0 0 5-5m-5 5-5-5M5 21h14"
              fill="none"
              stroke="currentColor"
              strokeWidth="1.8"
              strokeLinecap="round"
              strokeLinejoin="round"
            />
          </svg>
        </a>

        <a
          href="https://github.com/bxane-dev/xan"
          target="_blank"
          rel="noreferrer"
          className="secondary-button"
        >
          View GitHub
        </a>
      </div>

      <p className="download-help">On Android, open the APK and allow your browser to install apps when asked.</p>
      <div
        className="min-h-5 text-center text-xs tracking-[0.08em] text-white/42 sm:text-left"
        aria-live="polite"
      >
        {release ? (
          <>
            {release.version} · Android · Free &amp; Open Source
            {size ? ` · ${size}` : ""}
          </>
        ) : failed ? (
          <>
            Couldn&apos;t retrieve the latest build.{" "}
            <a
              className="text-white/70 underline decoration-white/20 underline-offset-4 hover:text-white"
              href="https://github.com/bxane-dev/xan/releases/latest"
              target="_blank"
              rel="noreferrer"
            >
              View latest release
            </a>
          </>
        ) : (
          "Checking the latest release…"
        )}
      </div>
    </div>
  );
}
