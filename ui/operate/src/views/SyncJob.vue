<template>
  <a-row style="padding: 10px;">
    <a-col span="24" style="margin: 10px 0">
      <a-button type="primary" @click="addSync">复制任务</a-button>
    </a-col>
    <a-col span="24">
      <a-table :dataSource="data" :columns="columns" :pagination="pagination" :loading="loading" size="small" bordered
               :rowKey="record => record.jobId" :scroll="{ x: 2500 }">
        <template slot="operation" slot-scope="text, record, index">
          <div>
            <span>
              <a @click="() => updateSync(record)">编辑</a>
              <a v-if="record.jobStatus === 0" @click="() => lockSync(record)">启动</a>
              <a v-if="record.jobStatus === 1" @click="() => lockSync(record)">暂停</a>
              <a @click="() => showLog(record)">执行日志</a>
              <a v-if="record.jobType == 1" @click="() => delSync(record)">删除</a>
            </span>
          </div>
        </template>
        <template slot="name" slot-scope="text, record, index">
          <div class="smileDark" :title="text">{{text}}</div>
        </template>
      </a-table>
      <NCPagination :page="pageObj" @changePage="(page)=>{this.pageObj = page}" @searchList="searchList"/>
    </a-col>
    <a-modal v-model="model.visible" :title="model.title" width="800px">
      <a-form :form="model.form" :label-col="model.labelCol" :wrapper-col="model.wrapperCol">
        <a-form-item label="任务名称" v-if="model.title === '添加任务'">
          <a-input placeholder="请输入任务名称！" v-decorator="['jobName', {rules: [{ required: true, message: '请输入任务名称！'}]}]"/>
        </a-form-item>
        <a-form-item label="任务组名" v-if="model.title === '添加任务'">
          <a-input placeholder="请输入任务组名！" v-decorator="['jobGroup', {rules: [{ required: true, message: '请输入任务组名！'}]}]"/>
        </a-form-item>
        <a-form-item label="任务描述">
          <a-textarea :rows="4" placeholder="请输入任务描述！" v-decorator="['description']"/>
        </a-form-item>
        <a-form-item label="任务类" v-if="model.title === '添加任务'">
          <a-select placeholder="请选择任务类！" v-decorator="['jobClass', {rules: [{ required: true, message: '请选择任务类！'}]}]" style="width: 500px">
            <a-select-option v-for="jobClass in model.jobClassList" :value="jobClass" :key="jobClass">{{jobClass}}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="任务参数">
          <a-textarea :rows="4" placeholder="请输入任务参数！" v-decorator="['jobParam']"/>
        </a-form-item>
        <a-form-item label="cron">
          <a-input placeholder="请输入cron！" v-decorator="['cronExpression', {rules: [{ required: true, message: '请输入cron！'}]}]"/>
        </a-form-item>
        <a-form-item label="任务状态">
          <a-select placeholder="请选择任务状态！" v-decorator="['jobStatus', {rules: [{ required: true, message: '请选择任务状态！'}]}]">
            <a-select-option value="0">暂停</a-select-option>
            <a-select-option value="1">启动</a-select-option>
            <a-select-option value="2" disabled>无法执行</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="计划策略">
          <a-select placeholder="请选择计划策略！" v-decorator="['misfirePolicy', {rules: [{ required: true, message: '请选择计划策略！'}]}]">
            <a-select-option value="0">默认</a-select-option>
            <a-select-option value="1">立即触发执行</a-select-option>
            <a-select-option value="2">触发一次执行</a-select-option>
            <a-select-option value="3">不触发立即执行</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="model.loading" @click="addOrUpdateSyncOk">
          确定
        </a-button>
        <a-button key="back" @click="model.visible = false">取消</a-button>
      </template>
    </a-modal>
    <a-modal v-model="logModal.visible" title="执行日志信息" width="1200px">
      <a-table :dataSource="logModal.data" :columns="logModal.columns" :pagination="logModal.pagination" :loading="logModal.loading" size="small" bordered
               :rowKey="record => record.jobLogId">
      </a-table>
      <NCPagination :page="logModal.pageObj" @changePage="(page)=>{this.logModal.pageObj = page}" @searchList="searchLogList"/>
      <template slot="footer">
        <a-button key="back" @click="logModal.visible = false">取消</a-button>
      </template>
    </a-modal>
  </a-row>
</template>

<script>
  import {NCPagination} from 'common'
  import * as operateApi from '../api/operate'

  export default {
    name: 'Sync',
    components: {
      NCPagination
    },
    data () {
      return {
        pageObj: {total: 10, pageSize: 10, pageNum: 1},
        search: {
          account: '',
          phone: ''
        },
        loading: false,
        data: [],
        pagination: false,
        columns: [
          {title: '任务名称', dataIndex: 'jobName', width: 150, fixed: 'left'},
          {title: '任务组名', dataIndex: 'jobGroup', width: 150},
          {title: '任务类', dataIndex: 'jobClass'},
          {title: '任务参数', dataIndex: 'jobParam', scopedSlots: {customRender: 'name'}},
          {title: 'cron', dataIndex: 'cronExpression', width: 150},
          {title: '任务状态', dataIndex: 'jobStatus', width: 150, customRender: (text, record, index) => { return text === 0 ? '暂停' : text === 1 ? '启动' : '无法执行' }},
          // {title: '计划策略', dataIndex: 'misfirePolicy', customRender: (text, record, index) => { return text === 0 ? '默认' : text === 1 ? '立即触发执行' : text === 2 ? '触发一次执行' : '不触发立即执行' }},
          {title: '更新用户', dataIndex: 'updateUser', width: 150},
          {title: '更新时间', dataIndex: 'updateTime', width: 150},
          {title: '创建时间', dataIndex: 'createTime', width: 150},
          {title: '任务描述', dataIndex: 'description'},
          {title: '任务类型', dataIndex: 'jobType', width: 200, customRender: (text, record, index) => { return text === 0 ? '原始任务（不可删除）' : '复制任务' }},
          {title: '操作', fixed: 'right', width: 150, scopedSlots: {customRender: 'operation'}}
        ],
        model: {
          labelCol: {span: 7},
          wrapperCol: {span: 12},
          loading: false,
          jobClassList: [],
          visible: false,
          title: '复制任务',
          form: this.$form.createForm(this),
          data: {}
        },
        logModal: {
          pagination: false,
          pageObj: {total: 10, pageSize: 10, pageNum: 1},
          loading: false,
          visible: false,
          form: this.$form.createForm(this),
          job: {},
          data: [],
          columns: [
            {title: '序号', width: '20', customRender: (text, record, index) => { return index + this.logModal.pageObj.pageSize * (this.logModal.pageObj.pageNum - 1) + 1 }},
            {title: '任务名称', dataIndex: 'jobName'},
            {title: '任务组名', dataIndex: 'jobGroup'},
            {title: '日志消息', dataIndex: 'logMessage'},
            {title: '日志状态', dataIndex: 'logState', customRender: (text, record, index) => { return text === 0 ? '正常' : '异常' }},
            {title: '耗时', dataIndex: 'lostTime'},
            {title: '时间', dataIndex: 'createTime'},
            {title: '错误日志信息', dataIndex: 'errorMessage'}
          ]
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
        operateApi.getJobList({
          pageNum: this.pageObj.pageNum,
          pageSize: this.pageObj.pageSize,
          data: {}
        }).then((res) => {
          if (res.success) {
            this.data = res.rows
            this.pageObj.total = res.total
          }
          this.loading = false
        })
      },
      // 复制任务
      addSync () {
        this.model.title = '复制任务'
        this.model.data = {}
        this.model.form.resetFields()
        if (this.model.jobClassList.length === 0) {
          operateApi.getJobClassList({}).then((res) => {
            if (res.success) {
              this.model.jobClassList = res.rows
            }
          })
        }
        this.model.visible = true
      },
      // 更新任务
      updateSync (record) {
        this.model.title = '修改任务'
        this.model.data = record
        if (this.model.jobClassList.length === 0) {
          operateApi.getJobClassList({}).then((res) => {
            if (res.success) {
              this.model.jobClassList = res.rows
            }
          })
        }
        setTimeout(() => {
          this.model.form.setFieldsValue({
            description: this.model.data.description,
            jobParam: this.model.data.jobParam,
            cronExpression: this.model.data.cronExpression,
            jobStatus: this.model.data.jobStatus + '',
            misfirePolicy: this.model.data.misfirePolicy + ''
          })
        }, 100)
        this.model.visible = true
      },
      // 删除任务
      delSync (record) {
        const that = this
        this.$confirm({
          title: '删除任务！?',
          onOk () {
            return new Promise((resolve, reject) => {
              operateApi.delJob({jobId: record.jobId}).then((res) => {
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
      // 锁定/解锁任务
      lockSync (record) {
        console.log('锁定任务', record)
        const that = this
        this.$confirm({
          title: record.jobStatus === 0 ? '暂停任务！?' : '启动任务！',
          onOk () {
            return new Promise((resolve, reject) => {
              operateApi.pauseJob({
                jobId: record.jobId,
                jobStatus: record.jobStatus === 0 ? 1 : 0
              }).then((res) => {
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
      // 复制或者更新任务
      addOrUpdateSyncOk (e) {
        e.preventDefault()
        this.model.form.validateFields((err, values) => {
          if (!err) {
            console.log('Received values of form: ', values)
            this.model.loading = true
            if (this.model.title === '复制任务') {
              operateApi.addJob({
                ...values
              }).then((res) => {
                if (res.success) {
                  this.$message.info(res.message)
                  this.searchList()
                  this.model.visible = false
                }
                this.model.loading = false
              })
            } else {
              operateApi.editJob({
                jobId: this.model.data.jobId,
                ...values
              }).then((res) => {
                if (res.success) {
                  this.$message.info(res.message)
                  this.searchList()
                  this.model.visible = false
                }
                this.model.loading = false
              })
            }
          }
        })
      },
      showLog (record) {
        this.logModal.job = record
        this.searchLogList()
        this.logModal.visible = true
      },
      // 查询数据
      searchLogList () {
        this.logModal.loading = true
        operateApi.getJobLogList({
          pageNum: this.logModal.pageObj.pageNum,
          pageSize: this.logModal.pageObj.pageSize,
          data: {jobId: this.logModal.job.jobId}
        }).then((res) => {
          if (res.success) {
            this.logModal.data = res.rows
            this.logModal.pageObj.total = res.total
          }
          this.logModal.loading = false
        })
      }
    }
  }
</script>

<style scoped>
  .smileDark {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    display: inline-block;
    width: 200px;
  }
</style>
