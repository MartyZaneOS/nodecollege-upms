<template>
  <a-row style="padding: 10px;">
    <a-col span="24">
      <a-form layout="inline">
        <a-form-item label="应用名称">
          <a-input v-model="search.appName" placeholder="请输入应用名称！"></a-input>
        </a-form-item>
        <a-form-item label="应用代码">
          <a-input v-model="search.appCode" placeholder="请输入应用代码！"></a-input>
        </a-form-item>
        <a-form-item label="应用类型">
          <a-select v-model="search.appType" placeholder="请选择应用类型" style="width: 194px" allowClear>
            <a-select-option value="0">运维</a-select-option>
            <a-select-option value="1">2C</a-select-option>
            <a-select-option value="2">2B</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="状态">
          <a-select v-model="search.state" placeholder="请选择状态" style="width: 194px" allowClear>
            <a-select-option value="2">锁定</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="searchList">查询</a-button>
        </a-form-item>
      </a-form>
    </a-col>
    <a-col span="24" style="margin: 10px 0">
      <a-button type="primary" @click="addApp">新增应用</a-button>
    </a-col>
    <a-col span="24">
      <a-table :dataSource="data" :columns="columns" :pagination="pagination" :loading="loading" size="small" bordered
               :rowKey="record => record.appId">
        <template slot="operation" slot-scope="text, record, index">
          <div>
            <span>
              <a v-if="(record.state == 1 || record.state == 0) && record.appType === 2" @click="() => showAppRole(index)">预制角色</a>
              <a v-if="record.state == 1 || record.state == 0" @click="() => showApp(index)">资源信息</a>
              <a v-if="record.state == 1 || record.state == 0" @click="() => updateApp(index)">编辑</a>
              <a v-if="record.state == 1" @click="() => lockApp(index)">锁定</a>
              <a v-if="record.state == 1" @click="() => delApp(index)">删除</a>
              <a v-if="record.state == 2" @click="() => lockApp(index)">解锁</a>
            </span>
          </div>
        </template>
      </a-table>
      <a-pagination showSizeChanger :pageSize="pageObj.pageSize" :total="pageObj.total" v-model="pageObj.page"
                    :pageSizeOptions="['5', '10', '20', '50', '100']" style="height: 50px; line-height: 50px;"
                    :showTotal="total => `总计 ${total} 条`" @change="pageChange" @showSizeChange="showSizeChange"/>
    </a-col>
    <!--  添加修改应用  -->
    <a-modal v-model="model.visible" :title="model.title">
      <a-form :form="model.form">
        <a-form-item label="应用名称" :label-col="model.labelCol" :wrapper-col="model.wrapperCol">
          <a-input placeholder="请输入应用名称！" :disabled="model.disabled"
                   v-decorator="['appName', {rules: [{ required: true, message: '请输入应用名称！'}]}]"/>
        </a-form-item>
        <a-form-item label="应用代码" :label-col="model.labelCol" :wrapper-col="model.wrapperCol">
          <a-input placeholder="请输入应用代码！" :disabled="model.disabled"
                   v-decorator="['appCode', {rules: [{ required: true, message: '请输入应用代码！'}]}]"/>
        </a-form-item>
        <a-form-item label="应用类型" :label-col="model.labelCol" :wrapper-col="model.wrapperCol">
          <a-select placeholder="请选择应用类型" style="width: 194px" allowClear :disabled="model.disabled"
                    v-decorator="['appType', {rules: [{ required: true, message: '请输入应用类型！'}]}]">
            <a-select-option value="0">运维</a-select-option>
            <a-select-option value="1">2C</a-select-option>
            <a-select-option value="2">2B</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="应用路径" :label-col="model.labelCol" :wrapper-col="model.wrapperCol">
          <a-input placeholder="请输入应用路径！" :disabled="model.disabled"
                   v-decorator="['appPath', {rules: [{ required: true, message: '请输入应用路径！'}]}]"/>
        </a-form-item>
        <a-form-item label="健康检查路径" :label-col="model.labelCol" :wrapper-col="model.wrapperCol">
          <a-input placeholder="请输入健康检查路径！" :disabled="model.disabled"
                   v-decorator="['healthPath', {rules: [{ required: true, message: '请输入健康检查路径！'}]}]"/>
        </a-form-item>
        <a-form-item label="排序序号" :label-col="model.labelCol" :wrapper-col="model.wrapperCol">
          <a-input placeholder="请输入排序序号！"
                   v-decorator="['num', {rules: [{ required: true, message: '请输入排序序号！'}]}]"/>
        </a-form-item>
      </a-form>
      <template slot="footer">
        <a-button key="submit" type="primary" :loading="model.loading" @click="addOrUpdateAppOk">
          确定
        </a-button>
        <a-button key="back" @click="model.visible = false">取消</a-button>
      </template>
    </a-modal>
    <!--  应用资源详情  -->
    <a-modal v-model="model.resourceVisible" title="应用资源详情" width="90%">
      <AppMenu :app-data="model.appData"/>
      <template slot="footer">
        <a-button key="back" @click="model.resourceVisible = false">取消</a-button>
      </template>
    </a-modal>
    <!--  应用角色详情  -->
    <a-modal v-model="model.roleVisible" title="应用角色详情" width="1200px">
      <AppRole :app-data="model.appData"/>
      <template slot="footer">
        <a-button key="back" @click="model.roleVisible = false">取消</a-button>
      </template>
    </a-modal>
  </a-row>
</template>

<script>
  import AppMenu from './AppMenu'
  import AppRole from './AppRole'

  export default {
    name: 'UpmsApp',
    components: {
      AppMenu,
      AppRole
    },
    data () {
      return {
        pageObj: {total: 10, pageSize: 10, pageNum: 1},
        search: {
          appName: null,
          appCode: null,
          appType: null,
          state: null
        },
        model: {
          labelCol: {span: 7},
          wrapperCol: {span: 12},
          loading: false,
          visible: false,
          title: '添加应用',
          form: this.$form.createForm(this),
          data: {},
          disable: 'none',
          disabled: false,
          resourceVisible: false,
          roleVisible: false
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
          },
          {title: '应用名称', dataIndex: 'appName'},
          {title: '应用代码', dataIndex: 'appCode'},
          {
            title: '应用类型',
            dataIndex: 'appType',
            customRender: (text, record, index) => {
              if (text === 0) {
                return '运维'
              }
              if (text === 1) {
                return '2C'
              }
              return '2B'
            }
          },
          {title: '应用路径', dataIndex: 'appPath'},
          {title: '健康检查地址', dataIndex: 'healthPath'},
          {title: '排序序号', dataIndex: 'num'},
          {
            title: '状态',
            dataIndex: 'state',
            customRender: (text, record, index) => {
              return text === 2 ? '锁定' : '正常'
            }
          },
          {title: '操作', fixed: 'right', width: 300, scopedSlots: {customRender: 'operation'}}
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
        this.http.post('upmsApi', '/app/getAppList', {
          ...this.pageObj,
          data: {
            ...this.search
          }
        }).then((res) => {
          if (res.success) {
            this.data = res.rows
            this.pageObj.total = res.total
          }
          this.loading = false
        })
      },
      // 添加应用
      addApp () {
        this.model.visible = true
        this.model.title = '添加应用'
        this.model.data = {}
        this.model.disabled = false
        this.model.form.setFieldsValue({
          appName: null,
          appCode: null,
          appType: null,
          appPath: null,
          healthPath: null,
          num: null
        })
      },
      // 显示应用预制角色
      showAppRole (index) {
        this.model.roleVisible = true
        this.model.appData = this.data[index]
      },
      // 显示应用详情
      showApp (index) {
        this.model.resourceVisible = true
        this.model.appData = this.data[index]
      },
      // 更新应用
      updateApp (index) {
        this.model.visible = true
        this.model.title = '修改应用'
        this.model.data = this.data[index]
        this.model.disabled = true
        setTimeout(() => {
          this.model.form.setFieldsValue({
            appName: this.model.data.appName,
            appCode: this.model.data.appCode,
            appType: this.model.data.appType + '',
            appPath: this.model.data.appPath,
            healthPath: this.model.data.healthPath,
            num: this.model.data.num
          })
        }, 100)
      },
      // 删除应用
      delApp (index) {
        const that = this
        this.$confirm({
          title: '删除应用！?',
          onOk () {
            return new Promise((resolve, reject) => {
              console.log('删除')
              that.http.post('upmsApi', '/app/delApp', {appId: that.data[index].appId}).then((res) => {
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
      // 锁定/解锁应用
      lockApp (index) {
        console.log('锁定应用' + index)
        const that = this
        this.$confirm({
          title: that.data[index].state === 1 ? '锁定应用！?' : '解锁应用！',
          onOk () {
            return new Promise((resolve, reject) => {
              that.http.post('upmsApi', '/app/lockApp', {
                appId: that.data[index].appId,
                state: that.data[index].state === 1 ? 2 : 1
              }).then((res) => {
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
      // 添加或者更新应用
      addOrUpdateAppOk (e) {
        e.preventDefault()
        this.model.form.validateFields((err, values) => {
          if (!err) {
            console.log('Received values of form: ', values)
            this.model.loading = true
            let url = this.model.title === '添加应用' ? '/app/addApp' : '/app/updateApp'
            this.http.post('upmsApi', url, {
              appId: this.model.data.appId,
              ...values
            }).then((res) => {
              if (res.success) {
                this.$message.info(res.message)
                this.searchList()
              }
              this.model.loading = false
              this.model.visible = false
            })
          }
        })
      },
      // 切换页数
      pageChange (page, pageSize) {
        this.pageObj.pageNum = page
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

<style lang="less" scoped>
  .resource-detail {
    width: 300px;

    div {
      text-align: right;
      height: 24px;
      line-height: 24px;
      width: 100px;
      float: left;
    }

    span {
      text-align: left;
      height: 24px;
      line-height: 24px;
      width: 200px;
      float: left;
    }
  }
</style>
