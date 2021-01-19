<template>
  <a-row style="padding: 10px;">
    <a-col span="24">
      <a-form layout="inline">
        <a-form-item label="管理员账号">
          <a-input v-model="search.account" placeholder="请输入管理员账号！"></a-input>
        </a-form-item>
        <a-form-item label="管理员电话">
          <a-input v-model="search.telephone" placeholder="请输入管理员电话！"></a-input>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="searchList">查询</a-button>
        </a-form-item>
      </a-form>
    </a-col>
    <a-col span="24" style="margin: 10px 0">
      <a-button type="primary" @click="addAdmin">新增管理员</a-button>
    </a-col>
    <a-col span="24">
      <a-table :dataSource="data" :columns="columns" :pagination="pagination" :loading="loading" size="small" bordered
               :rowKey="record => record.adminId">
        <template slot="operation" slot-scope="text, record, index">
          <div>
            <span>
              <a @click="() => showOrg(record)">机构信息</a>
              <a @click="() => showRole(record)">角色信息</a>
              <a @click="() => showPower(record)">授权菜单</a>
              <a @click="() => resetPwd(record)">重置密码</a>
              <a v-if="record.state == 1 || record.state == 2" @click="() => updateAdmin(index)">编辑</a>
              <a v-if="record.state == 1" @click="() => lockAdmin(index)">锁定</a>
              <a v-if="record.state == 2" @click="() => lockAdmin(index)">解锁</a>
              <a v-if="record.state == 1 || record.state == 2" @click="() => delAdmin(index)">删除</a>
            </span>
          </div>
        </template>
      </a-table>
      <NCPagination :page="pageObj" @changePage="(page)=>{this.pageObj = page}" @searchList="searchList"/>
    </a-col>
    <a-modal v-model="model.visible" :title="model.title">
      <a-form :form="model.form">
        <a-form-item label="管理员账号" :label-col="model.labelCol" :wrapper-col="model.wrapperCol">
          <a-input placeholder="请输入管理员账号！"
                   v-decorator="['account', {rules: [{ required: true, message: '请输入管理员账号！'}]}]"/>
        </a-form-item>
        <a-form-item label="管理员电话" :label-col="model.labelCol" :wrapper-col="model.wrapperCol">
          <a-input placeholder="请输入管理员电话！"
                   v-decorator="['telephone', {rules: [{ required: true, message: '请输入管理员电话！'}]}]"/>
        </a-form-item>
      </a-form>
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="model.loading" @click="addOrUpdateAdminOk">
          确定
        </a-button>
        <a-button key="back" @click="model.visible = false">取消</a-button>
      </template>
    </a-modal>
    <a-modal v-model="bindRoleModel.visible" :title="bindRoleModel.title" width="1200px">
      <a-table :dataSource="bindRoleModel.data" :columns="bindRoleModel.columns" :pagination="bindRoleModel.pagination" :loading="bindRoleModel.loading" size="small" bordered
               :rowKey="record => record.roleCode">
      </a-table>
      <template slot="footer">
        <a-button key="back" @click="bindRoleModel.visible = false">取消</a-button>
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
    <a-modal v-model="powerModal.visible" title="授权菜单" width="1200px">
      <a-row>
        <a-col span="24">
          <a-form layout="inline">
            <a-form-item label="显示所有角色数据">
              <a-switch v-model="powerModal.search.showAllRole"></a-switch >
            </a-form-item>
            <a-form-item label="显示所有机构数据">
              <a-switch v-model="powerModal.search.showAllOrg"></a-switch >
            </a-form-item>
            <a-form-item label="默认角色" v-if="!powerModal.search.showAllRole">
              <a-select placeholder="请选择默认角色！" v-model="powerModal.search.defaultRoleCode" @change="roleChange" style="width: 200px">
                <a-select-option v-for="role in powerModal.powerData.roleList" :value="role.code" :key="role.code">{{role.name}}</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item label="默认机构" v-if="!powerModal.search.showAllOrg">
              <a-select placeholder="请选择默认角色！" v-model="powerModal.search.defaultOrgCode" @change="orgChange" style="width: 200px">
                <a-select-option v-for="org in powerModal.powerData.orgList" :value="org.code" :key="org.code">{{org.name}}</a-select-option>
              </a-select>
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="getAdminPower">查询</a-button>
            </a-form-item>
          </a-form>
        </a-col>
        <a-col span="12">
          <a-tabs v-model="powerModal.navSelect" @change="modelNavChange">
            <a-tab-pane :key="1" tab="PC端"></a-tab-pane>
            <a-tab-pane :key="0" tab="其他"></a-tab-pane>
          </a-tabs>
          <a-tree
              :replace-fields="{children:'children', title:'menuName', key: 'menuCode'}"
              :tree-data="powerModal.treeData"
              :selectedKeys="powerModal.selectedKeys"
              @select="showPowerMenuInfo"
          />
        </a-col>
        <a-col span="12">
          <a-form :label-col="model.labelCol" :wrapper-col="model.wrapperCol">
            <a-form-item label="菜单名称">{{powerModal.data.menuCode}}</a-form-item>
            <a-form-item label="菜单代码">{{powerModal.data.menuName}}</a-form-item>
            <a-form-item label="菜单类型">{{powerModal.data.menuType === 0 ? '分类导航' : powerModal.data.menuType === 1 ? '菜单页面' : powerModal.data.menuType === 2 ? '查询类按钮' : powerModal.data.menuType === 3 ? '操作类按钮' : ''}}</a-form-item>
            <a-form-item label="菜单排序">{{powerModal.data.num}}</a-form-item>
            <a-form-item label="页面代码" v-if="powerModal.data.menuType >= 1">{{powerModal.data.pageCode}}</a-form-item>
            <a-form-item label="页面路径" v-if="powerModal.data.menuType >= 1">{{powerModal.data.pagePath}}</a-form-item>
            <a-form-item label="接口服务" v-if="powerModal.data.menuType > 1">{{powerModal.data.appName}}</a-form-item>
            <a-form-item label="接口地址" v-if="powerModal.data.menuType > 1">{{powerModal.data.apiUrl}}</a-form-item>
            <a-form-item label="数据权限" v-if="powerModal.data.menuType > 1">{{powerModal.data.dataPower === 0 ? '所有机构' : powerModal.data.dataPower === 1 ? '所属机构及下级机构' : powerModal.data.dataPower === 2 ? '所属机构' : powerModal.data.dataPower === 3 ? '用户个人数据' : ''}}</a-form-item>
            <a-form-item label="授权机构" v-if="powerModal.data.menuType > 1">{{powerModal.data.orgCodeList}}</a-form-item>
          </a-form>
        </a-col>
      </a-row>
      <template slot="footer">
        <a-button key="back" @click="powerModal.visible = false">取消</a-button>
      </template>
    </a-modal>
    <a-modal v-model="selectOrgModal.visible" title="选择组织机构">
      <a-form layout="inline">
        <a-form-item label="组织机构">
          <a-select placeholder="请选择默认机构！" v-model="powerModal.search.defaultOrgCode" style="width: 200px">
            <a-select-option v-for="org in selectOrgModal.orgList" :value="org.code" :key="org.code">{{org.name}}</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
      <template slot="footer">
        <a-button key="submit" type="primary" @click="()=>{this.selectOrgModal.visible = false}">
          确定
        </a-button>
      </template>
    </a-modal>
    <a-modal v-model="selectRoleModal.visible" title="选择角色">
      <a-form layout="inline">
        <a-form-item label="角色">
          <a-select placeholder="请选择默认角色！" v-model="powerModal.search.defaultRoleCode" style="width: 200px">
            <a-select-option v-for="role in selectRoleModal.roleList" :value="role.code" :key="role.code">{{role.name}}</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
      <template slot="footer">
        <a-button key="submit" type="primary" @click="()=>{this.selectRoleModal.visible = false}">
          确定
        </a-button>
      </template>
    </a-modal>
  </a-row>
</template>

<script>
  import {NCPagination} from 'common'
  import * as operateApi from '../api/operate'

  export default {
    name: 'Admin',
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
          {title: '序号', width: '20', customRender: (text, record, index) => { return index + this.pageObj.pageSize * (this.pageObj.pageNum - 1) + 1 }},
          {title: '管理员名称', dataIndex: 'account'},
          {title: '管理员电话', dataIndex: 'telephone'},
          {title: '首次登陆', dataIndex: 'firstLogin', customRender: (text, record, index) => { return text ? '是' : '否' }},
          {title: '状态', dataIndex: 'state', customRender: (text, record, index) => { return text === 2 ? '锁定' : '正常' }},
          {title: '创建时间', dataIndex: 'createTime'},
          {title: '操作', fixed: 'right', width: 350, scopedSlots: {customRender: 'operation'}}
        ],
        model: {
          labelCol: {span: 7},
          wrapperCol: {span: 12},
          loading: false,
          visible: false,
          title: '添加管理员',
          form: this.$form.createForm(this),
          data: {}
        },
        bindRoleModel: {
          labelCol: {span: 7},
          wrapperCol: {span: 12},
          loading: false,
          visible: false,
          pagination: false,
          title: '角色信息',
          data: [],
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
                  return '可以操作所有数据'
                }
                if (text === 1) {
                  return '可以操作所属机构及下级机构所有数据'
                }
                if (text === 2) {
                  return '可以操作所属机构的数据'
                }
                if (text === 3) {
                  return '仅能操作用户自己的数据'
                }
              }
            },
            {title: '状态', dataIndex: 'roleState', customRender: (text, record, index) => { return text === -1 ? '已删除' : text === 0 ? '正常' : text === 1 ? '禁用' : '' }}
          ]
        },
        bindOrgModal: {
          loading: false,
          visible: false,
          adminData: {},
          bindTreeData: [],
          bindTreeExpandedKeys: [],
          checkedKeys: []
        },
        powerModal: {
          labelCol: {span: 4},
          wrapperCol: {span: 8},
          loading: false,
          visible: false,
          powerData: {},
          adminData: {},
          navSelect: 1,
          sourceData: [],
          treeData: [],
          treeExpandedKeys: [],
          selectedKeys: [],
          pageObj: {total: 10, pageSize: 10, pageNum: 1},
          pagination: false,
          search: {
            adminId: undefined,
            showAllOrg: undefined,
            showAllRole: undefined,
            defaultOrgCode: undefined,
            defaultRoleCode: undefined
          },
          data: {}
        },
        selectOrgModal: {
          visible: false,
          orgList: []
        },
        selectRoleModal: {
          visible: false,
          roleList: []
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
        operateApi.getAdminList({
          pageNum: this.pageObj.pageNum,
          pageSize: this.pageObj.pageSize,
          data: {
            account: this.search.account,
            telephone: this.search.telephone
          }
        }).then((res) => {
          if (res.success) {
            this.data = res.rows
            this.pageObj.total = res.total
          }
          this.loading = false
        })
      },
      // 添加管理员
      addAdmin () {
        this.model.visible = true
        this.model.title = '添加管理员'
        this.model.data = {}
        this.model.form.resetFields()
      },
      // 更新管理员
      updateAdmin (index) {
        this.model.visible = true
        this.model.title = '修改管理员'
        this.model.data = this.data[index]
        setTimeout(() => {
          this.model.form.setFieldsValue({
            account: this.model.data.account,
            telephone: this.model.data.telephone
          })
        }, 100)
      },
      // 删除管理员
      delAdmin (index) {
        const that = this
        this.$confirm({
          title: '删除管理员！?',
          onOk () {
            return new Promise((resolve, reject) => {
              console.log('删除')
              operateApi.delAdmin({adminId: that.data[index].adminId}).then((res) => {
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
      // 锁定/解锁管理员
      lockAdmin (index) {
        console.log('锁定管理员' + index)
        const that = this
        this.$confirm({
          title: that.data[index].state === 1 ? '锁定管理员！?' : '解锁管理员！',
          onOk () {
            return new Promise((resolve, reject) => {
              operateApi.lockAdmin({
                adminId: that.data[index].adminId,
                state: that.data[index].state === 1 ? 2 : 1
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
      // 添加或者更新管理员
      addOrUpdateAdminOk (e) {
        e.preventDefault()
        this.model.form.validateFields((err, values) => {
          if (!err) {
            console.log('Received values of form: ', values)
            this.model.loading = true
            if (this.model.title === '添加管理员') {
              operateApi.addAdmin({
                ...values
              }).then((res) => {
                if (res.success) {
                  this.$message.info(res.message)
                  this.searchList()
                }
                this.model.loading = false
                this.model.visible = false
              })
            } else {
              operateApi.updateAdmin({
                adminId: this.model.data.adminId,
                ...values
              }).then((res) => {
                if (res.success) {
                  this.$message.info(res.message)
                  this.searchList()
                }
                this.model.loading = false
                this.model.visible = false
              })
            }
          }
        })
      },
      resetPwd (record) {
        console.log('重置管理员密码' + record)
        const that = this
        this.$confirm({
          title: '重置管理员密码? 重置后密码为系统配置管理中默认密码设置的值！',
          onOk () {
            return new Promise((resolve, reject) => {
              operateApi.resetPwd({
                adminId: record.adminId
              }).then((res) => {
                if (res.success) {
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
      // 显示绑定组织机构modal
      showOrg (record) {
        this.bindOrgModal.adminData = record
        this.bindOrgModal.bindTreeData = []
        this.bindOrgModal.bindTreeExpandedKeys = []
        this.bindOrgModal.checkedKeys = []
        operateApi.getOrgTree({data: {}}).then(res => {
          this.bindOrgModal.bindTreeData = res.rows
          this.handleBindOrgTree(res.rows, this.bindOrgModal.bindTreeExpandedKeys)
          operateApi.getOrgTreeByAdmin({pageSize: -1, data: {adminId: record.adminId}}).then(res2 => {
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
        operateApi.bindAdminOrg({sourceIds: [this.bindOrgModal.adminData.adminId], targetCodes: this.bindOrgModal.checkedKeys.checked}).then(res => {
          this.searchList()
          this.bindOrgModal.visible = false
        })
      },
      // 显示授权角色列表
      showRole (record) {
        this.bindRoleModel.visible = true
        this.bindRoleModel.data = []
        operateApi.getRoleListByAdmin({data: {adminId: record.adminId}}).then(res => {
          this.bindRoleModel.data = res.rows
        })
      },
      // 显示授权信息
      showPower (record) {
        this.powerModal.adminData = record
        this.powerModal.navSelect = 1
        this.powerModal.sourceData = []
        this.powerModal.treeData = []
        this.powerModal.treeExpandedKeys = []
        this.powerModal.search = {
          adminId: record.adminId,
          showAllOrg: record.showAllOrg,
          showAllRole: record.showAllRole,
          defaultOrgCode: record.defaultOrgCode,
          defaultRoleCode: record.defaultRoleCode
        }
        this.getAdminPower()
      },
      getAdminPower () {
        this.powerModal.selectedKeys = []
        this.powerModal.data = {}
        if (!this.powerModal.search.showAllOrg && !this.powerModal.search.defaultOrgCode) {
          this.$message.error('请选择默认机构进行查询')
          this.powerModal.visible = true
          return
        }
        if (!this.powerModal.search.showAllRole && !this.powerModal.search.defaultRoleCode) {
          this.$message.error('请选择默认角色进行查询')
          this.powerModal.visible = true
          return
        }
        operateApi.getAdminPower({...this.powerModal.search}).then(res => {
          this.powerModal.powerData = res.rows[0]
          this.powerModal.sourceData = res.rows[0].menuTree
          this.powerModal.treeData = []
          for (let i = 0; i < this.powerModal.sourceData.length; i++) {
            if (this.powerModal.sourceData[i].navPlatform === 1) {
              this.powerModal.treeData.push(this.powerModal.sourceData[i])
            }
          }
          this.handleMenuTree(this.powerModal.treeData, this.powerModal.treeExpandedKeys)
          this.powerModal.visible = true
        })
      },
      modelNavChange (value) {
        console.log('navChange', value)
        this.powerModal.navSelect = value
        this.powerModal.treeData = []
        this.powerModal.treeExpandedKeys = []
        for (let i = 0; i < this.powerModal.sourceData.length; i++) {
          if (this.powerModal.sourceData[i].navPlatform === value) {
            this.powerModal.treeData.push(this.powerModal.sourceData[i])
          }
        }
        this.handleMenuTree(this.powerModal.treeData, this.powerModal.treeExpandedKeys)
      },
      handleMenuTree (treeList, selectKeys) {
        if (!treeList) {
          return
        }
        treeList.forEach(item => {
          selectKeys.push(item.menuCode)
          if (item.children) {
            this.handleMenuTree(item.children, selectKeys)
          }
        })
      },
      showPowerMenuInfo (selectedKeys, info) {
        this.powerModal.data = info.selected ? info.node.dataRef : {}
        this.powerModal.selectedKeys = selectedKeys
      },
      roleChange (value) {
        console.log(value)
        if (!this.powerModal.search.showAllOrg) {
          let f = false
          for (let i = 0; i < this.powerModal.powerData.roleOrgMap[value].length; i++) {
            if (this.powerModal.powerData.roleOrgMap[value][i].code === this.powerModal.search.defaultOrgCode) {
              f = true
            }
          }
          if (!f) {
            this.$message.error('默认的机构不在选择的角色的授权范围内')
            this.powerModal.search.defaultOrgCode = undefined
            this.selectOrgModal.orgList = this.powerModal.powerData.roleOrgMap[value]
            this.selectOrgModal.visible = true
          }
        }
      },
      orgChange (value) {
        console.log(value)
        if (!this.powerModal.search.showAllOrg) {
          let f = false
          for (let i = 0; i < this.powerModal.powerData.orgRoleMap[value].length; i++) {
            if (this.powerModal.powerData.orgRoleMap[value][i].code === this.powerModal.search.defaultRoleCode) {
              f = true
            }
          }
          if (!f) {
            this.$message.error('选择的机构未授权默认的角色')
            this.powerModal.search.defaultRoleCode = undefined
            this.selectRoleModal.roleList = this.powerModal.powerData.orgRoleMap[value]
            this.selectRoleModal.visible = true
          }
        }
      }
    }
  }
</script>

<style scoped>

</style>
