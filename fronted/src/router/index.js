import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '../layouts/MainLayout.vue'
import AdminLayout from '../layouts/AdminLayout.vue'

const routes = [
  {
    path: '/',
    name: 'LoginRegister',
    component: () => import('../pages/LoginRegister.vue')
  },
  {
    path: '/user',
    component: MainLayout,
    children: [
      {
        path: '',
        name: 'Forum',
        component: () => import('../pages/Forum.vue')
      },
      {
        path: 'trend',
        name: 'Trend',
        component: () => import('../pages/Trend.vue')
      },
      {
        path: 'notice',
        name: 'Notice',
        component: () => import('../pages/Notice.vue')
      },
      {
        path: 'order',
        name: 'Order',
        component: () => import('../pages/Order.vue')
      }
    ]
  },
  {
    path: '/admin',
    component: AdminLayout,
    children: [
      {
        path: '',
        name: 'Dashboard',
        component: () => import('../pages/admin/Dashboard.vue')
      },
      {
        path: 'users',
        name: 'UserManagement',
        component: () => import('../pages/admin/UserManagement.vue')
      },
      {
        path: 'products',
        name: 'ProductManagement',
        component: () => import('../pages/admin/ProductManagement.vue')
      },
      {
        path: 'merchants',
        name: 'MerchantManagement',
        component: () => import('../pages/admin/MerchantManagement.vue')
      },
      {
        path: 'product-merchant',
        name: 'ProductMerchantManagement',
        component: () => import('../pages/admin/ProductMerchantManagement.vue')
      },
      {
        path: 'orders',
        name: 'OrderManagement',
        component: () => import('../pages/admin/OrderManagement.vue')
      },
      {
        path: 'announcements',
        name: 'AnnouncementManagement',
        component: () => import('../pages/admin/AnnouncementManagement.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('grain_oil_token')
  if (to.path === '/') {
    next()
  } else if (!token) {
    next('/')
  } else {
    next()
  }
})

export default router 