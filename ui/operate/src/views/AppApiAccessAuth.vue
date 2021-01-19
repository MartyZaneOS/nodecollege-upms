<template>
  <a-row style="padding: 10px;">
    <a-col span="24">
      <a-form layout="inline">
        <a-form-item label="接口url">
          <a-input v-model="search.apiUrl" placeholder="接口url！" />
        </a-form-item>
        <a-form-item label="服务名称">
          <a-input v-model="search.applicationName" placeholder="服务名称！" />
        </a-form-item>
        <a-form-item label="控制类">
          <a-input v-model="search.controllerName" placeholder="控制类！" />
        </a-form-item>
        <a-form-item label="访问授权">
          <a-select allowClear v-model="search.accessAuth" placeholder="访问授权！" style="width: 150px;">
            <a-select-option value="none">无限制</a-select-option>
            <a-select-option value="inner">内部调用</a-select-option>
            <a-select-option value="adminLogin">管理员登录</a-select-option>
            <a-select-option value="adminAuth">管理员授权</a-select-option>
            <a-select-option value="userLogin">用户登录</a-select-option>
            <a-select-option value="userAuth">用户授权</a-select-option>
            <a-select-option value="memberLogin">租户成员登录</a-select-option>
            <a-select-option value="memberAuth">租户成员授权</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="状态">
          <a-select allowClear v-model="search.state" placeholder="状态！" style="width: 150px;">
            <a-select-option value="1">正常</a-select-option>
            <a-select-option value="2">后期删除</a-select-option>
            <a-select-option value="-1">不可用</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="init" :disabled="disabled">查询</a-button>
        </a-form-item>
      </a-form>
    </a-col>
    <a-col span="24">
      <a-form layout="inline">
        <a-form-item>
          <a-button type="primary" @click="syncAllAppApiAccessAuth" :disabled="disabled">刷新产品授权限制信息</a-button>
        </a-form-item>
      </a-form>
    </a-col>
    <a-col span="24">
      <a-table :dataSource="data" :columns="columns" :pagination="pagination"
               :loading="loading" :rowSelection="rowSelection"
               size="small" bordered
               :rowKey="record => (record.apiId)" :scroll="{ x: 1800 }">
        <template slot="operation" slot-scope="text, record, index">
          <a @click="() => editModal(record)">访问授权</a>
          <a v-if="record.state == -1" @click="() => delApi(record)">删除</a>
          <a @click="() => visit(record)">访问统计</a>
        </template>
      </a-table>
      <NCPagination :page="pageObj" @changePage="(page)=>{this.pageObj = page}" @searchList="searchList"/>
    </a-col>
    <a-modal v-model="addOrUpdateModal.visible" :title="addOrUpdateModal.title">
      <a-form :label-col="addOrUpdateModal.labelCol" :wrapper-col="addOrUpdateModal.wrapperCol">
        <a-row>
          <a-col span="12">
            <a-form-item label="无限制">
              <a-switch :disabled="addOrUpdateModal.disabled.none" v-model="addOrUpdateModal.data.none"
                        @change="noneChange"></a-switch>
            </a-form-item>
          </a-col>
          <a-col span="12">
            <a-form-item label="内部调用">
              <a-switch :disabled="addOrUpdateModal.disabled.inner" v-model="addOrUpdateModal.data.inner"
                        @change="innerChange"></a-switch>
            </a-form-item>
          </a-col>
          <a-col span="12">
            <a-form-item label="管理员登录">
              <a-switch v-model="addOrUpdateModal.data.adminLogin" @change="adminLoginChange"></a-switch>
            </a-form-item>
          </a-col>
          <a-col span="12">
            <a-form-item label="管理员授权">
              <a-switch disabled v-model="addOrUpdateModal.data.adminAuth"></a-switch>
            </a-form-item>
          </a-col>
          <a-col span="12">
            <a-form-item label="用户登录">
              <a-switch v-model="addOrUpdateModal.data.userLogin" @change="userLoginChange"></a-switch>
            </a-form-item>
          </a-col>
          <a-col span="12">
            <a-form-item label="用户授权">
              <a-switch disabled v-model="addOrUpdateModal.data.userAuth"></a-switch>
            </a-form-item>
          </a-col>
          <a-col span="12">
            <a-form-item label="租户成员登录">
              <a-switch v-model="addOrUpdateModal.data.memberLogin" @change="memberLoginChange"></a-switch>
            </a-form-item>
          </a-col>
          <a-col span="12">
            <a-form-item label="租户成员授权">
              <a-switch disabled v-model="addOrUpdateModal.data.memberAuth"></a-switch>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="addOrUpdateModal.loading" @click="addOrUpdateOk">
          确定
        </a-button>
        <a-button key="back" @click="addOrUpdateModal.visible = false">取消</a-button>
      </template>
    </a-modal>
  </a-row>
</template>

<script>
  import * as operateApi from '../api/operate'
  import {NCPagination} from 'common'

  export default {
    name: 'AppApiAccessAuth',
    components: {
      NCPagination
    },
    data () {
      return {
        search: {
          apiUrl: undefined,
          applicationName: undefined,
          controllerName: undefined,
          accessAuth: undefined,
          state: undefined
        },
        pageObj: {total: 10, pageSize: 10, pageNum: 1},
        pagination: false,
        loading: false,
        disabled: false,
        data: [],
        rowSelection: {
          selectedRowKeys: [],
          onChange: (selectedRowKeys, selectedRows) => {
            this.rowSelection.selectedRowKeys = selectedRowKeys
          }
        },
        columns: [
          {title: '接口url', dataIndex: 'apiUrl', width: 250, fixed: 'left'},
          {title: '服务名称', dataIndex: 'applicationName', width: 150},
          {title: '控制类', dataIndex: 'controllerName'},
          {title: '模块名称', dataIndex: 'modularName'},
          {
            title: '访问授权',
            dataIndex: 'accessAuth',
            key: 'accessAuth',
            customRender: (text, record, index) => {
              let accessAuth = text.split(',')
              let returnStr = ''
              accessAuth.forEach(item => {
                if (item === 'adminAuth') {
                  returnStr = returnStr + ' 管理员授权'
                }
                if (item === 'adminLogin') {
                  returnStr = returnStr + ' 管理员登录'
                }
                if (item === 'userAuth') {
                  returnStr = returnStr + ' 用户授权'
                }
                if (item === 'userLogin') {
                  returnStr = returnStr + ' 用户登录'
                }
                if (item === 'memberAuth') {
                  returnStr = returnStr + ' 租户成员授权'
                }
                if (item === 'memberLogin') {
                  returnStr = returnStr + ' 租户成员登录'
                }
                if (item === 'inner') {
                  returnStr = returnStr + ' 内部调用'
                }
                if (item === 'none') {
                  returnStr = returnStr + ' 无限制'
                }
              })
              return returnStr
            }
          },
          {title: '状态', dataIndex: 'state', width: 80, customRender: (text, record, index) => { return text === -1 ? '不可用' : text === 1 ? '正常' : text === 2 ? '后期删除' : '' }},
          {title: '描述', dataIndex: 'description'},
          {title: '操作', width: 150, fixed: 'right', scopedSlots: {customRender: 'operation'}}
        ],
        addOrUpdateModal: {
          labelCol: {span: 12},
          wrapperCol: {span: 12},
          loading: false,
          visible: false,
          apiData: {},
          data: {
            none: false,
            inner: false,
            adminAuth: false,
            adminLogin: false,
            userAuth: false,
            userLogin: false,
            memberAuth: false,
            memberLogin: false
          },
          disabled: {
            none: false,
            inner: false
          }
        }
      }
    },
    mounted () {
      this.init()
    },
    methods: {
      init () {
        this.rowSelection.selectedRowKeys = []
        this.searchList()
      },
      // 查询需授权访问接口信息
      searchList () {
        this.loading = true
        operateApi.getAppApiList({...this.pageObj, data: {...this.search}}).then((res) => {
          if (res.success) {
            this.data = res.rows
            this.pageObj.total = res.total
          }
          this.loading = false
          this.disabled = false
        })
      },
      // 查询不需要登录就能访问的接口信息
      syncAllAppApiAccessAuth () {
        this.disabled = true
        operateApi.syncAllAppApiAccessAuth({}).then((res) => {
          if (res.success) {
            this.$message.info(res.message)
            this.init()
          }
        })
      },
      editModal (record) {
        this.addOrUpdateModal.data = {
          none: false,
          inner: false,
          adminAuth: false,
          adminLogin: false,
          userAuth: false,
          userLogin: false,
          memberAuth: false,
          memberLogin: false
        }
        this.addOrUpdateModal.disabled.none = false
        this.addOrUpdateModal.disabled.inner = false
        this.addOrUpdateModal.apiData = record
        let accessAuth = record.accessAuth.split(',')
        accessAuth.forEach(item => {
          if (item === 'adminAuth') {
            this.addOrUpdateModal.data.adminAuth = true
            this.addOrUpdateModal.disabled.none = true
            this.addOrUpdateModal.disabled.inner = true
          }
          if (item === 'adminLogin') {
            this.addOrUpdateModal.data.adminLogin = true
            this.addOrUpdateModal.disabled.none = true
            this.addOrUpdateModal.disabled.inner = true
          }
          if (item === 'userAuth') {
            this.addOrUpdateModal.data.userAuth = true
            this.addOrUpdateModal.disabled.none = true
            this.addOrUpdateModal.disabled.inner = true
          }
          if (item === 'userLogin') {
            this.addOrUpdateModal.data.userLogin = true
            this.addOrUpdateModal.disabled.none = true
            this.addOrUpdateModal.disabled.inner = true
          }
          if (item === 'memberAuth') {
            this.addOrUpdateModal.data.memberAuth = true
            this.addOrUpdateModal.disabled.none = true
            this.addOrUpdateModal.disabled.inner = true
          }
          if (item === 'memberLogin') {
            this.addOrUpdateModal.data.memberLogin = true
            this.addOrUpdateModal.disabled.none = true
            this.addOrUpdateModal.disabled.inner = true
          }
          if (item === 'inner') {
            this.addOrUpdateModal.data.inner = true
          }
          if (item === 'none') {
            this.addOrUpdateModal.data.none = true
          }
        })
        this.addOrUpdateModal.visible = true
      },
      addOrUpdateOk () {
        let accessAuth = []
        if (this.addOrUpdateModal.data.adminAuth) {
          accessAuth.push('adminAuth')
        }
        if (this.addOrUpdateModal.data.adminLogin) {
          accessAuth.push('adminLogin')
        }
        if (this.addOrUpdateModal.data.userAuth) {
          accessAuth.push('userAuth')
        }
        if (this.addOrUpdateModal.data.userLogin) {
          accessAuth.push('userLogin')
        }
        if (this.addOrUpdateModal.data.memberAuth) {
          accessAuth.push('memberAuth')
        }
        if (this.addOrUpdateModal.data.memberLogin) {
          accessAuth.push('memberLogin')
        }
        if (this.addOrUpdateModal.data.inner) {
          accessAuth.push('inner')
        }
        if (this.addOrUpdateModal.data.none) {
          accessAuth.push('none')
        }
        operateApi.updateAppApiAccessAuth({
          apiId: this.addOrUpdateModal.apiData.apiId,
          accessAuth: accessAuth.join(',')
        }).then((res) => {
          if (res.success) {
            this.$message.info(res.message)
            this.init()
            this.addOrUpdateModal.visible = false
          }
        })
      },
      noneChange (flag) {
        this.addOrUpdateModal.data.none = flag
        this.addOrUpdateModal.data.inner = !flag
        this.addOrUpdateModal.data.adminLogin = false
        this.addOrUpdateModal.data.userLogin = false
        this.addOrUpdateModal.data.memberLogin = false
      },
      innerChange (flag) {
        this.addOrUpdateModal.data.inner = flag
        this.addOrUpdateModal.data.none = !flag
        this.addOrUpdateModal.data.adminLogin = false
        this.addOrUpdateModal.data.userLogin = false
        this.addOrUpdateModal.data.memberLogin = false
      },
      adminLoginChange (flag) {
        const data = this.addOrUpdateModal.data
        data.adminLogin = flag
        if (data.adminLogin || data.adminAuth || data.memberLogin || data.memberAuth || data.userLogin || data.userAuth) {
          data.inner = false
          data.none = false
        } else {
          data.inner = true
          data.none = false
        }
      },
      userLoginChange (flag) {
        const data = this.addOrUpdateModal.data
        data.userLogin = flag
        if (data.adminLogin || data.adminAuth || data.memberLogin || data.memberAuth || data.userLogin || data.userAuth) {
          data.inner = false
          data.none = false
        } else {
          data.inner = true
          data.none = false
        }
      },
      memberLoginChange (flag) {
        const data = this.addOrUpdateModal.data
        data.memberLogin = flag
        if (data.adminLogin || data.adminAuth || data.memberLogin || data.memberAuth || data.userLogin || data.userAuth) {
          data.inner = false
          data.none = false
        } else {
          data.inner = true
          data.none = false
        }
      },
      // 删除管理员
      delApi (record) {
        if (record.state !== -1) {
          this.$message.error('该接口不能删除！')
          return
        }
        const that = this
        this.$confirm({
          title: '删除接口！?',
          onOk () {
            return new Promise((resolve, reject) => {
              console.log('删除')
              operateApi.delAppApi({apiId: record.apiId}).then((res) => {
                if (res.success) {
                  that.$message.info(res.message)
                  that.searchList()
                  resolve(res.message)
                } else {
                  reject(res.message)
                }
              })
            })
          }
        })
      },
      visit (record) {
        document.location.href = '/operate/visitCount?appName=' + record.applicationName + '&controllerName=' + record.controllerName + '&apiUrl=' + record.apiUrl
      }
    }
  }
</script>

<style scoped>

</style>
