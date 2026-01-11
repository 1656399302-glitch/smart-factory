<template>
  <el-row :gutter="40" class="panel-group">
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('newVisitis')">
        <div class="card-panel-icon-wrapper icon-people">
          <svg-icon icon-class="peoples" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            访客
          </div>
          <count-to :start-val="0" :end-val="102400" :duration="2600" class="card-panel-num" />
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('messages')">
        <div class="card-panel-icon-wrapper icon-message">
          <svg-icon icon-class="message" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            消息
          </div>
          <count-to :start-val="0" :end-val="81212" :duration="3000" class="card-panel-num" />
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('purchases')">
        <div class="card-panel-icon-wrapper icon-money">
          <svg-icon icon-class="money" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            金额
          </div>
          <count-to :start-val="0" :end-val="9280" :duration="3200" class="card-panel-num" />
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('shoppings')">
        <div class="card-panel-icon-wrapper icon-shopping">
          <svg-icon icon-class="shopping" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            订单
          </div>
          <count-to :start-val="0" :end-val="13600" :duration="3600" class="card-panel-num" />
        </div>
      </div>
    </el-col>
  </el-row>
</template>

<script>
import CountTo from 'vue-count-to'

export default {
  components: {
    CountTo
  },
  methods: {
    handleSetLineChartData(type) {
      this.$emit('handleSetLineChartData', type)
    }
  }
}
</script>

<style lang="scss" scoped>
.panel-group {
  margin-top: var(--spacing-md);

  .card-panel-col {
    margin-bottom: var(--spacing-xl);
  }

  .card-panel {
    height: 108px;
    cursor: pointer;
    font-size: var(--font-size-xs);
    position: relative;
    overflow: hidden;
    color: var(--color-text-secondary);
    background: var(--color-bg-secondary);
    box-shadow: var(--shadow-sm);
    border-radius: var(--radius-md);
    transition: transform var(--transition-base), box-shadow var(--transition-base);

    &:hover {
      transform: translateY(-6px);
      box-shadow: var(--shadow-hover);

      .card-panel-icon-wrapper {
        color: var(--color-white);
      }

      .icon-people {
        background: var(--color-info);
      }

      .icon-message {
        background: var(--color-accent);
      }

      .icon-money {
        background: var(--color-error);
      }

      .icon-shopping {
        background: var(--color-success);
      }
    }

    .icon-people {
      color: var(--color-info);
      transition: color var(--transition-base);
    }

    .icon-message {
      color: var(--color-accent);
      transition: color var(--transition-base);
    }

    .icon-money {
      color: var(--color-error);
      transition: color var(--transition-base);
    }

    .icon-shopping {
      color: var(--color-success);
      transition: color var(--transition-base);
    }

    .card-panel-icon-wrapper {
      float: left;
      margin: var(--spacing-sm) 0 0 var(--spacing-sm);
      padding: var(--spacing-md);
      transition: all var(--transition-base);
      border-radius: var(--radius-md);
    }

    .card-panel-icon {
      float: left;
      font-size: 48px;
      transition: transform var(--transition-base);
    }

    .card-panel-description {
      float: right;
      font-weight: var(--font-weight-bold);
      margin: var(--spacing-xl);
      margin-left: 0px;

      .card-panel-text {
        line-height: var(--line-height-normal);
        color: var(--color-text-tertiary);
        font-size: var(--font-size-base);
        margin-bottom: var(--spacing-sm);
        transition: color var(--transition-base);
      }

      .card-panel-num {
        font-size: var(--font-size-xl);
        color: var(--color-text);
        transition: color var(--transition-base);
      }
    }
  }
}

@media (max-width: 576px) {
  .card-panel-description {
    display: none;
  }

  .card-panel-icon-wrapper {
    float: none !important;
    width: 100%;
    height: 100%;
    margin: 0 !important;

    .svg-icon {
      display: block;
      margin: var(--spacing-sm) auto !important;
      float: none !important;
    }
  }
}

@media (min-width: 577px) and (max-width: 767px) {
  .card-panel {
    height: 96px;

    .card-panel-icon-wrapper {
      margin: var(--spacing-xs) 0 0 var(--spacing-xs);
      padding: var(--spacing-sm);
    }

    .card-panel-icon {
      font-size: 40px;
    }

    .card-panel-description {
      margin: var(--spacing-lg);
    }
  }
}
</style>
