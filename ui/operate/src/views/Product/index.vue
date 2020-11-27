<template>
  <a-row>
    <a-col span="24">
      <a-card title="产品信息" size="small">
        <a-form layout="inline" slot="extra">
          <a-form-item>
            <a-button type="primary" @click="addModal">添加</a-button>
            <a-button type="primary" @click="clearProductMenuTreeCache" style="margin-left: 10px">清空产品树缓存</a-button>
          </a-form-item>
        </a-form>
        <a-table :dataSource="data" :columns="columns" :pagination="pagination" :loading="loading"
                 size="small" bordered :rowKey="record => record.id" :customRow="onClick" :rowSelection="rowSelection">
          <template slot="operation" slot-scope="text, record, index">
            <div>
            <span>
              <a @click="() => editModal(record)">编辑</a>
              <a @click="() => delModal(record)">删除</a>
            </span>
            </div>
          </template>
        </a-table>
      </a-card>
    </a-col>
    <a-col span="24">
      <product-menu :product-data="rowSelectData"/>
    </a-col>
    <a-modal v-model="addOrUpdateModal.visible" :title="addOrUpdateModal.title">
      <a-form :form="addOrUpdateModal.form">
        <a-form-item label="产品名称" :label-col="addOrUpdateModal.labelCol" :wrapper-col="addOrUpdateModal.wrapperCol">
          <a-input placeholder="请输入产品名称！"
                   v-decorator="['productName', {rules: [{ required: true, message: '请输入产品名称！'}]}]"/>
        </a-form-item>
        <a-form-item label="产品代码" :label-col="addOrUpdateModal.labelCol" :wrapper-col="addOrUpdateModal.wrapperCol"
                     v-if="addOrUpdateModal.title === '添加产品'">
          <a-input placeholder="请输入产品代码！"
                   v-decorator="['productCode', {rules: [{ required: addOrUpdateModal.title === '添加产品', message: '请输入产品代码！'}]}]"/>
        </a-form-item>
        <a-form-item label="产品类型" :label-col="addOrUpdateModal.labelCol" :wrapper-col="addOrUpdateModal.wrapperCol"
                     v-if="addOrUpdateModal.title === '添加产品'">
          <a-select placeholder="请选择产品类型！"
                    v-decorator="['productType', {initialValue: '0', rules: [{ required: addOrUpdateModal.title === '添加产品', message: '请选择产品类型！'}]}]">
            <a-select-option value="0">顶级产品</a-select-option>
            <a-select-option value="1">共存式产品</a-select-option>
            <a-select-option value="2">排斥式产品</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="产品用途" :label-col="addOrUpdateModal.labelCol" :wrapper-col="addOrUpdateModal.wrapperCol"
                     v-if="addOrUpdateModal.title === '添加产品' && !this.rowSelectData.productCode">
          <a-select placeholder="请选择产品用途！"
                    v-decorator="['productUsage', {rules: [{ required: addOrUpdateModal.title === '添加产品' && !this.rowSelectData.productCode, message: '请选择产品用途！'}]}]">
            <a-select-option value="0">运营/运维</a-select-option>
            <a-select-option value="1">2C</a-select-option>
            <a-select-option value="2">2B</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="描述" :label-col="addOrUpdateModal.labelCol" :wrapper-col="addOrUpdateModal.wrapperCol">
          <a-input placeholder="请输入描述！" v-decorator="['productDesc']"/>
        </a-form-item>
      </a-form>
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="addOrUpdateModal.loading" @click="addOrUpdateOk">
          确定
        </a-button>
        <a-button key="back" @click="addOrUpdateModal.visible = false">取消</a-button>
      </template>
    </a-modal>
  </a-row>
</template>

<script>
  import * as operateApi from '../../api/operate'
  import ProductMenu from './ProductMenu'

  export default {
    name: 'Product',
    components: {
      ProductMenu
    },
    data () {
      return {
        loading: false,
        pageObj: {total: 10, pageSize: 10, pageNum: 1},
        pagination: false,
        data: [],
        rowSelection: {
          selectedRowKeys: [],
          type: 'radio',
          onChange: (selectedRowKeys, selectedRows) => {
            this.rowSelectData = selectedRows[0]
            this.rowSelection.selectedRowKeys = selectedRowKeys
          }
        },
        rowSelectData: {},
        columns: [
          {title: '产品名称', dataIndex: 'productName'},
          {title: '产品代码', dataIndex: 'productCode'},
          {title: '产品类型', dataIndex: 'productType', customRender: (text, record, index) => { return text === 0 ? '顶级产品' : text === 1 ? '共存式产品' : '排斥式产品' }},
          {title: '产品用途', dataIndex: 'productUsage', customRender: (text, record, index) => { return text === 0 ? '运营/运维' : text === 1 ? '2C' : text === 2 ? '2B' : '' }},
          {title: '产品描述', dataIndex: 'productDesc'},
          {title: '操作', fixed: 'right', width: 100, scopedSlots: {customRender: 'operation'}}
        ],
        addOrUpdateModal: {
          labelCol: {span: 7},
          wrapperCol: {span: 12},
          loading: false,
          visible: false,
          title: '添加产品',
          form: this.$form.createForm(this),
          data: {}
        }
      }
    },
    mounted () {
      this.init()
    },
    methods: {
      // 查询产品数据
      init () {
        this.rowSelectData = {}
        this.rowSelection.selectedRowKeys = []
        operateApi.getProductList({}).then(res => {
          this.data = res.rows
          this.pageObj.total = res.total
        })
      },
      addModal () {
        this.addOrUpdateModal.data = {}
        this.addOrUpdateModal.form.resetFields()
        this.addOrUpdateModal.visible = true
        this.addOrUpdateModal.title = '添加产品'
      },
      editModal (record) {
        this.addOrUpdateModal.data = record
        setTimeout(() => {
          this.addOrUpdateModal.form.setFieldsValue({
            productName: record.productName,
            productDesc: record.productDesc
          })
        }, 100)
        this.addOrUpdateModal.visible = true
        this.addOrUpdateModal.title = '编辑产品'
      },
      delModal (record) {
        const that = this
        this.$confirm({
          title: '删除产品?',
          onOk () {
            return new Promise((resolve, reject) => {
              operateApi.delProduct({id: record.id}).then((res) => {
                if (res.success) {
                  that.$message.info(res.message)
                  that.init()
                  resolve(res.message)
                } else {
                  reject(res.message)
                }
              })
            })
          }
        })
      },
      addOrUpdateOk (e) {
        this.addOrUpdateModal.form.validateFields((err, values) => {
          if (!err) {
            this.addOrUpdateModal.loading = true
            if (this.addOrUpdateModal.title === '添加产品') {
              if ((!this.rowSelectData || !this.rowSelectData.productCode) && values.productType !== '0') {
                this.$message.error('未选上级的产品只能是顶级产品类型！')
                return
              }
              if (this.rowSelectData && this.rowSelectData.productCode && values.productType === '0') {
                this.$message.error('非顶级产品只能是共存式产品或者排斥式产品！')
                this.addOrUpdateModal.loading = false
                return
              }
              operateApi.addProduct({
                ...values,
                parentCode: this.rowSelectData ? this.rowSelectData.productCode : undefined
              }).then(res => {
                this.$message.info(res.message)
                this.init()
                this.addOrUpdateModal.loading = false
                this.addOrUpdateModal.visible = false
              })
            } else {
              operateApi.editProduct({...values, id: this.addOrUpdateModal.data.id}).then(res => {
                this.$message.info(res.message)
                this.init()
                this.addOrUpdateModal.loading = false
                this.addOrUpdateModal.visible = false
              })
            }
          }
        })
      },
      onClick (record) {
        return {
          on: {
            click: () => {
              if (this.rowSelectData.id !== record.id) {
                this.rowSelectData = record
                this.rowSelection.selectedRowKeys = [record.id]
              } else {
                this.rowSelectData = {}
                this.rowSelection.selectedRowKeys = []
              }
            }
          }
        }
      },
      clearProductMenuTreeCache () {
        operateApi.clearProductMenuTreeCache({}).then(res => {
          this.$message.info(res.message)
          this.init()
        })
      }
    }
  }
</script>

<style scoped>

</style>
