<template>
  <a-row style="padding: 10px;">
    <a-col span="24">
      <a-form layout="inline">
        <a-form-item label="租户名称">
          <a-input v-model="search.tenantName" placeholder="请输入租户名称！"></a-input>
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
               :rowKey="record => record.tenantId">
        <template slot="operation" slot-scope="text, record, index">
          <div>
            <span>
              <a v-if="record.state === 1 || record.state === 0" @click="() => tenantProduct(record)">应用开通/关闭</a>
              <a v-if="record.state === 1 || record.state === 0" @click="() => updateTenant(record)">编辑</a>
            </span>
          </div>
        </template>
      </a-table>
      <NCPagination :page="pageObj" @changePage="(page)=>{this.pageObj = page}" @searchList="searchList"/>
    </a-col>
    <a-modal v-model="model.visible" title="修改租户信息">
      <a-form :form="model.form">
        <a-form-item label="租户名称" :label-col="model.labelCol" :wrapper-col="model.wrapperCol">
          <a-input placeholder="请输入租户名称！"
                   v-decorator="['tenantName', {rules: [{ required: true, message: '请输入租户名称！'}]}]"/>
        </a-form-item>
        <a-form-item label="租户描述" :label-col="model.labelCol" :wrapper-col="model.wrapperCol">
          <a-input placeholder="请输入租户描述！"
                   v-decorator="['introduce']"/>
        </a-form-item>
      </a-form>
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="model.loading" @click="updateTenantOk">确定</a-button>
        <a-button key="back" @click="model.visible = false">取消</a-button>
      </template>
    </a-modal>

    <!-- 租户应用管理 -->
    <a-modal v-model="productModal.visible" title="租户产品信息">
      <a-tree
          :replace-fields="{children:'children', title:'productName', key: 'productCode'}"
          :tree-data="productModal.bindTreeData"
          :expanded-keys="productModal.bindTreeExpandedKeys"
          v-model="productModal.checkedKeys"
          checkable
          checkStrictly
          @expand="(expandedKeys)=>{this.productModal.bindTreeExpandedKeys = expandedKeys}"
      />
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="productModal.loading" @click="bindTenantProductOk">确定</a-button>
        <a-button key="back" @click="productModal.visible = false">取消</a-button>
      </template>
    </a-modal>
  </a-row>
</template>

<script>
  import * as operateApi from '../api/operate'
  import {NCPagination} from 'common'

  export default {
    name: 'Tenant',
    components: {
      NCPagination
    },
    data () {
      return {
        pageObj: {total: 10, pageSize: 10, pageNum: 1, pageSizeOptions: ['5', '10', '20', '50', '100']},
        search: {
          tenantName: '',
          tenantCode: ''
        },
        model: {
          labelCol: {span: 7},
          wrapperCol: {span: 12},
          loading: false,
          visible: false,
          title: '修改租户信息',
          form: this.$form.createForm(this),
          data: {}
        },
        loading: false,
        data: [],
        pagination: false,
        columns: [
          {title: '序号', width: '20', customRender: (text, record, index) => { return index + this.pageObj.pageSize * (this.pageObj.pageNum - 1) + 1 }},
          {title: '租户名称', dataIndex: 'tenantName'},
          {title: '租户代码', dataIndex: 'tenantCode'},
          {title: '描述', dataIndex: 'introduce'},
          {title: '创建时间', dataIndex: 'createTime'},
          {title: '状态', dataIndex: 'state', customRender: (text, record, index) => { return text === 2 ? '锁定' : '正常' }},
          {title: '操作', fixed: 'right', width: 200, scopedSlots: {customRender: 'operation'}}
        ],
        checkedKeysResult: [],
        checkedKeys: [],
        productModal: {
          visible: false,
          loading: false,
          tenantData: {},
          bindTreeData: [],
          bindTreeExpandedKeys: [],
          checkedKeys: []
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
        operateApi.getTenantList({
          pageNum: this.pageObj.pageNum,
          pageSize: this.pageObj.pageSize,
          data: {...this.search}
        }).then((res) => {
          if (res.success) {
            this.data = res.rows
            this.pageObj.total = res.total
          }
          this.loading = false
        })
      },
      // 更新租户
      updateTenant (record) {
        this.model.visible = true
        this.model.title = '修改租户信息'
        this.model.data = record
        console.log({...this.model.data})
        setTimeout(() => {
          this.model.form.setFieldsValue({
            tenantName: this.model.data.tenantName,
            introduce: this.model.data.introduce
          })
        }, 100)
      },
      // 更新租户
      updateTenantOk (e) {
        e.preventDefault()
        this.model.loading = true
        this.model.form.validateFields((err, values) => {
          if (!err) {
            this.model.loading = true
            operateApi.editTenant({
              tenantId: this.model.data.tenantId,
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
        })
      },
      // 租户应用信息
      tenantProduct (record) {
        this.productModal.checkedKeys = []
        this.productModal.tenantData = record
        if (this.productModal.bindTreeData.length <= 0) {
          operateApi.getProductList({pageSize: -1, data: {productUsage: 2}}).then((res) => {
            if (res.success) {
              this.productModal.bindTreeExpandedKeys = []
              this.handleProductTree(res.rows)
              this.productModal.bindTreeData = res.rows
              this.handleBindOrgTree(res.rows, this.productModal.bindTreeExpandedKeys)
              this.getTenantAppList()
            }
          })
        } else {
          this.getTenantAppList()
        }
        this.productModal.visible = true
      },
      // 查询租户绑定应用列表
      getTenantAppList () {
        operateApi.getTenantProduct({
          tenantId: this.productModal.tenantData.tenantId
        }).then((res) => {
          if (res.success) {
            this.handleBindOrgTree(res.rows, this.productModal.checkedKeys)
          }
        })
      },
      handleProductTree (treeList) {
        if (!treeList) {
          return
        }
        treeList.forEach(item => {
          if (item.productType !== 1) {
            item.disabled = true
          }
          if (item.children) {
            this.handleProductTree(item.children)
          }
        })
      },
      handleBindOrgTree (treeList, selectKeys) {
        if (!treeList) {
          return
        }
        treeList.forEach(item => {
          selectKeys.push(item.productCode)
          if (item.children) {
            this.handleBindOrgTree(item.children, selectKeys)
          }
        })
      },
      // 绑定租户应用
      bindTenantProductOk () {
        this.productModal.loading = true
        operateApi.bindTenantProduct({
          tenantId: this.productModal.tenantData.tenantId,
          targetCodes: this.productModal.checkedKeys.checked
        }).then((res) => {
          if (res.success) {
            this.$message.info(res.message)
            this.getTenantAppList()
            this.productModal.visible = false
          }
          this.productModal.loading = false
        })
      }
    }
  }
</script>

<style scoped>

</style>
