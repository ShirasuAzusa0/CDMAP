<script setup>
import { ref, onMounted } from "vue"
import { useRouter, useRoute } from "vue-router";
import "@/styles/variables.scss"
import { useUserStore } from "@/stores/user.js"
import { useAlertStore } from "@/stores/alert.js"

// 移动端菜单开关
const isMenuOpen = ref(true)

const router = useRouter()
const route = useRoute()

const userStore = useUserStore()
const alertStore = useAlertStore()

// 导航列表
const navLinks = ref([
  {id: '1', label: 'Events', path: '/Events'},
  {id: '2', label: 'Dashboard', path: '/Dashboard'}
])

// 预先加一个 to 字段（纯对象，无函数）
navLinks.value.forEach(l => (l.to = { id: l.id }))

// 首页跳转
function goHome() {
  if (route.path === "/Home") {
    window.scrollTo(0, 0);
    window.location.reload()
  }
  else {
    router.push("/Home").then(() => {
      window.scrollTo(0, 0);
    });
  }
}

// 跳转登录
function Login() {
  router.push("/Welcome/Login")
  }

// 跳转注册
function Signup() {
  router.push("/Welcome/Signup")
}

// 退出登录
function Logout() {
  userStore.logout()
  alertStore.showAlertMessage("success", "已登出账号")
  router.push("/Home")
}

const addAdminNav = async () => {
  const user = userStore.user
  if (user?.type === 'ADMIN' && !navLinks.value.find(l => l.label === 'Manage')) {
    navLinks.value.push({ id: 3, label: 'Manage', path: '/Manage' });
  }
}

onMounted(() => {
  addAdminNav();
})
</script>

<template>
  <header class="header">
    <div class="header__container">
      <!-- logo -->
      <div class="header__logo">
        <img src="../../src/components/icons/Logo.png" alt="logo" class="header__logo-img" @click="goHome" />
      </div>
      <!-- 导航栏nav -->
      <nav class="header__nav">
        <router-link v-for="link in navLinks"
                     :key="link.name"
                     :to="{ path: link.path }"
                     class="nav__link"
                     active-class="nav__link--active"
        >{{ link.label }}</router-link>
      </nav>
      <!-- 用户操作（登录/注册）区 -->
      <div class="header__user">
        <!-- 登录状态 -->
        <template v-if="userStore.user && userStore.user.username">
          <img
              :src="userStore.user.avatar|| 'https://avatars.githubusercontent.com/u/19370775'"
              alt="avatar"
              class="user__avatar"
          />
          <span class="user__name">{{ userStore.user.username }}</span>
          <button class="btn btn__Logout" @click="Logout">Logout</button>
        </template>
        <!-- 未登录状态 -->
        <template v-else>
          <button class="btn btn__Signup" @click="Signup">Signup</button>
          <button class="btn btn__Login" @click="Login">Login</button>
        </template>
      </div>
    </div>
  </header>
</template>

<style scoped lang="scss">
.header {
  background-color: var(--c-white);
  border-bottom: 1px solid var(--c-gray);
  box-shadow: var(--shadow-sm);

  &__container {
    max-width: 80rem;               /* 1280px */
    margin: 0 auto;                 /* 水平居中 */
    padding: 0 1rem;                /* 响应式左右留白 */
    display: flex;
    align-items: center;            /* 垂直居中 */
    justify-content: space-between;
    height: 4rem;                   /* 64px 高度 */
  }

  &__logo {
    flex-shrink: 0;
  }

  &__logo-img {
    width: 200px;
    cursor: pointer;
    gap: 0.75rem;
  }

  &__nav {
    display: flex;                  /* 始终可见，不再用 @media 隐藏 */
    gap: 0.5rem;
    overflow-x: auto;               /* 关键：超出时出现横向滚动条 */
    white-space: nowrap;            /* 防止文字换行 */
    -webkit-overflow-scrolling: touch;
    scrollbar-width: none;          /* Firefox 隐藏滚动条 */
    &::-webkit-scrollbar {
      display: none;                /* Chrome / Safari 隐藏滚动条 */
    }

    .nav__link {
      font-size: 0.875rem; /* 14px */
      font-weight: 500;
      color: var(--c-mauve);
      padding: 0.5rem 0.75rem; /* 8px 12px */
      border-radius: 0.375rem; /* 6px 圆角 */
      text-decoration: none;
      transition: color 0.15s ease-in-out;

      &:hover { color: var(--c-accent); }

      &--active { color: var(--c-deep); }   /* 当前路由高亮 */
    }
  }

  &__user {
    display: flex;
    align-items: center;
    gap: 0.75rem;                           /* 12px 间距 */
  }
}

.user {
  &__avatar {
    width: 32px;
    height: 32px;
    border-radius: 50%;
  }

  &__name {
    margin: 0 8px;
    font-weight: 500;
    color: var(--c-deep);
    white-space: nowrap;
  }
}

.btn {
  font-size: 0.875rem;
  font-weight: 500;
  padding: 0.5rem 1rem;
  border-radius: 0.375rem;
  cursor: pointer;
  transition: all 0.15s ease-in-out;

  &__Logout, &__Signup {
    color: var(--c-mauve);
    background: none;
    border: none;
    &:hover { color: var(--c-accent); }
  }

  &__Login {
    color: #fff;
    background-color: var(--c-mauve);
    border: none;
    &:hover { background-color: var(--c-accent); }
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .header {
    gap: 0.5rem;
  }
}
</style>