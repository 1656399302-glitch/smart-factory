<template>
  <div class="data-card u-grid-12">
    <div v-for="(item, index) in cardData" :key="index" class="data-card-item u-card u-card-hover u-col-12" v-reveal>
      <div class="data-card__title">{{ item.title }}</div>
      <div class="data-card__content">
        <svg-icon :icon-class="item.icon" class="data-card__icon" />
        <div class="data-card__value">
          <ICountUp :delay="delay" :endVal="item.value" :options="options" />
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ICountUp from "vue-countup-v2";
import { getHomeData } from "@/api/home";

export default {
  name: "DataCard",
  components: {
    ICountUp,
  },
  data() {
    return {
      cardData: [
        {
          title: "AR内容数量",
          value: 74,
          icon: "component",
        },
      ],
      delay: 1000,
      endVal: 100,
      options: {
        useEasing: true,
        useGrouping: true,
        separator: ",",
        decimal: ".",
        prefix: "",
        suffix: "",
      },
    };
  },
  computed: {},
  methods: {
    getData() {
      // listArContent().then((response) => {
      //   this.cardData[0].value = response.total;
      // });
      // listVrContent().then((response) => {
      //   this.cardData[1].value = response.total;
      // });
      // listCase().then((response) => {
      //   this.cardData[2].value = response.total;
      // });
      getHomeData().then((response) => {
        this.cardData[0].value = response.arContentCount;
      });
    },
  },
  created() {
    this.getData();
  },
};
</script>

<style scoped>
.data-card {
  gap: var(--spacing-md);
}

.data-card-item {
  border-radius: var(--radius-md);
  padding: var(--spacing-lg);
  background: var(--gradient-primary);
  transition: transform var(--transition-base), box-shadow var(--transition-base);
  box-shadow: var(--shadow-md);
}

.data-card-item:hover {
  transform: translateY(-6px);
  box-shadow: var(--shadow-hover);
}

.data-card__content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: var(--spacing-md);
}

.data-card__icon {
  width: 30px;
  height: 30px;
  color: var(--color-white);
}

.data-card__title {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--color-white);
}

.data-card__value {
  font-size: var(--font-size-2xl);
  font-weight: var(--font-weight-bold);
  color: var(--color-white);
}

@media (max-width: 576px) {
  .data-card-item {
    padding: var(--spacing-md);
  }

  .data-card__value {
    font-size: var(--font-size-xl);
  }
}

@media (min-width: 577px) and (max-width: 767px) {
  .data-card-item {
    padding: var(--spacing-md);
  }
}

@media (min-width: 768px) {
  .data-card-item {
    padding: var(--spacing-lg);
  }
}
</style>
