/**
 * 产品趋势分析相关 API 封装
 * @module trend
 */

import api from './auth'
import type { AxiosResponse } from 'axios'

/**
 * API 响应数据接口
 */
interface ApiResponse<T = any> {
  code: number
  msg: string
  data: T
}

/**
 * 产品ID列表
 */
export const getProductIds = (): Promise<AxiosResponse<ApiResponse<number[]>>> => {
  const userInfo = localStorage.getItem('grain_oil_user')
  if (!userInfo) {
    return Promise.reject(new Error('请先登录'))
  }
  return api.get('/price/product-ids')
}

/**
 * 获取产品详情
 * @param {number} id - 产品ID
 */
export const getProductDetail = (id: number): Promise<AxiosResponse<ApiResponse<any>>> => {
  const userInfo = localStorage.getItem('grain_oil_user')
  if (!userInfo) {
    return Promise.reject(new Error('请先登录'))
  }
  return api.post('/product/detail', { id })
}

/**
 * 获取历史价格走势
 * @param params { productId: number, days: number }
 */
export const getTrend = (params: { productId: number, days: number }): Promise<AxiosResponse<ApiResponse<any>>> => {
  const userInfo = localStorage.getItem('grain_oil_user')
  if (!userInfo) {
    return Promise.reject(new Error('请先登录'))
  }
  return api.post('/price/trend', params)
}

/**
 * 获取价格预测
 * @param params { productId: number, days: number }
 */
export const getPredict = (params: { productId: number, days: number }): Promise<AxiosResponse<ApiResponse<any>>> => {
  const userInfo = localStorage.getItem('grain_oil_user')
  if (!userInfo) {
    return Promise.reject(new Error('请先登录'))
  }
  return api.post('/price/predict', params)
} 