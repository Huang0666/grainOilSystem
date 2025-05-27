/**
 * 商品管理相关的 API 封装
 * @module adminproduct
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
  /** 商品类型（可选） */
  type?: string
  /** 商品名称（可选） */
  name?: string
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
 * 商品数据接口
 * @interface Product
 */
export interface Product {
  /** 商品ID */
  id?: number
  /** 商品名称 */
  name: string
  /** 商品类型 */
  type: string
  /** 商品描述 */
  description?: string
  /** 商品图片 */
  image?: string
  /** 创建时间 */
  createTime?: string
}

/**
 * 获取商品列表
 * @param {PaginationParams} params - 分页参数
 * @returns {Promise<AxiosResponse<ApiResponse>>} 商品列表数据
 */
export const getProductList = (params: PaginationParams): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/product/list', {
    page: params.page || 1,
    size: params.size || 10,
    type: params.type,
    name: params.name
  })
}

/**
 * 添加商品
 * @param {Product} product - 商品信息
 * @returns {Promise<AxiosResponse<ApiResponse>>} 添加结果
 */
export const addProduct = (product: Product): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/product/add', product)
}

/**
 * 更新商品
 * @param {Product} product - 商品信息
 * @returns {Promise<AxiosResponse<ApiResponse>>} 更新结果
 */
export const updateProduct = (product: Product): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/product/update', product)
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
 * 删除商品
 * @param {number} id - 商品ID
 * @returns {Promise<AxiosResponse<ApiResponse>>} 删除结果
 */
export const deleteProduct = (id: number): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/product/delete', { id })
}

/**
 * 获取所有商品类型列表
 * @returns {Promise<AxiosResponse<ApiResponse<string[]>>>} 商品类型列表
 */
export const getProductTypes = (): Promise<AxiosResponse<ApiResponse<string[]>>> => {
  return api.get('/admin/product/types')
} 