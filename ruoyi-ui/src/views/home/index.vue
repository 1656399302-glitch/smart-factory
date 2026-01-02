<template>
  <div class="app-container home u-container">
    <el-card class="home-card-box u-card u-card-hover" v-reveal>
      <userCard />
    </el-card>
    <el-card class="home-card-box u-card u-card-hover" v-reveal>
      <dataCard />
    </el-card>
    <div class="charts-box u-grid-12">
      <el-card class="home-card-box u-card u-card-hover u-col-12 u-col-md-5" v-reveal>
        <PieCharts class="pieCharts" />
      </el-card>
      <el-card class="home-card-box u-card u-card-hover u-col-12 u-col-md-7" v-reveal>
        <LineCharts class="lineCharts" />
      </el-card>
    </div>
  </div>
</template>

<script>
import userCard from "./components/userCard.vue";
import dataCard from "./components/dataCard.vue";
import PieCharts from "./components/pieCharts.vue";
import LineCharts from "./components/lineCharts.vue";

export default {
  components: {
    userCard,
    dataCard,
    PieCharts,
    LineCharts,
  },
  name: "Index",
  data() {
    return {
      user: {},
      title: process.env.VUE_APP_TITLE,
    };
  },
  created() {},
  methods: {
    goTarget(href) {
      window.open(href, "_blank");
    },
  },
};
</script>

<style scoped lang="scss">
.home {
  padding: var(--spacing-lg);

  .home-card-box {
    width: 100%;
    border-radius: var(--radius-md);
    margin-bottom: var(--spacing-lg);
    overflow: hidden;
    box-shadow: var(--shadow-sm);
    transition: transform var(--transition-base), box-shadow var(--transition-base);
  }

  blockquote {
    padding: var(--spacing-sm) var(--spacing-md);
    margin: 0 0 var(--spacing-md);
    font-size: var(--font-size-lg);
    border-left: 5px solid var(--color-border);
  }

  hr {
    margin-top: var(--spacing-md);
    margin-bottom: var(--spacing-md);
    border: 0;
    border-top: 1px solid var(--color-border);
  }

  .col-item {
    margin-bottom: var(--spacing-md);
  }

  ul {
    padding: 0;
    margin: 0;
  }

  font-family: "open sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
  font-size: var(--font-size-sm);
  color: var(--color-text-secondary);
  overflow-x: hidden;

  ul {
    list-style-type: none;
  }

  h4 {
    margin-top: 0px;
  }

  h2 {
    margin-top: var(--spacing-md);
    font-size: var(--font-size-3xl);
    font-weight: var(--font-weight-normal);
  }

  p {
    margin-top: var(--spacing-sm);

    b {
      font-weight: var(--font-weight-bold);
    }
  }

  .update-log {
    ol {
      display: block;
      list-style-type: decimal;
      margin-block-start: 1em;
      margin-block-end: 1em;
      margin-inline-start: 0;
      margin-inline-end: 0;
      padding-inline-start: 40px;
    }
  }
}

.charts-box {
  .home-card-box {
    margin-bottom: 0;
  }

  .pieCharts {
    height: 300px;
  }

  .lineCharts {
    height: 300px;
  }
}

@media (max-width: 576px) {
  .home {
    padding: var(--spacing-md);
  }

  .lineCharts {
    height: 260px;
  }
}

@media (min-width: 577px) and (max-width: 767px) {
  .lineCharts {
    height: 280px;
  }
}

@media (min-width: 768px) {
  .charts-box {
    gap: var(--spacing-md);
  }
}
</style>
