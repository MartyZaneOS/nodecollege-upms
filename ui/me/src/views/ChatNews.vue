<template>
  <a-row>
    <a-col span="4" style="height: 100%">
      <a-menu
          @click="handleClick"
          style="border-top: 1px solid #ebedf0;"
          v-model="current"
          :openKeys.sync="openKeys"
          mode="inline"
      >
        <a-menu-item v-for="activeChat in activeChatList" :key="activeChat.chatActiveId" :chat="activeChat"
                     :avatarThumb="activeChat.avatarThumb">
          <a-avatar v-if="activeChat.avatarThumb" :src="activeChat.avatarThumb"/>
          <a-avatar v-if="!activeChat.avatarThumb">
            {{activeChat.chatName ? activeChat.chatName.substring(0, 2) : ''}}
          </a-avatar>
          {{user.loginId == activeChat.friendId ? '我(' + user.name + ')' : activeChat.chatName}}
          <span v-if="activeChat.newsNum !== 0" style="float: right">{{activeChat.newsNum}}</span>
        </a-menu-item>
      </a-menu>
    </a-col>
    <a-col span="20" style="padding: 0 3px">
      <a-card :title="currentChatName">
        <a-button type="primary" slot="extra" @click="showChatInfo" v-if="currentChatId">...</a-button>
        <div slot="cover" style="height: 600px">
          <div style="height: 450px;overflow-y: auto;background-color: #f9f9f9" id="recordContent">
            <a-row>
              <a-col span="24" v-for="info in currentChatInfoList" :key="info.recordId" class="recordItem"
                     :id="info.recordId" :receiveUserId="info.receiveUserId" :sendUserId="info.sendUserId"
                     :receiveState="info.receiveState">
                <div v-if="info.recordId === -1" style="width: 100%; text-align: center; color: #cccccc">已经没有了</div>
                <div v-if="info.recordId !== -1"
                     :class="info.sendUserId === user.loginId ? 'record-img floatRight' : 'record-img floatLeft'">
                  <a-avatar v-if="userAvatarMap[info.sendUserId]" :src="userAvatarMap[info.sendUserId]"/>
                  <a-avatar v-if="!userAvatarMap[info.sendUserId]">
                    {{info.nickname ? info.nickname.substring(0, 2) : ''}}
                  </a-avatar>
                </div>
                <div v-if="info.recordId !== -1"
                     :class="info.sendUserId === user.loginId ? 'record-content floatRight' : 'record-content floatLeft'">
                  <div class="record-nickname" v-if="info.groupId">{{info.nickname}}</div>
                  <div class="record-txt">{{info.content}}</div>
                  <span v-if="!info.groupId && info.sendUserId === user.loginId"
                        style="font-size: 12px; color: #c8c8c8; float: right;">
                    {{info.receiveState === 1 ? '未读' : '已读'}}
                  </span>
                </div>
              </a-col>
            </a-row>
          </div>
          <div style="width: 100%; height: 132px;margin-bottom: 0px;" v-if="currentChatId">
            <a-textarea v-model="input_msg" style="height: 100px"/>
            <a-button @click="sendChatMsg(input_msg)" style="float: right; margin-right: 30px; width: 100px;">发送
            </a-button>
          </div>
        </div>
      </a-card>
    </a-col>
    <a-modal v-model="chatInfo.visible" title="聊天设置" :footer="null">
      <a-row>
        <a-col span="24" style="height: 40px;">
          <div style="font-size: 24px;" v-if="!chatInfo.updateName">
            {{chatInfo.groupName}}
            <span v-if="chatInfo.info.groupId" style="font-size: 12px;color: #c8c8c8;"
                  @click="chatInfo.updateName = true">
              修改
            </span>
          </div>
          <div v-if="chatInfo.updateName">
            <a-input v-model="chatInfo.groupName">
              <span slot="suffix" @click="updateChatName" style="margin-right: 10px;">
                确定
              </span>
            </a-input>
          </div>
        </a-col>
      </a-row>
      <a-row v-if="chatInfo.info.groupId" style="margin-top: 20px;">
        <a-col span="24">
          群成员{{chatInfo.groupUserList.length}}人
          <a-button v-if="chatInfo.loginUser.userType < 3" shape="circle" icon="edit"
                    @click="editUserModal.visible = true" style="float: right;"/>
          <a-button shape="circle" icon="plus" @click="addGroupUser" style="float: right;"/>
        </a-col>
        <a-col span="24">
          <div v-for="gUser in chatInfo.groupUserList" :key="gUser.userId"
               style="width: 50px; float: left; margin-right: 5px;" :title="gUser.nickname">
            <div style="width: 100%;">
              <a-badge v-if="gUser.userType !== 3">
                <span slot="count" class="groupAdmin">{{gUser.userType === 1 ? '群主' : '管理员'}}</span>
                <a-avatar :size="50" v-if="gUser.avatarThumb" :src="gUser.avatarThumb"></a-avatar>
                <a-avatar :size="50" v-if="!gUser.avatarThumb">{{gUser.nickname.substring(0, 2)}}</a-avatar>
              </a-badge>
              <div v-if="gUser.userType === 3">
                <a-avatar :size="50" v-if="gUser.avatarThumb" :src="gUser.avatarThumb"></a-avatar>
                <a-avatar :size="50" v-if="!gUser.avatarThumb">{{gUser.nickname.substring(0, 2)}}</a-avatar>
              </div>
            </div>
            <div style="width: 100%; overflow: hidden; text-overflow:ellipsis;white-space: nowrap;">
              {{gUser.nickname}}
            </div>
          </div>
        </a-col>
        <a-col span="24" style="margin-top: 20px;height: 40px;">
          我在本群的昵称
          <div style="float: right; text-align: right;" v-if="!chatInfo.updateGroupNickname">
            {{chatInfo.myGroupNickName}}
            <span @click="chatInfo.updateGroupNickname = true" style="font-size: 12px;color: #c8c8c8;">修改</span>
          </div>
          <div style="float: right; text-align: right;" v-if="chatInfo.updateGroupNickname">
            <a-input v-model="chatInfo.myGroupNickName">
              <span slot="suffix" @click="updateGroupNickname" style="margin-right: 10px;">
                确定
              </span>
            </a-input>
          </div>
        </a-col>
      </a-row>
      <a-row style="margin-top: 10px;">
        <a-col span="24">
          消息设置
          <div style="float: right; text-align: right;" v-if="!chatInfo.updateGroupState">
            {{chatInfo.myGroupState === '2' ? '屏蔽' : '正常' }}
            <span @click="chatInfo.updateGroupState = true" style="font-size: 12px;color: #c8c8c8;">修改</span>
          </div>
          <div style="float: right; text-align: right;" v-if="chatInfo.updateGroupState">
            <a-select v-model="chatInfo.myGroupState" style="width: 120px" @change="updateGroupState">
              <a-select-option value="1">正常</a-select-option>
              <a-select-option value="2">屏蔽</a-select-option>
            </a-select>
          </div>
        </a-col>
      </a-row>
    </a-modal>
    <!-- 添加群成员 -->
    <a-modal v-model="addUserModal.visible" title="添加新成员">
      <NCTree :tree-all-data="addUserModal.allFriendList"
              :tree-select-data="addUserModal.selectFriendList"
              @onCheck="(selectData) => {addUserModal.selectFriendList = selectData}"/>
      <template slot="footer">
        <a-button key="submit" type="primary" @click="addGroupUserOk">确定</a-button>
        <a-button key="back" @click="addUserModal.visible = false">取消</a-button>
      </template>
    </a-modal>

    <!-- 编辑群成员 -->
    <a-modal v-model="editUserModal.visible" title="编辑新成员" :footer="null">
      <a-row>
        <a-col span="24" v-for="gUser in chatInfo.groupUserList" :key="gUser.userId">
          <div style="width: 80px;float: left">
            <a-badge v-if="gUser.userType !== 3">
              <span slot="count" class="groupAdmin">{{gUser.userType === 1 ? '群主' : '管理员'}}</span>
              <a-avatar :size="50" v-if="gUser.avatarThumb" :src="gUser.avatarThumb"></a-avatar>
              <a-avatar :size="50" v-if="!gUser.avatarThumb">{{gUser.nickname.substring(0, 2)}}</a-avatar>
            </a-badge>
            <div v-if="gUser.userType === 3">
              <a-avatar :size="50" v-if="gUser.avatarThumb" :src="gUser.avatarThumb"></a-avatar>
              <a-avatar :size="50" v-if="!gUser.avatarThumb">{{gUser.nickname.substring(0, 2)}}</a-avatar>
            </div>
          </div>
          <div style="width: 100px;float: left">
            {{gUser.nickname}}
          </div>
          <div style="float: right">
            <a @click="() => delGroupUserOk(gUser)">移出群</a>
          </div>
        </a-col>
      </a-row>
    </a-modal>
  </a-row>
</template>

<script>
  import {USER_INFO, NCTree} from 'common'

  export default {
    name: 'ChatNews',
    components: {
      NCTree
    },
    data () {
      return {
        topRecord: '',
        openKeys: [],
        current: [],
        my_socket: null,
        user: {},
        activeChatList: [],
        currentChatId: '',
        // 当前聊天类型 f-好友聊天，g-群组聊天
        currentType: '',
        // 当前聊天窗口位置
        scrollTop: '',
        // 聊天记录列表
        recordElementList: [],
        currentChatName: '',
        currentChatInfoList: [],
        // 用户头像对应map
        userAvatarMap: {},
        // 聊天室信息
        chatInfo: {
          visible: false,
          info: {},
          loginUser: {},
          updateName: false,
          groupName: '',
          groupUserList: [],
          updateGroupNickname: false,
          myGroupNickName: '',
          updateGroupState: false,
          myGroupState: ''
        },
        input_msg: '',
        get_msg: [],
        addUserModal: {
          visible: false,
          allFriendList: [],
          selectFriendList: []
        },
        editUserModal: {
          visible: false,
          delUserList: []
        }
      }
    },
    watch: {
      '$store.state.chatData.ChatData.updateTime' () {
        console.log('watch this.$store.state.chatData', this.$store.state.chatData)
        this.activeChatList = []
        this.activeChatList = this.$store.state.chatData.ChatData.activeChatList
        this.current = [this.$store.state.chatData.ChatData.currentChatId]
        this.currentChatId = this.$store.state.chatData.ChatData.currentChatId
        this.currentType = this.currentChatId.substring(0, 1)
        this.handleRecordList()
        setTimeout(() => {
          // 获取聊天窗口里所有的聊天数据
          this.recordElementList = document.getElementById('recordContent').getElementsByClassName('recordItem')
          if (this.topRecord) {
            window.location.hash = '#' + this.topRecord.recordId
          }
        }, 1)
      },
      '$store.state.chatData.ChatData.currentChatId' () {
        console.log('ChatNews $store.state.chatData.ChatData.currentChatId', this.$store.state.chatData.ChatData.currentChatId)
        this.user = JSON.parse(sessionStorage.getItem(USER_INFO))
        this.current = [this.$store.state.chatData.ChatData.currentChatId]
        this.currentChatId = this.$store.state.chatData.ChatData.currentChatId
        this.currentType = this.currentChatId.substring(0, 1)
        let activeChatList = this.$store.state.chatData.ChatData.activeChatList
        if (activeChatList) {
          activeChatList.forEach(item => {
            if (item.currentChatId === this.currentChatId) {
              this.currentChatName = this.user.loginId === item.friendId ? '我(' + this.user.name + ')' : item.chatName
            }
          })
          this.topRecord = ''
          this.handleRecordList()
          // 清空输入框数据
          this.input_msg = ''
          // 定义鼠标滚动事件
          setTimeout(() => {
            document.getElementById('recordContent').addEventListener('scroll', this.scrollListener)
            // 获取聊天窗口里所有的聊天数据
            this.recordElementList = document.getElementById('recordContent').getElementsByClassName('recordItem')
            if (this.topRecord) {
              window.location.hash = '#' + this.topRecord.recordId
            }
          }, 100)
        }
      }
    },
    mounted () {
      this.user = JSON.parse(sessionStorage.getItem(USER_INFO))
      this.activeChatList = []
      this.activeChatList = this.$store.state.chatData.ChatData.activeChatList
      this.activeChatList.forEach(item => {
        if (item.chatActiveId === this.$route.params.currentId) {
          this.changeChat(item)
        }
      })
    },
    methods: {
      // 切换好友
      handleClick (val) {
        console.log('handleClick', val.item.$attrs.chat.chatName)
        this.changeChat(val.item.$attrs.chat)
      },
      changeChat (val) {
        console.log('changeChat ', val)
        this.currentChatId = val.chatActiveId
        this.currentChatAvatarThump = val.avatarThumb
        this.currentType = this.currentChatId.substring(0, 1)
        this.$store.state.chatData.ChatData.currentChatId = this.currentChatId
        this.current = [this.currentChatId]
        this.openKeys = [this.currentChatId]
        this.currentChatName = this.user.loginId === val.friendId ? '我(' + this.user.name + ')' : val.chatName
        this.topRecord = ''
        this.handleRecordList()
        // 清空输入框数据
        this.input_msg = ''
        // 定义鼠标滚动事件
        setTimeout(() => {
          let recordContent = document.getElementById('recordContent')
          recordContent.addEventListener('scroll', this.scrollListener)
          if (this.currentChatId.indexOf('f') !== -1) {
            // 获取聊天窗口里所有的聊天数据
            this.computeSigned(recordContent.offsetHeight + recordContent.scrollTop, recordContent.scrollTop)
            this.recordElementList = document.getElementById('recordContent').getElementsByClassName('recordItem')
          } else {
            this.sendSignedMsg({groupId: this.currentChatId.substring(1)})
          }
          if (this.topRecord) {
            window.location.hash = '#' + this.topRecord.recordId
          }
        }, 100)
      },
      handleRecordList () {
        if (!this.currentChatId) {
          return
        }
        if (this.currentType === 'f') {
          this.userAvatarMap[this.user.loginId] = this.user.avatarThumb
          this.userAvatarMap[this.currentChatId.substring(1)] = this.currentChatAvatarThump
        } else {
          let groupUserList = this.$store.state.chatData.ChatData.groupIdUserMap[this.currentChatId.substring(1)]
          if (groupUserList) {
            groupUserList.forEach(item2 => {
              this.userAvatarMap[item2.userId] = item2.avatarThumb
              if (item2.userId === this.user.loginId) {
                this.chatInfo.myGroupNickName = item2.nickname + ''
                this.chatInfo.myGroupState = item2.groupState + ''
                this.chatInfo.groupName = this.currentChatName + ''
              }
            })
          }
        }
        let tempList = this.$store.state.chatData.ChatData.chatInfoMap[this.currentChatId]
        if (tempList) {
          if (!this.topRecord) {
            this.topRecord = tempList[0]
          }
          tempList = [...tempList]
          tempList.reverse()
          this.currentChatInfoList = tempList
          if (this.currentType === 'g') {
            let groupId = this.currentChatId.substring(1)
            let groupUserList = this.$store.state.chatData.ChatData.groupIdUserMap[groupId]
            if (!groupUserList) {
              this.sendGroupUserMsg(groupId)
              return
            }
            let has = false
            this.currentChatInfoList.forEach(info => {
              for (let i = 0; i < groupUserList.length; i++) {
                if (info.sendUserId === groupUserList[i].userId) {
                  info.nickname = groupUserList[i].nickname
                  has = true
                  break
                }
              }
            })
            if (!has) {
              this.sendGroupUserMsg(groupId)
            }
          } else {
            this.currentChatInfoList.forEach(info => {
              if (info.sendUserId === this.user.loginId) {
                info.nickname = this.user.nickname
              } else {
                info.nickname = this.currentChatName
              }
            })
          }
        } else {
          this.topRecord = ''
          this.sendGetRecordMsg()
        }
      },
      // 聊天窗口消息滚动事件监听
      scrollListener () {
        let recordContent = document.getElementById('recordContent')
        if (recordContent.scrollTop === 0) {
          // 滚动到顶部 加载历史记录
          console.log('scrollTop', recordContent.scrollTop)
          let tempList = this.$store.state.chatData.ChatData.chatInfoMap[this.currentChatId]
          if (tempList) {
            this.topRecord = tempList[tempList.length - 1]
            if (this.topRecord.recordId !== -1) {
              this.sendGetRecordMsg()
            }
          }
          if (this.currentType === 'f') {
            this.computeSigned(recordContent.scrollHeight - recordContent.offsetHeight, 0)
          }
        } else if (recordContent.offsetHeight + recordContent.scrollTop === recordContent.scrollHeight) {
          console.log('底部')
          // 滚动到底部
          this.topRecord = ''
          if (this.currentType === 'f') {
            this.computeSigned(recordContent.offsetHeight + recordContent.scrollTop, recordContent.scrollTop)
          }
        } else if (this.currentType === 'f' && Math.abs(recordContent.scrollTop - this.scrollTop) >= 35) {
          // 每移动35px距离就统计下已读的数据  只针对好友消息
          this.scrollTop = recordContent.scrollTop
          this.computeSigned(this.scrollTop + recordContent.offsetHeight, this.scrollTop)
        }
      },
      // 计算签收消息
      computeSigned (buttom, top) {
        // 每移动35px距离就统计下已读的数据  只针对好友消息
        let unreadRecordIdList = []
        let sendUserId = ''
        for (let i = 0; i < this.recordElementList.length; i++) {
          let item = this.recordElementList[i]
          // 计算聊天数据是否在可视窗口内
          if (item.id !== '-1' && item.attributes.receiveUserId && item.attributes.receiveUserId.nodeValue === this.user.loginId + '' && item.attributes.receiveState.nodeValue === 1 + '') {
            if ((item.offsetTop - item.offsetHeight <= buttom + 10) && (item.offsetTop - item.offsetHeight >= top)) {
              sendUserId = item.attributes.sendUserId.nodeValue
              unreadRecordIdList.push(item.id)
            }
          }
        }
        // 发送好友签收消息
        if (unreadRecordIdList && unreadRecordIdList.length > 0) {
          console.log('unreadRecordIdList', unreadRecordIdList)
          this.sendSignedMsg({recordIdList: unreadRecordIdList, sendUserId: sendUserId})
        }
      },
      onClose () {
        this.moreVisible = false
      },
      // 发送连接信息
      sendConnectMsg (chatMsgContext) {
        console.log('发送初始连接信息！')
        let chatMsg = {
          recordType: 3,
          contentType: 1
        }
        this.$store.state.chatData.ChatData.ws_send('/chat/connect', chatMsg)
      },
      // 发送获取群组成员信息
      sendGroupUserMsg (groupId) {
        console.log('发送获取群组成员信息！')
        let chatMsg = {
          recordType: 4,
          groupId: groupId
        }
        this.$store.state.chatData.ChatData.ws_send('/chat/getGroupUser', chatMsg)
      },
      // 发送消息签收信息
      sendSignedMsg (signedMsg) {
        console.log('发送消息签收信息！')
        let chatMsg = {
          recordType: 5,
          groupId: signedMsg.groupId,
          recordIdList: signedMsg.recordIdList,
          sendUserId: signedMsg.sendUserId
        }
        this.$store.state.chatData.ChatData.ws_send('/chat/signed', chatMsg)
      },
      // 发送获取历史记录信息
      sendGetRecordMsg () {
        console.log('发送获取历史记录信息！')
        let groupId = ''
        let receiveUserId = ''
        if (this.currentType === 'g') {
          groupId = this.currentChatId.substring(1)
        } else {
          receiveUserId = this.currentChatId.substring(1)
        }
        let sendTime = ''
        let infoList = this.$store.state.chatData.ChatData.chatInfoMap[this.currentChatId]
        if (infoList) {
          sendTime = infoList[infoList.length - 1].sendTime
        }
        let chatMsg = {
          recordType: 6,
          sendTime: sendTime,
          groupId: groupId,
          receiveUserId: receiveUserId
        }
        this.$store.state.chatData.ChatData.ws_send('/chat/getRecord', chatMsg)
      },
      // 发送更新群用户消息信息
      sendUpdateGroupUserMsg (msg) {
        console.log('发送更新群用户信息消息！')
        let chatMsg = {
          recordType: 8,
          groupId: this.currentChatId.substring(1),
          nickname: msg.nickname,
          groupState: msg.groupState
        }
        this.$store.state.chatData.ChatData.ws_send('/chat/updateGroupUser', chatMsg)
      },
      // 发送更新群用户消息信息
      sendUpdateGroupMsg (msg) {
        console.log('发送更新群信息消息！')
        let chatMsg = {
          recordType: 9,
          groupId: this.currentChatId.substring(1),
          groupName: msg.groupName
        }
        this.$store.state.chatData.ChatData.ws_send('/chat/updateGroup', chatMsg)
      },
      // 发送聊天信息
      sendChatMsg (chatMsgContext) {
        if (!chatMsgContext) {
          this.$message.warning('消息内容不能为空')
          return
        }
        if (!this.currentChatId) {
          this.$message.warning('聊天室id不能为空')
          return
        }
        let recordType = 1
        let path = '/chat/addGroupRecord'
        let groupId = ''
        let receiveUserId = ''
        this.topRecord = ''
        if (this.currentType === 'f') {
          recordType = 2
          path = '/chat/addRecord'
          receiveUserId = this.currentChatId.substring(1)
        } else {
          groupId = this.currentChatId.substring(1)
        }
        const chatMsg = {
          recordType: recordType,
          contentType: 1,
          groupId: groupId,
          receiveUserId: receiveUserId,
          content: chatMsgContext
        }
        this.$store.state.chatData.ChatData.ws_send(path, chatMsg)
        this.input_msg = ''
      },
      showChatInfo () {
        this.chatInfo.visible = true
        this.chatInfo.updateName = false
        this.chatInfo.updateGroupNickname = false
        this.chatInfo.myGroupNickname = ''
        this.$store.state.chatData.ChatData.activeChatList.forEach(item => {
          if (item.chatActiveId === this.currentChatId) {
            this.chatInfo.info = item
          }
        })
        this.chatInfo.groupUserList = []
        if (this.currentType === 'g') {
          let userList = this.$store.state.chatData.ChatData.groupIdUserMap[this.currentChatId.substring(1)]
          userList.forEach(item => {
            if (item.state !== -1) {
              this.chatInfo.groupUserList.push(item)
              if (item.userId === this.user.loginId) {
                this.chatInfo.loginUser = item
              }
            }
          })
        }
      },
      // 修改群名称
      updateChatName () {
        this.chatInfo.updateName = false
        this.sendUpdateGroupMsg({groupName: this.chatInfo.groupName})
        this.currentChatName = this.chatInfo.groupName + ''
      },
      // 修改群昵称
      updateGroupNickname () {
        this.chatInfo.updateGroupNickname = false
        this.sendUpdateGroupUserMsg({nickname: this.chatInfo.myGroupNickName})
      },
      // 群消息设置
      updateGroupState () {
        this.chatInfo.updateGroupState = false
        this.sendUpdateGroupUserMsg({groupState: this.chatInfo.myGroupState})
      },
      // 邀请成员
      addGroupUser () {
        this.addUserModal.selectFriendList = []
        this.chatInfo.groupUserList.forEach(item => {
          this.addUserModal.selectFriendList.push(item.userId)
        })
        this.searchMyFriendList()
        this.addUserModal.visible = true
      },
      // 邀请成员
      addGroupUserOk () {
        console.log('发送添加新成员消息！')
        let chatMsg = {
          recordType: 10,
          userList: this.addUserModal.selectFriendList,
          groupId: this.currentChatId.substring(1)
        }
        this.$store.state.chatData.ChatData.ws_send('/chat/addGroupUser', chatMsg)
        this.addUserModal.visible = false
      },
      searchMyFriendList () {
        this.addUserModal.allFriendList = []
        this.http.post('upmsApi', '/myFriend/getMyFriendList', {
          nickname: this.addUserModal.nickname
        }).then((res) => {
          if (res.success) {
            res.rows.forEach(item => {
              item.key = item.friendId
              item.title = item.nickname
              if (this.addUserModal.selectFriendList.indexOf(item.key) !== -1) {
                item.disabled = true
              }
            })
            this.addUserModal.allFriendList = res.rows
          }
          this.addUserModal.loading = false
        })
      },
      // 编辑用户信息
      delGroupUserOk (user) {
        console.log('发送删除成员消息！')
        let chatMsg = {
          recordType: 11,
          userList: [user.userId],
          groupId: this.currentChatId.substring(1)
        }
        this.$store.state.chatData.ChatData.ws_send('/chat/delGroupUser', chatMsg)
        this.editUserModal.visible = false
        this.chatInfo.visible = false
      }
    }
  }
</script>

<style lang="less" scoped>
  .recordItem {
    min-height: 35px;
    margin-top: 10px;
    width: 100%;

    .record-img {
      width: 35px;
      height: 35px;
      margin: 0 5px;
    }

    .record-content {
      max-width: 80%;

      .record-nickname {
        height: 20px;
        line-height: 20px;
        width: 100%;
      }

      .record-txt {
        width: 100%;
        min-height: 35px;
        padding: 5px;
        background-color: #ffffff;
        border-radius: 5px;
      }
    }
  }

  .floatLeft {
    float: left;
    text-align: left;
  }

  .floatRight {
    float: right;
    text-align: right;
  }

  .groupAdmin {
    text-align: right;
    -webkit-transform: translate(0%, -10%);
    transform: translate(0%, -10%);
    -webkit-transform-origin: 0% -10%;
    transform-origin: 0% -10%;
    background-color: #c8c8c8;
    padding: 3px;
    border-radius: 5px;
  }
</style>
