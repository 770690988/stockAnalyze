import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      '/watchlist/bk': {
        target: 'http://localhost:8082', // 改成你后端的 IP 和端口
        changeOrigin: true
      }
    }
  }
})