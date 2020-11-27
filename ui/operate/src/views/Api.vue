<template>
  <a-row style="padding: 10px;">
    <a-col span="24">
      <a-table :dataSource="table.data" :columns="table.columns" :pagination="table.pagination"
               size="small" bordered :rowKey="record => (record.applicationName + record.controllerName + record.apiId)">
      </a-table>
    </a-col>
  </a-row>
</template>

<script>
  import * as operateApi from '../api/operate'
  export default {
    name: 'Api',
    data () {
      return {
        table: {
          pagination: false,
          loading: false,
          data: [],
          columns: [
            {title: 'appName', dataIndex: 'applicationName', width: 150},
            {title: 'controller', dataIndex: 'controllerName', width: 300},
            {title: 'apiUrl', dataIndex: 'apiUrl', width: 300},
            {title: '模块名称', dataIndex: 'modularName'},
            {title: '描述', dataIndex: 'description'},
            {title: '访问授权', dataIndex: 'accessAuth', customRender: (text, record, index) => { return text === 0 ? '无限制' : text === 1 ? '登录访问' : text === 2 ? '授权访问' : '' }},
            {title: '状态', dataIndex: 'state', customRender: (text, record, index) => { return text === -1 ? '已删除' : text === 1 ? '正常' : text === 2 ? '该接口后期删除' : '' }}
          ]
        }
      }
    },
    mounted () {
      this.searchList()
    },
    methods: {
      // 查询应用资源树
      searchList () {
        this.loading = true
        operateApi.getAppApiTree({}).then((res) => {
          if (res.success) {
            this.table.data = res.rows
          }
        })
      }
    }
  }
</script>

<style scoped>

</style>
