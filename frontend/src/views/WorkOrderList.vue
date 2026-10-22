<template>
    <el-card>
        <div style="margin-bottom: 16px;">
            <el-select v-model="query.status" placeholder="工单状态" style="width: 180px; margin-right: 10px;" clearable @change="loadData">
                <el-option label="待处理" value="PENDING" />
                <el-option label="处理中" value="PROCESSING" />
                <el-option label="已完成" value="DONE" />
            </el-select>
            <el-button type="success" @click="openAdd">上报工单</el-button>
        </div>

        <el-table :data="list" border stripe>
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="title" label="标题" show-overflow-tooltip />
            <el-table-column prop="description" label="问题描述" show-overflow-tooltip />
            <el-table-column label="状态" width="100">
                <template #default="{ row }">
                    <el-tag :type="orderType(row.status)">{{ orderText(row.status) }}</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="reporterId" label="上报人ID" width="90" />
            <el-table-column prop="handleUserId" label="处理人ID" width="90" />
            <el-table-column prop="createTime" label="创建时间" width="160" />
            <el-table-column label="操作" width="220">
                <template #default="{ row }">
                    <el-button v-if="row.status === 'PENDING'" size="small" type="primary" @click="handleProcess(row)">接单处理</el-button>
                    <el-button v-if="row.status === 'PROCESSING'" size="small" type="success" @click="openFinish(row)">完成处理</el-button>
                </template>
            </el-table-column>
        </el-table>

        <el-pagination
            style="margin-top: 16px; justify-content: flex-end;"
            v-model:current-page="query.current"
            v-model:page-size="query.size"
            :total="total"
            layout="total, prev, pager, next"
            @current-change="loadData"
        />

        <el-dialog v-model="addVisible" title="上报工单" width="500px">
            <el-form :model="addForm" label-width="80px">
                <el-form-item label="设备">
                    <el-select v-model="addForm.equipmentId">
                        <el-option v-for="e in equipments" :key="e.id" :label="e.name + '(' + e.assetNo + ')'" :value="e.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="标题"><el-input v-model="addForm.title" /></el-form-item>
                <el-form-item label="问题描述"><el-input v-model="addForm.description" type="textarea" /></el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="addVisible = false">取消</el-button>
                <el-button type="primary" @click="handleAdd">提交</el-button>
            </template>
        </el-dialog>

        <el-dialog v-model="finishVisible" title="完成处理" width="500px">
            <el-form :model="finishForm" label-width="80px">
                <el-form-item label="处理备注"><el-input v-model="finishForm.handleRemark" type="textarea" placeholder="维修过程、更换部件等" /></el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="finishVisible = false">取消</el-button>
                <el-button type="primary" @click="handleFinish">确认完成</el-button>
            </template>
        </el-dialog>
    </el-card>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '../utils/request'

const list = ref([])
const total = ref(0)
const equipments = ref([])
const query = reactive({ current: 1, size: 10, status: '' })

const addVisible = ref(false)
const addForm = reactive({ equipmentId: null, title: '', description: '' })

const finishVisible = ref(false)
const finishForm = reactive({ id: null, handleRemark: '' })

const orderText = s => ({ PENDING: '待处理', PROCESSING: '处理中', DONE: '已完成' }[s] || s)
const orderType = s => ({ PENDING: 'danger', PROCESSING: 'warning', DONE: 'success' }[s] || '')

const loadData = async () => {
    const res = await request.get('/api/order/page', { params: query })
    list.value = res.data.records
    total.value = res.data.total
}

const openAdd = () => {
    addForm.equipmentId = null
    addForm.title = ''
    addForm.description = ''
    addVisible.value = true
}

const handleAdd = async () => {
    await request.post('/api/order', addForm)
    ElMessage.success('工单已上报')
    addVisible.value = false
    loadData()
}

const handleProcess = async (row) => {
    await request.post(`/api/order/${row.id}/process`)
    ElMessage.success('已接单')
    loadData()
}

const openFinish = (row) => {
    finishForm.id = row.id
    finishForm.handleRemark = ''
    finishVisible.value = true
}

const handleFinish = async () => {
    await request.post(`/api/order/${finishForm.id}/finish`, { handleRemark: finishForm.handleRemark })
    ElMessage.success('工单已关闭')
    finishVisible.value = false
    loadData()
}

onMounted(async () => {
    loadData()
    // 拉取设备列表供上报工单下拉选
    const res = await request.get('/api/equipment/page', { params: { current: 1, size: 100 } })
    equipments.value = res.data.records
})
</script>
