<template>
	<view class="wrap">
		<view class="search">
			<u-search v-model="keywords" @custom="search" @search="search"></u-search>
		</view>
		<scroll-view class="scroll-list msg-list-item" scroll-y="true">
			<view v-for="(item, index) in tableData" :key="index">
				<view class="msg-time">{{item.invoiceDate}}</view>
				<u-card :title="item.omCode" :sub-title="item.workStaff" padding="20" margin="0rpx 20rpx" thumb="/static/aidex/images/list-icon.png" 
							@click="goDetail(item.omCode, item.customer)">
					<view class="" slot="body">
						<view class="u-body-item u-flex  u-col-between u-p-t-0">
							<view class="u-body-item-title u-line-2">{{item.customer}}</view>
						</view>
					</view>
					<view class="" slot="body" v-for="(salve, idx) in item.omOrderSalve">
						<view class="u-body-item u-flex  u-col-between u-p-t-0">
							<view class="u-body-item-title u-line-2" style="color: #c2c2c2;" >{{salve.inv_code}} {{salve.inv_name}} {{salve.inv_attribute}} {{salve.quantity}}</view>
						</view>						
					</view>					
				</u-card>
			</view>
			<u-divider>已经到底了</u-divider>
		</scroll-view>
	</view>
</template>
<script>
/**
 * Copyright (c) 2013-Now http://zhjl.vip All rights reserved.
 */

import {
  listOrder,
  sequenceView,
	sequenceDetail
} from "../../api/sequence";
export default {
	data() {
		return {
			loading: false,
			queryParams: {
				pageNum: 1,
				pageSize: 100,
				ppNumber: "",
				customer: "",
				invName: "",
				DeliverStatus: "",
			},
			keywords: '',
			tableData: [],
		};
	},
	onLoad() {
		this.getOmOrderList();
	},
	methods: {
    getOmOrderList() {
      this.loading = true;
      listOrder(this.queryParams).then((res) => {
        if (res.code == 200) {
          this.tableData= res.rows;
          this.loading = false;
        }
      });
    },

    getSequence : function (row, column, e) {      
      sequenceView({ppNumber : row.omCode}).then((res)=>{
        if(res.code == 200){
          this.maxHeight= 'auto';
          this.invMsg= row.omCode +' '+ row.customer;
          const testDom = document.getElementById("mermaidx");
          this.handler(res.data, testDom);          
          this.nodes= res.data;
        }
      });
    },

		navTo(url) {
			uni.navigateTo({
				url: url
			});
		},
		search(value) {
			this.queryParams.customer= this.keywords;
			this.getOmOrderList();
		},
		goDetail(ppNumber, customer){
			let url= "/module/sales/views/sequence/detail?ppNumber="+ ppNumber+ "&customer="+customer;
			this.navTo(url);
		}
	}
};

</script>
<style lang="scss">
@import '../../../../common/uni.css';
page {
	background-color: #f5f5f5;
}
.wrap .search{
	background: #ffffff;
}
.msg-time{
	font-size: 26rpx;
	padding: 10px 0;
	color: #999999;
	text-align: center;
}
.u-card__foot{
	.u-icon{
		margin-right: 10px;
	}
}


</style>

