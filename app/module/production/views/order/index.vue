<template>
	<view class="wrap">
		<view class="search">
			<u-search v-model="keywords" @custom="search" @search="search"></u-search>
		</view>
		<scroll-view class="scroll-list msg-list-item" scroll-y="true">
			<view v-for="(item, index) in detailTableData" :key="index">
				<view class="msg-time">{{inMaterTime(item.invoiceDate)}}</view>
				<u-card :title="item.prsCode" :sub-title="inMaterTime(item.needDate)" padding="20" margin="0rpx 20rpx" thumb="/static/aidex/images/list-icon.png" >
					<view class="" slot="body">
						<view class="u-body-item u-flex  u-col-between u-p-t-0">
							<view class="u-body-item-title u-line-2">{{item.invCode}} {{item.invName}} {{item.invAttribute}} </view>
						</view>
					</view>
					<view class="" slot="body">
						<view class="u-body-item u-flex  u-col-between u-p-t-0">
							<view class="u-body-item-title u-line-2" style="color: #c2c2c2;" >生产数量 {{item.invQuantity}} &nbsp;&nbsp; 完工数量 {{item.wiQuantity}}</view>
						</view>						
					</view>
					<view class="" slot="body">
						<view class="u-body-item u-flex  u-col-between u-p-t-0">
							<view class="u-body-item-title u-line-2" style="color: #c2c2c2;" >跟单编号 {{item.ppNumber}} &nbsp;&nbsp; 交期 {{inMaterTime(item.ppDate)}}</view>
						</view>						
					</view>																		
				</u-card>
			</view>
			<u-divider>已经到底了</u-divider>
		</scroll-view>
	</view>
</template>

<script>
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
  listBeltline, //生产线
} from "../../api/order";
//并行请求
import axios from "axios";	
export default {
data() {
	return {
      // 单据遮罩层
      loading: true,
			keywords: '',
			
      // 单据查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 100,
        prsCode: "", //单据编码
        invoiceStatus: "",
        workStatus: "", //单据状态
        startDate: "",
        endDate: "",
        workType: '',
      },
      // 开始时间/结束时间配置
      pickerOptions: {
        shortcuts: [
          {
            text: "最近一周",
            onClick(picker) {
              const end = new Date(
                new Date(new Date().toLocaleDateString()).getTime()
              );
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit("pick", [start, end]);
            },
          },
          {
            text: "最近一个月",
            onClick(picker) {
              const end = new Date(
                new Date(new Date().toLocaleDateString()).getTime()
              );
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit("pick", [start, end]);
            },
          },
          {
            text: "最近三个月",
            onClick(picker) {
              const end = new Date(
                new Date(new Date().toLocaleDateString()).getTime()
              );
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              picker.$emit("pick", [start, end]);
            },
          },
        ],
      },
      valueDate: "",
      //明细状态
      detailStatus: true,
      activeName: "first",
      //单据表格key
      tableKey: "",
      // 搜索单据
      searchForm: {
        prsCode: "", //订单号
        ppNumber: "", //销售单号
        startDate: "",
        endDate: "",
        workStatus: "",
        invoiceStatus: "",
        invName: "", //物料名称
        invCode: "", //物料编码
        beltlineId: "",
      },
      //批量删除 批量审核 批量打印
      batchStatus: true,
      // 单据总条数
      total: 0,
      //业务状态
      optionsworkStatus: [],
      //单据状态
      optionsinvoiceStatus: [],
      // 生产线
      optionsBeltline: [],
      // 单据最大高度
      maxHeight: null,
      //单据数据
      tableData: [],
      // 已选单据编码
      codeArr: [],
      //详情抽屉
      drawerTitle: "",
      drawer: false,
      isDrawer: null,
      direction: "rtl",
      drawerData: null,
      drawerTableData: [],
      drawerCustomer: {
        omOrderSalveList: [],
      },
      materialStatus: false,
      drawerMaterial: {}, //物料信息
      drawerTableLoading: true,
      drawerTableKey: "",
      drawerTableMaxHeight: null,
      optionsDensityCode: [],
      // 明细遮罩层
      detailLoading: true,
      //单据参数信息
      parametersSetting: {
        decimalQuantity: "2", //数量小数位数
        decimalPrice: "2",
        industryCode: "",
      },
      //明细查询参数
      queryDetail: {
        pageNum: 1,
        pageSize: 100,
        prsCode: "", //订单号
        ppNumber: "", //销售单号
        invName: "",
        invCode: "", //物料编码
        startDate: "",
        endDate: "",
        beltlineId: "",
        workType: '',
      },
      // 小数位数
      decimal: 2,
      // 明细总条数
      detailTotal: 0,
      //明细表格key
      tableKey2: "",
      // 明细最大高度
      detailMaxHeight: null,
      //物料大类数据
      sortData: [],
      //物料大类下标
      sortIndex: null,
      //明细数据
      detailTableData: [],		
	}
},
onLoad(option) {	
		if(this.$route === undefined){
			this.$route= {};
			this.$route.query= {};
			this.$route.query.fmConfig= option.fmConfig;
			this.$route.query.workType= option.workType;	
			this.queryParams.workType= option.workType;
			this.queryDetail.workType= option.workType;		
		}
		
		this.getParameters().then(() => {
			this.getDetailList();
		});
},
methods: {
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
			this.queryDetail.invName= this.keywords;
			this.getDetailList();
		},
			
    // 获取明细
    getDetailList() {
      return new Promise((resolve, reject) => {
        this.detailLoading = true;
        listBill(this.queryDetail).then((res) => {
          if (res.code == 200) {
            this.detailLoading = false;
            this.detailTotal = res.total;
            this.tableKey2 = Math.random();
            this.getDecimal();
            this.detailMaxHeight = null;
            if (res.rows.length > 0) {
              let list = JSON.parse(JSON.stringify(res.rows));
              let last = list[0].prsCode,
                tag = 0;
              list.forEach((item, i) => {
                if (list[i].prsCode !== last) {
                  if (tag == 1) {
                    tag = 0;
                  } else if (tag == 0) {
                    tag = 1;
                  }
                }
                list[i].tag = tag;
                last = list[i].prsCode;
              });
              this.detailTableData = list;
              this.detailTableData.forEach((item) => {
                this.optionsDensityCode.forEach((obj) => {
                  if (item.densityCode == obj.dictValue) {
                    item.densityCode = obj.dictLabel;
                  }
                });
              });
              this.getDecimal();
            } else {
              //this.$refs["tableDetailHeight"].doLayout();
              this.detailTableData = [];
            }

          //   this.detailMaxHeight =
          //     `${document.documentElement.clientHeight}` - 270;
           }
        });
      });
    },
			
    //获取单据参数
    getParameters() {
      return new Promise((resolve, reject) => {
        billParameters({
          formPath: this.$route.query.fmConfig,
        }).then((res) => {
          if (res.code == 200) {
            if (this.parametersSetting.decimalQuantity !== "null") {
              this.parametersSetting.decimalQuantity =
                res.data["precision.quantity"];
            }
            if (this.parametersSetting.decimalPrice !== "null") {
              this.parametersSetting.decimalPrice = res.data["precision.price"];
            }
            this.parametersSetting.industryCode = this.$store.getters.industry;
            this.$nextTick(() => {
              //this.$refs["tableDetailHeight"].doLayout();
            });
            resolve();
          }
        });
      });
    },	
		
    //格式化数量单价金额小数位数
    getDecimal() {
      if (this.detailTableData.length > 0) {
        this.detailTableData.forEach((item) => {
          if (item.price !== null && item.price !== undefined) {
            item.price = parseFloat(item.price).toFixed(2);
          }
          if (item.invQuantity !== null && item.invQuantity !== undefined) {
            item.invQuantity = parseFloat(item.invQuantity).toFixed(2);
          }
          if (item.amount !== null && item.amount !== undefined) {
            item.amount = parseFloat(item.amount).toFixed(2);
          }
          if (item.wiQuantity !== null && item.wiQuantity !== undefined) {
            item.wiQuantity = parseFloat(item.wiQuantity).toFixed(2);
          }
        });
      }
      if (this.drawerTableData.length > 0) {
        this.drawerTableData.forEach((item) => {
          if (item.quantity !== null && item.quantity !== undefined) {
            item.quantity = parseFloat(item.quantity).toFixed(
              this.parametersSetting.decimalQuantity
            );
          }
          if (item.wi_quantity !== null && item.wi_quantity !== undefined) {
            item.wi_quantity = parseFloat(item.wi_quantity).toFixed(2);
          }
        });
      }
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