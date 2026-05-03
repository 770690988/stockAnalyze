import { createRouter, createWebHistory } from 'vue-router'
import WatchlistManager from '../views/WatchlistManager.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      component: WatchlistManager
    }
  ]
})

export default router