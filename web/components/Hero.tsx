"use client";

import { DownloadButton } from "@/components/DownloadButton";
import { SpinnableLogo } from "@/components/SpinnableLogo";
import { motion, useReducedMotion } from "motion/react";
import { FormEvent, useEffect, useRef, useState } from "react";

const ease = [0.16, 1, 0.3, 1] as const;

type SearchTrack = {
  id: string;
  title: string;
  artist: string;
  thumbnail: string;
  duration: number;
};

type EqPresetEvent = CustomEvent<{
  name: string;
  gains: number[];
}>;

type YouTubePlayer = {
  destroy: () => void;
  loadVideoById: (videoId: string) => void;
  playVideo: () => void;
  pauseVideo: () => void;
  seekTo: (seconds: number, allowSeekAhead: boolean) => void;
  setVolume: (volume: number) => void;
  getCurrentTime: () => number;
  getDuration: () => number;
};

type YouTubeNamespace = {
  Player: new (
    target: HTMLElement,
    options: {
      width: string;
      height: string;
      playerVars: Record<string, string | number>;
      events: {
        onReady: () => void;
        onStateChange: (event: { data: number }) => void;
        onError?: (event: { data: number }) => void;
      };
    },
  ) => YouTubePlayer;
  PlayerState: {
    ENDED: number;
    PLAYING: number;
    PAUSED: number;
  };
};

declare global {
  interface Window {
    YT?: YouTubeNamespace;
    onYouTubeIframeAPIReady?: () => void;
  }
}

function formatTime(value: number) {
  if (!Number.isFinite(value) || value < 0) return "0:00";

  const minutes = Math.floor(value / 60);
  const seconds = Math.floor(value % 60)
    .toString()
    .padStart(2, "0");

  return `${minutes}:${seconds}`;
}

export function Hero() {
  const sectionRef = useRef<HTMLElement>(null);
  const playerHostRef = useRef<HTMLDivElement>(null);
  const playerRef = useRef<YouTubePlayer | null>(null);
  const playerInitializingRef = useRef(false);
  const mountedRef = useRef(false);
  const queueRef = useRef<SearchTrack[]>([]);
  const indexRef = useRef(-1);
  const reduced = useReducedMotion();

  const [query, setQuery] = useState("");
  const [results, setResults] = useState<SearchTrack[]>([]);
  const [queue, setQueue] = useState<SearchTrack[]>([]);
  const [currentIndex, setCurrentIndex] = useState(-1);
  const [isSearching, setIsSearching] = useState(false);
  const [searchError, setSearchError] = useState("");
  const [playerError, setPlayerError] = useState("");
  const [playerReady, setPlayerReady] = useState(false);
  const [isPlaying, setIsPlaying] = useState(false);
  const [isBuffering, setIsBuffering] = useState(false);
  const [elapsed, setElapsed] = useState(0);
  const [duration, setDuration] = useState(0);
  const [volume, setVolume] = useState(72);
  const [activePreset, setActivePreset] = useState("XAN Signature");

  const currentTrack = currentIndex >= 0 ? queue[currentIndex] : null;

  useEffect(() => {
    queueRef.current = queue;
  }, [queue]);

  useEffect(() => {
    indexRef.current = currentIndex;
  }, [currentIndex]);

  useEffect(() => {
    const handlePreset = (event: Event) => {
      const detail = (event as EqPresetEvent).detail;
      if (detail?.name) setActivePreset(detail.name);
    };

    window.addEventListener("xan:eq-preset", handlePreset);
    return () => window.removeEventListener("xan:eq-preset", handlePreset);
  }, []);

  useEffect(() => {
    mountedRef.current = true;

    return () => {
      mountedRef.current = false;
      playerRef.current?.destroy();
      playerRef.current = null;
      playerInitializingRef.current = false;
    };
  }, []);

  const makePlayer = () => {
    if (
      !mountedRef.current ||
      !playerHostRef.current ||
      playerRef.current ||
      playerInitializingRef.current ||
      !window.YT?.Player
    ) {
      return;
    }

    playerInitializingRef.current = true;

    // YT.Player replaces the target node. Keep that node inside an unmanaged
    // wrapper so React never loses ownership of the visible XAN player card.
    const target = document.createElement("div");
    target.style.width = "100%";
    target.style.height = "100%";
    playerHostRef.current.replaceChildren(target);

    playerRef.current = new window.YT.Player(target, {
      width: "640",
      height: "360",
      playerVars: {
        controls: 1,
        playsinline: 1,
        rel: 0,
        modestbranding: 1,
        origin: window.location.origin,
      },
      events: {
        onReady: () => {
          playerInitializingRef.current = false;
          playerRef.current?.setVolume(volume);
          if (mountedRef.current) setPlayerReady(true);
        },
        onStateChange: (event) => {
          if (!mountedRef.current || !window.YT) return;

          setIsBuffering(event.data === 3);
          if (event.data === window.YT.PlayerState.PLAYING) {
            setIsPlaying(true);
            setPlayerError("");
          } else if (event.data === window.YT.PlayerState.PAUSED) {
            setIsPlaying(false);
          } else if (event.data === window.YT.PlayerState.ENDED) {
            setIsPlaying(false);
            const nextIndex = indexRef.current + 1;
            if (nextIndex < queueRef.current.length) {
              setCurrentIndex(nextIndex);
            }
          }
        },
        onError: () => {
          if (!mountedRef.current) return;
          setIsPlaying(false);
          setIsBuffering(false);
          setPlayerError("YouTube could not play this result. Try another song.");
        },
      },
    });
  };

  const ensurePlayer = () => {
    if (playerRef.current || playerInitializingRef.current) return;

    if (window.YT?.Player) {
      makePlayer();
      return;
    }

    const existing = document.querySelector<HTMLScriptElement>(
      'script[src="https://www.youtube.com/iframe_api"]',
    );

    const previousReady = window.onYouTubeIframeAPIReady;
    window.onYouTubeIframeAPIReady = () => {
      previousReady?.();
      makePlayer();
    };

    if (!existing) {
      const script = document.createElement("script");
      script.src = "https://www.youtube.com/iframe_api";
      script.async = true;
      script.onerror = () => { setPlayerError("YouTube could not load. Check your connection and try again."); script.remove(); };
      document.head.appendChild(script);
      window.setTimeout(() => {
        if (mountedRef.current && (!playerRef.current || playerInitializingRef.current)) {
          setPlayerError("YouTube is taking too long to load. Check your connection and try another track.");
        }
      }, 15000);
    }
  };

  useEffect(() => {
    if (!currentTrack) return;

    setPlayerError("");
    setElapsed(0);
    setDuration(currentTrack.duration || 0);

    if (!playerRef.current) {
      ensurePlayer();
      return;
    }

    if (playerReady) {
      playerRef.current.loadVideoById(currentTrack.id);
    }
  }, [currentTrack?.id, playerReady]);

  useEffect(() => {
    if (!playerReady) return;

    const timer = window.setInterval(() => {
      const player = playerRef.current;
      if (!player) return;

      const nextElapsed = player.getCurrentTime();
      const nextDuration = player.getDuration();

      if (Number.isFinite(nextElapsed)) setElapsed(nextElapsed);
      if (Number.isFinite(nextDuration) && nextDuration > 0) {
        setDuration(nextDuration);
      }
    }, 500);

    return () => window.clearInterval(timer);
  }, [playerReady]);

  const runSearch = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const trimmed = query.trim();
    if (trimmed.length < 2) return;

    setIsSearching(true);
    setSearchError("");

    try {
      const response = await fetch(
        `/api/youtube/search?q=${encodeURIComponent(trimmed)}`,
        { cache: "no-store", signal: AbortSignal.timeout(20000) },
      );
      const data = (await response.json()) as {
        items?: SearchTrack[];
        error?: string;
      };

      if (!response.ok) {
        throw new Error(data.error || "Search failed.");
      }

      setResults(data.items ?? []);
      if (!data.items?.length) setSearchError("No songs found.");
    } catch (error) {
      setResults([]);
      setSearchError(
        error instanceof Error ? error.message : "Search failed.",
      );
    } finally {
      setIsSearching(false);
    }
  };

  const playTrack = (track: SearchTrack, source = results) => {
    const nextQueue = source.length ? source : [track];
    const nextIndex = Math.max(
      0,
      nextQueue.findIndex((item) => item.id === track.id),
    );

    queueRef.current = nextQueue;
    indexRef.current = nextIndex;
    setQueue(nextQueue);
    setCurrentIndex(nextIndex);
    setResults([]);
    setSearchError("");
    setPlayerError("");

    ensurePlayer();

    if (playerRef.current && playerReady) {
      playerRef.current.loadVideoById(track.id);
    }
  };

  const togglePlayback = () => {
    if (!currentTrack) {
      if (results[0]) playTrack(results[0]);
      return;
    }

    if (!playerRef.current) {
      ensurePlayer();
      return;
    }

    if (isPlaying) {
      playerRef.current.pauseVideo();
    } else {
      playerRef.current.playVideo();
    }
  };

  const previousTrack = () => {
    const player = playerRef.current;

    if (player && player.getCurrentTime() > 3) {
      player.seekTo(0, true);
      setElapsed(0);
      return;
    }

    if (currentIndex > 0) setCurrentIndex(currentIndex - 1);
  };

  const nextTrack = () => {
    if (currentIndex >= 0 && currentIndex < queue.length - 1) {
      setCurrentIndex(currentIndex + 1);
    }
  };

  const intro = (delay: number, y = 22) => ({
    initial: reduced ? false : { opacity: 0, y, filter: "blur(10px)" },
    animate: reduced
      ? undefined
      : { opacity: 1, y: 0, filter: "blur(0px)" },
    transition: { duration: 0.9, delay, ease },
  });

  return (
    <section
      ref={sectionRef}
      id="top"
      className="hero-shell section-shell"
    >
      <div className="ambient ambient-one" aria-hidden="true" />
      <div className="ambient ambient-two" aria-hidden="true" />

      <div className="hero-flow">
        <div className="landing-intro">
          <motion.div
            {...intro(0.04, 14)}
            className="mb-7 flex items-center justify-center gap-3 lg:justify-start"
          >
            <SpinnableLogo
              alt="XAN"
              className="logo-intro-spin-delayed h-11 w-11 rounded-xl object-cover shadow-2xl shadow-white/5"
            />
            <span className="text-xs font-medium tracking-[0.3em] text-white/45">
              ANDROID MUSIC PLAYER
            </span>
          </motion.div>

          <motion.h1
            {...intro(0.11, 36)}
            className="display-title"
          >
            Your music.<br /><span className="gradient-text">Your world.</span>
          </motion.h1>

          <motion.p
            {...intro(0.2, 28)}
            className="mt-2 text-balance text-2xl font-medium tracking-[-0.04em] text-white sm:text-3xl md:text-4xl"
          >
            Meet XAN. Find your next repeat.
          </motion.p>

          <motion.p
            {...intro(0.28, 22)}
            className="mx-auto mt-5 max-w-lg text-pretty text-base leading-7 text-white/48 lg:mx-0"
          >
            A focused, open-source music player for your library, playlists,
            queue and lyrics — built for Android.
          </motion.p>

          <motion.div
            {...intro(0.36, 18)}
            className="mt-9 flex justify-center lg:justify-start"
          >
            <DownloadButton />
          </motion.div>
        </div>

        <div id="player" className="listening-workspace">
          <div className="workspace-heading"><div><p className="eyebrow">THE WEB PLAYER</p><h2>Press play. Stay a while.</h2></div><p>Search YouTube Music and choose a track. Each selection replaces Up next with those search results.</p></div>

          <div
            className={`hero-object interactive-player ${
              isPlaying ? "is-playing" : "is-paused"
            }`}
            aria-label="Interactive XAN player"
          >
            <div className="hero-object-highlight" aria-hidden="true" />

            <div className="player-layout">
              <div className="flex items-start justify-between gap-4">
                <div className="flex items-center gap-3">
                  <SpinnableLogo className="player-logo-spin h-10 w-10 rounded-xl object-cover" />
                  <div>
                    <p className="text-[10px] font-medium tracking-[0.24em] text-white/35">
                      XAN · LIVE DEMO
                    </p>
                    <p className="mt-0.5 text-sm font-medium text-white/75">
                      XAN player
                    </p>
                  </div>
                </div>
                <span className="glass-pill" aria-live="polite">
                  {currentTrack ? (!playerReady ? "LOADING" : isBuffering ? "BUFFERING" : isPlaying ? "PLAYING" : "PAUSED") : "READY"}
                </span>
              </div>

              <form onSubmit={runSearch} className="relative z-30 mt-5">
                <label htmlFor="music-search" className="search-label">Find a song</label>
                <div className="player-search-row relative z-40 flex items-center gap-2 rounded-2xl border border-white/12 bg-[#111114] p-2 shadow-[0_10px_34px_rgba(0,0,0,0.34)]">
                  <svg
                    viewBox="0 0 24 24"
                    className="ml-2 h-4 w-4 shrink-0 fill-none stroke-white/40"
                    strokeWidth="1.8"
                    aria-hidden="true"
                  >
                    <circle cx="11" cy="11" r="6.5" />
                    <path d="m16 16 4 4" />
                  </svg>
                  <input
                    value={query}
                    onChange={(event) => setQuery(event.target.value)}
                    placeholder="Search XAN…"
                    className="min-w-0 flex-1 bg-transparent px-1 py-2 text-sm text-white outline-none placeholder:text-white/28"
                    aria-label="Search music"
                  />
                  <button
                    type="submit"
                    disabled={isSearching || query.trim().length < 2}
                    className="rounded-xl bg-white px-3 py-2 text-xs font-semibold text-black transition hover:bg-white/90 disabled:cursor-not-allowed disabled:opacity-35"
                  >
                    {isSearching ? "Searching" : "Search"}
                  </button>
                </div>

                {(results.length > 0 || searchError) && (
                  <div className="absolute left-0 right-0 top-[calc(100%+0.5rem)] z-50 max-h-64 overflow-y-auto rounded-2xl border border-white/10 bg-[#0b0b0d] p-2 shadow-2xl">
                    {searchError ? (
                      <p className="px-3 py-4 text-sm text-white/50">
                        {searchError}
                      </p>
                    ) : (
                      results.map((track) => (
                        <button
                          key={track.id}
                          type="button"
                          onClick={() => playTrack(track)}
                          className="flex w-full items-center gap-3 rounded-xl p-2 text-left transition hover:bg-white/[0.07] focus-visible:bg-white/[0.07] focus-visible:outline-none"
                        >
                          <img
                            src={track.thumbnail}
                            alt=""
                            className="h-11 w-11 shrink-0 rounded-lg object-cover"
                          />
                          <span className="min-w-0 flex-1">
                            <span className="block truncate text-sm font-medium text-white/90">
                              {track.title}
                            </span>
                            <span className="mt-0.5 block truncate text-xs text-white/40">
                              {track.artist}
                            </span>
                          </span>
                          <span className="text-[10px] text-white/30">
                            {formatTime(track.duration)}
                          </span>
                        </button>
                      ))
                    )}
                  </div>
                )}
                <p role="status" aria-live="polite" className="search-status">{isSearching ? "Searching YouTube Music…" : searchError || (results.length ? `${results.length} results. Select a track to replace Up next.` : "")}</p>
              </form>

              <div className="mx-auto my-6 w-full max-w-sm">
                <div className="album-art">
                  {currentTrack ? (
                    <>
                      <img
                        src={currentTrack.thumbnail}
                        alt=""
                        className="absolute inset-0 h-full w-full object-cover"
                      />
                      <div className="pointer-events-none absolute inset-0 z-[2] bg-gradient-to-t from-black/55 via-transparent to-black/10" />
                    </>
                  ) : (
                    <>
                      <div className="album-orbit orbit-one" />
                      <div className="album-orbit orbit-two" />
                      <div className="album-core">
                        <SpinnableLogo className="player-logo-spin h-20 w-20 rounded-[1.4rem] object-cover" />
                      </div>
                    </>
                  )}
                </div>
              </div>

              <div className="mt-auto">
                <div className="player-track-row flex items-end justify-between gap-5">
                  <div className="min-w-0">
                    <p className="truncate text-lg font-medium tracking-[-0.03em] text-white/90">
                      {currentTrack?.title ?? "Search for a song"}
                    </p>
                    <p className={`mt-1 text-sm text-white/38 ${playerError ? "player-error" : "truncate"}`}
                      role={playerError ? "alert" : undefined}>
                      {playerError || currentTrack?.artist || "Your next favorite is a search away."}
                    </p>
                  </div>

                  <div className="player-track-controls flex shrink-0 items-center gap-2">
                    <button
                      type="button"
                      onClick={previousTrack}
                      disabled={!currentTrack}
                      className="grid h-10 w-10 place-items-center rounded-full border border-white/10 bg-white/[0.05] text-white transition hover:bg-white/[0.1] disabled:cursor-not-allowed disabled:opacity-25"
                      aria-label="Previous track"
                    >
                      <svg viewBox="0 0 24 24" className="h-4 w-4 fill-current">
                        <path d="M6 5h2v14H6V5Zm12.2 1.3a1 1 0 0 1 1.6.8v9.8a1 1 0 0 1-1.6.8l-6.5-4.9a1 1 0 0 1 0-1.6l6.5-4.9Z" />
                      </svg>
                    </button>

                    <button
                      type="button"
                      className="play-orb"
                      onClick={togglePlayback}
                      disabled={!currentTrack && !results.length}
                      aria-label={isPlaying ? "Pause" : "Play"}
                      aria-pressed={isPlaying}
                    >
                      {isPlaying ? (
                        <svg viewBox="0 0 24 24" className="h-5 w-5 fill-current">
                          <path d="M7.5 6.2c0-.7.5-1.2 1.2-1.2h1.8c.7 0 1.2.5 1.2 1.2v11.6c0 .7-.5 1.2-1.2 1.2H8.7c-.7 0-1.2-.5-1.2-1.2V6.2Zm6.8 0c0-.7.5-1.2 1.2-1.2h1.8c.7 0 1.2.5 1.2 1.2v11.6c0 .7-.5 1.2-1.2 1.2h-1.8c-.7 0-1.2-.5-1.2-1.2V6.2Z" />
                        </svg>
                      ) : (
                        <svg viewBox="0 0 24 24" className="h-5 w-5 fill-current">
                          <path d="M8.5 6.8c0-1 1.1-1.6 2-1l7.5 5.2c.8.5.8 1.6 0 2.2l-7.5 5.1c-.9.6-2 .1-2-1V6.8Z" />
                        </svg>
                      )}
                    </button>

                    <button
                      type="button"
                      onClick={nextTrack}
                      disabled={currentIndex < 0 || currentIndex >= queue.length - 1}
                      className="grid h-10 w-10 place-items-center rounded-full border border-white/10 bg-white/[0.05] text-white transition hover:bg-white/[0.1] disabled:cursor-not-allowed disabled:opacity-25"
                      aria-label="Next track"
                    >
                      <svg viewBox="0 0 24 24" className="h-4 w-4 fill-current">
                        <path d="M16 5h2v14h-2V5ZM5.8 6.3a1 1 0 0 0-1.6.8v9.8a1 1 0 0 0 1.6.8l6.5-4.9a1 1 0 0 0 0-1.6L5.8 6.3Z" />
                      </svg>
                    </button>
                  </div>
                </div>

                <div className="player-seek-wrap mt-5">
                  <div
                    className="player-seek-fill"
                    style={{
                      width: `${duration > 0 ? (elapsed / duration) * 100 : 0}%`,
                    }}
                    aria-hidden="true"
                  />
                  <input
                    className="player-seek"
                    type="range"
                    min="0"
                    max={Math.max(duration, 1)}
                    step="0.5"
                    value={Math.min(elapsed, Math.max(duration, 1))}
                    onChange={(event) => {
                      const value = Number(event.target.value);
                      setElapsed(value);
                      playerRef.current?.seekTo(value, true);
                    }}
                    disabled={!currentTrack}
                    aria-label="Seek"
                    aria-valuetext={`${formatTime(elapsed)} of ${formatTime(duration)}`}
                  />
                </div>

                <div className="mt-2 flex justify-between text-[10px] tracking-widest text-white/25">
                  <span>{formatTime(elapsed)}</span>
                  <span>{formatTime(duration)}</span>
                </div>

                <div className="player-volume-row mt-4 flex items-center gap-3">
                  <svg
                    viewBox="0 0 24 24"
                    className="h-4 w-4 shrink-0 fill-none stroke-white/40"
                    strokeWidth="1.8"
                    aria-hidden="true"
                  >
                    <path d="M5 10v4h3l4 3V7l-4 3H5Z" />
                    <path d="M15.5 9.2a4 4 0 0 1 0 5.6" />
                    <path d="M18 7a7 7 0 0 1 0 10" />
                  </svg>
                  <input
                    className="volume-range"
                    type="range"
                    min="0"
                    max="100"
                    step="1"
                    value={volume}
                    onChange={(event) => {
                      const nextVolume = Number(event.target.value);
                      setVolume(nextVolume);
                      playerRef.current?.setVolume(nextVolume);
                    }}
                    aria-label="Volume"
                    aria-valuetext={`${volume}%`}
                  />
                  <span className="w-8 text-right text-[10px] tabular-nums text-white/28">
                    {volume}%
                  </span>
                </div>

                <div className="mt-3 flex items-center justify-between text-[10px] tracking-[0.15em] text-white/25">
                  <span>EQ CURVE PREVIEW</span>
                  <span>{activePreset}</span>
                </div>
              </div>
            </div>
          </div>

          <div className="floating-chip floating-chip-left" aria-hidden="true">
            <span className="dot" />
            Live search
          </div>
          <div className="floating-chip floating-chip-right" aria-hidden="true">
            YouTube
          </div>
        </div>
      </div>

      <div className="queue-panel" aria-label="Play queue">
        <div className="queue-heading"><h3>Up next</h3><span>{queue.length ? `${queue.length} tracks` : "Your queue starts here"}</span></div>
        {queue.length ? <ol>{queue.map((track, index) => <li key={`${track.id}-${index}`}><button type="button" onClick={() => setCurrentIndex(index)} aria-current={index === currentIndex ? "true" : undefined}><span className="queue-number">{String(index + 1).padStart(2, "0")}</span><span className="queue-title">{track.title}<small>{track.artist}</small></span><span>{formatTime(track.duration)}</span></button></li>)}</ol> : <p>Select a search result to load its results into Up next. Use previous and next to move through them.</p>}
      </div>
      <div
        ref={playerHostRef}
        aria-label="YouTube video player"
        className={`youtube-host ${currentTrack ? "has-track" : ""}`}
      />
    </section>
  );
}
