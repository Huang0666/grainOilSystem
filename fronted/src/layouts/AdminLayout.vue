<template>
  <a-layout class="admin-layout">
    <a-layout-sider v-model:collapsed="collapsed" :trigger="null" collapsible>
      <div class="logo">
        <img src="@/assets/logo.svg" alt="logo" v-if="!collapsed"/>
        <img src="@/assets/logo-small.svg" alt="logo" v-else/>
      </div>
      <a-menu
        v-model:selectedKeys="selectedKeys"
        theme="dark"
        mode="inline"
        @click="handleMenuClick"
      >
        <a-menu-item key="dashboard">
          <template #icon><DashboardOutlined /></template>
          <span>数据概览</span>
        </a-menu-item>
        <a-menu-item key="users">
          <template #icon><UserOutlined /></template>
          <span>用户管理</span>
        </a-menu-item>
        <a-menu-item key="products">
          <template #icon><ShoppingOutlined /></template>
          <span>商品管理</span>
        </a-menu-item>
        <a-menu-item key="merchants">
          <template #icon><ShopOutlined /></template>
          <span>商家管理</span>
        </a-menu-item>
        <a-menu-item key="product-merchant">
          <template #icon><AppstoreOutlined /></template>
          <span>商品上架</span>
        </a-menu-item>
        <a-menu-item key="orders">
          <template #icon><OrderedListOutlined /></template>
          <span>订单管理</span>
        </a-menu-item>
        <a-menu-item key="announcements">
          <template #icon><NotificationOutlined /></template>
          <span>公告管理</span>
        </a-menu-item>
      </a-menu>
    </a-layout-sider>
    <a-layout>
      <a-layout-header :class="['admin-header', { collapsed }]">
        <menu-unfold-outlined
          v-if="collapsed"
          class="trigger"
          @click="() => (collapsed = !collapsed)"
        />
        <menu-fold-outlined
          v-else
          class="trigger"
          @click="() => (collapsed = !collapsed)"
        />
        <div class="header-right">
          <a-dropdown>
            <a class="ant-dropdown-link" @click.prevent>
              {{ adminInfo?.username }}
              <down-outlined />
            </a>
            <template #overlay>
              <a-menu>
                <a-menu-item key="logout" @click="handleLogout">
                  退出登录
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </a-layout-header>
      <a-layout-content class="admin-content">
        <router-view></router-view>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  DashboardOutlined,
  UserOutlined,
  ShoppingOutlined,
  ShopOutlined,
  AppstoreOutlined,
  OrderedListOutlined,
  NotificationOutlined,
  DownOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()
const collapsed = ref(false)
const selectedKeys = ref(['dashboard'])
const adminInfo = ref(null)

// 根据当前路由设置选中的菜单项
const updateSelectedKeys = () => {
  const currentPath = route.path
  if (currentPath.includes('/admin/users')) {
    selectedKeys.value = ['users']
  } else if (currentPath.includes('/admin/products')) {
    selectedKeys.value = ['products']
  } else if (currentPath.includes('/admin/merchants')) {
    selectedKeys.value = ['merchants']
  } else if (currentPath.includes('/admin/product-merchant')) {
    selectedKeys.value = ['product-merchant']
  } else if (currentPath.includes('/admin/orders')) {
    selectedKeys.value = ['orders']
  } else if (currentPath.includes('/admin/announcements')) {
    selectedKeys.value = ['announcements']
  } else {
    selectedKeys.value = ['dashboard']
  }
}

// 监听路由变化
watch(route, () => {
  updateSelectedKeys()
})

onMounted(() => {
  const userStr = localStorage.getItem('grain_oil_user')
  if (userStr) {
    adminInfo.value = JSON.parse(userStr)
  }
  updateSelectedKeys() // 初始化时设置菜单项
})

const handleMenuClick = (e) => {
  switch (e.key) {
    case 'dashboard':
      router.push('/admin')
      break
    case 'users':
      router.push('/admin/users')
      break
    case 'products':
      router.push('/admin/products')
      break
    case 'merchants':
      router.push('/admin/merchants')
      break
    case 'product-merchant':
      router.push('/admin/product-merchant')
      break
    case 'orders':
      router.push('/admin/orders')
      break
    case 'announcements':
      router.push('/admin/announcements')
      break
  }
}

const handleLogout = () => {
  localStorage.removeItem('grain_oil_token')
  localStorage.removeItem('grain_oil_user')
  router.push('/')
}
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
  width: 100%;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
}

.logo {
  height: 64px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.2);
  margin: 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo img {
  height: 32px;
}

.admin-header {
  background: #fff;
  padding: 0 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  height: 64px;
  position: fixed;
  top: 0;
  right: 0;
  width: calc(100% - 200px);
  z-index: 2;
  transition: all 0.2s;
}

.admin-header.collapsed {
  width: calc(100% - 80px);
}

.trigger {
  font-size: 18px;
  cursor: pointer;
  transition: color 0.3s;
}

.trigger:hover {
  color: #1890ff;
}

.header-right {
  display: flex;
  align-items: center;
}

.ant-dropdown-link {
  color: rgba(0, 0, 0, 0.85);
  cursor: pointer;
  padding: 0 12px;
  height: 100%;
  display: flex;
  align-items: center;
}

.ant-dropdown-link:hover {
  background: rgba(0,0,0,0.025);
}

.admin-content {
  margin: 64px 16px 16px;
  padding: 16px;
  background: #f0f2f5;
  min-height: calc(100vh - 64px);
  position: relative;
  overflow-y: auto;
}

/* 确保内容区域铺满 */
:deep(.ant-layout) {
  height: 100vh;
  overflow: hidden;
}

:deep(.ant-layout-has-sider) {
  height: 100%;
}

:deep(.ant-layout-sider) {
  position: fixed;
  height: 100vh;
  left: 0;
  top: 0;
  bottom: 0;
  box-shadow: 2px 0 8px 0 rgba(29,35,41,.05);
}

:deep(.ant-layout-sider-children) {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

:deep(.ant-menu) {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
}

/* 主体内容区域布局 */
:deep(.ant-layout-content) {
  margin-left: 200px;
  transition: margin-left 0.2s;
  height: 100%;
  overflow: auto;
}

/* 侧边栏收起时的样式 */
:deep(.ant-layout-sider-collapsed + .ant-layout .ant-layout-content) {
  margin-left: 80px;
}

/* 确保子页面内容区域铺满 */
:deep(.page-container) {
  min-height: 100%;
  background: #fff;
  padding: 24px;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
}
</style> 