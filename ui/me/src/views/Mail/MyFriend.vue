<template>
  <a-row>
    <a-col span="24">
      <a-card title="我的好友">
        <a-button type="primary" @click="addUserFriend" slot="extra">添加好友</a-button>
        <div slot="cover" style="min-height:600px">
          <div v-for="friend in myFriendList" :key="friend.userFriendId" :friend="friend" class="friend"
               @click="showFriendInfo(friend)">
            <a>
              <a-avatar icon="user"/>
              {{friend.nickname}}
            </a>
          </div>
        </div>
      </a-card>
    </a-col>
    <!-- 添加好友 -->
    <a-modal v-model="modal.visible" title="添加好友" width="900px">
      <a-form layout="inline">
        <a-form-item label="账号/昵称/手机号/邮箱">
          <a-input v-model="modal.nickname" placeholder="请输入账号/昵称/手机号/邮箱！"></a-input>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="searchUserListByNickname">查询</a-button>
        </a-form-item>
      </a-form>
      <a-table :dataSource="modal.userList" :columns="modal.columns" :pagination="modal.pagination" size="small"
               bordered
               :rowKey="record => record.userId" :loading="modal.loading">
        <template slot="operation" slot-scope="text, record, index">
          <div>
            <span>
              <a @click="() => addUserFriendOk(record)">添加</a>
            </span>
          </div>
        </template>
      </a-table>
      <NCPagination :page="modal.pageObj" @changePage="(page)=>{this.modal.pageObj = page}"
                    @searchList="searchUserListByNickname"/>
      <template slot="footer">
        <a-button key="back" @click="modal.visible = false">取消</a-button>
      </template>
    </a-modal>
    <a-modal v-model="friendInfo.visible" :footer="null" title="好友信息" width="300px">
      <a-row>
        <a-col span="16">
          <p>{{friendInfo.info.nickname}}</p>
          <!--          <p>公司信息</p>-->
        </a-col>
        <a-col span="8" style="min-height: 84px;">
          <a-avatar icon="user"/>
        </a-col>
        <a-col span="24">账号：{{friendInfo.info.friendAccount}}</a-col>
        <a-col span="24">来源：{{friendInfo.info.source === 1 ? '账号搜索' : '群好友添加'}}</a-col>
        <a-col span="24" style="text-align:center;margin-top: 20px">
          <a-button key="back" @click="sendNews(friendInfo.info)">发消息</a-button>
        </a-col>
      </a-row>
    </a-modal>
  </a-row>
</template>

<script>
  import {NCPagination} from 'common'
  import * as operateApi from '../../api/operate'
  import * as chatApi from '../../api/chat'

  export default {
    name: 'MyFriend',
    components: {
      NCPagination
    },
    data () {
      return {
        search: {
          nickname: ''
        },
        loading: false,
        myFriendList: [],
        modal: {
          pageObj: {total: 10, pageSize: 10, pageNum: 1, pageSizeOptions: ['5', '10', '20', '50', '100']},
          labelCol: {span: 7},
          wrapperCol: {span: 12},
          pagination: false,
          loading: false,
          visible: false,
          nickname: '',
          userList: [],
          columns: [
            {title: '序号', width: '20', customRender: (text, record, index) => { return index + this.modal.pageObj.pageSize * (this.modal.pageObj.pageNum - 1) + 1 }},
            {title: '好友账号', dataIndex: 'account'},
            {title: '好友昵称', dataIndex: 'nickname'},
            {title: '手机号', dataIndex: 'telephone'},
            {title: '好友邮箱', dataIndex: 'email'},
            {title: '操作', fixed: 'right', width: 100, scopedSlots: {customRender: 'operation'}}
          ]
        },
        friendInfo: {
          visible: false,
          info: {}
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
        chatApi.getMyFriendList({
          nickname: this.search.nickname
        }).then((res) => {
          if (res.success) {
            this.myFriendList = res.rows
          }
          this.loading = false
        })
      },
      // 邀请成员
      addUserFriend () {
        this.modal.visible = true
        this.modal.nickname = undefined
        this.modal.userList = []
      },
      searchUserListByNickname () {
        this.modal.loading = true
        operateApi.getUserListByNickname({
          pageNum: this.modal.pageObj.pageNum,
          pageSize: this.modal.pageObj.pageSize,
          data: {
            nickname: this.modal.nickname
          }
        }).then((res) => {
          if (res.success) {
            this.modal.userList = res.rows
            this.modal.pageObj.total = res.total
          }
          this.modal.loading = false
        })
      },
      // 邀请成员
      addUserFriendOk (record) {
        this.modal.loading = true
        chatApi.addFriend({
          friendId: record.userId,
          source: 1
        }).then((res) => {
          if (res.success) {
            this.modal.visible = false
          }
          this.modal.loading = false
        })
      },
      showFriendInfo (friend) {
        this.friendInfo.info = friend
        this.friendInfo.visible = true
      },
      sendNews (friend) {
        console.log('发送消息 好友id', friend.friendId)
        this.friendInfo.visible = false
        friend.chatActiveId = 'f' + friend.friendId
        friend.chatName = friend.nickname
        this.$store.state.chatData.ChatData.setNewActiveChatList(friend)
        this.$router.push({name: 'news', params: {currentId: friend.chatActiveId}})
      }
    }
  }
</script>

<style scoped lang="less">

</style>
