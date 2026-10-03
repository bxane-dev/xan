import { Reveal } from "@/components/MotionReveal";
import { SpinnableLogo } from "@/components/SpinnableLogo";


export function VisualSection() {
  return (
    <section className="section-shell py-28 sm:py-36">
      <Reveal>
        <div className="mb-12">
          <p className="eyebrow">APPEARANCE</p>
          <h2 className="mt-4 text-4xl font-medium tracking-[-0.05em] text-white sm:text-6xl">
            Glass when you want it.
            <br />
            Solid when you don&apos;t.
          </h2>
        </div>
      </Reveal>

      <Reveal y={48} amount={0.12}>
        <div className="visual-stage">
          <div className="stage-aurora stage-aurora-a" aria-hidden="true" />
          <div className="stage-aurora stage-aurora-b" aria-hidden="true" />
          <div className="stage-grid" aria-hidden="true" />

          <div className="mode-card mode-card-back" aria-hidden="true">
            <span className="mode-label">SOLID</span>
          </div>

          <div className="mode-card mode-card-front">
            <div className="flex items-center justify-between">
              <span className="mode-label">LIQUID GLASS</span>
              <SpinnableLogo className="h-7 w-7 rounded-lg object-cover" />
            </div>

            <div className="waveform mt-auto" aria-hidden="true">
              {Array.from({ length: 28 }, (_, index) => (
                <span
                  key={index}
                  style={{
                    height: `${18 + ((index * 17) % 72)}%`,
                    opacity: 0.22 + ((index * 13) % 55) / 100,
                    animationDelay: `${index * 45}ms`,
                  }}
                />
              ))}
            </div>

            <div className="mt-8 flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-white/85">Your interface</p>
                <p className="mt-1 text-xs text-white/32">Your accent. Your choice.</p>
              </div>
              <div className="glass-pulse h-10 w-10 rounded-full border border-white/10 bg-white/[0.08]" />
            </div>
          </div>
        </div>
      </Reveal>
    </section>
  );
}
