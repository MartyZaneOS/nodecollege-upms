import {USER_INFO} from './Constants'
import axios from 'axios'
import notification from 'ant-design-vue/es/notification'

const ChatData = {
  mySocket: '',
  // 当前登陆人id
  userId: '',
  // 访问令牌
  accessToken: '',
  uuid: '',
  updateTime: new Date().valueOf(),
  debateUpdateTime: new Date().valueOf(),
  // 新消息数量
  newsNum: 0,
  activeChatList: [
    {
      // 活跃聊天id
      chatActiveId: '',
      // 聊天室名称
      chatName: '',
      // 聊天室类型 f-好友聊天， g-群组聊天
      chatType: '',
      // 群id
      groupId: '',
      // 好友id
      friendId: '',
      // 群组类型
      groupType: '',
      // 接收状态
      receiveState: '',
      // 新消息数量
      newsNum: 0,
      // 头像地址
      avatar: '',
      // 头像缩略图地址
      avatarThumb: ''
    }
  ],
  // 当前聊天窗口id
  currentChatId: '',
  // 聊天信息map 活跃聊天id:[消息信息]
  chatInfoMap: {
    // 聊天室id
    // chatActiveId: [
    //   {
    //     // 发送用户id
    //     sendUserId: '',
    //     // 消息内容类型
    //     contentType: '',
    //     // 消息内容
    //     content: '',
    //     // 消息类型
    //     recordType: '',
    //     // 发送时间
    //     sendTime: '',
    //     // 状态 1-正常，2-撤回
    //     state: '',
    //     // 接收状态
    //     receiveState: '',
    //     // 接收用户id
    //     receiveUserId: '',
    //     // 接收时间
    //     receiveTime: '',
    //     // 群组id
    //     groupId: ''
    //   }
    // ]
  },
  // 用户id对应用户昵称及头像
  groupIdUserMap: {
    groupId: [{
      groupUserId: '',
      groupId: '',
      userId: '',
      nickname: '',
      userType: '',
      memberId: '',
      state: '',
      groupState: '',
      recordReadTime: ''
    }]
  },
  // 辩论堂信息
  debateMap: {},
  // 辩论堂记录
  debateRecordMap: {
    debateId: []
  },
  // 我点赞的信息
  debateRecordMyUpMap: {},
  // 用户信息map
  userMap: {},
  // 初始化ws
  async initWebsocket () {
    let baseConfig = JSON.parse(sessionStorage.getItem('BASE_CONFIG'))
    let ws = ''
    if (!baseConfig) {
      await axios.post('/chatApi/getWebsocketUrl', '{}').then((result) => {
        sessionStorage.setItem('BASE_CONFIG', JSON.stringify(result.data.rows[0]))
        baseConfig = result.data.rows[0]
      }).catch((error) => {
        console.log('get baseConfig error...' + error)
      })
    }
    ws = baseConfig.chatWs
    if (ChatData.mySocket !== null && ChatData.mySocket !== undefined && ChatData.mySocket.readyState === WebSocket.OPEN) {
      return
    }
    ChatData.mySocket = new WebSocket(ws)
    ChatData.mySocket.onopen = ChatData.ws_onopen
    ChatData.mySocket.onerror = ChatData.ws_onerror
    ChatData.mySocket.onmessage = ChatData.ws_onmessage
    ChatData.mySocket.onclose = ChatData.ws_onclose
    let user = JSON.parse(sessionStorage.getItem(USER_INFO))
    ChatData.accessToken = user.accessToken
    ChatData.uuid = user.uuid
    ChatData.userId = user.loginId
  },
  // 发送消息
  ws_send (path, chatMsg) {
    // 如果当前websocket的状态是已打开，则直接发送， 否则重连
    if (ChatData.mySocket !== null && ChatData.mySocket !== undefined && ChatData.mySocket.readyState === WebSocket.OPEN) {
      ChatData.checkDate()
      console.log('消息路径 ' + path + ' 消息内容 >>>>', chatMsg)
      let sendContent = {
        path: path,
        data: JSON.stringify(chatMsg),
        accessToken: ChatData.accessToken,
        uuid: ChatData.uuid
      }
      ChatData.mySocket.send(JSON.stringify(sendContent))
    } else {
      // 重连websocket
      setTimeout(function () {
        ChatData.initWebsocket()
        ChatData.ws_send(path, chatMsg)
      }, 1000)
    }
  },
  // 接收消息
  ws_onmessage (e) {
    console.log('接收消息: ' + e.data)
    // 初次连接返回活跃聊天室列表
    let res = JSON.parse(e.data)
    let result = res.data
    if (!result.success) {
      notification.error({
        message: result.message
      })
      return
    }
    if (res.path.indexOf('/debate') >= 0) {
      ChatData.debateOnMessage(res)
      return
    }
    if (res.path === '/chat/addGroupRecord' || res.path === '/chat/addRecord') {
      // 1 群组消息
      // 2 好友消息
      let record = result.rows[0]
      let chatActiveId = ''
      let receiveUserId = ''
      if (record.recordType === 1) {
        chatActiveId = 'g' + record.groupId
      } else if (record.recordType === 2) {
        if (ChatData.userId === record.receiveUserId) {
          chatActiveId = 'f' + record.sendUserId
          receiveUserId = record.sendUserId
        } else if (ChatData.userId === record.sendUserId) {
          chatActiveId = 'f' + record.receiveUserId
          receiveUserId = record.receiveUserId
        }
      }
      if (!ChatData.chatInfoMap[chatActiveId]) {
        // 无数据 去请求历史数据
        let chatMsg = {
          recordType: 6,
          sendTime: '',
          groupId: record.groupId,
          receiveUserId: receiveUserId
        }
        ChatData.ws_send('/chat/getRecord', chatMsg)
      }
      ChatData.updateChatInfoMap(record)
    } else if (res.path === '/chat/connect') {
      // 活跃列表消息
      ChatData.initActiveChat(result.rows)
    } else if (res.path === '/chat/getGroupUser') {
      // 获取群组用户消息
      if (result.rows) {
        let groupId = result.rows[0].groupId
        ChatData.groupIdUserMap[groupId] = result.rows
      }
    } else if (res.path === '/chat/signed') {
      // 获取群组用户消息
      if (result.rows) {
        let groupId = result.rows[0].groupId
        let chatActiveId = 'g' + groupId
        if (!groupId) {
          ChatData.updateChatInfoMap(result.rows[0])
          chatActiveId = 'f' + result.rows[0].sendUserId
        }
        ChatData.activeChatList.forEach(item2 => {
          if (item2.chatActiveId === chatActiveId) {
            ChatData.newsNum = ChatData.newsNum - item2.newsNum
            item2.newsNum = 0
          }
        })
      }
    } else if (res.path === '/chat/getRecord') {
      // 历史记录消息
      result.rows.forEach(item => {
        ChatData.updateChatInfoMap(item)
      })
    } else if (res.path === '/chat/unreadNum') {
      // 历史记录消息]
      let num = 0
      result.rows.forEach(item => {
        ChatData.activeChatList.forEach(item2 => {
          if (item.chatActiveId === item2.chatActiveId) {
            item2.newsNum = item2.newsNum + item.newsNum
            num = num + item2.newsNum
          }
        })
      })
      ChatData.newsNum = ChatData.newsNum + num
    } else if (res.path === '/chat/updateGroupUser') {
      // 群用户更新
      let groupUser = result.rows[0]
      let userList = ChatData.groupIdUserMap[groupUser.groupId]
      if (userList) {
        let has = false
        for (let i = 0; i < userList.length; i++) {
          if (userList[i].userId === groupUser.userId) {
            userList[i] = groupUser
            has = true
            break
          }
        }
        if (!has) {
          userList.push(groupUser)
        }
        ChatData.groupIdUserMap[groupUser.groupId] = userList
      }
    } else if (res.path === '/chat/updateGroup') {
      // 群用户更新
      let group = result.rows[0]
      let chatList = ChatData.activeChatList
      if (chatList) {
        for (let i = 0; i < chatList.length; i++) {
          if (chatList[i].groupId === group.groupId) {
            chatList[i].chatName = group.groupName
            break
          }
        }
        ChatData.activeChatList = chatList
      }
    }
    ChatData.updateTime = new Date().valueOf()
  },
  ws_onopen () {
    console.log('连接服务器 成功...')
    console.log('发送初始连接信息！')
    let chatMsg = {
      recordType: 3,
      contentType: 1
    }
    ChatData.ws_send('/chat/connect', chatMsg)
  },
  ws_onerror () {
    console.log('连接服务器 错误...')
  },
  ws_onclose () {
    console.log('连接服务器 关闭...')
  },
  out () {
    console.log('主动退出')
    if (ChatData && ChatData.mySocket) {
      ChatData.mySocket.close()
    }
    ChatData.activeChatList = []
    ChatData.chatInfoMap = {}
    ChatData.currentChatId = ''
    ChatData.updateTime = ''
    ChatData.userId = ''
    ChatData.groupIdUserMap = {}
  },
  // 初始化活跃聊天室
  initActiveChat (activeChatList) {
    for (let chat of activeChatList) {
      if (chat.friendId === ChatData.userId) {
        let user = JSON.parse(sessionStorage.getItem(USER_INFO))
        if (user.avatar) {
          chat.avatar = user.avatar
          chat.avatarThumb = user.avatarThumb
        }
      }
    }
    ChatData.activeChatList = []
    ChatData.activeChatList = activeChatList
  },
  // 添加新的活跃聊天室
  setNewActiveChatList (chat) {
    let chatActiveId = chat.chatActiveId
    let activeChatList = ChatData.activeChatList
    let activeList = [{
      // 活跃聊天id
      chatActiveId: chat.chatActiveId,
      // 聊天室名称
      chatName: chat.chatName,
      // 群id
      groupId: '',
      // 好友id
      friendId: chat.friendId,
      // 群组类型
      groupType: '',
      // 接收状态
      receiveState: ''
    }]
    for (let i = 0; i < activeChatList.length; i++) {
      if (activeChatList[i].chatActiveId !== chatActiveId) {
        activeList.push(activeChatList[i])
      } else {
        activeList[0] = activeChatList[i]
      }
    }
    ChatData.activeChatList = activeList
    ChatData.currentChatId = chatActiveId
    ChatData.updateTime = new Date().valueOf()
  },
  // 更新聊天信息数据 撤销\已读状态更新
  updateChatInfoMap (record) {
    ChatData.checkDate()
    // 获取活跃聊天室id
    let chatActiveId = ''
    if (record.recordType === 1) {
      chatActiveId = 'g' + record.groupId
    } else if (record.recordType === 2) {
      if (ChatData.userId === record.receiveUserId) {
        chatActiveId = 'f' + record.sendUserId
        record.friendId = record.sendUserId
      } else if (ChatData.userId === record.sendUserId) {
        chatActiveId = 'f' + record.receiveUserId
        record.friendId = record.receiveUserId
      } else {
        console.log('不是我的消息！')
        console.log('ChatData.userId', ChatData.userId)
        console.log('record.sendUserId', record.sendUserId)
        console.log('record.receiveUserId', record.receiveUserId)
        return
      }
    }
    record.chatActiveId = chatActiveId
    // 更新活跃聊天室顺序
    ChatData.updateActiveList(record)
    // 更新消息记录
    let infoList = ChatData.chatInfoMap[chatActiveId]
    if (!infoList) {
      // 数据空的时候
      let list = []
      list.push(record)
      ChatData.chatInfoMap[chatActiveId] = []
      ChatData.chatInfoMap[chatActiveId] = [...list]
      ChatData.activeChatList.forEach(item2 => {
        if (chatActiveId === item2.chatActiveId && record.sendUserId !== ChatData.userId && ChatData.currentChatId !== chatActiveId) {
          item2.newsNum++
          ChatData.newsNum++
        }
      })
    } else if (record.sendTime > infoList[0].sendTime) {
      // 数据不为空，且是新加数据
      let list = []
      list.push(record)
      list.push(...infoList)
      ChatData.chatInfoMap[chatActiveId] = []
      ChatData.chatInfoMap[chatActiveId] = [...list]
      ChatData.activeChatList.forEach(item2 => {
        if (chatActiveId === item2.chatActiveId && record.sendUserId !== ChatData.userId && ChatData.currentChatId !== chatActiveId) {
          item2.newsNum++
          ChatData.newsNum++
        }
      })
    } else if (record.sendTime >= infoList[infoList.length - 1].sendTime) {
      // 数据不为空，且属于更新数据
      for (let i = 0; i < infoList.length; i++) {
        if (record.recordId === infoList[i].recordId) {
          infoList[i] = record
        }
      }
    } else {
      // 历史数据
      infoList.push(record)
    }
  },
  // 更新活跃聊天室顺序
  updateActiveList (record) {
    let newRecordList = ChatData.chatInfoMap[ChatData.activeChatList[0].chatActiveId]
    if (newRecordList && newRecordList.length > 0 && newRecordList[0].sendTime < record.sendTime) {
      if (ChatData.activeChatList[0].chatActiveId !== record.chatActiveId) {
        let tmpList = []
        tmpList[0] = record
        for (let i = 0; i < ChatData.activeChatList.length; i++) {
          if (ChatData.activeChatList[i].chatActiveId !== record.chatActiveId) {
            tmpList.push(ChatData.activeChatList[i])
          } else {
            tmpList[0] = ChatData.activeChatList[i]
          }
        }
        tmpList[0].newsNum++
        ChatData.newsNum++
        ChatData.activeChatList = []
        ChatData.activeChatList = tmpList
      } else {
        ChatData.activeChatList[0].receiveState = record.receiveState
      }
    }
  },
  // 校验当前用户数据
  checkDate () {
    if (!ChatData.userId) {
      let user = JSON.parse(sessionStorage.getItem(USER_INFO))
      ChatData.accessToken = user.accessToken
      ChatData.uuid = user.uuid
      ChatData.userId = user.loginId
    }
    if (!ChatData.mySocket) {
      ChatData.initWebsocket()
    }
  },
  // 辩论堂数据处理
  debateOnMessage (res) {
    let result = res.data
    if (res.path === '/debate/getRecordList') {
      // 获取辩论堂消息
      if (result.rows == null || result.rows.length === 0) {
        return
      }
      ChatData.checkUser(result.rows)
      if (!ChatData.debateRecordMap[result.rows[0].debateId]) {
        ChatData.debateRecordMap[result.rows[0].debateId] = []
      }
      let old = [...ChatData.debateRecordMap[result.rows[0].debateId]]
      result.rows.forEach(item => {
        if (old.length === 0 || item.sendTime > old[old.length - 1].sendTime) {
          old.push(item)
        } else if (item.sendTime < old[0].sendTime) {
          old.unshift(item)
        } else {
          let leftTime = old[0].sendTime
          for (let i = 0; i < old.length; i++) {
            let item2 = old[i]
            if (item2.debateRecordId === item.debateRecordId) {
              // 更新数据
              item2 = item
              break
            } else if (item.sendTime < item2.sendTime && item.sendTime > leftTime) {
              old.splice(i, 0, item)
              break
            }
            leftTime = item2.sendTime
          }
        }
      })
      ChatData.debateRecordMap[result.rows[0].debateId] = old
    } else if (res.path === '/debate/getUserInfo') {
      result.rows.forEach(item => {
        ChatData.userMap[item.userId] = item
      })
    } else if (res.path === '/debate/getUpUserList') {
      if (!result.rows || result.rows.length === 0) {
        return
      }
      let old = ChatData.debateRecordMap[result.rows[0].debateId]
      if (!old) {
        return
      }
      for (let i = 0; i < old.length; i++) {
        if (old[i].debateRecordId === result.rows[0].debateRecordId) {
          // 更新数据
          result.rows.forEach(item => {
            old[i].supportUser[item.userId] = item
          })
          break
        }
      }
    } else if (res.path === '/debate/updateDebateSupport') {
      ChatData.debateMap[result.rows[0].debateId].supportNum = result.rows[0].supportNum
      ChatData.debateMap[result.rows[0].debateId].refuseNum = result.rows[0].refuseNum
      ChatData.debateMap[result.rows[0].debateId].endTime = result.rows[0].endTime
      ChatData.debateMap[result.rows[0].debateId].result = result.rows[0].result
      ChatData.debateMap[result.rows[0].debateId].resultMsg = result.rows[0].resultMsg
    } else if (res.path === '/debate/recordUp') {
      let old = ChatData.debateRecordMap[result.rows[0].debateId]
      if (!old) {
        return
      }
      result.rows.forEach(item => {
        for (let i = 0; i < old.length; i++) {
          if (old[i].debateRecordId === item.debateRecordId) {
            // 更新数据
            old[i].supportNum = old[i].supportNum + 1
            if (item.userId === ChatData.userId) {
              ChatData.debateRecordMyUpMap[item.debateId][item.debateRecordId] = item.debateRecordId
            }
            break
          }
        }
      })
    } else if (res.path === '/debate/delRecordUp') {
      let old = ChatData.debateRecordMap[result.rows[0].debateId]
      if (!old) {
        return
      }
      result.rows.forEach(item => {
        for (let i = 0; i < old.length; i++) {
          if (old[i].debateRecordId === item.debateRecordId) {
            // 更新数据
            old[i].supportNum = old[i].supportNum - 1
            if (old[i].supportUser && old[i].supportUser[item.userId]) {
              old[i].supportUser[item.userId] = null
            }
            if (item.userId === ChatData.userId) {
              ChatData.debateRecordMyUpMap[item.debateId][item.debateRecordId] = null
            }
            break
          }
        }
      })
    } else if (res.path === '/debate/getMyUpList') {
      let old = ChatData.debateRecordMap[result.rows[0].debateId]
      if (!old) {
        return
      }
      let upList = []
      result.rows.forEach(item => {
        upList[item.debateRecordId] = item.debateRecordId
      })
      ChatData.debateRecordMyUpMap[result.rows[0].debateId] = upList
    }
    ChatData.debateUpdateTime = new Date().valueOf()
  },
  checkUser (rows) {
    const userIds = new Set()
    rows.forEach(item => {
      if (!ChatData.userMap[item.userId]) {
        userIds.add(item.userId)
      }
    })
    if (userIds.size > 0) {
      let msg = {
        longList: [...userIds]
      }
      ChatData.ws_send('/debate/getUserInfo', msg)
    }
  }
}

export default {
  ChatData
}
