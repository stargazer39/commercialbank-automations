import { defineConfig } from "vite";
import react from "@vitejs/plugin-react-swc";
// import { VitePWA } from "vite-plugin-pwa";
import { TanStackRouterVite } from "@tanstack/router-plugin/vite";

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    TanStackRouterVite(),
    // VitePWA({
    //   registerType: "autoUpdate",
    //   strategies: "injectManifest",
    //   srcDir: "src",
    //   filename: "sw.ts",
    //   devOptions: {
    //     enabled: true,
    //   },
    // }),
  ],
});
