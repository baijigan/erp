<template>
  <div id="center">
    <div class="top">
      <div class="sub-title">
              <span class="title-text" style="font-size:34px">裕鸢数字化工厂</span>
              <dv-decoration-6
                class="title-bototm"
                :reverse="true"
                :color="['#50e3c2', '#67a1e5']"
                style="width:3.125rem;height:.1rem;"
              />
        </div>
    </div>
    <div class="up">
      <div class="bg-color-black item" v-for="item in titleItem" :key="item.title" style="display:none">
        <p class="ml-3 colorBlue fw-b">{{item.title}}</p>
        <div>
          <dv-digital-flop :config="item.number" style="width:185px;height:.625rem;" />
        </div>
      </div>
      <div style="display: grid;grid-template-columns: 50% 50%; height: 240px; grid-gap:20px ; margin-top:40px; ">
          <div class="lbl">
            今日计划开工：<span class="num">150</span> 件
          </div>
          <div class="lbl">
            今日计划完工：<span class="num">150</span> 件
          </div>
          <div class="lbl">
            今日实际开工：<span class="num">150</span> 件
          </div>
           <div class="lbl">
            今日实际完工：<span class="num">150</span> 件
          </div>
          <div class="lbl">
            开工率：<span class="num">150</span> %
          </div>
          <div class="lbl">
            完工率：<span class="num">150</span> %
          </div>
      </div>


    </div>
    <div class="down" style="display:none"> 
      <div class="ranking bg-color-black">
        <span style="color:#5cd9e8">
          <icon name="align-left"></icon>
        </span>
        <span class="fs-xl text mx-2 mb-1">年度销售业绩排行榜</span>
        <dv-scroll-ranking-board :config="ranking" style="height:2.75rem" />
      </div>
      <div class="percent">
        <div class="item bg-color-black">
          <span>今日任务通过率</span>
          <CenterChart :id="rate[0].id" :tips="rate[0].tips" :colorObj="rate[0].colorData" />
        </div>
        <div class="item bg-color-black">
          <span>今日任务达标率</span>
          <CenterChart :id="rate[1].id" :tips="rate[1].tips" :colorObj="rate[1].colorData" />
        </div>
        <div class="water">
          <dv-water-level-pond :config="water" style="height: 1.5rem" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import CenterChart from "./echart/center/centerChartRate";
import { getOrderTotal, getSalesPersonTotal } from '@/api/dashboard/template'
export default {
  data () {
    return {
      titleItem: [
        {
          title: "本年销售总额",
          number: {
            number: [120],
            toFixed: 1,
            content: "{nt}"
          }
        },
        {
          title: "去年同期销售总额",
          number: {
            number: [18],
            toFixed: 1,
            content: "{nt}"
          }
        },
        {
          title: "销售指数",
          number: {
            number: [2],
            toFixed: 1,
            content: "{nt}"
          }
        },
        {
          title: "本年欠款总额",
          number: {
            number: [14],
            toFixed: 1,
            content: "{nt}"
          }
        },
        {
          title: "去年同期欠款总额",
          number: {
            number: [106],
            toFixed: 1,
            content: "{nt}"
          }
        },
        {
          title: "欠款指数2",
          number: {
            number: [100],
            toFixed: 1,
            content: "{nt}"
          }
        },
        {
          title: "欠款指数3",
          number: {
            number: [100],
            toFixed: 1,
            content: "{nt}"
          }
        },
        {
          title: "欠款指数",
          number: {
            number: [100],
            toFixed: 1,
            content: "{nt}"
          }
        }
      ],
      ranking: {
        data: [
          {
            name: "周口",
            value: 55
          },
          {
            name: "南阳",
            value: 120
          },
          {
            name: "西峡",
            value: 78
          },
          {
            name: "驻马店",
            value: 66
          },
          {
            name: "新乡",
            value: 80
          },
          {
            name: "新乡2",
            value: 80
          },
          {
            name: "新乡3",
            value: 80
          },
          {
            name: "新乡4",
            value: 80
          },
          {
            name: "新乡5",
            value: 80
          },
          {
            name: "新乡6",
            value: 80
          },
        ],
        carousel: "single",
        unit: "人"
      },
      water: {
        data: [24, 45],
        shape: "roundRect",
        formatter: "{value}%",
        waveNum: 3
      },
      // 通过率和达标率的组件复用数据
      rate: [
        {
          id: "centerRate1",
          tips: 60,
          colorData: {
            textStyle: "#3fc0fb",
            series: {
              color: ["#00bcd44a", "transparent"],
              dataColor: {
                normal: "#03a9f4",
                shadowColor: "#97e2f5"
              }
            }
          }
        },
        {
          id: "centerRate2",
          tips: 40,
          colorData: {
            textStyle: "#67e0e3",
            series: {
              color: ["#faf3a378", "transparent"],
              dataColor: {
                normal: "#ff9800",
                shadowColor: "#fcebad"
              }
            }
          }
        }
      ]
    };
  },
  components: {
    CenterChart
    // centerChart1,
    // centerChart2
  },
  created() {
    // this.timer = setInterval(this.fetchData, 500);
  },
  methods: {
    fetchData() {
      this.listLoading = true
      getOrderTotal().then(response => {
          this.titleItem= response.data;
          this.listLoading = false
      })

      getSalesPersonTotal().then(response => {
        var s1= JSON.stringify(this.ranking);
        var s2= JSON.stringify(response.data);
        if(s1!=s2){
          this.ranking= response.data;
          this.listLoading = false
        }
      })
    }
  }
};
</script>

<style lang="scss" scoped>

#center {
  #dv-digital-flop{ width: 185px !important; }
  #dv-digital-flop canvas{ width:185px !important;}
  display: flex;
  flex-direction: column;

  .lbl{
    font-size: 24px;

    .num{
      font-size: 26px;
      color: aqua;
    }
  }

  .top {
    width: 100%;

  }
  .up {
    width: 100%;
    display: flex;
    flex-wrap: wrap;
    justify-content: space-between;
    .item {
      border-radius: 0.0625rem;
      padding-top: 0.2rem;
      margin-top: 0.1rem;
      width: 32%;
      height: 0.875rem;
    }
  }
  .down {
    padding: 0.07rem 0.05rem;
    padding-bottom: 0;
    width: 100%;
    display: flex;
    height: 3.1875rem;
    justify-content: space-between;
    .bg-color-black {
      border-radius: 0.0625rem;
    }
    .ranking {
      padding: 0.125rem;
      width: 59%;
    }
    .percent {
      width: 40%;
      display: flex;
      flex-wrap: wrap;
      .item {
        width: 50%;
        height: 1.5rem;
        span {
          margin-top: 0.0875rem;
          display: flex;
          justify-content: center;
        }
      }
      .water {
        width: 100%;
      }
    }
  }
}
</style>