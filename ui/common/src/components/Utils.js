// 菜单工具类
const MenuUtils = {
  // 根据路径和菜单树获取顶级菜单代码
  getTopMenuCode (menuTree, path) {
    for (let i = 0; i < menuTree.length; i++) {
      if (menuTree[i].menuType === 1) {
        if (path.length > 1 && path.length - 1 === path.lastIndexOf('/')) {
          path = path.substr(0, path.length - 1)
        }
        let pagePath = menuTree[i].pagePath
        if (pagePath.length > 1 && pagePath.length - 1 === pagePath.lastIndexOf('/')) {
          pagePath = pagePath.substr(0, pagePath.length - 1)
        }
        if (pagePath === path) {
          return menuTree[i].menuCode
        }
      }
      if (menuTree[i].menuType === 0 && menuTree[i].children) {
        const res = MenuUtils.getTopMenuCode(menuTree[i].children, path)
        if (res) {
          return menuTree[i].menuCode
        }
      }
    }
    return ''
  },
  // 获取左侧菜单第一个页面菜单路径
  getLeftMenuFirstPath (menuTree) {
    let path = ''
    if (!menuTree) {
      return path
    }
    for (let i = 0; i < menuTree.length; i++) {
      if (menuTree[i].menuType === 1) {
        path = menuTree[i].pagePath
        break
      }
      if (menuTree[i].menuType === 0 && menuTree[i].children) {
        const res = MenuUtils.getLeftMenuFirstPath(menuTree[i].children)
        if (res) {
          path = res
          break
        }
      }
    }
    return path
  },
  // 获取打开菜单页面的分类导航代码列表
  getOpenKeys (tree, current) {
    const path = []
    for (let i = 0; i < tree.length; i++) {
      if (tree[i].pagePath === current) {
        path.push(tree[i].pagePath)
        return path
      }
      if (tree[i].children) {
        const res = this.getOpenKeys(tree[i].children, current)
        if (res && res.indexOf(current) > -1) {
          path.push(tree[i].menuCode)
          path.push(...res)
          return path
        }
      }
    }
  },
  // 设置cookie
  setCookie (key, value, expire) {
    var date = new Date()
    date.setSeconds(date.getSeconds() + expire)
    document.cookie = key + '=' + escape(value) + '; expires=' + date.toGMTString()
  },
  // 获取cookie
  getCookie (name) {
    if (document.cookie.length > 0) {
      let start = document.cookie.indexOf(name + '=')
      if (start !== -1) {
        start = start + name.length + 1
        let end = document.cookie.indexOf(';', start)
        if (end === -1) end = document.cookie.length
        return unescape(document.cookie.substring(start, end))
      }
    }
    return ''
  },
  // 删除cookie
  delCookie (name) {
    this.setCookie(name, '', -1)
  }
}

export {
  MenuUtils
}
