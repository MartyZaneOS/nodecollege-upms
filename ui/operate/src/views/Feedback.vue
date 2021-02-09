<template>
  <a-row style="padding: 10px;">
    <a-col span="24">
      <a-form layout="inline">
        <a-form-item label="来源">
          <a-select allowClear v-model="search.feedbackSource" placeholder="来源！" style="width: 150px;">
            <a-select-option :value="0">首页</a-select-option>
            <a-select-option :value="1">用户</a-select-option>
            <a-select-option :value="2">租户</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="租户代码">
          <a-input v-model="search.tenantCode" placeholder="请输入租户代码！"></a-input>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="searchList">查询</a-button>
        </a-form-item>
      </a-form>
    </a-col>
    <a-col span="24">
      <a-table :dataSource="data" :columns="columns" :pagination="pagination" :loading="loading" size="small" bordered
               :rowKey="record => record.id" childrenColumnName="replyList">
        <template slot="operation" slot-scope="text, record, index">
          <div>
            <span>
              <a v-if="record.isReply === 0 && record.state === 1" @click="() => reply(record, index, false)">回复</a>
              <a v-if="record.isReply === 0 && record.state === 1" @click="() => reply(record, index, true)">邮件回复</a>
              <a v-if="record.state === 1" @click="() => show(record)">隐藏</a>
              <a v-if="record.state === 0" @click="() => show(record)">显示</a>
            </span>
          </div>
        </template>
      </a-table>
      <NCPagination :page="pageObj" @changePage="(page)=>{this.pageObj = page}" @searchList="searchList"/>
    </a-col>
    <a-modal v-model="model.visible" :title="model.title">
      <a-form :form="model.form" :label-col="model.labelCol" :wrapper-col="model.wrapperCol">
        <a-form-item label="邮箱" class="form-item" v-if="model.title === '邮件回复'">
          <a-input placeholder="邮箱" v-decorator="['userEmail', {rules: [{ required: true, message: '请输入邮箱！'}]}]"/>
        </a-form-item>
        <a-form-item label="内容">
          <a-textarea :rows="4" placeholder="请输入内容！"
                      v-decorator="['contentStr', {rules: [{ required: true, message: '请输入内容！'}]}]"/>
        </a-form-item>
        <a-form-item label="验证码" class="form-item">
          <a-input placeholder="验证码"
                   v-decorator="['imageCert', {rules: [{ required: true, message: '请输入验证码！'}]}]"/>
          <img width="110" height="40px" alt="验证码" onclick="this.src='/operateApi/common/createImageCert?d=' + new Date()*1" src="/operateApi/common/createImageCert" />
        </a-form-item>
      </a-form>
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="model.loading" @click="replyOk">
          确定
        </a-button>
        <a-button key="back" @click="model.visible = false">取消</a-button>
      </template>
    </a-modal>
  </a-row>
</template>

<script>
  import {NCPagination} from 'common'
  import * as operateApi from '../api/operate'

  export default {
    name: '意见反馈',
    components: {
      NCPagination
    },
    data () {
      return {
        pageObj: {total: 10, pageSize: 10, pageNum: 1},
        search: {
          toMail: '',
          title: ''
        },
        loading: false,
        data: [],
        pagination: false,
        columns: [
          {title: '来源', dataIndex: 'feedbackSource', customRender: (text, record, index) => { return text === 0 ? '首页' : text === 1 ? '用户' : '租户' }},
          {title: '用户名', dataIndex: 'userName'},
          {title: '用户邮箱', dataIndex: 'userEmail'},
          {title: '内容', dataIndex: 'contentStr'},
          {title: '状态', dataIndex: 'state', customRender: (text, record, index) => { return text === 0 ? '隐藏' : text === 1 ? '显示' : '已删除' }},
          {title: '创建时间', dataIndex: 'createTime'},
          {title: '用户id', dataIndex: 'userId'},
          {title: '租户代码', dataIndex: 'tenantCode'},
          {title: '成员id', dataIndex: 'memberId'},
          {title: '操作', fixed: 'right', width: 150, scopedSlots: {customRender: 'operation'}}
        ],
        model: {
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
      // 查询数据
      searchList () {
        this.loading = true
        operateApi.getFeedbackList({
          pageNum: this.pageObj.pageNum,
          pageSize: this.pageObj.pageSize,
          data: {
            feedbackSource: this.search.feedbackSource,
            tenantCode: this.search.tenantCode
          }
        }).then((res) => {
          if (res.success) {
            this.data = res.rows
            this.pageObj.total = res.total
          }
          this.loading = false
        })
      },
      // 回复
      reply (record, index, email) {
        this.model.visible = true
        this.model.title = email ? '邮件回复' : '回复'
        this.model.form.resetFields()
        if (email) {
          setTimeout(() => {
            this.model.form.setFieldsValue({
              userEmail: record.userEmail
            })
          }, 100)
        }
        this.model.data = record
      },
      // 显示/隐藏
      show (record) {
        operateApi.editFeedback({id: record.id, state: record.state === 1 ? 0 : 1}).then((res) => {
          if (res.success) {
            this.searchList()
          }
        })
      },
      // 回复ok
      replyOk () {
        this.model.form.validateFields((err, values) => {
          if (!err) {
            this.model.loading = true
            operateApi.replyFeedback({
              replyId: this.model.data.id,
              replyEmailId: this.model.title === '邮件回复' ? 99999 : null,
              ...values
            }).then((res) => {
              if (res.success) {
                this.searchList()
                this.model.visible = false
              }
              this.model.loading = false
            })
          }
        })
      }
    }
  }
</script>

<style scoped>

</style>
