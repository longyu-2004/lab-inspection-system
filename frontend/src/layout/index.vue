<template>
    <el-container style="height: 100vh">
        <el-aside width="220px" style="background-color: #001529">
            <div style="height: 60px; line-height: 60px; color: #fff; text-align: center; font-size: 16px; font-weight: bold;">
                实验室巡检系统
            </div>
            <el-menu
                :default-active="activeMenu"
                background-color="#001529"
                text-color="#fff"
                active-text-color="#1890ff"
                router
            >
                <el-menu-item index="/dashboard">
                    <el-icon><Odometer /></el-icon><span>仪表盘</span>
                </el-menu-item>
                <el-menu-item index="/equipment">
                    <el-icon><Monitor /></el-icon><span>设备管理</span>
                </el-menu-item>
                <el-menu-item index="/inspection">
                    <el-icon><List /></el-icon><span>巡检任务</span>
                </el-menu-item>
                <el-menu-item index="/order">
                    <el-icon><Warning /></el-icon><span>异常工单</span>
                </el-menu-item>
                <el-menu-item index="/user" v-if="userRole === 'ADMIN'">
                    <el-icon><User /></el-icon><span>用户管理</span>
                </el-menu-item>
            </el-menu>
        </el-aside>
        <el-container>
            <el-header style="background: #fff; border-bottom: 1px solid #eee; display: flex; justify-content: space-between; align-items: center;">
                <div style="font-size: 16px;">{{ activeTitle }}</div>
                <el-dropdown @command="handleCommand">
                    <span style="cursor: pointer;">
                        <el-icon><UserFilled /></el-icon>
                        {{ userName }}（{{ roleText }}）
                    </span>
                    <template #dropdown>
                        <el-dropdown-menu>
                            <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </el-header>
            <el-main style="background: #f0f2f5;">
                <router-view />
            </el-main>
        </el-container>
    </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const route = useRoute()
const router = useRouter()

const activeMenu = computed(() => route.path)
const activeTitle = computed(() => route.name || '')
const userName = computed(() => localStorage.getItem('userName') || '未登录')
const userRole = computed(() => localStorage.getItem('userRole') || '')
const roleText = computed(() => {
    const map = { ADMIN: '管理员', MANAGER: '实验室管理员', INSPECTOR: '巡检员' }
    return map[userRole.value] || ''
})

const handleCommand = async (cmd) => {
    if (cmd === 'logout') {
        await request.post('/api/auth/logout')
        localStorage.clear()
        ElMessage.success('已退出登录')
        router.push('/login')
    }
}
</script>
