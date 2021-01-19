<template>
  <div>
    <TenantHeader :current="topCurrent" :menu-list="menuList" @currentChange="topCurrentChange"/>
    <a-row :style="{minHeight:height+'px'}">
      <a-col :span="leftMenuList.length > 0 ? 4 : 0" class="header-left" style="height: 100%;">
        <LeftMenu :menu-tree="leftMenuList" :current="leftCurrent" @currentChange="currentChange"/>
      </a-col>
      <a-col :span="leftMenuList.length > 0 ? 20 : 24">
        <router-view :style="{minHeight:(height - 60)+'px'}"></router-view>
      </a-col>
    </a-row>
    <a-row><UpmsFooter/></a-row>
  </div>
</template>

<script>
  import TenantHeader from './TenantHeader'
  import {MenuUtils} from './Utils.js'
  import {MEMBER_INFO} from './Constants'
  import UpmsFooter from './UpmsFooter'
  import LeftMenu from './LeftMenu'

  export default {
    name: 'TenantLayout',
    components: {
      TenantHeader,
      UpmsFooter,
      MenuUtils,
      MEMBER_INFO,
      LeftMenu
    },
    data () {
      return {
        height: this.$store.state.screenHeight - 48,
        menuList: [],
        topCurrent: [],
        leftMenuList: [],
        leftCurrent: []
      }
    },
    watch: {
      '$store.state.screenHeight' () {
        this.height = this.$store.state.screenHeight - 48
      },
      $route () {
        this.current = [this.$route.path]
      }
    },
    mounted () {
      sessionStorage.removeItem('notFoundPath')
      let userInfo = JSON.parse(sessionStorage.getItem(MEMBER_INFO))
      this.menuList = []
      for (let i = 0; i < userInfo.menuTree.length; i++) {
        if (userInfo.menuTree[i].navPlatform === 1) {
          this.menuList.push(userInfo.menuTree[i])
        }
      }
      let menuCode = MenuUtils.getTopMenuCode(this.menuList, this.$route.path)
      for (let i = 0; i < this.menuList.length; i++) {
        if (this.menuList[i].menuCode === menuCode) {
          if (this.menuList[i].menuType === 1) {
            this.leftCurrent = []
            this.leftMenuList = []
          } else {
            this.leftCurrent = [this.$route.path]
            this.leftMenuList = this.menuList[i].children
          }
          this.topCurrent = [menuCode]
          break
        }
      }
      this.$store.state.chatData.ChatData.initWebsocket()
    },
    destroyed () {
      this.$store.state.chatData.ChatData.out()
    },
    methods: {
      currentChange (item, info) {
        this.leftCurrent = item
        this.$router.push({path: this.leftCurrent[0]})
      },
      topCurrentChange (topMenuCode) {
        console.log(topMenuCode)
        // let menuTree = JSON.parse(sessionStorage.getItem(USER_INFO)).menuTree
        let menuTree = this.menuList
        for (let i = 0; i < menuTree.length; i++) {
          if (menuTree[i].menuCode === topMenuCode[0]) {
            let path = ''
            if (menuTree[i].menuType === 1) {
              this.leftMenuList = []
              this.leftCurrent = []
              path = menuTree[i].pagePath
            } else {
              this.leftMenuList = menuTree[i].children
              this.leftCurrent = [MenuUtils.getLeftMenuFirstPath(this.leftMenuList)]
              path = this.leftCurrent[0]
            }
            this.topCurrent = topMenuCode
            this.menuList = menuTree
            this.$router.push({path: path})
            break
          }
        }
      }
    }
  }
</script>

<style scoped>

</style>
