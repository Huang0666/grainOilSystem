<template>
  <div class="user-management">
    <div class="table-operations">
      <a-space>
        <a-button type="primary" @click="showAddModal">
          <template #icon><plus-outlined /></template>
          添加用户
        </a-button>
        <a-input-search
          v-model:value="searchText"
          placeholder="搜索用户名"
          style="width: 200px"
          @search="onSearch"
        />
        <a-select
          v-model:value="searchRole"
          style="width: 120px"
          placeholder="角色"
          allowClear
          @change="onSearch"
        >
          <a-select-option value="user">普通用户</a-select-option>
          <a-select-option value="admin">管理员</a-select-option>
        </a-select>
      </a-space>
    </div>

    <a-table
      :columns="columns"
      :data-source="userList"
      :loading="loading"
      :pagination="pagination"
      @change="handleTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'role'">
          <a-tag :color="record.role === 'admin' ? 'blue' : 'green'">
            {{ record.role === 'admin' ? '管理员' : '普通用户' }}
          </a-tag>
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a @click="showEditModal(record)">编辑</a>
            <a-divider type="vertical" />
            <a @click="showResetPasswordModal(record)">重置密码</a>
            <a-divider type="vertical" />
            <a-popconfirm
              title="确定要删除这个用户吗？"
              @confirm="handleDelete(record)"
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
    >
      <a-form
        ref="formRef"
        :model="formState"
        :rules="rules"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-form-item label="用户名" name="username">
          <a-input v-model:value="formState.username" :disabled="modalTitle === '编辑用户'" />
        </a-form-item>
        <a-form-item 
          label="密码" 
          name="password"
          v-if="modalTitle === '添加用户'"
        >
          <a-input-password v-model:value="formState.password" />
        </a-form-item>
        <a-form-item label="手机号" name="phone">
          <a-input v-model:value="formState.phone" />
        </a-form-item>
        <a-form-item label="角色" name="role">
          <a-select v-model:value="formState.role">
            <a-select-option value="user">普通用户</a-select-option>
            <a-select-option value="admin">管理员</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="resetPasswordVisible"
      title="重置密码"
      @ok="handleResetPasswordOk"
      @cancel="handleResetPasswordCancel"
    >
      <a-form
        ref="resetPasswordFormRef"
        :model="resetPasswordForm"
        :rules="resetPasswordRules"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-form-item label="新密码" name="password">
          <a-input-password v-model:value="resetPasswordForm.password" />
        </a-form-item>
        <a-form-item label="确认密码" name="confirmPassword">
          <a-input-password v-model:value="resetPasswordForm.confirmPassword" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import { getUserList, addUser, updateUser, deleteUser, resetUserPassword } from '@/api/user'
import type { User } from '@/api/user'

const columns = [
  {
    title: '用户名',
    dataIndex: 'username',
    key: 'username',
  },
  {
    title: '手机号',
    dataIndex: 'phone',
    key: 'phone',
  },
  {
    title: '角色',
    dataIndex: 'role',
    key: 'role',
  },
  {
    title: '操作',
    key: 'action',
  },
]

const searchText = ref('')
const searchRole = ref<string>()
const loading = ref(false)
const userList = ref<User[]>([])
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
})

const modalVisible = ref(false)
const modalTitle = ref('添加用户')
const formRef = ref()
const formState = ref<User>({
  username: '',
  password: '',
  phone: '',
  role: 'user',
})

const rules = {
  username: [{ required: true, message: '请输入用户名' }],
  password: [{ required: true, message: '请输入密码', min: 6, message: '密码不能低于6位' }],
  phone: [{ required: true, message: '请输入手机号' }],
  role: [{ required: true, message: '请选择角色' }],
}

const resetPasswordVisible = ref(false)
const resetPasswordFormRef = ref()
const currentUserId = ref<number>()
const resetPasswordForm = ref({
  password: '',
  confirmPassword: '',
})

const resetPasswordRules = {
  password: [
    { required: true, message: '请输入新密码' },
    { min: 6, message: '密码不能低于6位' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码' },
    {
      validator: async (rule: any, value: string) => {
        if (value && value !== resetPasswordForm.value.password) {
          throw new Error('两次输入的密码不一致')
        }
      }
    }
  ]
}

onMounted(() => {
  fetchUserList()
})

async function fetchUserList() {
  loading.value = true
  try {
    const response = await getUserList({
      page: pagination.value.current,
      size: pagination.value.pageSize,
      username: searchText.value || undefined,
      role: searchRole.value
    })
    if (response.data.code === 0) {
      userList.value = response.data.data.list
      pagination.value.total = response.data.data.total
    } else {
      message.error(response.data.msg)
    }
  } catch (error) {
    console.error('获取用户列表失败:', error)
    message.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

function showAddModal() {
  modalTitle.value = '添加用户'
  formState.value = {
    username: '',
    password: '',
    phone: '',
    role: 'user',
  }
  modalVisible.value = true
}

function showEditModal(record: User) {
  modalTitle.value = '编辑用户'
  formState.value = { ...record }
  modalVisible.value = true
}

function showResetPasswordModal(record: User) {
  currentUserId.value = record.id
  resetPasswordForm.value = {
    password: '',
    confirmPassword: '',
  }
  resetPasswordVisible.value = true
}

async function handleModalOk() {
  try {
    await formRef.value.validate()
    if (modalTitle.value === '添加用户') {
      const response = await addUser(formState.value)
      if (response.data.code === 0) {
        message.success('添加成功')
        modalVisible.value = false
        fetchUserList()
      } else {
        message.error(response.data.msg)
      }
    } else {
      const response = await updateUser(formState.value)
      if (response.data.code === 0) {
        message.success('编辑成功')
        modalVisible.value = false
        fetchUserList()
      } else {
        message.error(response.data.msg)
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

async function handleResetPasswordOk() {
  try {
    await resetPasswordFormRef.value.validate()
    const response = await resetUserPassword(currentUserId.value!, resetPasswordForm.value.password)
    if (response.data.code === 0) {
      message.success('密码重置成功')
      resetPasswordVisible.value = false
    } else {
      message.error(response.data.msg)
    }
  } catch (error) {
    console.error('表单验证失败:', error)
  }
}

function handleResetPasswordCancel() {
  resetPasswordVisible.value = false
  resetPasswordFormRef.value?.resetFields()
}

async function handleDelete(record: User) {
  try {
    const response = await deleteUser(record.id!)
    if (response.data.code === 0) {
      message.success('删除成功')
      fetchUserList()
    } else {
      message.error(response.data.msg)
    }
  } catch (error) {
    console.error('删除用户失败:', error)
    message.error('删除用户失败')
  }
}

function onSearch() {
  pagination.value.current = 1
  fetchUserList()
}

function handleTableChange(pag: any) {
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
  fetchUserList()
}
</script>

<style scoped>
.user-management {
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
</style> 