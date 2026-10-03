import { execFile } from "node:child_process";
import { promisify } from "node:util";
import { resolve } from "node:path";
import { NextResponse } from "next/server";

export const runtime = "nodejs";
export const dynamic = "force-dynamic";
export const maxDuration = 60;

const VIDEO_ID = /^[A-Za-z0-9_-]{11}$/;
const execFileAsync = promisify(execFile);
const YT_DLP = resolve(process.cwd(), process.platform === "win32" ? "bin/yt-dlp.exe" : "bin/yt-dlp");

async function resolveAudioUrl(id: string) {
  const target = `https://www.youtube.com/watch?v=${id}`;

  const { stdout, stderr } = await execFileAsync(
    YT_DLP,
    [
      "--get-url",
      "--format",
      "bestaudio[protocol^=https]/bestaudio/best",
      "--no-playlist",
      "--no-warnings",
      "--quiet",
      "--js-runtimes",
      `node:${process.execPath}`,
      target,
    ],
    {
      timeout: 35_000,
      maxBuffer: 1024 * 1024 * 4,
      env: {
        ...process.env,
        PATH: `${process.env.PATH ?? ""}:${resolve(process.execPath, "..")}`,
      },
    },
  );

  if (stderr?.trim()) {
    console.warn("yt-dlp stderr", stderr.trim().slice(0, 2000));
  }

  const url = stdout
    .split(/\r?\n/)
    .map((line) => line.trim())
    .find((line) => /^https?:\/\//i.test(line));

  if (!url) {
    throw new Error("yt-dlp did not return a playable audio URL.");
  }

  return url;
}

async function proxyAudio(request: Request, id: string) {
  const streamUrl = await resolveAudioUrl(id);
  const headers = new Headers();
  const range = request.headers.get("range");

  if (range) headers.set("Range", range);

  headers.set(
    "User-Agent",
    request.headers.get("user-agent") ||
      "Mozilla/5.0 (Linux; Android 16) AppleWebKit/537.36 Chrome/140 Safari/537.36",
  );
  headers.set("Referer", "https://www.youtube.com/");

  const upstream = await fetch(streamUrl, {
    headers,
    redirect: "follow",
    cache: "no-store",
  });

  if (!upstream.ok && upstream.status !== 206) {
    throw new Error(`YouTube audio upstream returned ${upstream.status}`);
  }

  const responseHeaders = new Headers();

  for (const name of [
    "content-type",
    "content-length",
    "content-range",
    "accept-ranges",
    "etag",
    "last-modified",
  ]) {
    const value = upstream.headers.get(name);
    if (value) responseHeaders.set(name, value);
  }

  if (!responseHeaders.has("content-type")) {
    responseHeaders.set("content-type", "audio/webm");
  }

  responseHeaders.set("Cache-Control", "private, no-store");
  responseHeaders.set("X-Content-Type-Options", "nosniff");

  return new Response(upstream.body, {
    status: upstream.status,
    headers: responseHeaders,
  });
}

export async function GET(request: Request) {
  const url = new URL(request.url);
  const id = url.searchParams.get("id")?.trim();

  if (!id || !VIDEO_ID.test(id)) {
    return NextResponse.json({ error: "Invalid video id." }, { status: 400 });
  }

  try {
    if (url.searchParams.get("probe") === "1") {
      const streamUrl = await resolveAudioUrl(id);
      const upstream = await fetch(streamUrl, {
        headers: {
          Range: "bytes=0-1",
          "User-Agent":
            request.headers.get("user-agent") ||
            "Mozilla/5.0 (Linux; Android 16) AppleWebKit/537.36 Chrome/140 Safari/537.36",
          Referer: "https://www.youtube.com/",
        },
        redirect: "follow",
        cache: "no-store",
      });

      return NextResponse.json({
        ok: upstream.ok || upstream.status === 206,
        status: upstream.status,
        contentType: upstream.headers.get("content-type"),
      });
    }

    return await proxyAudio(request, id);
  } catch (error) {
    console.error("XAN yt-dlp audio stream failed", {
      message: error instanceof Error ? error.message : String(error),
      code:
        typeof error === "object" && error && "code" in error
          ? String((error as { code?: unknown }).code)
          : undefined,
    });

    return NextResponse.json(
      { error: "Audio stream is temporarily unavailable." },
      { status: 503 },
    );
  }
}
