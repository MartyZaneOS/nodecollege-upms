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
              <a @click="() => showOrg(record)">授权机构</a>
              <a @click="() => showOrgUserModal(record)" v-if="record.roleType === 1">授权用户</a>
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
      <a-tabs v-model="bindModal.navSelect" @change="modelNavChange">
        <a-tab-pane :key="1" tab="PC端后台"></a-tab-pane>
        <a-tab-pane :key="0" tab="PC端首页"></a-tab-pane>
        <a-tab-pane :key="2" tab="移动端"></a-tab-pane>
      </a-tabs>
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
    <a-modal v-model="bindOrgModal.visible" title="绑定组织机构">
      <a-tree
          :replace-fields="{children:'children', title:'title', key: 'key'}"
          :tree-data="bindOrgModal.bindTreeData"
          :expanded-keys="bindOrgModal.bindTreeExpandedKeys"
          v-model="bindOrgModal.checkedKeys"
          checkable
          checkStrictly
          @expand="(expandedKeys)=>{this.bindOrgModal.bindTreeExpandedKeys = expandedKeys}"
      />
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="bindOrgModal.loading" @click="bindOrgOk">
          保存/更新
        </a-button>
        <a-button key="back" @click="bindOrgModal.visible = false">取消</a-button>
      </template>
    </a-modal>
    <a-modal v-model="bindOrgUserModal.visible" title="绑定用户" width="1200px">
      <a-row>
        <a-col span="8">
          <a-tree
              :replace-fields="{children:'children', title:'title', key: 'key'}"
              :tree-data="bindOrgUserModal.bindTreeData"
              :expanded-keys="bindOrgUserModal.bindTreeExpandedKeys"
              v-model="bindOrgUserModal.checkedKeys"
              @expand="(expandedKeys)=>{this.bindOrgUserModal.bindTreeExpandedKeys = expandedKeys}"
              @select="showUser"
          />
        </a-col>
        <a-col span="16">
          <a-table :dataSource="bindOrgUserModal.data" :columns="bindOrgUserModal.columns" :pagination="bindOrgUserModal.pagination"
                   size="small" bordered :loading="bindOrgUserModal.loading"
                   :rowKey="record => (record.userId)">
            <template slot="operation" slot-scope="text, record, index">
              <a @click="() => addUserOrgRole(record)" v-if="!record.isBind">授权</a>
              <a @click="() => delUserOrgRole(record)" v-if="record.isBind">解除授权</a>
            </template>
          </a-table>
          <NCPagination :page="bindOrgUserModal.pageObj" @changePage="(page)=>{this.bindOrgUserModal.pageObj = page}" @searchList="searchList"/>
        </a-col>
      </a-row>
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="bindOrgUserModal.loading" @click="bindOrgOk">
          保存/更新
        </a-button>
        <a-button key="back" @click="bindOrgUserModal.visible = false">取消</a-button>
      </template>
    </a-modal>
  </a-row>
</template>

<script>
  import * as operateApi from '../api/operate'
  import {NCPagination} from 'common'

  export default {
    name: 'Role',
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
          navSelect: 1,
          sourceData: [],
          bindTreeData: [],
          bindTreeExpandedKeys: [],
          checkedKeys: []
        },
        bindOrgModal: {
          loading: false,
          visible: false,
          roleData: {},
          bindTreeData: [],
          bindTreeExpandedKeys: [],
          checkedKeys: []
        },
        bindOrgUserModal: {
          loading: false,
          visible: false,
          roleData: {},
          bindTreeData: [],
          bindTreeExpandedKeys: [],
          checkedKeys: [],
          search: {
            account: '',
            phone: ''
          },
          pageObj: {total: 10, pageSize: 10, pageNum: 1},
          pagination: false,
          data: [],
          rowSelection: {
            selectedRowKeys: [],
            onChange: (selectedRowKeys, selectedRows) => {
              this.bindOrgUserModal.rowSelection.selectedRowKeys = selectedRowKeys
            }
          },
          columns: [
            {title: '用户账号', dataIndex: 'account'},
            {title: '用户名称', dataIndex: 'nickname'},
            {title: '用户电话', dataIndex: 'telephone'},
            {title: '创建时间', dataIndex: 'createTime'},
            {title: '状态', dataIndex: 'state', customRender: (text, record, index) => { return text === 2 ? '锁定' : '正常' }},
            {title: '操作', fixed: 'right', width: 100, scopedSlots: {customRender: 'operation'}}
          ]
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
        operateApi.getUserRoleList({...this.pageObj, data: {...this.search}}).then((res) => {
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
          operateApi.getProductList({data: {productUsage: 1}}).then((res) => {
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
              operateApi.delUserRole({id: record.id}).then((res) => {
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
              operateApi.addUserRole({...values}).then(res => {
                if (res.success) {
                  this.$message.info('成功！')
                  this.searchList()
                }
                this.addOrUpdateModal.visible = false
                this.addOrUpdateModal.loading = false
              })
            } else {
              operateApi.editUserRole({...values, id: this.addOrUpdateModal.data.id}).then(res => {
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
        this.bindModal.navSelect = 1
        this.bindModal.sourceData = []
        this.bindModal.bindTreeData = []
        this.bindModal.bindTreeExpandedKeys = []
        this.bindModal.sourceCheckedKeys = []
        this.bindModal.checkedKeys = []
        operateApi.getProductMenuTree({productCode: record.productCode}).then(res => {
          this.bindModal.sourceData = res.rows
          for (let i = 0; i < this.bindModal.sourceData.length; i++) {
            if (this.bindModal.sourceData[i].navPlatform === this.bindModal.navSelect) {
              this.bindModal.bindTreeData.push(this.bindModal.sourceData[i])
            }
          }
          this.handleBindTree(this.bindModal.bindTreeData, this.bindModal.bindTreeExpandedKeys)
          operateApi.getUserRoleMenuList({pageSize: -1, data: {roleCode: record.roleCode}}).then(res => {
            this.bindModal.sourceCheckedKeys = res.rows
            let check = []
            for (let i = 0; i < this.bindModal.sourceCheckedKeys.length; i++) {
              if (this.bindModal.sourceCheckedKeys[i].navPlatform === this.bindModal.navSelect) {
                check.push(this.bindModal.sourceCheckedKeys[i])
              }
            }
            this.handleBindTree(check, this.bindModal.checkedKeys)
            this.bindModal.visible = true
          })
        })
      },
      modelNavChange (value) {
        console.log('navChange', value)
        this.bindModal.navSelect = value
        this.bindModal.bindTreeData = []
        this.bindModal.bindTreeExpandedKeys = []
        this.bindModal.checkedKeys = []
        for (let i = 0; i < this.bindModal.sourceData.length; i++) {
          if (this.bindModal.sourceData[i].navPlatform === this.bindModal.navSelect) {
            this.bindModal.bindTreeData.push(this.bindModal.sourceData[i])
          }
        }
        this.handleBindTree(this.bindModal.bindTreeData, this.bindModal.bindTreeExpandedKeys)
        let check = []
        for (let i = 0; i < this.bindModal.sourceCheckedKeys.length; i++) {
          if (this.bindModal.sourceCheckedKeys[i].navPlatform === this.bindModal.navSelect) {
            check.push(this.bindModal.sourceCheckedKeys[i])
          }
        }
        this.handleBindTree(check, this.bindModal.checkedKeys)
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
        operateApi.bindUserRoleMenu({sourceCodes: [this.bindModal.roleData.roleCode], targetCodes: this.bindModal.checkedKeys, navPlatform: this.bindModal.navSelect}).then(res => {
          this.searchList()
          this.bindModal.visible = false
        })
      },
      // 显示绑定组织机构modal
      showOrg (record) {
        this.bindOrgModal.roleData = record
        this.bindOrgModal.bindTreeData = []
        this.bindOrgModal.bindTreeExpandedKeys = []
        this.bindOrgModal.checkedKeys = []
        operateApi.getUserOrgTree({data: {}}).then(res => {
          this.bindOrgModal.bindTreeData = res.rows
          this.handleBindOrgTree(res.rows, this.bindOrgModal.bindTreeExpandedKeys)
          operateApi.getUserOrgTreeByRole({pageSize: -1, data: {roleCode: record.roleCode}}).then(res2 => {
            this.handleBindOrgTree(res2.rows, this.bindOrgModal.checkedKeys)
            this.bindOrgModal.visible = true
          })
        })
      },
      handleBindOrgTree (treeList, selectKeys) {
        if (!treeList) {
          return
        }
        treeList.forEach(item => {
          selectKeys.push(item.key)
          if (item.children) {
            this.handleBindOrgTree(item.children, selectKeys)
          }
        })
      },
      bindOrgOk () {
        operateApi.bindUserRoleOrg({sourceCodes: [this.bindOrgModal.roleData.roleCode], targetCodes: this.bindOrgModal.checkedKeys.checked}).then(res => {
          this.searchList()
          this.bindOrgModal.visible = false
        })
      },
      // 显示组织机构用户信息
      showOrgUserModal (record) {
        this.bindOrgUserModal.roleData = record
        this.bindOrgUserModal.bindTreeData = []
        this.bindOrgUserModal.bindTreeExpandedKeys = []
        this.bindOrgUserModal.checkedKeys = []
        operateApi.getUserOrgTreeByRole({pageSize: -1, data: {roleCode: record.roleCode}}).then(res => {
          this.bindOrgUserModal.bindTreeData = res.rows
          this.handleBindOrgTree(res.rows, this.bindOrgUserModal.bindTreeExpandedKeys)
          this.bindOrgUserModal.visible = true
        })
      },
      showUser (selectedKeys, info) {
        this.bindOrgUserModal.orgCode = selectedKeys[0]
        if (selectedKeys.length > 0) {
          this.bindOrgUserModal.loading = true
          operateApi.getUserListByOrg({
            ...this.bindOrgUserModal.pageObj.pageNum,
            data: {orgCode: selectedKeys[0]}
          }).then((res) => {
            if (res.success) {
              operateApi.getUserListByRoleOrg({roleCode: this.bindOrgUserModal.roleData.roleCode, orgCode: selectedKeys[0]}).then((res2) => {
                if (res2.success) {
                  res.rows.forEach(item => {
                    item.isBind = false
                    res2.rows.forEach(item2 => {
                      if (item.userId === item2.userId) {
                        item.isBind = true
                      }
                    })
                  })
                  this.bindOrgUserModal.data = res.rows
                  this.bindOrgUserModal.pageObj.total = res.total
                }
                this.bindOrgUserModal.loading = false
              })
            }
          })
        }
      },
      // 删除菜单
      addUserOrgRole (record) {
        let that = this
        this.$confirm({
          title: '绑定管理员机构角色！?',
          onOk () {
            return new Promise((resolve, reject) => {
              operateApi.addUserOrgRole({
                userId: record.userId,
                roleCode: that.bindOrgUserModal.roleData.roleCode,
                orgCode: that.bindOrgUserModal.orgCode
              }).then((res) => {
                if (res.success) {
                  that.$message.info(res.message)
                  that.showUser([that.bindOrgUserModal.orgCode])
                  resolve(res.message)
                } else {
                  reject(res.message)
                }
              })
            })
          }
        })
      },
      // 删除菜单
      delUserOrgRole (record) {
        let that = this
        this.$confirm({
          title: '解绑管理员机构角色！?',
          onOk () {
            return new Promise((resolve, reject) => {
              operateApi.delUserOrgRole({
                userId: record.userId,
                roleCode: that.bindOrgUserModal.roleData.roleCode,
                orgCode: that.bindOrgUserModal.orgCode
              }).then((res) => {
                if (res.success) {
                  that.$message.info(res.message)
                  that.showUser([that.bindOrgUserModal.orgCode])
                  resolve(res.message)
                } else {
                  reject(res.message)
                }
              })
            })
          }
        })
      }
    }
  }
</script>

<style scoped>

</style>
