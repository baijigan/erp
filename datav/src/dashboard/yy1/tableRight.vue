<template>
  <div id="bottomRight">
    <div class="bg-color-black">
      <div class="d-flex pt-2 pl-2">
        <span style="color:#5cd9e8">
          <icon name="chart-area"></icon>
        </span>
        <div class="d-flex">
          <span class="fs-xl text mx-2">售后服务以及满意度统计图</span>
          <div class="decoration2">
            <dv-decoration-2 :reverse="true" style="width:5px;height:3.5rem;" />
          </div>
        </div>
      </div>
      <div style="display:xxxx">
        <dv-scroll-board :config="config" style="width:880px;height:220px" />
      </div>
    </div>
  </div>
</template>

<script>
import BottomRightChart from "./echart/bottom/bottomRightChart";
export default {
  data() {
    return {
      config: {
        header: ["类型", "报检人","设备号","零件号","工序","等待时间"],
        data: [
    ['行1列1', '行1列2', '行1列3', '行1列2', '行1列3', '行1列3'],
    ['行2列1', '行2列2', '行2列3', '行1列2', '行1列3', '行1列3'],
    ['行3列1', '行3列2', '行3列3', '行1列2', '行1列3', '行1列3'],
    ['行4列1', '行4列2', '行4列3', '行1列2', '行1列3', '行1列3'],
    ['行5列1', '行5列2', '行5列3', '行1列2', '行1列3', '行1列3'],
    ['行6列1', '行6列2', '行6列3', '行1列2', '行1列3', '行1列3'],
    ['行7列1', '行7列2', '行7列3', '行1列2', '行1列3', '行1列3'],
    ['行8列1', '行8列2', '行8列3', '行1列2', '行1列3', '行1列3'],
    ['行9列1', '行9列2', '行9列3', '行1列2', '行1列3', '行1列3'],
    ['行10列1', '行10列2', '行10列3', '行1列2', '行1列3', '行1列3']
  ],
        rowNum: 7, //表格行数
        headerHeight: 35,
        headerBGC: "#0f1325", //表头
        oddRowBGC: "#0f1325", //奇数行
        evenRowBGC: "#171c33", //偶数行
        index: true,
        columnWidth: [60, 235, 260, 260, 160, 160, 160],
        align: ["center"]
      }
    };
  },
  components: {
    BottomRightChart
  },
  mounted() {},
  methods: {}
};
</script>

<style lang="scss">
#bottomRight {
  padding: 0.2rem 0.2rem 0;
  height: 4.0rem;
  min-width: 3.75rem;
  border-radius: 0.0625rem;
  .bg-color-black {
    height: 95%;
    border-radius: 0.125rem;
  }
  .text {
    color: #c3cbde;
  } //下滑线动态
  .decoration2 {
    position: absolute;
    right: 0.125rem;
  }
  .chart-box {
    margin-top: 0.2rem;
    width: 2.125rem;
    height: 2.125rem;
    .active-ring-name {
      padding-top: 0.125rem;
    }
  }
}
</style>