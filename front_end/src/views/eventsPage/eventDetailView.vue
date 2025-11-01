<script setup>
import {onMounted, ref} from "vue";
import {useRoute, useRouter} from "vue-router";
import axios from "axios";
import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";
import {useAlertStore} from "@/stores/alert.js";

const alertStore = useAlertStore()
const route = useRoute();
const router = useRouter();
const eventName = route.params.eventName;

// 此处赋空值初始化，则后续html结构里必须判断对应的eventAvatar非空才渲染，否则会直接访问后端根目录被拦截403
const event = ref({
  eventName: "",
  eventAvatar: "",
  description: "",
  content: "",
  type: "",
  firstEvent: "",
  lastEvent: "",
  status: "",
  num: 0,
  cars: []
});

const loading = ref(true);
const error = ref("");
const sub_events = ref([]);

const fetchEventDetail = async () => {
  try {
    const res = await axios.get(`/api/source/events/${encodeURIComponent(eventName)}`);
    if (res.data?.status === "success") {
      event.value = res.data.data;
      console.log(event.value);
      console.log('raw avatar:', event.value.eventAvatar);
      console.log('resolved:', getPreviewPath(event.value.eventAvatar));

      if (!Array.isArray(event.value.cars)) event.value.cars = [];
    } else {
      error.value = res.data?.msg || "加载赛事详情失败";
    }
  } catch (err) {
    console.error("加载赛事详情出错：",err);
    error.value = err.message;
  } finally {
    loading.value = false;
  }
}

const fetchSub_Events = async () => {
  loading.value = true;
  error.value = null;
  try {
    const res = await axios.get(`/api/source/${encodeURIComponent(eventName)}/sub_events`);
    if (res.data?.status === "success") {
      sub_events.value = res.data.data.map((item, index) => ({
        sub_eventId: index + 1,
        sub_eventName: item.sub_eventName,
        description: item.description,
        status: item.status,
        sub_eventType: item.sub_eventType
      }))
    } else {
      sub_events.value = []
      console.warn("sub_events 返回格式异常", res.data)
    }
  } catch (err) {
    console.error("获取分站赛事资源失败:", err)
    error.value = err.message || "获取分站赛事资源失败"
    sub_events.value = []
  } finally {
    loading.value = false;
  }
}

async function goToSubEventDetail(sub_eventName) {
  try {
    await router.push({
      name: "sub_eventDetail",
      params: { sub_eventName }
    });
    window.scrollTo(0, 0);
  } catch (err) {
    console.error("子赛事跳转失败:", err);
  }
}

// 封面图片路径解析
function getPreviewPath(preview) {
  // 若为空，返回一个默认图片路径
  if (preview === 'none') {
    return '/img/NotFound.jpg'
  }
  return `http://localhost:8080/${preview.replace(/^\/?/, '')}`
}

// 枚举量转换
function enum2view(type) {
  switch (type) {
    case "INDIVIDUAL": return "个人赛"
    case "TEAM": return "团队赛"
    case "ENDED": return "赛事已结束"
    case "ONGOING": return "赛事进行中"
    case "UPDATED": return "赛事有调整，请留意赛事最新资讯"
    default: return "未定"
  }
}

// 枚举量转换（针对分站赛事）
function sub_enum2view(type) {
  switch (type) {
    case "REGISTER_ONGOING": return "赛事报名中"
    case "REGISTER_ENDED": return "赛事报名已截止"
    case "ONGOING": return "开赛中"
    case "ENDED": return "已完赛"
    case "UPDATED": return "赛事有调整，请关注最新资讯"
    default: return "未定";
  }
}

// 时间格式转换
function formatTime(time) {
  return typeof time === "string" ? time.replace("T", " ") : "未定";
}

onMounted(() => {
  fetchEventDetail();
  fetchSub_Events();
});
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
  <main class="eventDetail">
    <h1 class="event__title">{{ event.description }}</h1>
    <div class="event__container">
      <div class="event__left">
        <!-- 赛事概览图 -->
        <img v-if="event.eventAvatar" :src="getPreviewPath(event.eventAvatar)" :alt="event.eventName" class="event__avatar" />
      </div>
      <div class="event__right">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading">加载中...</div>
        <!-- 错误提示 -->
        <div v-else-if="error" class="error">{{ error }}</div>
        <!-- 赛事详情数据表格 -->
        <div v-else>
          <table class="event__table">
            <tr><th>赛事名称</th><td>{{ event.eventName }}</td></tr>
            <tr><th>赛制</th><td>{{ enum2view(event.type) }}</td></tr>
            <tr><th>揭幕战时间</th><td>{{ formatTime(event.firstEvent) || '未定' }}</td></tr>
            <tr><th>收官战时间</th><td>{{ formatTime(event.lastEvent) || '未定' }}</td></tr>
            <tr><th>赛事状态</th><td>{{ enum2view(event.status) }}</td></tr>
            <tr><th>分站数</th><td>{{ event.num }}</td></tr>
            <tr><th>赛事介绍</th><td>{{ event.content }}</td></tr>
            <tr><th>比赛用车</th>
              <td>
                <ul>
                  <li v-for="(car, index) in event.cars" :key="index">{{ car }}</li>
                </ul>
              </td>
            </tr>
          </table>
        </div>
      </div>
    </div>
    <!-- 分站赛事列表 -->
    <div class="event__bottom">
      <div class="sub-events">
        <h2 class="sub-events__title">分站赛事</h2>
        <table class="sub-events__table">
          <thead>
          <tr>
            <th>序号</th>
            <th>分站名称</th>
            <th>赛道</th>
            <th>状态</th>
            <th>赛制</th>
            <th>操作</th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="(subEvent, index) in sub_events" :key="subEvent.sub_eventId || index">
            <td>{{ index + 1 }}</td>
            <td>{{ subEvent.sub_eventName }}</td>
            <td>{{ subEvent.description }}</td>
            <td>{{ sub_enum2view(subEvent.status) }}</td>
            <td>{{ enum2view(subEvent.sub_eventType) }}</td>
            <td><button class="sub-events__btn" @click="goToSubEventDetail(subEvent.sub_eventName)">查看分站</button></td>
          </tr>
          </tbody>
        </table>
      </div>
    </div>
  </main>
  <Footer />
</template>

<style scoped lang="scss">
.eventDetail {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 0;
}

.event {
  &__container {
    display: flex;
  }

  &__left, &__right {
    display: flex;
    flex-direction: column;
    flex: 1; /* 使两部分平分父容器 */
  }

  &__left {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 0 25px;
    max-width: 1000px;
  }

  &__avatar {
    width: 100%;
    height: auto; /* 根据宽度自动调整高度 */
    border-radius: 8px;
    object-fit: contain; /* 让图片完整显示 */
  }

  &__table {
    border-collapse: collapse;
    width: 110%;
    max-width: 600px;
    background: #fff;
    box-shadow: 0 0 8px rgba(0,0,0,0.1);
    padding: 0 25px;
  }

  &__table th, &__table td {
    border: 1px solid #ddd;
    padding: 12px 16px;
    text-align: left;
  }

  &__table th {
    background: #f5f5f5;
    width: 30%;
    font-weight: bold;
  }
}

.sub-events {
  margin-top: 40px;
  width: 100%;
  max-width: 1200px;

  &__title {
    text-align: center;
  }

  &__table {
    width: 100%;
    border-collapse: collapse;
  }

  &__table th, &__table td {
    border: 1px solid #ddd;
    padding: 12px;
    text-align: left;
  }

  &__table th {
    background: #f5f5f5;
    font-weight: bold;
  }

  &__btn {
    cursor: pointer;
  }
}

.loading,
.error {
  font-size: 18px;
  color: #666;
  text-align: center;
  margin-top: 60px;
}

.error {
  color: #ff4d4f;
}
</style>