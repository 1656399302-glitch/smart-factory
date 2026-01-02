<template>
  <div class="wscn-http404-container">
    <div class="wscn-http404">
      <div class="pic-404">
        <img class="pic-404__parent" src="@/assets/404_images/404.png" alt="404">
        <img class="pic-404__child left" src="@/assets/404_images/404_cloud.png" alt="404">
        <img class="pic-404__child mid" src="@/assets/404_images/404_cloud.png" alt="404">
        <img class="pic-404__child right" src="@/assets/404_images/404_cloud.png" alt="404">
      </div>
      <div class="bullshit">
        <div class="bullshit__oops">
          404错误!
        </div>
        <div class="bullshit__headline">
          {{ message }}
        </div>
        <div class="bullshit__info">
          对不起，您正在寻找的页面不存在。尝试检查URL的错误，然后按浏览器上的刷新按钮或尝试在我们的应用程序中找到其他内容。
        </div>
        <router-link to="/" class="bullshit__return-home">
          返回首页
        </router-link>
      </div>
    </div>
  </div>
</template>

<script>

export default {
  name: 'Page404',
  computed: {
    message() {
      return '找不到网页！'
    }
  }
}
</script>

<style lang="scss" scoped>
.wscn-http404-container{
  transform: translate(-50%,-50%);
  position: absolute;
  top: 40%;
  left: 50%;
}
.wscn-http404 {
  position: relative;
  width: 1200px;
  padding: 0 var(--spacing-2xl);
  overflow: hidden;
  .pic-404 {
    position: relative;
    float: left;
    width: 600px;
    overflow: hidden;
    &__parent {
      width: 100%;
      transition: transform var(--transition-base);
    }
    &__child {
      position: absolute;
      &.left {
        width: 80px;
        top: 17px;
        left: 220px;
        opacity: 0;
        animation-name: cloudLeft;
        animation-duration: 2s;
        animation-timing-function: var(--easing-standard);
        animation-fill-mode: forwards;
        animation-delay: 1s;
      }
      &.mid {
        width: 46px;
        top: 10px;
        left: 420px;
        opacity: 0;
        animation-name: cloudMid;
        animation-duration: 2s;
        animation-timing-function: var(--easing-standard);
        animation-fill-mode: forwards;
        animation-delay: 1.2s;
      }
      &.right {
        width: 62px;
        top: 100px;
        left: 500px;
        opacity: 0;
        animation-name: cloudRight;
        animation-duration: 2s;
        animation-timing-function: var(--easing-standard);
        animation-fill-mode: forwards;
        animation-delay: 1s;
      }
      @keyframes cloudLeft {
        0% {
          top: 17px;
          left: 220px;
          opacity: 0;
        }
        20% {
          top: 33px;
          left: 188px;
          opacity: 1;
        }
        80% {
          top: 81px;
          left: 92px;
          opacity: 1;
        }
        100% {
          top: 97px;
          left: 60px;
          opacity: 0;
        }
      }
      @keyframes cloudMid {
        0% {
          top: 10px;
          left: 420px;
          opacity: 0;
        }
        20% {
          top: 40px;
          left: 360px;
          opacity: 1;
        }
        70% {
          top: 130px;
          left: 180px;
          opacity: 1;
        }
        100% {
          top: 160px;
          left: 120px;
          opacity: 0;
        }
      }
      @keyframes cloudRight {
        0% {
          top: 100px;
          left: 500px;
          opacity: 0;
        }
        20% {
          top: 120px;
          left: 460px;
          opacity: 1;
        }
        80% {
          top: 180px;
          left: 340px;
          opacity: 1;
        }
        100% {
          top: 200px;
          left: 300px;
          opacity: 0;
        }
      }
    }
  }
  .bullshit {
    position: relative;
    float: left;
    width: 300px;
    padding: var(--spacing-xl) 0;
    overflow: hidden;
    &__oops {
      font-size: var(--font-size-3xl);
      font-weight: var(--font-weight-bold);
      line-height: var(--line-height-tight);
      color: var(--color-accent);
      opacity: 0;
      margin-bottom: var(--spacing-md);
      animation-name: slideUp;
      animation-duration: var(--transition-base);
      animation-fill-mode: forwards;
    }
    &__headline {
      font-size: var(--font-size-xl);
      line-height: var(--line-height-normal);
      color: var(--color-text);
      font-weight: var(--font-weight-bold);
      opacity: 0;
      margin-bottom: var(--spacing-sm);
      animation-name: slideUp;
      animation-duration: var(--transition-base);
      animation-delay: 100ms;
      animation-fill-mode: forwards;
    }
    &__info {
      font-size: var(--font-size-sm);
      line-height: var(--line-height-relaxed);
      color: var(--color-text-secondary);
      opacity: 0;
      margin-bottom: var(--spacing-xl);
      animation-name: slideUp;
      animation-duration: var(--transition-base);
      animation-delay: 200ms;
      animation-fill-mode: forwards;
    }
    &__return-home {
      display: block;
      float: left;
      width: 110px;
      height: 36px;
      background: var(--color-accent);
      border-radius: var(--radius-full);
      text-align: center;
      color: var(--color-white);
      opacity: 0;
      font-size: var(--font-size-sm);
      line-height: 36px;
      cursor: pointer;
      transition: background-color var(--transition-fast), transform var(--transition-fast);
      animation-name: slideUp;
      animation-duration: var(--transition-base);
      animation-delay: 300ms;
      animation-fill-mode: forwards;

      &:hover {
        background: var(--color-accent-hover);
        transform: scale(1.03);
      }
    }
    @keyframes slideUp {
      0% {
        transform: translateY(60px);
        opacity: 0;
      }
      100% {
        transform: translateY(0);
        opacity: 1;
      }
    }
  }
}

@media (max-width: 1200px) {
  .wscn-http404 {
    width: 100%;
    padding: 0 var(--spacing-lg);
  }
}

@media (max-width: 768px) {
  .wscn-http404 {
    .pic-404 {
      width: 100%;
      float: none;
    }
    .bullshit {
      width: 100%;
      float: none;
      padding: var(--spacing-lg) 0;
    }
  }
}
</style>
