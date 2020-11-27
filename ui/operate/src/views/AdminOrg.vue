<template>
  <a-row>
    <a-col span="24">
      <a-card title="机构信息" size="small">
        <a-form layout="inline" slot="extra">
          <a-form-item>
            <a-button type="primary" @click="addModal">添加</a-button>
          </a-form-item>
        </a-form>
        <a-table :dataSource="data" :columns="columns" :pagination="pagination" :loading="loading"
                 size="small" bordered :rowKey="record => record.id" :customRow="onClick" :rowSelection="rowSelection">
          <template slot="operation" slot-scope="text, record, index">
            <div>
            <span>
              <a @click="() => editModal(record)">编辑</a>
              <a @click="() => delModal(record)">删除</a>
            </span>
            </div>
          </template>
        </a-table>
      </a-card>
    </a-col>
    <a-col span="12">
      <a-card title="角色信息" size="small" :headStyle="{lineHeight: '40px'}">
        <a-table :dataSource="role.data" :columns="role.columns" :pagination="pagination" :loading="role.loading"
                 size="small" bordered :rowKey="record => record.id">
        </a-table>
        <NCPagination :page="role.pageObj" @changePage="(page)=>{this.role.pageObj = page}" @searchList="selectRoleList"/>
      </a-card>
    </a-col>
    <a-col span="12">
      <a-card title="管理员信息" size="small" :headStyle="{lineHeight: '40px'}">
        <a-table :dataSource="admin.data" :columns="admin.columns" :pagination="pagination" :loading="admin.loading"
                 size="small" bordered :rowKey="record => record.id">
        </a-table>
        <NCPagination :page="admin.pageObj" @changePage="(page)=>{this.admin.pageObj = page}" @searchList="selectAdminList"/>
      </a-card>
    </a-col>
    <a-modal v-model="addOrUpdateModal.visible" :title="addOrUpdateModal.title">
      <a-form :form="addOrUpdateModal.form" :label-col="addOrUpdateModal.labelCol" :wrapper-col="addOrUpdateModal.wrapperCol">
        <a-form-item label="机构名称">
          <a-input placeholder="请输入机构名称！" v-decorator="['orgName', {rules: [{ required: true, message: '请输入机构名称！'}]}]"/>
        </a-form-item>
        <a-form-item label="机构代码" v-if="addOrUpdateModal.title === '添加机构'">
          <a-input placeholder="请输入机构代码！"
                   v-decorator="['orgCode', {rules: [{ required: addOrUpdateModal.title === '添加机构', message: '请输入机构代码！'}]}]"/>
        </a-form-item>
        <a-form-item label="排序号">
          <a-input placeholder="请输入排序号！" v-decorator="['num', {rules: [{ required: true, message: '请输入排序号！'}]}]"/>
        </a-form-item>
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
    name: 'Org',
    components: {
      NCPagination
    },
    data () {
      return {
        loading: false,
        pageObj: {total: 10, pageSize: 10, pageNum: 1},
        pagination: false,
        data: [],
        rowSelection: {
          selectedRowKeys: [],
          type: 'radio',
          onChange: (selectedRowKeys, selectedRows) => {
            this.rowSelectData = selectedRows[0]
            this.rowSelection.selectedRowKeys = selectedRowKeys
            this.selectRoleList()
            this.selectAdminList()
          }
        },
        rowSelectData: {},
        columns: [
          {title: '机构名称', dataIndex: 'title'},
          {title: '机构代码', dataIndex: 'key'},
          {title: '排序号', dataIndex: 'num'},
          {title: '创建时间', dataIndex: 'createTime'},
          {title: '操作', fixed: 'right', width: 100, scopedSlots: {customRender: 'operation'}}
        ],
        role: {
          loading: false,
          pageObj: {total: 10, pageSize: 10, pageNum: 1},
          data: [],
          columns: [
            {title: '归属产品代码', dataIndex: 'productCode'},
            {title: '角色名称', dataIndex: 'roleName'},
            {title: '角色代码', dataIndex: 'roleCode'},
            {title: '角色类型', dataIndex: 'roleType', customRender: (text, record, index) => { return text === 0 ? '组织角色' : text === 1 ? '组织成员角色' : '' }},
            {
              title: '数据权限',
              dataIndex: 'dataPower',
              customRender: (text, record, index) => {
                if (text === 0) {
                  return '所有机构'
                }
                if (text === 1) {
                  return '所属机构及下级机构'
                }
                if (text === 2) {
                  return '所属机构'
                }
                if (text === 3) {
                  return '用户自己的数据'
                }
              }
            },
            {title: '状态', dataIndex: 'roleState', customRender: (text, record, index) => { return text === -1 ? '已删除' : text === 0 ? '正常' : text === 1 ? '禁用' : '' }}
          ]
        },
        admin: {
          loading: false,
          pageObj: {total: 10, pageSize: 10, pageNum: 1},
          data: [],
          columns: [
            {title: '管理员账号', dataIndex: 'account'},
            {title: '管理员电话', dataIndex: 'telephone'},
            {title: '首次登陆', dataIndex: 'firstLogin', customRender: (text, record, index) => { return text ? '是' : '否' }},
            {title: '创建时间', dataIndex: 'createTime'},
            {title: '状态', dataIndex: 'state', customRender: (text, record, index) => { return text === 2 ? '锁定' : '正常' }}
          ]
        },
        addOrUpdateModal: {
          labelCol: {span: 7},
          wrapperCol: {span: 12},
          loading: false,
          visible: false,
          title: '添加机构',
          form: this.$form.createForm(this),
          data: {}
        }
      }
    },
    mounted () {
      this.init()
    },
    methods: {
      // 查询机构数据
      init () {
        this.rowSelectData = {}
        this.rowSelection.selectedRowKeys = []
        operateApi.getOrgTree({data: {}}).then(res => {
          this.data = res.rows
          this.pageObj.total = res.total
        })
      },
      addModal () {
        this.addOrUpdateModal.data = {}
        this.addOrUpdateModal.form.resetFields()
        this.addOrUpdateModal.visible = true
        this.addOrUpdateModal.title = '添加机构'
      },
      editModal (record) {
        this.addOrUpdateModal.data = record
        setTimeout(() => {
          this.addOrUpdateModal.form.setFieldsValue({
            orgName: record.title,
            num: record.num
          })
        }, 100)
        this.addOrUpdateModal.visible = true
        this.addOrUpdateModal.title = '编辑机构'
      },
      delModal (record) {
        const that = this
        this.$confirm({
          title: '删除机构?',
          onOk () {
            return new Promise((resolve, reject) => {
              operateApi.delOrg({id: record.id}).then((res) => {
                if (res.success) {
                  that.$message.info(res.message)
                  that.init()
                  resolve(res.message)
                } else {
                  reject(res.message)
                }
              })
            })
          }
        })
      },
      addOrUpdateOk (e) {
        this.addOrUpdateModal.form.validateFields((err, values) => {
          if (!err) {
            this.addOrUpdateModal.loading = true
            if (this.addOrUpdateModal.title === '添加机构') {
              operateApi.addOrg({
                ...values,
                parentCode: this.rowSelectData ? this.rowSelectData.key : undefined
              }).then(res => {
                this.$message.info(res.message)
                this.init()
                this.addOrUpdateModal.loading = false
                this.addOrUpdateModal.visible = false
              })
            } else {
              operateApi.editOrg({...values, id: this.addOrUpdateModal.data.id}).then(res => {
                this.$message.info(res.message)
                this.init()
                this.addOrUpdateModal.loading = false
                this.addOrUpdateModal.visible = false
              })
            }
          }
        })
      },
      onClick (record) {
        return {
          on: {
            click: () => {
              if (this.rowSelectData.id !== record.id) {
                this.rowSelectData = record
                this.rowSelection.selectedRowKeys = [record.id]
                this.selectRoleList()
                this.selectAdminList()
              } else {
                this.rowSelectData = {}
                this.rowSelection.selectedRowKeys = []
                this.role.data = []
                this.admin.data = []
                this.role.pageObj.total = 0
                this.admin.pageObj.total = 0
              }
            }
          }
        }
      },
      selectRoleList () {
        operateApi.getRoleListByOrg({...this.role.pageObj, data: {orgCode: this.rowSelectData.key}}).then(res => {
          if (res.success) {
            this.role.data = res.rows
            this.role.pageObj.total = res.total
          }
        })
      },
      selectAdminList () {
        operateApi.getAdminListByOrg({...this.admin.pageObj, data: {orgCode: this.rowSelectData.key}}).then(res => {
          if (res.success) {
            this.admin.data = res.rows
            this.admin.pageObj.total = res.total
          }
        })
      }
    }
  }
</script>

<style scoped>

</style>
