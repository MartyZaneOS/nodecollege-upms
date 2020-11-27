<template>
  <a-row style="padding: 10px;">
    <a-col span="24" style="margin: 10px 0">
      <a-button type="primary" @click="addMenu">新增应用菜单</a-button>
    </a-col>
    <a-col span="24">
      <a-table :dataSource="treeData" :columns="columns" :scroll="{ x: 2000, y: 500 }"
               size="small" bordered :rowKey="record => record.menuId"
               :rowSelection="rowSelection">
        <template slot="icon" slot-scope="text, record, index">
          <div>
            <a-icon :type="text"/>
            {{text}}
          </div>
        </template>
        <template slot="operation" slot-scope="text, record, index">
          <div>
            <span>
              <a @click="() => updateMenu(record)">编辑</a>
              <a @click="() => delMenu(record)">删除</a>
            </span>
          </div>
        </template>
      </a-table>
    </a-col>
    <!-- 添加/修改应用菜单   -->
    <a-modal v-model="modal.visible" :title="modal.title" width="600px">
      <a-form :form="modal.form">
        <a-form-item label="菜单名称" :label-col="modal.labelCol" :wrapper-col="modal.wrapperCol">
          <a-input placeholder="请输入菜单名称！"
                   v-decorator="['menuName', {rules: [{ required: true, message: '请输入菜单名称！'}]}]"/>
        </a-form-item>
        <a-form-item label="菜单代码" :label-col="modal.labelCol" :wrapper-col="modal.wrapperCol">
          <a-input placeholder="请输入菜单代码！"
                   v-decorator="['menuCode', {rules: [{ required: true, message: '请输入菜单代码！'}]}]"/>
        </a-form-item>
        <a-form-item label="菜单类型" :label-col="modal.labelCol" :wrapper-col="modal.wrapperCol">
          <a-select placeholder="请选择菜单类型" allowClear :disabled="modal.disabled"
                    @change="(value, option) =>{this.modal.saveOrUpdateData.menuType = value}"
                    v-decorator="['menuType', {rules: [{ required: true, message: '请选择菜单类型！'}]}]">
            <a-select-option value="0" v-if="selectData.menuType === 0">分页导航</a-select-option>
            <a-select-option value="1" v-if="selectData.menuType === 0">菜单页面</a-select-option>
            <a-select-option value="2" v-if="selectData.menuType === 1 || selectData.menuType === 2">查询类按钮
            </a-select-option>
            <a-select-option value="3" v-if="selectData.menuType === 2">操作类按钮</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="菜单图标" :label-col="modal.labelCol" :wrapper-col="modal.wrapperCol">
          <a-input placeholder="请输入菜单图标！"
                   v-decorator="['icon', {rules: [{ required: true, message: '请输入菜单图标！'}]}]"/>
        </a-form-item>
        <a-form-item label="排序号" :label-col="modal.labelCol" :wrapper-col="modal.wrapperCol">
          <a-input placeholder="请输入排序号！"
                   v-decorator="['num', {rules: [{ required: true, message: '请输入排序号！'}]}]"/>
        </a-form-item>
        <a-form-item label="路径" :label-col="modal.labelCol" :wrapper-col="modal.wrapperCol"
                     v-if="selectData.menuType === 0">
          <a-input placeholder="请输入路径！"
                   v-decorator="['path', {rules: [{ required: selectData.menuType === 0, message: '请输入路径！'}]}]"/>
        </a-form-item>
        <a-form-item label="接口信息" :label-col="modal.labelCol" :wrapper-col="modal.wrapperCol"
                     v-if="selectData.menuType >= 1">
          <a-cascader
              :displayRender="({labels}) =>{return labels != null && labels.length > 0 ? labels[0] + ' - ' + labels[labels.length - 1] : []}"
              :fieldNames="{ label: 'name', value: 'code', children: 'children' }"
              :options="api.data" placeholder="请选择接口信息"
              v-decorator="['apiPath', {rules: [{ required: selectData.menuType >= 1, message: '请选择接口信息！'}]}]"/>
        </a-form-item>
        <a-form-item label="打开方式" :label-col="modal.labelCol" :wrapper-col="modal.wrapperCol"
                     v-if="selectData.menuType == '0' && modal.saveOrUpdateData.menuType == '1'">
          <a-select placeholder="请选择打开方式" allowClear :disabled="modal.disabled"
                    v-decorator="['openType', {rules: [{ required: selectData.menuType == '0' && modal.saveOrUpdateData.menuType == '1', message: '请选择打开方式！'}]}]">
            <a-select-option value="0">单页方式</a-select-option>
            <a-select-option value="1">iframe方式</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="modal.loading" @click="addMenuOk">
          确定
        </a-button>
        <a-button key="back" @click="() => {modal.visible = false}">取消</a-button>
      </template>
    </a-modal>

    <!-- 修改应用菜单   -->
    <a-modal v-model="updateModal.visible" title="修改应用菜单" width="600px">
      <a-form :form="updateModal.form">
        <a-form-item label="菜单名称" :label-col="modal.labelCol" :wrapper-col="modal.wrapperCol">
          <a-input placeholder="请输入菜单名称！"
                   v-decorator="['menuName', {initialValue: updateModal.updateData.menuName, rules: [{ required: true, message: '请输入菜单名称！'}]}]"/>
        </a-form-item>
        <a-form-item label="菜单代码" :label-col="modal.labelCol" :wrapper-col="modal.wrapperCol">
          <a-input placeholder="请输入菜单代码！"
                   v-decorator="['menuCode', {initialValue: updateModal.updateData.menuCode, rules: [{ required: true, message: '请输入菜单代码！'}]}]"/>
        </a-form-item>
        <a-form-item label="菜单图标" :label-col="modal.labelCol" :wrapper-col="modal.wrapperCol">
          <a-input placeholder="请输入菜单图标！"
                   v-decorator="['icon', {initialValue: updateModal.updateData.icon, rules: [{ required: true, message: '请输入菜单图标！'}]}]"/>
        </a-form-item>
        <a-form-item label="排序号" :label-col="modal.labelCol" :wrapper-col="modal.wrapperCol">
          <a-input placeholder="请输入排序号！"
                   v-decorator="['num', {initialValue: updateModal.updateData.num, rules: [{ required: true, message: '请输入排序号！'}]}]"/>
        </a-form-item>
        <a-form-item label="路径" :label-col="modal.labelCol" :wrapper-col="modal.wrapperCol"
                     v-if="updateModal.updateData.menuType <= 1">
          <a-input placeholder="请输入路径！"
                   v-decorator="['path', {initialValue: updateModal.updateData.path, rules: [{ required: updateModal.updateData.menuType <= 1, message: '请输入路径！'}]}]"/>
        </a-form-item>
        <a-form-item label="接口信息" :label-col="modal.labelCol" :wrapper-col="modal.wrapperCol"
                     v-if="updateModal.updateData.menuType > 1">
          <a-cascader
              :displayRender="({labels}) =>{return labels != null && labels.length > 0 ? labels[0] + ' - ' + labels[labels.length - 1] : []}"
              :fieldNames="{ label: 'name', value: 'code', children: 'children' }"
              :options="api.data" placeholder="请选择接口信息"
              v-decorator="['apiPath', {initialValue: updateModal.updateData.apiPath, rules: [{ required: updateModal.updateData.menuType > 1, message: '请选择接口信息！'}]}]"/>
        </a-form-item>
      </a-form>
      <template slot="footer">
        <a-button key="submit" type="primary" @click="updateMenuOk">
          确定
        </a-button>
        <a-button key="back" @click="() => {updateModal.visible = false}">取消</a-button>
      </template>
    </a-modal>
  </a-row>
</template>

<script>
  export default {
    name: 'AppMenu',
    props: {
      appData: {
        type: Object,
        default: () => {
        }
      }
    },
    data () {
      return {
        loading: false,
        treeData: [],
        rowSelection: {
          type: 'radio',
          onChange: (selectedRowKeys, selectedRows) => {
            this.selectData = selectedRows[0]
          }
        },
        selectData: {},
        columns: [
          {
            title: '应用菜单名称',
            dataIndex: 'menuName',
            width: 300,
            fixed: 'left'
          }, {
            title: '应用菜单代码',
            dataIndex: 'menuCode',
            width: 200
          }, {
            title: '应用菜单类型',
            dataIndex: 'menuType',
            width: 150,
            customRender: (text, record, index) => {
              if (text === 0) {
                return '分类导航'
              }
              if (text === 1) {
                return '菜单页面'
              }
              if (text === 2) {
                return '查询类按钮'
              }
              if (text === 3) {
                return '操作类按钮'
              }
            }
          }, {
            title: '图标',
            dataIndex: 'icon',
            width: 150,
            scopedSlots: {customRender: 'icon'}
          }, {
            title: '打开方式',
            dataIndex: 'openType',
            width: 150,
            customRender: (text, record, index) => {
              if (text === 0) {
                return '单页方式'
              }
              if (text === 1) {
                return 'iframe方式'
              }
            }
          }, {
            title: '路径',
            dataIndex: 'path',
            width: 150
          }, {
            title: '排序号',
            dataIndex: 'num',
            width: 150
          }, {
            title: '状态',
            dataIndex: 'state',
            width: 150,
            customRender: (text, record, index) => {
              return text === 2 ? '锁定' : '正常'
            }
          }, {
            title: 'appName',
            dataIndex: 'applicationName',
            width: 150
          }, {
            title: '接口路径',
            dataIndex: 'apiUrl'
          }, {
            title: '操作',
            fixed: 'right',
            width: 150,
            scopedSlots: {customRender: 'operation'}
          }
        ],
        modal: {
          labelCol: {span: 7},
          wrapperCol: {span: 12},
          visible: false,
          title: '添加应用菜单',
          form: this.$form.createForm(this),
          disable: 'none',
          disabled: false,
          saveOrUpdateData: {}
        },
        updateModal: {
          visible: false,
          form: this.$form.createForm(this),
          loading: false,
          updateData: {}
        },
        api: {
          data: [],
          list: []
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
      this.searchAPiList()
    },
    methods: {
      // 查询应用菜单树
      searchList () {
        this.loading = true
        this.http.post('upmsApi', '/app/getMenuTreeByAppId', {
          appId: this.appData.appId
        }).then((res) => {
          if (res.success) {
            this.treeData = res.rows
          }
          this.loading = false
        })
      },
      // 添加应用菜单
      addMenu () {
        if (this.selectData.menuId == null) {
          this.$message.error('请先选择父菜单')
          return
        }
        if (this.selectData.menuType === 3) {
          this.$message.error('操作类按钮下不能添加菜单')
          return
        }
        this.modal.saveOrUpdateData = {}
        this.modal.visible = true
        this.modal.form.setFieldsValue({
          menuName: null,
          menuCode: null,
          menuType: null,
          icon: null,
          path: null,
          apiPath: [],
          openType: null,
          num: null
        })
      },
      // 更新菜单
      updateMenu (record) {
        this.updateModal.updateData = record
        let apiPath = []
        this.getApiPath(this.api.data, record, apiPath)
        this.updateModal.updateData.apiPath = apiPath
        this.updateModal.form.setFieldsValue({
          menuName: this.updateModal.updateData.menuName,
          menuCode: this.updateModal.updateData.menuCode,
          icon: this.updateModal.updateData.icon,
          path: this.updateModal.updateData.path,
          apiPath: this.updateModal.updateData.apiPath,
          num: this.updateModal.updateData.num
        })
        this.updateModal.visible = true
      },
      // 删除菜单
      delMenu (record) {
        const that = this
        this.$confirm({
          title: '删除菜单！?',
          onOk () {
            return new Promise((resolve, reject) => {
              console.log('删除')
              that.http.post('upmsApi', '/app/delMenuByAppId', {menuId: record.menuId}).then((res) => {
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
      // 查询接口树
      searchAPiList () {
        this.loading = true
        this.http.post('upmsApi', '/api/getApiTree').then((res) => {
          if (res.success) {
            let apiData = res.rows
            this.api.list = res.rows
            this.setApiData(apiData)
            this.api.data = apiData
          }
        })
      },
      setApiData (apiData) {
        apiData.forEach(item => {
          item.name = item.apiId != null ? item.apiUrl : item.controllerName != null ? item.controllerName : item.applicationName
          item.code = item.apiId != null ? item.apiId : item.controllerName != null ? item.controllerName : item.applicationName
          item.disabled = item.state === -1
          if (item.children) {
            this.setApiData(item.children)
          }
        })
      },
      getApiPath (apiData, record, apiPath) {
        if (apiPath.length > 0) {
          return
        }
        apiData.forEach(item => {
          if (apiPath.length > 0) {
            return
          }
          if (item.applicationName === record.applicationName && item.apiId === record.apiId) {
            apiPath.push(item.applicationName)
            apiPath.push(item.controllerName)
            apiPath.push(item.apiId)
            return
          }
          if (item.children) {
            this.getApiPath(item.children, record, apiPath)
          }
        })
      },
      // 提交新增或修改
      addMenuOk () {
        this.modal.form.validateFields((err, values) => {
          if (!err) {
            this.loading = true
            this.http.post('upmsApi', '/app/addMenuByAppId', {
              appId: this.appData.appId,
              parentId: this.selectData.menuId,
              apiId: values.apiPath != null ? values.apiPath[2] : null,
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
      updateMenuOk () {
        this.updateModal.form.validateFields((err, values) => {
          if (!err) {
            this.updateModal.loading = true
            this.http.post('upmsApi', '/app/updateMenuByAppId', {
              appId: this.appData.appId,
              menuId: this.updateModal.updateData.menuId,
              apiId: values.apiPath != null ? values.apiPath[2] : null,
              ...values
            }).then((res) => {
              if (res.success) {
                this.updateModal.visible = false
                this.searchList()
              }
              this.updateModal.loading = false
            })
          }
        })
      }
    }
  }
</script>

<style scoped>

</style>
