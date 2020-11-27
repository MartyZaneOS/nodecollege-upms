import Vue from 'vue'
import Vuex from 'vuex'
import router from '../router'
import {ACCESS_TOKEN, USER_INFO, ChatData} from 'common'

Vue.use(Vuex)
export default new Vuex.Store({
  state: {
    screenHeight: document.documentElement.clientHeight,
    screenWidth: document.documentElement.clientWidth,
    selectHead: '',
    userInfo: {},
    iframeSrc: null,
    chatData: ChatData
  },
  getters: {},
  mutations: {},
  actions: {
    getUserInfo () {
      return new Promise((resolve) => {
        Vue.prototype.http.post('upmsApi', '/user/getUserInfo', {
          token: sessionStorage.getItem(ACCESS_TOKEN)
        }).then((res) => {
          if (res.success) {
            console.log('userInfo', res.rows)
            // 个人中心数据
            let userCenter = {
              menuName: '个人中心',
              menuCode: 'userCenter',
              icon: 'bars',
              children: [
                {openType: 0, icon: 'bars', menuName: '个人信息', menuCode: 'myInfo'},
                {openType: 0, icon: 'bars', menuName: '我的企业', menuCode: 'myTenant'}
              ]
            }
            if (res.rows[0].menuTree == null) {
              res.rows[0].menuTree = []
            }
            res.rows[0].menuTree.push(userCenter)
            Vue.ls.set(USER_INFO, res.rows[0])
            this.state.userInfo = res.rows[0]
            this.state.operator = {
              tenantId: res.rows[0].tenantId,
              orgId: res.rows[0].orgId,
              memberId: res.rows[0].memberId,
              userId: res.rows[0].userId
            }
            if (this.state.userInfo.menuTree[0].children[0].openType === 0) {
              this.state.iframeSrc = null
              router.push({name: this.state.userInfo.menuTree[0].children[0].menuCode})
            } else {
              this.state.iframeSrc = this.state.userInfo.menuTree[0].children[0].path
              router.push({name: 'iframe'})
            }
          }
        })
      })
    }
  }
})
