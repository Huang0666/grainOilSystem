<template>
  <div class="product-merchant-management">
    <div class="table-operations">
      <a-space>
        <a-button type="primary" @click="showAddModal">
          <template #icon><plus-outlined /></template>
          商品上架
        </a-button>
        <a-select
          v-model:value="searchMerchant"
          style="width: 200px"
          placeholder="选择商家"
          allowClear
          @change="onSearch"
        >
          <a-select-option v-for="item in merchantOptions" :key="item.id" :value="item.id">
            {{ item.name }}
          </a-select-option>
        </a-select>
        <a-select
          v-model:value="searchProduct"
          style="width: 200px"
          placeholder="选择商品"
          allowClear
          @change="onSearch"
        >
          <a-select-option v-for="item in productOptions" :key="item.id" :value="item.id">
            {{ item.name }}
          </a-select-option>
        </a-select>
        
      </a-space>
    </div>

    <a-table
      :columns="columns"
      :data-source="dataList"
      :loading="loading"
      :pagination="pagination"
      @change="handleTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'currentPrice'">
          ¥{{ record.currentPrice.toFixed(2) }}
        </template>
        <template v-if="column.key === 'stock'">
          <a-tag color="green">库存充足</a-tag>
        </template>
        <template v-if="column.key === 'status'">
          <a-tag :color="record.status === 1 ? 'green' : 'red'">
            {{ record.status === 1 ? '已上架' : '已下架' }}
          </a-tag>
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a-popconfirm
              title="确定要删除这条记录吗？"
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
    >
      <a-form
        ref="formRef"
        :model="formState"
        :rules="rules"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-form-item label="选择商家" name="merchantId" v-if="modalTitle === '商品上架'">
          <a-select
            v-model:value="formState.merchantId"
            placeholder="请选择商家"
            @change="handleMerchantChange"
          >
            <a-select-option v-for="item in merchantOptions" :key="item.id" :value="item.id">
              {{ item.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="选择商品" name="productId" v-if="modalTitle === '商品上架'">
          <a-select
            v-model:value="formState.productId"
            placeholder="请选择商品"
          >
            <a-select-option v-for="item in availableProducts" :key="item.id" :value="item.id">
              {{ item.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="商品价格" name="currentPrice">
          <a-input-number
            v-model:value="formState.currentPrice"
            :min="0"
            :precision="2"
            :step="0.01"
            style="width: 100%"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { PlusOutlined } from '@ant-design/icons-vue'
import { 
  getProductMerchantList, 
  addProductMerchant, 
  updateProductMerchantStatus,
  getProductDetail,
  getMerchantDetail,
  getMerchantList,
  getProductList,
  type ProductMerchant,
  type Merchant,
  type Product
} from '@/api/adminproductmerchant'
import api from '@/api/auth'

const columns = [
  {
    title: '商品名称',
    dataIndex: 'productName',
    key: 'productName',
  },
  {
    title: '商家名称',
    dataIndex: 'merchantName',
    key: 'merchantName',
  },
  {
    title: '当前价格',
    dataIndex: 'currentPrice',
    key: 'currentPrice',
  },
  {
    title: '库存状态',
    key: 'stock',
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
  },
  {
    title: '操作',
    key: 'action',
  },
]

const searchMerchant = ref()
const searchProduct = ref()
const searchStatus = ref()
const loading = ref(false)
const dataList = ref<ProductMerchant[]>([])
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
})

const modalVisible = ref(false)
const modalTitle = ref('商品上架')
const formRef = ref()
const formState = ref<{
  merchantId: number | undefined
  productId: number | undefined
  currentPrice: number | undefined
}>({
  merchantId: undefined,
  productId: undefined,
  currentPrice: undefined,
})

const merchantOptions = ref<Merchant[]>([])
const productOptions = ref<Product[]>([])
const availableProducts = ref<Product[]>([])

const rules = {
  merchantId: [{ required: true, message: '请选择商家' }],
  productId: [{ required: true, message: '请选择商品' }],
  currentPrice: [{ required: true, message: '请输入商品价格' }],
}

onMounted(async () => {
  await Promise.all([
    fetchMerchantOptions(),
    fetchProductOptions()
  ])
  fetchDataList()
})

async function fetchDataList() {
  loading.value = true
  try {
    const response = await getProductMerchantList({
      page: pagination.value.current,
      size: pagination.value.pageSize,
      merchantId: searchMerchant.value,
      productId: searchProduct.value,
      status: searchStatus.value
    })

    if (response.data.code === 0) {
      const list = response.data.data.list
      // 获取商品和商家的详细信息
      const detailPromises = list.map(async (item: ProductMerchant) => {
        const [productRes, merchantRes] = await Promise.all([
          getProductDetail(item.productId),
          getMerchantDetail(item.merchantId)
        ])
        
        return {
          ...item,
          productName: productRes.data.code === 0 ? productRes.data.data.name : '未知商品',
          merchantName: merchantRes.data.code === 0 ? merchantRes.data.data.name : '未知商家'
        }
      })

      dataList.value = await Promise.all(detailPromises)
      pagination.value.total = response.data.data.total
    } else {
      message.error(response.data.msg)
    }
  } catch (error) {
    console.error('获取商品上架列表失败:', error)
    message.error('获取商品上架列表失败')
  } finally {
    loading.value = false
  }
}

async function fetchMerchantOptions() {
  try {
    const response = await getMerchantList({
      page: 1,
      size: 1000 // 获取足够多的商家数据用于选择
    })
    if (response.data.code === 0) {
      merchantOptions.value = response.data.data.list
    }
  } catch (error) {
    console.error('获取商家列表失败:', error)
    message.error('获取商家列表失败')
  }
}

async function fetchProductOptions() {
  try {
    const response = await getProductList({
      page: 1,
      size: 1000 // 获取足够多的商品数据用于选择
    })
    if (response.data.code === 0) {
      productOptions.value = response.data.data.list
      availableProducts.value = response.data.data.list
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
    message.error('获取商品列表失败')
  }
}

function showAddModal() {
  formState.value = {
    merchantId: undefined,
    productId: undefined,
    currentPrice: undefined,
  } as {
    merchantId: number | undefined,
    productId: number | undefined,
    currentPrice: number | undefined,
  }
  modalVisible.value = true
}

async function handleModalOk() {
  try {
    await formRef.value.validate()
    
    if (!formState.value.productId || !formState.value.merchantId || !formState.value.currentPrice) {
      message.error('请填写完整信息')
      return
    }
    
    const params = {
      productId: formState.value.productId,
      merchantId: formState.value.merchantId,
      currentPrice: formState.value.currentPrice,
    }
    
    const response = await addProductMerchant(params)

    if (response.data.code === 0) {
      message.success(modalTitle.value === '商品上架' ? '上架成功' : '修改成功')
      modalVisible.value = false
      fetchDataList()
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

function onSearch() {
  pagination.value.current = 1
  fetchDataList()
}

function handleTableChange(pag: { current: number; pageSize: number }) {
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
  fetchDataList()
}

async function handleMerchantChange(merchantId: number) {
  if (!merchantId) {
    availableProducts.value = productOptions.value
    return
  }
  // 这里可以根据商家ID筛选可用商品
  availableProducts.value = productOptions.value
}

async function handleDelete(record: ProductMerchant) {
  try {
    const response = await api.post('/admin/product-merchant/remove', {
      productId: record.productId,
      merchantId: record.merchantId
    })

    if (response.data.code === 0) {
      message.success('删除成功')
      fetchDataList()
    } else {
      message.error(response.data.msg)
    }
  } catch (error) {
    console.error('删除失败:', error)
    message.error('删除失败')
  }
}
</script>

<style scoped>
.product-merchant-management {
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