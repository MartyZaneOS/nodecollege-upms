<template>
  <a-row style="padding: 10px;">
    <a-col span="24">
      <a-form layout="inline">
        <a-form-item label="日期">
          <a-range-picker :placeholder="['开始日期', '结束日期']" @change="rangeChange"/>
        </a-form-item>
        <a-form-item label="请求id">
          <a-input v-model="search.requestId" placeholder="请输入请求id！"></a-input>
        </a-form-item>
        <a-form-item label="访问来源">
          <a-input v-model="search.accessSource" placeholder="请输入访问来源！"></a-input>
        </a-form-item>
        <a-form-item label="服务名称">
          <a-input v-model="search.appName" placeholder="请输入服务名称！"></a-input>
        </a-form-item>
        <a-form-item label="请求地址">
          <a-input v-model="search.requestUri" placeholder="请输入请求地址！"></a-input>
        </a-form-item>
        <a-form-item label="请求ip">
          <a-input v-model="search.requestIp" placeholder="请输入请求ip！"></a-input>
        </a-form-item>
        <a-form-item label="请求是否成功">
          <a-select allowClear v-model="search.success" placeholder="请求是否成功！" style="width: 150px;">
            <a-select-option value="0">失败</a-select-option>
            <a-select-option value="1">成功</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="管理员账号">
          <a-input v-model="search.adminAccount" placeholder="请输入管理员账号！"></a-input>
        </a-form-item>
        <a-form-item label="用户账号">
          <a-input v-model="search.userAccount" placeholder="请输入用户账号！"></a-input>
        </a-form-item>
        <a-form-item label="租户成员账号">
          <a-input v-model="search.memberAccount" placeholder="请输入租户成员账号！"></a-input>
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
               :rowKey="record => record.id" :scroll="{ x: 2500 }">
        <template slot="name" slot-scope="text, record, index">
          <div class="smileDark" :title="text">{{text}}</div>
        </template>
      </a-table>
      <NCPagination :page="pageObj" @changePage="(page)=>{this.pageObj = page}" @searchList="searchList"/>
    </a-col>
  </a-row>
</template>

<script>
  import {NCPagination} from 'common'
  import * as operateApi from '../api/operate'

  export default {
    name: 'Route',
    components: {
      NCPagination
    },
    data () {
      return {
        pageObj: {total: 10, pageSize: 10, pageNum: 1},
        loading: false,
        search: {
          requestId: undefined,
          appName: undefined,
          accessSource: undefined,
          requestUri: undefined,
          requestIp: undefined,
          success: undefined,
          adminAccount: undefined,
          userAccount: undefined,
          memberAccount: undefined,
          tenantCode: undefined
        },
        startTime: undefined,
        endTime: undefined,
        data: [],
        pagination: false,
        columns: [
          {title: '请求id', dataIndex: 'requestId', width: 150, fixed: 'left'},
          {title: '链路id', dataIndex: 'callIds', width: 200},
          {title: '请求时间', dataIndex: 'createTime', width: 150},
          {title: '访问来源', dataIndex: 'accessSource', width: 120},
          {title: '服务名称', dataIndex: 'appName', width: 80},
          {title: '成功', dataIndex: 'success', width: 50, customRender: (text, record, index) => { return text ? '成功' : '失败' }},
          {title: '耗时(ms)', dataIndex: 'lostTime', width: 80},
          {title: '请求ip', dataIndex: 'requestIp', width: 130},
          {title: '请求地址', dataIndex: 'requestUri', width: 250},
          {title: 'Referer', dataIndex: 'referer'},
          {title: '入参', dataIndex: 'inParam', width: 120, scopedSlots: {customRender: 'name'}},
          {title: '出参', dataIndex: 'outParam', width: 120, scopedSlots: {customRender: 'name'}},
          {title: '管理员账号', dataIndex: 'adminAccount', width: 120},
          {title: '用户账号', dataIndex: 'userAccount', width: 120},
          {title: '租户成员账号', dataIndex: 'memberAccount', width: 120},
          {title: '租户代码', dataIndex: 'tenantCode', width: 120}
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
        operateApi.getSysLogList({
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
    width: 100px;
  }
</style>
