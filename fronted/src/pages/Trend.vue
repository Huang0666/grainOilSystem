<template>
  <div class="trend-page page-container">
    <div class="page-header">
      <h2>粮油价格趋势分析</h2>
      <div class="header-right">
        <a-select
          v-model:value="selectedProductId"
          :options="productOptions"
          placeholder="选择商品"
          style="width: 200px"
          :loading="loadingProduct"
        />
        <a-radio-group v-model:value="timeRange" @change="handleTimeRangeChange" button-style="solid">
          <a-radio-button value="week">周</a-radio-button>
          <a-radio-button value="month">月</a-radio-button>
          <a-radio-button value="year">年</a-radio-button>
        </a-radio-group>
      </div>
    </div>

    <div class="content-area">
      <div class="charts-container">
        <div class="chart-wrapper">
          <div ref="priceChartRef" class="chart"></div>
        </div>
        <div class="chart-wrapper">
          <div ref="predictionChartRef" class="chart"></div>
        </div>
      </div>

      <div class="analysis-section">
        <a-card title="价格分析报告" :bordered="false" style="width: 100%;">
          <a-descriptions>
            <a-descriptions-item label="当前价格">¥{{ currentPrice }}</a-descriptions-item>
            <a-descriptions-item label="价格趋势">
              <span :class="priceChange >= 0 ? 'price-up' : 'price-down'">
                {{ priceChange >= 0 ? '↑' : '↓' }} {{ Math.abs(priceChange) }}%
              </span>
            </a-descriptions-item>
            <a-descriptions-item label="预测走势">{{ prediction }}</a-descriptions-item>
          </a-descriptions>
          <div class="analysis-content">
            {{ analysisContent }}
          </div>
        </a-card>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import * as echarts from 'echarts'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { getProductIds, getProductDetail, getTrend, getPredict } from '../api/trend'

const router = useRouter()
const selectedProductId = ref<number | null>(null)
const timeRange = ref('month')
const priceChartRef = ref(null)
const predictionChartRef = ref(null)
const currentPrice = ref<number>(0)
const priceChange = ref<number>(0)
const prediction = ref('')
const analysisContent = ref('')
const productOptions = ref<{ value: number, label: string }[]>([])
const loadingProduct = ref(false)
const trendData = ref<{ date: string, price: number }[]>([])
const predictData = ref<{ date: string, price: number }[]>([])
const trendLoading = ref(false)
const predictLoading = ref(false)
const trendType = ref('')
const slope = ref<number>(0)

let priceChart = null
let predictionChart = null

// 登录态校验
const checkLogin = () => {
  const userInfo = localStorage.getItem('grain_oil_user')
  if (!userInfo) {
    message.error('请先登录')
    router.push('/login')
    return false
  }
  return true
}

// 获取产品下拉数据
const fetchProductOptions = async () => {
  if (!checkLogin()) return
  loadingProduct.value = true
  try {
    const { data } = await getProductIds()
    if (data.code === 0 && Array.isArray(data.data)) {
      // 并发获取所有产品详情
      const detailPromises = data.data.map(id => getProductDetail(id))
      const details = await Promise.all(detailPromises)
      productOptions.value = details.map(res => ({
        value: res.data.data?.id,
        label: res.data.data?.name
      }))
      // 默认选中第一个
      if (productOptions.value.length > 0) {
        selectedProductId.value = productOptions.value[0].value
      }
    }
  } catch (e) {
    message.error(e.message || '获取产品列表失败')
  } finally {
    loadingProduct.value = false
  }
}

// 时间范围映射
const timeRangeMap: Record<string, number> = {
  week: 7,
  month: 30,
  year: 365
}

// 获取历史价格走势
const fetchTrend = async () => {
  if (!selectedProductId.value) return
  trendLoading.value = true
  try {
    const { data } = await getTrend({
      productId: selectedProductId.value,
      days: timeRangeMap[timeRange.value] || 7
    })
    if (data.code === 0 && Array.isArray(data.data.trend)) {
      trendData.value = data.data.trend.map((item: any) => ({
        date: item.recordTime ? item.recordTime.split('T')[0] : '',
        price: item.price
      }))
      // 当前价格、涨跌幅
      if (trendData.value.length > 0) {
        currentPrice.value = trendData.value[trendData.value.length - 1].price
        const first = trendData.value[0].price
        const last = trendData.value[trendData.value.length - 1].price
        priceChange.value = first ? Number((((last - first) / first) * 100).toFixed(2)) : 0
      } else {
        currentPrice.value = 0
        priceChange.value = 0
      }
    } else {
      trendData.value = []
      currentPrice.value = 0
      priceChange.value = 0
    }
  } catch (e: any) {
    trendData.value = []
    currentPrice.value = 0
    priceChange.value = 0
    message.error(e.message || '获取历史价格走势失败')
  } finally {
    trendLoading.value = false
  }
}

// 获取价格预测
const fetchPredict = async () => {
  if (!selectedProductId.value) return
  predictLoading.value = true
  try {
    const { data } = await getPredict({
      productId: selectedProductId.value,
      days: timeRangeMap[timeRange.value] || 7
    })
    if (data.code === 0 && Array.isArray(data.data.predictions)) {
      predictData.value = data.data.predictions.map((item: any) => ({
        date: item.date,
        price: item.price
      }))
      trendType.value = data.data.trend || ''
      slope.value = data.data.slope || 0
      prediction.value = `预计未来${timeRangeMap[timeRange.value]}天价格${trendType.value}`
      analysisContent.value = `预测斜率：${slope.value}，趋势：${trendType.value}`
    } else {
      predictData.value = []
      trendType.value = ''
      slope.value = 0
      prediction.value = ''
      analysisContent.value = ''
    }
  } catch (e: any) {
    predictData.value = []
    trendType.value = ''
    slope.value = 0
    prediction.value = ''
    analysisContent.value = ''
    message.error(e.message || '获取价格预测失败')
  } finally {
    predictLoading.value = false
  }
}

// 图表渲染
const initPriceChart = () => {
  if (priceChartRef.value) {
    if (priceChart) {
      priceChart.dispose()
    }
    priceChart = echarts.init(priceChartRef.value)
    const option = {
      title: { text: '历史价格走势' },
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: trendData.value.map(item => item.date)
      },
      yAxis: {
        type: 'value',
        name: '价格 (元/kg)'
      },
      series: [
        {
          name: '价格',
          type: 'line',
          data: trendData.value.map(item => item.price),
          smooth: true,
          lineStyle: { width: 2 }
        }
      ]
    }
    priceChart.setOption(option)
  }
}

const initPredictionChart = () => {
  if (predictionChartRef.value) {
    if (predictionChart) {
      predictionChart.dispose()
    }
    predictionChart = echarts.init(predictionChartRef.value)
    const option = {
      title: { text: '价格预测' },
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: predictData.value.map(item => item.date)
      },
      yAxis: {
        type: 'value',
        name: '预测价格 (元/kg)'
      },
      series: [
        {
          name: '预测价格',
          type: 'line',
          data: predictData.value.map(item => item.price),
          smooth: true,
          lineStyle: { width: 2, type: 'dashed' }
        }
      ]
    }
    predictionChart.setOption(option)
  }
}

const handleTimeRangeChange = (e) => {
  // 更新图表数据
  initPriceChart()
  initPredictionChart()
}

onMounted(() => {
  fetchProductOptions()
  initPriceChart()
  initPredictionChart()
  
  window.addEventListener('resize', () => {
    priceChart?.resize()
    predictionChart?.resize()
  })
})

watch([selectedProductId, timeRange], async () => {
  await fetchTrend()
  await fetchPredict()
  initPriceChart()
  initPredictionChart()
}, { immediate: true })
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
  display: flex;
  flex-direction: column;
}

.trend-page {
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

.charts-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 24px;
}

.chart-wrapper {
  /* background: #fff; */ /* 背景由 page-container 提供 */
  padding: 24px;
  border-radius: 8px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.06);
  border: 1px solid #f0f0f0; /* 添加边框以区分 */
}

.chart {
  height: 350px; /* 调整图表高度 */
}

.analysis-section {
  /* background: #fff; */ /* 背景由 page-container 提供 */
  border-radius: 8px;
  /* box-shadow: 0 1px 2px rgba(0, 0, 0, 0.06); */ /* 移除重复阴影 */
  margin-top: auto; /* 推动到容器底部 */
}

.analysis-content {
  margin-top: 16px;
  line-height: 1.6;
  color: #666;
}

.price-up {
  color: #f5222d;
}

.price-down {
  color: #52c41a;
}
</style> 