<template>
  <a-row>
    <a-col span="24">
      <a-card title="我的群组">
        <a-button type="primary" @click="addGroup" slot="extra">新建群</a-button>
        <div slot="cover" style="min-height:600px">
          <div v-for="group in myGroupList" :key="group.groupId" :group="group" class="friend"
               @click="showGroupInfo(group)">
            <a-avatar icon="user"/>
            {{group.groupName}}({{group.groupNo}})
          </div>
        </div>
      </a-card>
    </a-col>
    <!-- 创建群 -->
    <a-modal v-model="modal.visible" title="新建群">
      <a-input addonBefore="群名称" v-model="modal.groupName"/>
      <NCTree :tree-all-data="modal.allFriendList"
              :tree-select-data="modal.selectFriendList"
              @onCheck="(selectData) => {modal.selectFriendList = selectData}"/>
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="modal.loading" @click="addGroupOk">确定</a-button>
        <a-button key="back" @click="modal.visible = false">取消</a-button>
      </template>
    </a-modal>
    <a-modal v-model="groupInfo.visible" :footer="null" title="群组信息" width="300px">
      <a-row>
        <a-col span="24">
          群名称：{{groupInfo.info.groupName}}
        </a-col>
        <a-col span="24">群号：{{groupInfo.info.groupNo}}</a-col>
        <a-col span="24" style="text-align:center;margin-top: 20px">
          <a-button key="back" @click="sendNews(groupInfo.info)">发消息</a-button>
        </a-col>
      </a-row>
    </a-modal>
  </a-row>
</template>

<script>
  import {NCPagination, NCTree} from 'common'
  import * as chatApi from '../../api/chat'

  export default {
    name: 'MyGroup',
    components: {
      NCPagination,
      NCTree
    },
    data () {
      return {
        search: {
          groupNo: '',
          nickname: ''
        },
        loading: false,
        myGroupList: [],
        myFriendList: [],
        modal: {
          loading: false,
          visible: false,
          groupName: '',
          allFriendList: [],
          selectFriendList: []
        },
        groupInfo: {
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
        chatApi.getGroupList({
          data: {
            groupNo: this.search.groupNo
          }
        }).then((res) => {
          if (res.success) {
            this.myGroupList = res.rows
          }
          this.loading = false
        })
      },
      // 邀请成员
      addGroup () {
        this.modal.visible = true
        this.searchMyFriendList()
      },
      // 邀请成员
      addGroupOk () {
        if (!this.modal.groupName) {
          this.$message.error('请输入群组名称！')
          return
        }
        this.modal.loading = true
        chatApi.addMyGroup({
          groupName: this.modal.groupName,
          userList: this.modal.selectFriendList
        }).then((res) => {
          if (res.success) {
            this.modal.visible = false
            this.searchList()
          }
          this.modal.loading = false
        })
      },
      searchMyFriendList () {
        this.modal.loading = true
        this.modal.groupName = ''
        this.modal.selectFriendList = []
        this.modal.allFriendList = []
        chatApi.getMyFriendList({
          nickname: this.search.nickname
        }).then((res) => {
          if (res.success) {
            res.rows.forEach(item => {
              item.key = item.friendId
              item.title = item.nickname
            })
            this.modal.allFriendList = res.rows
          }
          this.modal.loading = false
        })
      },
      showGroupInfo (group) {
        this.groupInfo.info = group
        this.groupInfo.visible = true
      },
      sendNews (group) {
        console.log('发送消息 群id', group.groupId)
        this.groupInfo.visible = false
        group.chatActiveId = 'g' + group.groupId
        group.chatName = group.groupName
        this.$store.state.chatData.ChatData.setNewActiveChatList(group)
        this.$router.push({name: 'news', params: {currentId: group.chatActiveId}})
      }
    }
  }
</script>

<style scoped>

</style>
