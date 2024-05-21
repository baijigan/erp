<template>
  <div>
    <Echart
      :options="options"
      id="centreLeft1Chart"
      height="3.55rem"
      width="6.25rem"
    ></Echart>
  </div>
</template>

<script>
import Echart from '@/components/echart'
export default {
  data () {
    return {
      options: {},
    };
  },
  components: {
    Echart,
  },
  props: {
    cdata: {
      type: Object,
      default: () => ({})
    },
  },
  watch: {
    cdata: {
      handler (newData) {
        this.options = {
    xAxis: {
        type: 'category',
        data: ['运行', '手动', '空闲', '报警', '关机']
    },
    yAxis: {
        type: 'value'
    },
    series: [{
        data: [120, 200, 150, 80, 70],
        type: 'bar',
        itemStyle: {                
                normal: {                    
                    color: function(params) {                     
                        var colorList = ['#cca272','#74608f','#d7a02b','#c8ba23',];
                        return colorList[params.dataIndex]
                    },
                    barBorderRadius:[18,18,0,0]
                }
            },
            barWidth:'45',				//---柱形宽度
	          barCategoryGap:'20%'		//---柱形间距
    }]
}
      },
      immediate: true,
      deep: true
    }
  }
};
</script>

<style lang="scss" scoped>
</style>