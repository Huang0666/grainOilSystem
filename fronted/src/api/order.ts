/**
 * 订单相关的 API 封装
 * @module order
 */

import api from './auth'
import type { AxiosResponse } from 'axios'

/**
 * 订单数据接口
 * @interface Order
 */
export interface Order {
  /** 订单ID */
  id: number
  /** 用户ID */
  userId: number
  /** 商品ID */
  productId: number
  /** 商家ID */
  merchantId: number
  /** 购买数量 */
  quantity: number
  /** 单价 */
  price: number
  /** 总价 */
  totalPrice: number
  /** 下单时间 */
  createTime: string
}

/**
 * API 响应数据接口
 * @interface ApiResponse
 * @template T - 响应数据的类型
 */
export interface ApiResponse<T = any> {
  /** 响应状态码：0-成功，非0-失败 */
  code: number
  /** 响应消息 */
  msg: string
  /** 响应数据 */
  data: T
}

/**
 * 获取订单列表
 * @param {number} userId - 用户ID
 * @returns {Promise<AxiosResponse<ApiResponse<Order[]>>>} 订单列表数据
 */
export const getOrderList = (userId: number): Promise<AxiosResponse<ApiResponse<Order[]>>> => {
  return api.post('/order/list', { userId })
}

/**
 * 删除订单
 * @param {number} orderId - 订单ID
 * @param {number} userId - 用户ID
 * @returns {Promise<AxiosResponse<ApiResponse>>} 删除结果
 */
export const removeOrder = (orderId: number, userId: number): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/order/remove', { orderId, userId })
} 