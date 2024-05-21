<template>
	<view class="wrap">
		<view class="uni-container">
			<view class="u-p-t-30 u-p-b-30 u-flex u-flex-col">
				<!-- <u-avatar size="250" src="/static/aidex/logo200.png"></u-avatar> -->
				<view class="">采购入库</view>
				<!-- <view class="version">{{vuex_config.productVersion}}</view> -->
			</view>
			<u-cell-group class="" :border="false">
				<navigator url="" open-type="navigate">
					<u-cell-item :arrow="false" title="采购单号">2002125411</u-cell-item>
				</navigator>
				<navigator>
					<u-cell-item :arrow="false" title="供应商">上海大华科技有限公司</u-cell-item>
				</navigator>
				<navigator url="" open-type="navigate">
					<u-cell-item :arrow="false" title="仓库">成品仓</u-cell-item>
				</navigator>
			</u-cell-group>
			<view style="height: 40px;" class="u-text-left">
				<view class="title">物料信息：</view>
			</view>
			<view class="tbl">
				<uni-table ref="table" :loading="loading" border stripe emptyText="暂无更多数据" @selection-change="selectionChange">
					<uni-tr>
						<uni-th width="120" align="center">物料编码</uni-th>
						<uni-th width="120" align="center">物料名称</uni-th>
						<uni-th align="center">地址</uni-th>
						<uni-th width="204" align="center">设置</uni-th>
					</uni-tr>
					<uni-tr v-for="(item, index) in tableData" :key="index">
						<uni-td>{{ item.date }}</uni-td>
						<uni-td>
							<view class="name">{{ item.name }}</view>
						</uni-td>
						<uni-td align="center">{{ item.address }}</uni-td>
						<uni-td>
							<view class="uni-group">
								<button class="uni-button" size="mini" type="primary">修改</button>
								<button class="uni-button" size="mini" type="warn">删除</button>
							</view>
						</uni-td>
					</uni-tr>
				</uni-table>
				<view class="uni-pagination-box"><uni-pagination show-icon :page-size="pageSize" :current="pageCurrent" :total="total" @change="change" /></view>
			</view>
		</view>
	</view>
</template>

<script>
import tableData from './tableData.js'
export default {
	data() {
		return {
			searchVal: '',
			tableData: [],
			// 每页数据量
			pageSize: 10,
			// 当前页
			pageCurrent: 1,
			// 数据总量
			total: 0,
			loading: false
		}
	},
	onLoad() {
		this.selectedIndexs = []
		this.getData(1)
	},
	methods: {
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

<style>
/* #ifndef H5 */
/* page {
	padding-top: 85px;
} */
/* #endif */
page {
	background-color: #f5f5f5;
}

.uni-group {
	display: flex;
	align-items: center;
}
</style>

