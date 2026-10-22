import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    { path: '/login', component: () => import('../views/Login.vue') },
    {
        path: '/',
        component: () => import('../layout/index.vue'),
        redirect: '/dashboard',
        children: [
            { path: 'dashboard', name: '仪表盘', component: () => import('../views/Dashboard.vue') },
            { path: 'equipment', name: '设备管理', component: () => import('../views/EquipmentList.vue') },
            { path: 'inspection', name: '巡检任务', component: () => import('../views/InspectionList.vue') },
            { path: 'order', name: '异常工单', component: () => import('../views/WorkOrderList.vue') },
            { path: 'user', name: '用户管理', component: () => import('../views/UserList.vue') }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token')
    if (to.path !== '/login' && !token) {
        next('/login')
    } else {
        next()
    }
})

export default router
