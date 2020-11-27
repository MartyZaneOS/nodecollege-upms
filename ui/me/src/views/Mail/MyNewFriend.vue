<template>
  <a-row>
    <a-col span="24">
      <a-card title="新的好友" style="min-height: 200px;">
        <div v-for="friend in myNewFriendList" :key="friend.userFriendId" :friend="friend">
          <a-avatar icon="user"/>
          {{friend.nickname}}
          <a-button key="notAgree" @click="notAgree(friend)" style="float: right" v-if="friend.state === 0">拒绝</a-button>
          <a-button key="agree" @click="agree(friend)" style="float: right" v-if="friend.state === 0">同意</a-button>
          <span v-if="friend.state === 1" style="color: #cccccc; font-size: 12px;float: right;">等待同意中</span>
        </div>
      </a-card>
    </a-col>
    <a-col span="24" style="min-height: 200px">
      <a-card title="好友推荐" style="min-height:400px">

      </a-card>
    </a-col>
    <!-- 添加好友 -->
    <a-modal v-model="modal.visible" title="同意请求">
      <a-form layout="inline">
        <a-form-item label="昵称">
          <a-input v-model="modal.newFriend.nickname" placeholder="请输入昵称！"></a-input>
        </a-form-item>
      </a-form>
      <template slot="footer">
        <a-button key="submit" @click="agreeOk">确定</a-button>
        <a-button key="back" @click="modal.visible = false">取消</a-button>
      </template>
    </a-modal>
  </a-row>
</template>

<script>
  import {NCPagination} from 'common'
  import * as chatApi from '../../api/chat'

  export default {
    name: 'MyNewFriend',
    components: {
      NCPagination
    },
    data () {
      return {
        search: {
          nickname: ''
        },
        loading: false,
        myNewFriendList: [],
        modal: {
          loading: false,
          visible: false,
          newFriend: {
            account: '',
            nickname: ''
          }
        }
      }
    },
    mounted () {
      this.searchList()
    },
    methods: {
      // 查询数据
      searchList () {
        this.loading = true
        chatApi.getNewFriendList({
          nickname: this.search.nickname
        }).then((res) => {
          if (res.success) {
            this.myNewFriendList = res.rows
          }
          this.loading = false
        })
      },
      agree (friend) {
        this.modal.visible = true
        this.modal.newFriend = friend
      },
      agreeOk () {
        this.modal.loading = true
        chatApi.handleRequest({
          userFriendId: this.modal.newFriend.userFriendId,
          nickname: this.modal.newFriend.nickname,
          state: 2
        }).then((res) => {
          if (res.success) {
            this.modal.visible = false
            this.searchList()
          }
          this.modal.loading = false
        })
      },
      notAgree (friend) {
        const that = this
        this.$confirm({
          title: '拒绝？',
          onOk () {
            return new Promise((resolve, reject) => {
              chatApi.handleRequest({
                userFriendId: friend.userFriendId,
                state: 3
              }).then((res) => {
                if (res.success) {
                  that.searchList()
                }
                reject(res.message)
              })
            })
          }
        })
      }
    }
  }
</script>

<style scoped>

</style>
