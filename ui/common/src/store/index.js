import Vue from 'vue'
import Vuex from 'vuex'
import router from '../router'
import {ACCESS_TOKEN, USER_INFO} from '../components/Constants'
import ChatData from '../components/ChatData'

Vue.use(Vuex)
export default new Vuex.Store({
  state: {
    screenHeight: document.documentElement.clientHeight,
    screenWidth: document.documentElement.clientWidth,
    selectHead: '',
    userInfo: {},
    operator: {
      tenantId: null,
      orgId: null,
      memberId: null,
      userId: null
    },
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
            sessionStorage.setItem(USER_INFO, JSON.stringify(res.rows[0]))
            this.state.userInfo = res.rows[0]
            this.state.operator = {
              tenantId: res.rows[0].tenantId,
              orgId: res.rows[0].orgId,
              memberId: res.rows[0].memberId,
              userId: res.rows[0].userId
            }
            router.push({path: '/me/info'})
          }
        })
      })
    }
  }
})
