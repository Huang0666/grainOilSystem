/**
 * 商家管理相关的 API 封装
 * @module adminmerchant
 */

import api from './auth'
import type { AxiosResponse } from 'axios'

/**
 * 分页参数接口
 * @interface PaginationParams
 */
interface PaginationParams {
  /** 当前页码 */
  page: number
  /** 每页数量 */
  size: number
  /** 商家名称（可选） */
  name?: string
  /** 联系电话（可选） */
  phone?: string
}

/**
 * API 响应数据接口
 * @interface ApiResponse
 * @template T - 响应数据的类型
 */
interface ApiResponse<T = any> {
  /** 响应状态码：0-成功，非0-失败 */
  code: number
  /** 响应消息 */
  msg: string
  /** 响应数据 */
  data: T
}

/**
 * 商家数据接口
 * @interface Merchant
 */
export interface Merchant {
  /** 商家ID */
  id?: number
  /** 商家名称 */
  name: string
  /** 联系电话 */
  phone: string
}

/**
 * 获取商家列表
 * @param {PaginationParams} params - 分页和搜索参数
 * @returns {Promise<AxiosResponse<ApiResponse>>} 商家列表数据
 */
export const getMerchantList = (params: PaginationParams): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/merchant/list', {
    page: params.page || 1,
    size: params.size || 10,
    name: params.name,
    phone: params.phone
  })
}

/**
 * 添加商家
 * @param {Merchant} merchant - 商家信息
 * @returns {Promise<AxiosResponse<ApiResponse>>} 添加结果
 */
export const addMerchant = (merchant: Merchant): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/merchant/add', merchant)
}

/**
 * 更新商家
 * @param {Merchant} merchant - 商家信息
 * @returns {Promise<AxiosResponse<ApiResponse>>} 更新结果
 */
export const updateMerchant = (merchant: Merchant): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/merchant/update', merchant)
}

/**
 * 删除商家
 * @param {number} id - 商家ID
 * @returns {Promise<AxiosResponse<ApiResponse>>} 删除结果
 */
export const deleteMerchant = (id: number): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/merchant/delete', { id })
}

/**
 * 获取商家详情
 * @param {number} id - 商家ID
 * @returns {Promise<AxiosResponse<ApiResponse>>} 商家详情
 */
export const getMerchantDetail = (id: number): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/merchant/detail', { id })
} 