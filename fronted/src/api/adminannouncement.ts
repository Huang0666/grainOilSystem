/**
 * 公告管理相关的 API 封装
 * @module adminannouncement
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
  /** 公告标题（可选） */
  title?: string
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
 * @interface AdminAnnouncement
 */
export interface AdminAnnouncement {
  /** 公告ID */
  id: number
  /** 公告标题 */
  title: string
  /** 公告内容 */
  content: string
  /** 是否置顶 */
  isTop: number
  /** 创建时间 */
  createTime: string
}

/**
 * 获取公告列表
 * @param {PaginationParams} params - 分页和搜索参数
 * @returns {Promise<AxiosResponse<ApiResponse>>} 公告列表数据
 */
export const getAnnouncementList = (params: PaginationParams): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/announcement/list', {
    pageNum: params.page || 1,
    pageSize: params.size || 10,
    title: params.title || undefined
  })
}

/**
 * 添加公告
 * @param {Object} params - 公告信息
 * @returns {Promise<AxiosResponse<ApiResponse>>} 添加结果
 */
export const addAnnouncement = (params: {
  title: string
  content: string
  isTop: number
}): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/announcement/add', params)
}

/**
 * 更新公告
 * @param {AdminAnnouncement} announcement - 公告信息
 * @returns {Promise<AxiosResponse<ApiResponse>>} 更新结果
 */
export const updateAnnouncement = (announcement: AdminAnnouncement): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/announcement/update', announcement)
}

/**
 * 删除公告
 * @param {number} id - 公告ID
 * @returns {Promise<AxiosResponse<ApiResponse>>} 删除结果
 */
export const deleteAnnouncement = (id: number): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/announcement/delete', { id })
}

/**
 * 更新公告置顶状态
 * @param {Object} params - 置顶参数
 * @returns {Promise<AxiosResponse<ApiResponse>>} 更新结果
 */
export const updateAnnouncementTop = (params: {
  id: number
  isTop: number
}): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/announcement/top', params)
} 