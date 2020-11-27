<template>
  <a-row style="padding: 10px;">
    <a-col span="24">
      <a-form layout="inline">
        <a-form-item label="日期区间">
          <a-date-picker @change="dateChange" :defaultValue="moment(timeFormat(new Date()), this.dateFormat)"
                         :allowClear="false" :disabledDate="disabledDate"/>
        </a-form-item>
        <a-form-item label="接口信息">
          <a-cascader
              changeOnSelect
              @change="(value)=>{api.hourSelected = value}"
              :displayRender="displayRender"
              style="width:600px"
              :fieldNames="{ label: 'name', value: 'code', children: 'children' }"
              :options="api.data" placeholder="请选择接口信息"/>
        </a-form-item>
        <a-form-item label="租户">
          <a-select
              showSearch :allowClear="true"
              :value="tenant.selectedId"
              placeholder="请输入租户名称"
              style="width: 200px"
              :defaultActiveFirstOption="false"
              :showArrow="false"
              :filterOption="false"
              @search="handleSearch"
              @change="(value)=>{tenant.selectedId = value}"
              :notFoundContent="null"
          >
            <a-select-option v-for="d in tenant.data" :key="d.tenantId">{{d.tenantName}}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="searchDate">查询</a-button>
        </a-form-item>
      </a-form>
    </a-col>
    <a-col span="12">
      <v-chart theme="ovilia-green" :options="hourApiLines" style="width: 100%"/>
    </a-col>
    <a-col span="12">
      <v-chart theme="ovilia-green" :options="hourTenantLines" style="width: 100%"/>
    </a-col>
    <a-col span="24">
      <hr/>
      <a-form layout="inline">
        <a-form-item label="日期区间">
          <a-range-picker
              style="width:250px" :allowClear="false" :disabledDate="disabledDate"
              :defaultValue="[moment(getMonthOneDay(new Date()), this.dateFormat),moment(timeFormat(new Date()), this.dateFormat)]"
              @change="dataOnChange"/>
        </a-form-item>
        <a-form-item label="接口信息">
          <a-cascader
              changeOnSelect
              @change="(value)=>{api.selected = value}"
              :displayRender="displayRender"
              style="width:600px"
              :fieldNames="{ label: 'name', value: 'code', children: 'children' }"
              :options="api.data" placeholder="请选择接口信息"/>
        </a-form-item>
        <a-form-item label="租户">
          <a-select
              showSearch :allowClear="true"
              :value="dateTenant.selectedId"
              placeholder="请输入租户名称"
              style="width: 200px"
              :defaultActiveFirstOption="false"
              :showArrow="false"
              :filterOption="false"
              @search="handleSearchDate"
              @change="(value)=>{dateTenant.selectedId = value}"
              :notFoundContent="null"
          >
            <a-select-option v-for="d in dateTenant.data" :key="d.tenantId">{{d.tenantName}}</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" @click="searchRangeDate">查询</a-button>
        </a-form-item>
      </a-form>
    </a-col>
    <a-col span="12">
      <v-chart theme="ovilia-green" :options="visitTop10" style="width: 100%"/>
    </a-col>
    <a-col span="12">
      <v-chart theme="ovilia-green" :options="tenantVisitTop10" style="width: 100%"/>
    </a-col>
    <a-col span="12">
      <v-chart theme="ovilia-green" :options="dateApiLines" style="width: 100%"/>
    </a-col>
    <a-col span="12">
      <v-chart theme="ovilia-green" :options="dateTenantLines" style="width: 100%"/>
    </a-col>
  </a-row>
</template>

<script>
  import ECharts from 'vue-echarts'
  import moment from 'moment'
  import 'moment/locale/zh-cn'

  moment.locale('zh-cn')

  export default {
    name: 'ApiVisit',
    components: {
      'v-chart': ECharts
    },
    data () {
      return {
        dateFormat: 'YYYYMMDD',
        search: {
          oneDate: '',
          dateRange: [],
          date: []
        },
        api: {
          data: [],
          list: [],
          selected: [],
          hourSelected: []
        },
        tenant: {
          pageObj: {total: 10, pageSize: 50, pageNum: 1},
          data: [],
          selectedId: null
        },
        dateTenant: {
          pageObj: {total: 10, pageSize: 50, pageNum: 1},
          data: [],
          selectedId: null
        },
        hourTenantLines: {
          title: {text: '小时-租户访问量'},
          tooltip: {trigger: 'axis'},
          legend: {type: 'scroll', top: '10%', width: '90%', data: []},
          grid: {left: '3%', right: '4%', bottom: '3%', top: '20%', containLabel: true},
          xAxis: {type: 'category', boundaryGap: false, data: []},
          yAxis: {type: 'value'},
          series: []
        },
        hourApiLines: {
          title: {text: '小时-接口访问量'},
          tooltip: {trigger: 'axis'},
          legend: {type: 'scroll', top: '10%', width: '90%', data: []},
          grid: {left: '3%', right: '4%', bottom: '3%', top: '20%', containLabel: true},
          xAxis: {type: 'category', boundaryGap: false, data: []},
          yAxis: {type: 'value'},
          series: []
        },
        visitTop10: {
          title: {text: '接口访问量TOP10'},
          tooltip: {trigger: 'axis', axisPointer: {type: 'shadow'}},
          grid: {left: '3%', right: '4%', bottom: '3%', containLabel: true},
          xAxis: {type: 'value'},
          yAxis: {type: 'category', data: []},
          series: [{type: 'bar', label: {show: true, position: 'insideRight'}, data: []}]
        },
        tenantVisitTop10: {
          title: {text: '租户访问量TOP10'},
          tooltip: {trigger: 'axis', axisPointer: {type: 'shadow'}},
          grid: {left: '3%', right: '4%', bottom: '3%', containLabel: true},
          xAxis: {type: 'value'},
          yAxis: {type: 'category', data: []},
          series: [{type: 'bar', label: {show: true, position: 'insideRight'}, data: []}]
        },
        dateTenantLines: {
          title: {text: '日期-租户访问量'},
          tooltip: {trigger: 'axis'},
          legend: {type: 'scroll', top: '10%', width: '90%', data: []},
          grid: {left: '3%', right: '4%', bottom: '3%', top: '20%', containLabel: true},
          xAxis: {type: 'category', boundaryGap: false, data: [], axisLabel: {rotate: 38}},
          yAxis: {type: 'value'},
          series: []
        },
        dateApiLines: {
          title: {text: '日期-接口访问量'},
          tooltip: {trigger: 'axis'},
          legend: {type: 'scroll', top: '10%', width: '90%', data: []},
          grid: {left: '3%', right: '4%', bottom: '3%', top: '20%', containLabel: true},
          xAxis: {type: 'category', boundaryGap: false, data: [], axisLabel: {rotate: 38}},
          yAxis: {type: 'value'},
          series: []
        }
      }
    },
    mounted () {
      this.searchAPiList()
      let date = new Date()
      date.setDate(1)
      this.search.dateRange[0] = this.timeFormat(date)
      this.search.date[0] = this.moment(this.timeFormat(date), this.dateFormat)
      this.search.dateRange[1] = this.timeFormat(new Date())
      this.search.date[1] = this.moment(this.timeFormat(new Date()), this.dateFormat)
      this.search.oneDate = this.timeFormat(new Date())
      this.searchDate()
      this.searchRangeDate()
    },
    methods: {
      moment,
      defaultValue () {
        return this.search.date
      },
      disabledDate (current) {
        // Can not select days before today and today
        return current && current > moment().endOf('day')
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
          if (item.children) {
            this.setApiData(item.children)
          }
        })
      },
      displayRender (labels) {
        let labelHtml = ''
        if (labels.labels.length === 0) {
          labelHtml = ''
        } else if (labels.labels.length === 1) {
          labelHtml = '应用:' + labels.labels[0]
        } else if (labels.labels.length === 2) {
          labelHtml = '应用:' + labels.labels[0] + ' 类: ' + labels.labels[1]
        } else {
          labelHtml = '应用:' + labels.labels[0] + ' 类: ' + labels.labels[1] + ' 地址:' + labels.labels[2]
        }
        return labelHtml
      },
      searchDate () {
        this.getHourApiLines()
        this.getHourTenantLines()
      },
      searchRangeDate () {
        this.getVisitTop10()
        this.getTenantVisitTop10()
        this.getDataApiLines()
        this.getDataTenantLines()
      },
      dateChange (date, dateString) {
        console.log(dateString)
        this.search.oneDate = dateString.split('-').join('')
      },
      dataOnChange (date, dateString) {
        this.search.dateRange[0] = dateString[0].split('-').join('')
        this.search.dateRange[1] = dateString[1].split('-').join('')
      },
      handleSearch (value) {
        if (value) {
          this.http.post('upmsApi', '/admin/tenant/getTenantList', {
            pageNum: this.tenant.pageObj.pageNum,
            pageSize: this.tenant.pageObj.pageSize,
            data: {
              tenantName: value
            }
          }).then((res) => {
            if (res.success) {
              this.tenant.data = res.rows
            }
          })
        } else {
          this.$message.warning('请输入租户名称！')
        }
      },
      handleSearchDate (value) {
        if (value) {
          this.http.post('upmsApi', '/admin/tenant/getTenantList', {
            pageNum: this.dateTenant.pageObj.pageNum,
            pageSize: this.dateTenant.pageObj.pageSize,
            data: {
              tenantName: value
            }
          }).then((res) => {
            if (res.success) {
              this.dateTenant.data = res.rows
            }
          })
        } else {
          this.$message.warning('请输入租户名称！')
        }
      },
      // 小时租户访问量
      getHourTenantLines () {
        this.http.post('upmsApi', '/api/getApiVisitList', {
          pageNum: 1,
          pageSize: -1,
          startHH: '',
          endHH: '',
          tenantId: this.tenant.selectedId,
          applicationName: this.api.hourSelected[0],
          controllerName: this.api.hourSelected[1],
          apiId: this.api.hourSelected[2],
          startDate: this.search.oneDate,
          endDate: this.search.oneDate,
          sortDirection: 'desc',
          sortKey: 'visitH',
          groupByKey: 'visitH, v.tenantId'
        }).then((res) => {
          if (res.success) {
            this.hourTenantLines.legend.data = []
            this.hourTenantLines.xAxis.data = []
            this.hourTenantLines.series = []
            this.hourTenantLines = {...this.hourTenantLines}
            let hours = new Date().getHours()
            if (this.search.oneDate !== this.timeFormat(new Date())) {
              hours = 24
            }
            for (let i = 0; i < hours; i++) {
              this.hourTenantLines.xAxis.data.push(i < 10 ? '0' + i : '' + i)
            }
            let service = []
            res.rows.forEach(item => {
              let serviceName = item.tenantName ? item.tenantName : item.tenantId ? '租户-' + item.tenantId : '运维平台'
              if (this.hourTenantLines.legend.data.indexOf(serviceName) === -1) {
                this.hourTenantLines.legend.data.push(serviceName)
              }
              if (this.hourTenantLines.xAxis.data.indexOf(item.visitH) === -1) {
                this.hourTenantLines.xAxis.data.push(item.visitH)
              }
              let has = false
              service.forEach(serviceItem => {
                if (serviceItem.name === serviceName) {
                  has = true
                }
              })
              if (!has) {
                service.push({name: serviceName, type: 'line', data: []})
              }
            })
            let dataList = []
            this.hourTenantLines.xAxis.data.forEach(item => dataList.push(0))
            service.forEach(serviceItem => {
              serviceItem.data = [...dataList]
            })
            res.rows.forEach(item => {
              let serviceName = item.tenantName ? item.tenantName : item.tenantId ? '租户-' + item.tenantId : '运维平台'
              service.forEach(serviceItem => {
                if (serviceItem.name === serviceName) {
                  let dataIndeOf = this.hourTenantLines.xAxis.data.indexOf(item.visitH)
                  serviceItem.data[dataIndeOf] = item.visitCount
                  service[0].data[dataIndeOf] = service[0].data[dataIndeOf] + serviceItem.data[dataIndeOf]
                }
              })
            })
            this.hourTenantLines.series = service
            console.log(this.hourTenantLines)
          }
        })
      },
      // 小时接口访问量
      getHourApiLines () {
        this.http.post('upmsApi', '/api/getApiVisitList', {
          pageNum: 1,
          pageSize: -1,
          startHH: '',
          endHH: '',
          tenantId: this.tenant.selectedId,
          applicationName: this.api.hourSelected[0],
          controllerName: this.api.hourSelected[1],
          apiId: this.api.hourSelected[2],
          startDate: this.search.oneDate,
          endDate: this.search.oneDate,
          sortDirection: 'desc',
          sortKey: 'visitCount',
          groupByKey: 'visitH, v.apiId'
        }).then((res) => {
          if (res.success) {
            this.hourApiLines.legend.data = []
            this.hourApiLines.xAxis.data = []
            this.hourApiLines.series = []
            this.hourApiLines = {...this.hourApiLines}
            let hours = new Date().getHours()
            if (this.search.oneDate !== this.timeFormat(new Date())) {
              hours = 24
            }
            for (let i = 0; i < hours; i++) {
              this.hourApiLines.xAxis.data.push(i < 10 ? '0' + i : '' + i)
            }
            let service = []
            res.rows.forEach(item => {
              let serviceName = item.applicationName + ' ' + item.apiUrl
              if (this.hourApiLines.legend.data.indexOf(serviceName) === -1) {
                this.hourApiLines.legend.data.push(serviceName)
              }
              if (this.hourApiLines.xAxis.data.indexOf(item.visitH) === -1) {
                this.hourApiLines.xAxis.data.push(item.visitH)
              }
              let has = false
              service.forEach(serviceItem => {
                if (serviceItem.name === serviceName) {
                  has = true
                }
              })
              if (!has) {
                service.push({name: serviceName, type: 'line', data: []})
              }
            })
            let dataList = []
            this.hourApiLines.xAxis.data.forEach(item => dataList.push(0))
            service.forEach(serviceItem => {
              serviceItem.data = [...dataList]
            })
            res.rows.forEach(item => {
              let serviceName = item.applicationName + ' ' + item.apiUrl
              service.forEach(serviceItem => {
                if (serviceItem.name === serviceName) {
                  let dataIndeOf = this.hourApiLines.xAxis.data.indexOf(item.visitH)
                  serviceItem.data[dataIndeOf] = item.visitCount
                  service[0].data[dataIndeOf] = service[0].data[dataIndeOf] + serviceItem.data[dataIndeOf]
                }
              })
            })
            this.hourApiLines.series = service
            console.log('this.dateApiLines', this.hourApiLines)
          }
        })
      },
      // 接口访问量top10
      getVisitTop10 () {
        this.http.post('upmsApi', '/api/getApiVisitList', {
          pageNum: 1,
          pageSize: 10,
          startHH: '',
          endHH: '',
          tenantId: this.dateTenant.selectedId,
          applicationName: this.api.selected[0],
          controllerName: this.api.selected[1],
          apiId: this.api.selected[2],
          startDate: this.search.dateRange[0],
          endDate: this.search.dateRange[1],
          sortDirection: 'desc',
          sortKey: 'visitCount',
          groupByKey: 'v.apiId'
        }).then((res) => {
          if (res.success) {
            this.visitTop10.yAxis.data = []
            this.visitTop10.series[0].data = []
            res.rows.forEach(item => {
              this.visitTop10.yAxis.data.push(item.applicationName + ' ' + item.apiUrl)
              this.visitTop10.series[0].data.push(item.visitCount)
            })
          }
        })
      },
      // 租户访问量top10
      getTenantVisitTop10 () {
        this.http.post('upmsApi', '/api/getApiVisitList', {
          pageNum: 1,
          pageSize: 10,
          startHH: '',
          endHH: '',
          tenantId: this.dateTenant.selectedId,
          applicationName: this.api.selected[0],
          controllerName: this.api.selected[1],
          apiId: this.api.selected[2],
          startDate: this.search.dateRange[0],
          endDate: this.search.dateRange[1],
          sortDirection: 'desc',
          sortKey: 'visitCount',
          groupByKey: 'v.tenantId'
        }).then((res) => {
          if (res.success) {
            this.tenantVisitTop10.yAxis.data = []
            this.tenantVisitTop10.series[0].data = []
            res.rows.forEach(item => {
              this.tenantVisitTop10.yAxis.data.push(item.tenantName ? item.tenantName : item.tenantId ? '租户-' + item.tenantId : '运维平台')
              this.tenantVisitTop10.series[0].data.push(item.visitCount)
            })
          }
        })
      },
      // 日期租户访问量
      getDataTenantLines () {
        this.http.post('upmsApi', '/api/getApiVisitList', {
          pageNum: 1,
          pageSize: -1,
          startHH: '',
          endHH: '',
          tenantId: this.dateTenant.selectedId,
          applicationName: this.api.selected[0],
          controllerName: this.api.selected[1],
          apiId: this.api.selected[2],
          startDate: this.search.dateRange[0],
          endDate: this.search.dateRange[1],
          sortDirection: 'desc',
          sortKey: 'visitDate',
          groupByKey: 'visitDate, v.tenantId'
        }).then((res) => {
          if (res.success) {
            this.dateTenantLines.legend.data = []
            this.dateTenantLines.xAxis.data = []
            this.dateTenantLines.series = []
            let service = []
            let dates = Number(this.search.dateRange[1]) - Number(this.search.dateRange[0])
            for (let d = 0; d < dates; d++) {
              this.dateTenantLines.xAxis.data[d] = Number(this.search.dateRange[0]) + d
              this.dateTenantLines.xAxis.data[d] = this.dateTenantLines.xAxis.data[d] < 10 ? '0' + this.dateTenantLines.xAxis.data[d] : this.dateTenantLines.xAxis.data[d] + ''
            }
            this.dateTenantLines = {...this.dateTenantLines}
            res.rows.forEach(item => {
              let serviceName = item.tenantName ? item.tenantName : item.tenantId ? '租户-' + item.tenantId : '运维平台'
              if (this.dateTenantLines.legend.data.indexOf(serviceName) === -1) {
                this.dateTenantLines.legend.data.push(serviceName)
              }
              if (this.dateTenantLines.xAxis.data.indexOf(item.visitDate) === -1) {
                this.dateTenantLines.xAxis.data.push(item.visitDate)
              }
              let has = false
              service.forEach(serviceItem => {
                if (serviceItem.name === serviceName) {
                  has = true
                }
              })
              if (!has) {
                service.push({name: serviceName, type: 'line', data: []})
              }
            })
            let dataList = []
            this.dateTenantLines.xAxis.data.forEach(item => dataList.push(0))
            service.forEach(serviceItem => {
              serviceItem.data = [...dataList]
            })
            res.rows.forEach(item => {
              let serviceName = item.tenantName ? item.tenantName : item.tenantId ? '租户-' + item.tenantId : '运维平台'
              service.forEach(serviceItem => {
                if (serviceItem.name === serviceName) {
                  let dataIndeOf = this.dateTenantLines.xAxis.data.indexOf(item.visitDate)
                  serviceItem.data[dataIndeOf] = item.visitCount
                  service[0].data[dataIndeOf] = service[0].data[dataIndeOf] + serviceItem.data[dataIndeOf]
                }
              })
            })
            this.dateTenantLines.series = service
            console.log(this.dateTenantLines)
          }
        })
      },
      // 日期接口访问量
      getDataApiLines () {
        this.http.post('upmsApi', '/api/getApiVisitList', {
          pageNum: 1,
          pageSize: -1,
          startHH: '',
          endHH: '',
          tenantId: this.dateTenant.selectedId,
          applicationName: this.api.selected[0],
          controllerName: this.api.selected[1],
          apiId: this.api.selected[2],
          startDate: this.search.dateRange[0],
          endDate: this.search.dateRange[1],
          sortDirection: '',
          sortKey: '',
          groupByKey: 'visitDate, v.apiId'
        }).then((res) => {
          if (res.success) {
            this.dateApiLines.legend.data = []
            this.dateApiLines.xAxis.data = []
            this.dateApiLines.series = []
            let dates = Number(this.search.dateRange[1]) - Number(this.search.dateRange[0])
            for (let d = 0; d < dates; d++) {
              this.dateApiLines.xAxis.data[d] = Number(this.search.dateRange[0]) + d
              this.dateApiLines.xAxis.data[d] = this.dateApiLines.xAxis.data[d] < 10 ? '0' + this.dateApiLines.xAxis.data[d] : this.dateApiLines.xAxis.data[d] + ''
            }
            this.dateApiLines = {...this.dateApiLines}
            let service = []
            res.rows.forEach(item => {
              let serviceName = item.applicationName + ' ' + item.apiUrl
              if (this.dateApiLines.legend.data.indexOf(serviceName) === -1) {
                this.dateApiLines.legend.data.push(serviceName)
              }
              if (this.dateApiLines.xAxis.data.indexOf(item.visitDate) === -1) {
                this.dateApiLines.xAxis.data.push(item.visitDate)
              }
              let has = false
              service.forEach(serviceItem => {
                if (serviceItem.name === serviceName) {
                  has = true
                }
              })
              if (!has) {
                service.push({name: serviceName, type: 'line', data: []})
              }
            })
            let dataList = []
            this.dateApiLines.xAxis.data.forEach(item => dataList.push(0))
            service.forEach(serviceItem => {
              serviceItem.data = [...dataList]
            })
            res.rows.forEach(item => {
              let serviceName = item.applicationName + ' ' + item.apiUrl
              service.forEach(serviceItem => {
                if (serviceItem.name === serviceName) {
                  let dataIndeOf = this.dateApiLines.xAxis.data.indexOf(item.visitDate)
                  serviceItem.data[dataIndeOf] = item.visitCount
                  service[0].data[dataIndeOf] = service[0].data[dataIndeOf] + serviceItem.data[dataIndeOf]
                }
              })
            })
            this.dateApiLines.series = service
            console.log('this.dateApiLines', this.dateApiLines)
          }
        })
      },
      getMonthOneDay (date) {
        if (!date || typeof (date) === 'string') {
          this.error('参数异常，请检查...')
        }
        var y = date.getFullYear()
        var m = date.getMonth() + 1
        return y + '' + (m < 10 ? '0' + m : m) + '01'
      },
      timeFormat (date) {
        if (!date || typeof (date) === 'string') {
          this.error('参数异常，请检查...')
        }
        var y = date.getFullYear()
        var m = date.getMonth() + 1
        var d = date.getDate()

        return y + '' + (m < 10 ? '0' + m : m) + '' + (d < 10 ? '0' + d : d)
      }
    }
  }
</script>

<style scoped>

</style>
