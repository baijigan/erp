<template>
	<view class="wrap">
		<view class="container">
			<view class="icon">
				<u-image width="108rpx" height="108rpx" src="/static/images/success.png"></u-image>
			</view>
			<view class="title">入库成功</view>
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
import {listWarehouses} from "../../api/checkin";
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
		if(option.workType=='0')this.titleLabel= '供应商';
		else if(option.workType=='1')this.titleLabel= '生产车间';
		else if(option.workType=='2')this.titleLabel= '外协厂家';
	},	
	methods: {
		getWarehouse(invoiceType, workType) {
				return new Promise((resolve, reject) => {				
					let query = {
								invoiceType: invoiceType,
								bredVouch: '0',
								workType: workType,
					};

					let selectwarehouse= '';
					listWarehouses(query).then((res) => {
						if (res.code == 200) {
							let optionswarehouse = res.rows;
							if (optionswarehouse.length > 0) {
								console.log(optionswarehouse);
								selectwarehouse = optionswarehouse[0].code;
								resolve(selectwarehouse);
							}
						}
					});							
				});
		},
		scanCode(){
			// 允许从相机和相册扫码
			const that = this;
			uni.scanCode({
				autoZoom: true,
				success: function (res) {
					console.log('条码类型：' + res.scanType);
					console.log('条码内容：' + res.result);
					that.scanCheckin(res.result);
				},
				fail: function(res) {
					this.$u.toast("扫码失败了！");
				}
			});			
		},		
		scanCheckin(barCode){
			//let barCode= "invoiceDict=pu_invoice_type&invoiceType=3&workDict=pu_checkin_type&workType=0&code=1674588984338157568&fmConfig=010605";
			//let barCode= "invoiceDict=po_invoice_type&invoiceType=2&workDict=po_checkin_type&workType=0&code=CGDH000123&fmConfig=010103,02010103";
			//let barCode= "invoiceDict=prs_invoice_type&invoiceType=3&workDict=prs_checkin_type&workType=0&code=SCRK000045&fmConfig=010604";
			const jsonCode = this.queryStringToObject(barCode);			
			let url= '/module/stock/views/checkin/index';
			if(jsonCode['invoiceDict']=='po_invoice_type' && jsonCode['invoiceType']=='2'	&& jsonCode['workDict']=='po_checkin_type'){
				this.getWarehouse('0', '0').then((warehouse)=>{					
					let param= "open=addStatus&whCode=" +warehouse+ "&bredVouch=0&invoiceType=0&workType=0&poCode="+ jsonCode['code'];					
					this.navTo(url+ "?"+ param);		
				});				
			}			
			else if(jsonCode['invoiceDict']=='prs_invoice_type' && jsonCode['invoiceType']=='3'	&& jsonCode['workDict']=='prs_checkin_type'){
				this.getWarehouse('0', '1').then((warehouse)=>{					
					let param= "open=addStatus&whCode=" +warehouse+ "&bredVouch=0&invoiceType=0&workType=1&prsCode="+ jsonCode['code'];					
					this.navTo(url+ "?"+ param);		
				});				
			}			
			else if(jsonCode['invoiceDict']=='pu_invoice_type' && jsonCode['invoiceType']=='3'	&& jsonCode['workDict']=='pu_checkin_type'){
				this.getWarehouse('0', '2').then((warehouse)=>{					
					let param= "open=addStatus&whCode=" +warehouse+ "&bredVouch=0&invoiceType=0&workType=2&puCode="+ jsonCode['code'];					
					this.navTo(url+ "?"+ param);		
				});				
			}			
			else{
				this.$u.toast("无法识别的单据");
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
