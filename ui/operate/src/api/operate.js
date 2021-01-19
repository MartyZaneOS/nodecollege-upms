import http from '../utils/request'

// 查询微服务接口列表
export function getAppApiList (data) {
  return http.post('operateApi', '/appApi/getAppApiList', data)
}
// 查询微服务接口树
export function getAppApiTree (data) {
  return http.post('operateApi', '/appApi/getAppApiTree', data)
}
// 更新接口授权信息
export function updateAppApiAccessAuth (data) {
  return http.post('operateApi', '/appApi/updateAppApiAccessAuth', data)
}
// 同步刷新接口授权信息
export function syncAllAppApiAccessAuth (data) {
  return http.post('operateApi', '/appApi/syncAllAppApiAccessAuth', data)
}
// 查询微服务接口列表
export function delAppApi (data) {
  return http.post('operateApi', '/appApi/delAppApi', data)
}

// 查询前端工程
export function getUiList (data) {
  return http.post('operateApi', '/ui/getUiList', data)
}
// 添加前端工程
export function addUi (data) {
  return http.post('operateApi', '/ui/addUi', data)
}
// 删除前端功能
export function delUi (data) {
  return http.post('operateApi', '/ui/delUi', data)
}
// 删除前端工程
export function editUi (data) {
  return http.post('operateApi', '/ui/editUi', data)
}
// 查询前端页面列表
export function getUiPageList (data) {
  return http.post('operateApi', '/ui/getUiPageList', data)
}
// 查询前端页面树
export function getUiPageTree (data) {
  return http.post('operateApi', '/ui/getUiPageTree', data)
}
// 添加前端页面
export function addUiPage (data) {
  return http.post('operateApi', '/ui/addUiPage', data)
}
// 删除前端页面
export function delUiPage (data) {
  return http.post('operateApi', '/ui/delUiPage', data)
}
// 修改前端页面
export function editUiPage (data) {
  return http.post('operateApi', '/ui/editUiPage', data)
}
// 查询前端页面按钮列表
export function getButtonTree (data) {
  return http.post('operateApi', '/ui/getButtonTree', data)
}
// 添加前端页面按钮
export function addUiPageButton (data) {
  return http.post('operateApi', '/ui/addUiPageButton', data)
}
// 删除前端页面按钮
export function delUiPageButton (data) {
  return http.post('operateApi', '/ui/delUiPageButton', data)
}
// 编辑前端页面按钮
export function editUiPageButton (data) {
  return http.post('operateApi', '/ui/editUiPageButton', data)
}

// 查询产品列表
export function getProductList (data) {
  return http.post('operateApi', '/product/getProductList', data)
}
// 添加产品
export function addProduct (data) {
  return http.post('operateApi', '/product/addProduct', data)
}
// 清除产品菜单树缓存
export function clearProductMenuTreeCache (data) {
  return http.post('operateApi', '/product/clearProductMenuTreeCache', data)
}
// 编辑产品
export function editProduct (data) {
  return http.post('operateApi', '/product/editProduct', data)
}
// 删除产品
export function delProduct (data) {
  return http.post('operateApi', '/product/delProduct', data)
}

// 查询产品菜单树
export function getProductMenuTree (data) {
  return http.post('operateApi', '/product/getProductMenuTree', data)
}
// 添加产品菜单
export function addProductMenu (data) {
  return http.post('operateApi', '/product/addProductMenu', data)
}
// 修改产品菜单
export function editProductMenu (data) {
  return http.post('operateApi', '/product/editProductMenu', data)
}
// 删除产品菜单
export function delProductMenu (data) {
  return http.post('operateApi', '/product/delProductMenu', data)
}
// 绑定产品菜单
export function bindProductMenu (data) {
  return http.post('operateApi', '/product/bindProductMenu', data)
}

// 查询网关路由列表
export function getRouteList (data) {
  return http.post('operateApi', '/route/getRouteList', data)
}
// 添加网关路由
export function addRoute (data) {
  return http.post('operateApi', '/route/addRoute', data)
}
// 编辑网关路由
export function updateRoute (data) {
  return http.post('operateApi', '/route/updateRoute', data)
}
// 删除网关路由
export function delRoute (data) {
  return http.post('operateApi', '/route/delRoute', data)
}

// 查询系统日志列表
export function getSysLogList (data) {
  return http.post('logApi', '/log/getSysLogList', data)
}

// 查询定时任务列表
export function getJobList (data) {
  return http.post('syncApi', '/sync/getJobList', data)
}
// 获取任务class
export function getJobClassList (data) {
  return http.post('syncApi', '/sync/getJobClassList', data)
}
// 添加定时任务
export function addJob (data) {
  return http.post('syncApi', '/sync/addJob', data)
}
// 编辑定时任务
export function editJob (data) {
  return http.post('syncApi', '/sync/editJob', data)
}
// 删除定时任务
export function delJob (data) {
  return http.post('syncApi', '/sync/delJob', data)
}
// 暂停/恢复任务
export function pauseJob (data) {
  return http.post('syncApi', '/sync/pauseJob', data)
}
// 获取任务日志列表
export function getJobLogList (data) {
  return http.post('syncApi', '/sync/getJobLogList', data)
}

// 查询配置列表
export function getConfigList (data) {
  return http.post('operateApi', '/config/getConfigList', data)
}
// 添加配置列表
export function addConfig (data) {
  return http.post('operateApi', '/config/addConfig', data)
}
// 编辑配置列表
export function editConfig (data) {
  return http.post('operateApi', '/config/editConfig', data)
}
// 删除配置列表
export function delConfig (data) {
  return http.post('operateApi', '/config/delConfig', data)
}

// 查询组织机构树
export function getOrgTree (data) {
  return http.post('operateApi', '/admin/org/getOrgTree', data)
}
// 添加组织机构
export function addOrg (data) {
  return http.post('operateApi', '/admin/org/addOrg', data)
}
// 编辑组织机构
export function editOrg (data) {
  return http.post('operateApi', '/admin/org/editOrg', data)
}
// 删除组织机构
export function delOrg (data) {
  return http.post('operateApi', '/admin/org/delOrg', data)
}
// 查询角色列表根据组织信息
export function getRoleListByOrg (data) {
  return http.post('operateApi', '/admin/role/getRoleListByOrg', data)
}
// 查询管理员列表根据角色机构
export function getAdminListByRoleOrg (data) {
  return http.post('operateApi', '/admin/getAdminListByRoleOrg', data)
}
// 绑定管理员机构角色
export function addAdminOrgRole (data) {
  return http.post('operateApi', '/admin/addAdminOrgRole', data)
}
// 解绑管理员机构角色
export function delAdminOrgRole (data) {
  return http.post('operateApi', '/admin/delAdminOrgRole', data)
}

// 查询管理员列表
export function getAdminList (data) {
  return http.post('operateApi', '/admin/getAdminList', data)
}
// 添加管理员
export function addAdmin (data) {
  return http.post('operateApi', '/admin/addAdmin', data)
}
// 编辑管理员
export function updateAdmin (data) {
  return http.post('operateApi', '/admin/updateAdmin', data)
}
// 删除管理员
export function delAdmin (data) {
  return http.post('operateApi', '/admin/delAdmin', data)
}
// 锁定、解锁管理员
export function lockAdmin (data) {
  return http.post('operateApi', '/admin/lockAdmin', data)
}
// 重置管理员密码
export function resetPwd (data) {
  return http.post('operateApi', '/admin/resetPwd', data)
}
// 查询机构列表根据角色信息
export function getOrgTreeByAdmin (data) {
  return http.post('operateApi', '/admin/org/getOrgTreeByAdmin', data)
}
// 查询机构列表根据角色信息
export function getAdminListByOrg (data) {
  return http.post('operateApi', '/admin/getAdminListByOrg', data)
}
// 绑定管理员机构
export function bindAdminOrg (data) {
  return http.post('operateApi', '/admin/bindAdminOrg', data)
}
// 查询角色列表根据管理员信息
export function getRoleListByAdmin (data) {
  return http.post('operateApi', '/admin/role/getRoleListByAdmin', data)
}
// 查询管理员授权信息
export function getAdminPower (data) {
  return http.post('operateApi', '/admin/getAdminPower', data)
}

// 查询角色列表
export function getRoleList (data) {
  return http.post('operateApi', '/admin/role/getRoleList', data)
}
// 添加角色
export function addRole (data) {
  return http.post('operateApi', '/admin/role/addRole', data)
}
// 编辑角色
export function editRole (data) {
  return http.post('operateApi', '/admin/role/editRole', data)
}
// 删除角色
export function delRole (data) {
  return http.post('operateApi', '/admin/role/delRole', data)
}
// 查询角色菜单
export function getRoleMenuList (data) {
  return http.post('operateApi', '/admin/role/getRoleMenuList', data)
}
// 绑定角色菜单
export function bindRoleMenu (data) {
  return http.post('operateApi', '/admin/role/bindRoleMenu', data)
}
// 查询机构列表根据角色信息
export function getOrgTreeByRole (data) {
  return http.post('operateApi', '/admin/org/getOrgTreeByRole', data)
}
// 绑定角色组织机构
export function bindRoleOrg (data) {
  return http.post('operateApi', '/admin/role/bindRoleOrg', data)
}

// 查询用户组织机构树
export function getUserOrgTree (data) {
  return http.post('operateApi', '/user/org/getOrgTree', data)
}
// 添加用户组织机构
export function addUserOrg (data) {
  return http.post('operateApi', '/user/org/addOrg', data)
}
// 编辑用户组织机构
export function editUserOrg (data) {
  return http.post('operateApi', '/user/org/editOrg', data)
}
// 删除用户组织机构
export function delUserOrg (data) {
  return http.post('operateApi', '/user/org/delOrg', data)
}
// 根据机构信息查询用户信息
export function getUserRoleListByOrg (data) {
  return http.post('operateApi', '/user/role/getUserRoleListByOrg', data)
}

// 查询用户列表
export function getUserList (data) {
  return http.post('operateApi', '/user/getUserList', data)
}
// 编辑用户
export function editUser (data) {
  return http.post('operateApi', '/user/editUser', data)
}
// 删除用户
export function delUser (data) {
  return http.post('operateApi', '/user/delUser', data)
}
// 重置用户密码
export function resetUserPwd (data) {
  return http.post('operateApi', '/user/resetUserPwd', data)
}
// 根据用户信息查询组织机构列表
export function getOrgTreeByUser (data) {
  return http.post('operateApi', '/user/org/getOrgTreeByUser', data)
}
// 绑定用户机构
export function bindUserOrg (data) {
  return http.post('operateApi', '/user/bindUserOrg', data)
}
// 查询角色列表根据用户信息
export function getRoleListByUser (data) {
  return http.post('operateApi', '/user/role/getRoleListByUser', data)
}
// 查询管理员授权信息
export function getUserPower (data) {
  return http.post('operateApi', '/user/getUserPower', data)
}

// 查询角色列表
export function getUserRoleList (data) {
  return http.post('operateApi', '/user/role/getUserRoleList', data)
}
// 添加角色
export function addUserRole (data) {
  return http.post('operateApi', '/user/role/addUserRole', data)
}
// 编辑角色
export function editUserRole (data) {
  return http.post('operateApi', '/user/role/editUserRole', data)
}
// 删除角色
export function delUserRole (data) {
  return http.post('operateApi', '/user/role/delUserRole', data)
}
// 查询角色菜单
export function getUserRoleMenuList (data) {
  return http.post('operateApi', '/user/role/getUserRoleMenuList', data)
}
// 绑定角色菜单
export function bindUserRoleMenu (data) {
  return http.post('operateApi', '/user/role/bindUserRoleMenu', data)
}
// 查询机构列表根据角色信息
export function getUserOrgTreeByRole (data) {
  return http.post('operateApi', '/user/org/getOrgTreeByRole', data)
}
// 绑定角色组织机构
export function bindUserRoleOrg (data) {
  return http.post('operateApi', '/user/role/bindUserRoleOrg', data)
}
// 根据机构获取用户列表
export function getUserListByOrg (data) {
  return http.post('operateApi', '/user/getUserListByOrg', data)
}
// 查询用户列表根据机构角色
export function getUserListByRoleOrg (data) {
  return http.post('operateApi', '/user/getUserListByRoleOrg', data)
}
// 添加用户机构角色
export function addUserOrgRole (data) {
  return http.post('operateApi', '/user/addUserOrgRole', data)
}
// 删除用户机构角色
export function delUserOrgRole (data) {
  return http.post('operateApi', '/user/delUserOrgRole', data)
}

// 查询文件列表
export function getFileList (data) {
  return http.post('operateApi', '/getFileList', data)
}

// 查询用户租户申请列表
export function getTenantApplyList (data) {
  return http.post('operateApi', '/tenantApply/getTenantApplyList', data)
}
// 添加租户申请
export function editTenantApply (data) {
  return http.post('operateApi', '/tenantApply/editTenantApply', data)
}

// 查询租户列表
export function getTenantList (data) {
  return http.post('operateApi', '/tenant/getTenantList', data)
}
// 编辑租户信息
export function editTenant (data) {
  return http.post('operateApi', '/tenant/editTenant', data)
}
// 获取租户产品
export function getTenantProduct (data) {
  return http.post('operateApi', '/tenant/getTenantProduct', data)
}
// 绑定租户产品
export function bindTenantProduct (data) {
  return http.post('operateApi', '/tenant/bindTenantProduct', data)
}

// 查询角色列表
export function getTenantRoleList (data) {
  return http.post('operateApi', '/tenant/role/getTenantRoleList', data)
}
// 添加角色
export function addTenantRole (data) {
  return http.post('operateApi', '/tenant/role/addTenantRole', data)
}
// 编辑角色
export function editTenantRole (data) {
  return http.post('operateApi', '/tenant/role/editTenantRole', data)
}
// 删除角色
export function delTenantRole (data) {
  return http.post('operateApi', '/tenant/role/delTenantRole', data)
}
// 查询角色菜单
export function getTenantRoleMenuList (data) {
  return http.post('operateApi', '/tenant/role/getTenantRoleMenuList', data)
}
// 绑定角色菜单
export function bindTenantRoleMenu (data) {
  return http.post('operateApi', '/tenant/role/bindTenantRoleMenu', data)
}

// 查询当天访问情况
export function getCurrentVisitData (data) {
  return http.post('logApi', '/sysVisit/getCurrentVisitData', data)
}

// 查询历史访问情况
export function getHistoryVisitData (data) {
  return http.post('logApi', '/sysVisit/getHistoryVisitData', data)
}

// 获取数据权限列表
export function getDataPowerList (data) {
  return http.post('operateApi', '/dataPower/getList', data)
}
// 添加数据权限
export function addDataPower (data) {
  return http.post('operateApi', '/dataPower/addDataPower', data)
}
// 编辑数据权限
export function editDataPower (data) {
  return http.post('operateApi', '/dataPower/editDataPower', data)
}
// 查询数据授权
export function getAuthList (data) {
  return http.post('operateApi', '/dataPower/getAuthList', data)
}
// 添加数据授权
export function addAuth (data) {
  return http.post('operateApi', '/dataPower/addAuth', data)
}
// 编辑数据授权
export function editAuth (data) {
  return http.post('operateApi', '/dataPower/editAuth', data)
}
// 删除数据授权
export function delAuth (data) {
  return http.post('operateApi', '/dataPower/delAuth', data)
}
