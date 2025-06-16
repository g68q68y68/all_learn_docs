<template>
  <el-tabs
      v-model="activeName"
      class="demo-tabs"
      @tab-click="handleClick"
  >
    <!-- 第一页：图表 -->
    <el-tab-pane label="User" name="first">
      <div class="chart-contain">
        <div ref="chart01" class="demo"></div>
        <div class="demo"></div>
        <div class="demo"></div>
        <div class="demo"></div>
      </div>
    </el-tab-pane>

    <!-- 第二页：配置页 -->
    <el-tab-pane label="Config" name="second">
      下行
    </el-tab-pane>
  </el-tabs>
</template>

<script setup>
import { ref, onMounted, nextTick, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'

const activeName = ref('first')
const chart01 = ref(null)
let myChart = null

const chartOption = {
  xAxis: {
    type: 'category',
    data: ['Mon','Tue','Wed','Thu','Fri','Sat','Sun']
  },
  yAxis: { type: 'value' },
  series: [{
    data: [150,230,224,218,135,147,260],
    type: 'line'
  }]
}

function initChart() {
  if (chart01.value) {
    myChart = echarts.init(chart01.value)
    myChart.setOption(chartOption)
  }
}

onMounted(() => {
  initChart()
})

function handleClick(tab) {
  if (tab.name === 'first') {
    nextTick(() => {
      myChart && myChart.resize()
    })
  }
}

onBeforeUnmount(() => {
  if (myChart) {
    myChart.dispose()
    myChart = null
  }
})
</script>

<style scoped lang="less">
.demo-tabs > .el-tabs__content {
  padding: 32px;
  color: #6b778c;
  font-size: 32px;
  font-weight: 600;
}

.chart-contain {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  align-content: center;
  background-color: #ffd04b;

  .demo {
    width: 50%;
    height: 400px;
    box-sizing: border-box;
    background-color: #2d3a4b;
    border: 3px solid #f0f2f5;
    padding: 5px;
  }
}

.el-tabs__content {
  padding: 0 !important;
}
</style>
