<template>
  <a-row style="padding: 10px;">
    <a-col span="24">
      <p>注：1. 网关微服务配置文件中配置的网关路由不在此处显示。2. 每次新增、修改、删除操作后，半分钟内生效。3. 自动给所有网关路由添加自定义过滤器NCGatewayFilterFactory用于访问控制，和日志统计处理。</p>
    </a-col>
    <a-col span="24" style="margin: 10px 0">
      <a-button type="primary" @click="addRoute">新增网关路由</a-button>
    </a-col>
    <a-col span="24">
      <a-table :dataSource="data" :columns="columns" :pagination="pagination" :loading="loading" size="small" bordered
               :rowKey="record => record.routeId">
        <template slot="operation" slot-scope="text, record, index">
          <div>
            <span v-if="record.routeState !== 0">
              <a @click="() => updateRoute(record)">编辑</a>
              <a @click="() => delRoute(record)">删除</a>
            </span>
          </div>
        </template>
      </a-table>
      <NCPagination :page="pageObj" @changePage="(page)=>{this.pageObj = page}" @searchList="searchList"/>
    </a-col>
    <a-modal v-model="model.visible" :title="model.title">
      <a-form :form="model.form" :label-col="model.labelCol" :wrapper-col="model.wrapperCol">
        <a-form-item label="网关路由名称">
          <a-input placeholder="请输入网关路由名称！" v-decorator="['routeName']"/>
        </a-form-item>
        <a-form-item label="网关路由类型">
          <a-select placeholder="请选择网关路由类型！" v-decorator="['routeType', {rules: [{ required: true, message: '请选择网关路由类型！'}]}]">
            <a-select-option value="0">从注册中心获取地址</a-select-option>
            <a-select-option value="1">直接跳转网络地址</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="网关路由地址">
          <a-input placeholder="请输入网关路由地址" v-decorator="['routeUrl', {rules: [{ required: true, message: '请输入网关路由地址'}]}]"/>
        </a-form-item>
        <a-form-item label="路由排序">
          <a-input placeholder="请输入路由排序" v-decorator="['routeOrder', {rules: [{ required: true, message: '请输入路由排序'}]}]"/>
        </a-form-item>
        <a-form-item label="路由状态">
          <a-select placeholder="请选择路由状态！" v-decorator="['routeState', {rules: [{ required: true, message: '请选择路由状态！'}]}]">
            <a-select-option value="-1" disabled>配置异常</a-select-option>
            <a-select-option value="0" disabled>启用（不可操作）</a-select-option>
            <a-select-option value="1">启用</a-select-option>
            <a-select-option value="2">禁用</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="路由断言">
          <a-textarea :rows="4" placeholder="请输入路由断言" v-decorator="['predicateJson', {rules: [{ required: true, message: '请输入路由断言'}]}]"/>
        </a-form-item>
        <a-form-item label="路由过滤器">
          <a-textarea :rows="4" placeholder="请输入路由过滤器" v-decorator="['filterJson', {rules: [{ required: true, message: '请输入路由过滤器'}]}]"/>
        </a-form-item>
      </a-form>
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="model.loading" @click="addOrUpdateRouteOk">
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
    name: 'Route',
    components: {
      NCPagination
    },
    data () {
      return {
        pageObj: {total: 10, pageSize: 10, pageNum: 1},
        loading: false,
        data: [],
        pagination: false,
        columns: [
          {title: '序号', width: '20', customRender: (text, record, index) => { return index + this.pageObj.pageSize * (this.pageObj.pageNum - 1) + 1 }},
          {title: '名称', dataIndex: 'routeName'},
          {title: '转发类型', dataIndex: 'routeType', customRender: (text, record, index) => { return text === 0 ? '从注册中心获取地址' : '直接跳转网络地址' }},
          {title: '地址', dataIndex: 'routeUrl'},
          {title: '排序', dataIndex: 'routeOrder'},
          {title: '断言', dataIndex: 'predicateJson'},
          {title: '过滤器', dataIndex: 'filterJson'},
          {title: '状态', dataIndex: 'routeState', customRender: (text, record, index) => { return text === -1 ? '配置异常' : text === 0 ? '启用（不可操作）' : text === 1 ? '启用' : '禁用' }},
          {title: '操作', fixed: 'right', width: 100, scopedSlots: {customRender: 'operation'}}
        ],
        model: {
          labelCol: {span: 7},
          wrapperCol: {span: 12},
          loading: false,
          visible: false,
          title: '添加网关路由',
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
        operateApi.getRouteList({
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
      // 添加网关路由
      addRoute () {
        this.model.visible = true
        this.model.title = '添加网关路由'
        this.model.data = {}
        this.model.form.resetFields()
      },
      // 更新网关路由
      updateRoute (record) {
        this.model.visible = true
        this.model.title = '修改网关路由'
        this.model.data = record
        setTimeout(() => {
          this.model.form.setFieldsValue({
            routeName: this.model.data.routeName,
            routeType: this.model.data.routeType + '',
            routeUrl: this.model.data.routeUrl,
            routeOrder: this.model.data.routeOrder,
            routeState: this.model.data.routeState + '',
            predicateJson: this.model.data.predicateJson,
            filterJson: this.model.data.filterJson
          })
        }, 100)
      },
      // 删除网关路由
      delRoute (record) {
        const that = this
        this.$confirm({
          title: '删除网关路由！?',
          onOk () {
            return new Promise((resolve, reject) => {
              operateApi.delRoute({id: record.id}).then((res) => {
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
      // 添加或者更新网关路由
      addOrUpdateRouteOk (e) {
        e.preventDefault()
        this.model.form.validateFields((err, values) => {
          if (!err) {
            console.log('Received values of form: ', values)
            this.model.loading = true
            if (this.model.title === '添加网关路由') {
              operateApi.addRoute({
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
              operateApi.updateRoute({
                id: this.model.data.id,
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
      }
    }
  }
</script>

<style scoped>

</style>
