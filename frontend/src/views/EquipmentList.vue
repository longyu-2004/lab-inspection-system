<template>
    <el-card>
        <div style="margin-bottom: 16px; display: flex; gap: 10px;">
            <el-input v-model="query.name" placeholder="设备名称" style="width: 200px;" clearable />
            <el-select v-model="query.status" placeholder="状态" style="width: 150px;" clearable>
                <el-option label="正常" value="NORMAL" />
                <el-option label="异常" value="ABNORMAL" />
                <el-option label="维修中" value="REPAIRING" />
                <el-option label="报废" value="SCRAPPED" />
            </el-select>
            <el-button type="primary" @click="loadData">查询</el-button>
            <el-button type="success" @click="openAdd">新增设备</el-button>
        </div>

        <el-table :data="list" border stripe>
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="name" label="设备名称" />
            <el-table-column prop="model" label="型号" />
            <el-table-column prop="assetNo" label="资产编号" />
            <el-table-column prop="labId" label="所属实验室" width="120" />
            <el-table-column prop="installDate" label="安装日期" width="120" />
            <el-table-column label="状态" width="100">
                <template #default="{ row }">
                    <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
                <template #default="{ row }">
                    <el-button size="small" @click="openEdit(row)">编辑</el-button>
                    <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
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

        <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
            <el-form :model="form" label-width="80px">
                <el-form-item label="设备名称"><el-input v-model="form.name" /></el-form-item>
                <el-form-item label="型号"><el-input v-model="form.model" /></el-form-item>
                <el-form-item label="资产编号"><el-input v-model="form.assetNo" /></el-form-item>
                <el-form-item label="所属实验室">
                    <el-select v-model="form.labId">
                        <el-option v-for="l in labs" :key="l.id" :label="l.name" :value="l.id" />
                    </el-select>
                </el-form-item>
                <el-form-item label="状态">
                    <el-select v-model="form.status">
                        <el-option label="正常" value="NORMAL" />
                        <el-option label="异常" value="ABNORMAL" />
                        <el-option label="维修中" value="REPAIRING" />
                        <el-option label="报废" value="SCRAPPED" />
                    </el-select>
                </el-form-item>
                <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" /></el-form-item>
            </el-form>
            <template #footer>
                <el-button @click="dialogVisible = false">取消</el-button>
                <el-button type="primary" @click="handleSave">确定</el-button>
            </template>
        </el-dialog>
    </el-card>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../utils/request'

const list = ref([])
const total = ref(0)
const labs = ref([])
const query = reactive({ current: 1, size: 10, name: '', status: '' })

const dialogVisible = ref(false)
const dialogTitle = ref('')
const form = reactive({ id: null, name: '', model: '', assetNo: '', labId: null, status: 'NORMAL', remark: '' })

const statusText = s => ({ NORMAL: '正常', ABNORMAL: '异常', REPAIRING: '维修中', SCRAPPED: '报废' }[s] || s)
const statusType = s => ({ NORMAL: 'success', ABNORMAL: 'danger', REPAIRING: 'warning', SCRAPPED: 'info' }[s] || '')

const loadData = async () => {
    const res = await request.get('/api/equipment/page', { params: query })
    list.value = res.data.records
    total.value = res.data.total
}

const openAdd = () => {
    dialogTitle.value = '新增设备'
    Object.assign(form, { id: null, name: '', model: '', assetNo: '', labId: null, status: 'NORMAL', remark: '' })
    dialogVisible.value = true
}

const openEdit = (row) => {
    dialogTitle.value = '编辑设备'
    Object.assign(form, row)
    dialogVisible.value = true
}

const handleSave = async () => {
    if (form.id) {
        await request.put('/api/equipment', form)
        ElMessage.success('修改成功')
    } else {
        await request.post('/api/equipment', form)
        ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
}

const handleDelete = async (row) => {
    await ElMessageBox.confirm('确认删除该设备？', '提示', { type: 'warning' })
    await request.delete(`/api/equipment/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
}

onMounted(async () => {
    loadData()
    const res = await request.get('/api/user/labs')
    labs.value = res.data
})
</script>
