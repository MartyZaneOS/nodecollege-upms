// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import store from './store'
import axios from 'axios'

import './initPackage'
import './permission'

import http from './utils/request'

Vue.prototype.http = http
Vue.config.productionTip = false

/* eslint-disable no-new */
/* eslint-disable no-new */
axios.get('../static/serverConfig.json').then((result) => {
  console.log(result.data)
  Vue.prototype.baseConfig = result.data
  new Vue({
    el: '#app',
    router,
    store,
    components: {App},
    template: '<App/>'
  })
}).catch((error) => {
  console.log('get baseConfig error...' + error)
})
