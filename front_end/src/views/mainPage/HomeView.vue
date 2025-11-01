<script setup>
import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";
import NavCards from '@/components/NavCards.vue'
import { useAlertStore } from "@/stores/alert.js";

const alertStore = useAlertStore();

const navs = [
  {id: 1, name: "新手体验赛ROCKIE", info: "入门级赛事，旨在让新手体验并熟悉模拟赛车的比赛"},
  {id: 2, name: "青铜联赛BRONZE", info: "入门进阶赛事，旨在让参赛者学习不同赛车与赛道的驾驶技巧"},
  {id: 3, name: "白银联赛SILVER", info: "拥有多元的选择和丰富的赛制，强调参赛选手们之间的对抗，锻炼玩家的攻防能力"},
  {id: 4, name: "黄金联赛GOLD", info: "个人赛事的最高等级赛事，对车手个人实力和车辆调校能力的最终检验"},
  {id: 5, name: "铂金联赛PLATINUM", info: "以俱乐部/车队组队形式参与的最高规格赛事，多组别同场竞技，综合考验车手实力、策略制定、赛车调校和团队协作的耐力赛殿堂"},
  {id: 6, name: "测试赛TEST", info: "不定期举办的车辆测试赛事，通过玩家反馈为为玩家提供更好的赛事体验"},
  {id: 7, name: "娱乐赛事FUNCUP", info: "娱乐向的赛事，旨在让各路玩家体验到赛车赛事的乐趣，不参与Rating计分"}
]

function scrollToNavDetails() {
  const el = document.getElementById('nav_details_section')
  if(!el) return
  el.scrollIntoView({behavior: 'smooth', block: 'start'})
}
</script>

<template>
  <Header />
  <!-- 全局提示框 -->
  <transition name="fade">
    <el-alert
        v-if="alertStore.showAlert"
        :title="alertStore.alertMessage"
        :type="alertStore.alertType"
        show-icon
        effect="dark"
        class="global-alert"
    />
  </transition>
  <main class="home">
    <!-- 核心区（hero区） -->
    <section class="hero">
      <div class="hero__bg">
        <div class="hero__image">
          <el-image style="width: 100%; height: 100%;" fit="contain"
                    src="Home.png"/>
        </div>
      </div>
      <div class="hero__container">
        <h1 class="hero__title">BWOAH!</h1>
        <p class="hero__subtitle">Welcome to CDMAP!</p>
      </div>
      <!-- 按钮 -->
      <div class="btn">
        <div class="btn__container">
          <button class="btn__explore" @click="scrollToNavDetails">Explore</button>
        </div>
      </div>
    </section>
    <!-- 赛事分级详细展示区 -->
    <section id="nav_details_section" class="nav_details">
      <div class="nav_details__bg">
        <div class="nav_details__image">
          <el-image style="width: 100%; height: 100%;" fit="contain"
                    src="Home.png"/>
        </div>
      </div>
      <!-- 导航卡片容器 -->
      <div class="nav_details__container">
        <h1 class="nav_details__title">Race Introduction</h1>
        <div class="nav_details__grid">
          <!-- 触发链接跳转 -->
          <NavCards v-for="nav in navs" :key="nav.id" :nav_card="nav" />
        </div>
      </div>
    </section>
  </main>
  <Footer />
</template>

<style scoped lang="scss">
.global-alert {
  position: fixed;
  top: 80px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 9999;
  width: 600px;
}
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

.home {
  /* 根容器，让文字和按钮浮现在背景之上 */
  position: relative;
  margin: 0;
  display: block;
  flex-direction: column;
}

.hero {
  /* 文字层：绝对居中 */
  flex: 1;
  height: 100vh;
  position: relative;
  overflow: hidden;
  inset: 0;

  &__bg {
    position: absolute;
    inset: 0;
    z-index: 1;
    /* 淡入动画：opacity 0->1，持续1s */
    opacity: 0;
    animation: fadeInBg 0.8s ease-out 0s both;
    &::after {
      content: '';
      position: absolute;
      inset: 0;
      /* 45% 黑色蒙层 → 淡化背景 */
      background: rgba(0, 0, 0, 0.45);
    }
  }

  &__image {
    width: 100%;
    height: 100%;
  }

  &__title {
    position: absolute;
    left: 50%;
    z-index: 2;
    color: #fff;
    text-align: center;
    margin: 0;
    top: 50%;
    font-size: clamp(2.5rem, 6vw, 4rem);
    transform: translate(-50%, -100%);
    animation: fadeInTitle 0.8s ease-out 0.8s both;
  }

  &__subtitle {
    position: absolute;
    left: 50%;
    transform: translateX(-50%);
    z-index: 2;
    color: #fff;
    text-align: center;
    margin: 0;
    top: calc(50% + 1.5rem);
    font-size: clamp(1.125rem, 2.5vw, 1.5rem);
    animation: fadeInSubtitle 0.8s ease-out 0.8s both;
  }
}

.btn {
  margin-top: 2rem;
  bottom: 2rem;
  text-align: center;

  &__container {
    position: absolute;
    left: 0;
    right: 0;
    top: calc(50% + 4.5rem);
    /* 整个容器水平居中 */
    /* transform: translateX(-50%); */
    display: flex;
    /* 按钮在容器内居中 */
    justify-content: center;
    gap: 1rem;
    z-index: 2;
  }

  &__explore {
    /* 按钮的样式 */
    padding: 1rem 3rem;
    font-size: 1.25rem;
    background: #755b8a;
    color: #fff;
    border: none;
    border-radius: 0.5rem;
    cursor: pointer;
    animation: fadeInBtn 0.8s ease-out 1.6s both;
    transition: background 0.2s;
    &:hover { background: #5a2a92; }
  }
}

.nav_details {
  /* 让卡片区域紧跟在 Hero 之后，占剩余高度 */
  /* 第二屏高度 */
  min-height: 100vh;
  display: flex;
  align-items: center;
  flex-direction: column;
  justify-content: flex-start;
  position: relative;
  padding: 2rem 1rem;

  &__bg {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    z-index: 0;
    &::after {
      content: '';
      position: absolute;
      inset: 0;
      /* 45% 黑色蒙层 → 淡化背景 */
      background: rgba(0, 0, 0, 0.45);
    }
  }

  &__image {
    width: 100%;
    height: 100%
  }

  &__container {
    z-index: 1;
    max-width: 80rem;
    margin: 0 auto;
  }

  &__title {
    font-size: 2rem;
    text-align: center;
    z-index: 2;
    color: #181818;
    margin-bottom: 2rem;
  }

  &__grid {
    display: grid;
    /* 5 列等宽 */
    grid-template-columns: repeat(4, 1fr);
    gap: 1.5rem;
  }
}

@keyframes fadeInBg { to { opacity: 1; } }
@keyframes fadeInTitle {
  0%   { opacity: 0; transform: translate(-50%, -100%) translateY(1rem); }
  100% { opacity: 1; transform: translate(-50%, -100%) translateY(0); }
}
@keyframes fadeInSubtitle {
  0%   { opacity: 0; transform: translate(-50%, 0) translateY(1rem); }
  100% { opacity: 1; transform: translate(-50%, 0) translateY(0); }
}
@keyframes fadeInBtn {
  0%   { opacity: 0; transform: translateY(1rem); }
  100% { opacity: 1; transform: translateY(0); }
}
</style>