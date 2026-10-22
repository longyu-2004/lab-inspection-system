import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '../router'

const request = axios.create({
    baseURL: '/',
    timeout: 10000
})

request.interceptors.request.use(config => {
    const token = localStorage.getItem('token')
    if (token) {
        config.headers['Authorization'] = token
    }
    return config
})

request.interceptors.response.use(
    response => {
        const res = response.data
        if (res.code === 200) {
            return res
        }
        if (res.code === 401) {
            ElMessage.error('登录已过期，请重新登录')
            localStorage.removeItem('token')
            router.push('/login')
            return Promise.reject(new Error(res.msg))
        }
        ElMessage.error(res.msg || '请求失败')
        return Promise.reject(new Error(res.msg))
    },
    error => {
        ElMessage.error('网络异常')
        return Promise.reject(error)
    }
)

export default request
