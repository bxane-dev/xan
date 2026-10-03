import { Reveal } from "@/components/MotionReveal";
import { SpinnableLogo } from "@/components/SpinnableLogo";
import { VisitorCounter } from "@/components/VisitorCounter";


const links = [
  ["GitHub", "https://github.com/bxane-dev/xan"],
  ["Ko-fi", "https://ko-fi.com/bxane"],
  ["guns.lol", "https://guns.lol/bxane"],
];

export function Footer() {
  return (
    <footer className="section-shell pb-10 pt-24">
      <Reveal y={24}>
        <div className="flex flex-col gap-10 border-t border-white/[0.08] pt-10 sm:flex-row sm:items-end sm:justify-between">
          <div>
            <div className="flex items-center gap-3">
              <SpinnableLogo className="h-8 w-8 rounded-lg object-cover" />
              <span className="text-sm font-semibold tracking-[0.24em] text-white">
                XAN
              </span>
            </div>
            <p className="mt-4 text-sm text-white/34">
              A modern open-source music player for Android.
            </p>
            <div className="mt-4">
              <VisitorCounter />
            </div>
          </div>

          <div className="sm:text-right">
            <div className="flex flex-wrap gap-x-5 gap-y-2 text-sm text-white/45 sm:justify-end">
              {links.map(([label, href]) => (
                <a
                  key={label}
                  href={href}
                  target="_blank"
                  rel="noreferrer"
                  className="transition-colors hover:text-white"
                >
                  {label}
                </a>
              ))}
            </div>
            <p className="mt-5 text-xs tracking-[0.1em] text-white/22">© 2026 XAN</p>
            <p className="mt-2 text-xs text-white/28">
              Made by{" "}
              <a
                href="https://github.com/bxane-dev"
                target="_blank"
                rel="noreferrer"
                className="text-white/50 transition-colors hover:text-white"
              >
                bxane
              </a>
            </p>
          </div>
        </div>
      </Reveal>
    </footer>
  );
}
