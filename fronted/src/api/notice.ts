/**
 * 公告相关的 API 封装
 * @module notice
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
 * 公告数据接口
 * @interface Notice
 */
interface Notice {
  /** 公告ID */
  id: number
  /** 公告标题 */
  title: string
  /** 是否置顶 */
  isTop: number
  /** 公告内容 */
  content: string
  /** 创建时间 */
  createTime: string
}

/**
 * 获取公告列表
 * @param {PaginationParams} params - 分页参数
 * @returns {Promise<AxiosResponse<ApiResponse>>} 公告列表数据
 */
export const getNoticeList = (params: PaginationParams): Promise<AxiosResponse<ApiResponse>> => {
  // 检查用户登录状态
  const userInfo = localStorage.getItem('grain_oil_user')
  if (!userInfo) {
    return Promise.reject(new Error('请先登录'))
  }

  return api.post('/announcement/list', {
    page: params.page || 1,
    size: params.size || 10
  })
}

/**
 * 获取公告详情
 * @param {number} id - 公告ID
 * @returns {Promise<AxiosResponse<ApiResponse>>} 公告详情数据
 */
export const getNoticeDetail = (id: number): Promise<AxiosResponse<ApiResponse>> => {
  // 检查用户登录状态
  const userInfo = localStorage.getItem('grain_oil_user')
  if (!userInfo) {
    return Promise.reject(new Error('请先登录'))
  }

  return api.post('/announcement/detail', {
    id
  })
} 