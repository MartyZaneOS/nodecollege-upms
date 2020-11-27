import router from './router'

const defaultTitle = 'operate'
router.beforeEach((to, from, next) => {
  console.log('common permission', to)
  document.title = to.meta.title ? to.meta.title : defaultTitle
  next()
})
