<template>
	<view>
		<view style="background-color: ;padding: 10px 10px;">
			<u-card :title="fmData.ppNumber" :subTitle="inMaterTime(fmData.ppDate)" margin="0rpx auto" thumb="/static/aidex/images/list-icon.png">
				<view class="" slot="body" v-for="(item, index) in fmData.mpOrderSalveList">
					<view class="u-body-item u-flex  u-col-between u-p-t-0">
						<view class="u-body-item-title u-line-2">
							{{item.invCode}} {{item.invName}}{{item.invAttribute}}  {{item.quantity}}{{item.unitName}}
						</view>
					</view>
				</view>
				<view class="" slot="body">
					<view class="u-body-item u-flex  u-col-between u-p-t-0">
						<view class="u-body-item-title u-line-2" style="color: #c2c2c2;" >交期 {{inMaterTime(fmData.ppDate)}}</view>
					</view>						
				</view>	
			</u-card>			
		</view>
		<view >
		<scroll-view class="scroll-list msg-list-item" scroll-y="true">
			<view v-for="(item, index) in stack.mbomData" :key="index">
				<view style="margin-top:10px;background-color: #ffffff; margin-left:10px;margin-right: 10px;">
					<view style="padding: 8px 20px;">物料清单：{{item.mpCode}}</view>
					<u-line color="#a8a8a8" />
					<view style="padding: 5px 20px;color: #c3c3c3;">
						<view style="">{{item.invCode}} {{item.invName}} {{item.invAttribute}}  </view>
						<view>{{supplyTypeFilter(item.invSupplyType)}}数量 {{item.invQuantity}}{{item.unitName}}</view>
						<view>{{prsTypeFilter(item.prsWorkType)}} {{inMaterTime(item.prsDate)}}</view>
						<u-button style="margin-top: 10px;" type="primary" @click="checkReady(item.mpCode, item.invCode, item.invName, item.invAttribute, item.invQuantity, item.unitName, supplyTypeFilter(item.invSupplyType))" >齐套检查</u-button>
					</view>
				</view>
				<u-card style="display: none;" :title="item.mpCode" :sub-title="supplyTypeFilter(item.invSupplyType)" padding="20" margin="0rpx 0rpx" thumb="/static/aidex/images/list-icon.png" 
							@click="goDetail(item.mpCode)">
					<view class="" slot="body">
						<view class="u-body-item u-flex  u-col-between u-p-t-0">
							<view class="u-body-item-title u-line-2">{{item.invCode}} {{item.invName}} {{item.invAttribute}}  &nbsp;&nbsp; {{item.invQuantity}}{{item.unitName}}</view>
						</view>
					</view>
					<view class="" slot="body">
						<view class="u-body-item u-flex  u-col-between u-p-t-0">
							<view class="u-body-item-title u-line-2" style="color: #c2c2c2;" >{{prsTypeFilter(item.prsWorkType)}} {{inMaterTime(item.prsDate)}}</view>
							<u-button type="primary" @click="checkReady(item.mpCode, item.invCode, item.invName, item.invAttribute, item.invQuantity, item.unitName, supplyTypeFilter(item.invSupplyType))" >齐套检查</u-button>							
						</view>						
					</view>	
				</u-card>
			</view>
			<u-divider>已经到底了</u-divider>
		</scroll-view>
		</view>
	</view>
</template>

<script>
//接口
import {
  dictMatching, //系统字典
  listDept,     //部门列表
  listUser,     //人员(用户)
  addBill,     //新增单据
  seeBill,     //查看单据
  updateBill,  //更新单据
  NextBill,    //上一条下一条
  examineBill,    //审核单据
  examineDeBill,  //反审核单据
  moduleParameters,
  billParameters,
  operationBill, //操作
  billBeReady,   //齐套
  billPrsStatus, //下发生产
  listRdCodeBill,
  listRdCodeDetail,
  listCreate,
  listMbom,       //关联物料清单
  listMpReady,
  operationMbom,
} from "../../api/order";
//并行请求
import axios from "axios";	
export default {
	data() {
		return {
      // 遮罩层
      loading: false,
			keywords: "",
			
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
        //记录时间戳,用于是否刷新当前页面
        timeStamp: "",
      },

      //表单状态-addStatus:新增状态,seeStatus:查看状态,updateStatus:编辑状态
      fmStatus: "",

      //数据表单
      fmData: {
				ppNumber: '',
        ebomType: 0,
        poAttachmentList: [],
      },

      //新增物料信息
      fmItem: {
        invSortId: "", //物料分类
        invCode: "", //物料编码
        invName: "", //物料名称
        invAttribute: "", //型号规格
        unitCode: "", //主计量
        unitName: "", //主计量
        quantity: "", //数量
        price: "0.00", //单价
        amount: "0.00", //采购金额
        piece: "",
        pkgRemarks: "",
        qualityRemarks: "",
        woDate: null,
        woCode: "", //上游单据
        woUniqueId: 0, //上游物料
        woQantity: "",
        wiQantity: "",
        drawingNo: "",
        remarks: "", //备注
      },

      //单据参数信息
      fmConfig: {
        decimalQuantity: "4", //数量小数位数
        decimalPrice: "4", //数量小数位数
        attrMerge: "false", //规格合并
        attrAlias: "型号规格", //规格别名
        sortArrId: [], //料品大类
        industryCode: "", //物料特性
        fieldHiding: [], //隐藏字段
        bomType: "",
        bomConfig: "",
        fmMbomConfig: "",
        bomPrecision:"2",
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
        //生产车间
        optionsPrsWorkType: [],
        //物料清单业务类型
        optionsMbomWorkType: [],
        //供应方式
        optionsMbomSupplyType: [],
        //补料事由
        optionsMbomReplenishType: [],
      },

      //执行中
      stack: {
        //操作下拉切换
        commandIndex: "",
        //点击选择物料记录下标，用于更新物料信息
        rowIndex: "",
        //物料选择弹框状态
        selectDialog: false,
        // 物料编码搜索下拉状态
        saveIndex: null,
        // 物料编码搜索数据
        basicfileList: [],
        //供方
        optionssupplier: [],
        poStatus: false,
        poSearchStatus: false,
        // 引入信息
        optionsImportMaterial: [],
        ImportMaterialStatus: false,
        //上传采购合同
        uploadList: [],
        //采购人员
        optionsworkStaff: [],
        selectworkStaff: "",
        //下发生产
        prsDateDialog: false,
        //生产车间
        prsWorkType: "",
        //生产日期
        prsDate: null,
        // 用料方式
        optionsinvInType: [],
        // 物料清单用料方式
        optionsinvOutType: [],
        //配方单号
        selectRdCodeStatus: false,
        //分子固定用量
        ratioTitle: "",
        fillTitle: "",
        // BOM 计算
        bomLoading: false,
        bomTitle: "BOM计算",
        //物料清单
        mbomData: [],
        //补料清单
        mbomRepleData: [],
        //是否重复计算
        mbomCode: "",
        //详情抽屉
        activeName: "first",
        drawerTableLoading: true,
        drawerTitle: "",
        drawer: false,
        isDrawer: null,
        direction: "rtl",
        drawerData: null,
        drawerTableData: [],
        drawerCustomer: { params: {} },
        materialStatus: false,
        drawerMaterial: {}, //物料信息
        drawerTableLoading: true,
        drawerTableKey: "",
        drawerTableMaxHeight: null,
        //物料清单全部明细
        drawerMpMbomComponents: false,
        drawerMpMbomStatus: false,
        drawerMpMbomCode: "",
        drawerMpMbomTitle: "",

        //物料清单齐套明细
        drawerMpReadyComponents: false,
        drawerMpReadyStatus: false,
        drawerMpReadyCode: "",
        drawerMpReadyTitle: "",        
        drawerType: "",
      },		
				
			objMaterial: this.classMaterial(), //物料
			objHelper: this.classHelper(), //助手
			objUtils: this.classUtils(), //工具
		}
	},
	onLoad(option){
		//初始化页面		
		if(this.$route === undefined){
			this.$route= {};
			this.$route.query= {};
			this.$route.query.open= option.open;
			this.$route.query.timeStamp= option.timeStamp;
			this.$route.query.fmConfig= option.fmConfig;
			this.$route.query.workType= option.workType;
			this.$route.query.detailCode= option.detailCode;
		}

		this.initialStatus = true;
		this.Initial();
	},
	methods: {
    Initial() {
      // 按钮状态
      this.fmStatus = this.$route.query.open;
      this.entry.setConfig = this.$route.query.fmConfig;
      this.entry.timeStamp = this.$route.query.timeStamp;
      if (this.fmStatus == "addStatus") {
        //新增状态重置表单
        this.objHelper.handleReset();
        this.stack.mbomData = [];
        this.stack.mbomRepleData = [];
      }
      this.getDictionaryType().then((res) => {
        this.objHelper.getParameters().then(() => {
          if (this.$route.query.workType == "") {
            if (this.fmStatus == "addStatus") {
              this.fmData.workType = this.enums.optionsworkType[0].dictValue;
            }
          } else {
            this.fmData.workType =
              this.enums.optionsworkType[this.$route.query.workType].dictValue;
          }
          if (this.fmStatus == "seeStatus" || this.fmStatus == "updateStatus") {
            this.entry.seeCode = this.$route.query.detailCode;
            seeBill({
              mpCode: this.entry.seeCode,
            }).then((res) => {
              if (res.code == 200) {
                this.fmData = res.data;
                this.stack.mbomCode = this.fmData.mbomCode;
                //物料清单
                this.objMaterial.getMbom(this.fmData.mpCode);
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
            dictMatching("mp_invoice_type"),
            // 业务类型
            dictMatching("mp_order_type"),
            // 业务状态
            dictMatching("sys_work_status"),
            //子用料方式
            dictMatching("inv_in_type"),
            //生产车间
            dictMatching("prs_order_type"),
            // 物料清单类型
            dictMatching("mp_mbom_type"),
            //供应方式
            dictMatching("inv_supply_type"),
            //子用料方式
            dictMatching("inv_out_type"),
            // 补料事由
            dictMatching("mp_replenish_type"),
          ])
          .then(
            axios.spread(function (
              resIvType,
              resOrderType,
              resWorkStatus,
              resInvInType,
              resPrsType,
              resWorkMbom,
              resInvSupply,
              resInvOutType,
              resReplenishType
            ) {
              if (
                resIvType.code == 200 &&
                resOrderType.code == 200 &&
                resWorkStatus.code == 200
              ) {
                // 单据类型
                _self.enums.optionsInvoiceType = resIvType.data;
                // 业务类型
                _self.enums.optionsworkType = resOrderType.data;
                //业务状态
                _self.enums.optionsworkStatus = resWorkStatus.data;
                //物料清单用料方式
                _self.enums.optionsinvInType = resInvInType.data;
                //子用料方式
                _self.enums.optionsinvOutType = resInvOutType.data;
                // 生产车间
                _self.enums.optionsPrsWorkType = resPrsType.data;
                //物料清单业务类型
                _self.enums.optionsMbomWorkType = resWorkMbom.data;
                //物料清单供应方式
                _self.enums.optionsMbomSupplyType = resInvSupply.data;
                //补料事由
                _self.enums.optionsMbomReplenishType = resReplenishType.data;
                //新增状态
                if (_self.fmStatus == "addStatus") {
                  _self.loading = false;
                  _self.fmData.invoiceDate = new Date();
                  _self.fmData.warehouseOper = _self.$store.getters.name;
                }

                // 查看/编辑数据
                if (
                  (_self.fmStatus == "seeStatus" && _self.fmData !== null) ||
                  _self.fmStatus == "updateStatus"
                ) {
                  //_self.$refs.fmData.clearValidate();

                  // 业务人员
                  _self.stack.selectworkStaff = _self.fmData.workStaff;
                }

                resolve();
              } else {
                _self.msgError("获取信息失败");
              }
            })
          );
      });
    },

		checkReady(mpCode, invCode, invName, invAttribute, invQuantity, unitName, type){
			let param= "mpCode="+ mpCode+ "&invCode="+ invCode+ "&invName="+ invName+ "&invAttribute="+ invAttribute;
			param= param+ "&invQuantity="+ invQuantity+ "&unitName="+ unitName+ "&type="+type;
			let url= "/module/plan/views/order/material";
			uni.navigateTo({
				url: url+ "?"+ param
			});
		},
		
    inMaterTime(date) {
			if(date==null)return '';
      let time = new Date(date);
      return (
        time.getFullYear() + "-" + (time.getMonth() + 1) + "-" + time.getDate()
      );
    },	
		
		supplyTypeFilter(type){
			let str= '';
			this.enums.optionsMbomSupplyType.forEach((item)=>{
					if(item.dictValue== type){
						str= item.dictLabel;
					}
			})
			
			return str;
		},
		
		prsTypeFilter(type){
			let str= '';
			this.enums.optionsPrsWorkType.forEach((item)=>{
					if(item.dictValue== type){
						str= item.dictLabel;
					}
			})
			
			return str;
		},
		
    //物料信息
    classMaterial() {
      const self = this;
      return {
        // 物料信息最大高度
        materialMaxHeight() {
          if (`${document.documentElement.clientHeight}` - 460 < 400) {
            self.style.tableHeight = 500;
          } else {
            if (self.fmStatus == "seeStatus") {
              self.style.tableHeight =
                `${document.documentElement.clientHeight}` - 495;
            } else {
              self.style.tableHeight =
                `${document.documentElement.clientHeight}` - 475;
            }
          }
        },

        // 选择引入物料
        handleImportMaterialSelect(code, index) {
          self.stack.ImportMaterialStatus = true;
          self.$refs.fmData.clearValidate();
        },
        handdleAddImport(list) {
          return new Promise((resolve, reject) => {
            let materialDate = {
              invSortId: "", //物料分类
              invCode: "", //物料编码
              invName: "", //物料名称
              invAttribute: "", //型号规格
              unitCode: "", //主计量
              unitName: "", //主计量
              quantity: "", //采购数量
              price: "0.00", //单价
              amount: "0.00", //金额
              piece: "",
              qualityRemarks: "",
              pkgRemarks: "",
              woInvoice: "",
              woInvoiceId: "",
              woType: "",
              woTypeId: "",
              woDate: null,
              woConfig: "",
              woQuantity: "", //上游数量
              wiQuantity: "", //下游数量
              woCode: "", //上游单据
              woUniqueId: "", //上游物料
              drawingNo: "",
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

        // 选择引入物料成功
        handleImportMaterialSuccess(list) {
          self.fmData.mpOrderSalveList = [];
          this.handdleAddImport(list[1]).then((arr) => {
            let successList = JSON.parse(JSON.stringify(list[1]));
            if (successList.length > 0) {
              for (let i = 0; i < successList.length; i++) {
                arr[i].invSortRoot = successList[i].inv_sort_root;
                arr[i].invSortId = successList[i].inv_sort_id;
                arr[i].invCode = successList[i].inv_code;
                arr[i].invName = successList[i].inv_name;
                arr[i].unitName = successList[i].unit_name;
                arr[i].unitCode = successList[i].unit_code;
                arr[i].invAttribute = successList[i].inv_attribute;
                arr[i].piece = successList[i].piece;
                arr[i].qualityRemarks = successList[i].quality_remarks;
                arr[i].pkgRemarks = successList[i].pkg_remarks;
                arr[i].woInvoice = successList[i].invoice_type;
                arr[i].woInvoiceId = successList[i].invoice_type_id;
                arr[i].woType = successList[i].work_type;
                arr[i].woTypeId = successList[i].work_type_id;
                arr[i].woDate = successList[i].date || null;
                arr[i].woConfig = successList[i].form_config;
                arr[i].woCode = successList[i].om_code;
                arr[i].remarks = successList[i].remarks;
                arr[i].woUniqueId = successList[i].unique_id;
                arr[i].drawingNo = successList[i].drawing_no;
                arr[i].quantity = successList[i].surplus;
                arr[i].woQuantity = successList[i].surplus;
              }
            }
            self.fmData.ppNumber = successList[0].om_code;
            self.fmData.salesman = successList[0].work_staff;
            self.fmData.ebomCode = "";
            if (list[0][0].deliverDate !== undefined) {
              self.fmData.ppDate = list[0][0].deliverDate;
            }
            self.fmData.mpOrderSalveList.push(...arr);
          });
        },

        // 删除
        deleteList(index) {
          self.$message({
            message: "删除成功",
            duration: 1000,
          });
          self.fmData.mpOrderSalveList.splice(index, 1);
          self.stack.saveIndex = null;
        },

        //关闭引入组件
        selectImportMaterialClose() {
          self.stack.ImportMaterialStatus = false;
        },

        //获取关联物料清单
        getMbom(code) {
          //并行请求
          axios
            .all([
              listMbom({ mpOrderCode: code, isBomCalc: true }),
              listMbom({ mpOrderCode: code, isBomCalc: false }),
            ])
            .then(
              axios.spread(function (resMbom, resReple) {
                if (resMbom.code == 200 && resReple.code == 200) {
                  self.stack.mbomData = resMbom.rows;
                  self.stack.mbomRepleData = resReple.rows;
                  self.objUtils.getDecimal();
                } else {
                  self.msgError("查询物料清单失败");
                }
              })
            );
        },

        //物料清单跳转
        handleViewBill(code) {
          self.$router.push({
            path: "/mp/plan/mbomview",
            query: {
              detailCode: code,
              open: "seeStatus",
              workType: "",
              fmConfig: self.fmConfig.fmMbomConfig,
              timeStamp: new Date().getTime(),
            },
          });
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
                if (res.data["inv.scope"] !== "null") {
                  let arr = res.data["inv.scope"].split(",");
                  self.fmConfig.sortArrId = arr;
                }

                self.fmConfig.industryCode = self.$store.getters.industry;
                if(self.fmConfig.industryCode == '02'){
                  self.fmConfig.bomType = 0;
                  self.fmConfig.bomConfig = "010401,020401";
                  self.fmConfig.fmMbomConfig = "010502,020502";
                  self.fmConfig.bomPrecision = 0;
                }
                else if(self.fmConfig.industryCode == '03'){
                  self.fmConfig.bomType = 1;
                  self.fmConfig.bomConfig = "010401,030401";
                  self.fmConfig.fmMbomConfig = "010502,030502";
                  self.fmConfig.bomPrecision = 0;
                }
                else if(self.fmConfig.industryCode == '04'){
                  self.fmConfig.bomType = 2;
                  self.fmConfig.bomConfig = "010401,040401";
                  self.fmConfig.fmMbomConfig = "010502,040502";
                  self.fmConfig.bomPrecision = 4;
                }


                if (self.fmConfig.bomType == "0") {
                  self.stack.ratioTitle = "分子";
                  self.stack.fillTitle = "固定用量";
                } else if (self.fmConfig.bomType == "2") {
                  self.stack.ratioTitle = "比例";
                  self.stack.fillTitle = "补足";
                }
                setTimeout(() => {
                  if (self.fmStatus == "addStatus") {
                    //切换，重新渲染表格防止抖动
                    self.$refs["tableClums"].doLayout();
                  }
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
        },

        //业务部门选择
        workDeptChange(value) {
          let deptId = JSON.parse(JSON.stringify(value)); //防止干扰级联选择器
          self.fmData.workDept = deptId.pop();
          self.stack.selectworkStaff = "";
          self.fmData.workStaff = "";
          listUser({
            deptId: self.fmData.workDept,
          }).then((res) => {
            self.stack.optionsworkStaff = res.rows;
          });
        },

        //业务人员选择
        workStaffChange(value) {
          self.fmData.workStaff = self.stack.selectworkStaff;
        },

        //切换计划方式
        supplyTypeChange() {
          self.fmData.mpOrderSalveList = [];
          self.$nextTick(() => {
            //切换，重新渲染表格防止抖动
            self.$refs["tableClums"].doLayout();
          });
        },

        // 部门数据回显
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

        //点击新增/查看新增重置表单
        handleReset() {
          self.fmData = {
            mpCode: "", //单据编码
            invoiceDate: new Date(), //单据日期
            invoiceStatus: "0", //单据状态
            invoiceType: "0", //单据类型
            workStatus: "0", //业务状态
            pkgStatus: "0",
            omStatus: "0",
            workType: "", //业务类型
            formConfig: "",
            userOper: self.$store.getters.name,
            workDept: "", //业务部门
            workStaff: "", //业务人员
            checkDate: "", //审核日期
            userCheck: "",
            remarks: "", //备注
            ppNumber: "",
            ppDate: null,
            salesman: "",
            density: "",
            ebomCode: "",
            rdStatus: "0",
            rdUser: "",
            rdDate: "",
            mpOrderSalveList: [],
          };
          self.stack.selectworkStaff = "";
          /* 初始化状态 */
          self.option.disabledInput = false;
          self.workStatus = false;
          self.option.btnIsClick = false;
          self.option.wmworkStatus = false;
          self.enums.selectworkDept = "";
          self.workStaff = "";
          self.stack.mbomCode = "";
        },
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
            self.fmData.mpOrderSalveList[index].price !== "" &&
            isDecimal(self.fmData.mpOrderSalveList[index].price)
          ) {
            let price = self.fmData.mpOrderSalveList[index].price;
            self.fmData.mpOrderSalveList[index].price = parseFloat(
              price
            ).toFixed(self.fmConfig.decimalPrice);
          }
          if (
            self.fmData.mpOrderSalveList[index].quantity !== "" &&
            isDecimal(self.fmData.mpOrderSalveList[index].quantity)
          ) {
            let quantity = self.fmData.mpOrderSalveList[index].quantity;
            self.fmData.mpOrderSalveList[index].quantity = parseFloat(
              quantity
            ).toFixed(self.fmConfig.decimalQuantity);
          }
          if (
            self.fmData.mpOrderSalveList[index].price !== "" &&
            self.fmData.mpOrderSalveList[index].quantity !== "" &&
            isDecimal(self.fmData.mpOrderSalveList[index].price) &&
            isDecimal(self.fmData.mpOrderSalveList[index].quantity)
          ) {
            self.fmData.mpOrderSalveList[index].amount = (
              self.fmData.mpOrderSalveList[index].price *
              self.fmData.mpOrderSalveList[index].quantity
            ).toFixed(2);
          } else {
            self.fmData.mpOrderSalveList[index].amount = "0.00";
          }
        },

        //格式化数量单价金额小数位数
        getDecimal() {
          if (self.fmData.mpOrderSalveList.length > 0) {
            self.fmData.mpOrderSalveList.forEach((item) => {
              if (item.price !== null && item.price !== undefined) {
                item.price = parseFloat(item.price).toFixed(
                  self.fmConfig.decimalQuantity
                );
              }
              if (item.quantity !== null && item.quantity !== undefined) {
                item.quantity = parseFloat(item.quantity).toFixed(
                  self.fmConfig.decimalQuantity
                );
              }
              if (item.amount !== null && item.amount !== undefined) {
                item.amount = parseFloat(item.amount).toFixed(2);
              }
              if (item.wmQuantity !== null && item.wmQuantity !== undefined) {
                item.wmQuantity = parseFloat(item.wmQuantity).toFixed(
                  self.fmConfig.decimalQuantity
                );
              }
            });
          }
          if (self.stack.mbomData.length > 0) {
            self.stack.mbomData.forEach((item) => {
              if (item.invQuantity !== null && item.invQuantity !== undefined) {
                item.invQuantity = parseFloat(item.invQuantity).toFixed(
                  self.fmConfig.decimalQuantity
                );
              }
              if (item.wm_quantity !== null && item.wm_quantity !== undefined) {
                item.wm_quantity = parseFloat(item.wm_quantity).toFixed(
                  self.fmConfig.decimalQuantity
                );
              }
            });
          }
          if (self.stack.mbomRepleData.length > 0) {
            self.stack.mbomRepleData.forEach((item) => {
              if (item.invQuantity !== null && item.invQuantity !== undefined) {
                item.invQuantity = parseFloat(item.invQuantity).toFixed(
                  self.fmConfig.decimalQuantity
                );
              }
              if (item.wm_quantity !== null && item.wm_quantity !== undefined) {
                item.wm_quantity = parseFloat(item.wm_quantity).toFixed(
                  self.fmConfig.decimalQuantity
                );
              }
            });
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
            time.getDate()
          );
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

