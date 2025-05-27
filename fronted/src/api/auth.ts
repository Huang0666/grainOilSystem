import axios from 'axios'
import type { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse } from 'axios'
import { message } from 'ant-design-vue'

// 定义接口
interface LoginData {
  username: string
  password: string
}

interface RegisterData extends LoginData {
  username: string
  password: string
  phone: string
}

interface ApiResponse<T = any> {
  code: number
  msg: string
  data: T
}

// 创建axios实例
const api: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
api.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 从localStorage获取token
    const token = localStorage.getItem('grain_oil_token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error: any) => {
    console.error('请求错误:', error)
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  (response: AxiosResponse<ApiResponse>) => {
    const res = response.data
    if (res.code === 0) {
      return response
    } else {
      message.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg || '请求失败'))
    }
  },
  (error: any) => {
    if (error.response?.status === 401) {
      // 未登录或token过期
      localStorage.removeItem('grain_oil_token')
      localStorage.removeItem('grain_oil_user')
      message.error('请先登录')
      // 可以在这里添加重定向到登录页的逻辑
      window.location.href = '/login'
    } else {
      console.error('响应错误:', error)
      message.error(error.response?.data?.msg || '网络错误，请检查后端服务是否启动')
    }
    return Promise.reject(error)
  }
)

// API 函数类型定义
export const userLogin = (data: LoginData): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/user/login', data)
}

export const adminLogin = (data: LoginData): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/admin/user/login', data)
}

export const userRegister = (data: RegisterData): Promise<AxiosResponse<ApiResponse>> => {
  return api.post('/user/register', data)
}

export default api 