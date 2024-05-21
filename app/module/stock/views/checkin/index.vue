<template>
	<view class="wrap">
		<view class="container">
			<u-card :title="workTypeFilter(fmData.workType)" :sub-title="dateTimeFilter(fmData.invoiceDate)" :thumb="getCheckinImage(0)" :bodyStyle="{paddingTop:'0',paddingBottom:'0'}">
				<view slot="body" >
					<view class="u-body-item u-flex u-border-bottom u-col-between" v-if="fmData.workType == '0'">
						<view class="u-body-item-title u-line-2">采购单：</view>
						<view class="u-body-item-context u-line-2">{{fmData.poCheckinCode}}</view>
					</view>
					<view class="u-body-item u-flex u-border-bottom u-col-between " v-if="fmData.workType == '0'">
						<view class="u-body-item-title u-line-2">供应商：</view>
						<view class="u-body-item-context u-line-2">{{fmData.supplier}}</view>
					</view>
					<view class="u-body-item u-flex u-border-bottom u-col-between" v-if="fmData.workType == '1'">
						<view class="u-body-item-title u-line-2">生产单号：</view>
						<view class="u-body-item-context u-line-2">{{fmData.prsCheckinCode}}</view>
					</view>
					<view class="u-body-item u-flex u-border-bottom u-col-between " v-if="fmData.workType == '1'">
						<view class="u-body-item-title u-line-2">生产车间：</view>
						<view class="u-body-item-context u-line-2">{{workShopFilter(fmData.productionWorkshopId)}}</view>
					</view>
					<view class="u-body-item u-flex u-border-bottom u-col-between" v-if="fmData.workType == '2'">
						<view class="u-body-item-title u-line-2">到货单号：</view>
						<view class="u-body-item-context u-line-2">{{fmData.puCheckinCode}}</view>
					</view>
					<view class="u-body-item u-flex u-border-bottom u-col-between " v-if="fmData.workType == '2'">
						<view class="u-body-item-title u-line-2">外协厂家：</view>
						<view class="u-body-item-context u-line-2">{{fmData.outSource}}</view>
					</view>					
					<view class="u-body-item u-flex u-border-bottom u-col-between">
						<view class="u-body-item-title u-line-2">仓库：</view>
						<view class="u-body-item-context u-line-2">{{warehouseFilter(fmData.warehouse)}}</view>
					</view>
					<view class="u-body-item u-flex  u-col-between">
						<view class="u-body-item-title u-line-2">期间：</view>
						<view class="u-body-item-context u-line-2">{{fmData.period}}</view>
					</view>
				</view>
			</u-card>
			<view class="tbl">
				<uni-table style="width: 90%;margin: 0 auto;" ref="table" :loading="loading" border stripe emptyText="暂无更多数据" @selection-change="selectionChange">
					<uni-tr>
						<uni-th width="60" align="center" class="u-body-item-title">物料编码</uni-th>
						<uni-th width="160" align="center">物料名称</uni-th>
						<uni-th width="80" align="center">数量</uni-th>
					</uni-tr>
					<uni-tr v-for="(item, index) in fmData.wmInSalveList" :key="index">
						<uni-td>{{ item.invCode }}</uni-td>
						<uni-td>
							<view class="name">{{ item.invName }}</view>
						</uni-td>
						<uni-td align="center">{{ item.quantity }}</uni-td>
					</uni-tr>
				</uni-table>
			</view>
		</view>
		<view class="form-footer">
			<u-button class="btn" type="primary" @click="btnNew(fmData)">提交</u-button>
		</view>
	</view>
</template>

<script>
//并行请求
import axios from "axios"
import {
  dictMatching, 			//系统字典
  listDept, 					//部门列表
  listUser, 					//业务人员(用户)
  listWarehouses, 		//仓库列表
  checkWarehouses, 		//仓库信息查询
  examineBill, 				//审核单据
  examineDeBill, 			//反审核单据
  listMaterial, 			//物料信息
  listSupplier, 			//供方
  listEquipment, 			//加工设备
  listOutsource, 			//生产商（外协厂家）
  listCustomer, 			//客户
  addMaster,
  seeMaster,
  updateMaster,
  NextInMaster,
  blanceAvailable, 		//可结存期间
  operationBill,   		//操作
  moduleParameters,
	listPurchaseCheckinBill,
	listPurchaseCheckinDetail,
	listPrsCheckinBill,
	listPrsCheckinDetail,
	listPuCheckinBill,
	listPuCheckinDetail,
} from "../../api/checkin";
//js方法
import { getTreeData, getEcho } from "../../utils/stock";
import tableData from './tableData.js'
export default {
	data() {
		return {
      // 遮罩层
      loading: true,

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
        // 记录fmConfig
        setConfig: "",
        //仓库代码
        whCode: "",
        // 记录蓝字红字
        recordBredVouch: "",
      },

      //表单状态-addStatus:新增状态,seeStatus:查看状态,updateStatus:编辑状态
      fmStatus: "",

      //数据表单
      fmData: {},

      //添加物料
      fmItem: {
        invSortId: "", //物料分类
        invCode: "", //物料编码
        invName: "", //物料名称
        invAttribute: "", //型号规格
        unitName: "", //主计量
        unitCode: "", //主计量
        quantity: "", //数量
        price: "0.00", //单价
        amount: "0.00", //金额
        batchNumber: "", //批号
        ppNumber: "",
        location: "", //货位
        remarks: "", //备注
        woUniqueId: "0",
      },

      //单据参数
      fmConfig: {
        //数量小数位数
        decimalQuantity: "2",
        //单价小数位数
        decimalPrice: "2",
        //规格合并
        attrMerge: "false",
        //规格别名
        attrAlias: "型号规格",
        //隐藏字段
        fieldHiding: [],
        //特殊属性
        property: [],
      },
						
			searchVal: '',
			tableData: [],
			// 每页数据量
			pageSize: 10,
			// 当前页
			pageCurrent: 1,
			// 数据总量
			total: 0,
			loading: false,
						
      //枚举
      enums: {
        //单据类型
        optionsInvoiceType: [],
        //业务类型
        optionsworkType: [],
        selectworkType: "",
        workTypeInit: [],
        //业务部门
        optionsworkDept: [],
        selectworkDept: [],
        //业务状态
        optionsworkStatus: [],
        //客户
        optionscustomer: [],
        //供方
        optionssupplier: [],
        //外协厂家
        optionsoutsource: [],
        //加工车间
        optionProductWorkshop: [],
        selectProductWorkshop: "",
        //加工设备
        optionsproductionLine: [],
        //结存期间
        optionsperiod: [],
        selectperiod: "",
        //仓库
        optionswarehouse: [],
        selectwarehouse: "",
        //业务人员
        optionsworkStaff: [],
        selectworkStaff: "",
      },		
				
      //执行中
      stack: {
        //操作下拉切换
        commandIndex: "",
        //是否为搜索客户
        omStatus: false,
        //客户搜索下拉状态
        omSearchStatus: false,
        //点击选择物料记录下标，用于更新物料信息
        rowIndex: "",
        //物料选择弹框状态
        selectDialog: false,
        // 物料编码搜索下拉状态
        saveIndex: null,
        // 物料编码搜索数据
        basicfileList: [],
        // 引入信息
        optionsImportMaterial: [],
        ImportMaterialStatus: false,
        //供方
        poStatus: false,
        poSearchStatus: false,
        //外协厂家
        puStatus: false,
        puSearchStatus: false,
        workTypeStatus: false,
        //加工设备
        prsStatus: false,
        deptId: "",
        //货位显示状态 是否启用货位
        locationStatus: true,
        // 货位信息
        optionslocation: [],
        isLocation: false, //货位组件状态
        whName: "", //仓库名称
        //采购入库引入
        purchaseCheckinStatus: false,
        //生产入库引入
        prsCheckinStatus: false,
        //委外入库引入
        puCheckinStatus: false,
        //生产退料引入
        prsUnPickStatus: false,
        //委外退料引入
        puUnPickStatus: false,
        //销售退货单引入
        omRejectStatus: false,
        // 供方输入状态
        supplierStatus: false,
        //料品大类
        sortArrId: [],
        //引入组件传值 仓库code/料品code/物料大类特殊属性
        ImportMaterialCode: {
          workType: "",
          warehouse: "",
        },

        //表单物料操作方式（0 引入 1 新增）
        invMode: -1
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
				
			objWarehouse: this.classWarehouse(), //仓库
			objLeadin: this.classLeadin(), //引入
			objUtils: this.classUtils(), //工具
			objHelper: this.classHelper(), //助手			
		}
	},
	onLoad(option) {
		if(this.$route === undefined){
			this.$route= {};
			this.$route.query= {};
			this.$route.query.open= option.open;
			this.$route.query.fmConfig= option.fmConfig;
			this.$route.query.invoiceType= option.invoiceType;
			this.$route.query.detailCode= option.detailCode;
			this.$route.query.whCode= option.whCode;
			this.$route.query.bredVouch= option.bredVouch;
			this.$route.query.workType= option.workType;
			this.$route.query.poCode= option.poCode;
			this.$route.query.prsCode= option.prsCode;
			this.$route.query.puCode= option.puCode;
		}	
				
		this.initialStatus = true;
		this.Initial();				
	},
	watch: {
		"stack.workTypeStatus": {
				handler(newVal, oldVal){
					if(oldVal == false && newVal == true){
						if(this.fmData.workType=='0')this.importPurchaseCheckin();
						else if(this.fmData.workType=='1')this.importPrsCheckin();
						else if(this.fmData.workType=='2')this.importPuCheckin();
						console.log(this.fmData);
					}
				}
		}
	},
	methods: {
    // 初始化页面
    Initial() {
      // 按钮状态
      this.fmStatus = this.$route.query.open;
      this.entry.setConfig = this.$route.query.fmConfig;
      if (this.fmStatus == "addStatus") {
        //新增状态重置表单
        this.objHelper.handleReset();
        this.fmData.invoiceType= this.$route.query.invoiceType;
        //判断业务类型
      } else if (
        this.fmStatus == "seeStatus" ||
        this.fmStatus == "updateStatus"
      ) {
        this.entry.seeCode = this.$route.query.detailCode;
        seeMaster({
          wmCode: this.entry.seeCode,
        }).then((res) => {
          if (res.code == 200) {
            this.fmData = res.data;
            this.resetInvMode();

            this.enums.selectProductWorkshop= {label: this.fmData.productionWorkshop, value:this.fmData.productionWorkshopId};
            console.log(this.enums.selectProductWorkshop);
            
            this.fmData.bredVouch = this.fmData.bredVouch.toString();
            if (this.fmData.workStatus == "1") {
              this.stack.commandIndex = "1";
            } else if (this.fmData.workStatus == "2") {
              this.stack.commandIndex = "2";
            } else {
              this.stack.commandIndex = "0";
            }
            if (
              this.fmData.workDept !== undefined &&
              this.fmData.workDept !== null &&
              this.fmData.workDept !== ""
            ) {
              listUser({
                deptId: this.fmData.workDept,
              }).then((res) => {
                this.enums.optionsworkStaff = res.rows;
              });
            }
            this.objUtils.getDecimal();
            if (this.fmStatus == "seeStatus") {
              this.option.disabledInput = true;
              this.option.wmworkStatus = true; //编辑状态下业务类型与仓库是否可选
            } else {
              this.option.disabledInput = false;
              if (this.entryMode == true) {
                this.option.wmworkStatus = true;
              } else {
                this.option.wmworkStatus = false;
              }
            }

            if (this.entryMode == false) {
              if (this.fmData.bredVouch == "0") {
                this.objHelper.getWorkType("wm_in_type").then(() => {
                  //初始化单据与业务信息
                  this.$nextTick(() => {
                    this.getDictionaryType();
                  });
                });
              } else if (this.fmData.bredVouch == "1") {
                this.objHelper.getWorkType("wm_reject_in").then(() => {
                  //初始化单据与业务信息
                  this.$nextTick(() => {
                    this.getDictionaryType();
                  });
                });
              } else {
                this.objHelper.getWorkType("wm_adjust_in").then(() => {
                  //初始化单据与业务信息
                  this.$nextTick(() => {
                    this.getDictionaryType();
                  });
                });
              }
            }
          }
        });
      }
      // 路由信息用于关闭页面
      this.entry.routeMsg = this.$route;
      // 进入表单模式，有参、无参
      if (
        this.$route.query.whCode !== "" &&
        this.$route.query.bredVouch !== "" &&
        this.$route.query.workType !== ""
      ) {
        this.entryMode = true; //参数有值时
        // 字典对应业务类型
        if (this.$route.query.bredVouch == 0) {
          this.objHelper.getWorkType("wm_in_type").then(() => {
            //初始化单据与业务信息
            this.$nextTick(() => {
              this.getDictionaryType();
            });
          });
        } else if (this.$route.query.bredVouch == 1) {
          this.objHelper.getWorkType("wm_reject_in").then(() => {
            //初始化单据与业务信息
            this.$nextTick(() => {
              this.getDictionaryType();
            });
          });
        } else {
          this.objHelper.getWorkType("wm_adjust_in").then(() => {
            //初始化单据与业务信息
            this.$nextTick(() => {
              this.getDictionaryType();
            });
          });
        }
      } else {
        this.entryMode = false; //参数无值时
        if (this.fmStatus == "addStatus") {
          this.objHelper.getWorkType("wm_in_type").then(() => {
            //初始化单据与业务信息
            this.$nextTick(() => {
              this.getDictionaryType();
            });
          });
        }
      }

      //this.objMaterial.materialMaxHeight();
    },
    // 获取字典数据
    getDictionaryType() {
      const _self = this;
      _self.loading = true;
      //并行请求
      axios
        .all([
          dictMatching("wm_invoice_type"),
          dictMatching("sys_work_status"),
          listDept(),
          listWarehouses({
            bredVouch: this.$route.query.bredVouch,
            invoiceType: this.$route.query.invoiceType,
            workType: this.$route.query.workType,
          }),
          dictMatching("prs_order_type")
        ])
        .then(
          axios.spread(function (resIvType, resWorkStatus, resDept, resHouses, prsTypeRes) {
            if (
              resIvType.code == 200 &&
              resWorkStatus.code == 200 &&
              resDept.code == 200 &&              
              resHouses.code == 200 &&
              prsTypeRes.code == 200
            ) {
              // 车间定义
              _self.enums.optionProductWorkshop = prsTypeRes.data;
              // 单据类型
              _self.enums.optionsInvoiceType = resIvType.data;
              // 业务部门
              let arr = _self.handleTree(resDept.data, "deptId");
              _self.enums.optionsworkDept = getTreeData(arr);
              //仓库
              _self.enums.optionswarehouse = resHouses.rows;
              //业务状态
              _self.enums.optionsworkStatus = resWorkStatus.data;
              //新增状态下成功隐藏加载框
              if (_self.fmStatus == "addStatus") {
                if (_self.$route.query.whCode !== "") {
                  _self.objWarehouse.warehouseChange(_self.$route.query.whCode);
                }
                _self.loading = false;
                _self.fmData.invoiceDate = new Date();
                _self.fmData.userOper = _self.$store.getters.name;
              }

              // 查看/编辑数据
              if (
                (_self.fmStatus == "seeStatus" && _self.fmData !== null) ||
                _self.fmStatus == "updateStatus"
              ) {
                //业务类型
                _self.enums.selectworkType = _self.fmData.workType;
                _self.$refs.fmData.clearValidate();
                //仓库
                _self.enums.selectwarehouse = _self.fmData.warehouse;
                if (_self.fmData.workDept !== "") {
                  // 部门数据回显
                  _self.objHelper.undataAddSee(_self.fmData.workDept);
                }
                // 业务人员
                _self.enums.selectworkStaff = _self.fmData.workStaff;

                //记录蓝红/黄字
                _self.entry.recordBredVouch = _self.fmData.bredVouch;
                //匹配物料特殊属性物料信息导航名称/小数位数
                _self.objWarehouse.warehouseChange(_self.fmData.warehouse);
                _self.stack.workTypeStatus = true;							
              }
            } else {
              _self.msgError("获取信息失败");
            }
          })
        );
    },
		importPurchaseCheckin(){
			let queryParams= { };
			queryParams.pageNum= 1;
			queryParams.pageSize= 100;
			queryParams.poCode= this.$route.query.poCode;
			queryParams.supplier= '';
			queryParams.warehouse= this.$route.query.whCode;

			//已选单据
			let billSelected= [];
			//已选物料
			let materialSelected= [];
			listPurchaseCheckinBill(queryParams).then((res) => {
				if (res.code == 200) {
					billSelected= res.rows;					
					listPurchaseCheckinDetail(queryParams).then((res) => {
						if (res.code == 200) {
							materialSelected = [...res.data];
							let list= [billSelected, materialSelected];							
							this.objLeadin.handlePurchaseCheckinSuccess(list);
						}
					});
				}
			});		
		},
		importPrsCheckin(){
			let queryParams= { };
			queryParams.pageNum= 1;
			queryParams.pageSize= 100;
			queryParams.prsCode= this.$route.query.prsCode;
			queryParams.supplier= '';
			queryParams.warehouse= this.$route.query.whCode;

			//已选单据
			let billSelected= [];
			//已选物料
			let materialSelected= [];
			listPrsCheckinBill(queryParams).then((res) => {
				if (res.code == 200) {
					billSelected= res.rows;					
					listPrsCheckinDetail(queryParams).then((res) => {
						if (res.code == 200) {
							materialSelected = [...res.data];
							let list= [billSelected, materialSelected];							
							this.objLeadin.handlePrsCheckinSuccess(list);
						}
					});
				}
			});		
		},	
		importPuCheckin(){
			let queryParams= { };
			queryParams.pageNum= 1;
			queryParams.pageSize= 100;
			queryParams.puCode= this.$route.query.puCode;
			queryParams.supplier= '';
			queryParams.warehouse= this.$route.query.whCode;

			//已选单据
			let billSelected= [];
			//已选物料
			let materialSelected= [];
			listPuCheckinBill(queryParams).then((res) => {
				if (res.code == 200) {
					billSelected= res.rows;					
					listPuCheckinDetail(queryParams).then((res) => {
						if (res.code == 200) {
							materialSelected = [...res.data];
							let list= [billSelected, materialSelected];							
							this.objLeadin.handlePuCheckinSuccess(list);
						}
					});
				}
			});
		},			
    //新增
    btnNew(fmData) {
      if (this.fmData.wmInSalveList.length == 0) {
		  this.$u.toast('物料信息为空');
      } else {
        if(this.validInvMode()==false){
			this.$u.toast('物料明细必须全部引用或者全部新增');
          return;
        }

        //this.$refs.fmData.validate((valid) => {
          if (true) {
            fmData.invoiceDate = this.parseTime(fmData.invoiceDate);
            fmData.formConfig = this.entry.setConfig;
            addMaster(fmData)
              .then((res) => {
				  this.$u.toast('新增成功');
				  let url= "/module/stock/views/checkin/submit?workType=";
				  url= url+ this.$route.query.workType;
				  url= url+ "&invoiceCode=";
				  if(this.$route.query.workType=='0'){
					url= url+ this.$route.query.poCode;
					url= url+ "&title="+ this.fmData.supplier;
				  }
				  else if(this.$route.query.workType=='1'){
					url= url+ this.$route.query.prsCode;
					url= url+ "&title="+ this.workShopFilter(fmData.productionWorkshopId);
				  }
				  else if(this.$route.query.workType=='2'){
					url= url+ this.$route.query.puCode;
					url= url+ "&title="+ this.fmData.outSource;  
				  }
				  uni.redirectTo({
				  	url: url
				  });
				  
                // 更新成功不刷新,重新赋值表单数据
                // this.option.btnIsClick = true;
                // this.$store.dispatch("stockStatus/setCheckinRefresh", true);
                // this.fmStatus = "seeStatus";
                // this.option.disabledInput = true;
                // this.fmData = res.data;
                // this.fmData.bredVouch = this.fmData.bredVouch.toString();
                // this.option.wmworkStatus = true;
                // this.objMaterial.materialMaxHeight();
                // this.objUtils.getDecimal();
              })
              .catch((err) => {
                this.$u.toast("新增失败");
              });
          } else {
            this.$u.toast("error submit!!");
            return false;
          }
        //});
      }
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
		// 多选处理
		selectedItems() {
			return this.selectedIndexs.map(i => this.tableData[i])
		},
		// 多选
		selectionChange(e) {
			console.log(e.detail.index)
			this.selectedIndexs = e.detail.index
		},
		//批量删除
		delTable() {
			console.log(this.selectedItems())
		},
		// 分页触发
		change(e) {
			this.$refs.table.clearSelection()
			this.selectedIndexs.length = 0
			this.getData(e.current)
		},
		// 搜索
		search() {
			this.getData(1, this.searchVal)
		},
		// 获取数据
		getData(pageCurrent, value = '') {
			this.loading = true
			this.pageCurrent = pageCurrent
			this.request({
				pageSize: this.pageSize,
				pageCurrent: pageCurrent,
				value: value,
				success: res => {
					// console.log('data', res);
					this.tableData = res.data
					this.total = res.total
					this.loading = false
				}
			})
		},
    //仓库
    classWarehouse() {
      const self = this;
      return {
        //仓库选择 */
        warehouseChange(value) {
          //新增状态下重新选择仓库时清空物料
          if (self.fmStatus == "addStatus") {
            self.objHelper.resetInput();
          }
          //仓库回显名称
          self.enums.selectwarehouse = value;
          self.fmData.warehouse = value;
          this.WarehouseCheck(value).then((res) => {
            // if (self.selectPrint == "success") {
            //   self.objPrinter.handlePrint();
            // }
          });
          //获取仓库分类信息
          let wmObj = {};
          wmObj = self.enums.optionswarehouse.find((item) => {
            return item.code == value;
          });
          let wmArr = wmObj.sortId.split(",");
          // 隐藏物料编码搜索
          // self.stack.saveIndex = null;
          // self.stack.sortArrId = wmArr;

          //获取可用期间
          blanceAvailable({ whCode: value }).then((res) => {
            let periodList = JSON.parse(JSON.stringify(res.data));
            if (periodList.length > 0) {
              periodList.forEach((item) => {
                item.period = self.objUtils.periodDirStr(item.period);
              });
              self.enums.optionsperiod = periodList;
            } else {
              self.enums.optionsperiod = [];
            }
            if (self.fmStatus == "addStatus") {
              self.enums.selectperiod =
                self.enums.optionsperiod[
                  self.enums.optionsperiod.length - 1
                ].period;
              self.fmData.period = self.objUtils.periodDirInt(
                self.enums.selectperiod
              );
            } else {
              self.enums.selectperiod = self.objUtils.periodDirStr(
                self.fmData.period
              );
            }
          });
          self.fmData.warehouse = value;
        },
        WarehouseCheck(d) {
          return new Promise((resolve, reject) => {
            // 仓库配置
            let houseMsg = null;
            self.enums.optionswarehouse.forEach((item) => {
              if (item.code == d) {
                houseMsg = item;
              }
            });
            if (houseMsg !== null) {
              if (houseMsg.configMap !== null) {
                if (houseMsg.configMap["precision.quantity"] !== "null") {
                  self.fmConfig.decimalQuantity =
                    houseMsg.configMap["precision.quantity"];
                }

                if (houseMsg.configMap["precision.price"] !== "null") {
                  self.fmConfig.decimalPrice =
                    houseMsg.configMap["precision.price"];
                }
                // 合并规格
                if (houseMsg.configMap["attribute.merge"] !== "null") {
                  self.fmConfig.attrMerge =
                    houseMsg.configMap["attribute.merge"];
                }
                //规格别名
                if (houseMsg.configMap["attribute.alias"] !== "null") {
                  self.fmConfig.attrAlias =
                    houseMsg.configMap["attribute.alias"];
                }

                //特殊属性
                if (houseMsg.configMap["inv.property"] !== "null") {
                  self.fmConfig.property =
                    houseMsg.configMap["inv.property"].split(",");
                }
              } else {
                self.objHelper.resetFmConfig();
              }
              self.entry.setConfig = houseMsg.formConfig;

              //仓库名称
              self.stack.whName = houseMsg.name;
              if (houseMsg.enableLocation == 1) {
                self.stack.locationStatus = true;
              } else {
                self.stack.locationStatus = false;
              }
            }
            if (
              self.fmStatus == "seeStatus" ||
              self.fmStatus == "updateStatus"
            ) {
              self.fmData.wmInSalveList.forEach((item) => {
                if (item.price !== null) {
                  item.price = parseFloat(item.price).toFixed(
                    self.fmConfig.decimalPrice
                  );
                }
                if (item.quantity !== null) {
                  item.quantity = parseFloat(item.quantity).toFixed(
                    self.fmConfig.decimalQuantity
                  );
                }
                if (item.amount !== null) {
                  item.amount = parseFloat(item.amount).toFixed(2);
                }
              });
            }

            // 业务类型选择根据仓库类型回显
            if (
              (self.entryMode == false && self.fmData.bredVouch == "0") ||
              (self.entryMode == false && self.fmData.bredVouch == "1")
            ) {
              self.enums.optionsworkType = [];
              let type = houseMsg.inType.split(","); 
							console.log(self.enums.workTypeInit);
              type.forEach((id) => {
                let objType = self.enums.workTypeInit.find((item) => {
                  return item.dictValue == id;
                });
                self.enums.optionsworkType.push(objType);
              });
            }
            if (self.fmStatus == "addStatus") {
              self.fmData.wmInSalveList = [];
              self.stack.workTypeStatus = true;
              if (self.entryMode == false) {
                self.enums.selectworkType =
                  self.enums.optionsworkType[0].dictValue;
                self.fmData.workType = self.enums.optionsworkType[0].dictValue;
              }
              if (self.fmData.bredVouch !== "1") {
                //self.fmData.wmInSalveList.push({
                //  ...self.fmItem,
                //});
              }
            }

            self.loading = false;
            // 延迟显示物料信息优化体验
            setTimeout(() => {
              //self.$refs["tableClums"].doLayout();
              self.option.parametersStatus = true;
            }, 50);

            resolve();
          });
        },
      };
    },		
    //助手
    classHelper() {
      const self = this;
      return {
        //  业务类型选择
        workTypeChange(value) {
          self.fmData.workType = value;
          self.stack.workTypeStatus = true;
          self.stack.prsStatus = false;
          self.stack.puSearchStatus = false;
          self.stack.poSearchStatus = false;
          self.stack.omSearchStatus = false;
          //清空采购/委外已填写订单号等信息,防止页面缓存
          self.fmData.customer = "";
          self.fmData.customerId = "";
          self.fmData.supplier = "";
          self.fmData.supplierId = "";
          self.fmData.productionLine = "";
          self.fmData.productionLineId = "";
          self.fmData.outSource = "";
          self.fmData.outSourceId = "";
        },

        // 业务部门选择
        workDeptChange(value) {
          let deptId = JSON.parse(JSON.stringify(value)); //防止干扰级联选择器
          self.fmData.workDept = deptId.pop();
          self.enums.selectworkStaff = "";
          self.fmData.workStaff = "";
          listUser({
            deptId: self.fmData.workDept,
          }).then((res) => {
            self.enums.optionsworkStaff = res.rows;
          });
        },

        // 业务人员选择
        workStaffChange(value) {
          self.fmData.workStaff = self.enums.selectworkStaff;
        },

        //期间
        periodChange(value) {
          self.fmData.period = self.objUtils.periodDirInt(value);
        },

        //字典类型
        getWorkType(type) {
          return new Promise((resolve, reject) => {
            dictMatching(type).then((res) => {
              self.enums.optionsworkType = [];
              self.enums.optionsworkType = res.data;
              // console.log(self.enums.optionsworkType);
              self.enums.workTypeInit = res.data;
              if (self.$route.query.bredVouch == "") {
                if (self.fmStatus == "addStatus") {
                  self.enums.selectworkType =
                    self.enums.optionsworkType[0].dictValue;
                  self.fmData.workType =
                    self.enums.optionsworkType[0].dictValue;
                }
              } else {
                self.enums.selectworkType =
                  self.enums.optionsworkType[
                    self.$route.query.workType
                  ].dictValue;
                self.fmData.workType =
                  self.enums.optionsworkType[
                    self.$route.query.workType
                  ].dictValue;
              }
              resolve(res);
            });
          });
        },

        //点击新增/查看新增重置表单
        handleReset() {
          //数据表单
          self.fmData = {
            invoiceDate: new Date(), //单据日期
            invoiceStatus: "0", //单据状态
            invoiceType: "0", //单据类型
            workStatus: "0", //业务状态
            bredVouch:
              self.entry.recordBredVouch || self.$route.query.bredVouch || "0", //红字蓝字
            workType: "", //业务类型
            warehouse: "", //仓库
            userOper: self.$store.getters.name,
            workDept: "", //业务部门
            workStaff: "", //业务人员
            period: "", //期间
            remarks: "", //备注
            supplier: "", //供方
            supplierId: "", //供方
            outsource: "", //外协厂家
            outsourceId: "", //外协厂家
            productionLine: "",
            productionLineId: "",
            customer: "", //销售客户
            customerId: "", //销售客户
            poCheckinCode: "", //采购到货单号
            puCheckinCode: "", //委外到货单号
            prsCheckinCode: "", //生产到货单号
            omRejectCode: "", //销售退货单号
            productionWorkshop: "", //加工车间
            productionWorkshopId: "", //加工车间id
            prsRejectCode: "", //生产退料单编码
            puRejectCode: "", //委外退料单编码
            wmInSalveList: [],
          };

          self.enums.selectworkStaff = "";
          if(self.$route.query.workType!==""){
            self.fmData.workType = self.$route.query.workType;
          }else{
            self.fmData.workType = "0"
          }
          /* 初始化红蓝黄字 */
          let bredVouch;
          if (self.$route.query.bredVouch) {
            bredVouch = self.$route.query.bredVouch.toString();
            self.fmData.bredVouch = bredVouch;
          } else {
            self.fmData.bredVouch = "0";
          }

          /* 初始化状态 */
          self.option.disabledInput = false;
          self.option.wmworkStatus = false;
          self.option.btnIsClick = false;
          self.enums.selectworkDept = "";
          self.workStaff = "";
        },

        //部门数据回显
        undataAddSee(id) {
          return new Promise((resolve, reject) => {
            let parentArr = [], //用于储存父类部门信息
              childrenArr = []; //用于储存子类部门信息
            getEcho(
              id,
              self.enums.optionsworkDept,
              parentArr,
              childrenArr
            ).then((res) => {
              setTimeout(() => {
                let success = [];
                for (let i = 0; i < res.length; i++) {
                  if (success.indexOf(res[i]) === -1) {
                    success.push(res[i]);
                  }
                }
                let getNode = JSON.parse(JSON.stringify(success)); //拷贝vuex信息，避免影响源数据
                getNode.forEach((v) => {
                  if (v.ancestors.indexOf(",") > -1) {
                    //是否是子部门
                    v.ancestors = v.ancestors.split(",").length; //拼接数组长度，用于排序
                  } else {
                    v.ancestors = 1;
                  }
                });
                getNode.sort((a, b) => {
                  //排序得到正常的部门顺序
                  return a.ancestors - b.ancestors;
                });
                self.enums.selectworkDept = [];
                self.print.printWorkDept = [];
                getNode.forEach((code) => {
                  self.enums.selectworkDept.push(code.deptId);
                  self.print.printWorkDept.push(code.deptName);
                });
              }, 0);
            });
            resolve();
          });
        },

        //重置物料
        resetInput() {
          if (self.fmData.wmInSalveList.length > 0) {
            self.fmData.wmInSalveList.forEach((res) => {
              Object.keys(res).forEach(function (key) {
                key, (res[key] = "");
              });
            });
          }
        },

        //重置表单参数
        resetFmConfig() {
          self.fmConfig = {
            //数量小数位数
            decimalQuantity: "2",
            //单价小数位数
            decimalPrice: "2",
            //规格合并
            attrMerge: "false",
            //规格别名
            attrAlias: "型号规格",
            //隐藏字段
            fieldHiding: [],
            //特殊属性
            property: [],
          };
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
      };
    },		
    //引入
    classLeadin() {
      const self = this;
      return {
        //采购入库引入
        btnPurchaseCheckin() {
          self.stack.invMode= 0;
          self.stack.purchaseCheckinStatus = true;
        },
        purchaseCheckinImport(list) {
          return new Promise((resolve, reject) => {
            let materialDate = {
              invSortId: "", //物料分类
              invCode: "", //物料编码
              invName: "", //物料名称
              invAttribute: "", //型号规格
              unitCode: "", //主计量
              unitName: "", //主计量
              batchNumber: "", //批号
              ppNumber: "", //销售订单号
              quantity: "", //数量
              price: "", //单价
              amount: 0.0, //金额
              f1: "",
              f2: "",
              f3: "",
              f4: "",
              f5: "",
              location:"",
              woInvoice: "",
              woInvoiceId: "",
              woType: "",
              woTypeId: "",
              woCode: "",
              woConfig: "",
              woDate: null,
              woUniqueId: "",
              woQuantity: "",
              remarks: "", //备注
            };
            let successList = JSON.parse(JSON.stringify(list)),
              formMaterial = []; //赋值物料信息
            successList.forEach((item, i) => {
              formMaterial.push({
                ...materialDate,
              });
            });

            resolve(formMaterial);
          });
        },

        // 选择采购入库引入物料成功
        handlePurchaseCheckinSuccess(list) {
          self.fmData.wmInSalveList = [];
          this.purchaseCheckinImport(list[1]).then((arr) => {
            let successList = JSON.parse(JSON.stringify(list[1]));
            if (successList.length > 0) {
              for (let i = 0; i < successList.length; i++) {
                arr[i].invSortRoot = successList[i].inv_sort_root;
                arr[i].invSortId = successList[i].inv_sort_id;
                arr[i].invCode = successList[i].inv_code;
                arr[i].invName = successList[i].inv_name;
                arr[i].unitName = successList[i].unit_name;
                arr[i].unitCode = successList[i].unit_code;
                arr[i].batchNumber = successList[i].batch_number;
                arr[i].ppNumber = successList[i].pp_number;
                arr[i].invAttribute = successList[i].inv_attribute;
                arr[i].quantity = successList[i].surplus;
                arr[i].price = successList[i].price || "0.00";
                arr[i].amount = successList[i].price * successList[i].surplus;
                arr[i].remarks = successList[i].remarks;
                arr[i].woDate = successList[i].invoiceDate;
                arr[i].woInvoice = successList[i].invoice_type;
                arr[i].woInvoiceId = successList[i].invoice_type_id;
                arr[i].woType = successList[i].work_type;
                arr[i].woTypeId = successList[i].work_type_id;
                arr[i].woCode = successList[i].po_code;
                arr[i].woConfig = successList[i].form_config;
                arr[i].woUniqueId = successList[i].unique_id;
                arr[i].woQuantity = successList[i].surplus; //上游数量
                if (successList[i].f1) {
                  arr[i].f1 = successList[i].f1;
                }
                if (successList[i].f2) {
                  arr[i].f2 = successList[i].f2;
                }
                if (successList[i].f3) {
                  arr[i].f3 = successList[i].f3;
                }
                if (successList[i].f4) {
                  arr[i].f4 = successList[i].f4;
                }
                if (successList[i].f5) {
                  arr[i].f5 = successList[i].f5;
                }
              }
            }

            self.fmData.supplier = list[0][0].supplier;
            self.fmData.supplierId = list[0][0].supplier_id;
            self.fmData.poCheckinCode = list[0][0].po_code;
            // 供方输入状态
            self.stack.supplierStatus = true;
            self.fmData.wmInSalveList.push(...arr);
            self.objUtils.getDecimal();
          });					
        },
        purchaseCheckinClose() {
          self.stack.purchaseCheckinStatus = false;
        },

        //生产入库引入
        btnPrsCheckin() {
          self.stack.invMode= 0;
          self.stack.prsCheckinStatus = true;
        },

        prsCheckinImport(list) {
          return new Promise((resolve, reject) => {
            let materialDate = {
              invSortRoot: "",
              invSortId: "", //物料分类
              invCode: "", //物料编码
              invName: "", //物料名称
              invAttribute: "", //型号规格
              unitCode: "", //主计量
              unitName: "", //主计量
              batchNumber: "", //批号
              ppNumber: "",
              quantity: "", //数量
              price: "", //单价
              amount: 0.0, //金额
              f1: "",
              f2: "",
              f3: "",
              f4: "",
              f5: "",
               location:"",
              beltline: "",
              beltlineId: "",
              woInvoice: "",
              woInvoiceId: "",
              woType: "",
              woTypeId: "",
              woCode: "",
              woConfig: "",
              woDate: null,
              woUniqueId: "",
              woQuantity: "",
              remarks: "", //备注
            };
            let successList = JSON.parse(JSON.stringify(list)),
              formMaterial = []; //赋值物料信息
            successList.forEach((item, i) => {
              formMaterial.push({
                ...materialDate,
              });
            });
            resolve(formMaterial);
          });
        },

        // 选择生产入库引入物料成功
        handlePrsCheckinSuccess(list) {
          self.fmData.wmInSalveList = [];
          this.prsCheckinImport(list[1]).then((arr) => {
            let successList = JSON.parse(JSON.stringify(list[1]));
            if (successList.length > 0) {
              for (let i = 0; i < successList.length; i++) {
                arr[i].invSortRoot = successList[i].inv_sort_root;
                arr[i].invSortId = successList[i].inv_sort_id;
                arr[i].invCode = successList[i].inv_code;
                arr[i].invName = successList[i].inv_name;
                arr[i].unitName = successList[i].unit_name;
                arr[i].unitCode = successList[i].unit_code;
                arr[i].batchNumber = successList[i].batch_number;
                arr[i].ppNumber = successList[i].pp_number;
                arr[i].invAttribute = successList[i].inv_attribute;
                arr[i].quantity = successList[i].surplus || "0.00";
                arr[i].price = successList[i].price || "0.00";
                arr[i].amount =
                  parseFloat(arr[i].price) * parseFloat(arr[i].quantity);
                arr[i].remarks = successList[i].remarks;
                arr[i].beltline = successList[i].beltline || "";
                arr[i].beltlineId = successList[i].beltline_id || "";
                arr[i].woDate = successList[i].invoiceDate;
                arr[i].woInvoice = successList[i].invoice_type;
                arr[i].woInvoiceId = successList[i].invoice_type_id;
                arr[i].woType = successList[i].work_type;
                arr[i].woTypeId = successList[i].work_type_id;
                arr[i].woCode = successList[i].prs_code;
                arr[i].woConfig = successList[i].form_config;
                arr[i].woUniqueId = successList[i].unique_id;
                arr[i].woQuantity = successList[i].surplus; //上游数量
                if (successList[i].f1) {
                  arr[i].f1 = successList[i].f1;
                }
                if (successList[i].f2) {
                  arr[i].f2 = successList[i].f2;
                }
                if (successList[i].f3) {
                  arr[i].f3 = successList[i].f3;
                }
                if (successList[i].f4) {
                  arr[i].f4 = successList[i].f4;
                }
                if (successList[i].f5) {
                  arr[i].f5 = successList[i].f5;
                }
              }
            }
						console.log("001");
            self.fmData.prsCheckinCode = list[0][0].prs_code;
            self.fmData.productionWorkshop = list[0][0].workTypeCn;
            self.fmData.productionWorkshopId = list[0][0].work_type;
            self.enums.selectProductWorkshop = {label: list[0][0].workTypeCn, value: list[0][0].work_type};

            self.fmData.wmInSalveList.push(...arr);
            self.objUtils.getDecimal();
          });
        },

        //关闭
        prsCheckinClose() {
          self.stack.prsCheckinStatus = false;
        },

        //委外入库引入
        btnPuCheckin() {
          self.stack.invMode= 0;
          self.stack.puCheckinStatus = true;
        },

        puCheckinImport(list) {
          return new Promise((resolve, reject) => {
            let materialDate = {
              invSortRoot: "",
              invSortId: "", //物料分类
              invCode: "", //物料编码
              invName: "", //物料名称
              invAttribute: "", //型号规格
              unitCode: "", //主计量
              unitName: "", //主计量
              batchNumber: "", //批号
              ppNumber: "",
              quantity: "", //数量
              price: "", //单价
              amount: 0.0, //金额
              f1: "",
              f2: "",
              f3: "",
              f4: "",
              f5: "",
              location:"",
              beltline: "",
              beltlineId: "",
              woInvoice: "",
              woInvoiceId: "",
              woType: "",
              woTypeId: "",
              woCode: "",
              woConfig: "",
              woDate: null,
              woUniqueId: "",
              woQuantity: "",
              remarks: "", //备注
            };
            let successList = JSON.parse(JSON.stringify(list)),
              formMaterial = []; //赋值物料信息
            successList.forEach((item, i) => {
              formMaterial.push({
                ...materialDate,
              });
            });
            resolve(formMaterial);
          });
        },

        // 选择委外入库引入物料成功
        handlePuCheckinSuccess(list) {
          self.fmData.wmInSalveList = [];
          this.puCheckinImport(list[1]).then((arr) => {
            let successList = JSON.parse(JSON.stringify(list[1]));
            if (successList.length > 0) {
              for (let i = 0; i < successList.length; i++) {
                arr[i].invSortRoot = successList[i].inv_sort_root;
                arr[i].invSortId = successList[i].inv_sort_id;
                arr[i].invCode = successList[i].inv_code;
                arr[i].invName = successList[i].inv_name;
                arr[i].unitName = successList[i].unit_name;
                arr[i].unitCode = successList[i].unit_code;
                arr[i].batchNumber = successList[i].batch_number;
                arr[i].ppNumber = successList[i].pp_number;
                arr[i].invAttribute = successList[i].inv_attribute;
                arr[i].quantity = successList[i].surplus || "0.00";
                arr[i].price = successList[i].price || "0.00";
                arr[i].amount =
                  parseFloat(arr[i].price) * parseFloat(arr[i].quantity);
                arr[i].remarks = successList[i].remarks;
                arr[i].beltline = successList[i].beltline || "";
                arr[i].beltlineId = successList[i].beltline_id || "";
                arr[i].woDate = successList[i].invoice_date;
                arr[i].woInvoice = successList[i].invoice_type;
                arr[i].woInvoiceId = successList[i].invoice_type_id;
                arr[i].woType = successList[i].work_type;
                arr[i].woTypeId = successList[i].work_type_id;
                arr[i].woCode = successList[i].pu_code;
                arr[i].woConfig = successList[i].form_config;
                arr[i].woUniqueId = successList[i].unique_id;
                arr[i].woQuantity = successList[i].surplus; //上游数量
                if (successList[i].f1) {
                  arr[i].f1 = successList[i].f1;
                }
                if (successList[i].f2) {
                  arr[i].f2 = successList[i].f2;
                }
                if (successList[i].f3) {
                  arr[i].f3 = successList[i].f3;
                }
                if (successList[i].f4) {
                  arr[i].f4 = successList[i].f4;
                }
                if (successList[i].f5) {
                  arr[i].f5 = successList[i].f5;
                }
              }
            }
            self.fmData.puCheckinCode = list[0][0].pu_code;
            self.fmData.outSource = list[0][0].supplier;
            self.fmData.outSourceId = list[0][0].supplier_id;

            self.fmData.wmInSalveList.push(...arr);
            self.objUtils.getDecimal();
          });
        },

        //关闭
        puCheckinClose() {
          self.stack.puCheckinStatus = false;
        },

        //销售退货引入
        btnOmReject() {
          self.stack.invMode= 0;
          self.stack.omRejectStatus = true;
        },
        omRejectImport(list) {
          return new Promise((resolve, reject) => {
            let materialDate = {
              invSortId: "", //物料分类
              invCode: "", //物料编码
              invName: "", //物料名称
              invAttribute: "", //型号规格
              unitCode: "", //主计量
              unitName: "", //主计量
              batchNumber: "", //批号
              ppNumber: "",
              quantity: "", //数量
              price: "", //单价
               location:"",
              amount: 0.0, //金额
              f1: "",
              f2: "",
              f3: "",
              f4: "",
              f5: "",
              woInvoice: "",
              woInvoiceId: "",
              woType: "",
              woTypeId: "",
              woCode: "",
              woConfig: "",
              woDate: null,
              woUniqueId: "",
              woQuantity: "",
              remarks: "", //备注
            };
            let successList = JSON.parse(JSON.stringify(list)),
              formMaterial = []; //赋值物料信息
            successList.forEach((item, i) => {
              formMaterial.push({
                ...materialDate,
              });
            });
            resolve(formMaterial);
          });
        },

        // 选择销售退货引入物料成功
        handleOmRejectSuccess(list) {
          console.log(list);
          self.fmData.wmInSalveList = [];
          this.omRejectImport(list[1]).then((arr) => {
            let successList = JSON.parse(JSON.stringify(list[1]));
            if (successList.length > 0) {
              for (let i = 0; i < successList.length; i++) {
                arr[i].invSortRoot = successList[i].inv_sort_root;
                arr[i].invSortId = successList[i].inv_sort_id;
                arr[i].invCode = successList[i].inv_code;
                arr[i].invName = successList[i].inv_name;
                arr[i].unitName = successList[i].unit_name;
                arr[i].unitCode = successList[i].unit_code;
                arr[i].batchNumber = successList[i].batch_number;
                arr[i].ppNumber = successList[i].pp_number;
                arr[i].invAttribute = successList[i].inv_attribute;
                arr[i].quantity = successList[i].surplus;
                arr[i].price = successList[i].price || "0.00";
                arr[i].amount = successList[i].price * successList[i].surplus;
                arr[i].remarks = successList[i].remarks;
                arr[i].woDate = successList[i].invoice_date;
                arr[i].woInvoice = successList[i].invoice_type;
                arr[i].woInvoiceId = successList[i].invoice_type_id;
                arr[i].woType = successList[i].work_type;
                arr[i].woTypeId = successList[i].work_type_id;
                arr[i].woCode = successList[i].om_code;
                arr[i].woConfig = successList[i].form_config;
                arr[i].woUniqueId = successList[i].unique_id;
                arr[i].woQuantity = successList[i].surplus; //上游数量
                if (successList[i].f1) {
                  arr[i].f1 = successList[i].f1;
                }
                if (successList[i].f2) {
                  arr[i].f2 = successList[i].f2;
                }
                if (successList[i].f3) {
                  arr[i].f3 = successList[i].f3;
                }
                if (successList[i].f4) {
                  arr[i].f4 = successList[i].f4;
                }
                if (successList[i].f5) {
                  arr[i].f5 = successList[i].f5;
                }
              }
            }
            self.fmData.prsCheckinCode = list[0][0].prs_code;
            self.fmData.omRejectCode= list[0][0].om_code;
            self.fmData.customer= list[0][0].customer;
            self.fmData.customerId= list[0][0].customer_id;

            self.fmData.wmInSalveList.push(...arr);
            self.objUtils.getDecimal();
          });
        },

        //关闭
        omRejectClose() {
          self.stack.omRejectStatus = false;
        },

        //生产退料引入
        btnPrsUnPick() {
          self.stack.invMode= 0;
          self.stack.prsUnPickStatus = true;
        },
        prsUnPickImport(list) {
          return new Promise((resolve, reject) => {
            let materialDate = {
              invSortRoot: "",
              invSortId: "", //物料分类
              invCode: "", //物料编码
              invName: "", //物料名称
              invAttribute: "", //型号规格
              unitCode: "", //主计量
              unitName: "", //主计量
              batchNumber: "", //批号
              ppNumber: "",
              quantity: "", //数量
              price: "", //单价
              amount: 0.0, //金额
              f1: "",
              f2: "",
              f3: "",
              f4: "",
              f5: "",
               location:"",
              beltline: "",
              beltlineId: "",
              woInvoice: "",
              woInvoiceId: "",
              woType: "",
              woTypeId: "",
              woCode: "",
              woConfig: "",
              woDate: null,
              woUniqueId: "",
              woQuantity: "",
              remarks: "", //备注
            };
            let successList = JSON.parse(JSON.stringify(list)),
              formMaterial = []; //赋值物料信息
            successList.forEach((item, i) => {
              formMaterial.push({
                ...materialDate,
              });
            });
            resolve(formMaterial);
          });
        },

        // 选择生产退料引入物料成功
        handlePrsUnPickSuccess(list) {
          
          self.fmData.prsRejectCode = list[0][0].prs_code;
          self.fmData.productionWorkshop = list[0][0].workTypeCn;
          self.fmData.productionWorkshopId = list[0][0].work_type;
          self.enums.selectProductWorkshop = {label: list[0][0].workTypeCn, value: list[0][0].work_type};

          self.fmData.wmInSalveList = [];
          this.prsUnPickImport(list[1]).then((arr) => {
            let successList = JSON.parse(JSON.stringify(list[1]));
            if (successList.length > 0) {
              for (let i = 0; i < successList.length; i++) {
                arr[i].invSortRoot = successList[i].inv_sort_root;
                arr[i].invSortId = successList[i].inv_sort_id;
                arr[i].invCode = successList[i].inv_code;
                arr[i].invName = successList[i].inv_name;
                arr[i].unitName = successList[i].unit_name;
                arr[i].unitCode = successList[i].unit_code;
                arr[i].batchNumber = successList[i].batch_number;
                arr[i].ppNumber = successList[i].pp_number;
                arr[i].invAttribute = successList[i].inv_attribute;
                arr[i].quantity = successList[i].surplus || "0.00";
                arr[i].price = successList[i].price || "0.00";
                arr[i].amount =
                  parseFloat(arr[i].price) * parseFloat(arr[i].quantity);
                arr[i].remarks = successList[i].remarks;
                arr[i].beltline = successList[i].beltline || "";
                arr[i].beltlineId = successList[i].beltline_id || "";
                arr[i].woDate = successList[i].invoiceDate;
                arr[i].woInvoice = successList[i].invoice_type;
                arr[i].woInvoiceId = successList[i].invoice_type_id;
                arr[i].woType = successList[i].work_type;
                arr[i].woTypeId = successList[i].work_type_id;
                arr[i].woCode = successList[i].prs_code;
                arr[i].woConfig = successList[i].form_config;
                arr[i].woUniqueId = successList[i].unique_id;
                arr[i].woQuantity = successList[i].surplus; //上游数量
                if (successList[i].f1) {
                  arr[i].f1 = successList[i].f1;
                }
                if (successList[i].f2) {
                  arr[i].f2 = successList[i].f2;
                }
                if (successList[i].f3) {
                  arr[i].f3 = successList[i].f3;
                }
                if (successList[i].f4) {
                  arr[i].f4 = successList[i].f4;
                }
                if (successList[i].f5) {
                  arr[i].f5 = successList[i].f5;
                }
              }
            }

            self.fmData.wmInSalveList.push(...arr);
            self.objUtils.getDecimal();
          });
        },

        //关闭
        prsUnPickClose() {
          self.stack.prsUnPickStatus = false;
        },        

        //委外退料引入
        btnPuUnPick() {
          self.stack.invMode= 0;
          self.stack.puUnPickStatus = true;
        },

        puUnPickImport(list) {
          return new Promise((resolve, reject) => {
            let materialDate = {
              invSortRoot: "",
              invSortId: "", //物料分类
              invCode: "", //物料编码
              invName: "", //物料名称
              invAttribute: "", //型号规格
              unitCode: "", //主计量
              unitName: "", //主计量
              batchNumber: "", //批号
              ppNumber: "",
              quantity: "", //数量
              price: "", //单价
              amount: 0.0, //金额
              f1: "",
              f2: "",
              f3: "",
              f4: "",
              f5: "",
              location:"",
              beltline: "",
              beltlineId: "",
              woInvoice: "",
              woInvoiceId: "",
              woType: "",
              woTypeId: "",
              woCode: "",
              woConfig: "",
              woDate: null,
              woUniqueId: "",
              woQuantity: "",
              remarks: "", //备注
            };
            let successList = JSON.parse(JSON.stringify(list)),
              formMaterial = []; //赋值物料信息
            successList.forEach((item, i) => {
              formMaterial.push({
                ...materialDate,
              });
            });
            resolve(formMaterial);
          });
        },

        // 选择委外退料引入物料成功
        handlePuUnPickSuccess(list) {          
          self.fmData.puRejectCode = list[0][0].pu_code;
          self.fmData.outSource = list[0][0].supplier;
          self.fmData.outSourceId = list[0][0].supplier_id;

          self.fmData.wmInSalveList = [];
          this.puUnPickImport(list[1]).then((arr) => {
            let successList = JSON.parse(JSON.stringify(list[1]));
            if (successList.length > 0) {
              for (let i = 0; i < successList.length; i++) {
                arr[i].invSortRoot = successList[i].inv_sort_root;
                arr[i].invSortId = successList[i].inv_sort_id;
                arr[i].invCode = successList[i].inv_code;
                arr[i].invName = successList[i].inv_name;
                arr[i].unitName = successList[i].unit_name;
                arr[i].unitCode = successList[i].unit_code;
                arr[i].batchNumber = successList[i].batch_number;
                arr[i].ppNumber = successList[i].pp_number;
                arr[i].invAttribute = successList[i].inv_attribute;
                arr[i].quantity = successList[i].surplus || "0.00";
                arr[i].price = successList[i].price || "0.00";
                arr[i].amount =
                  parseFloat(arr[i].price) * parseFloat(arr[i].quantity);
                arr[i].remarks = successList[i].remarks;
                arr[i].beltline = successList[i].beltline || "";
                arr[i].beltlineId = successList[i].beltline_id || "";
                arr[i].woDate = successList[i].invoice_date;
                arr[i].woInvoice = successList[i].invoice_type;
                arr[i].woInvoiceId = successList[i].invoice_type_id;
                arr[i].woType = successList[i].work_type;
                arr[i].woTypeId = successList[i].work_type_id;
                arr[i].woCode = successList[i].pu_code;
                arr[i].woConfig = successList[i].form_config;
                arr[i].woUniqueId = successList[i].unique_id;
                arr[i].woQuantity = successList[i].surplus; //上游数量
                if (successList[i].f1) {
                  arr[i].f1 = successList[i].f1;
                }
                if (successList[i].f2) {
                  arr[i].f2 = successList[i].f2;
                }
                if (successList[i].f3) {
                  arr[i].f3 = successList[i].f3;
                }
                if (successList[i].f4) {
                  arr[i].f4 = successList[i].f4;
                }
                if (successList[i].f5) {
                  arr[i].f5 = successList[i].f5;
                }
              }
            }

            self.fmData.wmInSalveList.push(...arr);
            self.objUtils.getDecimal();
          });
        },

        //关闭
        puUnPickClose() {
          self.stack.puUnPickStatus = false;
        },           
      };
    },		
    //工具
    classUtils() {
      const self = this;
      return {
        /** 格式化用户输入价格单价小数位数 */
        formatDecimal(index, data) {
          if (
            self.fmData.wmInSalveList[index].price !== "" &&
            isDecimal(self.fmData.wmInSalveList[index].price)
          ) {
            let price = self.fmData.wmInSalveList[index].price;
            self.fmData.wmInSalveList[index].price = parseFloat(price).toFixed(
              self.fmConfig.decimalPrice
            );
          }
          if (
            self.fmData.wmInSalveList[index].quantity !== "" &&
            isDecimal(self.fmData.wmInSalveList[index].quantity)
          ) {
            let quantity = self.fmData.wmInSalveList[index].quantity;
            self.fmData.wmInSalveList[index].quantity = parseFloat(
              quantity
            ).toFixed(self.fmConfig.decimalQuantity);
          }
          if (
            self.fmData.wmInSalveList[index].price !== "" &&
            self.fmData.wmInSalveList[index].quantity !== "" &&
            isDecimal(self.fmData.wmInSalveList[index].price) &&
            isDecimal(self.fmData.wmInSalveList[index].quantity)
          ) {
            self.fmData.wmInSalveList[index].amount = (
              self.fmData.wmInSalveList[index].price *
              self.fmData.wmInSalveList[index].quantity
            ).toFixed(2);
          } else {
            self.fmData.wmInSalveList[index].amount = "0.00";
          }
        },

        /** 数量单价金额小数位数*/
        getDecimal() {
          self.fmData.wmInSalveList.forEach((item) => {
            if (item.price !== null) {
              item.price = parseFloat(item.price).toFixed(
                self.fmConfig.decimalPrice
              );
            }
            if (item.quantity !== null) {
              item.quantity = parseFloat(item.quantity).toFixed(
                self.fmConfig.decimalQuantity
              );
            }
            if (item.amount !== null) {
              item.amount = parseFloat(item.amount).toFixed(2);
            }
          });
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

        //期间转换
        periodDirInt(data) {
          let arr = data.split("-");
          let int = parseInt(arr.join(""));
          return int;
        },
        periodDirStr(data) {
          let time = data.toString();
          let y = time.slice(0, 4),
            m = time.slice(4, 6),
            str = y + "-" + m;
          return str;
        },

        //解决级联选择器回显高亮异常问题
        resetSelector() {
          if (self.$refs.classTree) {
            self.$refs.classTree.$refs.panel.activePath = [];
            self.$refs.classTree.$refs.panel.calculateCheckedNodePaths();
          }
        },
      };
    },		
		// 伪request请求
		request(options) {
			const { pageSize, pageCurrent, success, value } = options
			let total = tableData.length
			let data = tableData.filter((item, index) => {
				const idx = index - (pageCurrent - 1) * pageSize
				return idx < pageSize && idx >= 0
			})
			if (value) {
				data = []
				tableData.forEach(item => {
					if (item.name.indexOf(value) !== -1) {
						data.push(item)
					}
				})
				total = data.length
			}

			setTimeout(() => {
				typeof success === 'function' &&
					success({
						data: data,
						total: total
					})
			}, 500)
		}
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

