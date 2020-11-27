import Vue from 'vue'
import Router from 'vue-router'
import {NotFound, UserLayout, Login, Logout} from 'common'
import MyTenant from '../views/MyTenant'
import MyTenantApply from '../views/MyTenantApply'
import MyInfo from '../views/MyInfo'
import ChatNews from '../views/ChatNews'
import Mail from '../views/Mail'
// hack router push callback
const originalPush = Router.prototype.push
Router.prototype.push = function push (location, onResolve, onReject) {
  if (onResolve || onReject) return originalPush.call(this, location, onResolve, onReject)
  return originalPush.call(this, location).catch(err => err)
}

Vue.use(Router)

export default new Router({
  mode: 'history',
  // 页面跳转后，页面是否滚动
  scrollBehavior: () => ({y: 0}),
  routes: [
    {
      path: '/me',
      component: UserLayout,
      children: [
        {path: '/me/news', name: 'news', component: ChatNews, meta: {title: '最新消息'}},
        {path: '/me/mail', name: 'mail', component: Mail, meta: {title: '通讯录'}},
        {path: '/me/info', name: 'info', component: MyInfo, meta: {title: '我的信息'}},
        {path: '/me/tenant', name: 'tenant', component: MyTenant, meta: {title: '我的企业'}},
        {path: '/me/tenantApply', name: 'tenantApply', component: MyTenantApply, meta: {title: '我的企业申请'}}
      ]
    },
    {path: '/me/login', name: 'login', component: Login, meta: {title: '用户登录'}},
    {path: '/me/logout', name: 'logout', component: Logout, meta: {title: '用户登出'}},
    {path: '/404', name: '404', component: NotFound, meta: {title: '404'}},
    {path: '*', name: 'redirect', component: NotFound, meta: {title: ''}}
  ]
})
