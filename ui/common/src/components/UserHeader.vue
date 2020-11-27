<template>
  <div class="upms-head">
    <a-row>
      <a-col :xs="{span:4}" :sm="{span:4}" :md="{span:4}" :lg="{span:4}" :xxl="{span:4}" class="header-left">
        <div class="upms-head-txt" @click="$router.push({path: '/'})">
          {{navTitle}}
        </div>
      </a-col>
      <a-col :xs="{span:20}" :sm="{span:20}" :md="{span:20}" :lg="{span:20}" :xxl="{span:20}">
        <a-menu mode="horizontal" :selectable="false" :selectedKeys="topCurrent">
          <a-menu-item v-for="menu in topMenuList" :key="menu.menuCode" style="float: left;" @click="handleClick(menu.menuCode)">
            <a-icon v-if="menu.menuIcon" :type="menu.menuIcon"/>
            {{menu.menuName}}
          </a-menu-item>
          <a-menu-item v-if="!userInfo || !userInfo.account" style="float: right">
            <div @click="$router.push({path: '/me/login'})">登陆/注册</div>
          </a-menu-item>
          <a-sub-menu style="float: right" v-if="userInfo && userInfo.account">
            <span slot="title" class="submenu-title-wrapper">
              <a-avatar :src="avatar" v-if="avatar"/> {{userInfo.nickname ? userInfo.nickname : userInfo.account}} <a-icon type="down"/>
            </span>
            <a-menu-item key="updatePwd" @click="updateModal.visible = true">修改密码</a-menu-item>
            <a-menu-item key="jumpMe" v-if="userInfo && userInfo.accessToken" @click="jumpMe">个人中心</a-menu-item>
            <a-menu-item key="updateDefaultOption"><div @click="showPower">修改默认设置</div></a-menu-item>
            <a-menu-item key="logout"><div @click="$router.push({path: '/me/logout'})">退出登陆</div></a-menu-item>
          </a-sub-menu>
          <a-sub-menu style="float: right" v-if="topTenantList && topTenantList.length > 0">
            <span slot="title" class="submenu-title-wrapper">企业 <a-icon type="down"/></span>
            <a-menu-item v-for="tenant in topTenantList" :key="tenant.tenantId" v-if="tenant.tenantId"
                         @click="jumpClick(tenant)">
              {{tenant.tenantName}}
            </a-menu-item>
          </a-sub-menu>
          <a-sub-menu style="float: right" v-if="userInfo && userInfo.loginId && !userInfo.showAllRole" key="showAllRole">
            <span slot="title" class="submenu-title-wrapper">角色 {{userInfo.defaultRoleName}} <a-icon type="down"/></span>
            <a-menu-item v-for="(role, index) in userInfo.roleList" :key="index" @click="roleChange(role.code)">
              {{role.name}}
            </a-menu-item>
          </a-sub-menu>
          <a-sub-menu style="float: right" v-if="userInfo && userInfo.loginId && !userInfo.showAllOrg" key="showAllOrg">
            <span slot="title" class="submenu-title-wrapper">机构 {{userInfo.defaultOrgName}} <a-icon type="down"/></span>
            <a-menu-item v-for="(org, index) in userInfo.orgList" :key="index" @click="orgChange(org.code)">
              {{org.name}}
            </a-menu-item>
          </a-sub-menu>
        </a-menu>
      </a-col>
    </a-row>
    <a-modal v-model="updateModal.visible" title="修改密码">
      <a-form :form="updateModal.form">
        <a-form-item label="成员账号" :label-col="modal.labelCol" :wrapper-col="modal.wrapperCol">
          <a-input placeholder="请输入成员账号！"
                   v-decorator="['username', {rules: [{ required: true, message: '请输入成员账号！'}]}]"/>
        </a-form-item>
        <a-form-item label="旧密码" :label-col="modal.labelCol" :wrapper-col="modal.wrapperCol">
          <a-input placeholder="请输入旧密码！" type="password"
                   v-decorator="['password', {rules: [{ required: true, message: '请输入旧密码！'}]}]"/>
        </a-form-item>
        <a-form-item label="新密码" :label-col="modal.labelCol" :wrapper-col="modal.wrapperCol">
          <a-input placeholder="请输入新密码！" type="password"
                   v-decorator="['newPassword', {rules: [{ required: true, message: '请输入新密码！'}]}]"/>
        </a-form-item>
        <a-form-item label="确认密码" :label-col="modal.labelCol" :wrapper-col="modal.wrapperCol">
          <a-input placeholder="请输入确认密码！" type="password"
                   v-decorator="['confirmPassword', {rules: [{ required: true, message: '请输入确认密码！'}, {
              validator: compareToFirstPassword,
            }]}]"/>
        </a-form-item>
      </a-form>
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="modal.loading" @click="updatePwdOk">
          确定
        </a-button>
        <a-button key="back" @click="modal.visible = false">取消</a-button>
      </template>
    </a-modal>
    <a-modal v-model="powerModal.visible" title="修改默认设置">
      <a-row>
        <a-col span="24">
          <a-form  :label-col="powerModal.labelCol" :wrapper-col="powerModal.wrapperCol">
            <a-form-item label="显示所有角色菜单">
              <a-switch v-model="userInfo.showAllRole"></a-switch >
            </a-form-item>
            <a-form-item label="显示所有机构菜单">
              <a-switch v-model="userInfo.showAllOrg"></a-switch >
            </a-form-item>
            <a-form-item label="默认角色" v-if="userInfo && !userInfo.showAllRole">
              <a-select placeholder="请选择默认角色！" v-model="userInfo.defaultRoleCode" @change="roleChange" style="width: 200px" key="selectRole">
                <a-select-option v-for="role in userInfo.roleList" :value="role.code" :key="role.code">{{role.name}}</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="默认机构" v-if="userInfo && !userInfo.showAllOrg">
              <a-select placeholder="请选择默认角色！" v-model="userInfo.defaultOrgCode" @change="orgChange" style="width: 200px" key="selectOrg">
                <a-select-option v-for="org in userInfo.orgList" :value="org.code" :key="org.code">{{org.name}}</a-select-option>
              </a-select>
            </a-form-item>
          </a-form>
        </a-col>
      </a-row>
      <template slot="footer">
        <a-button type="primary" @click="getAdminPower">确定</a-button>
        <a-button key="back" @click="powerModal.visible = false">取消</a-button>
      </template>
    </a-modal>
    <a-modal v-model="selectOrgModal.visible" title="选择组织机构">
      <a-form layout="inline">
        <a-form-item label="组织机构">
          <a-select placeholder="请选择默认机构！" v-model="userInfo.defaultOrgCode" style="width: 200px">
            <a-select-option v-for="org in selectOrgModal.orgList" :value="org.code" :key="org.code">{{org.name}}</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
      <template slot="footer">
        <a-button key="submit" type="primary" @click="getAdminPower">
          确定
        </a-button>
      </template>
    </a-modal>
    <a-modal v-model="selectRoleModal.visible" title="选择角色">
      <a-form layout="inline">
        <a-form-item label="角色">
          <a-select placeholder="请选择默认角色！" v-model="userInfo.defaultRoleCode" style="width: 200px">
            <a-select-option v-for="role in selectRoleModal.roleList" :value="role.code" :key="role.code">{{role.name}}</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
      <template slot="footer">
        <a-button key="submit" type="primary" @click="getAdminPower">
          确定
        </a-button>
      </template>
    </a-modal>
  </div>
</template>

<script>
  import {MenuUtils} from './Utils.js'
  import {USER_INFO, MEMBER_INFO} from './Constants'
  import * as operateApi from '../api/operate'
  import * as tenantApi from '../api/tenant'

  export default {
    name: 'MeHeader',
    props: {
      menuList: {
        type: Array
      },
      current: {
        type: Array
      },
      title: {
        type: String
      }
    },
    data () {
      return {
        topCurrent: [],
        width: this.$store.state.screenWidth - 200,
        userInfo: {},
        avatar: '',
        navTitle: '',
        topMenuList: [],
        topTenantList: [],
        modal: {
          visible: false,
          style: 'border: 0px solid red',
          labelCol: {span: 10},
          wrapperCol: {span: 14},
          loading: false
        },
        updateModal: {
          form: this.$form.createForm(this),
          visible: false,
          loading: false
        },
        powerModal: {
          labelCol: {span: 8},
          wrapperCol: {span: 16},
          loading: false,
          visible: false
        },
        selectOrgModal: {
          visible: false,
          orgList: []
        },
        selectRoleModal: {
          visible: false,
          roleList: []
        }
      }
    },
    mounted () {
      this.topMenuList = this.menuList
      this.topCurrent = this.current
      this.navTitle = this.title
      let userInfo = JSON.parse(sessionStorage.getItem(USER_INFO))
      if (userInfo) {
        this.userInfo = userInfo
        this.userInfo.orgList.forEach(item => {
          if (item.code === this.userInfo.defaultOrgCode) {
            this.userInfo.defaultOrgName = item.name
          }
        })
        this.userInfo.roleList.forEach(item => {
          if (item.code === this.userInfo.defaultRoleCode) {
            this.userInfo.defaultRoleName = item.name
          }
        })
        operateApi.getTenantList({
          pageSize: -1
        }).then((res) => {
          if (res.success) {
            this.topTenantList = res.rows
          }
        })
      }
      this.init()
    },
    watch: {
      menuList () {
        this.topMenuList = this.menuList
      },
      current () {
        this.topCurrent = this.current
      },
      '$store.state.userInfo' () {
        this.init()
      }
    },
    methods: {
      init () {
        if (this.userInfo && this.userInfo.avatar) {
          this.avatar = this.userInfo.avatarThumb
        }
      },
      handleClick (menuCode) {
        if (this.topCurrent[0] !== menuCode) {
          this.topCurrent[0] = menuCode
          this.$emit('currentChange', this.topCurrent)
        }
      },
      // 新密码和确认密码校验
      compareToFirstPassword (rule, value, callback) {
        const form = this.updateModal.form
        if (value && value !== form.getFieldValue('newPassword')) {
          callback(new Error('新密码和确认密码不一致'))
        } else {
          callback()
        }
      },
      // 更新密码
      updatePwdOk () {
        this.updateModal.form.validateFields((err, values) => {
          if (!err) {
            console.log('Received values of form: ', values)
            operateApi.updateUserPwdNoLogin({
              account: values.username,
              password: values.password,
              newPassword: values.newPassword
            }).then((res) => {
              if (res.success) {
                this.$message.info('修改密码成功！')
              }
              this.updateModal.visible = false
            })
          }
        })
      },
      jumpClick (record) {
        tenantApi.loginByUserTenant({tenantId: record.tenantId}).then((res) => {
          if (res.success) {
            sessionStorage.setItem(MEMBER_INFO, JSON.stringify(res.rows[0]))
            let path = MenuUtils.getLeftMenuFirstPath(res.rows[0].menuTree[0].children)
            console.log('登录成功，跳转路径 ', path)
            window.location.href = path
          }
        })
      },
      jumpMe () {
        window.location.href = '/me/info'
      },
      // 显示授权信息
      showPower () {
        this.powerModal.visible = true
        console.log(this.topCurrent)
        console.log(this.userInfo.orgList)
        this.userInfo.orgList.forEach(item => {
          console.log(item.code)
        })
      },
      roleChange (value) {
        console.log(value)
        this.userInfo.defaultRoleCode = value
        if (!this.userInfo.showAllOrg) {
          let f = false
          for (let i = 0; i < this.userInfo.roleOrgMap[value].length; i++) {
            if (this.userInfo.roleOrgMap[value][i].code === this.userInfo.defaultOrgCode) {
              f = true
            }
          }
          if (!f) {
            this.userInfo.defaultOrgCode = undefined
            this.selectOrgModal.orgList = this.userInfo.roleOrgMap[value]
            this.selectOrgModal.visible = true
          } else {
            this.getAdminPower()
          }
        } else {
          this.getAdminPower()
        }
      },
      orgChange (value) {
        console.log(value)
        this.userInfo.defaultOrgCode = value
        if (!this.userInfo.showAllRole) {
          let f = false
          for (let i = 0; i < this.userInfo.orgRoleMap[value].length; i++) {
            if (this.userInfo.orgRoleMap[value][i].code === this.userInfo.defaultRoleCode) {
              f = true
            }
          }
          if (!f) {
            this.userInfo.defaultRoleCode = undefined
            this.selectRoleModal.roleList = this.userInfo.orgRoleMap[value]
            this.selectRoleModal.visible = true
          } else {
            this.getAdminPower()
          }
        } else {
          this.getAdminPower()
        }
      },
      getAdminPower () {
        if (!this.userInfo.showAllOrg && !this.userInfo.defaultOrgCode) {
          this.$message.error('请选择默认机构进行查询')
          this.powerModal.visible = true
          return
        }
        if (!this.userInfo.showAllRole && !this.userInfo.defaultRoleCode) {
          this.$message.error('请选择默认角色进行查询')
          this.powerModal.visible = true
          return
        }
        operateApi.changeUserDefaultOption({...this.userInfo}).then(res => {
          console.log('changDefaultOption: ', res)
          sessionStorage.setItem(USER_INFO, JSON.stringify(res.rows[0]))
          this.userInfo = res.rows[0]
          this.powerModal.visible = false
          this.selectOrgModal.visible = false
          this.selectRoleModal.visible = false
          let menuList = res.rows[0].menuTree
          let menuCode = MenuUtils.getTopMenuCode(menuList, this.$route.path)
          if (menuCode) {
            window.location.reload()
          } else {
            let path = MenuUtils.getLeftMenuFirstPath(menuList)
            window.location.href = path
          }
        })
      }
    }
  }
</script>

<style lang="less" scoped>
  .upms-head {
    height: 48px;
    line-height: 48px;
    text-align: center;

    .upms-head-txt {
      border-bottom: 1px solid #ebedf0;
      height: 48px;
      line-height: 48px;
      width: 100%;
      text-align: center;
    }
  }
</style>
