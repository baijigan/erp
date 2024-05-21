<template>
	<view class="wrap">
		<view class="search" style="margin-bottom: 4px;">
			<u-search v-model="keywords" @custom="search" @search="search"></u-search>
		</view>
		<scroll-view class="scroll-list msg-list-item" scroll-y="true">
			<view v-for="(item, index) in basicfileList" :key="index">
				<view style="padding-left: 10px;padding-right: 10px;padding-top: 10px; background-color: #ffffff;">
					<view>{{item.code}} {{item.name}} {{item.attribute}}</view>
					<view style="color: #c3c3c3;">
						<table>
							<tr style="border-spacing: 0px;">
								<td width="25%">库存数量：</td><td width="20%">{{item.quantity}}</td>
								<td width="25%">锁定数量：</td><td width="20%">{{item.lockQuantity}}</td>
							</tr>
							<tr style="border-spacing: 0px;">
								<td width="25%">在途数量：</td><td width="20%">{{item.wayQuantity}}</td>
								<td width="25%">实际可用：</td><td width="20%">{{item.availableQuantity}}</td>
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
  listSortMaterial, //分类物料
  listWhMaterial, //仓库物料
  seeMaterial, //物料汇总
  listPacking, //计量单位
  exportSortMaterial, //导出分类物料
  exportWhMaterial, //导出仓库物料
  listWarehouses, //仓库列表
  detailedMaterial, //物料明细
} from "../../api/balance"; //物料接口	
export default {
	data() {
		return {
      // 遮罩层
      loading: true,
			keywords: '',
      //分类搜索名称
      searchName: "",
      // 批量删除按钮状态
      multiple: true,
      //是否显示分类操作按钮
      isOperation: true,
      //树形数据
      sortTree: [],
      //树形选中是否高亮
      isHeight: true,
      //物料数据最大高度
      maxHeight: null,
      //搜索参数
      searchForm: {
        searchName: "", //搜索物料名称
        searchCode: "", //搜索物料编码
      },
      //仓库选择
      optionswarehouse: [],
      selectwarehouse: "",
      // 料品查询参数
      queryParamsSort: {
        pageNum: 1,
        pageSize: 100,
        sortId: "",
        invName: "",
        invCode: "",
      },
      // 料品查询参数
      queryParamsWh: {
        pageNum: 1,
        pageSize: 100,
        whCode: "",
      },
      // 物料明细查询参数
      queryParamsDetailed: {
        whCode: "",
        invCode: "",
      },
      // 总条数
      total: 0,
      //小数位数
      decimal: "2",
      //分页显示方式 分类查询/仓库查询
      pageStaus: "",
      //导出状态
      exportStatus: "sort",
      // 料品数据
      basicfileList: [],
      // 新增/修改/编辑弹框状态
      open: false,
      //明细弹框状态
      detailedStatus: false,
      //明细数据
      detailedDtata: [],
      //明细框高度
      detailedHeight: null,
      //选择分类回显信息
      value: [],
      //表单禁用状态
      seeStatus: false,
      //分类选择配置
      classProps: {
        children: "children",
        label: "sortName",
        value: "sortCode",
        checkStrictly: true,
      },
      packingDate: [],
      // 计量单位
      packingForm: "",
      // 物料新增/修改信息表单
      materialForm: {
        items: {},
        wm: [],
      },
      // 核验地址
      effecUrl: process.env.VUE_APP_BASE_API + "//wm/balance/check/",

      //打印分类名称
      printName: "",
      //打印数据
      printData: [],
      //批量打印数据
      batchPrintData: [],
      //物料查看数量表单
      materialMount: {
        items: {},
        wm: [],
      },			
		}
	},
	onLoad(option) {
		this.getSortMaterialList();
	},
	methods: {
    //搜索物料
    search() {
      if (
        this.keywords.searchName == "" 
      ) {
        this.msgError("物料编码或物料名称不能同时为空！");
      } else {
				if(this.keywords.indexOf('0')==0){
					this.queryParamsSort.invName = ''; //赋值物料名称
					this.queryParamsSort.invCode = this.keywords ; //赋值物料编码					
				}
				else{
					this.queryParamsSort.invName = this.keywords; //赋值物料名称
					this.queryParamsSort.invCode = ''; //赋值物料编码
				}
				
        this.queryParamsSort.sortId = ""; //避免当分类树点击时页面缓存的sortId导致搜索失败
        this.getSortMaterialList();
      }
    },
    // 获取分类物料数据
    getSortMaterialList() {
      listSortMaterial(this.queryParamsSort).then((res) => {
        this.pageStaus = "sort";
        let arrBasic = JSON.parse(JSON.stringify(res.rows));
        //数量格式化4位小说
        if (arrBasic.length > 0) {
          arrBasic.forEach((value) => {
            value.quantity = value.quantity.toFixed(this.decimal);
            value.lockQuantity = value.lockQuantity.toFixed(this.decimal);
            value.wayQuantity = value.wayQuantity.toFixed(this.decimal);
            value.availableQuantity = value.availableQuantity.toFixed(
              this.decimal
            );
          });
        }

        this.basicfileList = arrBasic;
        this.total = res.total;
        if (this.basicfileList.length >= 19) {
          //条数总数大于显示出现滚动条
          //this.maxHeight = `${document.documentElement.clientHeight}` - 270;
        } else {
          //this.maxHeight = "";
        }
      });
    },		
	}
}
</script>

<style>

</style>
