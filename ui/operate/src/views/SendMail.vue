<template>
  <a-row style="padding: 10px;">
    <a-col span="24">
      <a-form layout="inline">
        <a-form-item label="收件人">
          <a-input v-model="search.toMail" placeholder="请输入收件人！"></a-input>
        </a-form-item>
        <a-form-item label="标题">
          <a-input v-model="search.title" placeholder="请输入标题！"></a-input>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="searchList">查询</a-button>
        </a-form-item>
      </a-form>
    </a-col>
    <a-col span="24" style="margin: 10px 0">
      <a-button type="primary" @click="sendMail">发送邮件</a-button>
    </a-col>
    <a-col span="24">
      <a-table :dataSource="data" :columns="columns" :pagination="pagination" :loading="loading" size="small" bordered
               :rowKey="record => record.id">
        <template slot="operation" slot-scope="text, record, index">
          <div>
            <span>
              <a @click="() => info(record)">详情</a>
            </span>
          </div>
        </template>
      </a-table>
      <NCPagination :page="pageObj" @changePage="(page)=>{this.pageObj = page}" @searchList="searchList"/>
    </a-col>
    <a-modal v-model="model.visible" :title="model.title">
      <a-form :form="model.form" :label-col="model.labelCol" :wrapper-col="model.wrapperCol">
        <a-form-item label="收件人">
          <a-input placeholder="请输入收件人！" :disabled="model.title !== '发送邮件'"
                   v-decorator="['toMail', {rules: [{ required: true, message: '请输入收件人！'}]}]"/>
        </a-form-item>
        <a-form-item label="抄送人">
          <a-input placeholder="请输入抄送人！" :disabled="model.title !== '发送邮件'"
                   v-decorator="['replyTo']"/>
        </a-form-item>
        <a-form-item label="标题">
          <a-input placeholder="请输入标题！" :disabled="model.title !== '发送邮件'"
                   v-decorator="['title', {rules: [{ required: true, message: '请输入标题！'}]}]"/>
        </a-form-item>
        <a-form-item label="验证码" class="form-item" v-if="model.title === '发送邮件'">
          <a-input placeholder="验证码"
                   v-decorator="['imageCert', {rules: [{ required: true, message: '请输入验证码！'}]}]"/>
          <img width="110" height="40px" alt="验证码" onclick="this.src='/operateApi/common/createImageCert?d=' + new Date()*1" src="/operateApi/common/createImageCert" />
        </a-form-item>
        <a-form-item label="内容">
          <a-textarea :rows="4" placeholder="请输入内容！" :disabled="model.title !== '发送邮件'"
                   v-decorator="['contentStr', {rules: [{ required: true, message: '请输入内容！'}]}]"/>
        </a-form-item>
      </a-form>
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="model.loading" @click="sendMailOk" v-if="model.title === '发送邮件'">
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
    name: 'SendMail',
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
          {title: '邮件类型', dataIndex: 'mailType', customRender: (text, record, index) => { return text === 0 ? '验证码' : '管理员发送' }},
          {title: '标题', dataIndex: 'title'},
          {title: '收件人', dataIndex: 'toMail'},
          {title: '抄送', dataIndex: 'replayTo'},
          {title: '内容类型', dataIndex: 'contentType', customRender: (text, record, index) => { return text === 1 ? 'html内容' : '文本内容' }},
          {title: '状态', dataIndex: 'state', customRender: (text, record, index) => { return text === 1 ? '发送成功' : '发送失败' }},
          {title: '创建时间', dataIndex: 'createTime'},
          {title: '操作', fixed: 'right', width: 50, scopedSlots: {customRender: 'operation'}}
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
        operateApi.getSendMailList({
          pageNum: this.pageObj.pageNum,
          pageSize: this.pageObj.pageSize,
          data: {
            toMail: this.search.toMail,
            title: this.search.title
          }
        }).then((res) => {
          if (res.success) {
            this.data = res.rows
            this.pageObj.total = res.total
          }
          this.loading = false
        })
      },
      // 发送邮件
      sendMail () {
        this.model.visible = true
        this.model.title = '发送邮件'
        this.model.form.resetFields()
      },
      // 邮件详情
      info (mail) {
        operateApi.getMailInfo({id: mail.id}).then((res) => {
          if (res.success) {
            this.model.visible = true
            this.model.title = '邮件详情'
            this.model.data = res.rows[0]
            setTimeout(() => {
              this.model.form.setFieldsValue({
                mailType: this.model.data.mailType,
                title: this.model.data.title,
                toMail: this.model.data.toMail,
                replayTo: this.model.data.replayTo,
                contentType: this.model.data.contentType,
                contentStr: this.model.data.contentStr
              })
            }, 100)
          }
        })
      },
      // 发送邮件
      sendMailOk () {
        this.model.form.validateFields((err, values) => {
          if (!err) {
            console.log('Received values of form: ', values)
            this.model.loading = true
            operateApi.sendMail({
              contentType: 0,
              content: values.contentStr,
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
