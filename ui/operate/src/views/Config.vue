<template>
  <a-row style="padding: 10px;">
    <a-col span="24">
      <a-form layout="inline">
        <a-form-item label="配置用途">
          <a-select allowClear v-model="search.configUsage" placeholder="配置用途！" style="width: 150px;">
            <a-select-option value="0">系统配置</a-select-option>
            <a-select-option value="1">管理员机构配置</a-select-option>
            <a-select-option value="2">管理员配置</a-select-option>
            <a-select-option value="3">管理员机构-管理员配置</a-select-option>
            <a-select-option value="4">用户机构配置</a-select-option>
            <a-select-option value="5">用户配置</a-select-option>
            <a-select-option value="6">用户机构-用户配置</a-select-option>
            <a-select-option value="7">租户配置</a-select-option>
            <a-select-option value="8">租户-机构配置</a-select-option>
            <a-select-option value="9">租户-机构-成员配置</a-select-option>
            <a-select-option value="10">租户-成员配置</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="预制配置">
          <a-select allowClear v-model="search.preFlag" placeholder="预制配置！" style="width: 150px;">
            <a-select-option value="0">否</a-select-option>
            <a-select-option value="1">是</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="配置分组">
          <a-input v-model="search.configGroup" placeholder="请输入配置分组！"></a-input>
        </a-form-item>
        <a-form-item label="配置代码">
          <a-input v-model="search.configCode" placeholder="请输入配置代码！"></a-input>
        </a-form-item>
        <a-form-item label="配置名称">
          <a-input v-model="search.configName" placeholder="请输入配置名称！"></a-input>
        </a-form-item>
        <a-form-item label="配置值">
          <a-input v-model="search.configValue" placeholder="请输入配置值！"></a-input>
        </a-form-item>
        <a-form-item label="配置类型">
          <a-select allowClear v-model="search.configType" placeholder="配置类型！" style="width: 150px;">
            <a-select-option value="0">输入框</a-select-option>
            <a-select-option value="1">下拉单选</a-select-option>
            <a-select-option value="2">下拉多选</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="配置状态">
          <a-select allowClear v-model="search.state" placeholder="配置状态！" style="width: 150px;">
            <a-select-option value="0">不可编辑删除</a-select-option>
            <a-select-option value="1">正常</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="管理员机构代码">
          <a-input v-model="search.adminOrgCode" placeholder="管理员机构代码！"></a-input>
        </a-form-item>
        <a-form-item label="管理员账号">
          <a-input v-model="search.adminAccount" placeholder="管理员账号！"></a-input>
        </a-form-item>
        <a-form-item label="用户机构代码">
          <a-input v-model="search.userOrgCode" placeholder="用户机构代码！"></a-input>
        </a-form-item>
        <a-form-item label="用户账号">
          <a-input v-model="search.userAccount" placeholder="用户账号！"></a-input>
        </a-form-item>
        <a-form-item label="租户代码">
          <a-input v-model="search.tenantCode" placeholder="租户代码！"></a-input>
        </a-form-item>
        <a-form-item label="租户机构代码">
          <a-input v-model="search.tenantOrgCode" placeholder="租户机构代码！"></a-input>
        </a-form-item>
        <a-form-item label="租户成员账号">
          <a-input v-model="search.memberAccount" placeholder="租户成员账号！"></a-input>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="searchList">查询</a-button>
        </a-form-item>
      </a-form>
    </a-col>
    <a-col span="24">
      <a-table :dataSource="data" :columns="columns" :pagination="pagination" :loading="loading" size="small" bordered
               :rowKey="record => record.configId" :scroll="{ x: 4000 }">
        <template slot="name" slot-scope="text, record, index">
          <div class="smileDark" :title="text">{{text}}</div>
        </template>
      </a-table>
      <NCPagination :page="pageObj" @changePage="(page)=>{this.pageObj = page}" @searchList="searchList"/>
    </a-col>
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
        search: {
          preFlag: undefined,
          configGroup: undefined,
          configCode: undefined,
          configName: undefined,
          configValue: undefined,
          configUsage: undefined,
          configType: undefined,
          state: undefined,
          adminOrgCode: undefined,
          adminAccount: undefined,
          userOrgCode: undefined,
          userAccount: undefined,
          tenantCode: undefined,
          tenantOrgCode: undefined,
          memberAccount: undefined
        },
        startTime: undefined,
        endTime: undefined,
        data: [],
        pagination: false,
        columns: [
          {title: '配置用途', dataIndex: 'configUsage', width: 150, fixed: 'left', customRender: this.getConfigUsageName},
          {title: '预制配置', dataIndex: 'preFlag', width: 80, customRender: (text, record, index) => { return text === 1 ? '是' : '否' }},
          {title: '配置分组', dataIndex: 'configGroup'},
          {title: '配置代码', dataIndex: 'configCode'},
          {title: '配置名称', dataIndex: 'configName'},
          {title: '配置值', dataIndex: 'configValue', scopedSlots: {customRender: 'name'}},
          {title: '配置描述', dataIndex: 'configExplain', scopedSlots: {customRender: 'name'}},
          {title: '配置类型', dataIndex: 'configType', width: 100, customRender: (text, record, index) => { return text === 0 ? '输入框' : text === 1 ? '下拉单选' : '下拉多选' }},
          {title: '选项列表', dataIndex: 'optionList', scopedSlots: {customRender: 'name'}},
          {title: '配置状态', dataIndex: 'state', width: 100, customRender: (text, record, index) => { return text === 0 ? '启用' : text === 1 ? '启用' : '' }},
          {title: '管理员机构代码', dataIndex: 'adminOrgCode'},
          {title: '管理员账号', dataIndex: 'adminAccount'},
          {title: '用户机构代码', dataIndex: 'userOrgCode'},
          {title: '用户账号', dataIndex: 'userAccount'},
          {title: '租户代码', dataIndex: 'tenantCode'},
          {title: '租户机构代码', dataIndex: 'tenantOrgCode'},
          {title: '租户成员账号', dataIndex: 'memberAccount'},
          {title: '创建用户', dataIndex: 'createUser', width: 100},
          {title: '创建时间', dataIndex: 'createTime', width: 150},
          {title: '更新用户', dataIndex: 'updateUser', width: 100},
          {title: '更新时间', dataIndex: 'updateTime', width: 150}
        ]
      }
    },
    mounted () {
      this.searchList()
    },
    methods: {
      // 查询数据
      searchList () {
        this.loading = true
        operateApi.getConfigList({
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
      getConfigUsageName (text, record, index) {
        if (text === 0) {
          return '系统配置'
        } else if (text === 1) {
          return '管理员机构配置'
        } else if (text === 2) {
          return '管理员配置'
        } else if (text === 3) {
          return '管理员机构-管理员配置'
        } else if (text === 4) {
          return '用户机构配置'
        } else if (text === 5) {
          return '用户配置'
        } else if (text === 6) {
          return '用户机构-用户配置'
        } else if (text === 7) {
          return '租户配置'
        } else if (text === 8) {
          return '租户-机构配置'
        } else if (text === 9) {
          return '租户-机构-成员配置'
        } else if (text === 10) {
          return '租户-成员配置'
        }
      }
    }
  }
</script>

<style scoped>
  .smileDark {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    display: inline-block;
    width: 200px;
  }
</style>
