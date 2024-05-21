/**
 * Copyright (c) 2013-Now http://zhjl.vip All rights reserved.
 */
import Vue from 'vue';
import App from './App';

// 注入$tab、$auth、$modal
import plugins from './plugins'
Vue.use(plugins)

// 路由权限过滤、白名单
import './permission'

// npm install axios --save
import axios from 'axios'

Vue.config.productionTip = false;
App.mpType = 'app';

// 引入全局 uView 框架
import uView from 'uview-ui';
Vue.use(uView);

// 全局存储 vuex 的封装
import store from '@/store';

// 引入 uView 提供的对 vuex 的简写法文件
// let vuexStore = require('@/store/$u.mixin.js');
// Vue.mixin(vuexStore);

// 引入 uView 对小程序分享的 mixin 封装
let mpShare = require('uview-ui/libs/mixin/mpShare.js');
Vue.mixin(mpShare);

// Vue i18n 国际化
import VueI18n from '@/common/vue-i18n.min.js';
Vue.use(VueI18n);

// i18n 部分的配置，引入语言包，注意路径
import lang_zh_CN from '@/common/locales/zh_CN.js';
import lang_en from '@/common/locales/en.js';

const i18n = new VueI18n({
	// 默认语言
	locale: 'zh_CN',
	// 引入语言文件
	messages: {
		'zh_CN': lang_zh_CN,
		'en': lang_en,
	}
});

// 通用工具函数
import { getDicts } from "@/api/system/dict/data";
import { getConfigKey } from "@/api/system/config";
import { parseTime, resetForm, addDateRange, selectDictLabel, selectDictLabels, download, handleTree ,Decimal,timeData,timeMillisecond} from "@/utils/suncloud";
Vue.prototype.getDicts = getDicts
Vue.prototype.getConfigKey = getConfigKey
Vue.prototype.parseTime = parseTime
Vue.prototype.resetForm = resetForm
Vue.prototype.addDateRange = addDateRange
Vue.prototype.selectDictLabel = selectDictLabel
Vue.prototype.selectDictLabels = selectDictLabels
Vue.prototype.download = download
Vue.prototype.handleTree = handleTree
Vue.prototype.Decimal = Decimal
Vue.prototype.timeData = timeData
Vue.prototype.timeMillisecond = timeMillisecond
Vue.prototype.msgSuccess = function (msg) {
  this.$message({ showClose: true, message: msg, type: "success" });
}

Vue.prototype.msgError = function (msg) {
  this.$message({ showClose: true, message: msg, type: "error" });
}

Vue.prototype.msgInfo = function (msg) {
  this.$message.info(msg);
}

// 由于微信小程序的运行机制问题，需声明如下一行，H5和APP非必填
Vue.prototype._i18n = i18n;
const app = new Vue({
	i18n,
	store,
	...App
});

import { replaceAll } from '@/common/common.js'
Vue.prototype.replaceAll = replaceAll

// http 拦截器，将此部分放在 new Vue() 和 app.$mount() 之间，才能 App.vue 中正常使用
// import httpInterceptor from '@/common/http.interceptor.js';
// Vue.use(httpInterceptor, app);

// http 接口 API 抽离，免于写 url 或者一些固定的参数
// import httpApi from '@/common/http.api.js';
// Vue.use(httpApi, app);

app.$mount();
