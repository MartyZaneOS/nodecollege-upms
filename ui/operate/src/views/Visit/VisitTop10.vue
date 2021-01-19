<template>
  <a-row>
    <a-col span="12">
      <ECharts theme="ovilia-green" :options="option"/>
    </a-col>
  </a-row>
</template>

<script>
  import ECharts from 'vue-echarts'

  export default {
    name: 'VisitTop10',
    components: {
      ECharts
    },
    data () {
      return {
        option: {
          title: {
            text: '访问量TOP10'
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            }
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: {
            type: 'value'
          },
          yAxis: {
            type: 'category',
            data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
          },
          series: [
            {
              type: 'bar',
              label: {
                show: true,
                position: 'insideRight'
              },
              data: [320, 302, 301, 334, 390, 330, 320]
            }
          ]
        }
      }
    },
    mounted () {
      this.option.yAxis.data.push('sss')
    },
    methods: {}
  }
</script>

<style scoped>

</style>
