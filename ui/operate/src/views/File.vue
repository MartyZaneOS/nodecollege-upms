<template>
  <a-row style="padding: 10px;">
    <a-col span="24">
      <a-form layout="inline">
        <a-form-item label="文件用途">
          <a-select allowClear v-model="search.filePurpose" placeholder="文件用途！" style="width: 150px;">
            <a-select-option value="0">用户头像</a-select-option>
            <a-select-option value="1">聊天图片</a-select-option>
            <a-select-option value="2">文章图片</a-select-option>
            <a-select-option value="3">下载文件包</a-select-option>
            <a-select-option value="4">文章md</a-select-option>
            <a-select-option value="5">文章html</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="文件名称">
          <a-input v-model="search.fileName" placeholder="请输入配置名称！"></a-input>
        </a-form-item>
        <a-form-item label="文件路径">
          <a-input v-model="search.filePath" placeholder="文件路径！"></a-input>
        </a-form-item>
        <a-form-item label="文件类型">
          <a-select allowClear v-model="search.fileType" placeholder="文件类型！" style="width: 150px;">
            <a-select-option value="1">图片类</a-select-option>
            <a-select-option value="2">文档类</a-select-option>
            <a-select-option value="3">压缩文件类</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="配置状态">
          <a-select allowClear v-model="search.state" placeholder="配置状态！" style="width: 150px;">
            <a-select-option value="0">不可编辑删除</a-select-option>
            <a-select-option value="1">正常</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="用户id">
          <a-input v-model="search.userId" placeholder="用户id！"></a-input>
        </a-form-item>
        <a-form-item label="租户id">
          <a-input v-model="search.tenantId" placeholder="租户id！"></a-input>
        </a-form-item>
        <a-form-item label="组织机构id">
          <a-input v-model="search.orgId" placeholder="组织机构id！"></a-input>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="searchList">查询</a-button>
        </a-form-item>
      </a-form>
    </a-col>
    <a-col span="24">
      <a-table :dataSource="data" :columns="columns" :pagination="pagination" :loading="loading" size="small" bordered
               :rowKey="record => record.fileId" :scroll="{ x: 2000 }">
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
          userId: undefined,
          tenantId: undefined,
          orgId: undefined,
          fileName: undefined,
          filePath: undefined,
          fileType: undefined,
          filePurpose: undefined,
          fileValidity: undefined
        },
        startTime: undefined,
        endTime: undefined,
        data: [],
        pagination: false,
        columns: [
          {title: '文件用途', dataIndex: 'filePurpose', width: 150, fixed: 'left', customRender: this.getConfigUsageName},
          {title: '文件类型', dataIndex: 'fileType', width: 150, customRender: (text, record, index) => { return text === 1 ? '图片类' : text === 2 ? '文档类' : '压缩文件类' }},
          {title: '文件名称', dataIndex: 'fileName'},
          {title: '文件路径', dataIndex: 'filePath'},
          {title: '文件有效期', dataIndex: 'fileValidity', width: 150},
          {title: '用户id', dataIndex: 'userId', width: 150},
          {title: '租户id', dataIndex: 'tenantId', width: 150},
          {title: '组织机构id', dataIndex: 'orgId', width: 150},
          {title: '创建时间', dataIndex: 'createTime', width: 150}
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
        operateApi.getFileList({
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
          return '用户头像'
        } else if (text === 1) {
          return '聊天图片'
        } else if (text === 2) {
          return '文章图片'
        } else if (text === 3) {
          return '下载文件包'
        } else if (text === 4) {
          return '文章md'
        } else if (text === 5) {
          return '文章html'
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
