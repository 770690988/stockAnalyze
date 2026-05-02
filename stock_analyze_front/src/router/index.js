import { createRouter, createWebHistory } from 'vue-router'
import WatchlistManager from '../views/WatchlistManager.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/watchlist',
      component: WatchlistManager
    }
  ]
})

export default router