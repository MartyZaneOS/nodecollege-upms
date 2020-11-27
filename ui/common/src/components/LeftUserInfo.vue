<template>
  <div>
    <a-menu mode="inline" :selectable="false">
      <a-menu-item key="news" style="font-size: 12px; text-align: center; padding: 0;" @click="newsVisible = true">
        消息({{newsNum}})
      </a-menu-item>
      <a-menu-item key="mail" style="font-size: 12px; text-align: center; padding: 0" @click="mailVisible = true">
        通讯录
      </a-menu-item>
    </a-menu>
    <a-modal v-model="newsVisible" width="1200px" title="消息" :footer="null" :bodyStyle="{padding:  0}">
      <chat-news></chat-news>
    </a-modal>

    <a-modal v-model="mailVisible" width="1200px" title="通讯录" :footer="null" :bodyStyle="{padding:  0}">
      <mail></mail>
    </a-modal>
  </div>
</template>

<script>
  import ChatNews from '../views/ChatNews'
  import Mail from './Mail'

  export default {
    name: 'LeftUserInfo',
    components: {
      ChatNews,
      Mail
    },
    data () {
      return {
        newsNum: 0,
        newsVisible: false,
        mailVisible: false
      }
    },
    // 离开时,销毁websocket
    destroyed () {
      this.$store.state.chatData.ChatData.out()
    },
    watch: {
      '$store.state.chatData.ChatData.currentChatId' () {
        console.log('LeftUserInfo $store.state.chatData.ChatData.currentChatId', this.$store.state.chatData.ChatData.currentChatId)
        if (!this.newsVisible && this.$store.state.chatData.ChatData.currentChatId) {
          this.newsVisible = true
          this.mailVisible = false
        }
      },
      '$store.state.chatData.ChatData.newsNum' () {
        console.log('LeftUserInfo $store.state.chatData.ChatData.newsNum', this.$store.state.chatData.ChatData.newsNum)
        this.newsNum = this.$store.state.chatData.ChatData.newsNum
      },
      newsVisible () {
        if (!this.newsVisible) {
          this.$store.state.chatData.ChatData.currentChatId = ''
        }
      }
    },
    mounted () {
      this.$store.state.chatData.ChatData.initWebsocket()
    }
  }
</script>

<style scoped>

</style>
