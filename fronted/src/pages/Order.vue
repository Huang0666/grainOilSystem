<template>
  <div class="order-page page-container">
    <div class="page-header">
      <h2>我的订单</h2>
      <div class="header-right">
        <a-input-search
          v-model:value="searchText"
          placeholder="搜索订单号/商品"
          style="width: 240px"
          @search="onSearch"
          allow-clear
        />
        <a-select
          v-model:value="orderStatusFilter"
          style="width: 150px"
          placeholder="订单状态"
          @change="onStatusChange"
          allow-clear
        >
          <a-select-option value="all">全部状态</a-select-option>
          <a-select-option value="pending">待付款</a-select-option>
          <a-select-option value="paid">待发货</a-select-option>
          <a-select-option value="shipping">配送中</a-select-option>
          <a-select-option value="completed">已完成</a-select-option>
          <a-select-option value="cancelled">已取消</a-select-option>
        </a-select>
      </div>
    </div>

    <div class="order-list content-area">
      <a-table
        :columns="columns"
        :data-source="filteredOrderList"
        :loading="loading"
        :pagination="{ pageSize: 10, showSizeChanger: true, pageSizeOptions: ['10', '20', '50'] }"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'productName'">
            <div class="product-cell product-info-cell">
              <img :src="record.image" :alt="record.productName" class="product-image-small" />
              <div class="product-info-text">
                <div>{{ record.productName }}</div>
                <div style="color: #888; font-size: 12px;">{{ record.merchantName }}</div>
              </div>
            </div>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag color="green">已完成</a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button size="small" @click="showOrderDetail(record)">查看详情</a-button>
              <a-button size="small" type="primary" @click="handleCommentOrder(record.id)">评价</a-button>
              <a-popconfirm
                title="确定要删除该订单吗？"
                ok-text="删除"
                cancel-text="取消"
                @confirm="handleDeleteOrder(record.id)"
              >
                <a-button size="small" danger>删除订单</a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </div>

    <a-modal
      v-model:visible="detailVisible"
      title="订单详情"
      :footer="null"
      width="800px"
      wrap-class-name="order-detail-modal"
    >
      <template v-if="currentOrder">
        <a-descriptions bordered :column="2">
          <a-descriptions-item label="订单编号">
            {{ currentOrder.id }}
            <a-button type="link" size="small" @click="copyToClipboard(currentOrder.id.toString())"><CopyOutlined /></a-button>
          </a-descriptions-item>
          <a-descriptions-item label="下单时间">
            {{ currentOrder.createTime }}
          </a-descriptions-item>
          <a-descriptions-item label="订单状态">
            <a-tag :color="getStatusColor(currentOrder.status)">
              {{ getStatusText(currentOrder.status) }}
            </a-tag>
          </a-descriptions-item>
           <a-descriptions-item label="订单总额">
            <span style="color: #f5222d; font-weight: bold;">¥{{ currentOrder.totalPrice }}</span>
          </a-descriptions-item>
        </a-descriptions>

        <a-divider orientation="left">商品信息</a-divider>
        <div class="order-items-wrapper">
          <div v-for="item in currentOrder.items" :key="item.id" class="order-item">
            <img :src="item.image || 'https://via.placeholder.com/70x70'" :alt="item.name" class="item-image" />
            <div class="item-info">
              <div class="item-name">{{ item.name }}</div>
              <div class="item-specs">规格：{{ item.specs || '默认规格' }}</div>
            </div>
            <div class="item-price">¥{{ item.price.toFixed(2) }}</div>
            <div class="item-quantity">× {{ item.quantity }}</div>
            <div class="item-total">¥{{ (item.price * item.quantity).toFixed(2) }}</div>
          </div>
        </div>

        
        
        <a-divider v-if="currentOrder.status === 'pending' || currentOrder.status === 'paid' || currentOrder.status === 'shipping'" />
        <div class="order-actions-footer" v-if="currentOrder.status === 'pending' || currentOrder.status === 'paid' || currentOrder.status === 'shipping'" >
            <a-button v-if="currentOrder.status === 'pending'" type="primary" size="large" @click="handlePay(currentOrder)">立即付款</a-button>
            <a-button v-if="currentOrder.status === 'shipping'" type="primary" size="large" @click="handleConfirm(currentOrder)">确认收货</a-button>
             <a-popconfirm
                v-if="currentOrder.status === 'pending' || currentOrder.status === 'paid'"
                title="确定要取消该订单吗?"
                ok-text="确定"
                cancel-text="点错了"
                @confirm="handleCancelOrder(currentOrder)"
              >
                <a-button size="large">取消订单</a-button>
              </a-popconfirm>
        </div>
      </template>
    </a-modal>

    <a-modal
      v-model:visible="commentModalVisible"
      title="订单评价"
      :footer="null"
      width="500px"
      :maskClosable="false"
      @cancel="closeCommentModal"
      destroyOnClose
    >
      <div v-if="commentOrder" style="margin-bottom: 16px;">
        <div><b>商品：</b>{{ commentOrder.productName }}</div>
        <div><b>商家：</b>{{ commentOrder.merchantName }}</div>
      </div>
      <a-form :model="commentForm">
        <a-form-item>
          <a-textarea
            v-model:value="commentForm.content"
            :rows="4"
            placeholder="写下你的评价..."
            :maxLength="1000"
            show-count
          />
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button @click="closeCommentModal">取消</a-button>
            <a-button type="primary" @click="submitComment" :loading="commentSubmitting" :disabled="!commentForm.content.trim()">提交评价</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { getOrderList, removeOrder } from '../api/order'
import { getProductDetail, getMerchantDetail, addComment } from '../api/product'
import { CopyOutlined } from '@ant-design/icons-vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const loading = ref(false)
const detailVisible = ref(false)
const currentOrder = ref(null)
const orderList = ref([])

// 用户认证与获取userId
const getUserId = () => {
  const userInfo = localStorage.getItem('grain_oil_user')
  if (!userInfo) {
    message.error('请先登录')
    router.push('/login')
    return null
  }
  try {
    return JSON.parse(userInfo).id
  } catch {
    message.error('用户信息异常，请重新登录')
    router.push('/login')
    return null
  }
}

// 获取订单数据并适配商品名、商家名
const fetchOrders = async () => {
  const userId = getUserId()
  if (!userId) return
  loading.value = true
  try {
    const { data } = await getOrderList(userId)
    if (data.code === 0) {
      // 并发获取商品名、商家名
      const orders = await Promise.all(
        (data.data || []).map(async (order) => {
          let productName = '', merchantName = ''
          try {
            const [productRes, merchantRes] = await Promise.all([
              getProductDetail({ productId: order.productId }),
              getMerchantDetail({ merchantId: order.merchantId })
            ])
            productName = productRes.data.data?.name || ''
            merchantName = merchantRes.data.data?.name || ''
            return {
              ...order,
              productName,
              merchantName,
              image: productRes.data.data?.imageUrl || 'https://via.placeholder.com/40x40', // 使用真实商品图片
              status: 'completed' // 固定为已完成
            }
          } catch (e) {
            // 忽略异常
            return {
              ...order,
              productName: '',
              merchantName: '',
              image: 'https://via.placeholder.com/40x40',
              status: 'completed'
            }
          }
        })
      )
      orderList.value = orders
    } else {
      message.error(data.msg || '获取订单失败')
    }
  } catch (e) {
    message.error('获取订单失败')
  } finally {
    loading.value = false
  }
}

// 表格列定义
const columns = [
  {
    title: '订单编号',
    dataIndex: 'id',
    key: 'id',
    width: 120,
  },
  {
    title: '商品信息',
    dataIndex: 'productName',
    key: 'productName',
    width: 400,
  },
  {
    title: '单价',
    dataIndex: 'price',
    key: 'price',
    align: 'right',
    width: 100,
    customRender: ({ text }) => `¥${Number(text).toFixed(2)}`
  },
  {
    title: '数量',
    dataIndex: 'quantity',
    key: 'quantity',
    align: 'center',
    width: 80
  },
  {
    title: '总金额',
    dataIndex: 'totalPrice',
    key: 'totalPrice',
    align: 'right',
    width: 120,
    customRender: ({ text }) => `¥${Number(text).toFixed(2)}`
  },
  {
    title: '下单时间',
    dataIndex: 'createTime',
    key: 'createTime',
    width: 180,
  },
  {
    title: '订单状态',
    key: 'status',
    width: 100,
    align: 'center',
    customRender: () => '已完成'
  },
  {
    title: '操作',
    key: 'action',
    width: 240,
    align: 'center',
    customRender: ({ record }) => {
      return (
        `<a class='order-delete-btn' data-id='${record.id}'>删除订单</a> | <a class='order-comment-btn' data-id='${record.id}'>评价</a>`
      )
    }
  }
]

// 事件绑定（删除、评论）
const handleTableClick = (e) => {
  const target = e.target
  if (target.classList.contains('order-delete-btn')) {
    const id = Number(target.getAttribute('data-id'))
    handleDeleteOrder(id)
  } else if (target.classList.contains('order-comment-btn')) {
    const id = Number(target.getAttribute('data-id'))
    handleCommentOrder(id)
  }
}

// 删除订单
const handleDeleteOrder = (id) => {
  const userId = getUserId()
  if (!userId) return
  Modal.confirm({
    title: '确定要删除该订单吗？',
    okText: '删除',
    cancelText: '取消',
    onOk: async () => {
      try {
        const { data } = await removeOrder(id, userId)
        if (data.code === 0 && data.data.success) {
          message.success('删除成功')
          fetchOrders()
        } else {
          message.error(data.data.msg || '删除失败')
        }
      } catch (e) {
        message.error('删除失败')
      }
    }
  })
}

// 评价弹窗相关ref
const commentModalVisible = ref(false)
const commentForm = ref({ content: '' })
const commentSubmitting = ref(false)
const commentOrder = ref(null)

// 打开评价弹窗
const handleCommentOrder = (id) => {
  const order = orderList.value.find(o => o.id === id)
  if (!order) {
    message.error('订单信息异常')
    return
  }
  commentOrder.value = order
  commentForm.value.content = ''
  commentModalVisible.value = true
}

// 关闭评价弹窗
const closeCommentModal = () => {
  commentModalVisible.value = false
  commentOrder.value = null
  commentForm.value.content = ''
}

// 提交评价
const submitComment = async () => {
  if (!commentForm.value.content.trim()) {
    message.warning('请输入评价内容')
    return
  }
  const userId = getUserId()
  if (!userId) return
  const order = commentOrder.value
  if (!order) {
    message.error('订单信息异常')
    return
  }
  commentSubmitting.value = true
  try {
    const { data } = await addComment({
      userId,
      productId: order.productId,
      merchantId: order.merchantId,
      content: commentForm.value.content.trim()
    })
    if (data.code === 0 && data.data.success) {
      message.success('评价成功')
      closeCommentModal()
    } else {
      message.error(data.data.msg || '评价失败')
    }
  } catch (e) {
    message.error('评价失败')
  } finally {
    commentSubmitting.value = false
  }
}

// 订单详情弹窗
const showOrderDetail = (order) => {
  currentOrder.value = order
  detailVisible.value = true
}

// 复制订单编号
const copyToClipboard = async (text) => {
  try {
    await navigator.clipboard.writeText(text)
    message.success('订单号已复制到剪贴板')
  } catch (err) {
    message.error('复制失败')
  }
}

const searchText = ref('')
const orderStatusFilter = ref('all')

const filteredOrderList = computed(() => {
  let list = orderList.value;
  if (orderStatusFilter.value !== 'all') {
    list = list.filter(order => order.status === orderStatusFilter.value);
  }
  if (searchText.value) {
    const lowerSearchText = searchText.value.toLowerCase();
    list = list.filter(order => 
      order.id.toString().includes(lowerSearchText) || 
      (order.productName && order.productName.toLowerCase().includes(lowerSearchText))
    );
  }
  return list;
});

const getStatusColor = (status) => {
  const colorMap = {
    pending: 'orange',
    paid: 'geekblue',
    shipping: 'cyan',
    completed: 'green',
    cancelled: 'red'
  }
  return colorMap[status] || 'default'
}

const getStatusText = (status) => {
  const textMap = {
    pending: '待付款',
    paid: '待发货',
    shipping: '配送中',
    completed: '已完成',
    cancelled: '已取消'
  }
  return textMap[status] || status
}

const onSearch = (value) => {}
const onStatusChange = (value) => {}

const handlePay = (order) => {
  message.info(`模拟订单 ${order.id} 支付流程`);
}

const handleConfirm = (order) => {
  order.status = 'completed';
  message.success(`订单 ${order.id} 已确认收货`);
}

const handleCancelOrder = (order) => {
  order.status = 'cancelled';
  message.success(`订单 ${order.id} 已取消`);
  if (detailVisible.value && currentOrder.value?.id === order.id) {
    detailVisible.value = false; 
  }
}

const handleComment = (order) => {
  message.info('跳转到评价页面...')
}

onMounted(() => {
  fetchOrders()
  setTimeout(() => {
    const table = document.querySelector('.order-list .ant-table-tbody')
    if (table) {
      table.addEventListener('click', handleTableClick)
  }
  }, 500)
})
</script>

<style scoped>
.page-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  background-color: #fff; /* 白色背景 */
  padding: 24px;
  border-radius: 4px;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
}

.content-area {
  flex: 1;
  overflow-y: auto; /* 内容超出时显示滚动条 */
}

.order-page {
  /* padding: 24px; */ /* 统一在 page-container 中设置 */
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.header-right {
  display: flex;
  gap: 16px;
  align-items: center;
}

.order-list {
  /* background: #fff; */
  /* padding: 24px; */
  /* border-radius: 8px; */
  /* box-shadow: 0 1px 2px rgba(0, 0, 0, 0.06); */
}

.product-cell {
  display: flex;
  align-items: center;
}

.product-image-small {
  width: 40px;
  height: 40px;
  object-fit: cover;
  border-radius: 4px;
  margin-right: 12px;
  border: 1px solid #f0f0f0;
}

.action-link-pay {
  color: #ff4d4f;
}
.action-link-confirm {
  color: #52c41a;
}

/* 订单详情弹窗样式 */
.order-items-wrapper {
  max-height: 300px;
  overflow-y: auto;
  margin: 16px 0;
  border: 1px solid #f0f0f0;
  border-radius: 4px;
}

.order-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border-bottom: 1px solid #f0f0f0;
}

.order-item:last-child {
  border-bottom: none;
}

.item-image {
  width: 70px;
  height: 70px;
  object-fit: cover;
  border-radius: 4px;
  border: 1px solid #e8e8e8;
}

.item-info {
  flex: 1;
  margin-left: 16px;
  margin-right: 16px;
}

.item-name {
  font-size: 14px;
  margin-bottom: 4px;
  font-weight: 500;
}

.item-specs {
  color: #888;
  font-size: 12px;
}

.item-price, .item-quantity, .item-total {
  font-size: 14px;
  text-align: right;
  min-width: 80px; /* 保持对齐 */
}

.item-total {
  font-weight: bold;
  color: #f5222d;
}

.order-total {
  text-align: right;
  padding: 16px;
  font-size: 16px;
}

.total-price {
  color: #f5222d;
  font-size: 20px;
  font-weight: bold;
  margin-left: 8px;
}

.delivery-info {
  /* background: #f9f9f9; */
  /* padding: 16px; */
  /* border-radius: 4px; */
}

.delivery-info h3 {
  margin-bottom: 16px;
}

.delivery-info p {
  margin-bottom: 8px;
  color: #666;
}

.order-actions-footer {
    margin-top: 24px;
    display: flex;
    justify-content: flex-end;
    gap: 12px;
}

:deep(.order-detail-modal .ant-modal-body) {
  padding-top: 16px;
}

:deep(.ant-descriptions-item-label) {
  width: 100px; /* 统一标签宽度 */
}

.product-info-cell {
  display: flex;
  align-items: center;
}
.product-info-text {
  display: flex;
  flex-direction: column;
  margin-left: 12px;
}
</style> 