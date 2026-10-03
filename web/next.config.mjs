/** @type {import("next").NextConfig} */
const nextConfig = {
  outputFileTracingIncludes: { "/api/youtube/audio": ["./bin/yt-dlp*"] },
};
export default nextConfig;
