<template>
  <div class="upms-login">
    <div class="upms-login-header">
      <div class="h2">通用权限管理系统|租户成员登出</div>
    </div>
    <div class="upms-login-content" :style="{height:height+'px'}">
      <div style="margin: 100px auto; font-size: 24px">退出登陆中。。。</div>
    </div>
    <UpmsFooter></UpmsFooter>
  </div>
</template>

<script>
  import UpmsFooter from './UpmsFooter'
  import * as tenantApi from '../api/tenant'

  export default {
    name: 'Logout',
    components: {
      UpmsFooter
    },
    data () {
      return {
        height: this.$store.state.screenHeight - 200
      }
    },
    mounted () {
      console.log('退出登陆！')
      if (this.$store && this.$store.state && this.$store.state.chatData) {
        this.$store.state.chatData.ChatData.out()
      }
      tenantApi.logout({}).finally(() => {
        sessionStorage.clear()
        this.$router.push({path: '/tenant/login'})
      })
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
