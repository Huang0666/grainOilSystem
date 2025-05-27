<template>
  <div class="order-management">
    <div class="table-operations">
      <a-space>
        <a-input-search
          v-model:value="searchOrderNo"
          placeholder="订单号"
          style="width: 200px"
          @search="onSearch"
        />
        <a-range-picker
          v-model:value="dateRange"
          :disabled-date="disabledDate"
          @change="onSearch"
        />
      </a-space>
    </div>

    <a-table
      :columns="columns"
      :data-source="orderList"
      :loading="loading"
      :pagination="pagination"
      @change="handleTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.key === 'orderNo'">
          {{ `${record.id}-${record.user_id}-${record.product_id}-${record.merchant_id}` }}
        </template>
        <template v-if="column.key === 'total_price'">
          ¥{{ record.total_price.toFixed(2) }}
        </template>
        <template v-if="column.key === 'price'">
          ¥{{ record.price.toFixed(2) }}
        </template>
        <template v-if="column.key === 'status'">
          <a-tag color="green">已完成</a-tag>
        </template>
        <template v-if="column.key === 'action'">
          <a-space>
            <a-popconfirm
              title="确定要删除这个订单吗？"
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
      v-model:open="detailModalVisible"
      title="订单详情"
      width="800px"
      :footer="null"
    >
      <template v-if="currentOrder">
        <a-descriptions bordered :column="2">
          <a-descriptions-item label="订单号" :span="2">
            {{ `${currentOrder.id}-${currentOrder.user_id}-${currentOrder.product_id}-${currentOrder.merchant_id}` }}
          </a-descriptions-item>
          <a-descriptions-item label="下单时间" :span="2">
            {{ currentOrder.create_time }}
          </a-descriptions-item>
          <a-descriptions-item label="订单状态" :span="2">
            <a-tag color="green">已完成</a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="收货人">
            {{ currentOrder.username }}
          </a-descriptions-item>
          <a-descriptions-item label="联系电话">
            {{ currentOrder.phone }}
          </a-descriptions-item>
          <a-descriptions-item label="商品名称">
            {{ currentOrder.productName }}
          </a-descriptions-item>
          <a-descriptions-item label="商家名称">
            {{ currentOrder.merchantName }}
          </a-descriptions-item>
          <a-descriptions-item label="商品单价">
            ¥{{ currentOrder.price.toFixed(2) }}
          </a-descriptions-item>
          <a-descriptions-item label="购买数量">
            {{ currentOrder.quantity }}
          </a-descriptions-item>
          <a-descriptions-item label="订单总额" :span="2">
            <span class="amount">¥{{ currentOrder.total_price.toFixed(2) }}</span>
          </a-descriptions-item>
        </a-descriptions>
      </template>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import type { AdminOrder } from '@/api/adminorder'
import { getOrderList, deleteOrder, getUserDetail } from '@/api/adminorder'

const columns = [
  {
    title: '订单号',
    key: 'orderNo',
  },
  {
    title: '下单时间',
    dataIndex: 'create_time',
    key: 'create_time',
  },
  {
    title: '收货人',
    dataIndex: 'username',
    key: 'username',
  },
  {
    title: '联系电话',
    dataIndex: 'phone',
    key: 'phone',
  },
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
    title: '单价',
    key: 'price',
  },
  {
    title: '数量',
    dataIndex: 'quantity',
    key: 'quantity',
  },
  {
    title: '订单金额',
    key: 'total_price',
  },
  {
    title: '订单状态',
    key: 'status',
  },
  {
    title: '操作',
    key: 'action',
  },
]

const searchOrderNo = ref('')
const dateRange = ref()
const loading = ref(false)
const orderList = ref<AdminOrder[]>([])
const pagination = ref({
  current: 1,
  pageSize: 10,
  total: 0,
})

const detailModalVisible = ref(false)
const currentOrder = ref<AdminOrder | null>(null)

onMounted(() => {
  fetchOrderList()
})

async function fetchOrderList() {
  loading.value = true
  try {
    const response = await getOrderList({
      page: pagination.value.current,
      size: pagination.value.pageSize
    })

    if (response.data.code === 0) {
      const list = response.data.data.list
      // 获取用户电话信息
      const ordersWithPhone = await Promise.all(
        list.map(async (order: AdminOrder) => {
          try {
            const userResponse = await getUserDetail(order.user_id)
            if (userResponse.data.code === 0) {
              return {
                ...order,
                phone: userResponse.data.data.phone,
                status: '已完成'
              }
            }
          } catch (error) {
            console.error('获取用户信息失败:', error)
          }
          return {
            ...order,
            phone: '未知',
            status: '已完成'
          }
        })
      )

      orderList.value = ordersWithPhone
      pagination.value.total = response.data.data.total
    }
  } catch (error) {
    console.error('获取订单列表失败:', error)
    message.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

async function handleDelete(record: AdminOrder) {
  try {
    const response = await deleteOrder(record.id)
    if (response.data.code === 0) {
      message.success('删除成功')
      fetchOrderList()
    } else {
      message.error(response.data.msg || '删除失败')
    }
  } catch (error) {
    console.error('删除失败:', error)
    message.error('删除失败')
  }
}

function showDetailModal(record: AdminOrder) {
  currentOrder.value = record
  detailModalVisible.value = true
}

function disabledDate(current: Date) {
  return current && current > new Date()
}

function onSearch() {
  pagination.value.current = 1
  fetchOrderList()
}

function handleTableChange(pag: any) {
  pagination.value.current = pag.current
  pagination.value.pageSize = pag.pageSize
  fetchOrderList()
}
</script>

<style scoped>
.order-management {
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

.amount {
  color: #f5222d;
  font-size: 16px;
  font-weight: bold;
}
</style> 