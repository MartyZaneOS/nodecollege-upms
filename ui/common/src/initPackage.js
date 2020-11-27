import Vue from 'vue'
import Storage from 'vue-ls'
// import Antd from 'ant-design-vue'
import ARow from 'ant-design-vue/lib/row'
import ACol from 'ant-design-vue/lib/col'
import AMenu from 'ant-design-vue/lib/menu'
import AIcon from 'ant-design-vue/lib/icon'
import AAvatar from 'ant-design-vue/lib/avatar'
import ATag from 'ant-design-vue/lib/tag'
import ADropdown from 'ant-design-vue/lib/dropdown'
import AList from 'ant-design-vue/lib/list'
import ASpin from 'ant-design-vue/lib/spin'
import ATree from 'ant-design-vue/lib/tree'
import AModal from 'ant-design-vue/lib/modal'
import AForm from 'ant-design-vue/lib/form'
import AButton from 'ant-design-vue/lib/button'
import ASwitch from 'ant-design-vue/lib/switch'
import ASelect from 'ant-design-vue/lib/select'
import AInput from 'ant-design-vue/lib/input'
import APagination from 'ant-design-vue/lib/pagination'
import AMessage from 'ant-design-vue/lib/message'
import notification from 'ant-design-vue/lib/notification'
import Modal from 'ant-design-vue/lib/Modal'

// import 'ant-design-vue/dist/antd.css'

Vue.use(Storage, {
  namespace: 'pro__', // key prefix
  name: 'ls', // name variable Vue.[ls] or this.[$ls],
  storage: 'local' // storage name session, local, memory
})
// Vue.use(Antd)
Vue.use(ARow)
Vue.use(ACol)
Vue.use(AMenu)
Vue.use(AIcon)
Vue.use(AAvatar)
Vue.use(ATag)
Vue.use(ADropdown)
Vue.use(AList)
Vue.use(ASpin)
Vue.use(ATree)
Vue.use(AModal)
Vue.use(AForm)
Vue.use(AButton)
Vue.use(ASwitch)
Vue.use(ASelect)
Vue.use(AInput)
Vue.use(APagination)
Vue.use(AMessage)
Vue.use(notification)
Vue.use(Modal)

Vue.prototype.$message = AMessage
Vue.prototype.$notification = notification
Vue.prototype.$info = Modal.info
Vue.prototype.$success = Modal.success
Vue.prototype.$error = Modal.error
Vue.prototype.$warning = Modal.warning
Vue.prototype.$confirm = Modal.confirm
Vue.prototype.$destroyAll = Modal.destroyAll
