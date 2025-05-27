/**
 * 商品相关的 API 封装
 * @module product
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
  /** 商品类型 */
  type?: string | null
  /** 商家名称 */
  merchantName?: string | null
}

/**
 * 商品和商家关联参数接口
 * @interface ProductMerchantParams
 */
interface ProductMerchantParams {
  /** 商品ID */
  productId: number | string
  /** 商家ID */
  merchantId: number | string
}

/**
 * 用户操作参数接口
 * @interface UserActionParams
 * @extends {ProductMerchantParams}
 */
interface UserActionParams extends ProductMerchantParams {
  /** 用户ID */
  userId: number | string
}

/**
 * 订单参数接口
 * @interface OrderParams
 * @extends {UserActionParams}
 */
interface OrderParams extends UserActionParams {
  /** 商品ID */
  productId: number | string
  /** 商家ID */
  merchantId: number | string  
  /** 购买数量 */
  quantity: number

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
 * 获取商品列表
 * @param {PaginationParams} params - 分页和筛选参数
 * @returns {Promise<AxiosResponse<ApiResponse>>} 商品列表数据
 */
export const getProductList = (params: PaginationParams): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/product-merchant/filtered-list', {
    page: params.page || 1,
    size: params.size || 10,
    type: params.type || null,
    merchantName: params.merchantName || null
  })
}

/**
 * 获取商品详情
 * @param {Pick<ProductMerchantParams, 'productId'>} params - 商品ID参数
 * @returns {Promise<AxiosResponse<ApiResponse>>} 商品详情数据
 */
export const getProductDetail = (params: Pick<ProductMerchantParams, 'productId'>): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/product/detail', {
    id: params.productId
  })
}

/**
 * 获取商家详情
 * @param {Pick<ProductMerchantParams, 'merchantId'>} params - 商家ID参数
 * @returns {Promise<AxiosResponse<ApiResponse>>} 商家详情数据
 */
export const getMerchantDetail = (params: Pick<ProductMerchantParams, 'merchantId'>): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/merchant/detail', {
    merchantId: params.merchantId
  })
}

// 点赞相关 API
/**
 * 添加点赞
 * @param {UserActionParams} params - 用户点赞参数
 * @returns {Promise<AxiosResponse<ApiResponse>>} 点赞结果
 */
export const addLike = (params: UserActionParams): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/like/add', params)
}

/**
 * 取消点赞
 * @param {UserActionParams} params - 用户点赞参数
 * @returns {Promise<AxiosResponse<ApiResponse>>} 取消点赞结果
 */
export const removeLike = (params: UserActionParams): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/like/remove', params)
}

/**
 * 检查是否已点赞
 * @param {UserActionParams} params - 用户点赞参数
 * @returns {Promise<AxiosResponse<ApiResponse>>} 点赞状态
 */
export const isLiked = (params: UserActionParams): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/like/isLiked', params)
}

/**
 * 获取点赞数量
 * @param {ProductMerchantParams} params - 商品商家参数
 * @returns {Promise<AxiosResponse<ApiResponse>>} 点赞数量
 */
export const getLikeCount = (params: ProductMerchantParams): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/like/count', params)
}

// 收藏相关 API
/**
 * 添加收藏
 * @param {UserActionParams} params - 用户收藏参数
 * @returns {Promise<AxiosResponse<ApiResponse>>} 收藏结果
 */
export const addFavorite = (params: UserActionParams): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/favorite/add', params)
}

/**
 * 取消收藏
 * @param {UserActionParams} params - 用户收藏参数
 * @returns {Promise<AxiosResponse<ApiResponse>>} 取消收藏结果
 */
export const removeFavorite = (params: UserActionParams): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/favorite/remove', params)
}

/**
 * 检查是否已收藏
 * @param {UserActionParams} params - 用户收藏参数
 * @returns {Promise<AxiosResponse<ApiResponse>>} 收藏状态
 */
export const isFavorite = (params: UserActionParams): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/favorite/isFavorite', params)
}

/**
 * 获取收藏数量
 * @param {ProductMerchantParams} params - 商品商家参数
 * @returns {Promise<number>} 收藏数量
 */
export const getFavoriteCount = async (params: ProductMerchantParams): Promise<number> => {
  const { data } = await api.post<ApiResponse>('/favorite/count', {
    productId: params.productId,
    merchantId: params.merchantId
  })
  return data.code === 0 ? data.data.count : 0
}

// 评论相关 API
/**
 * 获取评论列表
 * @param {ProductMerchantParams} params - 商品商家参数
 * @returns {Promise<AxiosResponse<ApiResponse>>} 评论列表数据
 */
export const getComments = (params: ProductMerchantParams): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/comment/list', params)
}

/**
 * 添加评论
 * @param {UserActionParams & { content: string }} params - 用户评论参数
 * @returns {Promise<AxiosResponse<ApiResponse>>} 评论结果
 */
export const addComment = (params: UserActionParams & { content: string }): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/comment/add', params)
}

/**
 * 创建订单
 * @param {OrderParams} params - 订单参数
 * @returns {Promise<AxiosResponse<ApiResponse>>} 创建订单结果
 */
export const createOrder = (params: OrderParams): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/order/buy', params)
}

/**
 * 获取所有商品类型列表
 * @returns {Promise<AxiosResponse<ApiResponse>>} 商品类型列表
 */
export const getProductTypes = (): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/product/types')
}

/**
 * 获取所有商家列表
 * @returns {Promise<AxiosResponse<ApiResponse>>} 商家列表
 */
export const getMerchantList = (): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/merchant/list', {
    page: 1,
    size: 1000  // 获取足够多的商家数据用于搜索
  })
} 