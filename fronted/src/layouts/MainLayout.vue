<template>
  <a-layout class="main-layout">
    <a-layout-sider v-model:collapsed="collapsed" collapsible theme="dark" width="256">
      <div class="logo">
        <img src="../assets/logo.svg" alt="Logo" v-if="!collapsed" />
        <img src="../assets/logo-small.svg" alt="Logo" v-else />
      </div>
      <a-menu
        v-model:selectedKeys="selectedKeys"
        theme="dark"
        mode="inline"
        @click="handleMenuClick"
      >
        <a-menu-item key="forum">
          <template #icon>
            <home-outlined />
          </template>
          <span>论坛首页</span>
        </a-menu-item>
        <a-menu-item key="trend">
          <template #icon>
            <line-chart-outlined />
          </template>
          <span>粮油趋势</span>
        </a-menu-item>
        <a-menu-item key="notice">
          <template #icon>
            <notification-outlined />
          </template>
          <span>公告</span>
        </a-menu-item>
        <a-menu-item key="order">
          <template #icon>
            <shopping-outlined />
          </template>
          <span>我的订单</span>
        </a-menu-item>
      </a-menu>
    </a-layout-sider>
    <a-layout>
      <a-layout-header class="header">
        <div class="header-left">
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
        </div>
        <div class="header-right">
          <a-dropdown>
            <div class="user-info">
              <a-avatar :size="32" :style="{ backgroundColor: '#1890ff' }">
                {{ userInfo?.username?.charAt(0)?.toUpperCase() }}
              </a-avatar>
              <span class="username">{{ userInfo?.username }}</span>
            </div>
            <template #overlay>
              <a-menu>
                <a-menu-item key="profile">
                  <user-outlined />
                  <span>个人信息</span>
                </a-menu-item>
                <a-menu-item key="logout" @click="handleLogout">
                  <logout-outlined />
                  <span>退出登录</span>
                </a-menu-item>
              </a-menu>
            </template>
          </a-dropdown>
        </div>
      </a-layout-header>
      <a-layout-content class="content">
        <router-view v-slot="{ Component }">
          <keep-alive>
            <component :is="Component" />
          </keep-alive>
        </router-view>
      </a-layout-content>
    </a-layout>
  </a-layout>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  HomeOutlined,
  LineChartOutlined,
  NotificationOutlined,
  ShoppingOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  UserOutlined,
  LogoutOutlined
} from '@ant-design/icons-vue'

const router = useRouter()
const route = useRoute()

const collapsed = ref(false)
const selectedKeys = ref([])
const userInfo = ref(null)

// 根据当前路由设置选中的菜单项
const updateSelectedKeys = () => {
  const currentPath = route.path
  if (currentPath.includes('/user/trend')) {
    selectedKeys.value = ['trend']
  } else if (currentPath.includes('/user/notice')) {
    selectedKeys.value = ['notice']
  } else if (currentPath.includes('/user/order')) {
    selectedKeys.value = ['order']
  } else if (currentPath.includes('/user')) { // 默认论坛首页
    selectedKeys.value = ['forum']
  }
}

// 监听路由变化
watch(route, () => {
  updateSelectedKeys()
})

onMounted(() => {
  const savedUser = localStorage.getItem('grain_oil_user')
  if (savedUser) {
    userInfo.value = JSON.parse(savedUser)
  }
  updateSelectedKeys() // 初始化时设置菜单项
})

const handleMenuClick = (e) => {
  switch (e.key) {
    case 'forum':
      router.push('/user')
      break
    case 'trend':
      router.push('/user/trend')
      break
    case 'notice':
      router.push('/user/notice')
      break
    case 'order':
      router.push('/user/order')
      break
  }
}

function handleLogout() {
  localStorage.removeItem('grain_oil_token')
  localStorage.removeItem('grain_oil_user')
  router.push('/')
}
</script>

<style scoped>
.main-layout {
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

.header {
  background: #fff;
  padding: 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  height: 64px;
  position: fixed;
  top: 0;
  right: 0;
  width: calc(100% - 256px);
  z-index: 2;
  transition: all 0.2s;
}

.header.collapsed {
  width: calc(100% - 80px);
}

.header-left {
  padding-left: 24px;
}

.header-right {
  padding-right: 24px;
}

.trigger {
  font-size: 18px;
  cursor: pointer;
  transition: color 0.3s;
}

.trigger:hover {
  color: #1890ff;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 0 12px;
  height: 100%;
}

.user-info:hover {
  background: rgba(0,0,0,0.025);
}

.username {
  margin-left: 8px;
  font-size: 14px;
}

.content {
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
  margin-left: 256px;
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