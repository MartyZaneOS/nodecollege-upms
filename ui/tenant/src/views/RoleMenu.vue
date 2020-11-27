<template>
  <a-row>
    <a-col span="12">
      <a-table :dataSource="roleData" :columns="roleColumns" size="small" bordered :pagination="pagination"
               :rowKey="record => record.roleId">
        <template slot="operation" slot-scope="text, record, index">
          <div><span><a @click="() => showMenu(record)">绑定菜单信息</a></span></div>
        </template>
      </a-table>
      <NCPagination :page="rolePageObj" @changePage="(page)=>{this.rolePageObj = page}"
                    @searchList="searchRoleOrgList"/>
    </a-col>
    <a-col span="12" style="border: 1px solid #e8e8e8; min-height: 260px">
      <NCTree :tree-all-data="allMenuTree" :tree-select-data="selectMenuList" :checkable="checkable"
              @onCheck="(data)=> {selectMenuList = data}"/>
    </a-col>
  </a-row>
</template>

<script>
  import {NCPagination, NCTree} from 'common'

  export default {
    name: 'RoleMenu',
    components: {
      NCPagination,
      NCTree
    },
    props: {
      roleData: {
        type: Array,
        default: () => []
      },
      rolePageObj: {
        type: Object,
        default: () => {
          return {total: 0, pageSize: 10, pageNum: 1, pageSizeOptions: ['10', '20', '50', '100']}
        }
      }
    },
    data () {
      return {
        pagination: false,
        checkable: false,
        search: {
          memberAccount: null,
          memberName: null
        },
        allMenuTree: [],
        selectMenuList: [],
        roleColumns: [
          {
            title: '序号',
            width: '20',
            customRender: (text, record, index) => {
              return index + this.rolePageObj.pageSize * (this.rolePageObj.pageNum - 1) + 1
            }
          }, {
            title: '角色名称',
            dataIndex: 'roleName'
          }, {
            title: '数据权限',
            dataIndex: 'dataPower',
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
          }, {
            title: '操作',
            scopedSlots: {customRender: 'operation'}
          }
        ]
      }
    },
    watch: {
      roleData () {
        this.allMenuTree = []
      }
    },
    methods: {
      // 查询角色组织机构信息
      searchRoleOrgList () {
        this.$emit('searchRoleOrgList')
      },
      // 显示菜单信息
      showMenu (record) {
        this.http.post('upmsApi', '/role/getMenuListByRoleId', {
          roleId: record.roleId
        }).then((res) => {
          if (res.success) {
            console.log('getMenuList', res.rows)
            this.allMenuTree = this.getMenuTree(res.rows, 0)
            console.log('getMenuList', this.allMenuTree)
            res.rows.forEach(item => this.selectMenuList.push(item.menuId))
          }
        })
      },
      getMenuTree (allDataList, pid) {
        let menuTree = []
        let childrenList = []
        allDataList.forEach(item => {
          item.key = item.menuId
          item.title = item.menuName
          if (item.parentId === pid) {
            menuTree.push(item)
          } else {
            childrenList.push(item)
          }
        })
        menuTree.forEach(item => {
          item.children = this.getMenuTree(childrenList, item.menuId)
        })
        return menuTree
      }
    }
  }
</script>

<style scoped>

</style>
