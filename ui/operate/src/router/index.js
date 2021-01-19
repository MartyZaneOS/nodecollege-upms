import Vue from 'vue'
import Router from 'vue-router'
import {AdminLayout, AdminLogin, AdminLogout, NotFound} from 'common'

import AdminOrg from '../views/AdminOrg'
import AdminRole from '../views/AdminRole'
import Admin from '../views/Admin'

import UserOrg from '../views/UserOrg'
import User from '../views/User'
import UserRole from '../views/UserRole'

import Tenant from '../views/Tenant'
import TenantApply from '../views/TenantApply'
import TenantRole from '../views/TenantRole'

import Ui from '../views/Ui'
import Product from '../views/Product'
import AppApiAccessAuth from '../views/AppApiAccessAuth'
import GatewayRoute from '../views/GatewayRoute'
import SysLog from '../views/SysLog'
import SyncJob from '../views/SyncJob'
import SyncJobLog from '../views/SyncJobLog'
import Config from '../views/Config'
import SysConfig from '../views/SysConfig'
import PreConfig from '../views/PreConfig'
import File from '../views/File'
import DataPower from '../views/DataPower/index'
import Visit from '../views/Visit'

// hack router push callback
const originalPush = Router.prototype.push
Router.prototype.push = function push (location, onResolve, onReject) {
  if (onResolve || onReject) return originalPush.call(this, location, onResolve, onReject)
  return originalPush.call(this, location).catch(err => err)
}

Vue.use(Router)
const routes = [
  {
    path: '/admin',
    component: AdminLayout,
    children: [
      {path: '/admin/org', name: 'adminOrg', component: AdminOrg, meta: {title: '管理员机构信息'}},
      {path: '/admin/role', name: 'adminRole', component: AdminRole, meta: {title: '管理员角色信息'}},
      {path: '/admin/admin', name: 'admin', component: Admin, meta: {title: '管理员管理'}}
    ]
  },
  {
    path: '/user',
    component: AdminLayout,
    children: [
      {path: '/user/org', name: 'userOrg', component: UserOrg, meta: {title: '用户机构信息'}},
      {path: '/user', name: 'user', component: User, meta: {title: '用户信息'}},
      {path: '/user/role', name: 'userRole', component: UserRole, meta: {title: '用户角色信息'}}
    ]
  },
  {
    path: '/tenantMsg',
    component: AdminLayout,
    children: [
      {path: '/tenantMsg', name: 'tenant', component: Tenant, meta: {title: '租户管理'}},
      {path: '/tenantMsg/tenantApply', name: 'tenantApply', component: TenantApply, meta: {title: '租户申请工单'}},
      {path: '/tenantMsg/sysRole', name: 'tenantRole', component: TenantRole, meta: {title: '租户预制角色'}}
    ]
  },
  {
    path: '/operate',
    component: AdminLayout,
    children: [
      {path: '/operate/ui', name: 'ui', component: Ui, meta: {title: '前端管理'}},
      {path: '/operate/product', name: 'product', component: Product, meta: {title: '产品管理'}},
      {path: '/operate/appApiAccessAuth', name: 'appApiAccessAuth', component: AppApiAccessAuth, meta: {title: '微服务接口管理'}},
      {path: '/operate/gatewayRoute', name: 'gatewayRoute', component: GatewayRoute, meta: {title: '网关路由管理'}},
      {path: '/operate/sysLog', name: 'sysLog', component: SysLog, meta: {title: '系统日志管理'}},
      {path: '/operate/syncJob', name: 'syncJob', component: SyncJob, meta: {title: '定时任务管理'}},
      {path: '/operate/syncJobLog', name: 'syncJobLog', component: SyncJobLog, meta: {title: '定时任务日志'}},
      {path: '/operate/config', name: 'config', component: Config, meta: {title: '配置信息查询'}},
      {path: '/operate/sysConfig', name: 'sysConfig', component: SysConfig, meta: {title: '系统配置管理'}},
      {path: '/operate/preConfig', name: 'preConfig', component: PreConfig, meta: {title: '预制配置管理'}},
      {path: '/operate/file', name: 'file', component: File, meta: {title: '文件管理'}},
      {path: '/operate/dataPower', name: 'dataPower', component: DataPower, meta: {title: '数据权限管理'}},
      {path: '/operate/visitCount', name: 'visitCount', component: Visit, meta: {title: '访问统计'}}
    ]
  },
  {path: '/admin/login', name: 'login', component: AdminLogin},
  {path: '/admin/logout', name: 'logout', component: AdminLogout},
  {path: '/404', name: '404', component: NotFound, meta: {title: '404'}},
  {path: '*', name: 'redirect', component: NotFound, meta: {title: ''}}
]

// todo 获取当前登录人授权前端页面列表 然后移除无权限页面
const uiCode = 'operate'
console.log('uiCode', uiCode)

export default new Router({
  mode: 'history',
  // 页面跳转后，页面是否滚动
  scrollBehavior: () => ({y: 0}),
  routes: routes
})
