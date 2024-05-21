// 应用全局配置
module.exports = {
  // 应用上下文
  baseUrl: ()=>{
		let server= uni.getStorageSync('server');
		if(server==null || server.length==0){
			return '';
		}
		
		let port= uni.getStorageSync('port');
		let tenantId= uni.getStorageSync('tenantId');
		let url= "http://"+ server+ ":"+ port+ "/"
		if(process.env.NODE_ENV === 'development'){
			url= url+ "dev-api";
		}else{
			url= url+ "prod-api";
		}
	
		return url;
  },
  
  // 应用信息
  appInfo: {
    // 应用名称
    name: "升阳云ERP",
    // 应用版本
    version: "1.1.0",
    // 应用logo
    logo: "/static/logo.png",
    // 官方网站
    site_url: "http://www.njrsun.com",
    // 政策协议
    agreements: [{
        title: "隐私政策",
        url: "https://www.njrsun.com/protocol.html"
      },
      {
        title: "用户服务协议",
        url: "https://www.njrsun.com/protocol.html"
      }
    ]
  }
}
