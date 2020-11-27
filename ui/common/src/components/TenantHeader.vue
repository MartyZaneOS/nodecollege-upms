<template>
  <div class="upms-head">
    <a-row>
      <a-col :xs="{span:4}" :sm="{span:4}" :md="{span:4}" :lg="{span:4}" :xxl="{span:4}" class="header-left">
        <div class="upms-head-txt">
          {{memberInfo && memberInfo.tenantName ? memberInfo.tenantName : '通用权限管理系统'}}
        </div>
      </a-col>
      <a-col :xs="{span:20}" :sm="{span:20}" :md="{span:20}" :lg="{span:20}" :xxl="{span:20}">
        <a-menu mode="horizontal" :selectable="false" :selectedKeys="topCurrent">
          <a-menu-item v-for="menu in topMenuList" :key="menu.menuCode" style="float: left;" @click="handleClick(menu.menuCode)">
            <a-icon type="appstore"/>
            {{menu.menuName}}
          </a-menu-item>
          <a-sub-menu style="float: right">
            <span slot="title" class="submenu-title-wrapper">
              <a-avatar :src="avatar" v-if="avatar"/> {{memberInfo.nickname ? memberInfo.nickname : memberInfo.account}} <a-icon type="down"/>
            </span>
            <a-menu-item key="jumpMe" v-if="userInfo && userInfo.accessToken" @click="$router.push({path: '/me/info'})">个人中心</a-menu-item>
            <a-menu-item key="updatePwd" @click="updatePwd">修改密码</a-menu-item>
            <a-menu-item key="updateDefaultOption"><div @click="showPower">修改默认设置</div></a-menu-item>
            <a-menu-item key="logout" v-if="userInfo && userInfo.accessToken" @click="$router.push({path: '/me/logout'})">退出登陆</a-menu-item>
            <a-menu-item key="logout" v-if="!userInfo || !userInfo.accessToken" @click="$router.push({path: '/tenant/logout'})">退出登陆</a-menu-item>
          </a-sub-menu>
          <a-sub-menu style="float: right" v-if="!memberInfo.showAllRole" key="showAllRole">
            <span slot="title" class="submenu-title-wrapper">角色 {{memberInfo.defaultRoleName}} <a-icon type="down"/></span>
            <a-menu-item v-for="(role, index) in memberInfo.roleList" :key="index" v-if="'2role_' + role.code" @click="roleChange(role.code)">
              {{role.name}}
            </a-menu-item>
          </a-sub-menu>
          <a-sub-menu style="float: right" v-if="!memberInfo.showAllOrg" key="showAllOrg">
            <span slot="title" class="submenu-title-wrapper">机构 {{memberInfo.defaultOrgName}} <a-icon type="down"/></span>
            <a-menu-item v-for="(org, index) in memberInfo.orgList" :key="index" v-if="'1org_' + org.code" @click="orgChange(org.code)">
              {{org.name}}
            </a-menu-item>
          </a-sub-menu>
        </a-menu>
      </a-col>
    </a-row>
    <a-modal v-model="updateModal.visible" title="修改密码">
      <a-form :form="updateModal.form">
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
              <a-switch v-model="memberInfo.showAllRole"></a-switch >
            </a-form-item>
            <a-form-item label="显示所有机构菜单">
              <a-switch v-model="memberInfo.showAllOrg"></a-switch >
            </a-form-item>
            <a-form-item label="默认角色" v-if="!memberInfo.showAllRole">
              <a-select placeholder="请选择默认角色！" v-model="memberInfo.defaultRoleCode" @change="roleChange" style="width: 200px" key="selectRole">
                <a-select-option v-for="role in memberInfo.roleList" :value="role.code" :key="role.code">{{role.name}}</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="默认机构" v-if="!memberInfo.showAllOrg">
              <a-select placeholder="请选择默认角色！" v-model="memberInfo.defaultOrgCode" @change="orgChange" style="width: 200px" key="selectOrg">
                <a-select-option v-for="org in memberInfo.orgList" :value="org.code" :key="org.code">{{org.name}}</a-select-option>
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
          <a-select placeholder="请选择默认机构！" v-model="memberInfo.defaultOrgCode" style="width: 200px">
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
          <a-select placeholder="请选择默认角色！" v-model="memberInfo.defaultRoleCode" style="width: 200px">
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
  import * as tenantApi from '../api/tenant'
  import {MenuUtils} from './Utils.js'
  import {USER_INFO, MEMBER_INFO} from './Constants'
  import * as Forge from 'node-forge'

  export default {
    name: 'MeHeader',
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
        topCurrent: [],
        width: this.$store.state.screenWidth - 200,
        memberInfo: {},
        userInfo: {},
        avatar: '',
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
      this.userInfo = JSON.parse(sessionStorage.getItem(USER_INFO))
      this.memberInfo = JSON.parse(sessionStorage.getItem(MEMBER_INFO))
      this.memberInfo.orgList.forEach(item => {
        if (item.code === this.memberInfo.defaultOrgCode) {
          this.memberInfo.defaultOrgName = item.name
        }
      })
      this.memberInfo.roleList.forEach(item => {
        if (item.code === this.memberInfo.defaultRoleCode) {
          this.memberInfo.defaultRoleName = item.name
        }
      })
      this.init()
      console.log(this.memberInfo)
    },
    watch: {
      menuList () {
        this.topMenuList = this.menuList
      },
      current () {
        this.topCurrent = this.current
      },
      '$store.state.memberInfo' () {
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
      updatePwd () {
        this.updateModal.visible = true
        this.updateModal.form.resetFields()
      },
      // 更新密码
      updatePwdOk () {
        this.updateModal.form.validateFields((err, values) => {
          if (!err) {
            console.log('Received values of form: ', values)
            operateApi.getPublicKey({}).then((pubRes) => {
              const pki = Forge.pki
              // 规定格式：publicKey之前需要加'-----BEGIN PUBLIC KEY-----\n'，之后需要加'\n-----END PUBLIC KEY-----'
              const publicK = pki.publicKeyFromPem('-----BEGIN PUBLIC KEY-----\n' + pubRes.rows[0] + '\n-----END PUBLIC KEY-----')
              // forge通过公钥加密后一般会是乱码格式，可进行base64编码操作再进行传输，相应的，后台获取到密文的密码后需要先进行base64解码操作再进行解密
              const password =  Forge.util.encode64(publicK.encrypt(values.password))
              const newPassword =  Forge.util.encode64(publicK.encrypt(values.newPassword))
              tenantApi.updatePwd({
                password: password,
                newPassword: newPassword,
                rsaTag: MenuUtils.getCookie('RSA-TAG')
              }).then((res) => {
                if (res.success) {
                  this.$message.info('修改密码成功！')
                }
                this.updateModal.visible = false
              })
            })
          }
        })
      },
      // 显示授权信息
      showPower () {
        this.powerModal.visible = true
        this.memberInfo.orgList.forEach(item => {
          console.log(item.code)
        })
      },
      roleChange (value) {
        this.memberInfo.defaultRoleCode = value
        if (!this.memberInfo.showAllOrg) {
          let f = false
          for (let i = 0; i < this.memberInfo.roleOrgMap[value].length; i++) {
            if (this.memberInfo.roleOrgMap[value][i].code === this.memberInfo.defaultOrgCode) {
              f = true
            }
          }
          if (!f) {
            this.memberInfo.defaultOrgCode = undefined
            this.selectOrgModal.orgList = this.memberInfo.roleOrgMap[value]
            this.selectOrgModal.visible = true
          } else {
            this.getAdminPower()
          }
        } else {
          this.getAdminPower()
        }
      },
      orgChange (value) {
        this.memberInfo.defaultOrgCode = value
        if (!this.memberInfo.showAllRole) {
          let f = false
          for (let i = 0; i < this.memberInfo.orgRoleMap[value].length; i++) {
            if (this.memberInfo.orgRoleMap[value][i].code === this.memberInfo.defaultRoleCode) {
              f = true
            }
          }
          if (!f) {
            this.memberInfo.defaultRoleCode = undefined
            this.selectRoleModal.roleList = this.memberInfo.orgRoleMap[value]
            this.selectRoleModal.visible = true
          } else {
            this.getAdminPower()
          }
        } else {
          this.getAdminPower()
        }
      },
      getAdminPower () {
        if (!this.memberInfo.showAllOrg && !this.memberInfo.defaultOrgCode) {
          this.$message.error('请选择默认机构进行查询')
          this.powerModal.visible = true
          return
        }
        if (!this.memberInfo.showAllRole && !this.memberInfo.defaultRoleCode) {
          this.$message.error('请选择默认角色进行查询')
          this.powerModal.visible = true
          return
        }
        tenantApi.changeDefaultOption({...this.memberInfo}).then(res => {
          sessionStorage.setItem(MEMBER_INFO, JSON.stringify(res.rows[0]))
          this.memberInfo = res.rows[0]
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
