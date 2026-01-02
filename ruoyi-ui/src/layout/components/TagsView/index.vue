<template>
  <div id="tags-view-container" class="tags-view-container">
    <scroll-pane
      ref="scrollPane"
      class="tags-view-wrapper"
      @scroll="handleScroll"
    >
      <router-link
        v-for="tag in visitedViews"
        ref="tag"
        :key="tag.path"
        :class="isActive(tag) ? 'active' : ''"
        :to="{ path: tag.path, query: tag.query, fullPath: tag.fullPath }"
        tag="span"
        class="tags-view-item"
        :style="activeStyle(tag)"
        @click.middle.native="!isAffix(tag) ? closeSelectedTag(tag) : ''"
        @contextmenu.prevent.native="openMenu(tag, $event)"
      >
        <span class="tag-title">{{ tag.title }}</span>
        <span
          v-if="!isAffix(tag)"
          class="el-icon-close"
          @click.prevent.stop="closeSelectedTag(tag)"
        />
        <ChromeTabBg
          class="chrome-tab-bg"
          :class="{ affix: isAffix(tag) }"
          :style="activeBgStyle(tag)"
          v-if="isActive(tag)"
        />
      </router-link>
    </scroll-pane>
    <ul
      v-show="visible"
      :style="{ left: left + 'px', top: top + 'px' }"
      class="contextmenu"
    >
      <li @click="refreshSelectedTag(selectedTag)">
        <i class="el-icon-refresh-right"></i> 刷新页面
      </li>
      <li v-if="!isAffix(selectedTag)" @click="closeSelectedTag(selectedTag)">
        <i class="el-icon-close"></i> 关闭当前
      </li>
      <li @click="closeOthersTags">
        <i class="el-icon-circle-close"></i> 关闭其他
      </li>
      <li v-if="!isFirstView()" @click="closeLeftTags">
        <i class="el-icon-back"></i> 关闭左侧
      </li>
      <li v-if="!isLastView()" @click="closeRightTags">
        <i class="el-icon-right"></i> 关闭右侧
      </li>
      <li @click="closeAllTags(selectedTag)">
        <i class="el-icon-circle-close"></i> 全部关闭
      </li>
    </ul>
  </div>
</template>

<script>
import ScrollPane from "./ScrollPane";
import ChromeTabBg from "./ChromeTabBg";
import path from "path";

export default {
  components: { ScrollPane, ChromeTabBg },
  data() {
    return {
      visible: false,
      top: 0,
      left: 0,
      selectedTag: {},
      affixTags: [],
    };
  },
  computed: {
    visitedViews() {
      return this.$store.state.tagsView.visitedViews;
    },
    routes() {
      return this.$store.state.permission.routes;
    },
    theme() {
      return this.$store.state.settings.theme;
    },
  },
  watch: {
    $route() {
      this.addTags();
      this.moveToCurrentTag();
    },
    visible(value) {
      if (value) {
        document.body.addEventListener("click", this.closeMenu);
      } else {
        document.body.removeEventListener("click", this.closeMenu);
      }
    },
  },
  mounted() {
    this.initTags();
    this.addTags();
  },
  methods: {
    isActive(route) {
      return route.path === this.$route.path;
    },
    activeStyle(tag) {
      if (!this.isActive(tag)) return {};
      return {
        color: this.theme,
      };
    },
    activeBgStyle(tag) {
      if (!this.isActive(tag)) return {};
      return {
        color: this.get10PercentBlend(this.theme),
      };
    },
    isAffix(tag) {
      return tag.meta && tag.meta.affix;
    },
    isFirstView() {
      try {
        return (
          this.selectedTag.fullPath === "/index" ||
          this.selectedTag.fullPath === this.visitedViews[1].fullPath
        );
      } catch (err) {
        return false;
      }
    },
    isLastView() {
      try {
        return (
          this.selectedTag.fullPath ===
          this.visitedViews[this.visitedViews.length - 1].fullPath
        );
      } catch (err) {
        return false;
      }
    },
    filterAffixTags(routes, basePath = "/") {
      let tags = [];
      routes.forEach((route) => {
        if (route.meta && route.meta.affix) {
          const tagPath = path.resolve(basePath, route.path);
          tags.push({
            fullPath: tagPath,
            path: tagPath,
            name: route.name,
            meta: { ...route.meta },
          });
        }
        if (route.children) {
          const tempTags = this.filterAffixTags(route.children, route.path);
          if (tempTags.length >= 1) {
            tags = [...tags, ...tempTags];
          }
        }
      });
      return tags;
    },
    initTags() {
      const affixTags = (this.affixTags = this.filterAffixTags(this.routes));
      for (const tag of affixTags) {
        // Must have tag name
        if (tag.name) {
          this.$store.dispatch("tagsView/addVisitedView", tag);
        }
      }
    },
    addTags() {
      const { name } = this.$route;
      if (name) {
        this.$store.dispatch("tagsView/addView", this.$route);
      }
    },
    moveToCurrentTag() {
      const tags = this.$refs.tag;
      this.$nextTick(() => {
        for (const tag of tags) {
          if (tag.to.path === this.$route.path) {
            this.$refs.scrollPane.moveToTarget(tag);
            // when query is different then update
            if (tag.to.fullPath !== this.$route.fullPath) {
              this.$store.dispatch("tagsView/updateVisitedView", this.$route);
            }
            break;
          }
        }
      });
    },
    refreshSelectedTag(view) {
      this.$tab.refreshPage(view);
      if (this.$route.meta.link) {
        this.$store.dispatch("tagsView/delIframeView", this.$route);
      }
    },
    closeSelectedTag(view) {
      this.$tab.closePage(view).then(({ visitedViews }) => {
        if (this.isActive(view)) {
          this.toLastView(visitedViews, view);
        }
      });
    },
    closeRightTags() {
      this.$tab.closeRightPage(this.selectedTag).then((visitedViews) => {
        if (!visitedViews.find((i) => i.fullPath === this.$route.fullPath)) {
          this.toLastView(visitedViews);
        }
      });
    },
    closeLeftTags() {
      this.$tab.closeLeftPage(this.selectedTag).then((visitedViews) => {
        if (!visitedViews.find((i) => i.fullPath === this.$route.fullPath)) {
          this.toLastView(visitedViews);
        }
      });
    },
    closeOthersTags() {
      this.$router.push(this.selectedTag.fullPath).catch(() => {});
      this.$tab.closeOtherPage(this.selectedTag).then(() => {
        this.moveToCurrentTag();
      });
    },
    closeAllTags(view) {
      this.$tab.closeAllPage().then(({ visitedViews }) => {
        if (this.affixTags.some((tag) => tag.path === this.$route.path)) {
          return;
        }
        this.toLastView(visitedViews, view);
      });
    },
    toLastView(visitedViews, view) {
      const latestView = visitedViews.slice(-1)[0];
      if (latestView) {
        this.$router.push(latestView.fullPath);
      } else {
        // now the default is to redirect to the home page if there is no tags-view,
        // you can adjust it according to your needs.
        if (view.name === "Dashboard") {
          // to reload home page
          this.$router.replace({ path: "/redirect" + view.fullPath });
        } else {
          this.$router.push("/");
        }
      }
    },
    openMenu(tag, e) {
      const menuMinWidth = 105;
      const offsetLeft = this.$el.getBoundingClientRect().left; // container margin left
      const offsetWidth = this.$el.offsetWidth; // container width
      const maxLeft = offsetWidth - menuMinWidth; // left boundary
      const left = e.clientX - offsetLeft + 15; // 15: margin right

      if (left > maxLeft) {
        this.left = maxLeft;
      } else {
        this.left = left;
      }

      this.top = e.clientY;
      this.visible = true;
      this.selectedTag = tag;
    },
    closeMenu() {
      this.visible = false;
    },
    handleScroll() {
      this.closeMenu();
    },
    get10PercentBlend(hexColor) {
      // 1. 解析 HEX 颜色
      const r = parseInt(hexColor.slice(1, 3), 16);
      const g = parseInt(hexColor.slice(3, 5), 16);
      const b = parseInt(hexColor.slice(5, 7), 16);

      // 2. 计算混合后的 RGB（10% 颜色 + 90% 白色）
      const blendR = Math.round(r * 0.1 + 255 * 0.9);
      const blendG = Math.round(g * 0.1 + 255 * 0.9);
      const blendB = Math.round(b * 0.1 + 255 * 0.9);

      // 3. 转回 HEX
      return `#${blendR.toString(16).padStart(2, "0")}${blendG
        .toString(16)
        .padStart(2, "0")}${blendB.toString(16).padStart(2, "0")}`;
    },
  },
};
</script>

<style lang="scss" scoped>
.tags-view-container {
  height: 34px;
  width: 100%;
  background: var(--color-bg-secondary);
  border-bottom: 1px solid var(--color-border-light);
  box-shadow: var(--shadow-sm);
  transition: background-color var(--transition-base), border-color var(--transition-base);

  .tags-view-wrapper {
    .tags-view-item {
      box-sizing: border-box;
      display: inline-block;
      position: relative;
      cursor: pointer;
      height: 30px;
      line-height: 30px;
      color: var(--color-text-secondary);
      padding: 0 var(--spacing-sm);
      font-size: var(--font-size-xs);
      margin-left: var(--spacing-xs);
      margin-top: var(--spacing-xs);
      border-radius: var(--radius-sm);
      transition: color var(--transition-fast), transform var(--transition-fast), background-color var(--transition-fast);

      &:first-of-type {
        margin-left: var(--spacing-md);
      }
      &:last-of-type {
        margin-right: var(--spacing-md);
      }

      &:hover {
        transform: translateY(-2px);
        background-color: var(--color-bg);
      }

      &.active {
        color: var(--color-accent);
      }
    }
  }

  .contextmenu {
    margin: 0;
    background: var(--color-bg-secondary);
    z-index: 3000;
    position: absolute;
    list-style-type: none;
    padding: var(--spacing-xs) 0;
    border-radius: var(--radius-sm);
    font-size: var(--font-size-xs);
    font-weight: var(--font-weight-normal);
    color: var(--color-text);
    box-shadow: var(--shadow-md);
    transition: background-color var(--transition-base), box-shadow var(--transition-base);

    li {
      margin: 0;
      padding: var(--spacing-xs) var(--spacing-md);
      cursor: pointer;
      transition: background-color var(--transition-fast), transform var(--transition-fast);

      &:hover {
        background: var(--color-bg);
        transform: translateX(2px);
      }
    }
  }

  .tag-title {
    position: relative;
    z-index: 100;
  }
}
.router-link-active .chrome-tab-bg {
  position: absolute;
  top: -1px;
  left: -12%;
  width: 118%;
  height: 32px;
  // color: #e7f9f9;
  transform: scaleY(0.9);
  &.affix {
    left: -20%;
    width: 140%;
  }
}
</style>

<style lang="scss">
//reset element css of el-icon-close
.tags-view-wrapper {
  .tags-view-item {
    .el-icon-close {
      position: relative;
      z-index: 100;
      width: 16px;
      height: 16px;
      vertical-align: middle;
      border-radius: var(--radius-full);
      text-align: center;
      transition: transform var(--transition-fast), opacity var(--transition-fast), color var(--transition-fast);
      transform: scale(1.2);

      &:before {
        transform: scale(0.6);
        display: inline-block;
        vertical-align: -1px;
      }

      &:hover {
        transform: scale(1.5);
        color: var(--color-error);
        opacity: 0.9;
      }
    }
  }
}
</style>
