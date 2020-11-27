<template>
  <a-row style="padding: 10px;">
    <a-col span="24" style="margin: 10px 0">
      <a-button type="primary" @click="addAppRole">新增应用角色</a-button>
    </a-col>
    <a-col span="24">
      <a-table :dataSource="appRoleList" :columns="columns"
               size="small" bordered :rowKey="record => record.roleId">
        <template slot="operation" slot-scope="text, record, index">
          <div>
            <span>
              <a @click="() => showAppRoleMenu(record)">角色菜单信息</a>
            </span>
          </div>
        </template>
      </a-table>
    </a-col>
    <!-- 绑定应用角色菜单   -->
    <a-modal v-model="modal.visible" :title="modal.title" width="50%">
      <a-form :form="modal.form">
        <a-form-item label="角色名称" :label-col="modal.labelCol" :wrapper-col="modal.wrapperCol">
          <a-input placeholder="请输入角色名称！"
                   v-decorator="['roleName', {rules: [{ required: true, message: '请输入角色名称！'}]}]"/>
        </a-form-item>
        <a-form-item label="角色代码" :label-col="modal.labelCol" :wrapper-col="modal.wrapperCol">
          <a-input placeholder="请输入角色代码！"
                   v-decorator="['roleCode', {rules: [{ required: true, message: '请输入角色代码！'}]}]"/>
        </a-form-item>
        <a-form-item label="角色类型" :label-col="modal.labelCol" :wrapper-col="modal.wrapperCol">
          <a-select placeholder="请选择角色类型"
                    v-decorator="['roleType', {rules: [{ required: true, message: '请选择角色类型！'}]}]">
            <a-select-option value="0">组织角色</a-select-option>
            <a-select-option value="1">组织成员角色</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="数据权限" :label-col="modal.labelCol" :wrapper-col="modal.wrapperCol">
          <a-select placeholder="请选择数据权限"
                    v-decorator="['dataPower', {rules: [{ required: true, message: '请选择数据权限！'}]}]">
            <a-select-option value="0">可以操作该租户的所有数据</a-select-option>
            <a-select-option value="1">可以操作所属机构及下级机构所有数据</a-select-option>
<!--            <a-select-option value="2">可以操作所属机构及当前机构所有下级机构数据</a-select-option>-->
            <a-select-option value="3">可以操作所属机构的数据</a-select-option>
            <a-select-option value="4">可以操作当前机构及下级所有机构数据</a-select-option>
            <a-select-option value="5">仅能操作当前机构数据</a-select-option>
            <a-select-option value="6">仅能操作用户自己的数据</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="排序号" :label-col="modal.labelCol" :wrapper-col="modal.wrapperCol">
          <a-input placeholder="请输入排序号！"
                   v-decorator="['num', {rules: [{ required: true, message: '请输入排序号！'}]}]"/>
        </a-form-item>
      </a-form>
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="modal.loading" @click="addAppRoleOk">
          确定
        </a-button>
        <a-button key="back" @click="() => {modal.visible = false}">取消</a-button>
      </template>
    </a-modal>
    <!-- 绑定应用角色菜单   -->
    <a-modal v-model="modal.bindMenu.visible" title="绑定应用角色菜单" width="50%">
      <NCTree :tree-all-data="modal.bindMenu.allMenuTree"
              :tree-select-data="modal.bindMenu.exMenuList"
              @onCheck="onCheck"/>
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="modal.bindMenu.loading" @click="bindAppRoleMenuOk">
          确定
        </a-button>
        <a-button key="back" @click="() => {modal.bindMenu.visible = false}">取消</a-button>
      </template>
    </a-modal>
  </a-row>
</template>

<script>
  import {NCTree} from 'common'

  export default {
    name: 'AppRole',
    components: {
      NCTree
    },
    props: {
      appData: {
        type: Object,
        default: () => {
        }
      }
    },
    data () {
      return {
        pageObj: {total: 10, pageSize: 10, pageNum: 1},
        loading: false,
        appRoleList: [],
        columns: [
          {
            title: '序号',
            width: '20',
            customRender: (text, record, index) => {
              return index + this.pageObj.pageSize * (this.pageObj.pageNum - 1) + 1
            }
          },
          {title: '角色名称', dataIndex: 'roleName'},
          {title: '角色代码', dataIndex: 'roleCode'},
          {
            title: '角色类型',
            dataIndex: 'roleType',
            customRender: (text, record, index) => {
              if (text === 0) {
                return '组织角色'
              }
              if (text === 1) {
                return '组织成员角色'
              }
            }
          },
          {
            title: '数据权限',
            dataIndex: 'dataPower',
            sorter: true,
            customRender: (text, record, index) => {
              if (text === 0) {
                return '可以操作该租户的所有数据'
              }
              if (text === 1) {
                return '可以操作所属机构及下级机构所有数据'
              }
              if (text === 2) {
                return '可以操作所属机构及当前机构所有下级机构数据'
              }
              if (text === 3) {
                return '可以操作所属机构的数据'
              }
              if (text === 4) {
                return '可以操作当前机构及下级所有机构数据'
              }
              if (text === 5) {
                return '仅能操作当前机构数据'
              }
              if (text === 6) {
                return '仅能操作用户自己的数据'
              }
            }
          },
          {title: '排序序号', dataIndex: 'num'},
          {
            title: '状态',
            dataIndex: 'state',
            sorter: true,
            customRender: (text, record, index) => {
              return text === 2 ? '锁定' : '正常'
            }
          },
          {title: '操作', fixed: 'right', width: 300, scopedSlots: {customRender: 'operation'}}
        ],
        selectData: {},
        modal: {
          labelCol: {span: 7},
          wrapperCol: {span: 12},
          visible: false,
          form: this.$form.createForm(this),
          bindMenu: {
            visible: false,
            loading: false,
            allMenuTree: [],
            exMenuList: []
          }
        }
      }
    },
    watch: {
      appData () {
        this.searchList()
      }
    },
    mounted () {
      this.searchList()
    },
    methods: {
      // 查询应用角色列表
      searchList () {
        this.http.post('upmsApi', '/appRole/getRoleListByAppId', {
          data: {appId: this.appData.appId}
        }).then((res) => {
          if (res.success) {
            this.appRoleList = res.rows
          }
        })
      },
      // 添加应用角色
      addAppRole () {
        this.modal.visible = true
        this.modal.form.setFieldsValue({
          roleName: null,
          roleCode: null,
          dataPower: null,
          num: null
        })
      },
      // 提交新增或修改
      addAppRoleOk () {
        this.modal.form.validateFields((err, values) => {
          if (!err) {
            this.loading = true
            this.http.post('upmsApi', '/appRole/addRoleByAppId', {
              appId: this.appData.appId,
              ...values
            }).then((res) => {
              if (res.success) {
                this.modal.visible = false
                this.searchList()
              }
              this.loading = false
            })
          }
        })
      },
      // 显示应用角色菜单信息
      showAppRoleMenu (record) {
        this.selectData = record
        this.modal.bindMenu.exMenuList = []
        // 查询应用所有菜单信息
        this.http.post('upmsApi', '/app/getMenuTreeByAppId', {
          appId: record.appId
        }).then((res) => {
          if (res.success) {
            let treeList = res.rows
            this.handleAppMenuTree(treeList)
            this.modal.bindMenu.allMenuTree = treeList
            this.getAppRoleMenu()
            this.modal.bindMenu.visible = true
          }
        })
      },
      // 查询已经绑定的应用角色菜单信息
      getAppRoleMenu () {
        this.http.post('upmsApi', '/appRole/getMenuListByRoleId', {
          appId: this.selectData.appId,
          roleId: this.selectData.roleId
        }).then((res) => {
          if (res.success) {
            for (let i = 0; i < res.rows.length; i++) {
              this.modal.bindMenu.exMenuList.push(res.rows[i].menuId)
            }
          }
        })
      },
      handleAppMenuTree (treeList) {
        treeList.forEach(item => {
          item.title = item.menuName
          item.key = item.menuId
          if (item.children) {
            this.handleAppMenuTree(item.children)
          }
        })
      },
      onCheck (selectData) {
        this.modal.bindMenu.exMenuList = selectData
        console.log(this.modal.bindMenu.exMenuList)
      },
      // 绑定应用角色菜单
      bindAppRoleMenuOk () {
        this.http.post('upmsApi', '/appRole/bindRoleMenuByRoleId', {
          appId: this.selectData.appId,
          sourceIds: [this.selectData.roleId],
          targetIds: this.modal.bindMenu.exMenuList
        }).then((res) => {
          if (res.success) {
            this.getAppRoleMenu()
            this.modal.bindMenu.visible = false
          }
        })
      }
    }
  }
</script>

<style scoped>

</style>
