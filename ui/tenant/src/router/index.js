import Vue from 'vue'
import Router from 'vue-router'
import {Iframe, MemberLogin, MemberLogout, NotFound, TenantLayout} from 'common'

import Member from '../views/Member'
import Org from '../views/Org'
import Role from '../views/Role'
import Menu from '../views/Menu'
import TenantConfig from '../views/TenantConfig'

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
      path: '/tenant',
      component: TenantLayout,
      children: [
        {path: '/tenant/org', name: 'org', component: Org, meta: {title: '机构管理'}},
        {path: '/tenant/member', name: 'member', component: Member, meta: {title: '成员管理'}},
        {path: '/tenant/role', name: 'role', component: Role, meta: {title: '角色管理'}},
        {path: '/tenant/menu', name: 'menu', component: Menu, meta: {title: '菜单管理'}},
        {path: '/tenant/config', name: 'tenantConfig', component: TenantConfig, meta: {title: '配置管理'}}
      ]
    },
    {
      path: '/iframe',
      component: TenantLayout,
      children: [
        {path: '/iframe/*', name: 'iframe', component: Iframe, meta: {title: 'iframe'}}
      ]
    },
    {path: '/tenant/login', name: 'login', component: MemberLogin, meta: {title: '登录'}},
    {path: '/tenant/logout', name: 'logout', component: MemberLogout, meta: {title: '成员登出'}},
    {path: '/404', name: '404', component: NotFound, meta: {title: '404'}},
    {path: '*', name: 'redirect', component: NotFound, meta: {title: ''}}
  ]
})
