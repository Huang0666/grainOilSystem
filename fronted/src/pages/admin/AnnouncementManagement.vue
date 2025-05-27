<template>
  <div class="announcement-management">
    <div class="table-operations">
      <a-space>
        <a-button type="primary" @click="showAddModal">
          <template #icon><plus-outlined /></template>
          发布公告
        </a-button>
        <a-input-search
          v-model:value="searchTitle"
          placeholder="搜索公告标题"
          style="width: 200px"
          @search="onSearch"
        />
      </a-space>
    </div>

    <a-table
      :columns="columns"
      :data-source="announcementList"
      :loading="loading"
      :pagination="pagination"
      @change="handleTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <a-tag color="blue" v-if="record.isTop">置顶</a-tag>
        </template>
        <template v-if="column.key === 'importance'">
          <a-tag :color="record.isTop ? 'red' : 'default'">
            {{ record.isTop ? '重要' : '普通' }}
          </a-tag>
        </template>
        <template v-if="column.key === 'validTime'">
          {{ getValidTimeRange(record.createTime) }}
        </template>
        <template v-if="column.key === 'content'">
          <span class="content-preview">{{ record.content }}</span>
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a @click="showEditModal(record)">编辑</a>
            <a-divider type="vertical" />
            <a-popconfirm
              title="确定要删除这条公告吗？"
              @confirm="handleDelete(record)"
              ok-text="确定"
              cancel-text="取消"
            >
              <a class="danger-link">删除</a>
            </a-popconfirm>
          </a-space>
        </template>
      </template>
    </a-table>

    <a-modal
      v-model:open="modalVisible"
      :title="modalTitle"
      @ok="handleModalOk"
      @cancel="handleModalCancel"
      width="800px"
    >
      <a-form
        ref="formRef"
        :model="formState"
        :rules="rules"
        :label-col="{ span: 4 }"
        :wrapper-col="{ span: 20 }"
      >
        <a-form-item label="标题" name="title">
          <a-input v-model:value="formState.title" placeholder="请输入公告标题" />
        </a-form-item>
        <a-form-item label="内容" name="content">
          <a-textarea
            v-model:value="formState.content"
            :rows="6"
            placeholder="请输入公告内容"
          />
        </a-form-item>
        <a-form-item label="重要程度" name="isTop">
          <a-radio-group v-model:value="formState.isTop">
            <a-radio :value="0">普通</a-radio>
            <a-radio :value="1">重要（置顶）</a-radio>
          </a-radio-group>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import type { AdminAnnouncement } from '@/api/adminannouncement'
import {
  getAnnouncementList,
  addAnnouncement,
  updateAnnouncement,
  deleteAnnouncement,
  updateAnnouncementTop
} from '@/api/adminannouncement'

const columns = [
  {
    title: '标题',
    dataIndex: 'title',
    key: 'title',
  },
  {
    title: '内容',
    dataIndex: 'content',
    key: 'content',
    ellipsis: true,
    width: '300px'
  },
  {
    title: '发布时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: '180px'
  },
  {
    title: '有效期',
    key: 'validTime',
    width: '200px'
  },
  {
    title: '重要程度',
    key: 'importance',
    width: '100px'
  },
  {
    title: '状态',
    key: 'status',
    width: '80px'
  },
  {
    title: '操作',
    key: 'action',
    width: '150px',
    fixed: 'right'
  },
]

const searchTitle = ref('')
const loading = ref(false)
const announcementList = ref<AdminAnnouncement[]>([])
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
})

const modalVisible = ref(false)
const modalTitle = ref('发布公告')
const formRef = ref()
const formState = ref({
  id: undefined as number | undefined,
  title: '',
  content: '',
  isTop: 0,
})

const rules = {
  title: [{ required: true, message: '请输入公告标题' }],
  content: [{ required: true, message: '请输入公告内容' }],
  isTop: [{ required: true, message: '请选择重要程度' }],
}

onMounted(() => {
  fetchAnnouncementList()
})

function getValidTimeRange(createTime: string) {
  const start = new Date(createTime)
  const end = new Date(start)
  end.setDate(end.getDate() + 14)
  return `${start.toLocaleDateString()} 至 ${end.toLocaleDateString()}`
}

async function fetchAnnouncementList() {
  loading.value = true
  try {
    const response = await getAnnouncementList({
      page: pagination.value.current,
      size: pagination.value.pageSize,
      title: searchTitle.value || undefined
    })
    
    if (response.data.code === 0 && response.data.data) {
      announcementList.value = response.data.data.list.map((item: any) => ({
        ...item,
        createTime: item.create_time || item.createTime // 处理后端返回的可能不同的时间字段名
      }))
      pagination.value.total = response.data.data.total
    } else {
      message.error(response.data.msg || '获取公告列表失败')
    }
  } catch (error) {
    console.error('获取公告列表失败:', error)
    message.error('获取公告列表失败')
  } finally {
    loading.value = false
  }
}

function showAddModal() {
  modalTitle.value = '发布公告'
  formState.value = {
    id: undefined,
    title: '',
    content: '',
    isTop: 0,
  }
  modalVisible.value = true
}

function showEditModal(record: AdminAnnouncement) {
  modalTitle.value = '编辑公告'
  formState.value = {
    id: record.id,
    title: record.title,
    content: record.content,
    isTop: record.isTop,
  }
  modalVisible.value = true
}

async function handleModalOk() {
  try {
    await formRef.value.validate()
    if (modalTitle.value === '发布公告') {
      const response = await addAnnouncement({
        title: formState.value.title,
        content: formState.value.content,
        isTop: formState.value.isTop
      })
      if (response.data.code === 0) {
        message.success('发布成功')
        modalVisible.value = false
        fetchAnnouncementList()
      } else {
        message.error(response.data.msg || '发布失败')
      }
    } else {
      // 如果是编辑模式，且重要程度发生变化，需要调用置顶接口
      const currentAnnouncement = announcementList.value.find(item => item.id === formState.value.id)
      if (currentAnnouncement && currentAnnouncement.isTop !== formState.value.isTop) {
        const topResponse = await updateAnnouncementTop({
          id: formState.value.id!,
          isTop: formState.value.isTop
        })
        if (topResponse.data.code !== 0) {
          message.error(topResponse.data.msg || '更新重要程度失败')
          return
        }
      }

      const response = await updateAnnouncement({
        id: formState.value.id!,
        title: formState.value.title,
        content: formState.value.content,
        isTop: formState.value.isTop,
        createTime: '' // 后端会忽略这个字段
      })
      if (response.data.code === 0) {
        message.success('更新成功')
        modalVisible.value = false
        fetchAnnouncementList()
      } else {
        message.error(response.data.msg || '更新失败')
      }
    }
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

function handleModalCancel() {
  modalVisible.value = false
  formRef.value?.resetFields()
}

async function handleDelete(record: AdminAnnouncement) {
  try {
    const response = await deleteAnnouncement(record.id)
    if (response.data.code === 0) {
      message.success('删除成功')
      fetchAnnouncementList()
    } else {
      message.error(response.data.msg || '删除失败')
    }
  } catch (error) {
    console.error('删除失败:', error)
    message.error('删除失败')
  }
}

function onSearch() {
  pagination.value.current = 1
  fetchAnnouncementList()
}

function handleTableChange(pag: any) {
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
  fetchAnnouncementList()
}
</script>

<style scoped>
.announcement-management {
  padding: 24px;
}

.table-operations {
  margin-bottom: 16px;
}

.danger-link {
  color: #ff4d4f;
}

.danger-link:hover {
  color: #ff7875;
}

.content-preview {
  display: inline-block;
  max-width: 300px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style> 