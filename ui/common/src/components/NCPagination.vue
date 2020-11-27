<template>
  <a-pagination showSizeChanger :pageSize="pageObj.pageSize" :total="pageObj.total" v-model="pageObj.pageNum"
                :pageSizeOptions="pageObj.pageSizeOptions" style="height: 50px; line-height: 50px;"
                :showTotal="total => `总计 ${total} 条`" @change="pageChange" @showSizeChange="showSizeChange"/>
</template>

<script>
  export default {
    name: 'NCPagination',
    props: {
      page: {
        total: {
          type: Number,
          default: 0
        },
        pageSize: {
          type: Number,
          default: 10
        },
        pageNum: {
          type: Number,
          default: 1
        },
        pageSizeOptions: {
          type: Array,
          default: ['5', '10', '20', '50', '100']
        }
      }
    },
    data () {
      return {
        pageObj: {total: 0, pageSize: 10, pageNum: 1, pageSizeOptions: ['5', '10', '20', '50', '100']}
      }
    },
    mounted () {
      this.pageObj = this.page
    },
    watch: {
      page () {
        console.log('page', this.page)
        console.log('pageObj', this.pageObj)
        this.pageObj = this.page
      }
    },
    methods: {
      // 切换页数
      pageChange (pageNum, pageSize) {
        this.pageObj.pageNum = pageNum
        this.pageObj.pageSize = pageSize
        this.$emit('changePage', this.pageObj)
        this.$emit('searchList')
      },
      // 切换每页数量
      showSizeChange (current, size) {
        this.pageObj.pageNum = 1
        this.pageObj.pageSize = size
        this.$emit('changePage', this.pageObj)
        this.$emit('searchList')
      }
    }
  }
</script>

<style scoped>

</style>
