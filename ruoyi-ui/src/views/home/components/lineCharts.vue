<template>
  <div class="lineCharts-box" ref="lineCharts"></div>
</template>

<script>
import * as echarts from "echarts";
import { getMonthlyRegistrations } from "@/api/home";
import { debounce, getLast6Months } from "@/utils";

export default {
  name: "LineCharts",
  data() {
    return {
      option: {
        dataset: [
          {
            source: [
              ["2025-3", 41],
              ["2025-4", 86],
              ["2025-5", 24],
              ["2025-6", 41],
              ["2025-7", 86],
              ["2025-8", 24],
              ["2025-9", 41],
            ],
          },
        ],
        color: ["#26deca", "#00DDFF", "#37A2FF", "#FF0087", "#FFBF00"],
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "cross",
            label: {
              backgroundColor: "#6a7985",
            },
          },
        },
        legend: {
          data: ["注册量"],
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: "5%",
          containLabel: true,
        },
        xAxis: [
          {
            type: "category",
            boundaryGap: false,
          },
        ],
        yAxis: [
          {
            type: "value",
          },
        ],
        series: [
          {
            name: "注册量",
            type: "line",
            stack: "Total",
            smooth: true,
            // 是否显示交叉点
            // showSymbol: false,
            areaStyle: {
              color: {
                type: "linear",
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [
                  {
                    offset: 0.25,
                    color: "#26deca",
                  },
                  {
                    offset: 1,
                    color: "#fff",
                  },
                ],
              },
            },
            emphasis: {
              focus: "series",
            },
          },
        ],
      },
    };
  },
  methods: {
    monthlyRegistrations() {
      const { startDate, endDate } = getLast6Months();
      getMonthlyRegistrations(startDate, endDate).then((response) => {
        this.$set(
          this.option.dataset[0],
          "source",
          response.data.map((item) => [item.yearMonth, item.count])
        );
      });
    },
  },
  watch: {
    option: {
      handler() {
        if (this.chart) this.chart.setOption(this.option, true);
      },
      deep: true,
    },
  },
  created() {
    this.monthlyRegistrations();
  },
  mounted() {
    this.chart = echarts.init(this.$refs.lineCharts);
    if (this.option.dataset[0].source.length > 0) {
      this.chart.setOption(this.option);
    }
    this.resizeHandler = debounce(() => this.chart.resize(), 200);
    window.addEventListener("resize", this.resizeHandler);
  },
  beforeDestroy() {
    window.removeEventListener("resize", this.resizeHandler);
    this.chart?.dispose();
  },
};
</script>
