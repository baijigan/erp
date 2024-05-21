<template>
  <div>
    <Echart
      :options="options"
      id="centreLeft2Chart"
      height="5.625rem"
      width="8.125rem"
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
    tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
    },
    legend: {
        orient: 'vertical',
        left: 20,
        top: 40,
        data: ['运行', '手动', '空闲', '报警', '关机']
    },
    series: [
        {
            name: '访问来源',
            type: 'pie',
            radius: ['33%', '55%'],
            center: ['50%', '30%'],
            label: {
                position: 'inner',
                formatter: '{d}%'
            },
            data: [
                {value: 335, name: '运行'},
                {value: 310, name: '手动'},
                {value: 234, name: '空闲'},
                {value: 135, name: '报警'},
                {value: 1048, name: '关机'}
            ]
        }
    ]
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