import { NextResponse } from "next/server";
import { getYouTube } from "@/lib/youtube";

export const runtime = "nodejs";
export const dynamic = "force-dynamic";
export const maxDuration = 20;

export async function GET(request: Request) {
  const query = new URL(request.url).searchParams.get("q")?.trim();

  if (!query || query.length < 2 || query.length > 120) {
    return NextResponse.json(
      { error: "Search must be between 2 and 120 characters." },
      { status: 400 },
    );
  }

  try {
    const youtube = await getYouTube();
    const result = await youtube.music.search(query, { type: "song" });
    const shelf = result.songs ?? result.videos;
    const items = (shelf?.contents ?? [])
      .filter((item) => Boolean(item.id && item.title))
      .slice(0, 8)
      .map((item) => ({
        id: item.id as string,
        title: item.title ?? "Untitled",
        artist:
          item.artists?.map((artist) => artist.name).filter(Boolean).join(", ") ||
          item.author?.name ||
          "YouTube Music",
        thumbnail:
          item.thumbnails?.at(-1)?.url ||
          item.thumbnails?.[0]?.url ||
          `https://i.ytimg.com/vi/${item.id}/hqdefault.jpg`,
        duration: item.duration?.seconds ?? 0,
      }));

    return NextResponse.json(
      { items },
      {
        headers: {
          "Cache-Control": "public, s-maxage=120, stale-while-revalidate=300",
        },
      },
    );
  } catch (error) {
    console.error("YouTube Music search failed", error);
    return NextResponse.json(
      { error: "YouTube Music search is temporarily unavailable." },
      { status: 503 },
    );
  }
}
