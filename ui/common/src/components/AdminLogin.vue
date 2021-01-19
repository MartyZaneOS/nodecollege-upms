<template>
  <div class="upms-login">
    <div class="upms-login-header">
      <div class="h2">通用权限管理系统|运维登陆</div>
    </div>
    <div class="upms-login-content" :style="{height:height+'px'}">
      <a-form class="login-form">
        <div style="height: 50px; line-height: 50px; font-size: 24px; text-align: center;">运维登录</div>
        <a-form-item class="form-item">
          <a-input
              size="large"
              type="text"
              placeholder="账户"
              v-model="loginParm.username"
              v-decorator="[
                'username',
                {rules: [{ required: true, message: '请输入帐户名' }, { validator: handleUsernameOrEmail }], validateTrigger: 'change'}
              ]">
            <a-icon slot="prefix" type="user" :style="{ color: 'rgba(0,0,0,.25)' }"/>
          </a-input>
        </a-form-item>
        <a-form-item class="form-item">
          <a-input
              size="large"
              type="password"
              autocomplete="false"
              placeholder="密码"
              v-model="loginParm.password"
              v-decorator="[
                'password',
                {rules: [{ required: true, message: '请输入密码' }], validateTrigger: 'blur'}
              ]"
          >
            <a-icon slot="prefix" type="lock" :style="{ color: 'rgba(0,0,0,.25)' }"/>
          </a-input>
        </a-form-item>
        <a-form-item class="form-item">
          <a-button
              style="width: 100%"
              size="large"
              type="primary"
              htmlType="submit"
              class="login-button"
              :loading="loginBtn"
              :disabled="loginBtn"
              @click="login"
          >确定
          </a-button>
        </a-form-item>
      </a-form>
    </div>
    <UpmsFooter></UpmsFooter>
    <a-modal v-model="modal.visible" title="修改密码">
      <a-form :form="modal.form">
        <a-form-item label="管理员账号" :label-col="modal.labelCol" :wrapper-col="modal.wrapperCol">
          <a-input placeholder="请输入管理员账号！"
                   v-decorator="['username', {rules: [{ required: true, message: '请输入管理员账号！'}]}]"/>
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
  </div>
</template>

<script>
  import UpmsFooter from './UpmsFooter'
  import {MenuUtils} from './Utils'
  import * as operateApi from '../api/operate'
  import {ADMIN_INFO} from './Constants'
  import * as Forge from 'node-forge'

  export default {
    name: 'Login',
    components: {
      UpmsFooter,
      MenuUtils
    },
    data () {
      return {
        height: this.$store.state.screenHeight - 200,
        loginBtn: false,
        loginType: 'user',
        loginParm: {
          username: '',
          password: ''
        },
        modal: {
          labelCol: {span: 7},
          wrapperCol: {span: 12},
          form: this.$form.createForm(this),
          visible: false,
          loading: false
        }
      }
    },
    methods: {
      handleUsernameOrEmail (rule, value, callback) {
        const {state} = this
        const regex = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/
        if (regex.test(value)) {
          state.loginType = 0
        } else {
          state.loginType = 1
        }
        callback()
      },
      login () {
        let that = this
        operateApi.getPublicKey({}).then((pubRes) => {
          const pki = Forge.pki
          // 规定格式：publicKey之前需要加'-----BEGIN PUBLIC KEY-----\n'，之后需要加'\n-----END PUBLIC KEY-----'
          const publicK = pki.publicKeyFromPem('-----BEGIN PUBLIC KEY-----\n' + pubRes.rows[0] + '\n-----END PUBLIC KEY-----');
          // forge通过公钥加密后一般会是乱码格式，可进行base64编码操作再进行传输，相应的，后台获取到密文的密码后需要先进行base64解码操作再进行解密
          const password =  Forge.util.encode64(publicK.encrypt(that.loginParm.password))
          operateApi.adminLogin({
            'imageCert': '111',
            'account': that.loginParm.username,
            'password': password,
            rsaTag: MenuUtils.getCookie('RSA-TAG')
          }).then((res) => {
            if (res.success) {
              sessionStorage.setItem(ADMIN_INFO, JSON.stringify(res.rows[0]))
              let pcMenu = []
              for (let i = 0; i < res.rows[0].menuTree.length; i++) {
                if (res.rows[0].menuTree[i].navPlatform === 1) {
                  pcMenu.push(res.rows[0].menuTree[i])
                }
              }
              let path = MenuUtils.getLeftMenuFirstPath(pcMenu)
              console.log('登录成功，跳转路径 ', path)
              that.$router.push({path: path})
            } else {
              console.log('登陆失败！')
              if (res.code === '10001') {
                setTimeout(() => {
                  this.model.form.setFieldsValue({
                    username: that.loginParm.username,
                    password: that.loginParm.password
                  })
                }, 100)
                this.modal.visible = true
              }
            }
          })
        })
      },
      // 新密码和确认密码校验
      compareToFirstPassword (rule, value, callback) {
        const form = this.modal.form
        if (value && value !== form.getFieldValue('newPassword')) {
          callback(new Error('新密码和确认密码不一致'))
        } else {
          callback()
        }
      },
      // 更新密码
      updatePwdOk () {
        this.modal.form.validateFields((err, values) => {
          if (!err) {
            operateApi.getPublicKey({}).then((pubRes) => {
              const pki = Forge.pki
              // 规定格式：publicKey之前需要加'-----BEGIN PUBLIC KEY-----\n'，之后需要加'\n-----END PUBLIC KEY-----'
              const publicK = pki.publicKeyFromPem('-----BEGIN PUBLIC KEY-----\n' + pubRes.rows[0] + '\n-----END PUBLIC KEY-----');
              // forge通过公钥加密后一般会是乱码格式，可进行base64编码操作再进行传输，相应的，后台获取到密文的密码后需要先进行base64解码操作再进行解密
              const password =  Forge.util.encode64(publicK.encrypt(values.password))
              const newPassword =  Forge.util.encode64(publicK.encrypt(values.newPassword))
              operateApi.updatePwdNoLogin({
                account: values.username,
                password: password,
                newPassword: newPassword,
                rsaTag: MenuUtils.getCookie('RSA-TAG')
              }).then((res) => {
                if (res.success) {
                  this.$message.info('修改密码成功，请重新登陆！')
                }
                this.modal.visible = false
              })
            })
          }
        })
      }
    }
  }
</script>

<style lang="less" scoped>
  .upms-login {
    height: 100%;
    font-size: 14px;
    background-color: #f5f5f6;

    .upms-login-header {
      font-size: 24px;
      height: 100px;
      line-height: 100px;
      background-color: #ffffff;

      .h2 {
        width: 60%;
        margin: 0 auto;
        text-align: left;
      }
    }

    .upms-login-content {
      width: 800px;
      min-height: 300px;
      margin: 0 auto;
      padding-top: 50px;;

      .login-form {
        width: 50%;
        float: right;
        background-color: #ffffff;
        height: 300px;

        .form-item {
          width: 80%;
          margin: 0 auto;
          margin-top: 10px;
        }
      }
    }
  }
</style>
