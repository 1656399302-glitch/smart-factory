<template>
  <div ref="rightPanel" class="rightPanel-container">
    <div class="rightPanel-background" />
    <div class="rightPanel">
      <div class="rightPanel-items">
        <slot />
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'RightPanel',
  props: {
    clickNotClose: {
      default: false,
      type: Boolean
    }
  },
  computed: {
    show: {
      get() {
        return this.$store.state.settings.showSettings
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'showSettings',
          value: val
        })
      }
    }
  },
  watch: {
    show(value) {
      if (value && !this.clickNotClose) {
        this.addEventClick()
      }
    }
  },
  mounted() {
    this.addEventClick()
  },
  beforeDestroy() {
    const elx = this.$refs.rightPanel
    elx.remove()
  },
  methods: {
    addEventClick() {
      window.addEventListener('click', this.closeSidebar)
    },
    closeSidebar(evt) {
      const parent = evt.target.closest('.el-drawer__body')
      if (!parent) {
        this.show = false
        window.removeEventListener('click', this.closeSidebar)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.rightPanel-background {
  position: fixed;
  top: 0;
  left: 0;
  opacity: 0;
  transition: opacity var(--transition-base);
  background: rgba(0, 0, 0, 0.2);
  z-index: -1;
}

.rightPanel {
  width: 100%;
  max-width: 260px;
  height: 100vh;
  position: fixed;
  top: 0;
  right: 0;
  box-shadow: var(--shadow-lg);
  transition: transform var(--transition-base), box-shadow var(--transition-base);
  transform: translate(100%);
  background: var(--color-bg-secondary);
  z-index: 40000;
}

.handle-button {
  width: 48px;
  height: 48px;
  position: absolute;
  left: -48px;
  text-align: center;
  font-size: var(--font-size-2xl);
  border-radius: var(--radius-md) 0 0 var(--radius-md) !important;
  z-index: 0;
  pointer-events: auto;
  cursor: pointer;
  color: var(--color-white);
  line-height: 48px;
  transition: transform var(--transition-fast), background-color var(--transition-fast);

  &:hover {
    transform: scale(1.05);
  }

  i {
    font-size: var(--font-size-2xl);
    line-height: 48px;
    transition: transform var(--transition-fast);

    &:hover {
      transform: rotate(90deg);
    }
  }
}
</style>
