import { defineConfig } from "vite";
import scalaJSPlugin from "@scala-js/vite-plugin-scalajs";
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  server: {
    port: 5174,
  },
  plugins: [
    scalaJSPlugin({
      cwd: "../..",
      projectID: "mainMenuClient"
    }),
    tailwindcss(),
  ],
});
