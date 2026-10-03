import { NextResponse } from "next/server";
import { getLatestRelease } from "@/lib/github";

export const dynamic = "force-dynamic";

export async function GET() {
  try {
    const release = await getLatestRelease();

    return NextResponse.json(release, {
      headers: {
        "Cache-Control": "public, s-maxage=300, stale-while-revalidate=3600",
      },
    });
  } catch (error) {
    console.error("Release lookup failed", error);

    return NextResponse.json(
      { error: "Could not retrieve the latest XAN build." },
      { status: 503 },
    );
  }
}
