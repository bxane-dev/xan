"use client";

import { useEffect, useRef, useState } from "react";

const screenshots = [
  {
    src: "/screenshots/01.jpg",
    label: "Media controls",
    alt: "XAN lock screen media controls",
  },
  {
    src: "/screenshots/02.jpg",
    label: "Top songs",
    alt: "XAN Search suggestions showing top songs",
  },
  {
    src: "/screenshots/03.jpg",
    label: "Trending albums",
    alt: "XAN Search suggestions showing trending albums",
  },
  {
    src: "/screenshots/04.jpg",
    label: "Explore",
    alt: "XAN Explore screen with moods and moments",
  },
  {
    src: "/screenshots/05.jpg",
    label: "Search",
    alt: "XAN Search screen with the keyboard open",
  },
  {
    src: "/screenshots/06.jpg",
    label: "Mini player",
    alt: "XAN Search screen with the floating mini player",
  },
  {
    src: "/screenshots/07.jpg",
    label: "Library",
    alt: "XAN Library screen",
  },
  {
    src: "/screenshots/08.jpg",
    label: "Listen Now",
    alt: "XAN Listen Now home screen",
  },
  {
    src: "/screenshots/09.jpg",
    label: "Recently played",
    alt: "XAN home screen showing recently played music",
  },
  {
    src: "/screenshots/10.jpg",
    label: "Appearance",
    alt: "XAN Appearance settings screen",
  },
] as const;

export function ScreenshotShowcase() {
  const trackRef = useRef<HTMLDivElement>(null);
  const dialogRef = useRef<HTMLDivElement>(null);
  const [active, setActive] = useState(0);
  const [expanded, setExpanded] = useState<number | null>(null);

  useEffect(() => {
    const track = trackRef.current;
    if (!track) return;

    const updateActive = () => {
      const center = track.scrollLeft + track.clientWidth / 2;
      let closest = 0;
      let closestDistance = Number.POSITIVE_INFINITY;

      Array.from(track.children).forEach((child, index) => {
        const element = child as HTMLElement;
        const childCenter = element.offsetLeft + element.offsetWidth / 2;
        const distance = Math.abs(childCenter - center);

        if (distance < closestDistance) {
          closestDistance = distance;
          closest = index;
        }
      });

      setActive(closest);
    };

    updateActive();
    track.addEventListener("scroll", updateActive, { passive: true });
    window.addEventListener("resize", updateActive);

    return () => {
      track.removeEventListener("scroll", updateActive);
      window.removeEventListener("resize", updateActive);
    };
  }, []);

  useEffect(() => {
    if (expanded === null) return;

    const previousFocus = document.activeElement as HTMLElement | null;
    const siblings = Array.from(dialogRef.current?.parentElement?.children ?? []).filter(node => node !== dialogRef.current) as HTMLElement[];
    const previousInert = siblings.map(node => node.inert);
    siblings.forEach(node => { node.inert = true; });
    const previousOverflow = document.body.style.overflow;
    dialogRef.current?.querySelector<HTMLButtonElement>("button")?.focus();
    document.body.style.overflow = "hidden";

    const onKeyDown = (event: KeyboardEvent) => {
      if (event.key === "Tab") {
        const controls = Array.from(dialogRef.current?.querySelectorAll<HTMLButtonElement>("button") ?? []);
        const first = controls[0];
        const last = controls[controls.length - 1];
        if (event.shiftKey && document.activeElement === first) { event.preventDefault(); last?.focus(); }
        else if (!event.shiftKey && document.activeElement === last) { event.preventDefault(); first?.focus(); }
      }
      if (event.key === "Escape") {
        setExpanded(null);
        return;
      }

      if (event.key === "ArrowLeft") {
        setExpanded((current) =>
          current === null
            ? current
            : (current - 1 + screenshots.length) % screenshots.length,
        );
      }

      if (event.key === "ArrowRight") {
        setExpanded((current) =>
          current === null ? current : (current + 1) % screenshots.length,
        );
      }
    };

    window.addEventListener("keydown", onKeyDown);

    return () => {
      document.body.style.overflow = previousOverflow;
      siblings.forEach((node, index) => { node.inert = previousInert[index]; });
      previousFocus?.focus();
      window.removeEventListener("keydown", onKeyDown);
    };
  }, [expanded !== null]);

  const scrollTo = (index: number) => {
    const track = trackRef.current;
    const item = track?.children[index] as HTMLElement | undefined;

    item?.scrollIntoView({
      behavior: window.matchMedia("(prefers-reduced-motion: reduce)").matches ? "auto" : "smooth",
      block: "nearest",
      inline: "center",
    });
  };

  const move = (direction: number) => {
    const next = Math.min(
      screenshots.length - 1,
      Math.max(0, active + direction),
    );
    scrollTo(next);
  };

  const moveExpanded = (direction: number) => {
    setExpanded((current) => {
      if (current === null) return current;
      return (current + direction + screenshots.length) % screenshots.length;
    });
  };

  return (
    <>
      <section
        id="screenshots"
        className="relative mx-auto max-w-7xl px-5 py-24 sm:px-8 lg:px-10"
      >
        <div className="mb-9 flex items-end justify-between gap-6">
          <div>
            <p className="text-xs font-medium tracking-[0.28em] text-white/35">
              THE APP
            </p>
            <h2 className="mt-3 text-4xl font-medium tracking-[-0.05em] text-white sm:text-5xl">
              Inside XAN.
            </h2>
            <p className="mt-3 max-w-xl text-sm leading-6 text-white/45 sm:text-base">
              Swipe through the real interface. Tap any screenshot to expand it.
            </p>
          </div>

          <div className="hidden gap-2 sm:flex">
            <button
              type="button"
              onClick={() => move(-1)}
              disabled={active === 0}
              className="grid h-11 w-11 place-items-center rounded-full border border-white/10 bg-white/[0.04] text-xl text-white transition hover:bg-white/[0.09] disabled:cursor-not-allowed disabled:opacity-25"
              aria-label="Previous screenshot"
            >
              ←
            </button>
            <button
              type="button"
              onClick={() => move(1)}
              disabled={active === screenshots.length - 1}
              className="grid h-11 w-11 place-items-center rounded-full border border-white/10 bg-white/[0.04] text-xl text-white transition hover:bg-white/[0.09] disabled:cursor-not-allowed disabled:opacity-25"
              aria-label="Next screenshot"
            >
              →
            </button>
          </div>
        </div>

        <div
          ref={trackRef}
          className="-mx-5 flex snap-x snap-mandatory gap-4 overflow-x-auto scroll-smooth px-5 pb-5 sm:-mx-8 sm:gap-5 sm:px-8 lg:-mx-10 lg:px-10"
          style={{ scrollbarWidth: "none" }}
          tabIndex={0}
          onKeyDown={(event) => {
            if (event.key === "ArrowLeft") move(-1);
            if (event.key === "ArrowRight") move(1);
          }}
          aria-label="XAN app screenshots"
        >
          {screenshots.map((shot, index) => (
            <button
              key={shot.src}
              type="button"
              onClick={() => {
                setActive(index);
                setExpanded(index);
              }}
              className="group w-[58vw] max-w-[250px] shrink-0 snap-center cursor-zoom-in text-left sm:w-[250px]"
              aria-label={`Expand ${shot.label} screenshot`}
            >
              <div
                className={
                  "galaxy-s26-frame relative transition duration-500 " +
                  (active === index
                    ? "scale-100 border-white/20 opacity-100 shadow-black/60"
                    : "scale-[0.96] border-white/[0.07] opacity-55 shadow-black/30 group-hover:opacity-85")
                }
              >
                <div className="galaxy-s26-screen">
                <img
                  src={shot.src}
                  alt={shot.alt}
                  width={709}
                  height={1536}
                  loading={index < 2 ? "eager" : "lazy"}
                  draggable={false}
                  className="block h-auto w-full select-none"
                />
                <span className="galaxy-s26-camera" aria-hidden="true" />
                </div>
                <div className="pointer-events-none absolute inset-0 bg-gradient-to-t from-black/20 via-transparent to-white/[0.03]" />
                <div className="pointer-events-none absolute right-3 top-3 grid h-9 w-9 place-items-center rounded-full border border-white/15 bg-black/55 text-base text-white/85 opacity-0 backdrop-blur-md transition group-hover:opacity-100">
                  ↗
                </div>
              </div>
              <div className="mt-3 flex items-center justify-between px-1">
                <span className="text-sm font-medium text-white/70">
                  {shot.label}
                </span>
                <span className="text-[10px] tracking-[0.18em] text-white/25">
                  {String(index + 1).padStart(2, "0")}
                </span>
              </div>
            </button>
          ))}
        </div>

        <p className="gallery-position" role="status" aria-live="polite">{active + 1} / {screenshots.length} · {screenshots[active].label}</p>
        <div className="gallery-dots mt-2 flex items-center justify-center gap-2 sm:hidden">
          {screenshots.map((shot, index) => (
            <button
              key={shot.src}
              type="button"
              onClick={() => scrollTo(index)}
              className={
                "h-1.5 rounded-full transition-all " +
                (active === index ? "w-7 bg-white/80" : "w-1.5 bg-white/20")
              }
              aria-current={active === index ? "true" : undefined}
              aria-label={`Go to screenshot ${index + 1}`}
            />
          ))}
        </div>
      </section>

      {expanded !== null && (
        <div
          className="fixed inset-0 z-[100] flex items-center justify-center bg-black/90 p-3 backdrop-blur-xl sm:p-6"
          ref={dialogRef}
          role="dialog"
          aria-modal="true"
          aria-label={`Expanded ${screenshots[expanded].label} screenshot`}
          onClick={() => setExpanded(null)}
        >
          <button
            type="button"
            onClick={() => setExpanded(null)}
            className="absolute right-4 top-4 z-20 grid h-11 w-11 place-items-center rounded-full border border-white/15 bg-black/60 text-2xl text-white backdrop-blur-md transition hover:bg-white/10 sm:right-6 sm:top-6"
            aria-label="Close expanded screenshot"
          >
            ×
          </button>

          <button
            type="button"
            onClick={(event) => {
              event.stopPropagation();
              moveExpanded(-1);
            }}
            className="absolute left-3 top-1/2 z-20 grid h-12 w-12 -translate-y-1/2 place-items-center rounded-full border border-white/15 bg-black/60 text-2xl text-white backdrop-blur-md transition hover:bg-white/10 sm:left-6"
            aria-label="Previous expanded screenshot"
          >
            ←
          </button>

          <div
            className="flex max-h-[94vh] max-w-[92vw] flex-col items-center gap-3"
            onClick={(event) => event.stopPropagation()}
          >
            <div className="galaxy-s26-frame galaxy-s26-expanded">
            <div className="galaxy-s26-screen">
            <img
              src={screenshots[expanded].src}
              alt={screenshots[expanded].alt}
              width={709}
              height={1536}
              className="block h-auto w-full select-none"
            />
            <span className="galaxy-s26-camera" aria-hidden="true" />
            </div>
            </div>
            <div className="text-center">
              <p className="text-sm font-medium text-white/90">
                {screenshots[expanded].label}
              </p>
              <p className="mt-1 text-xs tracking-[0.15em] text-white/35">
                {String(expanded + 1).padStart(2, "0")} /{" "}
                {String(screenshots.length).padStart(2, "0")}
              </p>
            </div>
          </div>

          <button
            type="button"
            onClick={(event) => {
              event.stopPropagation();
              moveExpanded(1);
            }}
            className="absolute right-3 top-1/2 z-20 grid h-12 w-12 -translate-y-1/2 place-items-center rounded-full border border-white/15 bg-black/60 text-2xl text-white backdrop-blur-md transition hover:bg-white/10 sm:right-6"
            aria-label="Next expanded screenshot"
          >
            →
          </button>
        </div>
      )}
    </>
  );
}
