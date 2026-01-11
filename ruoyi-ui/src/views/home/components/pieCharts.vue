<template>
  <div class="pieCharts-box" ref="pieChart"></div>
</template>

<script>
import * as echarts from "echarts";
import { getHomeData } from "@/api/home";
import { debounce } from "@/utils";

export default {
  name: "PieCharts",
  data() {
    return {
      dataSrc: [],
      option: {
        dataset: {
          source: [
            ["AR内容数量", 41],
          ],
        },
        tooltip: {
          trigger: "item",
        },
        legend: {
          bottom: "5%",
          left: "center",
          itemStyle: {
            borderWidth: 0,
          },
        },
        series: [
          {
            color: [
              "#5da8ff",
              "#00DDFF",
              "#fedc69",
              "#26deca",
              "#ff6b8a",
              "#ff9f43",
              "#AE84FF",
            ],
            name: "各模块内容分布",
            type: "pie",
            radius: ["40%", "70%"],
            center: ["50%", "40%"], // 调整饼图的位置
            avoidLabelOverlap: false,
            padAngle: 2,
            itemStyle: {
              borderRadius: 10,
              borderColor: "#fff",
              borderWidth: 1,
            },
            label: {
              show: false,
              position: "center",
            },
            emphasis: {
              label: {
                show: true,
                fontSize: "12",
              },
            },
            labelLine: {
              show: false,
            },
          },
        ],
      },
    };
  },
  computed: {},
  methods: {
    getData() {
      getHomeData().then((response) => {
        const data = [
          ["AR内容数量", response.arContentCount],
        ];
        this.dataSrc = data;
      });
    },
  },
  watch: {
    dataSrc: {
      handler(newVal) {
        this.option.dataset.source = newVal;
        if (this.chart) {
          this.chart.setOption(this.option);
        }
      },
    },
  },
  created() {
    this.getData();
  },
  mounted() {
    const chartDom = this.$refs.pieChart;
    this.chart = echarts.init(chartDom);
    if (this.option.dataset.source.length > 0) {
      this.chart.setOption(this.option);
    }
    this.resizeHandler = debounce(() => this.chart.resize(), 200);

    window.addEventListener("resize", this.resizeHandler);
  },
  beforeDestroy() {
    if (this.chart) {
      window.removeEventListener("resize", this.resizeHandler);
      this.chart.dispose();
    }
  },
};
</script>
