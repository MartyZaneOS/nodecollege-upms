<template>
  <div>
    <iframe :src="iframeSrc" v-if="iframeSrc" width="100%"
            :style="{borderWidth: 0+'px', minHeight: height + 'px'}"></iframe>
  </div>
</template>

<script>
  export default {
    name: 'Iframe',
    data () {
      return {
        iframeSrc: '',
        height: this.$store.state.screenHeight - 114
      }
    },
    watch: {
      '$route.path' () {
        console.log('iframe')
        let path = this.$route.path.substring(7)
        this.iframeSrc = path
      },
      '$store.state.screenHeight' () {
        this.height = this.$store.state.screenHeight - 114
      }
    },
    mounted () {
      console.log('iframe')
      let path = this.$route.path.substring(7)
      this.iframeSrc = path
    }
  }
</script>

<style scoped>

</style>
