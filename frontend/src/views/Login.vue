<template>
    <div style="height: 100vh; display: flex; justify-content: center; align-items: center; background: linear-gradient(135deg, #1890ff, #722ed1);">
        <el-card style="width: 400px;">
            <h2 style="text-align: center; margin-bottom: 30px;">高校实验室设备巡检管理系统</h2>
            <el-form :model="form" @submit.prevent>
                <el-form-item>
                    <el-input v-model="form.username" placeholder="账号" prefix-icon="User" size="large" />
                </el-form-item>
                <el-form-item>
                    <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" size="large" @keyup.enter="handleLogin" />
                </el-form-item>
                <el-button type="primary" size="large" style="width: 100%;" :loading="loading" @click="handleLogin">
                    登 录
                </el-button>
            </el-form>
            <div style="margin-top: 20px; color: #999; font-size: 12px; line-height: 1.8;">
                测试账号：admin / zhangsan / lisi / wangwu，密码统一：123456
            </div>
        </el-card>
    </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const router = useRouter()
const form = reactive({ username: 'admin', password: '123456' })
const loading = ref(false)

const handleLogin = async () => {
    if (!form.username || !form.password) {
        ElMessage.warning('请输入账号和密码')
        return
    }
    loading.value = true
    try {
        const res = await request.post('/api/auth/login', form)
        localStorage.setItem('token', res.data.token)
        localStorage.setItem('userName', res.data.user.name)
        localStorage.setItem('userRole', res.data.user.role)
        ElMessage.success('登录成功')
        router.push('/dashboard')
    } finally {
        loading.value = false
    }
}
</script>
