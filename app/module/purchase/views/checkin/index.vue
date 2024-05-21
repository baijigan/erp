<template>
	<view class="wrap">
		<view class="search" style="margin-bottom: 4px;">
			<u-search v-model="keywords" @custom="search" @search="search"></u-search>
		</view>
		<scroll-view class="scroll-list msg-list-item" scroll-y="true">
			<view v-for="(item, index) in detailTableData" :key="index">
				<view style="padding-left: 10px;padding-right: 10px;padding-top: 10px; background-color: #ffffff;">
					<view>{{item.invCode}} {{item.invName}} {{item.invAttribute}}</view>
					<view style="color: #c3c3c3;">
						<table>
							<tr style="border-spacing: 0px;">
								<td width="12%">到货数量：</td><td width="25%">{{item.quantity}}</td>
								<td width="25%">{{item.invoiceDate}}</td>
							</tr>
						</table>
					</view>
					<u-line style="margin-top: 4px;" color="#c3c3c3" />
				</view>
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
  listCode, //到货单号查询
  materialSort,
} from "../../api/checkin";
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
        poCode: "", //单据编码
        invoiceStatus: "",
        workStatus: "", //单据状态
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
        poCode: "", //到货单号
        ppNumber: "", //销售单号
        startDate: "",
        endDate: "",
        workStatus: "",
        supplier: "",
        invoiceStatus: "",
        invName: "", //物料名称
        invCode: "", //物料编码
      },
      //批量删除 批量审核 批量打印
      batchStatus: true,
      // 单据总条数
      total: 0,
      //业务状态
      optionsworkStatus: [],
      //单据状态
      optionsinvoiceStatus: [],
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
      drawerMaterial: {}, //物料信息
      drawerTableLoading: true,
      drawerTableKey: "",
      drawerTableMaxHeight: null,
      materialStatus: false,
      // 明细遮罩层
      detailLoading: true,
      //单据参数信息
      parametersSetting: {
        decimalQuantity: "2", //数量小数位数
        decimalPrice: "2",
      },
      //明细查询参数
      queryDetail: {
        pageNum: 1,
        pageSize: 100,
        poCode: "", //到货单号
        ppNumber: "", //销售单号
        invName: "",
        invCode: "", //物料编码
        needType: "",
        supplier: "",
        invSortRoot: "",
        workType: '',
      },
      //供货方式
      optionsNeedType: [],
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
			this.$route.query.needType= option.needType;
		
			this.queryParams.needType= option.needType;
			this.queryParams.workType= option.workType;
			
			this.queryDetail.needType= option.needType;
			this.queryDetail.workType= option.workType;		
		}	
		
		this.getNeedType().then((res) => {
			this.getDetailList();
		});
	},
	methods: {
		search(){
			this.queryDetail.invName= keywords;
			this.getDetailList();
		},
		// 获取供货方式
		getNeedType() {
			return new Promise((resolve) => {
				dictMatching({
					dictType: "mp_need_type",
				}).then((res) => {
					if (res.code == 200) {
						this.optionsNeedType = res.rows;
						// this.queryDetail.needType = res.rows[0].dictValue;
						resolve();
					}
				});
			});
		},
    // 获取明细
    getDetailList() {
      return new Promise((resolve, reject) => {
        this.detailLoading = true;
        listDetail(this.queryDetail).then((res) => {
          if (res.code == 200) {
            this.detailLoading = false;
            this.detailTotal = res.total;
            this.tableKey2 = Math.random();
            this.getDecimal();
            this.detailMaxHeight = null;
            if (res.rows.length > 0) {
              let list = JSON.parse(JSON.stringify(res.rows));

              let last = list[0].poCode,
                tag = 0;
              list.forEach((item, i) => {
                if (list[i].poCode !== last) {
                  if (tag == 1) {
                    tag = 0;
                  } else if (tag == 0) {
                    tag = 1;
                  }
                }
                list[i].tag = tag;
                last = list[i].poCode;
              });
              this.detailTableData = list;
              this.getDecimal();
            } else {
              //this.$refs["tableDetailHeight"].doLayout();
              this.detailTableData = [];
            }

            // this.detailMaxHeight =
            //   `${document.documentElement.clientHeight}` - 270;
          }
        });
      });
    },
    //格式化数量单价金额小数位数
    getDecimal() {
      if (this.detailTableData.length > 0) {
        this.detailTableData.forEach((item) => {
          if (item.price !== null && item.price !== undefined) {
            item.price = parseFloat(item.price).toFixed(
              this.parametersSetting.decimalPrice
            );
          }
          if (item.quantity !== null && item.quantity !== undefined) {
            item.quantity = parseFloat(item.quantity).toFixed(2);
          }
          if (item.amount !== null && item.amount !== undefined) {
            item.amount = parseFloat(item.amount).toFixed(2);
          }
          if (item.wm_quantity !== null && item.wm_quantity !== undefined) {
            item.wm_quantity = parseFloat(item.wm_quantity).toFixed(2);
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
        });
      }
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
            resolve();
          }
        });
      });
    },
	}
}
</script>

<style>

</style>
