<template>
  <a-row style="padding: 10px;">
    <a-col span="24">
      <a-table :dataSource="data" :columns="columns" :pagination="pagination" :loading="loading" size="small" bordered
               :rowKey="record => record.id">
        <template slot="operation" slot-scope="text, record, index">
          <div>
            <span>
              <a @click="() => showModal(record)" v-if="record.state === 0">审核</a>
            </span>
          </div>
        </template>
      </a-table>
      <NCPagination :page="pageObj" @changePage="(page)=>{this.pageObj = page}" @searchList="searchList"/>
    </a-col>
    <!-- 注册企业 -->
    <a-modal v-model="modal.visible" title="注册企业">
      <a-form :form="modal.form" :label-col="modal.labelCol" :wrapper-col="modal.wrapperCol">
        <a-form-item  label="企业名称">
          {{modal.data.tenantName}}
        </a-form-item>
        <a-form-item label="企业代码">
          {{modal.data.tenantCode}}
        </a-form-item>
        <a-form-item label="企业简介">
          {{modal.data.tenantDesc}}
        </a-form-item>
        <a-form-item label="申请人联系邮箱">
          {{modal.data.applyEmail}}
        </a-form-item>
        <a-form-item label="审核意见">
          <a-input placeholder="请输入审核意见！"
                   v-decorator="['remarks', {rules: [{ required: true, message: '请输入审核意见！'}]}]"/>
        </a-form-item>
        <a-form-item label="审核结果">
          <a-select placeholder="请选择审核结果！" v-decorator="['state', {rules: [{ required: true, message: '请选择审核结果！'}]}]">
            <a-select-option value="-1">拒绝</a-select-option>
            <a-select-option value="1">通过</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="modal.loading" @click="applyTenantOk">
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
          {title: '审核时间', dataIndex: 'modifyTime'},
          {title: '审核人员', dataIndex: 'modifyName'},
          {title: '状态', dataIndex: 'state', customRender: (text, record, index) => { return text === -1 ? '拒绝' : text === 0 ? '待审核' : text === 1 ? '通过' : '' }},
          {title: '操作', fixed: 'right', width: 150, scopedSlots: {customRender: 'operation'}}
        ],
        modal: {
          labelCol: {span: 7},
          wrapperCol: {span: 12},
          loading: false,
          visible: false,
          form: this.$form.createForm(this),
          data: {}
        }
      }
    },
    mounted () {
      this.searchList()
    },
    methods: {
      searchList () {
        this.loading = true
        operateApi.getTenantApplyList({
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
      showModal (record) {
        this.modal.data = record
        this.modal.visible = true
      },
      applyTenantOk () {
        this.modal.form.validateFields((err, values) => {
          if (!err) {
            this.modal.loading = true
            operateApi.editTenantApply({
              id: this.modal.data.id,
              ...values
            }).then((res) => {
              if (res.success) {
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
