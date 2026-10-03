import type { Metadata, Viewport } from "next";
import "./globals.css";

const logo = "/xan-mark.webp";

const themeScript = `
(function () {
  try {
    var saved = localStorage.getItem("xan-theme");
    var theme = saved === "light" ? "light" : "dark";
    document.documentElement.dataset.theme = theme;
    document.documentElement.style.colorScheme = theme;
  } catch (error) {
    document.documentElement.dataset.theme = "dark";
    document.documentElement.style.colorScheme = "dark";
  }
})();
`;

export const metadata: Metadata = {
  metadataBase: new URL("https://xanapkweb.vercel.app"),
  title: "XAN — Music, without the noise.",
  description: "XAN is a modern open-source music player for Android.",
  applicationName: "XAN",
  icons: {
    icon: logo,
    shortcut: logo,
    apple: logo,
  },
  openGraph: {
    title: "XAN — Music, without the noise.",
    description: "A modern open-source music player for Android.",
    type: "website",
    images: [{ url: logo, width: 1200, height: 1200, alt: "XAN" }],
  },
  twitter: {
    card: "summary",
    title: "XAN — Music, without the noise.",
    description: "A modern open-source music player for Android.",
    images: [logo],
  },
};

export const viewport: Viewport = {
  themeColor: "#050505",
  colorScheme: "dark light",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en" data-theme="dark" suppressHydrationWarning>
      <head>
        <script dangerouslySetInnerHTML={{ __html: themeScript }} />
      </head>
      <body>{children}</body>
    </html>
  );
}
