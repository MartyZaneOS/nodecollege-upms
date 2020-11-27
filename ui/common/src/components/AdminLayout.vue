<template>
  <div>
    <AdminHeader :current="topCurrent" :menu-list="menuList" @currentChange="topCurrentChange"/>
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
  import AdminHeader from './AdminHeader'
  import UpmsFooter from './UpmsFooter'
  import LeftMenu from './LeftMenu'
  import {MenuUtils} from './Utils'
  import {ADMIN_INFO} from './Constants'

  export default {
    name: 'Layout',
    components: {
      AdminHeader,
      UpmsFooter,
      LeftMenu,
      MenuUtils
    },
    data () {
      return {
        height: this.$store.state.screenHeight - 108,
        menuList: [],
        topCurrent: [],
        leftMenuList: [],
        leftCurrent: []
      }
    },
    watch: {
      '$store.state.screenHeight' () {
        this.height = this.$store.state.screenHeight - 108
      },
      '$store.state.iframeSrc' () {
        console.log('this.$store.state.iframeSrc', this.$store.state.iframeSrc)
        this.iframeSrc = this.$store.state.iframeSrc
      }
    },
    mounted () {
      sessionStorage.removeItem('notFoundPath')
      let userInfo = JSON.parse(sessionStorage.getItem(ADMIN_INFO))
      this.menuList = userInfo.menuTree
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
    },
    methods: {
      currentChange (item, info) {
        this.leftCurrent = item
        this.$router.push({path: this.leftCurrent[0]})
      },
      topCurrentChange (topMenuCode) {
        console.log(topMenuCode)
        let menuTree = JSON.parse(sessionStorage.getItem(ADMIN_INFO)).menuTree
        // let menuTree = menuListMock
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
