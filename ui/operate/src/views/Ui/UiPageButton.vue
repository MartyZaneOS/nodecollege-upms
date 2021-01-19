<template>
  <a-card title="页面按钮信息" size="small">
    <a-form layout="inline" slot="extra">
      <a-form-item>
        <a-button type="primary" @click="addUiPageModal">添加</a-button>
      </a-form-item>
    </a-form>
    <a-table :dataSource="data" :columns="columns" :pagination="pagination"
             :loading="loading"
             size="small" bordered :rowKey="record => record.id" :customRow="uiPageOnClick"
             :rowSelection="rowSelection">
      <template slot="operation" slot-scope="text, record, index">
        <div>
            <span>
              <a @click="() => editUiModal(record)">编辑</a>
              <a @click="() => delUiModal(record)">删除</a>
            </span>
        </div>
      </template>
    </a-table>
    <NCPagination :page="pageObj" @changePage="(page)=>{this.pageObj = page}"
                  @searchList="init"/>
    <a-modal v-model="addOrUpdateModal.visible" :title="addOrUpdateModal.title">
      <a-form :form="addOrUpdateModal.form">
        <a-form-item label="按钮名称" :label-col="addOrUpdateModal.labelCol" :wrapper-col="addOrUpdateModal.wrapperCol">
          <a-input placeholder="请输入按钮名称！"
                   v-decorator="['buttonName', {rules: [{ required: true, message: '请输入按钮名称！'}]}]"/>
        </a-form-item>
        <a-form-item label="按钮代码" :label-col="addOrUpdateModal.labelCol" :wrapper-col="addOrUpdateModal.wrapperCol"
                     v-if="addOrUpdateModal.title === '添加按钮'">
          <a-input placeholder="请输入按钮代码！"
                   v-decorator="['buttonCode', {rules: [{ required: addOrUpdateModal.title === '添加按钮', message: '请输入按钮代码！'}]}]"/>
        </a-form-item>
        <a-form-item label="按钮排序号" :label-col="addOrUpdateModal.labelCol" :wrapper-col="addOrUpdateModal.wrapperCol">
          <a-input-number placeholder="请输入按钮排序号！" style="width: 100%"
                   v-decorator="['num', {rules: [{ required: true, message: '请输入按钮排序号！'}]}]"/>
        </a-form-item>
        <a-form-item label="按钮类型" :label-col="addOrUpdateModal.labelCol" :wrapper-col="addOrUpdateModal.wrapperCol"
                     v-if="addOrUpdateModal.title === '添加按钮'">
          <a-select placeholder="请选择按钮类型！"
                   v-decorator="['buttonType', {initialValue: '2', rules: [{ required: true, message: '请输入按钮代码！'}]}]">
            <a-select-option value="2">查询类型</a-select-option>
            <a-select-option value="3">操作类型</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="接口信息" :label-col="addOrUpdateModal.labelCol" :wrapper-col="addOrUpdateModal.wrapperCol">
          <a-cascader
              :displayRender="({labels}) =>{return labels != null && labels.length > 0 ? labels[0] + ' - ' + labels[labels.length - 1] : []}"
              :fieldNames="{ label: 'name', value: 'code', children: 'children' }"
              :options="api.tree" placeholder="请选择接口信息"
              v-decorator="['apiPath', {rules: [{ required: true, message: '请选择接口信息！'}]}]"/>
        </a-form-item>
        <a-form-item label="按钮图标" :label-col="addOrUpdateModal.labelCol" :wrapper-col="addOrUpdateModal.wrapperCol">
          <a-input placeholder="请输入按钮图标！" v-decorator="['buttonIcon']"/>
        </a-form-item>
      </a-form>
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="addOrUpdateModal.loading" @click="addOrUpdateUiOk">
          确定
        </a-button>
        <a-button key="back" @click="addOrUpdateModal.visible = false">取消</a-button>
      </template>
    </a-modal>
  </a-card>
</template>

<script>
  import {NCPagination} from 'common'
  import * as operateApi from '../../api/operate'

  export default {
    name: 'UiPageButton',
    components: {
      NCPagination
    },
    props: {
      uiPageData: {
        id: undefined,
        pageCode: undefined
      }
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
          }
        },
        rowSelectData: {},
        columns: [
          {title: '按钮名称', dataIndex: 'buttonName'},
          {title: '按钮代码', dataIndex: 'buttonCode'},
          {title: '按钮排序号', dataIndex: 'num'},
          {title: '按钮类型', dataIndex: 'buttonType', customRender: (text, record, index) => { return text === 2 ? '查询类' : '操作类' }},
          {title: '按钮图标', dataIndex: 'buttonIcon'},
          {title: '接口微服务名称', dataIndex: 'appName'},
          {title: '接口地址', dataIndex: 'apiUrl'},
          {title: '操作', fixed: 'right', width: 100, scopedSlots: {customRender: 'operation'}}
        ],
        addOrUpdateModal: {
          labelCol: {span: 7},
          wrapperCol: {span: 12},
          loading: false,
          visible: false,
          title: '添加按钮',
          form: this.$form.createForm(this),
          data: {}
        },
        api: {
          tree: []
        }
      }
    },
    mounted () {
      this.init()
      this.searchAPiList()
    },
    watch: {
      uiPageData () {
        this.init()
      }
    },
    methods: {
      // 查询前端数据
      init () {
        console.log('uiPageData', this.uiPageData)
        this.rowSelectData = {}
        this.rowSelection.selectedRowKeys = []
        if (!this.uiPageData || !this.uiPageData.pageCode) {
          return
        }
        operateApi.getButtonTree({pageCode: this.uiPageData.pageCode}).then(res => {
          console.log(res)
          this.data = res.rows
          this.pageObj.total = res.total
          this.pageObj.pageSize = res.total
        })
      },
      addUiPageModal () {
        if (!this.uiPageData || !this.uiPageData.pageCode) {
          this.$message.error('请先选择前段工程信息')
          return
        }
        this.addOrUpdateModal.data = {}
        this.addOrUpdateModal.form.resetFields()
        this.addOrUpdateModal.visible = true
        this.addOrUpdateModal.title = '添加按钮'
      },
      editUiModal (record) {
        this.addOrUpdateModal.data = record
        setTimeout(() => {
          const apiPath = []
          this.getApiPath(this.api.tree, record, apiPath)
          this.addOrUpdateModal.form.setFieldsValue({
            buttonName: record.buttonName,
            num: record.num,
            buttonIcon: record.buttonIcon,
            apiPath: apiPath
          })
        }, 100)
        this.addOrUpdateModal.visible = true
        this.addOrUpdateModal.title = '编辑按钮'
      },
      delUiModal (record) {
        const that = this
        this.$confirm({
          title: '删除前端页面?',
          onOk () {
            return new Promise((resolve, reject) => {
              operateApi.delUiPageButton(record).then((res) => {
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
      // 查询接口树
      searchAPiList () {
        operateApi.getAppApiTree({}).then((res) => {
          if (res.success) {
            this.setApiData(res.rows)
            this.api.tree = res.rows
          }
        })
      },
      setApiData (apiData) {
        apiData.forEach(item => {
          item.name = item.apiId != null ? item.apiUrl : item.controllerName != null ? item.controllerName : item.applicationName
          item.code = item.apiId != null ? item.apiUrl : item.controllerName != null ? item.controllerName : item.applicationName
          item.disabled = item.state === -1
          if (item.children) {
            this.setApiData(item.children)
          }
        })
      },
      getApiPath (apiData, record, apiPath) {
        if (apiPath.length > 0) {
          return
        }
        apiData.forEach(item => {
          if (apiPath.length > 0) {
            return
          }
          if (item.applicationName === record.appName && item.apiUrl === record.apiUrl) {
            apiPath.push(item.applicationName)
            apiPath.push(item.controllerName)
            apiPath.push(item.apiUrl)
            return
          }
          if (item.children) {
            this.getApiPath(item.children, record, apiPath)
          }
        })
      },
      addOrUpdateUiOk (e) {
        this.addOrUpdateModal.form.validateFields((err, values) => {
          if (!err) {
            // if ((!this.rowSelectData || !this.rowSelectData.buttonCode) && values.buttonType === '1') {
            //   this.$message.error('顶级按钮只能是查询类型！')
            //   return
            // }
            this.addOrUpdateModal.loading = true
            if (this.addOrUpdateModal.title === '添加按钮') {
              if (this.rowSelectData.buttonType === 1) {
                this.$message.error('父级按钮不能为操作类按钮！')
                this.addOrUpdateModal.loading = false
                return
              }
              operateApi.addUiPageButton({
                ...values,
                appName: values.apiPath[0],
                apiUrl: values.apiPath[2],
                parentCode: this.rowSelectData ? this.rowSelectData.buttonCode : undefined,
                pageCode: this.uiPageData.pageCode
              }).then(res => {
                if (res.success) {
                  this.$message.info(res.message)
                  this.init()
                  this.addOrUpdateModal.visible = false
                }
                this.addOrUpdateModal.loading = false
              })
            } else {
              operateApi.editUiPageButton({
                ...values,
                appName: values.apiPath[0],
                apiUrl: values.apiPath[2],
                parentCode: this.rowSelectData ? this.rowSelectData.buttonCode : undefined,
                pageCode: this.uiPageData.pageCode,
                id: this.addOrUpdateModal.data.id
              }).then(res => {
                if (res.success) {
                  this.$message.info(res.message)
                  this.init()
                  this.addOrUpdateModal.visible = false
                }
                this.addOrUpdateModal.loading = false
              })
            }
          }
        })
      },
      uiPageOnClick (record) {
        return {
          on: {
            click: () => {
              if (this.rowSelectData.id !== record.id) {
                this.rowSelectData = record
                this.rowSelection.selectedRowKeys = [record.id]
              } else {
                this.rowSelectData = {}
                this.rowSelection.selectedRowKeys = []
              }
            }
          }
        }
      }
    }
  }
</script>

<style scoped>

</style>
