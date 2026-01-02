<template>
  <div class="container">
    <div class="left-board">
      <div class="logo-wrapper">
        <div class="logo">
          <img :src="logo" alt="logo"> Form Generator
        </div>
      </div>
      <el-scrollbar class="left-scrollbar">
        <div class="components-list">
          <div class="components-title">
            <svg-icon icon-class="component" />输入型组件
          </div>
          <draggable
            class="components-draggable"
            :list="inputComponents"
            :group="{ name: 'componentsGroup', pull: 'clone', put: false }"
            :clone="cloneComponent"
            draggable=".components-item"
            :sort="false"
            @end="onEnd"
          >
            <div
              v-for="(element, index) in inputComponents" :key="index" class="components-item"
              @click="addComponent(element)"
            >
              <div class="components-body">
                <svg-icon :icon-class="element.tagIcon" />
                {{ element.label }}
              </div>
            </div>
          </draggable>
          <div class="components-title">
            <svg-icon icon-class="component" />选择型组件
          </div>
          <draggable
            class="components-draggable"
            :list="selectComponents"
            :group="{ name: 'componentsGroup', pull: 'clone', put: false }"
            :clone="cloneComponent"
            draggable=".components-item"
            :sort="false"
            @end="onEnd"
          >
            <div
              v-for="(element, index) in selectComponents"
              :key="index"
              class="components-item"
              @click="addComponent(element)"
            >
              <div class="components-body">
                <svg-icon :icon-class="element.tagIcon" />
                {{ element.label }}
              </div>
            </div>
          </draggable>
          <div class="components-title">
            <svg-icon icon-class="component" /> 布局型组件
          </div>
          <draggable
            class="components-draggable" :list="layoutComponents"
            :group="{ name: 'componentsGroup', pull: 'clone', put: false }" :clone="cloneComponent"
            draggable=".components-item" :sort="false" @end="onEnd"
          >
            <div
              v-for="(element, index) in layoutComponents" :key="index" class="components-item"
              @click="addComponent(element)"
            >
              <div class="components-body">
                <svg-icon :icon-class="element.tagIcon" />
                {{ element.label }}
              </div>
            </div>
          </draggable>
        </div>
      </el-scrollbar>
    </div>

    <div class="center-board">
      <div class="action-bar">
        <el-button icon="el-icon-download" type="text" @click="download">
          导出vue文件
        </el-button>
        <el-button class="copy-btn-main" icon="el-icon-document-copy" type="text" @click="copy">
          复制代码
        </el-button>
        <el-button class="delete-btn" icon="el-icon-delete" type="text" @click="empty">
          清空
        </el-button>
      </div>
      <el-scrollbar class="center-scrollbar">
        <el-row class="center-board-row" :gutter="formConf.gutter">
          <el-form
            :size="formConf.size"
            :label-position="formConf.labelPosition"
            :disabled="formConf.disabled"
            :label-width="formConf.labelWidth + 'px'"
          >
            <draggable class="drawing-board" :list="drawingList" :animation="340" group="componentsGroup">
              <draggable-item
                v-for="(element, index) in drawingList"
                :key="element.renderKey"
                :drawing-list="drawingList"
                :element="element"
                :index="index"
                :active-id="activeId"
                :form-conf="formConf"
                @activeItem="activeFormItem"
                @copyItem="drawingItemCopy"
                @deleteItem="drawingItemDelete"
              />
            </draggable>
            <div v-show="!drawingList.length" class="empty-info">
              从左侧拖入或点选组件进行表单设计
            </div>
          </el-form>
        </el-row>
      </el-scrollbar>
    </div>

    <right-panel
      :active-data="activeData"
      :form-conf="formConf"
      :show-field="!!drawingList.length"
      @tag-change="tagChange"
    />

    <code-type-dialog
      :visible.sync="dialogVisible"
      title="选择生成类型"
      :show-file-name="showFileName"
      @confirm="generate"
    />
    <input id="copyNode" type="hidden">
  </div>
</template>

<script>
import draggable from 'vuedraggable'
import beautifier from 'js-beautify'
import ClipboardJS from 'clipboard'
import render from '@/utils/generator/render'
import RightPanel from './RightPanel'
import { inputComponents, selectComponents, layoutComponents, formConf } from '@/utils/generator/config'
import { beautifierConf, titleCase } from '@/utils/index'
import { makeUpHtml, vueTemplate, vueScript, cssStyle } from '@/utils/generator/html'
import { makeUpJs } from '@/utils/generator/js'
import { makeUpCss } from '@/utils/generator/css'
import drawingDefault from '@/utils/generator/drawingDefault'
import logo from '@/assets/logo/logo.png'
import CodeTypeDialog from './CodeTypeDialog'
import DraggableItem from './DraggableItem'

let oldActiveId
let tempActiveData

export default {
  components: {
    draggable,
    render,
    RightPanel,
    CodeTypeDialog,
    DraggableItem
  },
  data() {
    return {
      logo,
      idGlobal: 100,
      formConf,
      inputComponents,
      selectComponents,
      layoutComponents,
      labelWidth: 100,
      drawingList: drawingDefault,
      drawingData: {},
      activeId: drawingDefault[0].formId,
      drawerVisible: false,
      formData: {},
      dialogVisible: false,
      generateConf: null,
      showFileName: false,
      activeData: drawingDefault[0]
    }
  },
  created() {
    // 防止 firefox 下 拖拽 会新打卡一个选项卡
    document.body.ondrop = event => {
      event.preventDefault()
      event.stopPropagation()
    }
  },
  watch: {
    // eslint-disable-next-line func-names
    'activeData.label': function (val, oldVal) {
      if (
        this.activeData.placeholder === undefined
        || !this.activeData.tag
        || oldActiveId !== this.activeId
      ) {
        return
      }
      this.activeData.placeholder = this.activeData.placeholder.replace(oldVal, '') + val
    },
    activeId: {
      handler(val) {
        oldActiveId = val
      },
      immediate: true
    }
  },
  mounted() {
    const clipboard = new ClipboardJS('#copyNode', {
      text: trigger => {
        const codeStr = this.generateCode()
        this.$notify({
          title: '成功',
          message: '代码已复制到剪切板，可粘贴。',
          type: 'success'
        })
        return codeStr
      }
    })
    clipboard.on('error', e => {
      this.$message.error('代码复制失败')
    })
  },
  methods: {
    activeFormItem(element) {
      this.activeData = element
      this.activeId = element.formId
    },
    onEnd(obj, a) {
      if (obj.from !== obj.to) {
        this.activeData = tempActiveData
        this.activeId = this.idGlobal
      }
    },
    addComponent(item) {
      const clone = this.cloneComponent(item)
      this.drawingList.push(clone)
      this.activeFormItem(clone)
    },
    cloneComponent(origin) {
      const clone = JSON.parse(JSON.stringify(origin))
      clone.formId = ++this.idGlobal
      clone.span = formConf.span
      clone.renderKey = +new Date() // 改变renderKey后可以实现强制更新组件
      if (!clone.layout) clone.layout = 'colFormItem'
      if (clone.layout === 'colFormItem') {
        clone.vModel = `field${this.idGlobal}`
        clone.placeholder !== undefined && (clone.placeholder += clone.label)
        tempActiveData = clone
      } else if (clone.layout === 'rowFormItem') {
        delete clone.label
        clone.componentName = `row${this.idGlobal}`
        clone.gutter = this.formConf.gutter
        tempActiveData = clone
      }
      return tempActiveData
    },
    AssembleFormData() {
      this.formData = {
        fields: JSON.parse(JSON.stringify(this.drawingList)),
        ...this.formConf
      }
    },
    generate(data) {
      const func = this[`exec${titleCase(this.operationType)}`]
      this.generateConf = data
      func && func(data)
    },
    execRun(data) {
      this.AssembleFormData()
      this.drawerVisible = true
    },
    execDownload(data) {
      const codeStr = this.generateCode()
      const blob = new Blob([codeStr], { type: 'text/plain;charset=utf-8' })
      this.$download.saveAs(blob, data.fileName)
    },
    execCopy(data) {
      document.getElementById('copyNode').click()
    },
    empty() {
      this.$confirm('确定要清空所有组件吗？', '提示', { type: 'warning' }).then(
        () => {
          this.drawingList = []
        }
      )
    },
    drawingItemCopy(item, parent) {
      let clone = JSON.parse(JSON.stringify(item))
      clone = this.createIdAndKey(clone)
      parent.push(clone)
      this.activeFormItem(clone)
    },
    createIdAndKey(item) {
      item.formId = ++this.idGlobal
      item.renderKey = +new Date()
      if (item.layout === 'colFormItem') {
        item.vModel = `field${this.idGlobal}`
      } else if (item.layout === 'rowFormItem') {
        item.componentName = `row${this.idGlobal}`
      }
      if (Array.isArray(item.children)) {
        item.children = item.children.map(childItem => this.createIdAndKey(childItem))
      }
      return item
    },
    drawingItemDelete(index, parent) {
      parent.splice(index, 1)
      this.$nextTick(() => {
        const len = this.drawingList.length
        if (len) {
          this.activeFormItem(this.drawingList[len - 1])
        }
      })
    },
    generateCode() {
      const { type } = this.generateConf
      this.AssembleFormData()
      const script = vueScript(makeUpJs(this.formData, type))
      const html = vueTemplate(makeUpHtml(this.formData, type))
      const css = cssStyle(makeUpCss(this.formData))
      return beautifier.html(html + script + css, beautifierConf.html)
    },
    download() {
      this.dialogVisible = true
      this.showFileName = true
      this.operationType = 'download'
    },
    run() {
      this.dialogVisible = true
      this.showFileName = false
      this.operationType = 'run'
    },
    copy() {
      this.dialogVisible = true
      this.showFileName = false
      this.operationType = 'copy'
    },
    tagChange(newTag) {
      newTag = this.cloneComponent(newTag)
      newTag.vModel = this.activeData.vModel
      newTag.formId = this.activeId
      newTag.span = this.activeData.span
      delete this.activeData.tag
      delete this.activeData.tagIcon
      delete this.activeData.document
      Object.keys(newTag).forEach(key => {
        if (this.activeData[key] !== undefined
          && typeof this.activeData[key] === typeof newTag[key]) {
          newTag[key] = this.activeData[key]
        }
      })
      this.activeData = newTag
      this.updateDrawingList(newTag, this.drawingList)
    },
    updateDrawingList(newTag, list) {
      const index = list.findIndex(item => item.formId === this.activeId)
      if (index > -1) {
        list.splice(index, 1, newTag)
      } else {
        list.forEach(item => {
          if (Array.isArray(item.children)) this.updateDrawingList(newTag, item.children)
        })
      }
    }
  }
}
</script>

<style lang='scss'>
.editor-tabs{
  background: var(--color-primary-dark);
  .el-tabs__header{
    margin: 0;
    border-bottom-color: var(--color-primary-dark);
  }
  .el-tabs__nav{
    border-color: var(--color-primary-dark);
  }
  .el-tabs__item{
    height: 32px;
    line-height: 32px;
    color: var(--color-text-tertiary);
    border-left: 1px solid var(--color-primary-dark) !important;
    background: var(--color-primary-light);
    margin-right: var(--spacing-xs);
    user-select: none;
    transition: background-color var(--transition-fast), color var(--transition-fast);
  }
  .el-tabs__item.is-active{
    background: var(--color-primary);
    border-bottom-color: var(--color-primary)!important;
    color: var(--color-white);
  }
  .el-icon-edit{
    color: var(--color-warning);
    transition: color var(--transition-fast);
  }
  .el-icon-document{
    color: var(--color-warning);
    transition: color var(--transition-fast);
  }
}

.right-scrollbar {
  .el-scrollbar__view {
    padding: var(--spacing-sm) var(--spacing-md) var(--spacing-md) var(--spacing-md);
  }
}
.left-scrollbar .el-scrollbar__wrap {
  box-sizing: border-box;
  overflow-x: hidden !important;
  margin-bottom: 0 !important;
}
.center-tabs{
  .el-tabs__header{
    margin-bottom: 0!important;
  }
  .el-tabs__item{
    width: 50%;
    text-align: center;
    transition: color var(--transition-fast);
  }
  .el-tabs__nav{
    width: 100%;
  }
}
.reg-item{
  padding: var(--spacing-sm) var(--spacing-xs);
  background: var(--color-bg);
  position: relative;
  border-radius: var(--radius-sm);
  transition: background-color var(--transition-fast), transform var(--transition-fast);
  &:hover {
    transform: translateY(-2px);
    background: var(--color-accent-light);
  }
  .close-btn{
    position: absolute;
    right: -6px;
    top: -6px;
    display: block;
    width: 16px;
    height: 16px;
    line-height: 16px;
    background: rgba(0, 0, 0, 0.2);
    border-radius: var(--radius-full);
    color: var(--color-white);
    text-align: center;
    z-index: 1;
    cursor: pointer;
    font-size: var(--font-size-xs);
    transition: background-color var(--transition-fast), transform var(--transition-fast);
    &:hover{
      background: var(--color-error);
      transform: scale(1.2);
    }
  }
  & + .reg-item{
    margin-top: var(--spacing-md);
  }
}
.action-bar{
  & .el-button+.el-button {
    margin-left: var(--spacing-md);
  }
  & i {
    font-size: var(--font-size-xl);
    vertical-align: middle;
    position: relative;
    top: -1px;
    transition: transform var(--transition-fast);
  }

  & .el-button:hover i {
    transform: scale(1.1);
  }
}

.custom-tree-node{
  width: 100%;
  font-size: var(--font-size-sm);
  .node-operation{
    float: right;
  }
  i[class*="el-icon"] + i[class*="el-icon"]{
    margin-left: var(--spacing-xs);
  }
  .el-icon-plus{
    color: var(--color-accent);
    transition: color var(--transition-fast), transform var(--transition-fast);
    &:hover {
      transform: scale(1.1);
    }
  }
  .el-icon-delete{
    color: var(--color-success);
    transition: color var(--transition-fast), transform var(--transition-fast);
    &:hover {
      transform: scale(1.1);
    }
  }
}

.left-scrollbar .el-scrollbar__view{
  overflow-x: hidden;
}

.el-rate{
  display: inline-block;
  vertical-align: text-top;
  transition: transform var(--transition-fast);
  &:hover {
    transform: scale(1.05);
  }
}
.el-upload__tip{
  line-height: var(--line-height-tight);
  color: var(--color-text-secondary);
  font-size: var(--font-size-xs);
}

.container {
  position: relative;
  width: 100%;
  height: 100%;
}

.components-list {
  padding: var(--spacing-sm);
  box-sizing: border-box;
  height: 100%;
  .components-item {
    display: inline-block;
    width: 48%;
    margin: 1%;
    transition: transform var(--transition-fast) !important;
  }
}
.components-draggable{
  padding-bottom: var(--spacing-md);
}
.components-title{
  font-size: var(--font-size-sm);
  color: var(--color-text);
  margin: var(--spacing-xs) var(--spacing-xs);
  transition: color var(--transition-base);
  .svg-icon{
    color: var(--color-text-secondary);
    font-size: var(--font-size-lg);
    transition: color var(--transition-fast);
  }
}

.components-body {
  padding: var(--spacing-sm) var(--spacing-sm);
  background: var(--color-accent-light);
  font-size: var(--font-size-xs);
  cursor: move;
  border: 1px dashed var(--color-accent-light);
  border-radius: var(--radius-sm);
  transition: border-color var(--transition-fast), color var(--transition-fast), transform var(--transition-fast);
  .svg-icon{
    color: var(--color-text-tertiary);
    font-size: var(--font-size-base);
    transition: color var(--transition-fast);
  }
  &:hover {
    border: 1px dashed var(--color-accent);
    color: var(--color-accent);
    transform: translateY(-2px);
    .svg-icon {
      color: var(--color-accent);
    }
  }
}

.left-board {
  width: 260px;
  position: absolute;
  left: 0;
  top: 0;
  height: 100vh;
}
.left-scrollbar{
  height: calc(100vh - 42px);
  overflow: hidden;
}
.center-scrollbar {
  height: calc(100vh - 42px);
  overflow: hidden;
  border-left: 1px solid var(--color-border-light);
  border-right: 1px solid var(--color-border-light);
  box-sizing: border-box;
}
.center-board {
  height: 100vh;
  width: auto;
  margin: 0 350px 0 260px;
  box-sizing: border-box;
}
.empty-info{
  position: absolute;
  top: 46%;
  left: 0;
  right: 0;
  text-align: center;
  font-size: var(--font-size-lg);
  color: var(--color-text-secondary);
  letter-spacing: var(--spacing-xs);
  transition: color var(--transition-base);
}
.action-bar{
  position: relative;
  height: 42px;
  text-align: right;
  padding: 0 var(--spacing-md);
  box-sizing: border-box;
  border: 1px solid var(--color-border-light);
  border-top: none;
  border-left: none;
  .delete-btn{
    color: var(--color-error);
    transition: color var(--transition-fast);

    &:hover {
      color: var(--color-error);
    }
  }
}
.logo-wrapper{
  position: relative;
  height: 42px;
  background: var(--color-bg-secondary);
  border-bottom: 1px solid var(--color-border-light);
  box-sizing: border-box;
}
.logo{
  position: absolute;
  left: var(--spacing-sm);
  top: var(--spacing-xs);
  line-height: 30px;
  color: var(--color-accent);
  font-weight: var(--font-weight-semibold);
  font-size: var(--font-size-lg);
  white-space: nowrap;
  transition: color var(--transition-base);
  > img{
    width: 30px;
    height: 30px;
    vertical-align: top;
    transition: transform var(--transition-fast);

    &:hover {
      transform: scale(1.1);
    }
  }
  .github{
    display: inline-block;
    vertical-align: sub;
    margin-left: var(--spacing-md);
    > img{
      height: 22px;
      transition: transform var(--transition-fast);

      &:hover {
        transform: scale(1.1);
      }
    }
  }
}

.center-board-row {
  padding: var(--spacing-sm) var(--spacing-sm) var(--spacing-md) var(--spacing-sm);
  box-sizing: border-box;
  & > .el-form {
    height: calc(100vh - 69px);
  }
}
.drawing-board {
  height: 100%;
  position: relative;
  .components-body {
    padding: 0;
    margin: 0;
    font-size: 0;
  }
  .sortable-ghost {
    position: relative;
    display: block;
    overflow: hidden;
    &::before {
      content: " ";
      position: absolute;
      left: 0;
      right: 0;
      top: 0;
      height: 3px;
      background: var(--color-accent);
      z-index: 2;
    }
  }
  .components-item.sortable-ghost {
    width: 100%;
    height: 60px;
    background-color: var(--color-accent-light);
  }
  .active-from-item {
    & > .el-form-item{
      background: var(--color-accent-light);
      border-radius: var(--radius-md);
    }
    & > .drawing-item-copy, & > .drawing-item-delete{
      display: initial;
    }
    & > .component-name{
      color: var(--color-accent);
    }
  }
  .el-form-item{
    margin-bottom: var(--spacing-md);
  }
}
.drawing-item{
  position: relative;
  cursor: move;
  transition: transform var(--transition-fast);
  &.unfocus-bordered:not(.activeFromItem) > div:first-child  {
    border: 1px dashed var(--color-border);
  }
  .el-form-item{
    padding: var(--spacing-sm) var(--spacing-sm);
    transition: background-color var(--transition-fast), border-radius var(--transition-fast);
  }
}
.drawing-row-item{
  position: relative;
  cursor: move;
  box-sizing: border-box;
  border: 1px dashed var(--color-border);
  border-radius: var(--radius-sm);
  padding: 0 var(--spacing-xs);
  margin-bottom: var(--spacing-md);
  transition: border-color var(--transition-fast);
  .drawing-row-item {
    margin-bottom: var(--spacing-xs);
  }
  .el-col{
    margin-top: var(--spacing-md);
  }
  .el-form-item{
    margin-bottom: 0;
  }
  .drag-wrapper{
    min-height: 80px;
  }
  &.active-from-item{
    border: 1px dashed var(--color-accent);
  }
  .component-name{
    position: absolute;
    top: 0;
    left: 0;
    font-size: var(--font-size-xs);
    color: var(--color-text-tertiary);
    display: inline-block;
    padding: 0 var(--spacing-xs);
    transition: color var(--transition-fast);
  }
}
.drawing-item, .drawing-row-item{
  &:hover {
    & > .el-form-item{
      background: var(--color-accent-light);
      border-radius: var(--radius-md);
    }
    & > .drawing-item-copy, & > .drawing-item-delete{
      display: initial;
    }
  }
  & > .drawing-item-copy, & > .drawing-item-delete{
    display: none;
    position: absolute;
    top: -10px;
    width: 22px;
    height: 22px;
    line-height: 22px;
    text-align: center;
    border-radius: var(--radius-full);
    font-size: var(--font-size-xs);
    border: 1px solid;
    cursor: pointer;
    z-index: 1;
    transition: background-color var(--transition-fast), color var(--transition-fast), transform var(--transition-fast);
  }
  & > .drawing-item-copy{
    right: 56px;
    border-color: var(--color-accent);
    color: var(--color-accent);
    background: var(--color-bg-secondary);
    &:hover{
      background: var(--color-accent);
      color: var(--color-white);
      transform: scale(1.1);
    }
  }
  & > .drawing-item-delete{
    right: 24px;
    border-color: var(--color-error);
    color: var(--color-error);
    background: var(--color-bg-secondary);
    &:hover{
      background: var(--color-error);
      color: var(--color-white);
      transform: scale(1.1);
    }
  }
}
</style>
