import axios from 'axios';
import type { AxiosInstance } from 'axios';
import { message } from 'ant-design-vue';

// 创建axios实例
const api: AxiosInstance = axios.create({
    baseURL: '/api',
    timeout: 5000,
    headers: {
        'Content-Type': 'application/json'
    }
});

// 请求拦截器
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('grain_oil_token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// 响应拦截器
api.interceptors.response.use(
    (response) => {
        const { code, msg, data } = response.data;
        if (code !== 0) {
            message.error(msg || '请求失败');
            return Promise.reject(new Error(msg || '请求失败'));
        }
        return response;
    },
    (error) => {
        if (error.response?.status === 401) {
            localStorage.removeItem('grain_oil_token');
            localStorage.removeItem('grain_oil_user');
            window.location.href = '/';
            message.error('登录已过期，请重新登录');
        } else {
            message.error(error.response?.data?.msg || '网络错误');
        }
        return Promise.reject(error);
    }
);

export default api; 