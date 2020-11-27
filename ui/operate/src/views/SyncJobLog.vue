<template>
  <a-row style="padding: 10px;">
    <a-col span="24">
      <a-form layout="inline">
        <a-form-item label="日期">
          <a-range-picker :placeholder="['开始日期', '结束日期']" @change="rangeChange"/>
        </a-form-item>
        <a-form-item label="任务名称">
          <a-input v-model="search.jobName" placeholder="请输入请求id！"></a-input>
        </a-form-item>
        <a-form-item label="任务组名">
          <a-input v-model="search.jobGroup" placeholder="请输入服务名称！"></a-input>
        </a-form-item>
        <a-form-item label="日志状态">
          <a-select allowClear v-model="search.logState" placeholder="请求是否成功！" style="width: 150px;">
            <a-select-option value="1">异常</a-select-option>
            <a-select-option value="0">正常</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="searchList">查询</a-button>
        </a-form-item>
      </a-form>
    </a-col>
    <a-col span="24">
      <a-table :dataSource="data" :columns="columns" :pagination="pagination" :loading="loading" size="small" bordered
               :rowKey="record => record.jobLogId">
      </a-table>
      <NCPagination :page="pageObj" @changePage="(page)=>{this.pageObj = page}" @searchList="searchList"/>
    </a-col>
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
          jobName: undefined,
          jobGroup: undefined,
          logState: undefined
        },
        loading: false,
        startTime: undefined,
        endTime: undefined,
        data: [],
        pagination: false,
        columns: [
          {title: '序号', width: '20', customRender: (text, record, index) => { return index + this.pageObj.pageSize * (this.pageObj.pageNum - 1) + 1 }},
          {title: '任务名称', dataIndex: 'jobName'},
          {title: '任务组名', dataIndex: 'jobGroup'},
          {title: '日志消息', dataIndex: 'logMessage'},
          {title: '日志状态', dataIndex: 'logState', customRender: (text, record, index) => { return text === 0 ? '正常' : '异常' }},
          {title: '耗时', dataIndex: 'lostTime'},
          {title: '时间', dataIndex: 'createTime'},
          {title: '错误日志信息', dataIndex: 'errorMessage'}
        ]
      }
    },
    mounted () {
      this.searchList()
    },
    methods: {
      // 查询数据
      searchList () {
        this.loading = true
        operateApi.getJobLogList({
          pageNum: this.pageObj.pageNum,
          pageSize: this.pageObj.pageSize,
          startTime: this.startTime,
          endTime: this.endTime,
          data: {...this.search}
        }).then((res) => {
          if (res.success) {
            this.data = res.rows
            this.pageObj.total = res.total
          }
          this.loading = false
        })
      },
      rangeChange (date, dateString) {
        console.log(date, dateString)
        this.startTime = dateString[0] + ' 00:00:00'
        this.endTime = dateString[1] + ' 23:59:59'
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
