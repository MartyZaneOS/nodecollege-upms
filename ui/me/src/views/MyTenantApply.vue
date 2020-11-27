<template>
  <a-row style="padding: 10px;">
    <a-col span="24" style="margin: 10px 0">
      <a-button type="primary" @click="modal.visible = true">申请注册企业</a-button>
    </a-col>
    <a-col span="24">
      <a-table :dataSource="data" :columns="columns" :pagination="pagination" :loading="loading" size="small" bordered
               :rowKey="record => record.id">
      </a-table>
      <NCPagination :page="pageObj" @changePage="(page)=>{this.pageObj = page}" @searchList="searchList"/>
    </a-col>
    <!-- 注册企业 -->
    <a-modal v-model="modal.visible" title="注册企业">
      <a-form :form="modal.form" :label-col="modal.labelCol" :wrapper-col="modal.wrapperCol">
        <a-form-item label="企业名称">
          <a-input placeholder="请输入企业名称！"
                   v-decorator="['tenantName', {rules: [{ required: true, message: '请输入企业名称！'}]}]"/>
        </a-form-item>
        <a-form-item label="企业代码">
          <a-input placeholder="请输入企业代码！"
                   v-decorator="['tenantCode', {rules: [{ required: true, message: '请输入企业税号！'}]}]"/>
        </a-form-item>
        <a-form-item label="企业简介">
          <a-input placeholder="请输入企业简介！"
                   v-decorator="['tenantDesc', {rules: [{ required: true, message: '请输入企业简介！'}]}]"/>
        </a-form-item>
        <a-form-item label="申请人联系邮箱">
          <a-input placeholder="请输入申请人联系邮箱！"
                   v-decorator="['applyEmail', {rules: [{ required: true, message: '请输入申请人联系邮箱！'}]}]"/>
        </a-form-item>
      </a-form>
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="modal.loading" @click="registerTenantOk">
          确定
        </a-button>
        <a-button key="back" @click="modal.visible = false">取消</a-button>
      </template>
    </a-modal>
  </a-row>
</template>

<script>
  import {NCPagination} from 'common'
  import * as operateApi from '../api/operate'

  export default {
    name: 'MyTenantApply',
    components: {
      NCPagination
    },
    data () {
      return {
        pageObj: {total: 10, pageSize: 10, pageNum: 1, pageSizeOptions: ['5', '10', '20', '50', '100']},
        rolePageObj: {total: 10, pageSize: 10, pageNum: 1, pageSizeOptions: ['5', '10', '20', '50', '100']},
        loading: false,
        pagination: false,
        data: [],
        columns: [
          {title: '序号', width: '20', customRender: (text, record, index) => { return index + this.pageObj.pageSize * (this.pageObj.pageNum - 1) + 1 }},
          {title: '企业名称', dataIndex: 'tenantName'},
          {title: '企业税号', dataIndex: 'tenantCode'},
          {title: '企业描述', dataIndex: 'tenantDesc'},
          {title: '联系邮箱', dataIndex: 'applyEmail'},
          {title: '创建时间', dataIndex: 'createTime'},
          {title: '状态', dataIndex: 'state', customRender: (text, record, index) => { return text === -1 ? '拒绝' : text === 0 ? '待审核' : text === 1 ? '通过' : '' }}
        ],
        modal: {
          labelCol: {span: 7},
          wrapperCol: {span: 12},
          loading: false,
          visible: false,
          form: this.$form.createForm(this)
        }
      }
    },
    mounted () {
      this.searchList()
    },
    methods: {
      searchList () {
        this.loading = true
        operateApi.getTenantApplyListByUserId({
          pageNum: this.pageObj.pageNum,
          pageSize: this.pageObj.pageSize,
          data: {}
        }).then((res) => {
          if (res.success) {
            this.data = res.rows
            this.pageObj.total = res.total
          } else {
            this.$message.error(res.message)
          }
          this.loading = false
        })
      },
      registerTenantOk () {
        this.modal.form.validateFields((err, values) => {
          if (!err) {
            this.modal.loading = true
            operateApi.addTenantApply({
              ...values
            }).then((res) => {
              if (res.success) {
                this.$message.info(res.message)
                this.searchList()
                this.modal.visible = false
              }
              this.modal.loading = false
            })
          }
        })
      }
    }
  }
</script>

<style scoped>

</style>
