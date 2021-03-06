'use strict'
// Template version: 1.3.1
// see http://vuejs-templates.github.io/webpack for documentation.

const path = require('path')

function onProxyRes (proxyRes, req, res) {
  // 本地开发时，只能使用 如下设置的 B端租户代码 对应的B端账号进行登录
  req.headers['login-tenant-code'] = 'testTenant'
}
module.exports = {
  dev: {

    // Paths
    assetsSubDirectory: 'static',
    assetsPublicPath: '/',
    proxyTable: {
      '/upmsApi': {
        target: 'http://localhost:1081/',
        changeOrigin: true,
        pathRewrite: {
          '^/upmsApi': '/'
        }
      }, '/ncimg': {
        target: 'http://120.79.216.173/ncimg/',
        changeOrigin: true,
        pathRewrite: {
          '^/ncimg': '/'
        }
      },
      // 走网关转发
      '/operateApi': {
        target: 'http://localhost:1081/',
        changeOrigin: true,
        pathRewrite: {
          '^/': '/'
        }
      },
      '/chatApi': {
        target: 'http://localhost:1081/',
        changeOrigin: true,
        pathRewrite: {
          '^/': '/'
        }
      },
      '/tenantApi': {
        target: 'http://localhost:1081/',
        changeOrigin: true,
        pathRewrite: {
          '^/': '/'
        }
      },
      '/ws': {
        target: 'http://localhost:2080/', // websocket 地址
        changeOrigin: true,
        ws: true,
        pathRewrite: {
          '^/': '/'
        }
      }
    },

    // Various Dev Server settings
    host: 'localhost', // can be overwritten by process.env.HOST
    port: 8083, // can be overwritten by process.env.PORT, if port is in use, a free one will be determined
    autoOpenBrowser: false,
    errorOverlay: true,
    notifyOnErrors: true,
    poll: false, // https://webpack.js.org/configuration/dev-server/#devserver-watchoptions-

    // Use Eslint Loader?
    // If true, your code will be linted during bundling and
    // linting errors and warnings will be shown in the console.
    useEslint: true,
    // If true, eslint errors and warnings will also be shown in the error overlay
    // in the browser.
    showEslintErrorsInOverlay: false,

    /**
     * Source Maps
     */

    // https://webpack.js.org/configuration/devtool/#development
    devtool: 'cheap-module-eval-source-map',

    // If you have problems debugging vue-files in devtools,
    // set this to false - it *may* help
    // https://vue-loader.vuejs.org/en/options.html#cachebusting
    cacheBusting: true,

    cssSourceMap: true
  },

  build: {
    // Template for index.html
    index: path.resolve(__dirname, '../dist/index.html'),

    // Paths
    assetsRoot: path.resolve(__dirname, '../dist'),
    assetsSubDirectory: 'static',
    assetsPublicPath: '/me/',

    /**
     * Source Maps
     */

    productionSourceMap: false,
    // https://webpack.js.org/configuration/devtool/#production
    devtool: '#source-map',

    // Gzip off by default as many popular static hosts such as
    // Surge or Netlify already gzip all static assets for you.
    // Before setting to `true`, make sure to:
    // npm install --save-dev compression-webpack-plugin
    productionGzip: false,
    productionGzipExtensions: ['js', 'css'],

    // Run the build command with an extra argument to
    // View the bundle analyzer report after build finishes:
    // `npm run build --report`
    // Set to `true` or `false` to always turn it on or off
    bundleAnalyzerReport: process.env.npm_config_report
  }
}
