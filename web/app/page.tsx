import { Editorial } from "@/components/Editorial";
import { FeatureSection } from "@/components/FeatureSection";
import { EqualizerSpotlight } from "@/components/EqualizerSpotlight";
import { Footer } from "@/components/Footer";
import { Hero } from "@/components/Hero";
import { Navbar } from "@/components/Navbar";
import { OpenSource } from "@/components/OpenSource";
import { ScrollProgress } from "@/components/ScrollProgress";
import { ScreenshotShowcase } from "@/components/ScreenshotShowcase";
import { VisualSection } from "@/components/VisualSection";

export default function Home() {
  return (
    <main className="overflow-hidden">
      <ScrollProgress />
      <Navbar />
      <Hero />
      <EqualizerSpotlight />
      <ScreenshotShowcase />
      <FeatureSection />
      <Editorial />
      <VisualSection />
      <OpenSource />
      <Footer />
    </main>
  );
}
