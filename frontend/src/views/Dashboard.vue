<template>
    <div>
        <el-row :gutter="20">
            <el-col :span="6">
                <el-card shadow="hover">
                    <div style="text-align: center;">
                        <div style="font-size: 28px; color: #1890ff; font-weight: bold;">{{ stats.equipmentTotal || 0 }}</div>
                        <div style="color: #666; margin-top: 8px;">设备总数</div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="hover">
                    <div style="text-align: center;">
                        <div style="font-size: 28px; color: #52c41a; font-weight: bold;">{{ stats.equipmentNormal || 0 }}</div>
                        <div style="color: #666; margin-top: 8px;">正常设备</div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="hover">
                    <div style="text-align: center;">
                        <div style="font-size: 28px; color: #faad14; font-weight: bold;">{{ stats.taskPending || 0 }}</div>
                        <div style="color: #666; margin-top: 8px;">待巡检任务</div>
                    </div>
                </el-card>
            </el-col>
            <el-col :span="6">
                <el-card shadow="hover">
                    <div style="text-align: center;">
                        <div style="font-size: 28px; color: #f5222d; font-weight: bold;">{{ stats.orderPending || 0 }}</div>
                        <div style="color: #666; margin-top: 8px;">待处理工单</div>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <el-card style="margin-top: 20px;">
            <h3>系统说明</h3>
            <p>本系统用于高校实验室设备的日常巡检与异常工单管理，主要功能：</p>
            <ul style="line-height: 2; color: #555;">
                <li>设备管理：登记设备信息、维护设备状态（正常/异常/维修中/报废）</li>
                <li>巡检任务：为巡检员派发任务，巡检员现场打卡并填写结果</li>
                <li>异常工单：巡检发现异常自动生成工单，处理人接单维修后关闭工单</li>
                <li>用户管理：支持系统管理员、实验室管理员、巡检员三种角色</li>
            </ul>
        </el-card>
    </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import request from '../utils/request'

const stats = reactive({})

onMounted(async () => {
    const res = await request.get('/api/dashboard/stats')
    Object.assign(stats, res.data)
})
</script>
