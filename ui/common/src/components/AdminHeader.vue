<template>
  <div class="upms-head">
    <a-row>
      <a-col :xs="{span:24}" :sm="{span:24}" :md="{span:6}" :lg="{span:5}" :xxl="{span:4}" class="header-left">
        <div class="upms-head-txt">
          通用权限管理系统
        </div>
      </a-col>
      <a-col :xs="{span:0}" :sm="{span:0}" :md="{span:18}" :lg="{span:19}" :xxl="{span:20}">
        <a-menu :selectedKeys="topCurrent" mode="horizontal">
          <a-menu-item v-for="menu in topMenuList" :key="menu.menuCode" style="float: left;" v-if="menu.menuCode"
                       @click="handleClick(menu.menuCode)">
            <a-icon type="appstore"/>
            {{menu.menuName}}
          </a-menu-item>
          <a-sub-menu style="float: right" key="_right1">
            <span slot="title" class="submenu-title-wrapper"><a-icon type="setting"/>管理员中心<a-icon type="down"/></span>
            <a-menu-item key="updatePwd" @click="updateModal.visible = true">修改密码</a-menu-item>
            <a-menu-item key="updateDefaultOption"><div @click="showPower">修改默认设置</div></a-menu-item>
            <a-menu-item key="logout">
              <div @click="$router.push({path: '/admin/logout'})">退出登陆</div>
            </a-menu-item>
          </a-sub-menu>
          <a-sub-menu style="float: right" v-if="!adminInfo.showAllRole" key="showAllRole">
            <span slot="title" class="submenu-title-wrapper">角色 {{adminInfo.defaultRoleName}} <a-icon type="down"/></span>
            <a-menu-item v-for="(role, index) in adminInfo.roleList" :key="index" @click="roleChange(role.code)">
              {{role.name}}
            </a-menu-item>
          </a-sub-menu>
          <a-sub-menu style="float: right" v-if="!adminInfo.showAllOrg" key="showAllOrg">
            <span slot="title" class="submenu-title-wrapper">机构 {{adminInfo.defaultOrgName}} <a-icon type="down"/></span>
            <a-menu-item v-for="(org, index) in adminInfo.orgList" :key="index" @click="orgChange(org.code)">
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
              <a-switch v-model="adminInfo.showAllRole"></a-switch >
            </a-form-item>
            <a-form-item label="显示所有机构菜单">
              <a-switch v-model="adminInfo.showAllOrg"></a-switch >
            </a-form-item>
            <a-form-item label="默认角色" v-if="!adminInfo.showAllRole">
              <a-select placeholder="请选择默认角色！" v-model="adminInfo.defaultRoleCode" @change="roleChange" style="width: 200px" key="selectRole">
                <a-select-option v-for="role in adminInfo.roleList" :value="role.code" :key="role.code">{{role.name}}</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="默认机构" v-if="!adminInfo.showAllOrg">
              <a-select placeholder="请选择默认角色！" v-model="adminInfo.defaultOrgCode" @change="orgChange" style="width: 200px" key="selectOrg">
                <a-select-option v-for="org in adminInfo.orgList" :value="org.code" :key="org.code">{{org.name}}</a-select-option>
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
          <a-select placeholder="请选择默认机构！" v-model="adminInfo.defaultOrgCode" style="width: 200px">
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
          <a-select placeholder="请选择默认角色！" v-model="adminInfo.defaultRoleCode" style="width: 200px">
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
  import * as operateApi from '../api/operate'
  import {ADMIN_INFO} from './Constants'
  import {MenuUtils} from './Utils'

  export default {
    name: 'UpmsHeader',
    props: {
      menuList: {
        type: Array
      },
      current: {
        type: Array
      }
    },
    data () {
      return {
        topCurrent: ['admin'],
        width: this.$store.state.screenWidth - 200,
        adminInfo: {},
        topMenuList: [],
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
      let userInfo = JSON.parse(sessionStorage.getItem(ADMIN_INFO))
      this.adminInfo = userInfo
      this.adminInfo.orgList.forEach(item => {
        if (item.code === this.adminInfo.defaultOrgCode) {
          this.adminInfo.defaultOrgName = item.name
        }
      })
      this.adminInfo.roleList.forEach(item => {
        if (item.code === this.adminInfo.defaultRoleCode) {
          this.adminInfo.defaultRoleName = item.name
        }
      })
    },
    watch: {
      menuList () {
        this.topMenuList = this.menuList
      },
      current () {
        this.topCurrent = this.current
      }
    },
    methods: {
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
            operateApi.updatePassword({
              account: values.username,
              password: values.password,
              newPassword: values.newPassword,
              cert: '1'
            }).then((res) => {
              if (res.success) {
                this.$message.info('修改密码成功！')
              }
              this.updateModal.visible = false
            })
          }
        })
      },
      // 显示授权信息
      showPower () {
        this.powerModal.visible = true
        console.log(this.topCurrent)
        console.log(this.adminInfo.orgList)
        this.adminInfo.orgList.forEach(item => {
          console.log(item.code)
        })
      },
      roleChange (value) {
        console.log(value)
        this.adminInfo.defaultRoleCode = value
        if (!this.adminInfo.showAllOrg) {
          let f = false
          for (let i = 0; i < this.adminInfo.roleOrgMap[value].length; i++) {
            if (this.adminInfo.roleOrgMap[value][i].code === this.adminInfo.defaultOrgCode) {
              f = true
            }
          }
          if (!f) {
            this.adminInfo.defaultOrgCode = undefined
            this.selectOrgModal.orgList = this.adminInfo.roleOrgMap[value]
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
        this.adminInfo.defaultOrgCode = value
        if (!this.adminInfo.showAllRole) {
          let f = false
          for (let i = 0; i < this.adminInfo.orgRoleMap[value].length; i++) {
            if (this.adminInfo.orgRoleMap[value][i].code === this.adminInfo.defaultRoleCode) {
              f = true
            }
          }
          if (!f) {
            this.adminInfo.defaultRoleCode = undefined
            this.selectRoleModal.roleList = this.adminInfo.orgRoleMap[value]
            this.selectRoleModal.visible = true
          } else {
            this.getAdminPower()
          }
        } else {
          this.getAdminPower()
        }
      },
      getAdminPower () {
        if (!this.adminInfo.showAllOrg && !this.adminInfo.defaultOrgCode) {
          this.$message.error('请选择默认机构进行查询')
          this.powerModal.visible = true
          return
        }
        if (!this.adminInfo.showAllRole && !this.adminInfo.defaultRoleCode) {
          this.$message.error('请选择默认角色进行查询')
          this.powerModal.visible = true
          return
        }
        operateApi.changeDefaultOption({...this.adminInfo}).then(res => {
          console.log('changDefaultOption: ', res)
          sessionStorage.setItem(ADMIN_INFO, JSON.stringify(res.rows[0]))
          this.adminInfo = res.rows[0]
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
