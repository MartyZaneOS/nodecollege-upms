<template>
  <a-tabs type="card" @change="callback" v-model="defaultActiveKey" style="min-height: 360px;">
    <a-tab-pane tab="授权角色信息" key="1">
      <a-row>
        <a-col span="24">
          <a-form layout="inline">
            <a-form-item label="角色名称">
              <a-input v-model="search.roleName" placeholder="请输入角色名称！"></a-input>
            </a-form-item>
            <a-form-item label="角色代码">
              <a-input v-model="search.roleCode" placeholder="请输入角色代码！"></a-input>
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="searchRoleOrgList">查询</a-button>
            </a-form-item>
          </a-form>
        </a-col>
        <a-col span="24">
          <RoleMenu :role-data="roleData" @searchRoleOrgList="searchRoleOrgList" :role-page-obj="rolePageObj"/>
        </a-col>
      </a-row>
    </a-tab-pane>
    <a-tab-pane tab="包含成员信息" key="3">
      <a-row>
        <a-col span="24">
          <a-form layout="inline">
            <a-form-item label="成员账号">
              <a-input v-model="search.memberAccount" placeholder="请输入成员账号！"></a-input>
            </a-form-item>
            <a-form-item label="成员名称">
              <a-input v-model="search.memberName" placeholder="请输入成员名称！"></a-input>
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="searchOrgMemberList">查询</a-button>
            </a-form-item>
          </a-form>
        </a-col>
        <a-col span="24">
          <a-table :dataSource="memberData" :columns="memberColumns" size="small" bordered :pagination="pagination"
                   :rowKey="record => record.memberId">
          </a-table>
          <NCPagination :page="memberPageObj" @changePage="(page)=>{this.memberPageObj = page}"
                        @searchList="searchOrgMemberList"/>
        </a-col>
      </a-row>
    </a-tab-pane>
  </a-tabs>
</template>

<script>
  import {NCPagination, NCTree} from 'common'
  import RoleMenu from './RoleMenu'

  export default {
    name: 'OrgMember',
    components: {
      NCTree,
      NCPagination,
      RoleMenu
    },
    props: {
      org: {
        type: Object,
        default: () => {
        }
      }
    },
    data () {
      return {
        rolePageObj: {total: 0, pageSize: 10, pageNum: 1, pageSizeOptions: ['10', '20', '50', '100']},
        memberPageObj: {total: 0, pageSize: 10, pageNum: 1, pageSizeOptions: ['10', '20', '50', '100']},
        pagination: false,
        checkable: false,
        defaultActiveKey: '1',
        search: {
          memberAccount: null,
          memberName: null
        },
        roleData: [],
        allMenuTree: [],
        selectMenuList: [],
        memberData: [],
        memberColumns: [
          {
            title: '序号',
            width: '20',
            customRender: (text, record, index) => {
              return index + this.memberPageObj.pageSize * (this.memberPageObj.pageNum - 1) + 1
            }
          }, {
            title: '成员账号',
            dataIndex: 'memberAccount',
            sorter: true
          }, {
            title: '成员姓名',
            dataIndex: 'memberName',
            sorter: true
          }, {
            title: '手机号',
            dataIndex: 'telephone',
            sorter: true
          }, {
            title: '创建时间',
            dataIndex: 'createTime',
            sorter: true
          }, {
            title: '状态',
            dataIndex: 'state',
            sorter: true,
            customRender: (text, record, index) => {
              return text === 2 ? '冻结' : '正常'
            }
          }
        ]
      }
    },
    mounted () {
      console.log('mounted', this.org)
      this.defaultActiveKey = '1'
      this.allMenuTree = []
      this.searchRoleOrgList()
      this.searchOrgMemberList()
    },
    watch: {
      org () {
        console.log('watch org', this.org)
        if (this.org.orgId) {
          this.defaultActiveKey = '1'
          this.searchRoleOrgList()
          this.searchOrgMemberList()
          this.allMenuTree = []
        }
      }
    },
    methods: {
      // 查询数据
      searchOrgMemberList () {
        this.http.post('upmsApi', '/org/getMemberListByOrgId', {
          pageNum: this.memberPageObj.pageNum,
          pageSize: this.memberPageObj.pageSize,
          startNode: {
            orgId: this.org.orgId
          }
        }).then((res) => {
          if (res.success) {
            this.memberData = res.rows
            this.memberPageObj.total = res.total
          }
        })
      },
      // 查询角色组织机构信息
      searchRoleOrgList () {
        this.http.post('upmsApi', '/org/getRoleListByOrgId', {
          pageNum: this.rolePageObj.pageNum,
          pageSize: this.rolePageObj.pageSize,
          endNode: {
            orgId: this.org.orgId
          }
        }).then((res) => {
          if (res.success) {
            this.roleData = res.rows
            this.rolePageObj.total = res.total
          }
        })
      },
      // 切换tab
      callback (key) {
        this.defaultActiveKey = key
      }
    }
  }
</script>

<style scoped>

</style>
