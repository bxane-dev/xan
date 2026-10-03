import { NextResponse } from "next/server";
import { getLatestRelease, RELEASES_PAGE } from "@/lib/github";

export const dynamic = "force-dynamic";

export async function GET(request: Request) {
  try {
    const release = await getLatestRelease();
    return NextResponse.redirect(release.downloadUrl, 307);
  } catch (error) {
    console.error("Download resolution failed", error);
    return NextResponse.redirect(new URL(RELEASES_PAGE, request.url), 307);
  }
}
