import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  { path: '/', redirect: '/products' },
  { path: '/login', component: () => import('@/views/LoginView.vue') },
  {
    path: '/products',
    component: () => import('@/views/ProductsView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/products/:id',
    component: () => import('@/views/ProductDetailView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/products/create',
    component: () => import('@/views/CreateProductView.vue'),
    meta: { requiresAuth: true },
  },
  {
    path: '/inventory/set/:productId',
    component: () => import('@/views/SetInventoryView.vue'),
    meta: { requiresAuth: true },
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// Guard: redirige al login si no hay token
router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.requiresAuth && !auth.isAuthenticated) {
    return '/login'
  }
})

export default router
