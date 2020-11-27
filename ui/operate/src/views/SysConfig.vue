<template>
  <a-row style="padding: 10px;">
    <a-col span="24">
      <a-table :dataSource="data" :columns="columns" :pagination="pagination" :loading="loading" size="small" bordered
               :rowKey="record => record.configId + record.configValue">
        <template slot="configValue" slot-scope="text, record, index">
          <div v-if="record.configType === 0">
            <a-input :defaultValue="text" @change="(e)=>{change(e.target.value, record)}"/>
          </div>
          <div v-if="record.configType === 1">
            <a-tree-select
                :defaultValue="text"
                style="width: 100%"
                :tree-data="record.optionData"
                placeholder="Please select"
                tree-default-expand-all
                @change="(e)=>{change(e, record)}"
            >
            </a-tree-select>
          </div>
          <div v-if="record.configType === 2">
            <a-tree-select
                :defaultValue="record.value"
                style="width: 100%"
                multiple
                :tree-data="record.optionData"
                placeholder="Please select"
                tree-default-expand-all
                @change="(e)=>{change(JSON.stringify(e), record)}"
            >
            </a-tree-select>
          </div>
        </template>
        <template slot="operate" slot-scope="text, record, index">
          <div>
            <a @click="() => editValue(record)">更新值</a>
          </div>
        </template>
      </a-table>
    </a-col>
  </a-row>
</template>

<script>
  import * as operateApi from '../api/operate'

  export default {
    name: 'SysConfig',
    data () {
      return {
        loading: false,
        specialData: [
          {configCode: 'DEFAULT_USER_ORG', configName: '用户默认机构', configValue: '', configUsage: 0, configType: 1, optionList: ''},
          {configCode: 'DEFAULT_TENANT_PRODUCT', configName: '租户默认产品', configValue: '[]', configUsage: 0, configType: 2, optionList: ''},
          {configCode: 'DEFAULT_SMS_SWITCH', configName: '短信开关', configValue: 'false', configUsage: 0, configType: 1, optionList: '[{"title":"开","value":"true","key":"true"},{"title":"关","value":"false","key":"false"}]'}
        ],
        specialOption: {
          DEFAULT_USER_ORG: [],
          DEFAULT_TENANT_PRODUCT: []
        },
        sourceData: [],
        data: [],
        pagination: false,
        columns: [
          {title: '配置代码', dataIndex: 'configCode', width: 150, fixed: 'left'},
          {title: '配置分组', dataIndex: 'configGroup'},
          {title: '配置名称', dataIndex: 'configName'},
          {title: '配置值', dataIndex: 'configValue', scopedSlots: {customRender: 'configValue'}},
          {title: '操作', scopedSlots: {customRender: 'operate'}}
        ]
      }
    },
    mounted () {
      this.searchList()
      this.searchUserOrgTree()
      this.searchTenantProductTree()
    },
    methods: {
      // 查询数据
      searchList () {
        this.loading = true
        operateApi.getConfigList({
          pageSize: -1,
          data: {configUsage: 0}
        }).then((res) => {
          if (res.success) {
            this.sourceData = res.rows
            this.handle()
          }
          this.loading = false
        })
      },
      handle () {
        // 特殊处理的数据和远程获取的数据合并
        let tmpData = JSON.parse(JSON.stringify(this.specialData))
        let addData = []
        for (let i = 0; i < this.sourceData.length; i++) {
          let has = false
          for (let j = 0; j < tmpData.length; j++) {
            if (this.sourceData[i].configCode === tmpData[j].configCode) {
              has = true
              tmpData[j] = this.sourceData[i]
              continue
            }
          }
          if (!has) {
            addData.push(this.sourceData[i])
          }
        }
        tmpData.push(...addData)

        // 单独处理特殊的数据
        for (let j = 0; j < tmpData.length; j++) {
          if (tmpData[j].configCode === 'DEFAULT_USER_ORG') {
            tmpData[j].optionList = JSON.stringify(this.specialOption.DEFAULT_USER_ORG)
          }
          if (tmpData[j].configCode === 'DEFAULT_TENANT_PRODUCT') {
            tmpData[j].optionList = JSON.stringify(this.specialOption.DEFAULT_TENANT_PRODUCT)
          }
        }

        tmpData = JSON.parse(JSON.stringify(tmpData))
        tmpData.forEach(item => {
          // 多选时，处理value
          if (item.configType === 2) {
            item.value = JSON.parse(item.configValue)
          }
          if (item.configType > 0 && item.optionList) {
            item.optionData = JSON.parse(item.optionList)
          }
        })
        this.data = tmpData
      },
      // 查询用户机构树
      searchUserOrgTree () {
        this.loading = true
        operateApi.getUserOrgTree({data: {}}).then(res => {
          if (res.success) {
            this.handleOrgTree(res.rows)
            this.specialOption.DEFAULT_USER_ORG = res.rows
            this.handle()
          }
        })
      },
      // 查询租户产品树
      searchTenantProductTree () {
        this.loading = true
        operateApi.getProductList({data: {productUsage: 2}}).then(res => {
          if (res.success) {
            this.handleProductTree(res.rows)
            this.specialOption.DEFAULT_TENANT_PRODUCT = res.rows
            this.handle()
          }
        })
      },
      handleOrgTree (orgTree) {
        for (let i = 0; i < orgTree.length; i++) {
          orgTree[i].value = orgTree[i].key
          if (orgTree[i].children && orgTree[i].children.length >= 0) {
            this.handleOrgTree(orgTree[i].children)
          }
        }
      },
      handleProductTree (productTree) {
        for (let i = 0; i < productTree.length; i++) {
          productTree[i].key = productTree[i].productCode
          productTree[i].value = productTree[i].productCode
          productTree[i].title = productTree[i].productName
          if (productTree[i].children && productTree[i].children.length >= 0) {
            this.handleProductTree(productTree[i].children)
          }
        }
      },
      change (text, record) {
        if (record.configId) {
          this.sourceData.forEach(item => {
            if (item.configCode === record.configCode) {
              item.configValue = text
              item.update = true
            }
          })
        } else {
          this.specialData.forEach(item => {
            if (item.configCode === record.configCode) {
              item.configValue = text
              item.update = true
            }
          })
        }
        this.handle()
      },
      editValue (record) {
        if (record.configId) {
          operateApi.editConfig({ configId: record.configId, configValue: record.configValue }).then((res) => {
            if (res.success) {
              this.$message.info(res.message)
              this.searchList()
            }
          })
        } else {
          operateApi.addConfig({ ...record }).then((res) => {
            if (res.success) {
              this.$message.info(res.message)
              this.searchList()
            }
          })
        }
      },
      editConfig (record) {
        console.log(record)
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
    width: 100px;
  }
</style>
