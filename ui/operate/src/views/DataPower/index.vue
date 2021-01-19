<template>
  <a-row style="padding: 10px;">
    <a-col span="24">
      <a-card title="数据权限" size="small">
        <a-button type="primary" @click="addDataPower" slot="extra">新增数据权限</a-button>
        <a-table :dataSource="data" :columns="columns" :pagination="pagination" :loading="loading"
                 size="small" bordered :rowKey="record => record.id" :customRow="onClick" :rowSelection="rowSelection">
          <template slot="operation" slot-scope="text, record, index">
            <span>
              <a @click="() => editDataPower(record)">编辑</a>
            </span>
          </template>
        </a-table>
        <NCPagination :page="pageObj" @changePage="(page)=>{this.pageObj = page}" @searchList="searchList"/>
      </a-card>
    </a-col>
    <a-col :span="rowSelectData.dataPowerType === 0 ? 24 : 8">
      <a-card :title="rowSelectData.dataPowerType === 0 ? '机构授权信息' : '机构信息'" size="small">
        <a-table :dataSource="org.data" :columns="org.columns" :pagination="pagination" :loading="org.loading"
                 size="small" bordered :rowKey="record => record.id" :customRow="orgOnClick" :rowSelection="org.rowSelection">
          <template slot="operation" slot-scope="text, record, index">
            <span>
              <a v-if="!record.authId" @click="() => addAuth(record)">授权</a>
              <a v-if="record.authId" @click="() => editAuth(record)">更新</a>
              <a v-if="record.authId" @click="() => delAuth(record)">解除</a>
            </span>
          </template>
        </a-table>
      </a-card>
    </a-col>
    <a-col :span="rowSelectData.dataPowerType === 0 ? 0 : 16">
      <a-card title="机构成员授权信息" size="small">
        <a-table :dataSource="admin.data" :columns="admin.columns" :pagination="pagination" :loading="admin.loading"
                 size="small" bordered :rowKey="record => record.adminId">
          <template slot="operation" slot-scope="text, record, index">
            <span>
              <a v-if="!record.auth" @click="() => addAuth(record)">授权</a>
              <a v-if="record.auth" @click="() => editAuth(record)">更新</a>
              <a v-if="record.auth" @click="() => delAuth(record)">解除</a>
            </span>
          </template>
        </a-table>
        <NCPagination :page="admin.pageObj" @changePage="(page)=>{this.admin.pageObj = page}" @searchList="searchList"/>
      </a-card>
    </a-col>
    <a-modal v-model="model.visible" title="添加数据权限">
      <a-form :form="model.form" :label-col="model.labelCol" :wrapper-col="model.wrapperCol">
        <a-form-item label="数据权限用途">
          <a-select placeholder="请选择数据权限用途！" v-decorator="['dataPowerUsage', {rules: [{ required: true, message: '请选择数据权限用途！'}]}]">
            <a-select-option value="0">运营/运维</a-select-option>
            <a-select-option value="1">2C</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="数据权限名称">
          <a-input placeholder="请输入数据权限名称！" v-decorator="['dataPowerName', {rules: [{ required: true, message: '请输入数据权限名称！'}]}]"/>
        </a-form-item>
        <a-form-item label="数据权限代码">
          <a-input placeholder="请输入数据权限代码！" v-decorator="['dataPowerCode', {rules: [{ required: true, message: '请输入数据权限代码！'}]}]"/>
        </a-form-item>
        <a-form-item label="数据权限类型">
          <a-select placeholder="请选择数据权限类型！" v-decorator="['dataPowerType', {rules: [{ required: true, message: '请选择数据权限类型！'}]}]">
            <a-select-option value="0">机构</a-select-option>
            <a-select-option value="1">机构成员</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="数据权限选项">
          <a-textarea :rows="4" placeholder="请输入数据权限选项！" v-decorator="['dataOption']"/>
        </a-form-item>
      </a-form>
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="model.loading" @click="addOk">
          确定
        </a-button>
        <a-button key="back" @click="model.visible = false">取消</a-button>
      </template>
    </a-modal>
    <a-modal v-model="editModel.visible" title="编辑数据权限">
      <a-form :form="editModel.form" :label-col="model.labelCol" :wrapper-col="model.wrapperCol">
        <a-form-item label="数据权限名称">
          <a-input placeholder="请输入数据权限名称！" v-decorator="['dataPowerName', {rules: [{ required: true, message: '请输入数据权限名称！'}]}]"/>
        </a-form-item>
        <a-form-item label="数据权限选项">
          <a-textarea :rows="4" placeholder="请输入数据权限选项！" v-decorator="['dataOption']"/>
        </a-form-item>
      </a-form>
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="editModel.loading" @click="editOk">
          确定
        </a-button>
        <a-button key="back" @click="editModel.visible = false">取消</a-button>
      </template>
    </a-modal>
    <!-- 添加编辑授权 -->
    <a-modal v-model="authModel.visible" title="授权信息">
      <a-form :label-col="model.labelCol" :wrapper-col="model.wrapperCol">
        <a-form-item label="所有数据权限">
          <a-switch v-model="authModel.data.allData"/>
        </a-form-item>
        <a-form-item label="数据权限选项" v-if="!authModel.data.allData">
          <a-tree-select
              v-model="authModel.data.authList"
              style="width: 100%"
              multiple
              :tree-data="authModel.optionData"
              placeholder="Please select"
              tree-default-expand-all
          >
          </a-tree-select>
        </a-form-item>
      </a-form>
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="editModel.loading" @click="addOrEditAuthOk">
          确定
        </a-button>
        <a-button key="back" @click="editModel.visible = false">取消</a-button>
      </template>
    </a-modal>
  </a-row>
</template>

<script>
  import {NCPagination} from 'common'
  import * as operateApi from '../../api/operate'

  export default {
    name: 'Auth',
    components: {
      NCPagination
    },
    data () {
      return {
        pageObj: {total: 10, pageSize: 10, pageNum: 1},
        loading: false,
        data: [],
        pagination: false,
        columns: [
          {title: '数据权限用途', dataIndex: 'dataPowerUsage', customRender: (text, record, index) => { return text === 0 ? '运营/运维' : '2C' }},
          {title: '数据权限名称', dataIndex: 'dataPowerName'},
          {title: '数据权限代码', dataIndex: 'dataPowerCode'},
          {title: '数据权限类型', dataIndex: 'dataPowerType', customRender: (text, record, index) => { return text === 0 ? '机构' : '机构成员' }},
          {title: '数据选项', dataIndex: 'dataOption'},
          {title: '操作', fixed: 'right', width: 50, scopedSlots: {customRender: 'operation'}}
        ],
        rowSelection: {
          selectedRowKeys: [],
          type: 'radio',
          onChange: (selectedRowKeys, selectedRows) => {
            this.rowSelectData = selectedRows[0]
            this.rowSelection.selectedRowKeys = selectedRowKeys
          }
        },
        rowSelectData: {},
        authData: [],
        authOrgMap: [],
        authUserMap: [],
        org: {
          loading: false,
          sourceData: [],
          data: [],
          baseColumns: [
            {title: '机构名称', dataIndex: 'title'},
            {title: '机构代码', dataIndex: 'value'}
          ],
          authOrgColumns: [
            {title: '授权', dataIndex: 'authId', customRender: (text, record, index) => { return text ? '是' : '否' }},
            {title: '所有数据', dataIndex: 'allData', customRender: (text, record, index) => { return text ? '是' : '否' }},
            {title: '授权数据', dataIndex: 'authList', customRender: (text, record, index) => { return record.allData ? '' : JSON.stringify(text) }},
            {title: '操作', fixed: 'right', width: 150, scopedSlots: {customRender: 'operation'}}
          ],
          authOrgUserColumns: [
            {title: '用户授权', dataIndex: 'userAuth', customRender: (text, record, index) => { return text ? '是' : '否' }}
          ],
          columns: [
          ],
          rowSelection: {
            selectedRowKeys: [],
            type: 'radio',
            onChange: (selectedRowKeys, selectedRows) => {
              this.org.rowSelectData = selectedRows[0]
              this.org.rowSelection.selectedRowKeys = selectedRowKeys
            }
          },
          rowSelectData: {}
        },
        admin: {
          pageObj: {total: 10, pageSize: 10, pageNum: 1},
          loading: false,
          sourceData: [],
          data: [],
          columns: [
            {title: '用户账号', dataIndex: 'account'},
            {title: '授权', dataIndex: 'auth', customRender: (text, record, index) => { return text ? '是' : '否' }},
            {title: '所有数据', dataIndex: 'allData', customRender: (text, record, index) => { return text ? '是' : '否' }},
            {title: '授权数据', dataIndex: 'authList', customRender: (text, record, index) => { return record.allData ? '' : JSON.stringify(text) }},
            {title: '操作', fixed: 'right', width: 100, scopedSlots: {customRender: 'operation'}}
          ],
          rowSelection: {
            selectedRowKeys: [],
            type: 'radio',
            onChange: (selectedRowKeys, selectedRows) => {
              this.admin.rowSelectData = selectedRows[0]
              this.admin.rowSelection.selectedRowKeys = selectedRowKeys
            }
          },
          rowSelectData: {}
        },
        specialData: [],
        model: {
          labelCol: {span: 7},
          wrapperCol: {span: 12},
          loading: false,
          visible: false,
          form: this.$form.createForm(this),
          data: {}
        },
        editModel: {
          loading: false,
          visible: false,
          form: this.$form.createForm(this),
          data: {}
        },
        authModel: {
          loading: false,
          visible: false,
          optionData: [],
          data: {}
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
        operateApi.getDataPowerList({
          pageNum: this.pageObj.pageNum,
          pageSize: this.pageObj.pageSize,
          data: {}
        }).then((res) => {
          if (res.success) {
            this.data = res.rows
            this.pageObj.total = res.total
            this.rowSelection.selectedRowKeys = []
            this.rowSelectData = {}
            this.org.columns = []
            this.org.data = []
            this.org.rowSelectData = {}
            this.admin.data = []
            this.authModel.optionData = []
          }
          this.loading = false
        })
      },
      // 添加数据权限
      addDataPower () {
        this.model.visible = true
        this.model.data = {}
        this.model.form.resetFields()
      },
      // 更新数据权限
      editDataPower (record) {
        this.editModel.visible = true
        this.editModel.data = record
        setTimeout(() => {
          this.editModel.form.setFieldsValue({
            dataPowerName: this.editModel.data.dataPowerName,
            dataOption: this.editModel.data.dataOption
          })
        }, 100)
      },
      // 添加数据权限
      addOk (e) {
        e.preventDefault()
        this.model.form.validateFields((err, values) => {
          if (!err) {
            this.model.loading = true
            operateApi.addDataPower({
              ...values
            }).then((res) => {
              if (res.success) {
                this.$message.info(res.message)
                this.searchList()
              }
              this.model.loading = false
              this.model.visible = false
            })
          }
        })
      },
      // 修改数据权限
      editOk (e) {
        e.preventDefault()
        this.editModel.form.validateFields((err, values) => {
          if (!err) {
            this.editModel.loading = true
            operateApi.editDataPower({
              id: this.editModel.data.id,
              ...values
            }).then((res) => {
              if (res.success) {
                this.$message.info(res.message)
                this.searchList()
              }
              this.editModel.loading = false
              this.editModel.visible = false
            })
          }
        })
      },
      // 数据权限列表点击事件
      onClick (record) {
        return {
          on: {
            click: () => {
              this.org.columns = []
              this.org.columns.push(...this.org.baseColumns)
              this.admin.data = []
              if (this.rowSelectData.id !== record.id) {
                this.org.rowSelectData = {}
                this.org.rowSelection.selectedRowKeys = []
                this.rowSelectData = record
                this.rowSelection.selectedRowKeys = [record.id]
                if (record.dataPowerType === 0) {
                  this.org.columns.push(...this.org.authOrgColumns)
                } else {
                  this.org.columns.push(...this.org.authOrgUserColumns)
                }
                this.getOrgTree()
              } else {
                this.rowSelectData = {}
                this.rowSelection.selectedRowKeys = []
                this.authData = []
                this.authOrgMap = []
                this.authUserMap = []
              }
            }
          }
        }
      },
      getOrgTree () {
        if (this.org.data.length <= 0) {
          operateApi.getOrgTree({data: {}}).then(res => {
            this.org.sourceData = res.rows
            this.getAuthList()
          })
        } else {
          this.getAuthList()
        }
      },
      getAuthList () {
        operateApi.getAuthList({data: {dataPowerCode: this.rowSelectData.dataPowerCode}}).then(res => {
          this.authData = res.rows
          this.authOrgMap = []
          this.authUserMap = []
          this.authData.forEach(item => {
            this.authOrgMap[item.orgCode] = item
            if (item.userId != null) {
              this.authUserMap[item.orgCode + '_' + item.userId] = item
            }
          })
          this.org.data = []
          this.org.data = this.handleOrg(this.org.sourceData)
        })
      },
      handleOrg (sourceList) {
        let list = []
        sourceList.forEach(item => {
          let tmp = JSON.parse(JSON.stringify(item))
          if (this.rowSelectData.dataPowerType === 0) {
            // 机构授权
            let auth = this.authOrgMap[item.value]
            if (auth) {
              tmp.authId = auth.id
              tmp.allData = auth.allData
              tmp.authList = JSON.parse(auth.authList)
            }
          } else {
            // 机构成员授权
            let auth = this.authOrgMap[item.value]
            if (auth) {
              tmp.userAuth = true
            }
          }
          if (tmp.children && tmp.children.length > 0) {
            tmp.children = this.handleOrg(tmp.children)
          }
          list.push(tmp)
        })
        return list
      },
      // 机构点击事件
      orgOnClick (record) {
        return {
          on: {
            click: () => {
              this.admin.data = []
              if (this.org.rowSelectData.id !== record.id) {
                this.org.rowSelectData = record
                this.org.rowSelection.selectedRowKeys = [record.id]
                if (this.rowSelectData.dataPowerType === 1) {
                  this.getAdminList()
                }
              } else {
                this.org.rowSelectData = {}
                this.org.rowSelection.selectedRowKeys = []
              }
            }
          }
        }
      },
      getAdminList () {
        this.admin.loading = true
        let orgCode = this.org.rowSelectData.value
        operateApi.getAdminListByOrg({
          pageNum: this.admin.pageObj.pageNum,
          pageSize: this.admin.pageObj.pageSize,
          data: {orgCode: orgCode}
        }).then((res) => {
          if (res.success) {
            this.admin.sourceData = res.rows
            this.admin.pageObj.total = res.total
            this.admin.sourceData.forEach(item => {
              let admin = JSON.parse(JSON.stringify(item))
              let auth = this.authUserMap[orgCode + '_' + item.adminId]
              if (auth) {
                admin.authId = auth.id
                admin.auth = true
                admin.allData = auth.allData
                admin.authList = JSON.parse(auth.authList)
              }
              this.admin.data.push(admin)
            })
          }
          this.admin.loading = false
        })
      },
      addAuth (record) {
        this.authModel.data = {...record}
        this.handleOptionData()
        this.authModel.visible = true
      },
      editAuth (record) {
        this.authModel.data = record
        this.handleOptionData()
        this.authModel.visible = true
      },
      delAuth (record) {
        const that = this
        this.$confirm({
          title: '解除授权！?',
          onOk () {
            return new Promise((resolve, reject) => {
              operateApi.delAuth({id: record.authId, dataPowerCode: that.rowSelectData.dataPowerCode}).then((res) => {
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
      handleOptionData () {
        this.authModel.optionData = []
        if (this.rowSelectData.dataPowerCode === 'dataPower') {
          if (!this.specialData.dataPower || this.specialData.dataPower.length <= 0) {
            this.specialData.dataPower = []
            this.data.forEach(item => {
              this.specialData.dataPower.push({title: item.dataPowerName, value: item.dataPowerCode, key: item.dataPowerCode})
            })
          }
          this.authModel.optionData = this.specialData.dataPower
        } else if (this.rowSelectData.dataPowerCode === 'appDataPower') {
          if (!this.specialData.appDataPower || this.specialData.appDataPower.length <= 0) {
            this.specialData.appDataPower = []
            operateApi.getAppApiTree({}).then((res) => {
              if (res.success) {
                res.rows.forEach(item => this.specialData.appDataPower.push({title: item.applicationName, value: item.applicationName, key: item.applicationName}))
              }
            })
            this.authModel.optionData = this.specialData.appDataPower
          } else this.authModel.optionData = this.specialData.appDataPower
        } else if (this.rowSelectData.dataPowerCode === 'uiDataPower') {
          if (!this.specialData.uiDataPower || this.specialData.uiDataPower.length <= 0) {
            this.specialData.uiDataPower = []
            operateApi.getUiList({}).then((res) => {
              if (res.success) {
                res.rows.forEach(item => this.specialData.uiDataPower.push({title: item.uiName, value: item.uiCode, key: item.uiCode}))
              }
            })
            this.authModel.optionData = this.specialData.uiDataPower
          } else this.authModel.optionData = this.specialData.uiDataPower
        } else {
          this.authModel.optionData = JSON.parse(this.rowSelectData.dataOption)
        }
      },
      addOrEditAuthOk () {
        let data = this.authModel.data
        console.log(data)
        if (data.authId) {
          operateApi.editAuth({
            id: data.authId,
            dataPowerCode: this.rowSelectData.dataPowerCode,
            allData: data.allData,
            authList: JSON.stringify(data.authList)
          }).then((res) => {
            this.searchList()
            this.authModel.visible = false
          })
        } else {
          let orgCode = this.authModel.data.value
          if (this.rowSelectData.dataPowerType === 1) {
            orgCode = this.org.rowSelectData.value
          }
          operateApi.addAuth({
            dataPowerCode: this.rowSelectData.dataPowerCode,
            orgCode: orgCode,
            userId: this.authModel.data.adminId,
            allData: data.allData,
            authList: JSON.stringify(data.authList)
          }).then((res) => {
            this.searchList()
            this.authModel.visible = false
          })
        }
      }
    }
  }
</script>

<style scoped>

</style>
