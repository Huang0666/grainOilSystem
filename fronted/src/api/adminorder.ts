/**
 * 订单管理相关的 API 封装
 * @module adminorder
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
 * 订单数据接口
 * @interface AdminOrder
 */
export interface AdminOrder {
  /** 订单ID */
  id: number
  /** 用户ID */
  user_id: number
  /** 商品ID */
  product_id: number
  /** 商家ID */
  merchant_id: number
  /** 订单号 */
  orderNo?: string
  /** 商品名称 */
  productName: string
  /** 商家名称 */
  merchantName: string
  /** 用户名称 */
  username: string
  /** 联系电话 */
  phone?: string
  /** 单价 */
  price: number
  /** 数量 */
  quantity: number
  /** 总价 */
  total_price: number
  /** 创建时间 */
  create_time: string
  /** 订单状态 */
  status?: string
}

/**
 * 获取订单列表
 * @param {PaginationParams} params - 分页参数
 * @returns {Promise<AxiosResponse<ApiResponse>>} 订单列表数据
 */
export const getOrderList = (params: PaginationParams): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/order/list', {
    page: params.page || 1,
    size: params.size || 10
  })
}

/**
 * 获取订单详情
 * @param {number} id - 订单ID
 * @returns {Promise<AxiosResponse<ApiResponse>>} 订单详情
 */
export const getOrderDetail = (id: number): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/order/detail', { id })
}

/**
 * 删除订单
 * @param {number} id - 订单ID
 * @returns {Promise<AxiosResponse<ApiResponse>>} 删除结果
 */
export const deleteOrder = (id: number): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/order/delete', { id })
}

/**
 * 获取用户详情
 * @param {number} id - 用户ID
 * @returns {Promise<AxiosResponse<ApiResponse>>} 用户详情
 */
export const getUserDetail = (id: number): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/user/detail', { id })
} 