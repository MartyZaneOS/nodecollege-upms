<template>
  <a-card title="前端页面信息" size="small">
    <a-form layout="inline" slot="extra">
      <a-form-item>
        <a-button type="primary" @click="addUiPageModal">添加</a-button>
      </a-form-item>
    </a-form>
    <a-table :dataSource="data" :columns="columns" :pagination="pagination"
             :loading="loading"
             size="small" bordered :rowKey="record => record.uiPageId" :customRow="uiPageOnClick"
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
    <NCPagination :page="pageObj" @changePage="(page)=>{this.pageObj = page}" @searchList="init"/>
    <a-modal v-model="addOrUpdateModal.visible" :title="addOrUpdateModal.title">
      <a-form :form="addOrUpdateModal.form" :label-col="addOrUpdateModal.labelCol" :wrapper-col="addOrUpdateModal.wrapperCol">
        <a-form-item label="前端页面名称">
          <a-input placeholder="请输入前端页面名称！"
                   v-decorator="['pageName', {rules: [{ required: true, message: '请输入前端页面名称！'}]}]"/>
        </a-form-item>
        <a-form-item label="前端页面代码" v-if="addOrUpdateModal.title === '添加前端页面'">
          <a-input placeholder="请输入前端页面代码！"
                   v-decorator="['pageCode', {rules: [{ required: addOrUpdateModal.title === '添加前端页面', message: '请输入前端页面代码！'}]}]"/>
        </a-form-item>
        <a-form-item label="前端页面排序号">
          <a-input-number placeholder="请输入前端页面排序号！" style="width: 100%;" v-decorator="['num', {rules: [{ required: true, message: '请输入前端页面代码！'}]}]"/>
        </a-form-item>
        <a-form-item label="页面路径">
          <a-input placeholder="请输入页面路径！" v-decorator="['pagePath', {rules: [{ required: true, message: '请输入页面路径！'}]}]"/>
        </a-form-item>
        <a-form-item label="页面图标">
          <a-input placeholder="请输入页面图标！"  v-decorator="['pageIcon']"/>
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
    name: 'UiPage',
    components: {
      NCPagination
    },
    props: {
      uiData: {
        uiId: undefined,
        uiCode: undefined
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
            this.rowSelection.selectedRowKeys = selectedRowKeys
          }
        },
        columns: [
          {title: '序号', width: '20', customRender: (text, record, index) => { return index + this.pageObj.pageSize * (this.pageObj.pageNum - 1) + 1 }},
          {title: '页面名称', dataIndex: 'pageName'},
          {title: '页面代码', dataIndex: 'pageCode'},
          {title: '排序号', dataIndex: 'num'},
          {title: '页面路径', dataIndex: 'pagePath'},
          {title: '页面图标', dataIndex: 'pageIcon'},
          {title: '操作', fixed: 'right', width: 100, scopedSlots: {customRender: 'operation'}}
        ],
        addOrUpdateModal: {
          labelCol: {span: 7},
          wrapperCol: {span: 12},
          loading: false,
          visible: false,
          title: '添加前端页面',
          form: this.$form.createForm(this),
          data: {}
        }
      }
    },
    mounted () {
      this.init()
    },
    watch: {
      uiData () {
        this.init()
      }
    },
    methods: {
      // 查询前端数据
      init () {
        console.log('uiData', this.uiData)
        this.rowSelection.selectedRowKeys = []
        if (!this.uiData || !this.uiData.uiCode) {
          return
        }
        operateApi.getUiPageList({...this.pageObj, data: {uiCode: this.uiData.uiCode}}).then(res => {
          console.log(res)
          this.data = res.rows
          this.pageObj.total = res.total
        })
      },
      addUiPageModal () {
        if (!this.uiData || !this.uiData.uiCode) {
          this.$message.error('请先选择前段工程信息')
          return
        }
        this.addOrUpdateModal.data = {}
        this.addOrUpdateModal.form.resetFields()
        this.addOrUpdateModal.visible = true
        this.addOrUpdateModal.title = '添加前端页面'
      },
      editUiModal (record) {
        this.addOrUpdateModal.data = record
        setTimeout(() => {
          this.addOrUpdateModal.form.setFieldsValue({
            pageName: record.pageName,
            pagePath: record.pagePath,
            num: record.num,
            pageIcon: record.pageIcon
          })
        }, 100)
        this.addOrUpdateModal.visible = true
        this.addOrUpdateModal.title = '编辑前端页面'
      },
      delUiModal (record) {
        const that = this
        this.$confirm({
          title: '删除前端页面?',
          onOk () {
            return new Promise((resolve, reject) => {
              operateApi.delUiPage(record).then((res) => {
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
      addOrUpdateUiOk (e) {
        this.addOrUpdateModal.form.validateFields((err, values) => {
          if (!err) {
            this.addOrUpdateModal.loading = true
            if (this.addOrUpdateModal.title === '添加前端页面') {
              operateApi.addUiPage({...values, uiCode: this.uiData.uiCode}).then(res => {
                this.$message.info(res.message)
                this.init()
                this.addOrUpdateModal.loading = false
                this.addOrUpdateModal.visible = false
              })
            } else {
              operateApi.editUiPage({...values, uiPageId: this.addOrUpdateModal.data.uiPageId}).then(res => {
                this.$message.info(res.message)
                this.init()
                this.addOrUpdateModal.loading = false
                this.addOrUpdateModal.visible = false
              })
            }
          }
        })
      },
      uiPageOnClick (record) {
        return {
          on: {
            click: () => {
              this.$emit('pageOnClick', record)
              this.rowSelection.selectedRowKeys = [record.uiPageId]
            }
          }
        }
      }
    }
  }
</script>

<style scoped>

</style>
