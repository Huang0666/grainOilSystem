<template>
  <div class="forum-page">
    <div class="page-header">
      <h2>粮油商品</h2>
      <div class="header-right">
        <a-auto-complete
          v-model:value="productSearch"
          :options="productOptions"
          placeholder="搜索商品类型"
          style="width: 180px; margin-right: 8px;"
          @select="onSearch"
          @search="onSearch"
        />
        <a-auto-complete
          v-model:value="merchantSearch"
          :options="merchantOptions"
          placeholder="搜索商家"
          style="width: 180px; margin-right: 8px;"
          @select="onSearch"
          @search="onSearch"
        />
        <a-select
          v-model:value="sortBy"
          style="width: 140px"
          placeholder="排序方式"
        >
          <a-select-option value="price-asc">价格从低到高</a-select-option>
          <a-select-option value="price-desc">价格从高到低</a-select-option>
          <a-select-option value="likes">点赞最多</a-select-option>
          <a-select-option value="collects">收藏最多</a-select-option>
        </a-select>
      </div>
    </div>

    <div class="product-list-panel">
      <a-spin :spinning="loading">
        <div v-if="productList.length > 0" class="product-grid">
        <a-row :gutter="[16, 16]">
            <a-col :xs="24" :sm="12" :md="8" :lg="6" v-for="item in filteredAndSortedList" :key="item.productId">
            <a-card hoverable class="product-card">
              <template #cover>
                  <img :src="item.imageUrl || 'https://via.placeholder.com/300x200'" :alt="item.productName" />
              </template>
              <template #actions>
                <div class="action-item">
                    <a-button 
                      type="text" 
                      :class="{ active: item.isLiked }" 
                      @click="toggleLike(item)"
                    >
                    <component :is="item.isLiked ? HeartFilled : HeartOutlined" />
                      <span class="count">{{ item.likeCount || 0 }}</span>
                  </a-button>
                </div>
                <div class="action-item">
                    <a-button 
                      type="text" 
                      :class="{ active: item.isCollected }" 
                      @click="toggleCollect(item)"
                    >
                    <component :is="item.isCollected ? StarFilled : StarOutlined" />
                      <span class="count">{{ item.collectCount || 0 }}</span>
                  </a-button>
                </div>
                <div class="action-item">
                  <a-button type="text" @click="showComments(item)">
                    <message-outlined />
                      <span class="count">{{ item.commentCount || 0 }}</span>
                  </a-button>
                </div>
                <div class="action-item">
                  <a-button type="text" @click="showBuyModal(item)">
                    <shopping-cart-outlined />
                  </a-button>
                </div>
              </template>
                <a-card-meta :title="item.productName">
                <template #description>
                  <div class="shop-row">
                    <span class="shop-label">{{ item.merchantName }}</span>
                    <span class="shop-type">{{ item.type }}</span>
                    <span class="shop-sales">月销 {{ item.sales || 0 }} 笔</span>
                  </div>
                  <div class="price">¥{{ item.price }}</div>
                </template>
              </a-card-meta>
            </a-card>
          </a-col>
        </a-row>
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
        <div v-else class="empty-state">
          <a-empty description="暂无商品数据" />
      </div>
      </a-spin>
    </div> 

   <a-modal
      v-model:visible="buyModalVisible"
      title="确认订单"
      :confirmLoading="confirmLoading"
      @ok="handleBuy"
      @cancel="closeBuyModal"
      :maskClosable="false"
    >
      <a-form :model="buyForm" :label-col="{ span: 4 }" :wrapper-col="{ span: 20 }">
        <a-form-item label="商品">
          <span>{{ selectedProduct?.productName }}</span>
        </a-form-item>
        <a-form-item label="单价">
          <span>¥{{ selectedProduct?.price }}</span>
        </a-form-item>
        <a-form-item label="数量">
          <a-input-number 
            v-model:value="buyForm.quantity" 
            :min="1" 
            :max="999"
            style="width: 120px"
          />
        </a-form-item>
        <a-form-item label="总价">
          <span class="total-price">¥{{ totalPrice }}</span>
        </a-form-item>
        
        
      </a-form>
    </a-modal>

    <a-modal
      v-model:visible="commentModalVisible"
      title="商品评论"
      width="1000px"
      :footer="null"
      :maskClosable="false"
      :keyboard="false"
      @cancel="closeCommentModal"
      destroyOnClose
    >
      <div class="comment-container">
      <div class="comment-list">
        <a-list
          :data-source="comments"
          :header="`${comments.length} 条评论`"
          item-layout="horizontal"
            :loading="commentLoading"
        >
          <template #renderItem="{ item }">
            <a-list-item>
              <a-comment
                :author="item.username"
                :content="item.content"
                  :datetime="item.datetime"
              >
                <template #avatar>
                  <a-avatar>
                      {{ item.username?.[0]?.toUpperCase() || 'U' }}
                  </a-avatar>
                </template>
              </a-comment>
            </a-list-item>
          </template>
            <template #empty>
              <div class="empty-comment">
                暂无评论
              </div>
          </template>
        </a-list>
      </div>
        
        <div class="comment-form">
      <a-form :model="commentForm">
        <a-form-item>
              <a-textarea 
                v-model:value="commentForm.content" 
                :rows="3" 
                placeholder="写下你的评论..." 
                :maxLength="1000"
                show-count
                class="comment-textarea"
              />
        </a-form-item>
            <a-form-item class="form-buttons">
              <a-space>
                <a-button @click="closeCommentModal">取消</a-button>
                <a-button 
                  type="primary" 
                  @click="submitComment" 
                  :loading="submitting"
                  :disabled="!commentForm.content?.trim()"
                >
                  发表评论
                </a-button>
              </a-space>
        </a-form-item>
      </a-form>
        </div>
      </div>
    </a-modal> 
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import {
  HeartOutlined,
  HeartFilled,
  StarOutlined,
  StarFilled,
  ShoppingCartOutlined,
  MessageOutlined
} from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import {
  getProductList,
  addLike,
  removeLike,
  isLiked,
  getLikeCount,
  addFavorite,
  removeFavorite,
  isFavorite,
  getComments,
  addComment,
  createOrder,
  getProductDetail,
  getMerchantDetail,
  getFavoriteCount,
  getProductTypes,
  getMerchantList
} from '../api/product'

const productSearch = ref('')
const merchantSearch = ref('')
const sortBy = ref('price-asc')
const buyModalVisible = ref(false)
const commentModalVisible = ref(false)
const selectedProduct = ref(null)
const confirmLoading = ref(false)
const buyForm = ref({
  quantity: 1,
  address: '',
  phone: ''
})
const commentForm = ref({
  content: ''
})
const comments = ref([])
const productList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const commentLoading = ref(false)
const submitting = ref(false)
const error = ref(null)
const retryCount = ref(0)
const MAX_RETRIES = 3

// 原始商品数据
const originalProductList = [
  {
    id: 1,
    name: '优质大米',
    price: 99.9,
    merchantName: '粮油专营店',
    sales: 1000,
    image: 'https://via.placeholder.com/300x200',
    isLiked: false,
    isCollected: false,
    likeCount: 12,
    collectCount: 5,
    commentCount: 3
  },
  {
    id: 2,
    name: '花生油',
    price: 159.9,
    merchantName: '健康食用油',
    sales: 850,
    image: 'https://via.placeholder.com/300x200',
    isLiked: true,
    isCollected: false,
    likeCount: 8,
    collectCount: 2,
    commentCount: 1
  },
  {
    id: 3,
    name: '小麦面粉',
    price: 29.9,
    merchantName: '面粉专卖',
    sales: 2000,
    image: 'https://via.placeholder.com/300x200',
    isLiked: false,
    isCollected: true,
    likeCount: 3,
    collectCount: 7,
    commentCount: 0
  },
  {
    id: 4,
    name: '玉米油',
    price: 119.9,
    merchantName: '健康食用油',
    sales: 500,
    image: 'https://via.placeholder.com/300x200',
    isLiked: false,
    isCollected: false,
    likeCount: 0,
    collectCount: 0,
    commentCount: 0
  }
]

const productOptions = ref([])
const merchantOptions = ref([])

// 添加获取商品类型和商家列表的函数
const fetchProductTypes = async () => {
  try {
    const { data } = await getProductTypes()
    if (data.code === 0) {
      productOptions.value = data.data.map(type => ({ 
        value: type,
        label: type 
      }))
    }
  } catch (error) {
    console.error('获取商品类型列表失败:', error)
    message.error('获取商品类型列表失败')
  }
}

const fetchMerchantList = async () => {
  try {
    const { data } = await getMerchantList()
    if (data.code === 0) {
      merchantOptions.value = data.data.list.map(merchant => ({ 
        value: merchant.name 
      }))
    }
  } catch (error) {
    console.error('获取商家列表失败:', error)
    message.error('获取商家列表失败')
  }
}

// 获取商品列表
const fetchProducts = async () => {
  try {
    loading.value = true
    const { data } = await getProductList({
      page: currentPage.value,
      size: pageSize.value,
      type: productSearch.value || null,
      merchantName: merchantSearch.value || null
    })

    if (data.code === 0) {
      const list = data.data.list || []
      total.value = data.data.total || 0
      
      const userId = JSON.parse(localStorage.getItem('grain_oil_user'))?.id
      if (!userId) {
        throw new Error('请先登录')
      }

      const processedList = await Promise.all(list.map(async (item) => {
        try {
          // 获取商品和商家详情
          const [productRes, merchantRes] = await Promise.all([
            getProductDetail({ productId: item.productId }),
            getMerchantDetail({ merchantId: item.merchantId })
          ])

          // 获取点赞、收藏状态、评论数和评论列表
          const [likeRes, favoriteRes, commentsRes, likeCountRes, favoriteCountRes] = await Promise.all([
            isLiked({ 
              userId, 
              productId: item.productId, 
              merchantId: item.merchantId 
            }),
            isFavorite({ 
              userId, 
              productId: item.productId, 
              merchantId: item.merchantId 
            }),
            getComments({ 
              productId: item.productId, 
              merchantId: item.merchantId 
            }),
            getLikeCount({ 
              productId: item.productId, 
              merchantId: item.merchantId 
            }),
            getFavoriteCount({ 
              productId: item.productId, 
              merchantId: item.merchantId 
            })
          ])

          return {
            ...item,
            productName: productRes.data.data.name,
            type: productRes.data.data.type,
            imageUrl: productRes.data.data.imageUrl,
            merchantName: merchantRes.data.data.name,
            price: item.currentPrice || 0,
            isLiked: likeRes.data.data.liked,
            isCollected: favoriteRes.data.data.favorite,
            likeCount: likeCountRes.data.data.count,
            collectCount: favoriteCountRes,
            commentCount: commentsRes.data.data.length || 0,
            comments: commentsRes.data.data || []
          }
        } catch (err) {
          console.error('处理商品数据失败:', err)
          return {
            ...item,
            commentCount: 0,
            comments: []
          }
        }
      }))

      productList.value = processedList
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
    message.error(error.message || '获取商品列表失败')
  } finally {
    loading.value = false
  }
}

// 点赞功能
const toggleLike = async (item) => {
  try {
    const userId = JSON.parse(localStorage.getItem('grain_oil_user')).id
    const params = {
      userId,
      productId: item.productId,
      merchantId: item.merchantId
    }
    
    if (item.isLiked) {
      const { data } = await removeLike(params)
      if (data.code === 0 && data.data.success) {
        item.isLiked = false
        // 更新点赞数
        const likeCountRes = await getLikeCount({ 
          productId: item.productId, 
          merchantId: item.merchantId 
        })
        item.likeCount = likeCountRes.data.data.count
        message.success(data.data.msg)
      }
    } else {
      const { data } = await addLike(params)
      if (data.code === 0 && data.data.success) {
        item.isLiked = true
        // 更新点赞数
        const likeCountRes = await getLikeCount({ 
          productId: item.productId, 
          merchantId: item.merchantId 
        })
        item.likeCount = likeCountRes.data.data.count
        message.success(data.data.msg)
      }
    }
  } catch (error) {
    console.error('点赞操作失败:', error)
    message.error('操作失败')
  }
}

// 收藏功能
const toggleCollect = async (item) => {
  try {
    const userId = JSON.parse(localStorage.getItem('grain_oil_user'))?.id
    if (!userId) {
      throw new Error('请先登录')
    }

    const params = {
      userId,
      productId: item.productId,
      merchantId: item.merchantId
    }
    
    if (item.isCollected) {
      const { data } = await removeFavorite(params)
      if (data.code === 0 && data.data.success) {
        item.isCollected = false
        // 获取最新的收藏数
        const favoriteCount = await getFavoriteCount({
          productId: item.productId,
          merchantId: item.merchantId
        })
        item.collectCount = favoriteCount
        message.success(data.data.msg)
      }
    } else {
      const { data } = await addFavorite(params)
      if (data.code === 0 && data.data.success) {
        item.isCollected = true
        // 获取最新的收藏数
        const favoriteCount = await getFavoriteCount({
          productId: item.productId,
          merchantId: item.merchantId
        })
        item.collectCount = favoriteCount
        message.success(data.data.msg)
      }
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
    message.error(error.message || '操作失败')
  }
}

// 显示评论弹窗
const showComments = async (item) => {
  try {
    selectedProduct.value = item
    commentModalVisible.value = true
    await fetchComments(item)
  } catch (error) {
    console.error('获取评论失败:', error)
    message.error('获取评论失败')
    closeCommentModal()
  }
}

// 关闭评论弹窗
const closeCommentModal = () => {
  commentModalVisible.value = false
  selectedProduct.value = null
  commentForm.value.content = ''
  comments.value = []
}

// 获取评论列表
const fetchComments = async (item) => {
  if (!item) return
  
  try {
    commentLoading.value = true
    const { data } = await getComments({
      productId: item.productId,
      merchantId: item.merchantId
    })
    
    if (data.code === 0) {
      comments.value = data.data.map(comment => ({
        ...comment,
        datetime: new Date(comment.createTime).toLocaleString()
      }))
    }
  } catch (error) {
    console.error('获取评论失败:', error)
    throw error
  } finally {
    commentLoading.value = false
  }
}

// 提交评论
const submitComment = async () => {
  if (!commentForm.value.content?.trim()) {
    message.warning('请输入评论内容')
    return
  }
  
  if (!selectedProduct.value) {
    message.error('商品信息异常')
    return
  }

  try {
    submitting.value = true
    const userId = JSON.parse(localStorage.getItem('grain_oil_user'))?.id
    if (!userId) {
      throw new Error('请先登录')
    }

    const { data } = await addComment({
      userId,
      productId: selectedProduct.value.productId,
      merchantId: selectedProduct.value.merchantId,
      content: commentForm.value.content.trim()
    })
    
    if (data.code === 0 && data.data.success) {
      message.success('评论成功')
      commentForm.value.content = ''
      
      // 刷新评论列表
      await fetchComments(selectedProduct.value)
      
      // 更新商品列表中的评论数量
      const product = productList.value.find(
        item => item.productId === selectedProduct.value.productId
      )
      if (product) {
        product.commentCount = comments.value.length
      }
    }
  } catch (error) {
    console.error('评论失败:', error)
    message.error(error.message || '评论失败')
  } finally {
    submitting.value = false
  }
}

// 下单功能
const handleBuy = async () => {
  if (!buyForm.value.quantity || buyForm.value.quantity <= 0) {
    message.warning('请输入有效的购买数量')
    return
  }

  if (!selectedProduct.value) {
    message.error('商品信息异常')
    return
  }

  try {
    confirmLoading.value = true
    const userId = JSON.parse(localStorage.getItem('grain_oil_user'))?.id
    if (!userId) {
      throw new Error('请先登录')
    }

    const { data } = await createOrder({
      userId,
      productId: selectedProduct.value.productId,
      merchantId: selectedProduct.value.merchantId,
      quantity: buyForm.value.quantity
    })
    
    if (data.code === 0) {
      if (data.data.success) {
        // 更新商品销量
        const product = productList.value.find(
          item => item.productId === selectedProduct.value.productId
        )
        if (product) {
          product.sales = (product.sales || 0) + buyForm.value.quantity
        }
        message.success(data.data.msg)
        closeBuyModal()
      } else {
        message.error(data.data.msg)
      }
    }
  } catch (error) {
    console.error('下单失败:', error)
    message.error(error.message || '下单失败，请稍后重试')
  } finally {
    confirmLoading.value = false
  }
}

// 显示购买弹窗
const showBuyModal = (item) => {
  if (!item) {
    message.error('商品信息异常')
    return
  }
  selectedProduct.value = item
  buyForm.value = {
    quantity: 1
  }
  buyModalVisible.value = true
}

// 关闭购买弹窗
const closeBuyModal = () => {
  buyModalVisible.value = false
  buyForm.value = {
    quantity: 1
  }
  selectedProduct.value = null
}

// 计算总价
const totalPrice = computed(() => {
  if (!selectedProduct.value || !buyForm.value.quantity) return 0
  return (selectedProduct.value.price * buyForm.value.quantity).toFixed(2)
})

// 商品筛选和排序
const filteredAndSortedList = computed(() => {
  let list = productList.value.filter(item => {
    const matchProduct = productSearch.value ? item.type === productSearch.value : true
    const matchMerchant = merchantSearch.value ? item.merchantName.includes(merchantSearch.value) : true
    return matchProduct && matchMerchant
  })
  
  switch (sortBy.value) {
    case 'price-asc':
      list = list.slice().sort((a, b) => a.price - b.price)
      break
    case 'price-desc':
      list = list.slice().sort((a, b) => b.price - a.price)
      break
    case 'likes':
      list = list.slice().sort((a, b) => b.likeCount - a.likeCount)
      break
    case 'collects':
      list = list.slice().sort((a, b) => b.collectCount - a.collectCount)
      break
  }
  return list
})

// 页面加载时获取商品列表
onMounted(() => {
  fetchProductTypes()
  fetchMerchantList()
  fetchProducts()
})

const handlePageChange = (page, size) => {
  currentPage.value = page
  if (size) pageSize.value = size
  fetchProducts()
}

const handleSizeChange = (current, size) => {
  currentPage.value = 1
  pageSize.value = size
  fetchProducts()
}

// 修改搜索函数
const onSearch = async () => {
  currentPage.value = 1 // 重置页码
  pageSize.value = 20  // 重置每页数量
  await fetchProducts()
}
</script>

<style scoped>
.forum-page {
  /* display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f5f6fa; */
}


.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
  background: #fff;
  border-radius: 8px 8px 0 0;
  box-shadow: 0 1px 4px rgba(0,21,41,.04);
}

.header-right {
  display: flex;
  gap: 16px;
  align-items: center;
}

.product-list-panel {
  width: 100%;
  max-width: 100%;
  height: 750px;
  margin: 0 auto;
  background: #fff;
  border-radius: 0 0 8px 8px;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
  border: 1px solid #e6e6e6;
  /* overflow-y: auto; */
  overflow-x: hidden;
  /* 滚动条美化 */
  scrollbar-width: thin;           /* Firefox */
  scrollbar-color: #d4d7de #fff;   /* Firefox */
}
.product-list-panel::-webkit-scrollbar {
  width: 8px;
  background: transparent;
}
.product-list-panel::-webkit-scrollbar-thumb {
  background: #d4d7de;
  border-radius: 6px;
}
.product-list-panel::-webkit-scrollbar-thumb:hover {
  background: #bfc2c9;
}
.product-list-panel::-webkit-scrollbar-track {
  background: transparent;
}

.product-grid {
  min-height: 100%;
}

:deep(.ant-row) {
  flex-wrap: wrap !important;
  margin: 0 -8px;
}
:deep(.ant-col) {
  flex: 0 0 20%;
  max-width: 20%;
  min-width: 20%;
  box-sizing: border-box;
  padding: 0 8px;
}

.product-card {
  margin: 8px 0;
  transition: all 0.3s;
}
.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}
.product-card :deep(.ant-card-cover img) {
  height: 200px;
  object-fit: cover;
  transition: transform 0.3s;
}
.product-card:hover :deep(.ant-card-cover img) {
  transform: scale(1.05);
}
.product-card :deep(.ant-card-body) {
  padding: 16px;
}

.product-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.price {
  color: #f5222d;
  font-size: 20px;
  font-weight: bold;
}

.merchant {
  color: #666;
}

.sales {
  color: #999;
  font-size: 12px;
}

.total-price {
  color: #ff4d4f;
  font-size: 20px;
  font-weight: bold;
}

.shop-row {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}
.shop-label {
  background: #e6f4ff;
  color: #1677ff;
  font-weight: bold;
  border-radius: 4px;
  padding: 2px 10px;
  margin-right: 10px;
  font-size: 14px;
  border: 1px solid #91caff;
}
.shop-sales {
  color: #999;
  font-size: 13px;
}
.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.action-item .count {
  font-size: 12px;
  color: #999;
  margin-left: 4px;
}
.action-item .active {
  color: #ff4d4f;
}
.action-item .active .anticon-star {
  color: #faad14;
}
.action-item .active .anticon-heart {
  color: #ff4d4f;
}

/* 响应式布局优化 */
:deep(.ant-row) {
  margin: 0 -12px;
}

:deep(.ant-col) {
  padding: 0 12px;
}

/* 商品卡片网格布局优化 */
@media (min-width: 1600px) {
  :deep(.ant-col-lg-6) {
    flex: 0 0 20%;
    max-width: 20%;
  }
}

@media (max-width: 1599px) {
  :deep(.ant-col-lg-6) {
    flex: 0 0 25%;
    max-width: 25%;
  }
}

@media (max-width: 1199px) {
  :deep(.ant-col-md-8) {
    flex: 0 0 33.33%;
    max-width: 33.33%;
  }
}

@media (max-width: 991px) {
  :deep(.ant-col-sm-12) {
    flex: 0 0 50%;
    max-width: 50%;
  }
}

.empty-state {
  padding: 40px 0;
  text-align: center;
}

.error-message {
  color: #ff4d4f;
  text-align: center;
  margin: 20px 0;
}

.comment-container {
  display: flex;
  flex-direction: column;
  height: 600px;
  max-height: 80vh;
  width: 100%;
}

.comment-list {
  flex: 1;
  overflow-y: auto;
  padding: 0 16px;
  margin-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
}

.comment-form {
  padding: 24px 32px;
  background: #fff;
  border-radius: 0 0 8px 8px;
  border-top: 1px solid #f0f0f0;
  width: 100%;
}

.empty-comment {
  text-align: center;
  padding: 32px 0;
  color: #999;
}

.form-buttons {
  margin-bottom: 0;
  text-align: right;
}

:deep(.ant-modal-body) {
  padding: 0;
  max-height: 80vh;
}

:deep(.ant-modal-header) {
  margin-bottom: 0;
}

:deep(.ant-list-header) {
  padding: 16px;
  background: #fafafa;
  border-radius: 8px 8px 0 0;
}

:deep(.ant-comment) {
  background: #fafafa;
  padding: 12px;
  border-radius: 4px;
  margin: 8px 0;
}

:deep(.ant-comment-content-author) {
  margin-bottom: 8px;
}

:deep(.ant-form-item:last-child) {
  margin-bottom: 0;
}

:deep(.comment-textarea) {
  width: 900px !important;
  resize: none;
  border-radius: 4px;
  font-size: 14px;
  padding: 12px;
}

:deep(.ant-input-textarea-show-count::after) {
  color: #999;
  font-size: 12px;
  margin-top: 4px;
}

:deep(.ant-modal-content) {
  padding: 0;
}

:deep(.ant-modal-header) {
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
}

:deep(.ant-form-item-control-input-content) {
  width: 100%;
}

:deep(.ant-form-item) {
  margin-bottom: 16px;
  width: 100%;
}

:deep(.ant-form-item-control-input) {
  width: 100%;
}

:deep(.ant-input) {
  width: 100% !important;
}

.pagination-container {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 16px;
  background: #fff;
  border-top: 1px solid #f0f0f0;
  position: sticky;
  bottom: 0;
  z-index: 1;
}

/* 添加商品类型样式 */
.shop-type {
  background: #f6ffed;
  color: #52c41a;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
  margin-right: 10px;
  border: 1px solid #b7eb8f;
}

.notice-page .pagination-container {
  position: sticky;
  bottom: 0;
  left: 0;
  width: 100%;
  background: #fff;
  z-index: 10;
  border-top: 1px solid #f0f0f0;
  box-shadow: 0 -2px 8px rgba(0,0,0,0.03);
}
</style> 