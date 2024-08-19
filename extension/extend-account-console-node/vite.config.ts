import { defineConfig } from "vite";
import react from "@vitejs/plugin-react-swc";
import { checker } from "vite-plugin-checker";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react(), checker({ typescript: true })],
  server: {
    origin: "http://localhost:5173",
    port: 5173,
  },
  base: "",
  build: {
    manifest: true,
    sourcemap: true,
    rollupOptions: {
      input: "src/main.tsx",
      external: ["react", "react/jsx-runtime", "react-dom"],
      treeshake: false,
    },
  },
});
