<template>
  <div class="merchant-management">
    <div class="table-operations">
      <a-space>
        <a-button type="primary" @click="showAddModal">
          <template #icon><plus-outlined /></template>
          添加商家
        </a-button>
        <a-input-search
          v-model:value="searchText"
          placeholder="搜索商家名称/手机号"
          style="width: 200px"
          @search="onSearch"
        />
      </a-space>
    </div>

    <a-table
      :columns="columns"
      :data-source="merchantList"
      :loading="loading"
      :pagination="pagination"
      @change="handleTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'status'">
          <a-tag color="green">正常</a-tag>
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a @click="showEditModal(record)">编辑</a>
            <a-divider type="vertical" />
            <a-popconfirm
              title="确定要删除这个商家吗？"
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
        <a-form-item label="商家名称" name="name">
          <a-input v-model:value="formState.name" />
        </a-form-item>
        <a-form-item label="联系电话" name="phone">
          <a-input v-model:value="formState.phone" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import { getMerchantList, addMerchant, updateMerchant, deleteMerchant, type Merchant } from '@/api/adminmerchant'

const columns = [
  {
    title: '商家名称',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: '联系电话',
    dataIndex: 'phone',
    key: 'phone',
  },
  {
    title: '状态',
    key: 'status',
  },
  {
    title: '操作',
    key: 'action',
  },
]

const searchText = ref('')
const loading = ref(false)
const merchantList = ref<Merchant[]>([])
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
})

const modalVisible = ref(false)
const modalTitle = ref('添加商家')
const formRef = ref()
const formState = ref({
  name: '',
  phone: '',
})

const rules = {
  name: [{ required: true, message: '请输入商家名称' }],
  phone: [{ required: true, message: '请输入联系电话' }],
}

onMounted(() => {
  fetchMerchantList()
})

async function fetchMerchantList() {
  loading.value = true
  try {
    const response = await getMerchantList({
      page: pagination.value.current,
      size: pagination.value.pageSize,
      name: searchText.value || undefined,
      phone: searchText.value || undefined
    })
    if (response.data.code === 0) {
      merchantList.value = response.data.data.list
      pagination.value.total = response.data.data.total
    } else {
      message.error(response.data.msg)
    }
  } catch (error) {
    console.error('获取商家列表失败:', error)
    message.error('获取商家列表失败')
  } finally {
    loading.value = false
  }
}

function showAddModal() {
  modalTitle.value = '添加商家'
  formState.value = {
    name: '',
    phone: '',
  }
  modalVisible.value = true
}

function showEditModal(record: Merchant) {
  modalTitle.value = '编辑商家'
  formState.value = { ...record }
  modalVisible.value = true
}

async function handleModalOk() {
  try {
    await formRef.value.validate()
    const merchantData = { ...formState.value }
    
    const response = await (modalTitle.value === '添加商家' 
      ? addMerchant(merchantData)
      : updateMerchant(merchantData))

    if (response.data.code === 0) {
      message.success(modalTitle.value === '添加商家' ? '添加成功' : '编辑成功')
      modalVisible.value = false
      fetchMerchantList()
    } else {
      message.error(response.data.msg)
    }
  } catch (error) {
    console.error('操作失败:', error)
    message.error('操作失败')
  }
}

function handleModalCancel() {
  modalVisible.value = false
  formRef.value?.resetFields()
}

async function handleDelete(record: Merchant) {
  if (!record.id) {
    message.error('商家ID不能为空')
    return
  }
  
  try {
    const response = await deleteMerchant(record.id)
    if (response.data.code === 0) {
      message.success('删除成功')
      fetchMerchantList()
    } else {
      message.error(response.data.msg)
    }
  } catch (error) {
    console.error('删除商家失败:', error)
    message.error('删除商家失败')
  }
}

async function toggleStatus(record: Merchant) {
  message.success('商家状态已更新')
  await fetchMerchantList()
}

function onSearch() {
  pagination.value.current = 1
  fetchMerchantList()
}

function handleTableChange(pag) {
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
  fetchMerchantList()
}
</script>

<style scoped>
.merchant-management {
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