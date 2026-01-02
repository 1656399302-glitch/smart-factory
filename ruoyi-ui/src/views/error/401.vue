<template>
  <div class="errPage-container">
    <el-button icon="arrow-left" class="pan-back-btn" @click="back">
      返回
    </el-button>
    <el-row>
      <el-col :span="12">
        <h1 class="text-jumbo text-ginormous">
          401错误!
        </h1>
        <h2>您没有访问权限！</h2>
        <h6>对不起，您没有访问权限，请不要进行非法操作！您可以返回主页面</h6>
        <ul class="list-unstyled">
          <li class="link-type">
            <router-link to="/">
              回首页
            </router-link>
          </li>
        </ul>
      </el-col>
      <el-col :span="12">
        <img :src="errGif" width="313" height="428" alt="Girl has dropped her ice cream.">
      </el-col>
    </el-row>
  </div>
</template>

<script>
import errGif from '@/assets/401_images/401.gif'

export default {
  name: 'Page401',
  data() {
    return {
      errGif: errGif + '?' + +new Date()
    }
  },
  methods: {
    back() {
      if (this.$route.query.noGoBack) {
        this.$router.push({ path: '/' })
      } else {
        this.$router.go(-1)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
  .errPage-container {
    width: 800px;
    max-width: 100%;
    margin: var(--spacing-3xl) auto;
    .pan-back-btn {
      background: var(--color-accent);
      color: var(--color-white);
      border: none!important;
      transition: background-color var(--transition-fast), transform var(--transition-fast);

      &:hover {
        background: var(--color-accent-hover);
        transform: scale(1.03);
      }
    }
    .pan-gif {
      margin: 0 auto;
      display: block;
      transition: transform var(--transition-base);

      &:hover {
        transform: scale(1.05);
      }
    }
    .pan-img {
      display: block;
      margin: 0 auto;
      width: 100%;
      transition: transform var(--transition-base);

      &:hover {
        transform: scale(1.02);
      }
    }
    .text-jumbo {
      font-size: var(--font-size-4xl);
      font-weight: var(--font-weight-bold);
      color: var(--color-text);
      transition: color var(--transition-base);
    }
    .list-unstyled {
      font-size: var(--font-size-sm);
      li {
        padding-bottom: var(--spacing-xs);
      }
      a {
        color: var(--color-accent);
        text-decoration: none;
        transition: color var(--transition-fast);

        &:hover {
          color: var(--color-accent-hover);
          text-decoration: underline;
        }
      }
    }
  }

  @media (max-width: 992px) {
    .errPage-container {
      width: 100%;
      padding: 0 var(--spacing-lg);
    }
  }

  @media (max-width: 768px) {
    .errPage-container {
      margin: var(--spacing-xl) auto;
      .text-jumbo {
        font-size: var(--font-size-3xl);
      }
    }
  }

  @media (max-width: 576px) {
    .errPage-container {
      margin: var(--spacing-lg) auto;
      .text-jumbo {
        font-size: var(--font-size-2xl);
      }
    }
  }
</style>
