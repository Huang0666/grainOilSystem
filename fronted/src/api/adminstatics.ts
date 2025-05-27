/**
 * 统计数据相关的 API 封装
 * @module adminstatics
 */

import api from './auth'
import type { AxiosResponse } from 'axios'

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
 * 概览数据接口
 * @interface Overview
 */
export interface Overview {
  /** 总成交量 */
  totalVolume: number
  /** 总成交额 */
  totalAmount: number
  /** 今日成交量 */
  todayVolume: number
  /** 今日成交额 */
  todayAmount: number
}

/**
 * 趋势数据项接口
 * @interface TrendItem
 */
export interface TrendItem {
  /** 日期 */
  day: string
  /** 成交量 */
  volume: number
  /** 成交额 */
  amount: number
}

/**
 * 品类排行数据项接口
 * @interface TypeRankItem
 */
export interface TypeRankItem {
  /** 商品类型 */
  productType: string
  /** 总成交量 */
  totalVolume: number
}

/**
 * 商家排行数据项接口
 * @interface MerchantRankItem
 */
export interface MerchantRankItem {
  /** 商家ID */
  merchantId: number
  /** 商家名称 */
  merchantName: string
  /** 总成交量 */
  totalVolume: number
}

/**
 * 公告统计数据接口
 * @interface AnnouncementStats
 */
export interface AnnouncementStats {
  /** 公告总数 */
  total: number
  /** 置顶公告数量 */
  topCount: number
  /** 普通公告数量 */
  normalCount: number
}

/**
 * 用户统计数据接口
 * @interface UserStats
 */
export interface UserStats {
  /** 用户总数 */
  total: number
  /** 普通用户数量 */
  userCount: number
  /** 管理员数量 */
  adminCount: number
}

/**
 * 商家统计数据接口
 * @interface MerchantStats
 */
export interface MerchantStats {
  /** 商家总数 */
  total: number
}

/**
 * 商品统计数据接口
 * @interface ProductStats
 */
export interface ProductStats {
  /** 商品总数 */
  total: number
}

/**
 * 商品类型统计数据接口
 * @interface ProductTypeStats
 */
export interface ProductTypeStats {
  /** 商品类型总数 */
  total: number
}

/**
 * 获取概览数据
 * @returns {Promise<AxiosResponse<ApiResponse<Overview>>>} 概览数据
 */
export const getOverview = (): Promise<AxiosResponse<ApiResponse<Overview>>> => {
  return api.get('/statistics/overview')
}

/**
 * 获取趋势数据
 * @param {number} days - 天数
 * @returns {Promise<AxiosResponse<ApiResponse<{trend: TrendItem[]}>>>} 趋势数据
 */
export const getTrend = (days: number = 7): Promise<AxiosResponse<ApiResponse<{trend: TrendItem[]}>>> => {
  return api.post('/statistics/trend', { days })
}

/**
 * 获取品类排行
 * @param {number} limit - 限制数量
 * @returns {Promise<AxiosResponse<ApiResponse<{rank: TypeRankItem[]}>>>} 品类排行数据
 */
export const getTypeRank = (limit: number = 10): Promise<AxiosResponse<ApiResponse<{rank: TypeRankItem[]}>>> => {
  return api.post('/statistics/type/rank', { limit })
}

/**
 * 获取商家排行
 * @param {string} type - 商品类型
 * @param {number} limit - 限制数量
 * @returns {Promise<AxiosResponse<ApiResponse<{rank: MerchantRankItem[]}>>>} 商家排行数据
 */
export const getMerchantRank = (type: string, limit: number = 10): Promise<AxiosResponse<ApiResponse<{rank: MerchantRankItem[]}>>> => {
  return api.post('/statistics/type/merchant-rank', { type, limit })
}

/**
 * 获取公告统计数据
 * @returns {Promise<AxiosResponse<ApiResponse<AnnouncementStats>>>} 公告统计数据
 */
export const getAnnouncementStats = (): Promise<AxiosResponse<ApiResponse<AnnouncementStats>>> => {
  return api.get('/admin/announcement/stats')
}

/**
 * 获取用户统计数据
 * @returns {Promise<AxiosResponse<ApiResponse<UserStats>>>} 用户统计数据
 */
export const getUserStats = (): Promise<AxiosResponse<ApiResponse<UserStats>>> => {
  return api.get('/admin/user/stats')
}

/**
 * 获取商家统计数据
 * @returns {Promise<AxiosResponse<ApiResponse<MerchantStats>>>} 商家统计数据
 */
export const getMerchantStats = (): Promise<AxiosResponse<ApiResponse<MerchantStats>>> => {
  return api.get('/admin/merchant/stats')
}

/**
 * 获取商品-商家关联统计数据
 * @returns {Promise<AxiosResponse<ApiResponse<ProductStats>>>} 商品-商家关联统计数据
 */
export const getProductMerchantStats = (): Promise<AxiosResponse<ApiResponse<ProductStats>>> => {
  return api.get('/admin/product-merchant/stats')
}

/**
 * 获取商品统计数据
 * @returns {Promise<AxiosResponse<ApiResponse<ProductStats>>>} 商品统计数据
 */
export const getProductStats = (): Promise<AxiosResponse<ApiResponse<ProductStats>>> => {
  return api.get('/admin/product/stats')
}

/**
 * 获取商品类型统计数据
 * @returns {Promise<AxiosResponse<ApiResponse<ProductTypeStats>>>} 商品类型统计数据
 */
export const getProductTypeStats = (): Promise<AxiosResponse<ApiResponse<ProductTypeStats>>> => {
  return api.get('/admin/product/type-stats')
} 