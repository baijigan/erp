<template>
	<view class="">
		<view style="background-color: ;padding: 10px 10px;">
			<u-card :title="'物料清单：'+mpCode" margin="0rpx auto" thumb="/static/aidex/images/list-icon.png">
				<view class="" slot="body">
					<view class="u-body-item u-flex  u-col-between u-p-t-0">
						<view class="u-body-item-title u-line-2">{{invCode}} {{invName}} {{invAttribute}} </view>
					</view>
				</view>
				<view class="" slot="body">
					<view class="u-body-item u-flex  u-col-between u-p-t-0">
						<view class="u-body-item-title u-line-2" style="color: #c2c2c2;" >{{type}}数量 {{invQuantity}} {{unitName}} </view>
					</view>						
				</view>	
			</u-card>
		</view>
		<scroll-view scroll-y="true">
			<view v-for="(item, index) in drawerTableData" :key="index">
				<view style="background-color: #ffffff; margin-top: 6px;margin-left:10px;margin-right: 10px;">
					<view style="padding: 5px 20px;">{{item.invCode}} {{item.invName}} {{item.invAttribute}}</view>
					<u-line color="#a8a8a8" />
					<view style="padding: 5px 20px;">
						<table style="border-spacing: 0px;">
							<tr>
								<td width="22%" class="label">需求数量</td><td width="15%" class="text">{{ parseFloat(item.quantity).toFixed(0) }}</td>
								<td width="22%" class="label">可用数量</td><td width="15%" class="text">{{ parseFloat(item.wmQuantity).toFixed(0) }}</td>
								<td width="22%" class="label">缺少数量</td><td width="15%" class="text">{{ parseFloat(item.lessQuantity).toFixed(0) }}</td>
							</tr>
							<tr>
								<td class="label">计划采购</td><td class="text">
									<span style="color:#F56C6C" v-if="item.invSupplyType==0 && item.poQuantity<item.lessQuantity">{{ parseFloat(item.poQuantity).toFixed(0) }}</span>
								  <span v-else>{{ parseFloat(item.poQuantity).toFixed(0) }}</span>
								</td>
								<td class="label">采购订单</td><td class="text">
								  <span style="color:#F56C6C" v-if="item.pooQuantity<item.poQuantity">{{ parseFloat(item.pooQuantity).toFixed(0) }}</span>
								  <span v-else>{{ parseFloat(item.pooQuantity).toFixed(0) }}</span>
								</td>
								<td class="label">采购到货</td><td class="text">
								  <span style="color:#F56C6C" v-if="item.pocQuantitiy<item.pooQuantity">{{ parseFloat(item.pocQuantitiy).toFixed(0) }}</span>
								  <span v-else>{{ parseFloat(item.pocQuantitiy).toFixed(0) }}</span>
								</td>
							</tr>				
							<tr>
								<td class="label">入库数量</td><td class="text">
									<div style="text-align: left">
										<div v-if="item.invSupplyType==0">
											<span style="color:#F56C6C" v-if="parseFloat(item.readyQuantity)<parseFloat(item.pocQuantitiy)">{{ parseFloat(item.readyQuantity).toFixed(0) }}</span>
											<span v-else>{{ parseFloat(item.readyQuantity).toFixed(0) }}</span>
										</div>
										<div v-if="item.invSupplyType>0 && item.invSupplyType<=4">
											<span style="color:#F56C6C" v-if="item.readyQuantity<item.lessQuantity">{{ parseFloat(item.readyQuantity).toFixed(0) }}</span>
											<span v-else>{{ parseFloat(item.readyQuantity).toFixed(0) }}</span>                  
										</div>
									</div>
								</td>
								<td class="label"></td><td class="text">
								</td>
								<td class="label"></td><td class="text">
								</td>
							</tr>												
						</table>
					</view>	
				
				</view>
			</view>
			<u-divider>已经到底了</u-divider>
		</scroll-view>
	</view>
</template>

<script>
import { listMpReady, dictMatching, billParameters, Translate } 
from "../../api/order";
//并行请求
import axios from "axios";		
export default {
	data() {
		return {
			mpCode: '',
			invCode: '',
			invName: '',
			invAttribute: '',
			invQuantity: '',
			unitName: '',
			type: '',
			
      optionsinvSupplyType: [],
      //抽屉
      isDrawer: null,
      direction: "rtl",
      drawerData: null,
      drawerTableData: [],
      drawerTableLoading: true,
      drawerTableKey: "",
      drawerTableMaxHeight: null,
      drawerStatus: false,
      //配置
      decimalQuantity: "4",			
		}
	},
	onLoad(option){
		this.mpCode= option.mpCode;
		this.invCode= option.invCode;
		this.invName= option.invName;
		this.invAttribute= option.invAttribute;
		this.invQuantity= option.invQuantity;
		this.unitName= option.unitName;
		this.type= option.type;
		this.handleDawer(option.mpCode);
	},
	methods: {
		//字典与配置
		getDictionaryType() {
			const self = this;
			return new Promise((resolve) => {
				//并行请求
				axios
					.all([
						//查询销售订单号
						dictMatching("inv_supply_type")
					])
					.then(
						axios.spread(function (resSupplyType) {
							if (resSupplyType.code == 200) {
								self.optionsinvSupplyType = resSupplyType.data;
								resolve();
							}
						})
					);
			});
		},
    //数据
    handleDawer(code) {
      this.drawerTableLoading = true;
      let query = {
          mpCode: code
        };

      listMpReady(query).then((res) => {
        if (res.code == 200) {
          if (res.data.length > 0) {
            Translate(
              res.data,
              this.optionsinvSupplyType,
              "invSupplyType",
              "invSupplyTypeCn"
            ).then((data) => {
              this.drawerTableData = data;
              // this.getDecimal();
            });
          } else {
            this.drawerTableData = [];
          }

          // this.drawerTableMaxHeight =
          //   `${document.documentElement.clientHeight}` - 180;
          this.drawerTableLoading = false;
        }
      });
    },				
	}
}
</script>

<style lang="scss">
@import '../../../../common/uni.css';
page {
	background-color: #f5f5f5;
}
.wrap .search{
	background: #c7c7c7;
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

.label{
	color: #c3c3c3;
}

.text{
	color: #313131;
}

</style>

