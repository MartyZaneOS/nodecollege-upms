<template>
  <a-row>
    <a-col span="24" :headStyle="{backgroundColor: 'red'}">
      <a-card title="前端工程" size="small">
        <a-form layout="inline" slot="extra">
          <a-form-item>
            <a-button type="primary" @click="addUiModal">添加</a-button>
          </a-form-item>
        </a-form>
        <a-table :dataSource="ui.data" :columns="ui.columns" :pagination="ui.pagination" :loading="ui.loading"
                 size="small" bordered :rowKey="record => record.uiId" :customRow="uiOnClick" :rowSelection="ui.rowSelection">
          <template slot="operation" slot-scope="text, record, index">
            <div>
            <span>
              <a @click="() => editUiModal(record)">编辑</a>
              <a @click="() => delUiModal(record)">删除</a>
            </span>
            </div>
          </template>
        </a-table>
        <NCPagination :page="ui.pageObj" @changePage="(page)=>{this.ui.pageObj = page}" @searchList="init"/>
      </a-card>
    </a-col>
    <a-col span="24">
      <UiPage :ui-data="ui.rowSelectData" @pageOnClick="(row)=>{this.uiPage.rowSelectData = row}"/>
    </a-col>
    <a-col span="24">
      <UiPageButton :ui-page-data="uiPage.rowSelectData"/>
    </a-col>
    <a-modal v-model="ui.addOrUpdateModal.visible" :title="ui.addOrUpdateModal.title">
      <a-form :form="ui.addOrUpdateModal.form">
        <a-form-item label="前端名称" :label-col="ui.addOrUpdateModal.labelCol" :wrapper-col="ui.addOrUpdateModal.wrapperCol">
          <a-input placeholder="请输入前端名称！"
                   v-decorator="['uiName', {rules: [{ required: true, message: '请输入前端名称！'}]}]"/>
        </a-form-item>
        <a-form-item label="前端代码" :label-col="ui.addOrUpdateModal.labelCol" :wrapper-col="ui.addOrUpdateModal.wrapperCol"
                     v-if="ui.addOrUpdateModal.title === '添加前端工程'">
          <a-input placeholder="请输入前端代码！"
                   v-decorator="['uiCode', {rules: [{ required: ui.addOrUpdateModal.title === '添加前端工程', message: '请输入前端代码！'}]}]"/>
        </a-form-item>
        <a-form-item label="描述" :label-col="ui.addOrUpdateModal.labelCol" :wrapper-col="ui.addOrUpdateModal.wrapperCol">
          <a-input placeholder="请输入描述！"
                   v-decorator="['uiDesc']"/>
        </a-form-item>
      </a-form>
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="ui.addOrUpdateModal.loading" @click="addOrUpdateUiOk">
          确定
        </a-button>
        <a-button key="back" @click="ui.addOrUpdateModal.visible = false">取消</a-button>
      </template>
    </a-modal>
  </a-row>
</template>

<script>
  import {NCPagination} from 'common'
  import * as operateApi from '../../api/operate'
  import UiPage from './UiPage'
  import UiPageButton from './UiPageButton'

  export default {
    name: 'Ui',
    components: {
      NCPagination,
      UiPage,
      UiPageButton
    },
    data () {
      return {
        ui: {
          loading: false,
          pageObj: {total: 10, pageSize: 10, pageNum: 1},
          pagination: false,
          data: [],
          rowSelection: {
            selectedRowKeys: [],
            type: 'radio',
            onChange: (selectedRowKeys, selectedRows) => {
              this.ui.rowSelection.selectedRowKeys = selectedRowKeys
            }
          },
          rowSelectData: {},
          columns: [
            {title: '序号', width: '20', customRender: (text, record, index) => { return index + this.ui.pageObj.pageSize * (this.ui.pageObj.pageNum - 1) + 1 }},
            {title: '前端名称', dataIndex: 'uiName'},
            {title: '前端代码', dataIndex: 'uiCode'},
            {title: '描述', dataIndex: 'uiDesc'},
            {title: '操作', fixed: 'right', width: 100, scopedSlots: {customRender: 'operation'}}
          ],
          addOrUpdateModal: {
            labelCol: {span: 7},
            wrapperCol: {span: 12},
            loading: false,
            visible: false,
            title: '添加前端工程',
            form: this.$form.createForm(this),
            data: {}
          }
        },
        uiPage: {
          rowSelectData: {}
        }
      }
    },
    mounted () {
      this.init()
    },
    methods: {
      // 查询前端数据
      init () {
        operateApi.getUiList({ ...this.ui.pageObj }).then(res => {
          this.ui.data = res.rows
          this.ui.pageObj.total = res.total
        })
      },
      addUiModal () {
        this.ui.addOrUpdateModal.data = {}
        this.ui.addOrUpdateModal.form.resetFields()
        this.ui.addOrUpdateModal.visible = true
        this.ui.addOrUpdateModal.title = '添加前端工程'
      },
      editUiModal (record) {
        this.ui.addOrUpdateModal.data = record
        setTimeout(() => {
          this.ui.addOrUpdateModal.form.setFieldsValue({
            uiName: record.uiName,
            uiDesc: record.uiDesc
          })
        }, 100)
        this.ui.addOrUpdateModal.visible = true
        this.ui.addOrUpdateModal.title = '编辑前端工程'
      },
      delUiModal (record) {
        const that = this
        this.$confirm({
          title: '删除前端工程?',
          onOk () {
            return new Promise((resolve, reject) => {
              operateApi.delUi({ uiId: record.uiId }).then((res) => {
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
        this.ui.addOrUpdateModal.form.validateFields((err, values) => {
          if (!err) {
            this.ui.addOrUpdateModal.loading = true
            if (this.ui.addOrUpdateModal.title === '添加前端工程') {
              operateApi.addUi({...values}).then(res => {
                this.$message.info(res.message)
                this.init()
                this.ui.addOrUpdateModal.loading = false
                this.ui.addOrUpdateModal.visible = false
              })
            } else {
              operateApi.editUi({...values, uiId: this.ui.addOrUpdateModal.data.uiId}).then(res => {
                this.$message.info(res.message)
                this.init()
                this.ui.addOrUpdateModal.loading = false
                this.ui.addOrUpdateModal.visible = false
              })
            }
          }
        })
      },
      uiOnClick (record) {
        return {
          on: {
            click: () => {
              this.ui.rowSelectData = record
              this.ui.rowSelection.selectedRowKeys = [record.uiId]
            }
          }
        }
      }
    }
  }
</script>

<style scoped>

</style>
