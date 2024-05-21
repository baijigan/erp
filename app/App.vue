<script>
/**
 * Copyright (c) 2013-Now http://zhjl.vip All rights reserved.
 */
import config from './config'
import store from '@/store'
import { getToken } from '@/utils/auth'

export default {
	onLaunch() {
		// 国际化，设置当前语言
		// if (this.vuex_locale){
		// 	this.$i18n.locale = this.vuex_locale;
		// 	this.$u.api.lang({lang: this.vuex_locale});
		// }
		
		// 设置底部导航栏角标
		uni.setTabBarBadge({
		  index: 0,
		  text: '3'
		});
		
		// uni.removeTabBarBadge({
		//   index: 0
		// });
		// 初始化应用
		this.initApp();
	},
	methods: {
		// 初始化应用
		initApp() {
			// 初始化应用配置
			this.initConfig()
			// 检查用户登录状态
			//#ifdef H5
			this.checkLogin()
			//#endif
		},
		initConfig() {
			this.globalData.config = config
		},
		checkLogin() {
			if (!getToken()) {
				this.$tab.reLaunch('/pages/sys/login/index') 
			}
		}
	}	
}
</script>

<style>
	@import url("~@/static/iconfont/iconfont.css");
</style>

<style lang="scss">
@import "uview-ui/index.scss";
@import "pages/common/aidex.scss";
</style>