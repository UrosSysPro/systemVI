import { defineConfig } from "vite";
import scalaJSPlugin from "@scala-js/vite-plugin-scalajs";

export default defineConfig({
  server: {
    port: 5174,
  },
  plugins: [
    scalaJSPlugin({
      cwd: "../..",
      projectID: "mainMenuClient"
    }),
  ],
});
