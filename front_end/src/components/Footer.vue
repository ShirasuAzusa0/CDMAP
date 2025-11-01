<script setup>
import "@/styles/variables.scss"
</script>

<template>
  <footer class="footer">
    <div class="footer__container">
      <!-- 简介区 -->
      <div class="footer__brand">
        <h3 class="footer__title">CDMAP</h3>
        <p class="footer__subtitle">
          Practice and ready for race
        </p>
      </div>
      <!-- 链接区 -->
      <div class="footer__nav">
        <h3 class="footer__navTitle">友链</h3>
        <ul class="footer__navList">
          <li class="footer__navItem1">
            <a href="https://github.com/ShirasuAzusa0">我的GitHub主页</a>
          </li>
          <li class="footer__navItem2">
            <a href="https://space.bilibili.com/290261723">我的b站主页</a>
          </li>
        </ul>
      </div>
      <!-- 版权声明 -->
      <div class="footer__copyRight">
        <h3 class="footer__copyRightTitle">Copy Right</h3>
        <p class="footer__copyRightContent1">
          © 2025 CDMAP.All rights reserved.
        </p>
        <p class="footer__copyRightContent2">
          本网站归 Y.Z.Ben 本人所有
        </p>
        <p class="footer__copyRightContent3">
          本站的所有资源均为免费开源资源，严禁商用，违者必究
        </p>
      </div>
    </div>
  </footer>
</template>

<style scoped lang="scss">
.footer {
  margin-top: auto;
  background: var(--footer-bg);
  color: var(--footer-text);
  padding: var(--footer-px) 0;

  &__container {
    max-width: 80rem;
    margin: 0 auto;
    padding: 1rem 0;
    /* 横向 + 自动换行 */
    display: flex;
    gap: 2rem;
    flex-wrap: wrap;
    /* 左右拉开 */
    justify-content: space-between;
    align-items: flex-start;
  }

  &__brand,
  &__nav,
  &__copyRight {
    flex: 1 1 0;           /* 横向排布，强制三等分 */
    min-width: 0;          /* 防止内容撑宽 */
  }

  &__brand {
    .footer__title {
      font-size: 1.25rem;
      font-weight: 700;
      color: var(--footer-primary);
      margin-bottom: 0.5rem;
    }
    .footer__subtitle {
      font-size: 0.875rem;
      color: var(--footer-text-muted);
    }
  }

  &__nav {
    .footer__navTitle {
      font-size: 1.125rem;
      font-weight: 600;
      margin-bottom: 0.75rem;
    }

    .footer__navList {
      list-style: none;
      padding: 0;
      margin: 0;
    }

    .footer__navItem1 {
      margin-bottom: 0.5rem;
    }
    .footer__navItem2 {
      margin-bottom: 0.5rem;
    }
    .footer__navItem3 {
      margin-bottom: 0.5rem;
    }
    .footer__navItem4 {
      margin-bottom: 0.5rem;
    }

    li {
      margin-bottom: 0.5rem;

      a {
        font-size: 0.875rem;
        color: var(--footer-text-muted);
        text-decoration: none;
        transition: color 0.25s ease;

        &:hover {
          color: var(--footer-primary); /* 鼠标悬停时，亮眼突出 */
        }

        &:active {
          color: var(--footer-text); /* 点击瞬间，变为正文色 */
        }
      }
    }
  }

  &__copyRight {
    h3 {
      color: white;
    }
    font-size: 0.875rem;
    color: var(--footer-text-muted);
  }
}
</style>