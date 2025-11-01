<script setup>
// 声明当前组件会接收到一个名为 nav_card 的 prop，类型必须为 Object，并且是必传的
defineProps({
  nav_card: {
    type: Object,
    required: true
  }
})
// 声明当前组件灰度外触发一个名为 click 的自定义事件
defineEmits(["click"])
</script>

<template>
  <article
      class="nav_card"
      @click="$emit('click')"
      :class="`nav_card--${nav_card.id}`"
  >
    <div class="nav_card__body">
      <h3 class="nav_card__name">{{ nav_card.name }}</h3>
      <p class="nav_card__info">{{ nav_card.info }}</p>
    </div>
  </article>
</template>

<style scoped lang="scss">
.nav_card {
  /* 整张卡片作为按钮可以点击 */
  cursor: pointer;
  /* 启用阴影溢出父容器显示 */
  overflow: visible;
  /* 主要阴影轮廓（外发光 + 轻微内阴影） */
  box-shadow:
      0 6px 18px rgba(0,0,0,0.10),       /* 主体阴影 */
      0 0 18px rgba(117,91,138,0.06);    /* 彩色微光轮廓（可删改为纯灰） */
  /* 明显的半透明边框 + 轻微内阴影，和模糊玻璃效果  */
  background: linear-gradient(180deg, rgba(255,255,255,0.02), rgba(255,255,255,0.01));
  /* 模糊玻璃效果 */
  backdrop-filter: blur(25px);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 0.5rem;
  padding: 1rem;
  text-align: center;
  transition: all 0.3s ease;
  position: relative;

  /* 添加渐变覆盖层 */
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    border-radius: 0.5rem;
    background: linear-gradient(135deg, rgba(255,255,255,0.1) 0%, rgba(255,255,255,0.05) 100%);
    opacity: 0;
    transition: opacity 0.3s ease;
    z-index: 1;
  }

  /* 内容层在渐变层之上 */
  &__body {
    position: relative;
    z-index: 2;
  }

  /* 悬停提升效果 */
  &:hover {
    transform: translateY(-4px);
    &::before {
      opacity: 1;
    }
  }

  &__name {
    font-size: 1.25rem;
    margin: 0.5rem 0;
    font-weight: 600;
  }
  &__info {
    font-size: 0.875rem;
    color: rgba(255,255,255,0.8);
    line-height: 1.4;
  }

  /* 新手体验赛 - 绿色系 */
  &--1 {
    border-color: rgba(76, 175, 80, 0.3);
    box-shadow:
        0 6px 18px rgba(0,0,0,0.15),
        0 0 18px rgba(76, 175, 80, 0.1);

    &:hover {
      border-color: rgba(76, 175, 80, 0.6);
      box-shadow:
          0 8px 25px rgba(0,0,0,0.2),
          0 0 25px rgba(76, 175, 80, 0.15);
    }
  }

  /* 青铜联赛 - 青铜棕色系 */
  &--2 {
    border-color: rgba(205, 127, 50, 0.3);
    box-shadow:
        0 6px 18px rgba(0,0,0,0.15),
        0 0 18px rgba(205, 127, 50, 0.1);

    &:hover {
      border-color: rgba(205, 127, 50, 0.6);
      box-shadow:
          0 8px 25px rgba(0,0,0,0.2),
          0 0 25px rgba(205, 127, 50, 0.15);
    }
  }

  /* 白银联赛 - 银灰色系 */
  &--3 {
    border-color: rgba(192, 192, 192, 0.3);
    box-shadow:
        0 6px 18px rgba(0,0,0,0.15),
        0 0 18px rgba(192, 192, 192, 0.1);

    &:hover {
      border-color: rgba(192, 192, 192, 0.6);
      box-shadow:
          0 8px 25px rgba(0,0,0,0.2),
          0 0 25px rgba(192, 192, 192, 0.15);
    }
  }

  /* 黄金联赛 - 金色系 */
  &--4 {
    border-color: rgba(255, 215, 0, 0.3);
    box-shadow:
        0 6px 18px rgba(0,0,0,0.15),
        0 0 18px rgba(255, 215, 0, 0.1);

    &:hover {
      border-color: rgba(255, 215, 0, 0.6);
      box-shadow:
          0 8px 25px rgba(0,0,0,0.2),
          0 0 25px rgba(255, 215, 0, 0.15);
    }
  }

  /* 铂金联赛 - 天蓝色系 */
  &--5 {
    border-color: rgba(135, 206, 235, 0.3);
    box-shadow:
        0 6px 18px rgba(0,0,0,0.15),
        0 0 18px rgba(135, 206, 235, 0.1);

    &:hover {
      border-color: rgba(135, 206, 235, 0.6);
      box-shadow:
          0 8px 25px rgba(0,0,0,0.2),
          0 0 25px rgba(135, 206, 235, 0.15);
    }
  }

  /* 测试赛 - 科技蓝色系 */
  &--6 {
    border-color: rgba(70, 130, 180, 0.3);
    box-shadow:
        0 6px 18px rgba(0,0,0,0.15),
        0 0 18px rgba(70, 130, 180, 0.1);

    &:hover {
      border-color: rgba(70, 130, 180, 0.6);
      box-shadow:
          0 8px 25px rgba(0,0,0,0.2),
          0 0 25px rgba(70, 130, 180, 0.15);
    }
  }

  /* 娱乐赛事 - 活泼紫色系 */
  &--7 {
    border-color: rgba(147, 112, 219, 0.3);
    box-shadow:
        0 6px 18px rgba(0,0,0,0.15),
        0 0 18px rgba(147, 112, 219, 0.1);

    &:hover {
      border-color: rgba(147, 112, 219, 0.6);
      box-shadow:
          0 8px 25px rgba(0,0,0,0.2),
          0 0 25px rgba(147, 112, 219, 0.15);
    }
  }
}
</style>