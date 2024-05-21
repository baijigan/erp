<template>
	<view class="wrap">
		<view class="search" style="background-color: #f5f5f5; padding:20px 20px 20px 20px;">			
			<view>销售订单：{{queryParams.ppNumber}}</view>
			<view>客户名称：{{queryParams.customer}}</view>
		</view>
		<view style="with: 85%; padding: 10px 20px; background-color: #ffffff; margin-top: 20px;">
			<u-time-line>
				<u-time-line-item nodeTop="2">
					<template v-slot:node>
						<view class="u-node" style="background: #19be6b;">
							<u-icon name="pushpin-fill" color="#ffff7f" :size="24"></u-icon>
						</view>
					</template>
					<template v-slot:content>
						<view>
							<view class="u-order-title">生产计划</view>
							<div v-for="(item, index) in tableData" :key="index">							
							<view class="u-order-desc" v-if="item.businessType=='mp_order'">{{item.code}} {{item.invoice_date}}</view>							
							</div>
						</view>
					</template>
				</u-time-line-item>
				<div v-for="(item, index) in tableData" :key="index">
				<u-time-line-item v-if="item.businessType=='mp_mbom'">
					<!-- 此处没有自定义左边的内容，会默认显示一个点 -->
					<template v-slot:content>
						<view>
							<view class="u-order-desc">物料清单 {{item.code}}</view>
							<view class="u-order-time">{{item.invoice_date}}</view>
						</view>
					</template>
				</u-time-line-item>		
				</div>
				<div v-for="(item, index) in tableData" :key="index">
				<u-time-line-item v-if="item.businessType=='mp_plan'">
					<!-- 此处没有自定义左边的内容，会默认显示一个点 -->
					<template v-slot:content>
						<view>
							<view class="u-order-desc">采购计划 {{item.code}}</view>
							<view class="u-order-time">{{item.invoice_date}}</view>
						</view>
					</template>
				</u-time-line-item>		
				</div>				
			</u-time-line>
			<u-time-line>
				<u-time-line-item nodeTop="2">
					<template v-slot:node>
						<view class="u-node" style="background: #19be6b;">
							<u-icon name="pushpin-fill" color="#ffff7f" :size="24"></u-icon>
						</view>
					</template>
					<template v-slot:content>
						<view>
							<view class="u-order-title">采购订单</view>
							<div v-for="(item, index) in tableData" :key="index">							
							<view class="u-order-desc" v-if="item.businessType=='po_order'">{{item.code}} {{item.invoice_date}}</view>							
							</div>
						</view>
					</template>
				</u-time-line-item>
				<div v-for="(item, index) in tableData" :key="index">
				<u-time-line-item v-if="item.businessType=='po_checkin'">
					<!-- 此处没有自定义左边的内容，会默认显示一个点 -->
					<template v-slot:content>
						<view>
							<view class="u-order-desc">采购入库 {{item.code}}</view>
							<view class="u-order-time">{{item.invoice_date}}</view>
						</view>
					</template>
				</u-time-line-item>		
				</div>				
			</u-time-line>					
			<u-time-line>
				<u-time-line-item nodeTop="2">
					<template v-slot:node>
						<view class="u-node" style="background: #19be6b;">
							<u-icon name="pushpin-fill" color="#fff" :size="24"></u-icon>
						</view>
					</template>
					<template v-slot:content>
						<view>
							<view class="u-order-title">生产订单</view>
							<div v-for="(item, index) in tableData" :key="index">							
							<view class="u-order-desc" v-if="item.businessType=='prs_order'">{{item.code}} {{item.invoice_date}}</view>							
							</div>
						</view>
					</template>
				</u-time-line-item>
				<div v-for="(item, index) in tableData" :key="index">
				<u-time-line-item v-if="item.businessType=='prs_product'">
					<!-- 此处没有自定义左边的内容，会默认显示一个点 -->
					<template v-slot:content>
						<view>
							<view class="u-order-desc">生产完工 {{item.code}}</view>
							<view class="u-order-time">{{item.invoice_date}}</view>
						</view>
					</template>
				</u-time-line-item>		
				</div>
				<div v-for="(item, index) in tableData" :key="index">
				<u-time-line-item v-if="item.businessType=='prs_checkin'">
					<!-- 此处没有自定义左边的内容，会默认显示一个点 -->
					<template v-slot:content>
						<view>
							<view class="u-order-desc">生产入库 {{item.code}}</view>
							<view class="u-order-time">{{item.invoice_date}}</view>
						</view>
					</template>
				</u-time-line-item>		
				</div>				
			</u-time-line>
			<u-time-line>
				<u-time-line-item nodeTop="2">
					<template v-slot:node>
						<view class="u-node" style="background: #19be6b;">
							<u-icon name="pushpin-fill" color="#fff" :size="24"></u-icon>
						</view>
					</template>
					<template v-slot:content>
						<view>
							<view class="u-order-title">仓库</view>
						</view>
					</template>
				</u-time-line-item>
				<div v-for="(item, index) in tableData" :key="index">
				<u-time-line-item v-if="item.businessType=='wm_in'">
					<!-- 此处没有自定义左边的内容，会默认显示一个点 -->
					<template v-slot:content>
						<view>
							<view class="u-order-desc">入库 {{item.code}}</view>
							<view class="u-order-time">{{item.invoice_date}}</view>
						</view>
					</template>
				</u-time-line-item>		
				</div>
				<div v-for="(item, index) in tableData" :key="index">
				<u-time-line-item v-if="item.businessType=='wm_out'">
					<!-- 此处没有自定义左边的内容，会默认显示一个点 -->
					<template v-slot:content>
						<view>
							<view class="u-order-desc">出库 {{item.code}}</view>
							<view class="u-order-time">{{item.invoice_date}}</view>
						</view>
					</template>
				</u-time-line-item>		
				</div>				
			</u-time-line>
			<u-time-line>
				<u-time-line-item nodeTop="2">
					<template v-slot:node>
						<view class="u-node" style="background: #19be6b;">
							<u-icon name="pushpin-fill" color="#fff" :size="24"></u-icon>
						</view>
					</template>
					<template v-slot:content>
						<view>
							<view class="u-order-title">开票收款</view>
							<div v-for="(item, index) in tableData" :key="index">
							<view class="u-order-desc" v-if="item.businessType=='om_fapiao'">{{item.code}} {{item.invoice_date}}</view>							
							</div>							
						</view>
					</template>
				</u-time-line-item>
				<div v-for="(item, index) in tableData" :key="index">
				<u-time-line-item v-if="item.businessType=='fd_receipt'">
					<!-- 此处没有自定义左边的内容，会默认显示一个点 -->
					<template v-slot:content>
						<view>
							<view class="u-order-desc">财务到账 {{item.code}}</view>
							<view class="u-order-time">{{item.invoice_date}}</view>
						</view>
					</template>
				</u-time-line-item>		
				</div>	
			</u-time-line>			
		</view>
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
	onLoad(option) {
			this.queryParams.ppNumber= option.ppNumber;
			this.queryParams.customer= option.customer;
			this.getOmOrderDetail();
	},
	methods: {
    getOmOrderDetail() {
      this.loading = true;
      sequenceDetail(this.queryParams).then((res) => {
        if (res.code == 200) {
          this.tableData= res.rows;
          this.loading = false;
        }
      });
    },
		businessFilter(item){
			let type= item.businessType;
			if(type=='om_order')return '销售订单'+ " "+ item.code;
			else if(type=='mp_mbom')return '物料计算'+ " "+ item.code;
			else if(type=='mp_order')return '生产计划'+ " "+ item.code;
			else if(type=='prs_order')return '生产订单'+ " "+ item.code;
			else if(type=='prs_product')return '生产完工'+ " "+ item.code;
			else if(type=='po_order')return '采购订单'+ " "+ item.code;
			else if(type=='po_checkin')return '采购入库'+ " "+ item.code;
			else if(type=='prs_checkin')return '生产入库'+ " "+ item.code;
			else if(type=='wm_in')return '仓库入库'+ " "+ item.code;
			else if(type=='wm_out')return '仓库出库'+ " "+ item.code;
			else if(type=='om_fapiao')return '销售开票'+ " "+ item.code;
			else if(type=='fd_receipt')return '财务收款'+ " "+ item.code;
		},
		colorFilter(type){
			if(type=='om_order')return 'background-color: #cfcfcf;';
			else if(type=='mp_mbom')return 'background-color: #f5ffd2;';
			else if(type=='mp_order')return 'background-color: #f5ffd2;';
			else if(type=='prs_order')return 'background-color: #a1d7ff;';
			else if(type=='prs_product')return 'background-color: #b2ffae;';
			else if(type=='po_order')return 'background-color: #a1d7ff;';
			else if(type=='po_checkin')return 'background-color: #223366;';
			else if(type=='prs_checkin')return 'background-color: #223366;';
			else if(type=='wm_in')return 'background-color: #223366;';
			else if(type=='wm_out')return 'background-color: #223366;';
			else if(type=='om_fapiao')return 'background-color: #223366;';
			else if(type=='fd_receipt')return 'background-color: #223366;';	
		},
		navTo(url) {
			uni.navigateTo({
				url: url
			});
		},
		search(value) {
			this.queryParams.customer= this.keywords;
			this.getOmOrderList();
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

.u-node {
	width: 44rpx;
	height: 44rpx;
	border-radius: 100rpx;
	display: flex;
	justify-content: center;
	align-items: center;
	background: #d0d0d0;
}

.u-order-title {
	color: #333333;
	font-weight: bold;
	font-size: 32rpx;
}

.u-order-desc {
	color: rgb(150, 150, 150);
	font-size: 28rpx;
	margin-bottom: 6rpx;
}

.u-order-time {
	color: rgb(200, 200, 200);
	font-size: 26rpx;
}

</style>

