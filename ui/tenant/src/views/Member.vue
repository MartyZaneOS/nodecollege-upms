<template>
  <a-row style="padding: 10px;">
    <a-col span="24">
      <a-form layout="inline">
        <a-form-item label="用户账号">
          <a-input v-model="search.account" placeholder="请输入用户账号！"></a-input>
        </a-form-item>
        <a-form-item label="用户名称">
          <a-input v-model="search.nickname" placeholder="请输入用户名称！"></a-input>
        </a-form-item>
        <a-form-item label="用户电话">
          <a-input v-model="search.telephone" placeholder="请输入用户电话！"></a-input>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="searchList">查询</a-button>
        </a-form-item>
      </a-form>
    </a-col>
    <a-col span="24">
      <a-form layout="inline">
        <a-form-item>
          <a-button type="primary" @click="inviteModal">邀请成员</a-button>
        </a-form-item>
      </a-form>
      <a-table :dataSource="userList" :columns="columns" :pagination="pagination" :loading="loading" size="small"
               bordered :rowKey="record => record.memberId">
        <template slot="operation" slot-scope="text, record, index">
          <div>
            <span>
              <a @click="() => showOrg(record)">机构信息</a>
              <a @click="() => showRole(record)">角色信息</a>
              <a @click="() => showPower(record)">授权菜单</a>
              <a @click="() => updateUser(record)">编辑</a>
              <a v-if="record.state > 0" @click="() => resetPwd(record)">重置密码</a>
              <a v-if="record.state > 0" @click="() => delUser(record)">删除</a>
            </span>
          </div>
        </template>
      </a-table>
      <NCPagination :page="pageObj" @changePage="(page)=>{this.pageObj = page}" @searchList="searchList"/>
    </a-col>
    <a-modal v-model="modal.visible" :title="modal.title">
      <a-form :form="modal.form"  :label-col="modal.labelCol" :wrapper-col="modal.wrapperCol">
        <a-form-item label="用户账号" v-if="modal.title === '修改成员信息'">
          <a-input placeholder="请输入用户账号！"
                   v-decorator="['account', {rules: [{ required: modal.title === '修改成员信息', message: '请输入用户账号！'}]}]"/>
        </a-form-item>
        <a-form-item label="用户名称">
          <a-input placeholder="请输入用户名称！"
                   v-decorator="['nickname', {rules: [{ required: true, message: '请输入用户名称！'}]}]"/>
        </a-form-item>
        <a-form-item label="用户电话">
          <a-input placeholder="请输入用户电话！"
                   v-decorator="['telephone', {rules: [{ required: true, message: '请输入用户电话！'}]}]"/>
        </a-form-item>
      </a-form>
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="modal.loading" @click="updateUserOk">
          确定
        </a-button>
        <a-button key="back" @click="modal.visible = false">取消</a-button>
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
    <a-modal v-model="bindRoleModel.visible" :title="bindRoleModel.title" width="1200px">
      <a-table :dataSource="bindRoleModel.data" :columns="bindRoleModel.columns" :pagination="bindRoleModel.pagination" :loading="bindRoleModel.loading" size="small" bordered
               :rowKey="record => record.roleCode">
      </a-table>
      <template slot="footer">
        <a-button key="back" @click="bindRoleModel.visible = false">取消</a-button>
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
          <a-form :label-col="modal.labelCol" :wrapper-col="modal.wrapperCol">
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
    <a-modal v-model="selectRoleModal.visible" title="选择角色">
      <a-form layout="inline">
        <a-form-item label="角色">
          <a-select placeholder="请选择默认角色！" v-model="powerModal.search.defaultRoleCode" style="width: 200px">
            <a-select-option v-for="role in selectRoleModal.orgList" :value="role.code" :key="role.code">{{role.name}}</a-select-option>
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
  import * as tenantApi from '../api/tenant'
  import {NCPagination} from 'common'

  export default {
    name: 'User',
    components: {
      NCPagination
    },
    data () {
      return {
        pageObj: {total: 10, pageSize: 10, pageNum: 1},
        pagination: false,
        loading: false,
        userList: [],
        search: {
          account: null,
          nickname: null,
          telephone: null
        },
        columns: [
          {title: '序号', width: '20', customRender: (text, record, index) => { return index + this.pageObj.pageSize * (this.pageObj.pageNum - 1) + 1 }},
          {title: '用户账号', dataIndex: 'account'},
          {title: '用户名称', dataIndex: 'nickname'},
          {title: '用户电话', dataIndex: 'telephone'},
          {title: '创建时间', dataIndex: 'createTime', sorter: true},
          {title: '状态', dataIndex: 'state', sorter: true, customRender: (text, record, index) => { return text === 2 ? '锁定' : '正常' }},
          {title: '操作', fixed: 'right', width: 350, scopedSlots: {customRender: 'operation'}}
        ],
        selectedData: {},
        modal: {
          labelCol: {span: 7},
          wrapperCol: {span: 12},
          title: '修改成员信息',
          loading: false,
          visible: false,
          form: this.$form.createForm(this)
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
            {title: '角色来源', dataIndex: 'roleSource', customRender: (text, record, index) => { return text === 0 ? '预制角色' : text === 1 ? '自定义角色' : '' }},
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
      searchList () {
        tenantApi.getMemberList({
          pageNum: this.pageObj.pageNum,
          pageSize: this.pageObj.pageSize,
          data: {
            account: this.search.account,
            nickname: this.search.nickname,
            telephone: this.search.telephone
          }
        }).then((res) => {
          if (res.success) {
            this.userList = res.rows
            this.pageObj.total = res.total
          }
        })
      },
      inviteModal () {
        this.selectedData = {}
        this.modal.title = '邀请成员'
        this.modal.form.resetFields()
        this.modal.visible = true
      },
      updateUser (record) {
        this.selectedData = record
        this.modal.title = '修改成员信息'
        setTimeout(() => {
          this.modal.form.setFieldsValue({
            account: record.account,
            nickname: record.nickname,
            telephone: record.telephone
          })
        }, 100)
        this.modal.visible = true
      },
      updateUserOk () {
        this.modal.form.validateFields((err, values) => {
          if (!err) {
            if (this.modal.title === '修改成员信息') {
              tenantApi.editMember({
                memberId: this.selectedData.memberId,
                account: values.account,
                nickname: values.nickname,
                telephone: values.telephone
              }).then((res) => {
                if (res.success) {
                  this.searchList()
                  this.modal.visible = false
                }
              })
            } else {
              tenantApi.inviteMember({
                userName: values.nickname,
                telephone: values.telephone
              }).then((res) => {
                if (res.success) {
                  this.$message.info(res.message)
                  this.searchList()
                  this.modal.visible = false
                }
              })
            }
          }
        })
      },
      // 删除管理员
      delUser (record) {
        const that = this
        this.$confirm({
          title: '删除用户！?',
          onOk () {
            return new Promise((resolve, reject) => {
              console.log('删除')
              tenantApi.delMember({memberId: record.memberId}).then((res) => {
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
      // 重置密码
      resetPwd (record) {
        const that = this
        this.$confirm({
          title: '重置密码！?',
          onOk () {
            return new Promise((resolve, reject) => {
              tenantApi.resetMemberPwd({memberId: record.memberId}).then((res) => {
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
      // 显示绑定组织机构modal
      showOrg (record) {
        this.bindOrgModal.adminData = record
        this.bindOrgModal.bindTreeData = []
        this.bindOrgModal.bindTreeExpandedKeys = []
        this.bindOrgModal.checkedKeys = []
        tenantApi.getOrgTree({data: {}}).then(res => {
          this.bindOrgModal.bindTreeData = res.rows
          this.handleBindOrgTree(res.rows, this.bindOrgModal.bindTreeExpandedKeys)
          tenantApi.getOrgTreeByMember({pageSize: -1, data: {memberId: record.memberId}}).then(res2 => {
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
        tenantApi.bindMemberOrg({sourceIds: [this.bindOrgModal.adminData.memberId], targetCodes: this.bindOrgModal.checkedKeys.checked}).then(res => {
          this.searchList()
          this.bindOrgModal.visible = false
        })
      },
      // 显示授权角色列表
      showRole (record) {
        this.bindRoleModel.visible = true
        this.bindRoleModel.data = []
        tenantApi.getRoleListByMember({data: {memberId: record.memberId}}).then(res => {
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
          memberId: record.memberId,
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
        tenantApi.getMemberPower({...this.powerModal.search}).then(res => {
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
