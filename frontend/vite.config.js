import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  build: {
    outDir: '../myproject/src/main/resources/static',
    emptyOutDir: true
  },
  server: {
    proxy: {
      '/game': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
