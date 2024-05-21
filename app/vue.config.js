// 应用全局配置，本地服务器配置
module.exports = {
	configureWebpack: {
		devServer: {
			port: 9090,
			disableHostCheck : true,
			proxy: {
				"/dev-api": {
					target: "http://127.0.0.1:8088",
					changeOrigin: "true",
					pathRewrite: {
						"^/dev-api": ""
					}
				}
			}
		},	
	},
}