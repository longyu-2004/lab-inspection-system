<template>
    <el-card>
        <div style="margin-bottom: 16px;">
            <el-select v-model="query.status" placeholder="任务状态" style="width: 180px; margin-right: 10px;" clearable @change="loadData">
                <el-option label="待巡检" value="PENDING" />
                <el-option label="已完成" value="DONE" />
                <el-option label="已逾期" value="OVERDUE" />
            </el-select>
        </div>

        <el-table :data="list" border stripe>
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="equipmentId" label="设备ID" width="80" />
            <el-table-column prop="inspectorId" label="巡检员ID" width="90" />
            <el-table-column prop="planDate" label="计划日期" width="120" />
            <el-table-column label="状态" width="100">
                <template #default="{ row }">
                    <el-tag :type="row.status === 'DONE' ? 'success' : row.status === 'PENDING' ? 'warning' : 'danger'">
                        {{ { PENDING: '待巡检', DONE: '已完成', OVERDUE: '已逾期' }[row.status] }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="巡检结果" width="100">
                <template #default="{ row }">
                    <el-tag v-if="row.result === 'NORMAL'" type="success">正常</el-tag>
                    <el-tag v-else-if="row.result === 'ABNORMAL'" type="danger">异常</el-tag>
                    <span v-else>-</span>
                </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" show-overflow-tooltip />
            <el-table-column prop="finishTime" label="完成时间" width="160" />
            <el-table-column label="操作" width="180">
                <template #default="{ row }">
                    <el-button v-if="row.status === 'PENDING'" size="small" type="primary" @click="openExecute(row)">
                        执行巡检
                    </el-button>
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

        <el-dialog v-model="execVisible" title="执行巡检" width="500px">
            <el-form :model="execForm" label-width="80px">
                <el-form-item label="巡检结果">
                    <el-radio-group v-model="execForm.result">
                        <el-radio value="NORMAL">正常</el-radio>
                        <el-radio value="ABNORMAL">异常（自动生成工单）</el-radio>
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="备注">
                    <el-input v-model="execForm.remark" type="textarea" placeholder="设备外观、运行状态、异常现象等" />
                </el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="execVisible = false">取消</el-button>
                <el-button type="primary" @click="handleExecute">提交</el-button>
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
const query = reactive({ current: 1, size: 10, status: '' })

const execVisible = ref(false)
const execForm = reactive({ id: null, result: 'NORMAL', remark: '' })

const loadData = async () => {
    const res = await request.get('/api/inspection/page', { params: query })
    list.value = res.data.records
    total.value = res.data.total
}

const openExecute = (row) => {
    execForm.id = row.id
    execForm.result = 'NORMAL'
    execForm.remark = ''
    execVisible.value = true
}

const handleExecute = async () => {
    await request.post(`/api/inspection/${execForm.id}/execute`, execForm)
    ElMessage.success('巡检已提交')
    execVisible.value = false
    loadData()
}

onMounted(loadData)
</script>
