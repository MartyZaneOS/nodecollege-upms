<template>
  <a-row style="padding: 10px;">
    <a-col span="24">
      <a-table :dataSource="data" :columns="columns" :pagination="pagination" :loading="loading" size="small" bordered
               :rowKey="record => record.tenantId">
        <template slot="operation" slot-scope="text, record, index">
          <div>
            <span>
              <a @click="() => setDefaultTenant(record)" v-if="record.tenantId != $store.state.userInfo.tenantId">设为默认登陆企业</a>
            </span>
          </div>
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
    name: 'MyTenant',
    components: {
      NCPagination
    },
    data () {
      return {
        pageObj: {total: 10, pageSize: 10, pageNum: 1, pageSizeOptions: ['5', '10', '20', '50', '100']},
        loading: false,
        pagination: false,
        data: [],
        columns: [
          {title: '序号', width: '20', customRender: (text, record, index) => { return index + this.pageObj.pageSize * (this.pageObj.pageNum - 1) + 1 }},
          {title: '企业名称', dataIndex: 'tenantName'},
          {title: '企业税号', dataIndex: 'tenantCode'},
          {title: '企业描述', dataIndex: 'introduce'},
          {title: '创建时间', dataIndex: 'createTime'},
          {title: '状态', dataIndex: 'state', customRender: (text, record, index) => { return text === 2 ? '锁定' : '正常' }},
          {title: '操作', fixed: 'right', width: 200, scopedSlots: {customRender: 'operation'}}
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
        operateApi.getTenantList({
          pageNum: this.pageObj.pageNum,
          pageSize: this.pageObj.pageSize
        }).then((res) => {
          if (res.success) {
            this.data = res.rows
            this.pageObj.total = res.total
          }
          this.loading = false
        })
      },
      // 设置为默认租户
      setDefaultTenant (record) {
        const that = this
        this.$confirm({
          title: '设置并切换默认企业！?',
          onOk () {
            operateApi.setDefaultTenant({tenantId: record.tenantId}).then((res) => {
              if (res.success) {
                that.$store.dispatch('getUserInfo')
                that.$store.state.selectHead = null
              }
            })
          }
        })
      }
    }
  }
</script>

<style scoped>

</style>
