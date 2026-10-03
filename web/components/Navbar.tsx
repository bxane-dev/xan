"use client";

import { useEffect, useState } from "react";

const LOGO = "/xan-mark.webp";

type Theme = "dark" | "light";

const mobileItems = [
  {
    label: "Download",
    href: "#download",
    icon: (
      <path d="M3.5 10.5 12 3l8.5 7.5v9a1.5 1.5 0 0 1-1.5 1.5h-4.5v-6h-5v6H5a1.5 1.5 0 0 1-1.5-1.5v-9Z" />
    ),
  },
  {
    label: "Player",
    href: "#player",
    icon: (
      <>
        <circle cx="12" cy="12" r="8.5" />
        <path d="m10 8.8 5 3.2-5 3.2V8.8Z" />
      </>
    ),
  },
  {
    label: "App",
    href: "#screenshots",
    icon: (
      <>
        <rect x="6.5" y="3" width="11" height="18" rx="2.5" />
        <path d="M10 6h4" />
      </>
    ),
  },
  {
    label: "EQ",
    href: "#equalizer",
    icon: (
      <>
        <path d="M5 6v12M12 4v16M19 7v10" />
        <circle cx="5" cy="10" r="1.7" />
        <circle cx="12" cy="14" r="1.7" />
        <circle cx="19" cy="11" r="1.7" />
      </>
    ),
  },
] as const;

export function Navbar() {
  const [scrolled, setScrolled] = useState(false);
  const [theme, setTheme] = useState<Theme>("dark");

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 28);
    onScroll();
    window.addEventListener("scroll", onScroll, { passive: true });

    const activeTheme: Theme =
      document.documentElement.dataset.theme === "light" ? "light" : "dark";
    setTheme(activeTheme);

    return () => window.removeEventListener("scroll", onScroll);
  }, []);

  const toggleTheme = () => {
    const nextTheme: Theme = theme === "dark" ? "light" : "dark";
    setTheme(nextTheme);
    document.documentElement.dataset.theme = nextTheme;
    document.documentElement.style.colorScheme = nextTheme;
    window.localStorage.setItem("xan-theme", nextTheme);

    const themeMeta = document.querySelector('meta[name="theme-color"]');
    themeMeta?.setAttribute(
      "content",
      nextTheme === "light" ? "#f6f6f3" : "#050505",
    );
  };

  return (
    <>
      <header className="fixed inset-x-0 top-0 z-50 px-3 pt-3 sm:px-6 sm:pt-4">
        <nav
          className={[
            "site-nav mx-auto flex h-14 max-w-6xl items-center justify-between rounded-full px-3 transition-all duration-500 sm:px-5",
            scrolled
              ? "site-nav-scrolled border border-white/10 bg-black/55 shadow-2xl shadow-black/25 backdrop-blur-2xl"
              : "border border-transparent bg-transparent",
          ].join(" ")}
          aria-label="Primary navigation"
        >
          <a
            href="#top"
            className="group flex shrink-0 items-center gap-2 sm:gap-2.5"
            aria-label="XAN home"
          >
            <img
              src={LOGO}
              alt=""
              className="h-7 w-7 rounded-lg object-cover transition-transform duration-300 group-hover:scale-105"
              draggable={false}
            />
            <span className="text-xs font-semibold tracking-[0.16em] text-white min-[390px]:text-sm sm:tracking-[0.22em]">
              XAN
            </span>
          </a>

          <div className="hidden items-center gap-2 text-sm text-white/68 sm:flex">
            <a className="nav-link" href="#download">Download</a>
            <a className="nav-link" href="#player">Player</a>
            <a className="nav-link" href="#screenshots">App</a>
            <a className="nav-link" href="#equalizer">EQ</a>
            <a
              className="nav-link"
              href="https://github.com/bxane-dev/xan"
              target="_blank"
              rel="noreferrer"
            >
              GitHub
            </a>
            <button
              type="button"
              className="theme-toggle"
              onClick={toggleTheme}
              aria-label={theme === "dark" ? "Switch to light mode" : "Switch to dark mode"}
              title={theme === "dark" ? "Light mode" : "Dark mode"}
            >
              <svg viewBox="0 0 24 24" className="h-[18px] w-[18px]" aria-hidden="true">
                <path
                  fill="currentColor"
                  d="M12 3a9 9 0 1 0 9 9 7 7 0 0 1-9-9Z"
                />
              </svg>
            </button>
          </div>

          <button
            type="button"
            className="theme-toggle sm:hidden"
            onClick={toggleTheme}
            aria-label={theme === "dark" ? "Switch to light mode" : "Switch to dark mode"}
          >
            <svg viewBox="0 0 24 24" className="h-[18px] w-[18px]" aria-hidden="true">
              <path fill="currentColor" d="M12 3a9 9 0 1 0 9 9 7 7 0 0 1-9-9Z" />
            </svg>
          </button>
        </nav>
      </header>

      <nav className="mobile-dock sm:hidden" aria-label="Mobile navigation">
        <div className="mobile-dock-inner">
          {mobileItems.map((item) => (
            <a key={item.label} href={item.href} className="mobile-dock-item">
              <svg
                viewBox="0 0 24 24"
                className="h-5 w-5 fill-none stroke-current"
                strokeWidth="1.7"
                strokeLinecap="round"
                strokeLinejoin="round"
                aria-hidden="true"
              >
                {item.icon}
              </svg>
              <span>{item.label}</span>
            </a>
          ))}
          <a
            href="/api/download"
            target="_blank"
            rel="noreferrer"
            className="mobile-dock-download"
            aria-label="Download XAN"
          >
            <svg
              viewBox="0 0 24 24"
              className="h-5 w-5 fill-none stroke-current"
              strokeWidth="1.8"
              strokeLinecap="round"
              strokeLinejoin="round"
              aria-hidden="true"
            >
              <path d="M12 3v12" />
              <path d="m7.5 10.5 4.5 4.5 4.5-4.5" />
              <path d="M5 20h14" />
            </svg>
          </a>
        </div>
      </nav>
    </>
  );
}
