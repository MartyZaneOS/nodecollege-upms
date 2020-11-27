<template>
  <a-row style="padding: 10px;">
    <a-col span="24">
      <a-form layout="inline">
        <a-form-item>
          <a-button type="primary" @click="addModal" :disabled="disabled">新增角色</a-button>
        </a-form-item>
      </a-form>
    </a-col>
    <a-col span="24">
      <a-table :dataSource="data" :columns="columns" :pagination="pagination"
               size="small" bordered :loading="loading" :rowSelection="rowSelection"
               :rowKey="record => (record.id)">
        <template slot="operation" slot-scope="text, record, index">
          <div>
            <span>
              <a @click="() => showMenu(record)">授权菜单</a>
              <a @click="() => updateRole(record)">编辑</a>
              <a @click="() => delRole(record)">删除</a>
            </span>
          </div>
        </template>
      </a-table>
      <NCPagination :page="pageObj" @changePage="(page)=>{this.pageObj = page}" @searchList="searchList"/>
    </a-col>

    <a-modal v-model="addOrUpdateModal.visible" :title="addOrUpdateModal.title">
      <a-form :form="addOrUpdateModal.form" :label-col="addOrUpdateModal.labelCol" :wrapper-col="addOrUpdateModal.wrapperCol">
        <a-form-item label="产品信息" v-if="addOrUpdateModal.title === '添加角色'">
          <a-tree-select
              style="width: 100%"
              :dropdown-style="{ maxHeight: '400px', overflow: 'auto' }"
              :tree-data="addOrUpdateModal.productTree"
              placeholder="请选择产品信息"
              tree-default-expand-all
              v-decorator="['productCode', {rules: [{ required: addOrUpdateModal.title === '添加角色', message: '请选择产品信息！'}]}]"
          >
          </a-tree-select>
        </a-form-item>
        <a-form-item label="角色名称">
          <a-input placeholder="请输入角色名称！" v-decorator="['roleName', {rules: [{ required: true, message: '请输入角色名称！'}]}]"/>
        </a-form-item>
        <a-form-item label="角色代码" v-if="addOrUpdateModal.title === '添加角色'">
          <a-input placeholder="请输入角色代码！" v-decorator="['roleCode', {rules: [{ required: addOrUpdateModal.title === '添加角色', message: '请输入角色代码！'}]}]"/>
        </a-form-item>
        <a-form-item label="角色类型" v-if="addOrUpdateModal.title === '添加角色'">
          <a-select placeholder="请选择角色类型！"
                    v-decorator="['roleType', {rules: [{ required: addOrUpdateModal.title === '添加角色', message: '请选择角色类型！'}]}]">
            <a-select-option value="0">组织角色</a-select-option>
            <a-select-option value="1">组织成员角色</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="数据权限">
          <a-select placeholder="请选择数据权限！" v-decorator="['dataPower', {rules: [{ required: true, message: '请选择数据权限！'}]}]">
            <a-select-option value="0">所有机构</a-select-option>
            <a-select-option value="1">所属机构及下级机构</a-select-option>
            <a-select-option value="2">所属机构</a-select-option>
            <a-select-option value="3">用户自己数据</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="描述">
          <a-input placeholder="请输入描述！" v-decorator="['roleDesc']" />
        </a-form-item>
      </a-form>
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="addOrUpdateModal.loading" @click="addOrUpdateOk">
          确定
        </a-button>
        <a-button key="back" @click="addOrUpdateModal.visible = false">取消</a-button>
      </template>
    </a-modal>
    <a-modal v-model="bindModal.visible" title="绑定菜单">
      <a-tree
          :replace-fields="{children:'children', title:'menuName', key: 'menuCode'}"
          :tree-data="bindModal.bindTreeData"
          :expanded-keys="bindModal.bindTreeExpandedKeys"
          v-model="bindModal.checkedKeys"
          checkable
          checkStrictly
          @expand="(expandedKeys)=>{this.bindModal.bindTreeExpandedKeys = expandedKeys}"
          @check="bindTreeCheck"
      />
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="bindModal.loading" @click="bindOk">
          保存/更新
        </a-button>
        <a-button key="back" @click="bindModal.visible = false">取消</a-button>
      </template>
    </a-modal>
  </a-row>
</template>

<script>
  import * as operateApi from '../api/operate'
  import {NCPagination} from 'common'

  export default {
    name: 'TenantRole',
    components: {
      NCPagination
    },
    data () {
      return {
        search: {
          accessAuth: undefined,
          roleUsage: undefined,
          roleState: undefined
        },
        pageObj: {total: 10, pageSize: 10, pageNum: 1},
        pagination: false,
        loading: false,
        disabled: false,
        data: [],
        rowSelection: {
          selectedRowKeys: [],
          onChange: (selectedRowKeys, selectedRows) => {
            this.rowSelection.selectedRowKeys = selectedRowKeys
          }
        },
        columns: [
          {title: '归属产品代码', dataIndex: 'productCode'},
          {title: '角色名称', dataIndex: 'roleName'},
          {title: '角色代码', dataIndex: 'roleCode'},
          {title: '角色描述', dataIndex: 'roleDesc'},
          {title: '角色类型', dataIndex: 'roleType', customRender: (text, record, index) => { return text === 0 ? '组织角色' : text === 1 ? '组织成员角色' : '' }},
          {
            title: '数据权限',
            dataIndex: 'dataPower',
            customRender: (text, record, index) => {
              if (text === 0) {
                return '所有机构'
              }
              if (text === 1) {
                return '所属机构及下级机构'
              }
              if (text === 2) {
                return '所属机构'
              }
              if (text === 3) {
                return '用户自己的数据'
              }
            }
          },
          {title: '状态', dataIndex: 'roleState', customRender: (text, record, index) => { return text === -1 ? '已删除' : text === 0 ? '正常' : text === 1 ? '禁用' : '' }},
          {title: '操作', fixed: 'right', width: 300, scopedSlots: {customRender: 'operation'}}
        ],
        addOrUpdateModal: {
          labelCol: {span: 7},
          wrapperCol: {span: 12},
          loading: false,
          visible: false,
          title: '添加角色',
          form: this.$form.createForm(this),
          productTree: [],
          data: {}
        },
        bindModal: {
          loading: false,
          visible: false,
          roleData: {},
          bindTreeData: [],
          bindTreeExpandedKeys: [],
          checkedKeys: []
        }
      }
    },
    mounted () {
      this.init()
    },
    methods: {
      init () {
        this.rowSelection.selectedRowKeys = []
        this.searchList()
      },
      // 查询需授权访问接口信息
      searchList () {
        this.loading = true
        operateApi.getTenantRoleList({...this.pageObj, data: {...this.search}}).then((res) => {
          if (res.success) {
            this.data = res.rows
            this.pageObj.total = res.total
          }
          this.loading = false
          this.disabled = false
        })
      },
      // 查询不需要登录就能访问的接口信息
      addModal () {
        this.addOrUpdateModal.visible = true
        this.addOrUpdateModal.title = '添加角色'
        setTimeout(() => {
          this.addOrUpdateModal.form.resetFields()
        }, 100)
        if (this.addOrUpdateModal.productTree.length === 0) {
          operateApi.getProductList({data: {productUsage: 2}}).then((res) => {
            if (res.success) {
              this.handleProductTree(res.rows)
              this.addOrUpdateModal.productTree = res.rows
            }
          })
        }
      },
      handleProductTree (productList) {
        if (productList.length > 0) {
          productList.forEach(item => {
            item.disabled = item.productType !== 1
            item.title = item.productName
            item.key = item.productCode
            item.value = item.productCode
            if (item.children) {
              this.handleProductTree(item.children)
            }
          })
        }
      },
      // 更新菜单
      updateRole (record) {
        this.addOrUpdateModal.title = '编辑角色'
        this.addOrUpdateModal.data = record
        setTimeout(() => {
          this.addOrUpdateModal.form.setFieldsValue({
            roleName: record.roleName,
            roleDesc: record.roleDesc,
            dataPower: record.dataPower + ''
          })
        }, 200)
        this.addOrUpdateModal.visible = true
      },
      // 删除用户角色
      delRole (record) {
        let that = this
        this.$confirm({
          title: '删除菜单！?',
          onOk () {
            return new Promise((resolve, reject) => {
              operateApi.delTenantRole({id: record.id}).then((res) => {
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
      // 添加或更新
      addOrUpdateOk () {
        this.addOrUpdateModal.loading = true
        this.addOrUpdateModal.form.validateFields((err, values) => {
          if (!err) {
            if (this.addOrUpdateModal.title === '添加角色') {
              operateApi.addTenantRole({...values}).then(res => {
                if (res.success) {
                  this.$message.info('成功！')
                  this.searchList()
                }
                this.addOrUpdateModal.visible = false
                this.addOrUpdateModal.loading = false
              })
            } else {
              operateApi.editTenantRole({...values, id: this.addOrUpdateModal.data.id}).then(res => {
                if (res.success) {
                  this.$message.info('成功！')
                  this.searchList()
                }
                this.addOrUpdateModal.visible = false
                this.addOrUpdateModal.loading = false
              })
            }
          }
        })
      },
      showMenu (record) {
        this.bindModal.roleData = record
        this.bindModal.bindTreeData = []
        this.bindModal.bindTreeExpandedKeys = []
        this.bindModal.checkedKeys = []
        operateApi.getProductMenuTree({productCode: record.productCode}).then(res => {
          this.bindModal.bindTreeData = res.rows
          this.handleBindTree(res.rows, this.bindModal.bindTreeExpandedKeys)
          operateApi.getTenantRoleMenuList({pageSize: -1, data: {roleCode: record.roleCode}}).then(res => {
            this.handleBindTree(res.rows, this.bindModal.checkedKeys)
            this.bindModal.visible = true
          })
        })
      },
      handleBindTree (treeList, selectKeys) {
        if (!treeList) {
          return
        }
        treeList.forEach(item => {
          selectKeys.push(item.menuCode)
          if (item.children) {
            this.handleBindTree(item.children, selectKeys)
          }
        })
      },
      bindTreeCheck (checkedKeys, info) {
        const targetCodes = []
        if (checkedKeys.checked) {
          checkedKeys.checked.forEach(item => {
            this.bindModal.bindTreeData.forEach(item2 => targetCodes.push(...this.getParentCode(item2, item)))
          })
        }
        this.bindModal.checkedKeys = Array.from(new Set(targetCodes))
      },
      getParentCode (menu, key) {
        let res = []
        if (!menu || !menu.children) {
          return res
        }
        for (let i = 0; i < menu.children.length; i++) {
          if (menu.children[i].menuCode === key) {
            res.push(menu.menuCode)
            res.push(key)
            return res
          } else {
            let temp = this.getParentCode(menu.children[i], key)
            if (temp && temp.length > 0) {
              res.push(...temp)
              res.push(menu.menuCode)
            }
          }
        }
        return res
      },
      bindOk () {
        operateApi.bindTenantRoleMenu({sourceCodes: [this.bindModal.roleData.roleCode], targetCodes: this.bindModal.checkedKeys}).then(res => {
          this.searchList()
          this.bindModal.visible = false
        })
      }
    }
  }
</script>

<style scoped>

</style>
