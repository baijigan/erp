<template>
  <div>
    <Chart :cdata="cdata" />
  </div>
</template>

<script>
import { getList } from '@/api/dashboard'
import Chart from './chart.vue';
export default {
  data () {
    return {
      cdata: {
        xData: ["绳头HJ30", "支架直板", "绳头ZH08", "电磁铁", "制动器", "绳头ZH06"],
        seriesData: [
          { value: 10, name: "绳头HJ30" },
          { value: 5, name: "支架直板" },
          { value: 15, name: "绳头ZH08" },
          { value: 25, name: "电磁铁" },
          { value: 20, name: "制动器" },
          { value: 35, name: "绳头ZH06" }
        ]
      }
    }
  },
  components: {
    Chart,
  },
  mounted () {
  },
  created() {
    //this.fetchData()
  },
  methods: {
    fetchData() {
      this.listLoading = true
      getList().then(response => {
        this.cdata = response.data
        this.listLoading = false
      })
    }
  }
}
</script>

<style lang="scss" scoped>
</style>