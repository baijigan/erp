<template>
	<view class="wrap">
		<view class="search">
			<u-search v-model="keywords" @custom="search" @search="search"></u-search>
		</view>
		<scroll-view class="scroll-list msg-list-item" scroll-y="true">
			<view v-for="(item, index) in tableData" :key="index">
				<view class="msg-time">{{inMaterTime(item.invoiceDate)}}</view>
				<u-card :title="item.mpCode" :sub-title="item.salesman" padding="20" margin="0rpx 20rpx" thumb="/static/aidex/images/list-icon.png" 
							@click="goDetail(item.mpCode)">
					<view class="" slot="body">
						<view class="u-body-item u-flex  u-col-between u-p-t-0">
							<view class="u-body-item-title u-line-2">{{item.invCode}} {{item.invName}} {{item.invAttribute}} {{item.quantity}}{{item.unitName}}</view>
						</view>
					</view>
					<view class="" slot="body">
						<view class="u-body-item u-flex  u-col-between u-p-t-0">
							<view class="u-body-item-title u-line-2" style="color: #c2c2c2;" >销售订单 {{item.ppNumber}} 交期 {{inMaterTime(item.ppDate)}}</view>
						</view>						
					</view>	
					<view class="" slot="body">
						<view class="u-body-item u-flex  u-col-between u-p-t-0">
							<view class="u-body-item-title u-line-2" style="color: #c2c2c2;" >物料清单 {{item.mbomType}}</view>
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
  dictMatching, //字典
  listBill, //单据列表
  deleteBill, // 删除单条
  examineBill, //批量审核
  examineDeBill, //批量反审核
  billParameters,
  listDetail, //查询明细
  exportDetail, //明细导出
  listLink, //关联查询
  listCode, //订单号查询
  listSortChild,
  listMbom,
  listPurchase,
} from "../../api/order";
export default {
	data() {
		return {
			loading: false,
      queryParams: {
        pageNum: 1,
        pageSize: 100,
        mpCode: "", //单据编码
        invoiceStatus: "",
        workStatus: "", //单据状态
        startDate: "",
        endDate: "",
        workType: '0',
      },
			
			keywords: '',
			tableData: [],
		};
	},
	onLoad(option) {
		this.queryParams.workType= option.workType;
		this.getDetailList();
	},
	methods: {
    getDetailList() {
      return new Promise((resolve, reject) => {
        this.loading = true;
        listDetail(this.queryDetail).then((res) => {				
          if (res.code == 200) {					
            this.loading = false;
            // this.detailTotal = res.total;
            // this.tableKey2 = Math.random();
            // this.getDecimal();
            // this.detailMaxHeight = null;
            if (res.rows.length > 0) {
              let list = JSON.parse(JSON.stringify(res.rows));
              let last = list[0].mpCode,
              tag = 0;
              list.forEach((item, i) => {
                if (list[i].mpCode !== last) {
                  if (tag == 1) {
                    tag = 0;
                  } else if (tag == 0) {
                    tag = 1;
                  }
                }
                list[i].tag = tag;
                last = list[i].mpCode;
              });
              this.tableData = list;
              this.tableData.forEach((item) => {
                this.optionsDensityCode.forEach((obj) => {
                  if (item.densityCode == obj.dictValue) {
                    item.densityCode = obj.dictLabel;
                  }
                });
              });
              this.getDecimal();
            } else {
              this.tableData = [];
            }
          }
        });
      });
    },   
    inMaterTime(date) {
      let time = new Date(date);
      return (
        time.getFullYear() + "-" + (time.getMonth() + 1) + "-" + time.getDate()
      );
    },		
		navTo(url) {
			uni.navigateTo({
				url: url
			});
		},
		search(value) {
			this.queryParams.mpCode= this.keywords;
			this.getDetailList();
		},
		goDetail(mpCode){
			let param= "detailCode="+ mpCode +"&open=seeStatus&workType="+ this.queryParams.workType +"&fmConfig=010501&timeStamp=1688521263178";
			let url= "/module/plan/views/order/detail";
			this.navTo(url+"?"+ param);
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

