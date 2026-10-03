import { Reveal } from "@/components/MotionReveal";

export function OpenSource() {
  return (
    <section className="section-shell py-28 sm:py-36">
      <div className="grid gap-6 lg:grid-cols-[1.25fr_0.75fr]">
        <Reveal y={36}>
          <div className="info-panel mobile-source-card min-h-[420px]">
            <p className="eyebrow">OPEN SOURCE</p>
            <div className="mt-auto">
              <h2 className="text-4xl font-medium tracking-[-0.05em] text-white sm:text-6xl">
                Built in the open.
              </h2>
              <p className="mt-5 max-w-lg text-sm leading-6 text-white/42">
                XAN&apos;s source, releases and development history are public on
                GitHub. Explore the code, build it yourself, or contribute.
              </p>
              <a
                href="https://github.com/bxane-dev/xan"
                target="_blank"
                rel="noreferrer"
                className="secondary-button mt-8 inline-flex"
              >
                View source
              </a>
            </div>
          </div>
        </Reveal>

        <Reveal delay={0.08} y={36}>
          <div className="info-panel mobile-support-card min-h-[420px]">
            <p className="eyebrow">SUPPORT XAN</p>
            <div className="mt-auto">
              <h2 className="text-3xl font-medium tracking-[-0.045em] text-white sm:text-4xl">
                Enjoy XAN?
                <br />
                Support development.
              </h2>
              <a
                href="https://ko-fi.com/bxane"
                target="_blank"
                rel="noreferrer"
                className="download-button mt-8 inline-flex"
              >
                Ko-fi
                <svg viewBox="0 0 24 24" className="h-4 w-4" aria-hidden="true">
                  <path
                    d="M7 17 17 7M9 7h8v8"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="1.8"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                  />
                </svg>
              </a>
            </div>
          </div>
        </Reveal>
      </div>
    </section>
  );
}
