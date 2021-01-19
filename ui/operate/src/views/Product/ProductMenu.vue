<template>
  <div>
    <a-card title="产品菜单信息" size="small">
      <a-row>
        <a-col span="8">
          <a-tabs v-model="navSelect" @change="navChange">
            <a-tab-pane :key="1" tab="PC端"></a-tab-pane>
            <a-tab-pane :key="0" tab="其他"></a-tab-pane>
          </a-tabs>
          <a-form layout="inline">
            <a-form-item v-if="productData">
              <a-button type="primary" v-if="productData.productType !== 1 && (!treeSelectData.menuType || treeSelectData.menuType < 2)" @click="addModal">添加菜单</a-button>
              <a-button type="primary" v-if="productData.productType !== 1 && treeSelectData && treeSelectData.menuType < 2" @click="editModal" style="margin-left: 10px">编辑菜单</a-button>
              <a-button type="danger" v-if="productData.productType !== 1 && treeSelectData && treeSelectData.menuType < 2" @click="delModal" style="margin-left: 10px">删除菜单</a-button>
              <a-button type="primary" v-if="productData.productType === 1" @click="showBindModal">绑定菜单</a-button>
            </a-form-item>
          </a-form>
          <a-tree
              :replace-fields="{children:'children', title:'menuName', key: 'menuCode'}"
              :tree-data="treeData"
              :expanded-keys="treeExpandedKeys"
              @expand="(expandedKeys)=>{this.treeExpandedKeys = expandedKeys}"
              :selectedKeys="selectedKeys"
              @select="treeCheck"
          />
        </a-col>
        <a-col span="16">
          <a-table :dataSource="data" :columns="columns" :pagination="pagination" :loading="loading"
                   size="small" bordered :rowKey="record => record.menuCode" :customRow="onClick"
                   :rowSelection="rowSelection">
          </a-table>
        </a-col>
      </a-row>
    </a-card>

    <a-modal v-model="addOrUpdateModal.visible" :title="addOrUpdateModal.title">
      <a-form :form="addOrUpdateModal.form" :label-col="addOrUpdateModal.labelCol" :wrapper-col="addOrUpdateModal.wrapperCol">
        <a-form-item label="关联产品">
          <div>{{this.productData.productName}}</div>
        </a-form-item>
        <a-form-item label="选中菜单名称">
          <div>{{this.treeSelectData ? this.treeSelectData.menuName : ''}}</div>
        </a-form-item>
        <a-form-item label="产品菜单类型" v-if="addOrUpdateModal.title === '添加产品菜单'">
          <a-select placeholder="请选择产品类型！" @change="menuTypeChange"
                    v-decorator="['menuType', {initialValue: '0', rules: [{ required: addOrUpdateModal.title === '添加产品菜单', message: '请输入产品类型！'}]}]">
            <a-select-option value="0">分类导航</a-select-option>
            <a-select-option value="1">菜单页面</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="前端菜单页面信息" v-if="menuTypeSelectData === '1'">
          <a-cascader
              :displayRender="({labels}) =>{return labels != null && labels.length > 0 ? labels[0] + ' - ' + labels[labels.length - 1] : []}"
              :fieldNames="{ label: 'menuName', value: 'menuCode', children: 'children' }"
              :options="uiPageTreeData" placeholder="请选择前端菜单页面信息"
              v-decorator="['uiPage', {rules: [{ required: menuTypeSelectData === '1', message: '请选择前端菜单页面信息！'}]}]"/>
        </a-form-item>
        <a-form-item label="产品菜单名称" v-if="menuTypeSelectData === '0'">
          <a-input placeholder="请输入产品菜单名称！"
                   v-decorator="['menuName', {rules: [{ required: menuTypeSelectData === '0', message: '请输入产品菜单名称！'}]}]"/>
        </a-form-item>
        <a-form-item label="产品菜单代码" v-if="addOrUpdateModal.title === '添加产品菜单' && menuTypeSelectData === '0'">
          <a-input placeholder="请输入产品菜单代码！"
                   v-decorator="['menuCode', {rules: [{ required: addOrUpdateModal.title === '添加产品菜单' && menuTypeSelectData === '0', message: '请输入产品菜单代码！'}]}]"/>
        </a-form-item>
        <a-form-item label="产品菜单图标" v-if="menuTypeSelectData === '0'">
          <a-input placeholder="请输入产品菜单图标！" v-decorator="['menuIcon']"/>
        </a-form-item>
        <a-form-item label="排序">
          <a-input-number placeholder="请输入排序！" style="width: 100%" v-decorator="['num', {rules: [{ required: true, message: '请输入排序！'}]}]"/>
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
      <a-tabs @change="modelNavChange">
        <a-tab-pane :key="1" tab="PC端"></a-tab-pane>
        <a-tab-pane :key="0" tab="其他"></a-tab-pane>
      </a-tabs>
      <a-tree
          :replace-fields="{children:'children', title:'menuName', key: 'menuCode'}"
          :tree-data="bindModal.bindTreeData"
          :expanded-keys="bindModal.bindTreeExpandedKeys"
          v-model="bindModal.checkedKeys"
          checkable
          checkStrictly
          @expand="bindOnExpand"
          @check="bindTreeCheck"
      />
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="bindModal.loading" @click="bindOk">
          确定
        </a-button>
        <a-button key="back" @click="bindModal.visible = false">取消</a-button>
      </template>
    </a-modal>
  </div>
</template>

<script>
  import * as operateApi from '../../api/operate'

  export default {
    name: 'ProductMenu',
    props: {
      productData: {
        id: undefined,
        productCode: undefined
      }
    },
    data () {
      return {
        loading: false,
        pageObj: {total: 10, pageSize: 10, pageNum: 1},
        pagination: false,
        navSelect: 1,
        sourceData: [],
        data: [],
        sourceTreeData: [],
        treeData: [],
        treeSelectData: {},
        selectedKeys: [],
        treeExpandedKeys: [],
        menuTypeSelectData: '0',
        uiPageTreeData: [],
        rowSelection: {
          selectedRowKeys: [],
          type: 'radio',
          onChange: (selectedRowKeys, selectedRows) => {
            this.rowSelectData = selectedRows[0]
            this.rowSelection.selectedRowKeys = selectedRowKeys
          }
        },
        rowSelectData: {},
        columns: [
          {title: '菜单名称', dataIndex: 'menuName'},
          {title: '菜单代码', dataIndex: 'menuCode'},
          {title: '菜单类型', dataIndex: 'menuType', customRender: (text, record, index) => { return text === 0 ? '分类导航' : text === 1 ? '菜单页面' : text === 2 ? '查询类按钮' : '操作类按钮' }},
          {title: '产品代码', dataIndex: 'productCode'},
          {title: '菜单图标', dataIndex: 'menuIcon'},
          {title: '排序号', dataIndex: 'num'}
        ],
        addOrUpdateModal: {
          labelCol: {span: 7},
          wrapperCol: {span: 12},
          loading: false,
          visible: false,
          title: '添加产品菜单',
          form: this.$form.createForm(this),
          data: {}
        },
        bindModal: {
          loading: false,
          visible: false,
          navSelect: 1,
          sourceData: [],
          bindTreeData: [],
          checkedKeys: [],
          bindTreeExpandedKeys: []
        }
      }
    },
    mounted () {
      this.init()
      operateApi.getUiPageTree({}).then(res => {
        this.uiPageTreeData = res.rows
      })
    },
    watch: {
      productData () {
        this.init()
      }
    },
    methods: {
      // 查询产品菜单树
      init () {
        this.menuTypeSelectData = '0'
        this.treeExpandedKeys = []
        this.treeSelectData = {}
        this.selectedKeys = []
        this.navSelect = 1
        this.sourceData = []
        this.data = []
        this.sourceTreeData = []
        this.treeData = []
        if (!this.productData || !this.productData.productCode) {
          return
        }
        operateApi.getProductMenuTree({productCode: this.productData.productCode}).then(res => {
          this.sourceData = res.rows
          this.sourceTreeData = res.rows
          this.data = []
          this.treeData = []
          for (let i = 0; i < this.sourceData.length; i++) {
            if (this.sourceData[i].navPlatform === this.navSelect) {
              this.data.push(this.sourceData[i])
              this.treeData.push(this.sourceData[i])
            }
          }
          this.handleTree(this.data)
        })
      },
      handleTree (treeList) {
        if (!treeList) {
          return
        }
        treeList.forEach(item => {
          if (item.menuType < 1) {
            this.treeExpandedKeys.push(item.menuCode)
            if (item.children) {
              this.handleTree(item.children)
            }
          }
        })
      },
      treeCheck (selectedKeys, info) {
        this.treeSelectData = info.selected ? info.node.dataRef : {}
        this.selectedKeys = selectedKeys
      },
      menuTypeChange (value) {
        this.menuTypeSelectData = value
      },
      navChange (value) {
        console.log('navChange', value)
        this.navSelect = value
        this.menuTypeSelectData = '0'
        this.treeExpandedKeys = []
        this.treeSelectData = {}
        this.selectedKeys = []
        this.data = []
        this.treeData = []
        for (let i = 0; i < this.sourceData.length; i++) {
          if (this.sourceData[i].navPlatform === this.navSelect) {
            this.data.push(this.sourceData[i])
            this.treeData.push(this.sourceData[i])
          }
        }
        this.handleTree(this.data)
      },
      addModal () {
        this.menuTypeSelectData = '0'
        this.addOrUpdateModal.data = {}
        this.addOrUpdateModal.form.resetFields()
        this.addOrUpdateModal.visible = true
        this.addOrUpdateModal.title = '添加产品菜单'
      },
      editModal () {
        this.addOrUpdateModal.data = this.treeSelectData
        setTimeout(() => {
          this.addOrUpdateModal.form.setFieldsValue({
            menuName: this.treeSelectData.menuName,
            menuIcon: this.treeSelectData.menuIcon,
            num: this.treeSelectData.num
          })
        }, 100)
        this.addOrUpdateModal.visible = true
        this.addOrUpdateModal.title = '编辑产品菜单'
      },
      delModal () {
        if (!this.treeSelectData || this.treeSelectData.menuType > 1) {
          this.$message.error('只能删除分类导航和菜单页面')
        }
        const that = this
        this.$confirm({
          title: '删除产品菜单?',
          onOk () {
            return new Promise((resolve, reject) => {
              operateApi.delProductMenu({productCode: that.productData.productCode, menuCode: that.treeSelectData.menuCode, navPlatform: that.navSelect}).then((res) => {
                if (res.success) {
                  that.$message.info(res.message)
                  that.init()
                  resolve(res.message)
                } else {
                  reject(res.message)
                }
              })
            })
          }
        })
      },
      addOrUpdateOk (e) {
        this.addOrUpdateModal.form.validateFields((err, values) => {
          if (!err) {
            // if ((!this.treeSelectData || !this.treeSelectData.menuCode) && values.menuType !== '0') {
            //   this.$message.error('未选上级的菜单只能是分类导航类型！')
            //   return
            // }
            this.addOrUpdateModal.loading = true
            if (this.addOrUpdateModal.title === '添加产品菜单') {
              if (this.treeSelectData && this.treeSelectData.menuType === 1) {
                this.$message.error('菜单页面类型下不能添加其他菜单！')
                this.addOrUpdateModal.loading = false
                return
              }
              let parm = {}
              if (values.menuType === '1') {
                const page = this.getPageInfo(values.uiPage[1])
                parm = {
                  ...values,
                  menuCode: page.menuCode,
                  navPlatform: this.navSelect,
                  productCode: this.productData.productCode,
                  parentCode: this.treeSelectData ? this.treeSelectData.menuCode : undefined
                }
              } else {
                parm = {
                  ...values,
                  productCode: this.productData.productCode,
                  navPlatform: this.navSelect,
                  parentCode: this.treeSelectData ? this.treeSelectData.menuCode : undefined
                }
              }
              operateApi.addProductMenu(parm).then(res => {
                if (res.success) {
                  this.$message.info('产品菜单添加成功！')
                  this.init()
                  this.addOrUpdateModal.visible = false
                }
                this.addOrUpdateModal.loading = false
              })
            } else {
              operateApi.editProductMenu({...values, menuCode: this.addOrUpdateModal.data.menuCode, navPlatform: this.navSelect}).then(res => {
                if (res.success) {
                  this.$message.info('修改成功！')
                  this.init()
                  this.addOrUpdateModal.visible = false
                }
                this.addOrUpdateModal.loading = false
              })
            }
          }
        })
      },
      getPageInfo (menuCode) {
        for (let i = 0; i < this.uiPageTreeData.length; i++) {
          let ui = this.uiPageTreeData[i].children
          for (let j = 0; j < ui.length; j++) {
            if (ui[j].menuCode === menuCode) {
              return ui[j]
            }
          }
        }
        return {}
      },
      onClick (record) {
        return {
          on: {
            click: () => {
              this.rowSelectData = record
              this.rowSelection.selectedRowKeys = [record.id]
            }
          }
        }
      },
      showBindModal () {
        this.bindModal.navSelect = 1
        this.bindModal.sourceData = []
        this.bindModal.bindTreeData = []
        this.bindModal.bindTreeExpandedKeys = []
        this.bindModal.checkedKeys = []
        operateApi.getProductMenuTree({productCode: this.productData.parentCode}).then(res => {
          this.bindModal.sourceData = res.rows
          for (let i = 0; i < this.bindModal.sourceData.length; i++) {
            if (this.bindModal.sourceData[i].navPlatform === this.bindModal.navSelect) {
              this.bindModal.bindTreeData.push(this.bindModal.sourceData[i])
            }
          }
          this.handleBindTree(this.bindModal.bindTreeData, this.bindModal.bindTreeExpandedKeys)
          let parentData = []
          for (let i = 0; i < this.sourceData.length; i++) {
            if (this.sourceData[i].navPlatform === this.bindModal.navSelect) {
              parentData.push(this.sourceData[i])
            }
          }
          this.handleBindTree(parentData, this.bindModal.checkedKeys)
          this.bindModal.visible = true
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
        let parentData = []
        for (let i = 0; i < this.sourceData.length; i++) {
          if (this.sourceData[i].navPlatform === this.bindModal.navSelect) {
            parentData.push(this.sourceData[i])
          }
        }
        this.handleBindTree(parentData, this.bindModal.checkedKeys)
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
      bindOnExpand (expandedKeys) {
        this.bindModal.bindTreeExpandedKeys = expandedKeys
      },
      bindOk () {
        operateApi.bindProductMenu({sourceCodes: [this.productData.productCode], targetCodes: this.bindModal.checkedKeys, navPlatform: this.bindModal.navSelect}).then(res => {
          this.init()
          this.bindModal.visible = false
        })
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
      }
    }
  }
</script>

<style scoped>

</style>
