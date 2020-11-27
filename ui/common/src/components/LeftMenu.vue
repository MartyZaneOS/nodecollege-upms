<template>
  <div>
    <a-menu
        @click="handleClick"
        v-model="model"
        :openKeys.sync="openKeys"
        mode="inline"
    >
      <template v-for="item in leftMenuTree">
        <a-menu-item v-if="item.menuType == 1" :key="item.pagePath" :info="item">
          <a-icon :type="item.icon"/>
          <span>{{ item.menuName }}</span>
        </a-menu-item>
        <sub-menu v-else-if="item.menuType == 0" :key="item.menuCode" :menu-info="item"/>
      </template>
    </a-menu>
  </div>
</template>

<script>
  import {Menu} from 'ant-design-vue'
  import {MenuUtils} from './Utils.js'

  const SubMenu = {
    template: `
      <a-sub-menu :key="menuInfo.menuCode" v-bind="$props" v-on="$listeners">
        <span slot="title">
          <a-icon :type="menuInfo.icon" /><span>{{ menuInfo.menuName }}</span>
        </span>
        <template v-for="item in menuInfo.children">
          <a-menu-item v-if="item.menuType == 1" :key="item.pagePath" :info="item">
            <a-icon :type="item.icon" />
            <span>{{ item.menuName }}</span>
          </a-menu-item>
          <sub-menu v-else-if="item.menuType == 0" :key="item.menuCode" :menu-info="item" />
        </template>
      </a-sub-menu>
    `,
    name: 'SubMenu',
    isSubMenu: true,
    props: {
      ...Menu.SubMenu.props,
      menuInfo: {
        type: Object,
        default: () => ({})
      }
    }
  }
  export default {
    name: 'LeftMenu',
    components: {
      'sub-menu': SubMenu,
      MenuUtils
    },
    props: {
      menuTree: {
        type: Array
      },
      current: {
        type: Array
      }
    },
    data () {
      return {
        model: [],
        leftMenuTree: [],
        openKeys: []
      }
    },
    watch: {
      menuTree () {
        this.leftMenuTree = this.menuTree
      },
      current () {
        this.model = this.current
        this.openKeys = MenuUtils.getOpenKeys(this.leftMenuTree, this.model[0])
      }
    },
    methods: {
      handleClick (val, e) {
        this.model = [val.key]
        this.openKeys = val.keyPath
        this.$emit('currentChange', this.model, val.item.$attrs.info)
      }
    }
  }
</script>

<style scoped>

</style>
