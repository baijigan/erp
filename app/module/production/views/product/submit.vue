<template>
	<view class="wrap">
		<view class="container">
			<view class="icon">
				<u-image width="108rpx" height="108rpx" src="/static/images/success.png"></u-image>
			</view>
			<view class="title">报工完成</view>
			<view class="subTitle">单据编码：{{invoiceCode}}</view>			
			<view class="lblTitle">{{titleLabel}}：{{title}}</view>			
		</view>
		<view class="form-footer">
			<u-button class="btn" type="primary" @click="scanCode()">继续扫码</u-button>
		</view>
	</view>
</template>

<script>
import { TitleLevel } from "@icon-park/vue";
export default {
	data() {
		return {
			titleLabel: '',
			title: '',
			invoiceCode: ''
		}
	},
	onLoad(option) {		
		this.invoiceCode= option.invoiceCode;
		this.title= option.title;
		this.titleLabel= '生产车间';
	},	
	methods: {
		scanCode(){
			// 允许从相机和相册扫码
			const that = this;
			uni.scanCode({
				autoZoom: true,
				success: function (res) {
					console.log('条码类型：' + res.scanType);
					console.log('条码内容：' + res.result);
					that.scanProduct(res.result);
				},
				fail: function(res) {
					this.$u.toast("扫码失败了！");
				}
			});			
		},
		scanProduct(barCode){
			//let barCode= "invoiceDict=prs_invoice_type&invoiceType=0&workDict=prs_order_type&workType=0&code=SCDD000200&fmConfig=010601";
			const jsonCode = this.queryStringToObject(barCode);			
			let url= '/module/production/views/product/index';
			if(jsonCode['invoiceDict']=='prs_invoice_type' && jsonCode['invoiceType']=='0'	&& jsonCode['workDict']=='prs_order_type'){				
				let param= "open=addStatus&fmConfig=010602&workType=" + jsonCode['workType']+ "&prsCode="+ jsonCode['code'];
				this.navTo(url+ "?"+ param);
			}		
			else{
				this.$u.toast("无法识别的生产订单");
			}
		},
		queryStringToObject(queryString) {
			// 将字符串按照 & 分割成数组
			var pairs = queryString.split('&');
			
			// 创建结果对象
			var result = {};
			
			// 遍历数组中的每一对键值对
			for (var i = 0; i < pairs.length; i++) {
				// 将键值对按照 = 分割成数组
				var pair = pairs[i].split('=');
				
				// 如果键值对的长度不等于2，跳过该键值对
				if (pair.length !== 2) {
					continue;
				}
				
				// 获取键和值，并将它们存储到结果对象中
				var key = decodeURIComponent(pair[0]);
				var value = decodeURIComponent(pair[1]);
				result[key] = value;
			}
			
			return result;
		},		
		navTo(url) {
			uni.redirectTo({
				url: url
			});
		},					
	}
}
</script>

<style>
page {
	background-color: #f5f5f5;
}
	
.container{
	width: 100%;
	margin-top: 30%;
	display: flex;
	flex-direction: column;
	justify-content: center;
}

.icon{
	display: flex;
	justify-content: center;
	margin-bottom: 3%;
}

.title{
	margin: 0 auto;
	font-size: 16px;
}
.subTitle{
	margin-top: 30px;
	margin-left: 20px;
	font-size: 12px;
	color: #bebebe;
}
.lblTitle{
	margin-top: 5px;
	margin-left: 20px;
	font-size: 12px;
	color: #bebebe;	
}
.form-footer{
	margin-top: 2%;
}
</style>

