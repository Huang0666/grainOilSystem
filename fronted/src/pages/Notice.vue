<template>
  <div class="notice-page page-container">
    <div class="page-header">
      <h2>系统公告</h2>
    </div>

    <div class="notice-list content-area">
      <a-list :data-source="noticeList" :loading="loading" item-layout="horizontal">
        <template #renderItem="{ item }">
          <a-list-item>
            <a-list-item-meta>
              <template #title>
                <a @click="showNoticeDetail(item)">{{ item.title }}</a>
              </template>
              <template #description>
                <div class="notice-meta">
                  <span class="notice-time">{{ formatDate(item.createTime) }}</span>
                  <a-tag :color="item.isTop === 1 ? 'volcano' : 'blue'">
                    {{ item.isTop === 1 ? '置顶' : '普通' }}
                  </a-tag>
                </div>
              </template>
            </a-list-item-meta>
             <template #actions>
              <a-button type="link" @click="showNoticeDetail(item)">查看详情</a-button>
            </template>
          </a-list-item>
        </template>
        <template #empty>
          <a-empty description="暂无公告" />
        </template>
      </a-list>

      <!-- 分页组件 -->
      <div class="pagination-container">
        <a-pagination
          v-model:current="currentPage"
          :total="total"
          :pageSize="pageSize"
          show-size-changer
          show-quick-jumper
          :show-total="total => `共 ${total} 条`"
          @change="handlePageChange"
          @showSizeChange="handleSizeChange"
          :locale="{
            items_per_page: '条/页',
            jump_to: '跳至',
            jump_to_confirm: '确定',
            page: '页'
          }"
        />
      </div>
    </div>

    <!-- 公告详情弹窗 -->
    <a-modal
      v-model:visible="detailVisible"
      :title="currentNotice?.title"
      :footer="null"
      width="700px"
      wrap-class-name="notice-detail-modal"
    >
      <div class="notice-detail-content">
        <div class="notice-info">
          <span class="notice-time">发布时间：{{ formatDate(currentNotice?.createTime) }}</span>
          <a-tag :color="currentNotice?.isTop === 1 ? 'volcano' : 'blue'">
            {{ currentNotice?.isTop === 1 ? '置顶' : '普通' }}
          </a-tag>
        </div>
        <a-divider />
        <div class="content-main" v-html="currentNotice?.content"></div>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getNoticeList, getNoticeDetail } from '../api/notice'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import dayjs from 'dayjs'

const router = useRouter()
const loading = ref(false)
const detailVisible = ref(false)
const currentNotice = ref(null)
const noticeList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 检查登录状态
const checkLogin = () => {
  const userInfo = localStorage.getItem('grain_oil_user')
  if (!userInfo) {
    message.error('请先登录')
    router.push('/login')
    return false
  }
  return true
}

// 添加日期格式化函数
const formatDate = (dateString) => {
  if (!dateString) return ''
  return dayjs(dateString).format('YYYY-MM-DD HH:mm')
}

// 获取公告列表
const fetchNoticeList = async () => {
  if (!checkLogin()) return

  try {
    loading.value = true
    const { data } = await getNoticeList({
      page: currentPage.value,
      size: pageSize.value
    })

    if (data.code === 0) {
      noticeList.value = data.data.list
      total.value = data.data.total
    } else {
      message.error(data.msg || '获取公告列表失败')
    }
  } catch (error) {
    console.error('获取公告列表失败:', error)
    if (error.message === '请先登录') {
      router.push('/login')
    } else {
      message.error('获取公告列表失败，请稍后重试')
    }
  } finally {
    loading.value = false
  }
}

// 显示公告详情
const showNoticeDetail = async (notice) => {
  if (!checkLogin()) return

  try {
    const { data } = await getNoticeDetail(notice.id)
    if (data.code === 0) {
      currentNotice.value = data.data
  detailVisible.value = true
    } else {
      message.error(data.msg || '获取公告详情失败')
    }
  } catch (error) {
    console.error('获取公告详情失败:', error)
    if (error.message === '请先登录') {
      router.push('/login')
    } else {
      message.error('获取公告详情失败，请稍后重试')
    }
  }
}

// 分页改变
const handlePageChange = (page, size) => {
  currentPage.value = page
  if (size) pageSize.value = size
  fetchNoticeList()
}

// 每页条数改变
const handleSizeChange = (current, size) => {
  currentPage.value = 1
  pageSize.value = size
  fetchNoticeList()
}

// 页面加载时获取公告列表
onMounted(() => {
  fetchNoticeList()
})
</script>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: #fff;
  padding: 24px;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
}

.content-area {
  flex: 1;
  overflow-y: auto;
}

.page-header {
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 16px;
}

.notice-meta {
  display: flex;
  align-items: center;
  gap: 16px;
}

.notice-time {
  color: #999;
}

.notice-detail-content {
  padding: 0 8px;
}

.notice-info {
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
  color: #666;
  font-size: 13px;
}

.content-main {
  line-height: 1.8;
  margin-top: 16px;
  max-height: 60vh;
  overflow-y: auto;
}

:deep(.ant-list-item-meta-title) {
  margin-bottom: 8px !important;
  font-size: 16px;
}

:deep(.ant-list-item-meta-title a) {
  color: rgba(0, 0, 0, 0.85);
}

:deep(.ant-list-item-meta-title a:hover) {
  color: #1890ff;
}

:deep(.notice-detail-modal .ant-modal-body) {
  padding-top: 16px; 
}

:deep(.ant-list-item) {
  padding: 16px !important;
}

.pagination-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 16px;
  background: #fff;
  border-top: 1px solid #f0f0f0;
  margin-top: 16px;
}

/* 滚动条美化 */
.content-area {
  scrollbar-width: thin;
  scrollbar-color: #d4d7de #fff;
}

.content-area::-webkit-scrollbar {
  width: 8px;
  background: transparent;
}

.content-area::-webkit-scrollbar-thumb {
  background: #d4d7de;
  border-radius: 6px;
}

.content-area::-webkit-scrollbar-thumb:hover {
  background: #bfc2c9;
}

.content-area::-webkit-scrollbar-track {
  background: transparent;
}
</style> 