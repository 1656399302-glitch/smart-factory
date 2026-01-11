<template>
  <div class="icon-dialog">
    <el-dialog
      v-bind="$attrs"
      width="980px"
      :modal-append-to-body="false"
      v-on="$listeners"
      @open="onOpen"
      @close="onClose"
    >
      <div slot="title">
        选择图标
        <el-input
          v-model="key"
          size="mini"
          :style="{width: '260px'}"
          placeholder="请输入图标名称"
          prefix-icon="el-icon-search"
          clearable
        />
      </div>
      <ul class="icon-ul">
        <li
          v-for="icon in iconList"
          :key="icon"
          :class="active===icon?'active-item':''"
          @click="onSelect(icon)"
        >
          <i :class="icon" />
          <div>{{ icon }}</div>
        </li>
      </ul>
    </el-dialog>
  </div>
</template>
<script>
import iconList from '@/utils/generator/icon.json'

const originList = iconList.map(name => `el-icon-${name}`)

export default {
  inheritAttrs: false,
  props: ['current'],
  data() {
    return {
      iconList: originList,
      active: null,
      key: ''
    }
  },
  watch: {
    key(val) {
      if (val) {
        this.iconList = originList.filter(name => name.indexOf(val) > -1)
      } else {
        this.iconList = originList
      }
    }
  },
  methods: {
    onOpen() {
      this.active = this.current
      this.key = ''
    },
    onClose() {},
    onSelect(icon) {
      this.active = icon
      this.$emit('select', icon)
      this.$emit('update:visible', false)
    }
  }
}
</script>
<style lang="scss" scoped>
.icon-ul {
  margin: 0;
  padding: 0;
  font-size: 0;
  li {
    list-style-type: none;
    text-align: center;
    font-size: var(--font-size-sm);
    display: inline-block;
    width: 16.66%;
    box-sizing: border-box;
    height: 108px;
    padding: var(--spacing-md) var(--spacing-xs) var(--spacing-xs) var(--spacing-xs);
    cursor: pointer;
    overflow: hidden;
    border-radius: var(--radius-sm);
    transition: background-color var(--transition-fast), color var(--transition-fast), transform var(--transition-fast);
    &:hover {
      background: var(--color-bg);
      transform: scale(1.05);
    }
    &.active-item{
      background: var(--color-accent-light);
      color: var(--color-accent);
    }
    > i {
      font-size: var(--font-size-3xl);
      line-height: 50px;
      transition: transform var(--transition-fast);
    }

    &:hover > i {
      transform: scale(1.1);
    }
  }
}
.icon-dialog {
  ::v-deep .el-dialog {
    border-radius: var(--radius-md);
    margin-bottom: 0;
    margin-top: 4vh !important;
    display: flex;
    flex-direction: column;
    max-height: 92vh;
    overflow: hidden;
    box-sizing: border-box;
    box-shadow: var(--shadow-lg);
    .el-dialog__header {
      padding-top: var(--spacing-sm);
    }
    .el-dialog__body {
      margin: 0 var(--spacing-md) var(--spacing-md) var(--spacing-md);
      padding: 0;
      overflow: auto;
    }
  }
}
</style>
