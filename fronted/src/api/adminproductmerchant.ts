/**
 * 商品上架管理相关的 API 封装
 * @module adminproductmerchant
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
  /** 商品ID（可选） */
  productId?: number
  /** 商家ID（可选） */
  merchantId?: number
  /** 商品名称（可选） */
  productName?: string
  /** 商家名称（可选） */
  merchantName?: string
  /** 上架状态（可选） */
  status?: number
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
 * 商品上架数据接口
 * @interface ProductMerchant
 */
export interface ProductMerchant {
  /** 商品ID */
  productId: number
  /** 商家ID */
  merchantId: number
  /** 当前价格 */
  currentPrice: number
  /** 上架状态：1-上架，0-下架 */
  status: number
  /** 创建时间 */
  createTime?: string
  /** 商品名称（前端补充） */
  productName?: string
  /** 商家名称（前端补充） */
  merchantName?: string
}

/**
 * 商品数据接口
 * @interface Product
 */
export interface Product {
  /** 商品ID */
  id: number
  /** 商品名称 */
  name: string
  /** 商品类型 */
  type: string
}

/**
 * 商家数据接口
 * @interface Merchant
 */
export interface Merchant {
  /** 商家ID */
  id: number
  /** 商家名称 */
  name: string
  /** 联系电话 */
  phone: string
}

/**
 * 获取商品上架列表
 * @param {PaginationParams} params - 分页和搜索参数
 * @returns {Promise<AxiosResponse<ApiResponse>>} 商品上架列表数据
 */
export const getProductMerchantList = (params: PaginationParams): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/product-merchant/list', {
    page: params.page || 1,
    size: params.size || 10,
    productId: params.productId,
    merchantId: params.merchantId,
    productName: params.productName,
    merchantName: params.merchantName,
    status: params.status
  })
}

/**
 * 添加商品上架
 * @param {Object} params - 上架参数
 * @returns {Promise<AxiosResponse<ApiResponse>>} 添加结果
 */
export const addProductMerchant = (params: {
  productId: number
  merchantId: number
  currentPrice: number
}): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/product-merchant/add', params)
}

/**
 * 修改商品状态
 * @param {Object} params - 状态参数
 * @returns {Promise<AxiosResponse<ApiResponse>>} 修改结果
 */
export const updateProductMerchantStatus = (params: {
  productId: number
  merchantId: number
  status: number
}): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/product-merchant/status', params)
}

/**
 * 获取商品详情
 * @param {number} id - 商品ID
 * @returns {Promise<AxiosResponse<ApiResponse>>} 商品详情
 */
export const getProductDetail = (id: number): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/product/detail', { id })
}

/**
 * 获取商家详情
 * @param {number} id - 商家ID
 * @returns {Promise<AxiosResponse<ApiResponse>>} 商家详情
 */
export const getMerchantDetail = (id: number): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/merchant/detail', { id })
}

/**
 * 获取商品列表
 * @param {Object} params - 查询参数
 * @returns {Promise<AxiosResponse<ApiResponse>>} 商品列表数据
 */
export const getProductList = (params: {
  page: number
  size: number
  type?: string
}): Promise<AxiosResponse<ApiResponse<{ list: Product[], total: number }>>> => {
  return api.post('/admin/product/list', params)
}

/**
 * 获取商家列表
 * @param {Object} params - 查询参数
 * @returns {Promise<AxiosResponse<ApiResponse>>} 商家列表数据
 */
export const getMerchantList = (params: {
  page: number
  size: number
  name?: string
  phone?: string
}): Promise<AxiosResponse<ApiResponse<{ list: Merchant[], total: number }>>> => {
  return api.post('/admin/merchant/list', params)
} 