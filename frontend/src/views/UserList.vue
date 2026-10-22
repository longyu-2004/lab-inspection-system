<template>
    <el-card>
        <div style="margin-bottom: 16px;">
            <el-input v-model="query.name" placeholder="姓名" style="width: 200px; margin-right: 10px;" clearable />
            <el-select v-model="query.role" placeholder="角色" style="width: 160px; margin-right: 10px;" clearable>
                <el-option label="系统管理员" value="ADMIN" />
                <el-option label="实验室管理员" value="MANAGER" />
                <el-option label="巡检员" value="INSPECTOR" />
            </el-select>
            <el-button type="primary" @click="loadData">查询</el-button>
            <el-button type="success" @click="openAdd">新增用户</el-button>
        </div>

        <el-table :data="list" border stripe>
            <el-table-column prop="id" label="ID" width="60" />
            <el-table-column prop="username" label="账号" />
            <el-table-column prop="name" label="姓名" />
            <el-table-column label="角色" width="120">
                <template #default="{ row }">
                    <el-tag>{{ { ADMIN: '管理员', MANAGER: '实验室管理员', INSPECTOR: '巡检员' }[row.role] }}</el-tag>
                </template>
            </el-table-column>
            <el-table-column prop="phone" label="手机号" />
            <el-table-column prop="labId" label="所属实验室ID" width="120" />
            <el-table-column label="状态" width="80">
                <template #default="{ row }">
                    <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                        {{ row.status === 1 ? '启用' : '禁用' }}
                    </el-tag>
                </template>
            </el-table-column>
            <el-table-column label="操作" width="150">
                <template #default="{ row }">
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

        <el-dialog v-model="dialogVisible" title="新增用户" width="500px">
            <el-form :model="form" label-width="80px">
                <el-form-item label="账号"><el-input v-model="form.username" /></el-form-item>
                <el-form-item label="密码"><el-input v-model="form.password" type="password" /></el-form-item>
                <el-form-item label="姓名"><el-input v-model="form.name" /></el-form-item>
                <el-form-item label="角色">
                    <el-select v-model="form.role">
                        <el-option label="实验室管理员" value="MANAGER" />
                        <el-option label="巡检员" value="INSPECTOR" />
                    </el-select>
                </el-form-item>
                <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
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
const query = reactive({ current: 1, size: 10, name: '', role: '' })

const dialogVisible = ref(false)
const form = reactive({ username: '', password: '', name: '', role: 'INSPECTOR', phone: '' })

const loadData = async () => {
    const res = await request.get('/api/user/page', { params: query })
    list.value = res.data.records
    total.value = res.data.total
}

const openAdd = () => {
    Object.assign(form, { username: '', password: '', name: '', role: 'INSPECTOR', phone: '' })
    dialogVisible.value = true
}

const handleSave = async () => {
    await request.post('/api/user', form)
    ElMessage.success('新增成功')
    dialogVisible.value = false
    loadData()
}

const handleDelete = async (row) => {
    await ElMessageBox.confirm(`确认删除用户 ${row.name}？`, '提示', { type: 'warning' })
    await request.delete(`/api/user/${row.id}`)
    ElMessage.success('删除成功')
    loadData()
}

onMounted(loadData)
</script>
