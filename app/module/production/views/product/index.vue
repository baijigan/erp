<template>
	<view class="wrap">
		<view class="container">
			<u-card title="生产订单" :sub-title="dateTimeFilter(fmData.invoiceDate)" :thumb="getCheckinImage(0)" :bodyStyle="{paddingTop:'0',paddingBottom:'0'}">
				<view slot="body" >
					<view class="u-body-item u-flex u-border-bottom u-col-between">
						<view class="u-body-item-title u-line-2">生产单号：</view>
						<view class="u-body-item-context u-line-2">{{fmData.woCode}}</view>
					</view>
					<view class="u-body-item u-flex u-border-bottom u-col-between ">
						<view class="u-body-item-title u-line-2">生产车间：</view>
						<view class="u-body-item-context u-line-2">{{workTypeFilter(fmData.workType)}}</view>
					</view>					
					<view class="u-body-item u-flex u-border-bottom u-col-between ">
						<view class="u-body-item-title u-line-2">生产线：</view>
						<view class="u-body-item-context u-line-2">{{fmData.beltline}}</view>
					</view>
					<view class="u-body-item u-flex u-border-bottom u-col-between">
						<view class="u-body-item-title u-line-2">工艺路线：</view>
						<view class="u-body-item-context u-line-2">{{fmData.process}}</view>
					</view>					
				</view>
			</u-card>
			
			<u-card title="物料信息"  :thumb="getCheckinImage(0)" :bodyStyle="{paddingTop:'0',paddingBottom:'0'}">
				<view slot="body" >
					<view class="u-body-item u-flex u-border-bottom u-col-between">
						<view class="u-body-item-title u-line-2">物料编码：</view>
						<view class="u-body-item-context u-line-2">{{fmData.invCode}}</view>
					</view>
					<view class="u-body-item u-flex u-border-bottom u-col-between ">
						<view class="u-body-item-title u-line-2">物料名称：</view>
						<view class="u-body-item-context u-line-2">{{fmData.invName}}</view>
					</view>
					<view class="u-body-item u-flex u-border-bottom u-col-between">
						<view class="u-body-item-title u-line-2">型号规格：</view>
						<view class="u-body-item-context u-line-2">{{fmData.invAttribute}}</view>
					</view>
					<view class="u-body-item u-flex u-border-bottom u-col-between">
						<view class="u-body-item-title u-line-2">质量属性：</view>
						<view class="u-body-item-context u-line-2">
							<uni-data-select v-model="fmData.qualitativeId" :localdata="range">
							</uni-data-select>
						</view>
					</view>
					<view class="u-body-item u-flex u-border-bottom u-col-between">
						<view class="u-body-item-title u-line-2">完工数量：</view>
						<view class="u-body-item-context u-line-2">
							<u-input v-model="fmData.invQuantity"/>
						</view>
					</view>
				</view>
			</u-card>
		</view>
		<view class="form-footer">
			<u-button class="btn" type="primary" @click="btnNew(fmData)">提交</u-button>
		</view>
	</view>
</template>

<script>
import {
  dictMatching, //系统字典
  addBill, //新增单据
  seeBill, //查看单据
  updateBill, //更新单据
  NextBill, //上一条下一条
  examineBill, //审核单据
  examineDeBill, //反审核单据
  moduleParameters,
  billParameters,
  operationBill, //操作
  listImportBill,
  listWarehouses,
  listImportDetail, //工艺路线明细
} from "../../api/product";
//并行请求
import axios from "axios";	
export default {
	data() {
		return {
			 range: [
			        { value: 0, text: "篮球" },
			        { value: 1, text: "足球" },
			        { value: 2, text: "游泳" },
			      ],
		      // 遮罩层
		      loading: false,
		
		      //初始化状态
		      initialStatus: false,
		
		      // 进入表单模式，有参、无参
		      entryMode: false,
		
		      //进入表单参数
		      entry: {
		        //记录编辑查看物料编码
		        seeCode: "",
		        //记录当前路由信息用于关闭当前页面
		        routeMsg: {},
		        // 记录fmConfig,用于是否刷新当前页面,未做
		        setConfig: "",
		        //记录supplyType,用于是否刷新当前页面
		        setSupplyType: "",
		        //记录时间戳,用于是否刷新当前页面
		        timeStamp: "",
		      },
		
		      //表单状态-addStatus:新增状态,seeStatus:查看状态,updateStatus:编辑状态
		      fmStatus: "",
		
		      //数据表单
		      fmData: {
		        poAttachmentList: [],
		        qualitativeId: '0',
		        qualitative: ''
		      },
		
		      //单据参数信息
		      fmConfig: {
		        decimalQuantity: "2", //数量小数位数
		        decimalPrice: "2", //数量小数位数
		        attrMerge: "false", //规格合并
		        attrAlias: "型号规格", //规格别名
		        sortArrId: [], //料品大类
		        industryCode: "", //物料特性
		        fieldHiding: [], //隐藏字段
		        warehouses: "", //仓库
		        industryCode: "",
		      },
		
		      // 查询
		      query: {
		        code: "",
		        sortRoot: "",
		        type: true,
		      },
		
		      //选项
		      option: {
		        //按钮是否禁用
		        btnIsClick: false,
		        //输入框是否禁用
		        disabledInput: false,
		        //业务类型是否可更改
		        wmworkStatus: false,
		        //上一条下一条状态
		        stripStatus: false,
		        // 物料信息状态
		        parametersStatus: false,
		        //单价状态
		        priceStatus: true,
		      },
		
		      //枚举
		      enums: {
		        //单据类型
		        optionsInvoiceType: [],
		        selectInvoiceType: "",
		        //业务类型
		        optionsworkType: [],
		        selectworkType: "",
		        //采购部门
		        optionsworkDept: [],
		        selectworkDept: [],
		        //业务状态
		        optionsworkStatus: [],
		        selectworkStatus: "",
		        //用料方式
		        optionsSupplyType: [],
		        // 密度单位
		        optionsDensityCode: [],
		        //产线
		        optionsbeltline: [],
		        //
		        optionsqualitative: [],
						optionsQuanlity: [{text:'222',value:'0'}],
		        //仓库
		        optionswarehouse: [],
		        selectwarehouse: "",
		        warehouseAll: "", //所有仓库
		      },
		
		      //执行中
		      stack: {
		        //操作下拉切换
		        commandIndex: "",
		        //点击选择物料记录下标，用于更新物料信息
		        rowIndex: "",
		        //物料选择弹框状态
		        selectDialog: true,
		        // 物料编码搜索下拉状态
		        saveIndex: null,
		        // 引入信息
		        optionsImportMaterial: [],
		        ImportMaterialStatus: false,
		        // 生产单号
		        prsCodeStatus: false,
		        //工人弹框
		        workerStatus: false,
		        //报工单状态
		        yieldBillStatus: false,
		        //刷新报工单组件
		        yieldBillKey: 0,
		      },
			  
			  
			//内存优化
			objMaterial: this.classMaterial(), //物料
			objHelper: this.classHelper(), //助手
			objUtils: this.classUtils(), //工具
			objFilling: this.classFilling(), //生产单号						
		}
	},
	onLoad(option) {
		if(this.$route === undefined){
			this.$route= {};
			this.$route.query= {};
			this.$route.query.open= option.open;
			this.$route.query.fmConfig= option.fmConfig;
			this.$route.query.timeStamp= option.timeStamp;
			this.$route.query.workType= option.workType;	
			this.$route.query.detailCode= option.detailCode;	
			this.$route.query.prsCode= option.prsCode;
		}

		this.initialStatus = true;
		this.Initial();
	},
	watch: {
		"fmData.workType": {
				handler(newVal, oldVal){
					if(oldVal == undefined && newVal >=0){
						this.importProductionProduct();
					}
				}
		}
	},
	methods: {
		Initial() {
		  // 按钮状态
		  this.fmStatus = this.$route.query.open;
		  this.entry.setConfig = this.$route.query.fmConfig;
		  this.entry.timeStamp = this.$route.query.timeStamp;

		  this.getDictionaryType().then(() => {
			this.objHelper.getParameters().then(() => {
			  if (this.fmStatus == "addStatus") {
				//新增状态重置表单
				this.objHelper.handleReset();
				this.objHelper.handleDefault();
			  };

			  if (this.fmStatus == "seeStatus" || this.fmStatus == "updateStatus") {
				this.entry.seeCode = this.$route.query.detailCode;
				seeBill({
				  prsCode: this.entry.seeCode,
				}).then((res) => {
				  if (res.code == 200) {
					this.fmData = res.data;                
					this.stack.yieldBillStatus = true;
					if (this.fmData.workStatus == "1") {
					  this.stack.commandIndex = "1";
					} else if (this.fmData.workStatus == "2") {
					  this.stack.commandIndex = "2";
					} else {
					  this.stack.commandIndex = "0";
					}
					this.stack.uploadList = this.fmData.poAttachmentList;
					this.objUtils.getDecimal();
					if (this.fmStatus == "seeStatus") {
					  this.option.disabledInput = true;
					  this.option.wmworkStatus = true; //编辑状态下业务类型
					} else {
					  this.option.disabledInput = false;
					  if (this.entryMode == true) {
						this.option.wmworkStatus = true;
					  } else {
						this.option.wmworkStatus = false;
					  }
					}
				  }
				});
			  }
			});
		  });

		  // 路由信息用于关闭页面
		  this.entry.routeMsg = this.$route;
		  if (
			this.$route.query.workType !== "" &&
			this.$route.query.fmConfig !== ""
		  ) {
			this.entryMode = true; //参数有值时
		  } else {
			this.entryMode = false; //参数无值时
		  }
		  //this.objMaterial.materialMaxHeight();
		},

		// 获取字典数据
		getDictionaryType() {
		  return new Promise((resolve) => {
			const _self = this;
			_self.loading = false;
			//并行请求
			axios
			  .all([
				// 单据类型
				dictMatching("prs_invoice_type"),
				// 业务类型
				dictMatching("prs_product_type"),
				// 业务状态
				dictMatching("sys_work_status"),
				// 质量属性
				dictMatching("prs_qualitative_type"),
				// 仓库
				listWarehouses(),
			  ])
			  .then(
				axios.spread(function (
				  resIvType,
				  resOrderType,
				  resWorkStatus,
				  resQualitativeType,
				  resWarehouses
				) {
				  if (
					resIvType.code == 200 &&
					resOrderType.code == 200 &&
					resQualitativeType.code == 200 &&
					resWorkStatus.code == 200 &&
					resWarehouses.code == 200
				  ) {
					// 单据类型
					_self.enums.optionsInvoiceType = resIvType.data;
					// 业务类型
					_self.enums.optionsworkType = resOrderType.data;
					//业务状态
					_self.enums.optionsworkStatus = resWorkStatus.data;
					//质量属性
					_self.enums.optionsqualitative = resQualitativeType.data;
					_self.fmData.qualitativeId= resQualitativeType.data[0].dictValue;
					_self.fmData.qualitative= resQualitativeType.data[0].dictLabel;
					_self.enums.optionsqualitative.forEach((item)=>{
							//_self.enums.optionsQuanlity.push({text:item.dictLabel, value:item.dictValue});
					});
					
					//仓库
					_self.enums.warehouseAll = resWarehouses.rows;
					//新增状态
					if (_self.fmStatus == "addStatus") {
					  _self.loading = false;
					  _self.fmData.invoiceDate = new Date();
					  _self.fmData.warehouseOper = _self.$store.getters.name;
					}

					// 查看/编辑数据
					if (_self.$route.query.workType == "") {
					  if (_self.fmStatus == "addStatus") {
						_self.fmData.workType =
						  _self.enums.optionsworkType[0].dictValue;
					  } else {
						_self.enums.selectworkType = _self.fmData.workType;
					  }
					} else {
					  _self.enums.selectworkType = _self.enums.optionsworkType[_self.$route.query.workType].dictValue;
					  _self.fmData.workType =_self.$route.query.workType;
					}
					resolve();
				  } else {
					_self.msgError("获取信息失败");
				  }
				})
			  );
		  });
		},
		importProductionProduct(){
			let queryParams= { };
			queryParams.pageNum= 1;
			queryParams.pageSize= 100;
			queryParams.prsCode= this.$route.query.prsCode;
			queryParams.workType= this.$route.query.workType;

			//已选单据
			let billSelected= [];
			//已选物料
			let materialSelected= [];
			listImportBill(queryParams).then((res) => {
				if (res.code == 200) {
					billSelected= res.rows;
					this.objFilling.handlePrsCodeSuccess(billSelected);					
				}
			});		
		},
		//确定新增
		btnNew(fmData) {
		  if (this.fmData.mpOrderCode == "") {
				this.$u.toast("请选择计划单号");
				return;
		  }

		  //this.$refs.fmData.validate((valid) => {
			if (true) {
			  fmData.invoiceDate = this.parseTime(fmData.invoiceDate);
			  if (this.$route.query.fmConfig !== undefined) {
					fmData.formConfig = this.$route.query.fmConfig;
			  }
			  addBill(fmData)
				.then((res) => {
				  //this.$u.toast("新增成功");
					
					let url= "/module/production/views/product/submit?workType=";
					url= url+ this.$route.query.workType;
					url= url+ "&invoiceCode=";
					url= url+ this.$route.query.prsCode;
					url= url+ "&title="+ this.workTypeFilter(fmData.workType);
					
					uni.redirectTo({
						url: url
					});
					
				  // 更新成功不刷新,重新赋值表单数据
				  // this.option.btnIsClick = true;
				  // this.fmStatus = "seeStatus";
				  // this.option.disabledInput = true;
				  // this.fmData = res.data;
				  // this.option.wmworkStatus = true;
				  // this.$store.dispatch("productionStatus/setProductRefresh", true);
				  // if (this.$route.query.fmReferer == "platform") {
						// this.$store.dispatch("productionStatus/setOrderRefresh", true);
						// this.$store.dispatch("productionStatus/setCommonRefresh", true);
				  // }
				  // this.objMaterial.materialMaxHeight();
				  // this.objUtils.getDecimal();
				})
				.catch((err) => {
					console.log(err);
				  this.$u.toast("新增失败");
				});
			} else {
			  this.$u.toast("error submit!!");
			  return false;
			}
		  //});
		},

    //复位物料模式（0引入 1新增）
    resetInvMode()
    {
      let _this= this;
      _this.stack.invMode= -1;    
      let item= _this.fmData.wmInSalveList;
      if(item==null)return;

      //fmItem.woUniqueId字段必须有赋值
      if(item.length>0 && (item[0].woUniqueId=='' || item[0].woUniqueId=='0')){
          _this.stack.invMode= 1;
      }
      else if(item.length>0){
          _this.stack.invMode= 0;
      }    
    },

    //验证物料模式是否同源（同为引入、或增加）
    validInvMode(){
      let salveList= this.fmData.wmInSalveList;      
      if(salveList==null)return true;

      let topUniqueId= '';
      for(var i=0; i<salveList.length; i++){
          if(i==0)topUniqueId= salveList[i].woUniqueId;
          else{
            if(topUniqueId=='' && salveList[i].woUniqueId!=''){
              return false;
            }
            else if(topUniqueId!='' && salveList[i].woUniqueId==''){
              return false;
            }
          }
      }

      return true;
    },				
	  warehouseFilter:function (value) {
			if (!value) return '';
			let name= '';
			
			this.enums.optionswarehouse.forEach((item)=>{
					if(item.code==value){
							name= item.name;
					}
			})
			
			return name;
		},
		
		dateTimeFilter(date) {
			let time = new Date(date);
			return (
				time.getFullYear() + "-" + (time.getMonth() + 1) + "-" + time.getDate()+" "+time.getHours()+ ":"+ time.getMinutes()
			);
		},
		
		workTypeFilter(type) {
			if (!type) return '';
			let name= '';

			this.enums.optionsworkType.forEach((item)=>{
					if(item.dictValue==type){
							name= item.dictLabel;
					}
			})
			
			return name;			
		},		
		
		workShopFilter(type) {
			if (!type) return '';
			let name= '';

			this.enums.optionProductWorkshop.forEach((item)=>{
					if(item.dictValue==type){
							name= item.dictLabel;
					}
			})
			
			return name;			
		},		
		
		getCheckinImage(workType) {
			// const length = this.imageList.length;
			// const i = this.getRandomInt(0, length - 1);
			return '/static/aidex/images/user01.png';
		},		
		// 生产订单
		classFilling() {
		  const self = this;
		  return {
			//生产单号/选择
			btnPrscode(index) {
			  self.stack.prsCodeStatus = true;
			},
			// 生产单号/选择成功
			handlePrsCodeSuccess(list) {
			  self.fmData.salesman = list[0].salesman;
			  self.fmData.ppNumber = list[0].pp_number;
			  self.fmData.ppDate = list[0].pp_date || null;
			  self.fmData.invSortRoot = list[0].inv_sort_root;
			  self.fmData.invSortId= list[0].inv_sort_id;
			  self.fmData.invCode = list[0].inv_code;
			  self.fmData.invName = list[0].inv_name;
			  self.fmData.unitName = list[0].unit_name;
			  self.fmData.unitCode = list[0].unit_code;
			  self.fmData.invAttribute = list[0].inv_attribute;
			  self.fmData.mpOrderCode = list[0].mp_order_code;
			  self.fmData.processId = list[0].process_id;
			  self.fmData.process = list[0].process;
			  self.fmData.invQuantity = list[0].surplus;
			  self.fmData.invSupplyType = list[0].inv_supply_type;
			  self.fmData.woQuantity = list[0].surplus;
			  self.fmData.woConfig = list[0].form_config;
			  self.fmData.woCode = list[0].prs_code;
			  self.fmData.woUniqueId = list[0].unique_id;
			  self.fmData.woInvoice = list[0].invoice_type;
			  self.fmData.woInvoiceId = list[0].invoice_type_id;
			  self.fmData.woType = list[0].work_type;
			  self.fmData.woTypeId = list[0].work_type_id;
			  self.fmData.woDate = list[0].invoice_date;
			  //加工产线
			  self.fmData.beltline = list[0].beltline;
			  self.fmData.beltlineId = list[0].beltline_id;

			  //可选仓库
			  self.enums.optionswarehouse= [];
			  self.enums.warehouseAll.forEach((obj) => {                  
				let arr= obj.sortId.split(",");
				arr.forEach((v)=>{
					if(v==self.fmData.invSortRoot){
					  self.enums.optionswarehouse.push(obj);
					}
				})                  
			  });

			  if(self.enums.optionswarehouse.length>0)
				self.fmData.warehouse= self.enums.optionswarehouse[0].code;

			  //禁止业务类型
			  self.option.wmworkStatus = false;
			},

			//关闭生产单号弹框
			selectPrsCodeClose() {
			  self.stack.prsCodeStatus = false;
			},

			//工人/选择
			btnWorker(index) {
			  self.stack.rowIndex = index;
			  self.stack.workerStatus = true;
			},

			//工人/选择成功
			handleWorkerSuccess(list) {
			  self.fmData.prsProductSalveList[self.stack.rowIndex].workersIds =
				list[0];
			  self.fmData.prsProductSalveList[self.stack.rowIndex].workersNames =
				list[1];
			  self.stack.workerStatus = false;
			},

			//工人选择关闭
			selectWorkerClose() {
			  self.stack.workerStatus = false;
			},

			//质量属性选择
			onSelectQualitative(value){
			  for(let i=0; i<self.enums.optionsqualitative.length; i++)
			  {
				  if(self.enums.optionsqualitative[i].dictValue==value){
					self.fmData.qualitativeId= self.enums.optionsqualitative[i].dictValue;
					self.fmData.qualitative= self.enums.optionsqualitative[i].dictLabel;     
					self.$forceUpdate();           
				  }
			  }
			}
		  };
		},
		//工艺路线信息
		classMaterial() {
		  const self = this;
		  return {
			// 工艺路线最大高度
			materialMaxHeight() {
			  if (`${document.documentElement.clientHeight}` - 460 < 400) {
				self.style.tableHeight = 500;
			  } else {
				if (self.fmStatus == "seeStatus") {
				  self.style.tableHeight =
					`${document.documentElement.clientHeight}` - 455;
				} else {
				  self.style.tableHeight =
					`${document.documentElement.clientHeight}` - 435;
				}
			  }
			},
		  };
		},
		//助手
		classHelper() {
		  const self = this;
		  return {
			//获取单据参数
			getParameters() {
			  return new Promise((resolve, reject) => {
				billParameters({
				  formPath: self.$route.query.fmConfig,
				}).then((res) => {
				  if (res.code == 200) {
					if (res.data["precision.quantity"] !== "null") {
					  self.fmConfig.decimalQuantity =
						res.data["precision.quantity"];
					}
					if (res.data["precision.price"] !== "null") {
					  self.fmConfig.decimalPrice = res.data["precision.price"];
					}
					if (res.data["attribute.merge"] !== "null") {
					  self.fmConfig.attrMerge = res.data["attribute.merge"];
					}

					if (res.data["attribute.alias"] !== "null") {
					  self.fmConfig.attrAlias = res.data["attribute.alias"];
					}
					self.enums.optionswarehouse = [];
					if (res.data["warehouse.scope"] !== "null") {
					  self.fmConfig.warehouses =
						res.data["warehouse.scope"].split(",");
					  self.enums.warehouseAll.forEach((item) => {
						self.fmConfig.warehouses.forEach((code) => {
						  if (item.code == code) {
							self.enums.optionswarehouse.push(item);
						  }
						});
					  });
					} else {
					  self.enums.optionswarehouse = self.enums.warehouseAll;
					}
					if (self.fmStatus == "addStatus") {
					  self.enums.selectwarehouse =
						self.enums.optionswarehouse[0].code;
					  self.fmData.warehouse = self.enums.optionswarehouse[0].code;
					}

					if (res.data["inv.scope"] !== "null") {
					  let arr = res.data["inv.scope"].split(",");
					  self.fmConfig.sortArrId = arr;
					}
					self.fmConfig.industryCode = self.$store.getters.industry;
					self.fmConfig.industryCode = self.$store.getters.industry;
					setTimeout(() => {
					  self.option.parametersStatus = true;
					}, 50);
					resolve();
				  }
				});
			  });
			},

			// 业务类型选择
			workTypeChange(value) {
			  self.fmData.workType = value;

			  // 复位已选择
			  self.fmData.woCode = "";
			  self.fmData.mpOrderCode = "";
			  self.fmData.ppNumber = "";
			  self.fmData.ppDate = "";
			  self.fmData.invSortId = "";
			  self.fmData.invSortRoot = "";
			  self.fmData.invCode = "";
			  self.fmData.invName = "";
			  self.fmData.unitName = "";
			  self.fmData.unitCode = "";
			  self.fmData.invAttribute = "";
			  self.fmData.prsOrderCode = "";
			  self.fmData.invQuantity = "";
			  self.fmData.invSupplyType = "";
			  self.fmData.warehouse= "";
			  self.fmData.remarks = "";
			  self.fmData.process = "";
			  self.fmData.beltline = "";                
			},

			//收起
			downToggle() {
			  self.stack.saveIndex = null;
			  if (self.style.downTitle == "收起") {
				self.style.downTitle = "展开";
			  } else {
				self.style.downTitle = "收起";
			  }
			  self.style.isToggle = !self.style.isToggle;
			  if (self.style.isToggle == false) {
				self.style.tableHeight = parseInt(self.style.tableHeight) + 170;
			  } else {
				self.style.tableHeight =
				  `${document.documentElement.clientHeight}` - 440;
			  }
			},

			//展开收缩
			btnExtend() {
			  self.style.extendStatus = !self.style.extendStatus;
			  if (self.style.extendStatus == false) {
				setTimeout(() => {
				  self.style.extendTitile = "收缩";
				}, 0);
			  } else {
				setTimeout(() => {
				  self.style.extendTitile = "展开";
				}, 400);
			  }
			},

			//点击新增/查看新增重置表单
			handleReset() {
			  self.fmData = {
				prsCode: "", //单据编码
				invoiceDate: new Date(), //单据日期
				invoiceStatus: "0", //单据状态
				invoiceType: "1", //单据类型
				workType: self.fmData.workType,
				workStatus: "0", //业务状态
				formConfig: "",
				userOper: self.$store.getters.name,
				workDept: "", //业务部门
				workStaff: "", //业务人员
				checkDate: "", //审核日期
				processId: "",
				process: "",
				invSortRoot: "",
				invSortId: "",
				invCode: "",
				invName: "",
				invAttribute: "",
				warehouse: "",
				unitName: "",
				unitCode: "",
				invQuantity: "",
				userCheck: "",
				beltline: "",
				beltlineId: "",
				ppNumber: "",
				ppDate: "",
				mpOrderCode: "",
				qualitativeId: "0",
				remarks: "", //备注
				woUniqueId: "",
				woCode: "",
				woConfig: "",
				woInvoice: "",
				woInvoiceId: "",
				woType: "",
				woTypeId: "",
				woQuantity: "",
				wiQuantity: "",
				prsProductSalveList: [],
			  };
			  self.stack.selectworkStaff = "";
			  /* 初始化状态 */
			  self.option.disabledInput = false;
			  self.workStatus = false;
			  self.option.btnIsClick = false;
			  self.option.wmworkStatus = false;
			  self.stack.yieldBillStatus = true;
			  if (self.enums.optionswarehouse.length > 0) {
				self.fmData.warehouse = self.enums.optionswarehouse[0].code;
			  }
			},

			handleDefault(){
			  //生产订单报工
			  if (
				self.$route.query.fmReferer !== undefined &&
				self.$route.query.fmReferer == "platform"
			  ) {
				let queryDetail = {
				  prsCode: self.$route.query.initCode,
				};
				listImportBill(queryDetail).then((res) => {
				  if (res.rows.length > 0) {
					self.fmData.mpOrderCode = res.rows[0].mp_order_code;
					self.fmData.salesman = res.rows[0].salesman;
					self.fmData.ppNumber = res.rows[0].pp_number;
					self.fmData.ppDate = res.rows[0].pp_date || null;
					self.fmData.invSortRoot = res.rows[0].inv_sort_root;
					self.fmData.invSortId= res.rows[0].inv_sort_id;
					self.fmData.invCode = res.rows[0].inv_code;
					self.fmData.invName = res.rows[0].inv_name;
					self.fmData.unitName = res.rows[0].unit_name;
					self.fmData.unitCode = res.rows[0].unit_code;
					self.fmData.invAttribute = res.rows[0].inv_attribute;
					self.fmData.invSupplyType = res.rows[0].inv_supply_type;
					self.fmData.mpOrderCode = res.rows[0].mp_order_code;
					self.fmData.invQuantity = res.rows[0].surplus;
					self.fmData.woConfig = res.rows[0].form_config;
					self.fmData.woCode = res.rows[0].prs_code;
					self.fmData.processId = res.rows[0].process_id;
					self.fmData.process = res.rows[0].process;
					self.fmData.woUniqueId = res.rows[0].unique_id;
					//加工产线
					self.fmData.beltline = res.rows[0].beltline;
					self.fmData.beltlineId = res.rows[0].beltline_id;
					self.objUtils.getDecimal();
					self.fmData.prsProductSalveList = [];

					//可选仓库
					self.enums.optionswarehouse= [];
					self.enums.warehouseAll.forEach((obj) => {                  
					  let arr= obj.sortId.split(",");
					  arr.forEach((v)=>{
						  if(v==self.fmData.invSortRoot){
							self.enums.optionswarehouse.push(obj);
						  }
					  })                  
					});

					if(self.enums.optionswarehouse.length>0)
					  self.fmData.warehouse= self.enums.optionswarehouse[0].code;
					
				  } else {
					self.msgError("生产订单"+queryDetail.prsCode+"已经报工完毕");
				  }
				});
			  }
			}
		  };
		},

		//工具
		classUtils() {
		  const self = this;
		  return {
			//格式化用户输入价格单价小数位数
			formatDecimal(index, data) {
			  self.stack.rowIndex = index;

			  if (
				self.fmData.prsProductSalveList[index].badQuantity !== "" &&
				isDecimal(self.fmData.prsProductSalveList[index].badQuantity)
			  ) {
				let badQuantity =
				  self.fmData.prsProductSalveList[index].badQuantity;
				self.fmData.prsProductSalveList[index].badQuantity = parseFloat(
				  badQuantity
				).toFixed(self.fmConfig.decimalQuantity);
			  }
			  if (
				self.fmData.prsProductSalveList[index].quantity !== "" &&
				isDecimal(self.fmData.prsProductSalveList[index].quantity)
			  ) {
				let quantity = self.fmData.prsProductSalveList[index].quantity;
				self.fmData.prsProductSalveList[index].quantity = parseFloat(
				  quantity
				).toFixed(self.fmConfig.decimalQuantity);
			  }
			},

			//格式化数量单价金额小数位数
			getDecimal() {
			  if (
				self.fmData.invQuantity !== null &&
				self.fmData.invQuantity !== undefined
			  ) {
				self.fmData.invQuantity = parseFloat(
				  self.fmData.invQuantity
				).toFixed(self.fmConfig.decimalQuantity);
			  }
			},

			//时间格式转换
			inMaterTime(date) {
			  let time = new Date(date);
			  return (
				time.getFullYear() +
				"-" +
				(time.getMonth() + 1) +
				"-" +
				time.getDate() +
				" " +
				time.getHours() +
				":" +
				time.getMinutes() +
				":" +
				time.getSeconds()
			  );
			},
		  };
		},		
	}
}
</script>


<style lang="scss">
/* #ifndef H5 */
/* page {
	padding-top: 85px;
} */
/* #endif */

page {
	background-color: #f5f5f5;
}

.u-card-wrap { 
	background-color: $u-bg-color;
	padding: 1px;
}

.u-body-item {
	font-size: 32rpx;
	color: #333;
	padding: 20rpx 10rpx;
}
	
.u-body-item image {
	width: 120rpx;
	flex: 0 0 120rpx;
	height: 120rpx;
	border-radius: 8rpx;
	margin-left: 12rpx;
}

.u-body-item-title {
	color: #959595;
	font-size: 30rpx;
}

.u-body-item-context {		
	font-size: 30rpx;
}

::v-deep{	
	.u-card__head--left__thumb{
		display: none !important;
	}
	
	.u-card__head--left__title{
		color: #2979ff !important;
		font-weight: 700;
		font-size: 17px !important;
		margin-left: 2px;
	}
}

</style>
