<template>
  <div style="padding: 10px;">
    <a-row>
      <a-card title="当天访问数据" size="small">
        <a-col span="24">
          <a-form layout="inline">
            <a-form-item label="微服务接口信息">
              <a-cascader
                  changeOnSelect
                  :allowClear="false"
                  @change="(value)=>{api.hourSelected = value}"
                  :displayRender="displayRender"
                  style="width:600px"
                  :value="api.hourSelected"
                  :fieldNames="{ label: 'name', value: 'code', children: 'children' }"
                  :options="api.data"
                  placeholder="请选择接口信息"/>
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="searchDate">查询</a-button>
            </a-form-item>
          </a-form>
        </a-col>
        <a-col span="24">
          <div id="hourApiLines" style="width: 100%;height: 400px"></div>
        </a-col>
      </a-card>
    </a-row>
    <a-row>
      <a-card title="历史访问数据" size="small">
        <a-col span="24" style="margin-top: 10px;">
          <a-form layout="inline">
            <a-form-item label="日期区间">
              <a-range-picker
                  style="width:250px" :allowClear="false" :disabledDate="disabledDate"
                  @change="dataOnChange"/>
            </a-form-item>
            <a-form-item label="微服务接口信息">
              <a-cascader
                  changeOnSelect
                  @change="(value)=>{api.selected = value}"
                  :displayRender="displayRender"
                  :value="api.selected"
                  style="width:600px"
                  :fieldNames="{ label: 'name', value: 'code', children: 'children' }"
                  :options="api.data" placeholder="请选择接口信息"/>
            </a-form-item>
            <a-form-item>
              <a-button type="primary" @click="getHistoryLines">查询</a-button>
            </a-form-item>
          </a-form>
        </a-col>
        <a-col span="24">
          <div id="historyAppLines" style="width: 100%;height: 400px"></div>
        </a-col>
        <a-col span="24">
          <div id="historyApiLines" style="width: 100%;height: 400px"></div>
        </a-col>
      </a-card>
    </a-row>
  </div>
</template>

<script>
  import moment from 'moment'
  import 'moment/locale/zh-cn'
  import * as operateApi from '../../api/operate'

  moment.locale('zh-cn')

  export default {
    name: 'ApiVisit',
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
          hourSelected: ['gateway']
        },
        currentShow: false,
        historyShow: false,
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
        hourApiLines: {
          title: {text: '实时微服务接口/小时访问量', left: 'center'},
          tooltip: {
            trigger: 'axis',
            formatter: function (params) {
              params.sort((a, b) => b.data - a.data)
              params = params.slice(0, 11)
              let res = ['Top10']
              params.forEach(item => res.push(item.seriesName + ': ' + item.value + '(次/小时)'))
              return res.join('<br>')
            }
          },
          legend: {type: 'scroll', top: '10%', width: '90%', data: []},
          grid: {left: '3%', right: '4%', bottom: '3%', top: '20%', containLabel: true},
          xAxis: {type: 'category', boundaryGap: false, data: []},
          yAxis: {type: 'value'},
          series: []
        },
        historyAppLines: {
          title: {text: '微服务/天访问量', left: 'center'},
          tooltip: {trigger: 'axis'},
          legend: {type: 'scroll', top: '10%', width: '90%', data: []},
          grid: {left: '3%', right: '4%', bottom: '3%', top: '20%', containLabel: true},
          xAxis: {type: 'category', boundaryGap: false, data: []},
          yAxis: {type: 'value'},
          series: []
        },
        historyApiLines: {
          title: {text: '接口/天访问量', left: 'center'},
          tooltip: {
            trigger: 'axis',
            formatter: function (params) {
              params.sort((a, b) => b.data - a.data)
              params = params.slice(0, 11)
              let res = ['Top10']
              params.forEach(item => res.push(item.seriesName + ': ' + item.value + '(次/天)'))
              return res.join('<br>')
            }
          },
          legend: {type: 'scroll', top: '10%', width: '90%', data: []},
          grid: {left: '3%', right: '4%', bottom: '3%', top: '20%', containLabel: true},
          xAxis: {type: 'category', boundaryGap: false, data: []},
          yAxis: {type: 'value'},
          series: []
        }
      }
    },
    mounted () {
      let query = this.$route.query
      if (query && query.appName) {
        this.api.hourSelected = [query.appName, decodeURIComponent(query.controllerName), decodeURIComponent(query.apiUrl)]
        this.api.selected = [query.appName, decodeURIComponent(query.controllerName), decodeURIComponent(query.apiUrl)]
      }
      this.searchAPiList()
      let date = new Date()
      date.setDate(1)
      // this.search.dateRange[0] = this.timeFormat(date)
      this.search.date[0] = this.moment(this.timeFormat(date), this.dateFormat)
      // this.search.dateRange[1] = this.timeFormat(new Date())
      this.search.date[1] = this.moment(this.timeFormat(new Date()), this.dateFormat)
      this.search.oneDate = this.timeFormat(new Date())
      this.searchDate()
    },
    methods: {
      moment,
      defaultValue () {
        return this.search.date
      },
      disabledDate (current) {
        // Can not select days before today and today
        return current && current > moment().subtract(1, 'day').endOf('day')
      },
      // 查询接口树
      searchAPiList () {
        this.loading = true
        operateApi.getAppApiTree({}).then((res) => {
          if (res.success) {
            let apiData = res.rows
            this.api.list = res.rows
            this.setApiData(apiData)
            this.api.data = [{name: 'gateway', code: 'gateway'}, ...apiData]
          }
        })
      },
      setApiData (apiData) {
        apiData.forEach(item => {
          item.name = item.apiId != null ? item.apiUrl : item.controllerName != null ? item.controllerName : item.applicationName
          item.code = item.apiId != null ? item.apiUrl : item.controllerName != null ? item.controllerName : item.applicationName
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
          labelHtml = '微服务:' + labels.labels[0]
        } else if (labels.labels.length === 2) {
          labelHtml = '微服务:' + labels.labels[0] + ' 类: ' + labels.labels[1]
        } else {
          labelHtml = '微服务:' + labels.labels[0] + ' 类: ' + labels.labels[1] + ' 地址:' + labels.labels[2]
        }
        return labelHtml
      },
      searchDate () {
        this.getHourApiLines()
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
      // 网关当天分钟访问量
      getHourApiLines () {
        operateApi.getCurrentVisitData({
          data: {
            visitType: 0,
            timeDimension: 1,
            visitAppName: this.api.hourSelected[0] ? this.api.hourSelected[0] : 'gateway',
            visitUrl: this.api.hourSelected[2]
          }
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
              this.hourApiLines.xAxis.data.push(i < 10 ? '0' + i : i + '')
            }
            let service = []
            service.push({name: '汇总', type: 'line', smooth: true, data: []})
            this.hourApiLines.legend.data.push('汇总')
            res.rows.forEach(item => {
              let serviceName = item.visitUrl
              if (this.hourApiLines.legend.data.indexOf(serviceName) === -1) {
                this.hourApiLines.legend.data.push(serviceName)
              }
              if (this.hourApiLines.xAxis.data.indexOf(item.visitHour) === -1) {
                this.hourApiLines.xAxis.data.push(item.visitHour)
              }
              let has = false
              service.forEach(serviceItem => {
                if (serviceItem.name === serviceName) {
                  has = true
                }
              })
              if (!has) {
                service.push({name: serviceName, type: 'line', smooth: true, data: []})
              }
            })
            let dataList = []
            this.hourApiLines.xAxis.data.forEach(item => dataList.push(0))
            service.forEach(serviceItem => {
              serviceItem.data = [...dataList]
            })
            res.rows.forEach(item => {
              let serviceName = item.visitUrl
              service.forEach(serviceItem => {
                if (serviceItem.name === serviceName) {
                  let dataIndeOf = this.hourApiLines.xAxis.data.indexOf(item.visitHour)
                  serviceItem.data[dataIndeOf] = item.visitCount
                  service[0].data[dataIndeOf] = service[0].data[dataIndeOf] + serviceItem.data[dataIndeOf]
                }
              })
            })
            this.hourApiLines.series = service
            this.currentShow = true
            console.log('this.dateApiLines', this.hourApiLines)

            var demo = window.echarts.init(document.getElementById('hourApiLines'))
            demo.setOption(this.hourApiLines)
          }
        })
      },
      getHistoryLines () {
        this.getHistoryAppLines()
        this.getHistoryApiLines()
        this.historyShow = true
      },
      // 网关当天分钟访问量
      getHistoryAppLines () {
        if (!this.search.dateRange || this.search.dateRange.length === 0) {
          this.$message.info('请选择时间区间！')
          return
        }
        operateApi.getHistoryVisitData({
          groupByKey: 'visitDay, visitAppName',
          startDate: this.moment(this.search.dateRange[0] + ' 00:00:00', 'YYYYMMDD HH:mm:ss').format('YYYY-MM-DD HH:mm:ss'),
          endDate: this.moment(this.search.dateRange[1] + ' 23:59:59', 'YYYYMMDD HH:mm:ss').format('YYYY-MM-DD HH:mm:ss'),
          data: {
            visitType: 1,
            timeDimension: 2,
            visitAppName: this.api.selected[0]
          }
        }).then((res) => {
          if (res.success) {
            this.historyAppLines.legend.data = []
            this.historyAppLines.xAxis.data = []
            this.historyAppLines.series = []
            this.historyAppLines = {...this.historyAppLines}
            let service = []
            service.push({name: '汇总', type: 'line', smooth: true, data: []})
            this.historyAppLines.legend.data.push('汇总')
            res.rows.forEach(item => {
              let serviceName = item.visitAppName
              if (this.historyAppLines.legend.data.indexOf(serviceName) === -1) {
                this.historyAppLines.legend.data.push(serviceName)
              }
              if (this.historyAppLines.xAxis.data.indexOf(item.visitDay) === -1) {
                this.historyAppLines.xAxis.data.push(item.visitDay)
              }
              let has = false
              service.forEach(serviceItem => {
                if (serviceItem.name === serviceName) {
                  has = true
                }
              })
              if (!has) {
                service.push({name: serviceName, type: 'line', smooth: true, data: []})
              }
            })
            let dataList = []
            this.historyAppLines.xAxis.data.forEach(item => dataList.push(0))
            service.forEach(serviceItem => {
              serviceItem.data = [...dataList]
            })
            res.rows.forEach(item => {
              let serviceName = item.visitAppName
              service.forEach(serviceItem => {
                if (serviceItem.name === serviceName) {
                  let dataIndeOf = this.historyAppLines.xAxis.data.indexOf(item.visitDay)
                  serviceItem.data[dataIndeOf] = item.visitCount
                  service[0].data[dataIndeOf] = service[0].data[dataIndeOf] + serviceItem.data[dataIndeOf]
                }
              })
            })
            this.historyAppLines.series = service
            console.log('this.historyApiLines', this.historyAppLines)
            var demo = window.echarts.init(document.getElementById('historyAppLines'))
            demo.setOption(this.historyAppLines)
          }
        })
      },
      // 网关当天分钟访问量
      getHistoryApiLines () {
        if (!this.search.dateRange || this.search.dateRange.length === 0) {
          this.$message.info('请选择时间区间！')
          return
        }
        operateApi.getHistoryVisitData({
          groupByKey: 'visitDay, visitUrl',
          startDate: this.moment(this.search.dateRange[0] + ' 00:00:00', 'YYYYMMDD HH:mm:ss').format('YYYY-MM-DD HH:mm:ss'),
          endDate: this.moment(this.search.dateRange[1] + ' 23:59:59', 'YYYYMMDD HH:mm:ss').format('YYYY-MM-DD HH:mm:ss'),
          data: {
            visitType: 0,
            timeDimension: 2,
            visitAppName: this.api.selected[0],
            visitUrl: this.api.selected[2]
          }
        }).then((res) => {
          if (res.success) {
            this.historyApiLines.legend.data = []
            this.historyApiLines.xAxis.data = []
            this.historyApiLines.series = []
            this.historyApiLines = {...this.historyApiLines}
            let service = []
            service.push({name: '汇总', type: 'line', smooth: true, data: []})
            this.historyApiLines.legend.data.push('汇总')
            res.rows.forEach(item => {
              let serviceName = item.visitUrl
              if (this.historyApiLines.legend.data.indexOf(serviceName) === -1) {
                this.historyApiLines.legend.data.push(serviceName)
              }
              if (this.historyApiLines.xAxis.data.indexOf(item.visitDay) === -1) {
                this.historyApiLines.xAxis.data.push(item.visitDay)
              }
              let has = false
              service.forEach(serviceItem => {
                if (serviceItem.name === serviceName) {
                  has = true
                }
              })
              if (!has) {
                service.push({name: serviceName, type: 'line', smooth: true, data: []})
              }
            })
            let dataList = []
            this.historyApiLines.xAxis.data.forEach(item => dataList.push(0))
            service.forEach(serviceItem => {
              serviceItem.data = [...dataList]
            })
            res.rows.forEach(item => {
              let serviceName = item.visitUrl
              service.forEach(serviceItem => {
                if (serviceItem.name === serviceName) {
                  let dataIndeOf = this.historyApiLines.xAxis.data.indexOf(item.visitDay)
                  serviceItem.data[dataIndeOf] = item.visitCount
                  service[0].data[dataIndeOf] = service[0].data[dataIndeOf] + serviceItem.data[dataIndeOf]
                }
              })
            })
            this.historyApiLines.series = service
            console.log('this.historyApiLines', this.historyApiLines)
            var demo = window.echarts.init(document.getElementById('historyApiLines'))
            demo.setOption(this.historyApiLines)
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
