<template>
  <div id="centreRight1">
    <div class="bg-color-black">
      <div class="d-flex pt-2 pl-2">
        <span style="color:#5cd9e8">
          <icon name="chart-line"></icon>
        </span>
        <div class="d-flex">
          <span class="fs-xl text mx-2">实时销售订单明细</span>
        </div>
      </div>
      <div class="d-flex jc-center body-box" style="margin-top:4px !important">
        <dv-scroll-board :config="config" style="width:3.375rem;height:4.28rem" />
      </div>
    </div>
  </div>
</template>

<script>
import { getOrderList } from '@/api/dashboard/template'
export default {
  data() {
    return {
      config: {
        header: ["产品", "数量"],
        data: [ ['绳头HJ30', 28650.25], ['电磁铁', 15623.26], ['制动器', 16332.36], ['绳头ZH06', 5632.21], ['W11-机械三辊对称式卷板机', 28650], ['标准闸式剪板机 201', 28650]],
        rowNum: 7, //表格行数
        headerHeight: 35,
        headerBGC: "#0f1325", //表头
        oddRowBGC: "#0f1325", //奇数行
        evenRowBGC: "#171c33", //偶数行
        index: true,
        columnWidth: [60, 235, 90],
        align: ["center"]
      }
    };
  },
  components: {},
  mounted() {},
  created() {
    //this.timer = setInterval(this.fetchData, 500);
  },
  methods: {
    fetchData() {
      this.listLoading = true
      var self= this;
      getOrderList().then(response => {
        if(this.config.data.length==0 || this.config.data[0][0]!=response.data[0][0]){
          var config = JSON.parse(JSON.stringify(this.config));
          config.data= response.data;

          this.config= config;
          this.listLoading = false
        }
      })
    }
  }
};
</script>

<style lang="scss">
#centreRight1 {
  padding: 0.2rem;
  height: 5.125rem;
  min-width: 3.75rem;
  border-radius: 0.0625rem;
  .bg-color-black {
    height: 4.8125rem;
    border-radius: 0.125rem;
  }
  .text {
    color: #c3cbde;
  }
  .body-box {
    border-radius: 0.125rem;
    overflow: hidden;
  }
}
</style>