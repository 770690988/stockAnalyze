import { createRouter, createWebHistory } from 'vue-router'
import WatchlistManager from '../views/WatchlistManager.vue'
import Login from '../views/Login.vue'  // ← 加这行

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      component: Login,
      meta: { public: true }
    },
    {
      path: '/',
      component: WatchlistManager
    }
  ]
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (!to.meta.public && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/')
  } else {
    next()
  }
})

export default router