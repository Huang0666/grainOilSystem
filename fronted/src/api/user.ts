/**
 * 用户管理相关的 API 封装
 * @module user
 */

import api from './auth'
import type { AxiosResponse } from 'axios'

/**
 * 分页查询参数接口
 * @interface UserListParams
 */
interface UserListParams {
  /** 当前页码 */
  page: number
  /** 每页数量 */
  size: number
  /** 用户名（可选） */
  username?: string
  /** 角色（可选） */
  role?: string
}

/**
 * 用户数据接口
 * @interface User
 */
interface User {
  /** 用户ID */
  id?: number
  /** 用户名 */
  username: string
  /** 密码 */
  password?: string
  /** 手机号 */
  phone: string
  /** 角色 */
  role: 'user' | 'admin'
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
 * 获取用户列表
 * @param {UserListParams} params - 查询参数
 * @returns {Promise<AxiosResponse<ApiResponse>>} 用户列表数据
 */
export const getUserList = (params: UserListParams): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/user/list', params)
}

/**
 * 添加用户
 * @param {User} user - 用户信息
 * @returns {Promise<AxiosResponse<ApiResponse>>} 添加结果
 */
export const addUser = (user: User): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/user/add', user)
}

/**
 * 更新用户
 * @param {User} user - 用户信息
 * @returns {Promise<AxiosResponse<ApiResponse>>} 更新结果
 */
export const updateUser = (user: User): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/user/update', user)
}

/**
 * 删除用户
 * @param {number} id - 用户ID
 * @returns {Promise<AxiosResponse<ApiResponse>>} 删除结果
 */
export const deleteUser = (id: number): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/user/delete', { id })
}

/**
 * 获取用户详情
 * @param {number} id - 用户ID
 * @returns {Promise<AxiosResponse<ApiResponse>>} 用户详情
 */
export const getUserDetail = (id: number): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/user/detail', { id })
}

/**
 * 重置用户密码
 * @param {number} id - 用户ID
 * @param {string} password - 新密码
 * @returns {Promise<AxiosResponse<ApiResponse>>} 重置结果
 */
export const resetUserPassword = (id: number, password: string): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/user/reset-password', { id, password })
} 