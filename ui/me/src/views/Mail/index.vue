<template>
  <a-row>
    <a-col span="4" style="height: 100%">
      <a-menu
          @click="handleClick"
          style="border-top: 1px solid #ebedf0;"
          :defaultSelectedKeys="['myFriend']"
          :openKeys.sync="openKeys"
          mode="inline"
      >
        <a-menu-item key="myFriend">
          <a-icon type="team"/>
          我的好友
        </a-menu-item>
        <a-menu-item key="myNewFriend">
          <a-icon type="team"/>
          新的好友
        </a-menu-item>
        <a-menu-item key="myGroup">
          <a-icon type="team"/>
          我的群组
        </a-menu-item>
      </a-menu>
    </a-col>
    <a-col span="20">
      <MyFriend v-if="selectKey === 'myFriend'"/>
      <MyNewFriend v-if="selectKey === 'myNewFriend'"/>
      <MyGroup v-if="selectKey === 'myGroup'"/>
    </a-col>
  </a-row>
</template>

<script>
  import MyFriend from './MyFriend'
  import MyNewFriend from './MyNewFriend'
  import MyGroup from './MyGroup'

  export default {
    name: 'Mail',
    components: {
      MyFriend,
      MyNewFriend,
      MyGroup
    },
    data () {
      return {
        selectKey: 'myFriend',
        openKeys: []
      }
    },
    watch: {
      openKeys (val) {
        console.log('openKeys', val)
      }
    },
    methods: {
      handleClick (val) {
        console.log(val)
        this.selectKey = val.key
      }
    }
  }
</script>
<style lang="less">
  @import "mail.less";
</style>
<style scoped lang="less">

</style>
