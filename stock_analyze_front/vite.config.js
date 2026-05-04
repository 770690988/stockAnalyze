import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  base: process.env.NODE_ENV === 'production' ? '/stock/' : '/',
  plugins: [vue()],
  server: {
    proxy: {
      '/watchlist/bk': {
        target: 'http://localhost:8082',
        changeOrigin: true
      },
      '/api': {
        target: 'http://localhost:8082',
        changeOrigin: true
      }
    }
  }
})