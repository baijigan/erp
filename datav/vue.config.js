const path = require('path')
const resolve = dir => {
  return path.join(__dirname, dir)
}

const port = process.env.port || process.env.npm_config_port || 8081 // dev port
module.exports = {
  publicPath: './',
  chainWebpack: config => {
    config.resolve.alias
      .set('_c', resolve('src/components')) // key,value自行定义，比如.set('@@', resolve('src/components'))
  },
  devServer: {
    port: port,
    open: true,
    overlay: {
      warnings: false,
      errors: true
    },
    before: require('./mock/mock-server.js')
  }
}
