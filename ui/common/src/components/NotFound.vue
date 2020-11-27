<template>
  <div class="upms-login" v-if="show">
    <div class="upms-login-header">
      <div class="h2">通用权限管理系统|404 Not Found</div>
    </div>
    <div class="upms-login-content" :style="{height:height+'px'}">
      <div style="margin: 100px auto; font-size: 24px">页面不存在！！！</div>
    </div>
    <UpmsFooter></UpmsFooter>
  </div>
</template>

<script>
  import UpmsFooter from './UpmsFooter'

  export default {
    name: 'NotFound',
    components: {
      UpmsFooter
    },
    data () {
      return {
        iframeSrc: '',
        height: this.$store.state.screenHeight - 200,
        show: false
      }
    },
    watch: {
      '$route.path' () {
        this.notFound()
      },
      '$store.state.screenHeight' () {
        this.height = this.$store.state.screenHeight - 200
      }
    },
    mounted () {
      this.notFound()
    },
    methods: {
      notFound () {
        let path = this.$route.path
        if (path.indexOf('/404') === 0) {
          this.show = true
          return
        }
        if (sessionStorage.getItem('notFoundPath') === path) {
          sessionStorage.removeItem('notFoundPath')
          window.location.href = '/404'
        } else {
          sessionStorage.setItem('notFoundPath', path)
          this.show = false
          window.location.href = path
        }
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
