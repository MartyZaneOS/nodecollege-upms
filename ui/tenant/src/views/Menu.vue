<template>
  <a-row style="padding: 10px;">
    <!--    <a-col span="24" style="margin: 10px 0">-->
    <!--      <a-button type="primary" @click="addMenu">新增菜单</a-button>-->
    <!--    </a-col>-->
    <a-col span="24">
      <a-table :dataSource="model.menu.treeData" :columns="model.menu.columns"
               size="small" bordered :rowKey="record => record.menuId"
               :rowSelection="model.menu.rowSelection">
        <template slot="icon" slot-scope="text, record, index">
          <div><a-icon :type="text" v-if="text"/> {{text}}</div>
        </template>
      </a-table>
    </a-col>
    <!-- 添加/修改菜单   -->
    <a-modal v-model="model.visible" :title="model.title">
      <a-form :form="model.form">
        <a-form-item label="菜单名称" :label-col="model.labelCol" :wrapper-col="model.wrapperCol">
          <a-input placeholder="请输入菜单名称！"
                   v-decorator="['menuName', {rules: [{ required: true, message: '请输入菜单名称！'}]}]"/>
        </a-form-item>
        <a-form-item label="菜单代码" :label-col="model.labelCol" :wrapper-col="model.wrapperCol">
          <a-input placeholder="请输入菜单代码！"
                   v-decorator="['menuCode', {rules: [{ required: true, message: '请输入菜单代码！'}]}]"/>
        </a-form-item>
        <a-form-item label="菜单类型" :label-col="model.labelCol" :wrapper-col="model.wrapperCol">
          <a-select placeholder="请选择菜单类型" allowClear :disabled="model.disabled"
                    @change="(value, option) =>{this.modal.data.menuType = value}"
                    v-decorator="['menuType', {rules: [{ required: true, message: '请选择菜单类型！'}]}]">
            <a-select-option value="0" v-if="model.menu.data.menuType === 0">分页导航</a-select-option>
            <a-select-option value="1" v-if="model.menu.data.menuType === 0">菜单页面</a-select-option>
            <a-select-option value="2" v-if="model.menu.data.menuType === 1">查询类按钮</a-select-option>
            <a-select-option value="3" v-if="model.menu.data.menuType === 1">操作类按钮</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="排序号" :label-col="model.labelCol" :wrapper-col="model.wrapperCol">
          <a-input placeholder="请输入排序号！"
                   v-decorator="['num', {rules: [{ required: true, message: '请输入排序号！'}]}]"/>
        </a-form-item>
        <a-form-item label="路径" :label-col="model.labelCol" :wrapper-col="model.wrapperCol"
                     v-if="model.menu.data.menuType === 0">
          <a-input placeholder="请输入路径！"
                   v-decorator="['path', {rules: [{ required: model.menu.data.menuType === 0, message: '请输入路径！'}]}]"/>
        </a-form-item>
        <a-form-item label="接口路径" :label-col="model.labelCol" :wrapper-col="model.wrapperCol"
                     v-if="model.menu.data.menuType === 1">
          <a-input placeholder="请输入接口路径！"
                   v-decorator="['apiUrl', {rules: [{ required: model.menu.data.menuType === 1, message: '请输入接口路径！'}]}]"/>
        </a-form-item>
        <a-form-item label="打开方式" :label-col="model.labelCol" :wrapper-col="model.wrapperCol"
                     v-if="model.menu.data.menuType == '0' && model.data.menuType == '1'">
          <a-select placeholder="请选择打开方式" allowClear :disabled="model.disabled"
                    v-decorator="['openType', {rules: [{ required: model.menu.data.menuType == '0' && model.data.menuType == '1', message: '请选择打开方式！'}],
                    initialValue: '1'}]">
            <a-select-option value="0">单页方式</a-select-option>
            <a-select-option value="1">iframe方式</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="model.loading" @click="addOrUpdateResourceOk">
          确定
        </a-button>
        <a-button key="back" @click="handleCancel">取消</a-button>
      </template>
    </a-modal>
  </a-row>
</template>

<script>
  export default {
    name: 'Menu',
    data () {
      return {
        pageObj: {total: 10, pageSize: 10, pageNum: 1, pageSizeOptions: ['5', '10', '20', '50', '100']},
        model: {
          labelCol: {span: 7},
          wrapperCol: {span: 12},
          loading: false,
          visible: false,
          form: this.$form.createForm(this),
          data: {},
          menu: {
            visible: false,
            treeData: [],
            data: {},
            rowSelection: {
              type: 'radio',
              onChange: this.menuOnChange
            },
            columns: [
              {
                title: '菜单名称',
                dataIndex: 'menuName',
                sorter: true,
                width: 300
              }, {
                title: '菜单代码',
                dataIndex: 'menuCode',
                sorter: true
              }, {
                title: '图标',
                dataIndex: 'icon',
                scopedSlots: {customRender: 'icon'}
              }, {
                title: '菜单类型',
                dataIndex: 'menuType',
                sorter: true,
                customRender: (text, record, index) => {
                  if (text === 0) {
                    return '分类导航'
                  }
                  if (text === 1) {
                    return '菜单页面'
                  }
                  if (text === 2) {
                    return '按钮事件'
                  }
                }
              }, {
                title: '打开方式',
                dataIndex: 'openType',
                sorter: true,
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
                sorter: true
              }, {
                title: '接口路径',
                dataIndex: 'apiUrl',
                sorter: true
              }, {
                title: '排序号',
                dataIndex: 'num',
                sorter: true
              }, {
                title: '状态',
                dataIndex: 'state',
                sorter: true,
                customRender: (text, record, index) => {
                  return text === 2 ? '锁定' : '正常'
                }
              }, {
                title: '操作',
                scopedSlots: {customRender: 'operation'}
              }
            ]
          },
          addResource: {
            visible: false,
            title: '添加菜单',
            form: this.$form.createForm(this),
            data: {}
          }
        },
        loading: false,
        data: [],
        pagination: false,
        columns: [
          {
            title: '序号',
            width: '20',
            customRender: (text, record, index) => {
              return index + this.pageObj.pageSize * (this.pageObj.pageNum - 1) + 1
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
          }, {
            title: '操作',
            fixed: 'right',
            width: 200,
            scopedSlots: {customRender: 'operation'}
          }
        ]
      }
    },
    mounted () {
      this.searchList()
    },
    methods: {
      // 查询菜单树
      searchList () {
        this.loading = true
        this.http.post('upmsApi', '/menu/getMenuTree', {
          appId: this.model.data.appId
        }).then((res) => {
          if (res.success) {
            this.model.menu.treeData = res.rows
          }
          this.loading = false
        })
      },
      // 添加菜单
      addMenu () {
        this.model.visible = true
        this.model.data = {}
        this.model.form.setFieldsValue({
          addMenuTelephone: null,
          addMenuName: null
        })
      },
      // 更新成员
      updateMenu (index) {
        this.model.update.visible = true
        this.model.update.data = this.data[index]
        setTimeout(() => {
          this.model.update.form.setFieldsValue({
            memberAccount: this.model.update.data.memberAccount,
            memberName: this.model.update.data.memberName
          })
        }, 100)
      },
      // 删除成员
      delMenu (index) {
        const that = this
        this.$confirm({
          title: '删除成员！?',
          onOk () {
            return new Promise((resolve, reject) => {
              console.log('删除')
              that.http.post('upmsApi', '/member/delMenu', {memberId: that.data[index].memberId}).then((res) => {
                if (res.success) {
                  that.$message.info(res.message)
                  that.searchList()
                  resolve(res.message)
                } else {
                  that.$message.error(res.message)
                  reject(res.message)
                }
              })
            })
          }
        })
      },
      // 锁定/解锁成员
      lockMenu (index) {
        console.log('锁定成员' + index)
        const that = this
        this.$confirm({
          title: that.data[index].state === 1 ? '冻结成员！?' : '解冻成员！',
          onOk () {
            return new Promise((resolve, reject) => {
              that.http.post('upmsApi', '/member/lockMenu', {
                memberId: that.data[index].memberId,
                state: that.data[index].state === 1 ? 2 : 1
              }).then((res) => {
                if (res.success) {
                  that.$message.info(res.message)
                  that.searchList()
                  resolve(res.message)
                } else {
                  that.$message.error(res.message)
                  reject(res.message)
                }
              })
            })
          }
        })
      },
      // 添加菜单
      addMenuOk (e) {
        e.preventDefault()
        this.model.form.validateFields((err, values) => {
          if (!err) {
            console.log('Received values of form: ', values)
            this.model.loading = true
            this.http.post('upmsApi', '/member/inviteMenu', {
              ...values
            }).then((res) => {
              if (res.success) {
                this.$message.info(res.message)
                this.searchList()
              } else {
                this.$message.error(res.message)
              }
              this.model.loading = false
              this.model.visible = false
            })
          }
        })
      },
      // 更新成员
      updateMenuOk (e) {
        e.preventDefault()
        this.model.update.form.validateFields((err, values) => {
          if (!err) {
            console.log('Received values of form: ', values)
            this.model.loading = true
            this.http.post('upmsApi', '/member/updateMenu', {
              memberId: this.model.update.data.memberId,
              ...values
            }).then((res) => {
              if (res.success) {
                this.$message.info(res.message)
                this.searchList()
              } else {
                this.$message.error(res.message)
              }
              this.model.loading = false
              this.model.update.visible = false
            })
          }
        })
      },
      addOrUpdateResourceOk () {
      },
      // 模态框取消
      handleCancel () {
        this.model.visible = false
        this.model.update.visible = false
      },
      // 切换页数
      pageChange (pageNum, pageSize) {
        this.pageObj.pageNum = pageNum
        this.pageObj.pageSize = pageSize
        this.searchList()
      },
      // 切换每页数量
      showSizeChange (current, size) {
        this.pageObj.pageNum = 1
        this.pageObj.pageSize = size
        this.searchList()
      }
    }
  }
</script>

<style scoped>

</style>
