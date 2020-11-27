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
import ATreeSelect from 'ant-design-vue/lib/tree-select'
import AInput from 'ant-design-vue/lib/input'
import APagination from 'ant-design-vue/lib/pagination'
import ADatePicker from 'ant-design-vue/lib/date-picker'
import ATable from 'ant-design-vue/lib/table'
import ACard from 'ant-design-vue/lib/card'
import AUpload from 'ant-design-vue/lib/upload'
import ABadge from 'ant-design-vue/lib/badge'
import AMessage from 'ant-design-vue/lib/message'
import ANotification from 'ant-design-vue/lib/notification'

// import 'ant-design-vue/dist/antd.css'
import 'ant-design-vue/lib/row/style/css'
import 'ant-design-vue/lib/col/style/css'
import 'ant-design-vue/lib/menu/style/css'
import 'ant-design-vue/lib/icon/style/css'
import 'ant-design-vue/lib/avatar/style/css'
import 'ant-design-vue/lib/tag/style/css'
import 'ant-design-vue/lib/dropdown/style/css'
import 'ant-design-vue/lib/list/style/css'
import 'ant-design-vue/lib/spin/style/css'
import 'ant-design-vue/lib/tree/style/css'
import 'ant-design-vue/lib/modal/style/css'
import 'ant-design-vue/lib/form/style/css'
import 'ant-design-vue/lib/button/style/css'
import 'ant-design-vue/lib/switch/style/css'
import 'ant-design-vue/lib/select/style/css'
import 'ant-design-vue/lib/tree-select/style/css'
import 'ant-design-vue/lib/input/style/css'
import 'ant-design-vue/lib/pagination/style/css'
import 'ant-design-vue/lib/date-picker/style/css'
import 'ant-design-vue/lib/table/style/css'
import 'ant-design-vue/lib/card/style/css'
import 'ant-design-vue/lib/upload/style/css'
import 'ant-design-vue/lib/badge/style/css'
import 'ant-design-vue/lib/message/style/css'
import 'ant-design-vue/lib/notification/style/css'

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
Vue.use(ATreeSelect)
Vue.use(AModal)
Vue.use(AForm)
Vue.use(AButton)
Vue.use(ASwitch)
Vue.use(ASelect)
Vue.use(AInput)
Vue.use(APagination)
Vue.use(ADatePicker)
Vue.use(ATable)
Vue.use(ACard)
Vue.use(AUpload)
Vue.use(ABadge)
Vue.use(AMessage)
Vue.use(ANotification)

Vue.prototype.$message = AMessage
Vue.prototype.$notification = ANotification
Vue.prototype.$info = AModal.info
Vue.prototype.$success = AModal.success
Vue.prototype.$error = AModal.error
Vue.prototype.$warning = AModal.warning
Vue.prototype.$confirm = AModal.confirm
Vue.prototype.$destroyAll = AModal.destroyAll
