<template>
  <div id="app">
    <!--    <img src="./assets/logo.png">-->
    <router-view/>
  </div>
</template>

<script>
  export default {
    name: 'App',
    mounted () {
      // 监听页面高度的变化
      window.addEventListener('resize', () => {
        this.$store.state.screenHeight = document.documentElement.clientHeight
        this.$store.state.screenWidth = document.documentElement.clientWidth
      })
    }
  }
</script>

<style>
  #app {
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    /*text-align: center;*/
    color: #2c3e50;
    height: 100%;
    /*margin-top: 60px;*/
  }
</style>
