<template>
  <div class="dashboard">
    <a-row :gutter="16">
      <a-col :span="6">
        <a-card :bodyStyle="{ padding: '20px' }">
          <template #title>
            <span class="card-title">
              <user-outlined /> 总用户数
            </span>
          </template>
          <div class="card-value">{{ statistics.totalUsers || 0 }}</div>
          <div class="card-footer">
            <span class="highlight-warning">管理员: {{ statistics.adminUsers || 0 }}</span>
            <span class="highlight-info" style="margin-left: 8px">普通用户: {{ statistics.normalUsers || 0 }}</span>
          </div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card :bodyStyle="{ padding: '20px' }">
          <template #title>
            <span class="card-title">
              <shopping-outlined /> 总成交量
            </span>
          </template>
          <div class="card-value">{{ statistics.totalVolume || 0 }}</div>
          <div class="card-footer">
            今日成交: <span class="highlight">{{ statistics.todayVolume || 0 }}</span>
          </div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card :bodyStyle="{ padding: '20px' }">
          <template #title>
            <span class="card-title">
              <dollar-outlined /> 总成交额
            </span>
          </template>
          <div class="card-value">¥{{ (statistics.totalAmount || 0).toFixed(2) }}</div>
          <div class="card-footer">
            今日成交: <span class="highlight">¥{{ (statistics.todayAmount || 0).toFixed(2) }}</span>
          </div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card :bodyStyle="{ padding: '20px' }">
          <template #title>
            <span class="card-title">
              <shop-outlined /> 商家数量
            </span>
          </template>
          <div class="card-value">{{ statistics.totalMerchants || 0 }}</div>
          <div class="card-placeholder"></div>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="16" style="margin-top: 16px">
      <a-col :span="6">
        <a-card :bodyStyle="{ padding: '20px' }">
          <template #title>
            <span class="card-title">
              <notification-outlined /> 公告数量
            </span>
          </template>
          <div class="card-value">{{ statistics.totalAnnouncements || 0 }}</div>
          <div class="card-footer">
            <span class="highlight-warning">置顶: {{ statistics.topAnnouncements || 0 }}</span>
            <span class="highlight-info" style="margin-left: 8px">普通: {{ statistics.normalAnnouncements || 0 }}</span>
          </div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card :bodyStyle="{ padding: '20px' }">
          <template #title>
            <span class="card-title">
              <rise-outlined /> 商品上架数量
            </span>
          </template>
          <div class="card-value">{{ statistics.onSaleProducts || 0 }}</div>
          <div class="card-placeholder"></div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card :bodyStyle="{ padding: '20px' }">
          <template #title>
            <span class="card-title">
              <shopping-cart-outlined /> 商品数量
            </span>
          </template>
          <div class="card-value">{{ statistics.totalProducts || 0 }}</div>
          <div class="card-placeholder"></div>
        </a-card>
      </a-col>
      <a-col :span="6">
        <a-card :bodyStyle="{ padding: '20px' }">
          <template #title>
            <span class="card-title">
              <appstore-outlined /> 商品种类数量
            </span>
          </template>
          <div class="card-value">{{ statistics.productCategories || 0 }}</div>
          <div class="card-placeholder"></div>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="16" style="margin-top: 16px">
      <a-col :span="24">
        <a-card title="成交趋势">
          <div ref="trendChart" style="height: 300px"></div>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="16" style="margin-top: 16px">
      <a-col :span="12">
        <a-card title="品类排行" :bodyStyle="{ height: '680px' }">
          <a-row>
            <a-col :span="24">
              <div ref="typeChart" style="height: 300px"></div>
            </a-col>
            <a-col :span="24" style="margin-top: 16px">
              <a-table
                :columns="typeRankColumns"
                :data-source="typeRankData"
                :pagination="false"
                size="small"
                :scroll="{ y: 220 }"
              >
                <template #bodyCell="{ column, record, index }">
                  <template v-if="column.key === 'index'">
                    {{ index + 1 }}
                  </template>
                  <template v-if="column.key === 'productType'">
                    <a @click="showMerchantRank(record.productType)">{{ record.productType }}</a>
                  </template>
                </template>
              </a-table>
            </a-col>
          </a-row>
        </a-card>
      </a-col>

      <a-col :span="12">
        <a-card :title="'商家排行 - ' + (selectedType || '全部')" :bodyStyle="{ height: '680px' }">
          <a-row>
            <a-col :span="24">
              <div ref="merchantChart" style="height: 300px"></div>
            </a-col>
            <a-col :span="24" style="margin-top: 16px">
              <a-table
                :columns="merchantRankColumns"
                :data-source="merchantRankData"
                :pagination="false"
                size="small"
                :scroll="{ y: 220 }"
              >
                <template #bodyCell="{ column, record, index }">
                  <template v-if="column.key === 'index'">
                    {{ index + 1 }}
                  </template>
                </template>
              </a-table>
            </a-col>
          </a-row>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick } from 'vue'
import { 
  UserOutlined, 
  ShoppingOutlined, 
  DollarOutlined, 
  ShopOutlined,
  NotificationOutlined,
  RiseOutlined,
  ShoppingCartOutlined,
  AppstoreOutlined
} from '@ant-design/icons-vue'
import * as echarts from 'echarts'
import { 
  getOverview, 
  getTrend, 
  getTypeRank, 
  getMerchantRank,
  getAnnouncementStats,
  getUserStats,
  getMerchantStats,
  getProductMerchantStats,
  getProductStats,
  getProductTypeStats
} from '@/api/adminstatics'
import type { 
  Overview, 
  TrendItem, 
  TypeRankItem, 
  MerchantRankItem,
  AnnouncementStats,
  UserStats,
  MerchantStats,
  ProductStats,
  ProductTypeStats
} from '@/api/adminstatics'

const statistics = ref<Overview & {
  totalUsers: number;
  adminUsers: number;
  normalUsers: number;
  totalMerchants: number;
  totalAnnouncements: number;
  topAnnouncements: number;
  normalAnnouncements: number;
  onSaleProducts: number;
  totalProducts: number;
  productCategories: number;
}>({
  totalUsers: 0,
  adminUsers: 0,
  normalUsers: 0,
  totalVolume: 0,
  todayVolume: 0,
  totalAmount: 0,
  todayAmount: 0,
  totalMerchants: 0,
  totalAnnouncements: 0,
  topAnnouncements: 0,
  normalAnnouncements: 0,
  onSaleProducts: 0,
  totalProducts: 0,
  productCategories: 0
})

const trendChart = ref<HTMLElement>()
let chart: echarts.ECharts | null = null

const typeChart = ref<HTMLElement>()
const typeRankData = ref<TypeRankItem[]>([])
const typeRankColumns = [
  { title: '排名', key: 'index', width: 60 },
  { title: '品类', key: 'productType', width: 100 },
  { 
    title: '成交量', 
    dataIndex: 'totalVolume',
    key: 'totalVolume', 
    align: 'right',
    width: 100,
    customRender: ({ record }: { record: TypeRankItem }) => `${record.totalVolume} 件`
  },
  { 
    title: '占比', 
    key: 'percentage', 
    align: 'right',
    width: 80,
    customRender: ({ record }: { record: TypeRankItem }) => calculatePercentage(record.totalVolume, typeRankData.value) + '%'
  }
]

const merchantChart = ref<HTMLElement>()
const merchantRankData = ref<MerchantRankItem[]>([])
const selectedType = ref('')
const merchantRankColumns = [
  { title: '排名', key: 'index', width: 60 },
  { 
    title: '商家', 
    dataIndex: 'merchantName',
    key: 'merchantName', 
    width: 160,
    customRender: ({ record }: { record: MerchantRankItem }) => record.merchantName
  },
  { 
    title: '成交量', 
    dataIndex: 'totalVolume',
    key: 'totalVolume', 
    align: 'right',
    width: 100,
    customRender: ({ record }: { record: MerchantRankItem }) => `${record.totalVolume} 件`
  },
  { 
    title: '占比', 
    key: 'percentage', 
    align: 'right',
    width: 80,
    customRender: ({ record }: { record: MerchantRankItem }) => calculatePercentage(record.totalVolume, merchantRankData.value) + '%'
  }
]

onMounted(async () => {
  await Promise.all([
    fetchOverview(),
    fetchTrend(),
    fetchTypeRank(),
    fetchMerchantRank(),
    fetchAnnouncementStats(),
    fetchUserStats(),
    fetchMerchantStats(),
    fetchProductStats()
  ])
})

async function fetchOverview() {
  try {
    const response = await getOverview()
    if (response.data.code === 0) {
      Object.assign(statistics.value, response.data.data)
    }
  } catch (error) {
    console.error('获取概览数据失败:', error)
  }
}

async function fetchTrend() {
  try {
    const response = await getTrend()
    if (response.data.code === 0) {
      const trend = response.data.data.trend
      await nextTick()
      initChart(trend)
    }
  } catch (error) {
    console.error('获取趋势数据失败:', error)
  }
}

async function fetchTypeRank() {
  try {
    const response = await getTypeRank()
    if (response.data.code === 0) {
      typeRankData.value = response.data.data.rank.map(item => ({
        ...item,
        totalVolume: Number(item.totalVolume)
      }))
      await nextTick()
      initTypeChart()
    }
  } catch (error) {
    console.error('获取品类排行失败:', error)
  }
}

async function fetchMerchantRank(type?: string) {
  try {
    const response = await getMerchantRank(type || '')
    if (response.data.code === 0) {
      merchantRankData.value = response.data.data.rank.map(item => ({
        ...item,
        totalVolume: Number(item.totalVolume)
      }))
      await nextTick()
      initMerchantChart()
    }
  } catch (error) {
    console.error('获取商家排行失败:', error)
  }
}

async function fetchAnnouncementStats() {
  try {
    const response = await getAnnouncementStats()
    if (response.data.code === 0) {
      const { total, topCount, normalCount } = response.data.data
      statistics.value.totalAnnouncements = total
      statistics.value.topAnnouncements = topCount
      statistics.value.normalAnnouncements = normalCount
    }
  } catch (error) {
    console.error('获取公告统计数据失败:', error)
  }
}

async function fetchUserStats() {
  try {
    const response = await getUserStats()
    if (response.data.code === 0) {
      const { total, userCount, adminCount } = response.data.data
      statistics.value.totalUsers = total
      statistics.value.normalUsers = userCount
      statistics.value.adminUsers = adminCount
    }
  } catch (error) {
    console.error('获取用户统计数据失败:', error)
  }
}

async function fetchMerchantStats() {
  try {
    const response = await getMerchantStats()
    if (response.data.code === 0) {
      statistics.value.totalMerchants = response.data.data.total
    }
  } catch (error) {
    console.error('获取商家统计数据失败:', error)
  }
}

async function fetchProductStats() {
  try {
    const [productMerchantResponse, productResponse, productTypeResponse] = await Promise.all([
      getProductMerchantStats(),
      getProductStats(),
      getProductTypeStats()
    ])
    
    if (productMerchantResponse.data.code === 0) {
      statistics.value.onSaleProducts = productMerchantResponse.data.data.total
    }
    
    if (productResponse.data.code === 0) {
      statistics.value.totalProducts = productResponse.data.data.total
    }
    
    if (productTypeResponse.data.code === 0) {
      statistics.value.productCategories = productTypeResponse.data.data.total
    }
  } catch (error) {
    console.error('获取商品相关统计数据失败:', error)
  }
}

function initChart(trend: TrendItem[]) {
  if (!trendChart.value) return
  
  if (!chart) {
    chart = echarts.init(trendChart.value)
  }

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        crossStyle: {
          color: '#999'
        }
      }
    },
    legend: {
      data: ['成交量', '成交额']
    },
    xAxis: {
      type: 'category',
      data: trend.map(item => item.day),
      axisPointer: {
        type: 'shadow'
      }
    },
    yAxis: [
      {
        type: 'value',
        name: '成交量',
        position: 'left'
      },
      {
        type: 'value',
        name: '成交额',
        position: 'right',
        axisLabel: {
          formatter: '{value} 元'
        }
      }
    ],
    series: [
      {
        name: '成交量',
        type: 'bar',
        data: trend.map(item => item.volume)
      },
      {
        name: '成交额',
        type: 'line',
        yAxisIndex: 1,
        data: trend.map(item => item.amount)
      }
    ]
  }

  chart.setOption(option)
}

function calculatePercentage(value: number, dataArray: { totalVolume: number }[]) {
  const total = dataArray.reduce((sum, item) => sum + item.totalVolume, 0)
  return ((value / total) * 100).toFixed(1)
}

function initTypeChart() {
  if (!typeChart.value) return
  const chartInstance = echarts.init(typeChart.value)
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}件 ({d}%)'
    },
    legend: {
      orient: 'horizontal',
      bottom: 0,
      left: 'center',
      type: 'scroll',
      pageIconSize: 12,
      pageTextStyle: { fontSize: 12 },
      itemWidth: 15,
      itemHeight: 10,
      textStyle: { fontSize: 12 }
    },
    series: [
      {
        name: '品类成交量',
        type: 'pie',
        radius: ['45%', '75%'],
        center: ['50%', '45%'],
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          position: 'outside',
          formatter: (params: { name: string; value: number }) => {
            return `${params.name}: ${calculatePercentage(params.value, typeRankData.value)}%`
          },
          fontSize: 12
        },
        labelLine: {
          show: true,
          length: 10,
          length2: 10,
          smooth: true
        },
        data: typeRankData.value.map(item => ({
          name: item.productType,
          value: Number(item.totalVolume)
        }))
      }
    ]
  }
  
  chartInstance.setOption(option)
  window.addEventListener('resize', () => chartInstance.resize())
}

function initMerchantChart() {
  if (!merchantChart.value) return
  const chartInstance = echarts.init(merchantChart.value)
  
  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c}件 ({d}%)'
    },
    legend: {
      orient: 'horizontal',
      bottom: 0,
      left: 'center',
      type: 'scroll',
      pageIconSize: 12,
      pageTextStyle: { fontSize: 12 },
      itemWidth: 15,
      itemHeight: 10,
      textStyle: { fontSize: 12 }
    },
    series: [
      {
        name: '商家成交量',
        type: 'pie',
        radius: ['45%', '75%'],
        center: ['50%', '45%'],
        avoidLabelOverlap: true,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: true,
          position: 'outside',
          formatter: (params: { name: string; value: number }) => {
            return `${params.name}: ${calculatePercentage(params.value, merchantRankData.value)}%`
          },
          fontSize: 12
        },
        labelLine: {
          show: true,
          length: 10,
          length2: 10,
          smooth: true
        },
        data: merchantRankData.value.map(item => ({
          name: item.merchantName,
          value: Number(item.totalVolume)
        }))
      }
    ]
  }
  
  chartInstance.setOption(option)
  window.addEventListener('resize', () => chartInstance.resize())
}

async function showMerchantRank(type: string) {
  selectedType.value = type
  await fetchMerchantRank(type)
}
</script>

<style scoped>
.dashboard {
  padding: 24px;
}

.card-title {
  font-size: 14px;
  color: rgba(0, 0, 0, 0.85);
}

.card-value {
  font-size: 24px;
  font-weight: bold;
  margin: 16px 0;
}

.card-footer {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.45);
  height: 20px;
  line-height: 20px;
}

.card-placeholder {
  height: 20px;
}

.highlight {
  color: #1890ff;
  font-weight: bold;
}

.highlight-warning {
  color: #faad14;
  font-weight: bold;
}

.highlight-info {
  color: #1890ff;
  font-weight: bold;
}

:deep(.ant-card-body) {
  padding: 16px;
}

:deep(.ant-card) {
  height: 100%;
}

:deep(.ant-card-head) {
  min-height: 48px;
}

:deep(.ant-card-head-title) {
  padding: 12px 0;
}
</style> 