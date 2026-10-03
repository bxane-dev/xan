"use client";

import { motion, useReducedMotion } from "motion/react";
import { useEffect, useState } from "react";

const PRESETS = {
  "XAN Signature": [3, 2, 1, 0, -0.4, 0, 1.6, 3, 4, 3],
  "Bass Boost": [7, 6, 4.5, 2.5, 0.8, 0, -0.5, 0, 0.8, 1],
  "Pure Clarity": [-1.5, -1, 0, 0.8, 1.4, 2.1, 3.2, 4, 3, 2],
  Spatial: [-2, -1, 0, 1, 1.5, 2, 2.7, 3.5, 2.8, 1.8],
} as const;

type PresetName = keyof typeof PRESETS;
type EqName = PresetName | "Custom";

const labels = ["31", "62", "125", "250", "500", "1K", "2K", "4K", "8K", "16K"];
const CUSTOM_KEY = "xan.eq.custom-bands.v1";
const PRESET_KEY = "xan.eq.preset";

function dispatchBands(name: EqName, gains: number[]) {
  window.dispatchEvent(
    new CustomEvent("xan:eq-preset", {
      detail: { name, gains: [...gains] },
    }),
  );
}

function formatGain(gain: number) {
  if (gain > 0) return `+${gain.toFixed(1)}`;
  return gain.toFixed(1);
}

export function EqualizerSpotlight() {
  const reduced = useReducedMotion();
  const [activePreset, setActivePreset] = useState<EqName>("XAN Signature");
  const [bands, setBands] = useState<number[]>([...PRESETS["XAN Signature"]]);

  useEffect(() => {
    const storedPreset = window.localStorage.getItem(PRESET_KEY) as EqName | null;

    if (storedPreset === "Custom") {
      try {
        const parsed = JSON.parse(
          window.localStorage.getItem(CUSTOM_KEY) ?? "[]",
        ) as number[];

        if (parsed.length === labels.length && parsed.every(Number.isFinite)) {
          const safe = parsed.map((value) =>
            Math.max(-12, Math.min(12, Number(value))),
          );
          setActivePreset("Custom");
          setBands(safe);
          dispatchBands("Custom", safe);
          return;
        }
      } catch {
        // Fall back to XAN Signature.
      }
    }

    if (storedPreset && storedPreset in PRESETS) {
      const preset = storedPreset as PresetName;
      const gains = [...PRESETS[preset]];
      setActivePreset(preset);
      setBands(gains);
      dispatchBands(preset, gains);
      return;
    }

    dispatchBands("XAN Signature", [...PRESETS["XAN Signature"]]);
  }, []);

  const selectPreset = (name: PresetName) => {
    const gains = [...PRESETS[name]];
    setActivePreset(name);
    setBands(gains);
    window.localStorage.setItem(PRESET_KEY, name);
    dispatchBands(name, gains);
  };

  const changeBand = (index: number, value: number) => {
    const next = bands.map((gain, bandIndex) =>
      bandIndex === index ? Math.max(-12, Math.min(12, value)) : gain,
    );

    setBands(next);
    setActivePreset("Custom");
    window.localStorage.setItem(PRESET_KEY, "Custom");
    window.localStorage.setItem(CUSTOM_KEY, JSON.stringify(next));
    dispatchBands("Custom", next);
  };

  return (
    <section
      id="equalizer"
      className="relative mx-auto max-w-7xl px-5 py-24 sm:px-8 lg:px-10"
    >
      <div className="grid items-center gap-12 lg:grid-cols-[0.9fr_1.1fr] lg:gap-16">
        <div className="max-w-xl">
          <p className="eq-kicker text-xs font-medium tracking-[0.28em]">
            XAN EQUALIZER
          </p>
          <h2 className="eq-title mt-3 text-4xl font-medium tracking-[-0.05em] sm:text-5xl">
            Tune it your way.
          </h2>
          <p className="eq-copy mt-5 text-base leading-7">
            Drag any of the 10 bands or tap a preset to edit the XAN EQ curve.
            This browser saves your curve locally; the Android app has its own audio controls.
          </p>

          <div className="mt-7 grid gap-3 sm:grid-cols-2">
            <div className="eq-info-card rounded-2xl p-4">
              <p className="eq-info-title text-sm font-medium">10-band editor</p>
              <p className="eq-info-copy mt-1 text-sm leading-6">
                Each band is adjustable from −12 dB to +12 dB and supports
                touch, mouse and keyboard control.
              </p>
            </div>
            <div className="eq-info-card rounded-2xl p-4">
              <p className="eq-info-title text-sm font-medium">Web limitation</p>
              <p className="eq-info-copy mt-1 text-sm leading-6">
                YouTube&apos;s embedded audio is isolated by the browser, so the
                website can preview the EQ curve but cannot process that stream.
              </p>
            </div>
          </div>

          <div className="mt-6 flex flex-wrap gap-2" aria-label="Equalizer presets">
            {(Object.keys(PRESETS) as PresetName[]).map((preset) => (
              <button
                key={preset}
                type="button"
                onClick={() => selectPreset(preset)}
                aria-pressed={activePreset === preset}
                className={`eq-preset ${
                  activePreset === preset ? "eq-preset-active" : ""
                }`}
              >
                {preset}
              </button>
            ))}
          </div>
        </div>

        <motion.div
          initial={reduced ? false : { opacity: 0, y: 24 }}
          whileInView={reduced ? undefined : { opacity: 1, y: 0 }}
          viewport={{ once: true, amount: 0.25 }}
          transition={{ duration: 0.7, ease: [0.16, 1, 0.3, 1] }}
          className="relative"
        >
          <div className="eq-glow absolute inset-10 rounded-full blur-3xl" aria-hidden="true" />
          <div className="eq-panel relative overflow-hidden rounded-[2rem] p-5 sm:p-7">
            <div className="flex items-center justify-between gap-4">
              <div>
                <p className="eq-panel-kicker text-[10px] font-medium tracking-[0.24em]">
                  XAN EQ
                </p>
                <p className="eq-panel-title mt-1 text-lg font-medium tracking-[-0.03em]">
                  {activePreset}
                </p>
              </div>
              <span className="eq-badge">XAN · 10 BAND</span>
            </div>

            <div className="eq-bands mt-8 flex h-72 items-end justify-between gap-1.5 sm:gap-3">
              {bands.map((gain, index) => {
                const normalized = Math.max(4, Math.min(96, 50 + (gain / 12) * 46));
                return (
                  <div
                    key={labels[index]}
                    className="flex min-w-0 flex-1 flex-col items-center gap-2"
                  >
                    <span className="eq-gain-value text-[9px] tabular-nums">
                      {formatGain(gain)}
                    </span>

                    <div className="eq-band-track relative flex h-48 w-full items-end justify-center rounded-full">
                      <motion.div
                        animate={{ height: `${normalized}%` }}
                        transition={{ duration: reduced ? 0 : 0.18, ease: [0.16, 1, 0.3, 1] }}
                        className="eq-band-fill pointer-events-none w-[58%] rounded-full"
                      />
                      <span className="eq-zero-line pointer-events-none absolute top-1/2 h-px w-[70%] -translate-y-1/2" />
                      <input
                        className="eq-band-slider absolute inset-0 z-10 h-full w-full cursor-ns-resize opacity-0"
                        type="range"
                        min="-12"
                        max="12"
                        step="0.5"
                        value={gain}
                        onChange={(event) =>
                          changeBand(index, Number(event.target.value))
                        }
                        aria-label={`${labels[index]} Hz gain`}
                        aria-valuetext={`${formatGain(gain)} dB`}
                      />
                    </div>

                    <span className="eq-band-label text-[9px] tabular-nums">
                      {labels[index]}
                    </span>
                  </div>
                );
              })}
            </div>

            <div className="eq-footer mt-6 flex items-center justify-between gap-4 pt-5">
              <div>
                <p className="eq-footer-label text-xs">Curve</p>
                <p className="eq-footer-value mt-1 text-sm font-medium">
                  {activePreset}
                </p>
              </div>
              <div className="flex items-center gap-2 text-right">
                <span className="eq-live-dot h-2 w-2 rounded-full" />
                <span className="eq-live-text text-xs">
                  Drag bands to edit
                </span>
              </div>
            </div>
          </div>
        </motion.div>
      </div>
    </section>
  );
}
