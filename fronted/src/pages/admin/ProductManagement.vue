<template>
  <div class="product-management">
    <div class="table-operations">
      <a-space>
        <a-button type="primary" @click="showAddModal">
          <template #icon><plus-outlined /></template>
          添加商品
        </a-button>
        <a-input-search
          v-model:value="searchText"
          placeholder="搜索商品名称"
          style="width: 200px"
          @search="onSearch"
        />
        <a-select
          v-model:value="searchType"
          style="width: 120px"
          placeholder="商品类型"
          allowClear
          @change="onSearch"
        >
          <a-select-option v-for="type in productTypes" :key="type" :value="type">
            {{ type }}
          </a-select-option>
        </a-select>
      </a-space>
    </div>

    <a-table
      :columns="columns"
      :data-source="productList"
      :loading="loading"
      :pagination="pagination"
      @change="handleTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'type'">
          <a-tag :color="record.type === '粮食' ? 'blue' : 'green'">
            {{ record.type }}
          </a-tag>
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a @click="showEditModal(record)">编辑</a>
            <a-divider type="vertical" />
            <a-popconfirm
              title="确定要删除这个商品吗？"
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
        <a-form-item label="商品名称" name="name">
          <a-input v-model:value="formState.name" />
        </a-form-item>
        <a-form-item label="商品类型" name="type">
          <a-select v-model:value="formState.type">
            <a-select-option v-for="type in productTypes" :key="type" :value="type">
              {{ type }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="商品描述" name="description">
          <a-textarea v-model:value="formState.description" :rows="4" />
        </a-form-item>
        <a-form-item label="商品图片" name="image">
          <a-upload
            v-model:file-list="fileList"
            list-type="picture-card"
            :max-count="1"
            :before-upload="beforeUpload"
          >
            <div v-if="fileList.length < 1">
              <plus-outlined />
              <div style="margin-top: 8px">上传</div>
            </div>
          </a-upload>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import { getProductList, addProduct, updateProduct, getProductDetail, deleteProduct, getProductTypes } from '@/api/adminproduct'
import type { Product } from '@/api/adminproduct'
import type { UploadFile } from 'ant-design-vue'

const columns = [
  {
    title: '商品名称',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: '商品类型',
    dataIndex: 'type',
    key: 'type',
  },
  {
    title: '创建时间',
    dataIndex: 'createTime',
    key: 'createTime',
  },
  {
    title: '操作',
    key: 'action',
  },
]

const searchText = ref('')
const searchType = ref<string>()
const loading = ref(false)
const productList = ref<Product[]>([])
const productTypes = ref<string[]>([])
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
})

const modalVisible = ref(false)
const modalTitle = ref('添加商品')
const formRef = ref()
const formState = ref<Product>({
  name: '',
  type: undefined,
  description: '',
  image: '',
})
const fileList = ref<UploadFile[]>([])

const rules = {
  name: [{ required: true, message: '请输入商品名称' }],
  type: [{ required: true, message: '请选择商品类型' }],
  description: [{ required: true, message: '请输入商品描述' }],
}

onMounted(() => {
  fetchProductList()
  fetchProductTypes()
})

async function fetchProductList() {
  loading.value = true
  try {
    const response = await getProductList({
      page: pagination.value.current,
      size: pagination.value.pageSize,
      name: searchText.value || undefined,
      type: searchType.value
    })
    if (response.data.code === 0) {
      productList.value = response.data.data.list
      pagination.value.total = response.data.data.total
    } else {
      message.error(response.data.msg)
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
    message.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

async function fetchProductTypes() {
  try {
    const response = await getProductTypes()
    if (response.data.code === 0) {
      productTypes.value = response.data.data
    } else {
      message.error(response.data.msg)
    }
  } catch (error) {
    console.error('获取商品类型列表失败:', error)
    message.error('获取商品类型列表失败')
  }
}

function showAddModal() {
  modalTitle.value = '添加商品'
  formState.value = {
    name: '',
    type: undefined,
    description: '',
    image: '',
  }
  fileList.value = []
  modalVisible.value = true
}

function showEditModal(record: Product) {
  modalTitle.value = '编辑商品'
  formState.value = { ...record }
  fileList.value = record.image ? [{ uid: '-1', name: 'image.png', status: 'done', url: record.image }] : []
  modalVisible.value = true
}

async function handleModalOk() {
  try {
    await formRef.value.validate()
    if (modalTitle.value === '添加商品') {
      const response = await addProduct(formState.value)
      if (response.data.code === 0) {
        message.success('添加成功')
        modalVisible.value = false
        fetchProductList()
      } else {
        message.error(response.data.msg)
      }
    } else {
      const response = await updateProduct(formState.value)
      if (response.data.code === 0) {
        message.success('编辑成功')
        modalVisible.value = false
        fetchProductList()
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

async function handleDelete(record: Product) {
  try {
    const response = await deleteProduct(record.id!)
    if (response.data.code === 0) {
      message.success('删除成功')
      fetchProductList()
    } else {
      message.error(response.data.msg)
    }
  } catch (error) {
    console.error('删除商品失败:', error)
    message.error('删除商品失败')
  }
}

function onSearch() {
  pagination.value.current = 1
  fetchProductList()
}

function handleTableChange(pag: any) {
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
  fetchProductList()
}

function beforeUpload(file: File) {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    message.error('只能上传图片文件!')
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    message.error('图片必须小于2MB!')
  }
  return isImage && isLt2M
}
</script>

<style scoped>
.product-management {
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