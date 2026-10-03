import { Innertube } from "youtubei.js";

const globalForYouTube = globalThis as typeof globalThis & {
  xanYouTube?: Promise<Innertube>;
};

export function getYouTube() {
  if (!globalForYouTube.xanYouTube) {
    globalForYouTube.xanYouTube = Innertube.create({
      lang: "en",
      location: "US",
    }).catch((error) => {
      globalForYouTube.xanYouTube = undefined;
      throw error;
    });
  }

  return globalForYouTube.xanYouTube;
}
