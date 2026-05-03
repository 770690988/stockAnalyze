import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/watchlist/bk': {
        target: 'http://localhost:8082',
        changeOrigin: true
      },
      '/api': {                          // ← 要在 proxy 里面
        target: 'http://localhost:8082',
        changeOrigin: true
      }
    }
  }
})