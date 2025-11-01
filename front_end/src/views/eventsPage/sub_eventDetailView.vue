<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import axios from "axios";
import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";
import { useUserStore } from "@/stores/user";
import { useAlertStore } from "@/stores/alert";

const route = useRoute();
const router = useRouter();
const sub_eventName = ref(route.params.sub_eventName || "");
const userStore = useUserStore();
const alertStore = useAlertStore();

// 子赛事详情数据
const subEvent = ref({
  sub_eventName: "",
  description: "",
  status: "",
  sub_eventType: "",
  registerEndDate: "",
  sub_eventStart: "",
  sub_eventEnd: ""
});

const loading = ref(true);
const error = ref("");

// 报名表数据
const registration = ref({
  account: "",
  eventName: "",
  sub_eventName: "",
  carName: "",
  teamName: ""
});
const regLoading = ref(false);
const regError = ref("");
const regSuccess = ref("");

// 判断赛事是否可以报名
const isRegistrationOpen = computed(() => {
  return subEvent.value.status === "REGISTER_ONGOING";
});

// 枚举量转换
function sub_enum2view(type) {
  switch (type) {
    case "REGISTER_ONGOING": return "赛事报名中";
    case "REGISTER_ENDED": return "赛事报名已截止";
    case "ONGOING": return "开赛中";
    case "ENDED": return "已完赛";
    case "UPDATED": return "赛事有调整，请关注最新资讯";
    default: return "未定";
  }
}

function enum2view(type) {
  switch (type) {
    case "INDIVIDUAL": return "个人赛";
    case "TEAM": return "团队赛";
    default: return "未定";
  }
}

function formatTime(time) {
  return typeof time === "string" ? time.replace("T", " ") : "未定";
}

// 登录拦截判断
function getAuthConfigOrThrow() {
  const token = userStore?.token || localStorage.getItem('token')
  if (!token) {
    alertStore.showAlertMessage("warning", "请先登录 ꒰ঌ(🎀 ᗜ`˰´ᗜ 🌸)໒꒱ ❌");
    router.push("/Welcome/logIn")
  }
  return { Authorization: `${token}` }
}

// 获取子赛事详情
const fetchSubEventDetail = async () => {
  loading.value = true;
  error.value = "";
  try {
    const res = await axios.get(`/api/source/${sub_eventName.value}`);
    if (res.data?.status === "success") {
      subEvent.value = res.data.data;
      registration.value.sub_eventName = subEvent.value.sub_eventName || "";
      console.log(subEvent.value.eventName);
    } else {
      error.value = res.data?.msg || "加载子赛事详情失败";
    }
  } catch (err) {
    error.value = err.message || "加载子赛事详情失败";
  } finally {
    loading.value = false;
  }
}

// 从子赛事获取父赛事名
const fetchEventName = async () => {
  try {
    const res = await axios.get(`/api/source/getEventName/${sub_eventName.value}`);
    if (res.data?.status === "success") {
      registration.value.eventName = res.data.data;
    } else {
      error.value = res.data?.msg || "获取父赛事名失败";
    }
  } catch (err) {
    error.value = err.message || "获取父赛事名失败";
  }
}

// 跳转到另一个子赛事
async function goToSubEventDetail(name) {
  try {
    await router.push({ name: "sub_eventDetail", params: { sub_eventName: name } });
    window.scrollTo(0, 0);
    sub_eventName.value = name;
    await fetchSubEventDetail(); // 刷新详情
  } catch (err) {
    console.error("子赛事跳转失败:", err);
  }
}

// 提交报名
const submitRegistration = async () => {
  const config = {}
  config.headers = getAuthConfigOrThrow();
  regLoading.value = true;
  regError.value = "";
  regSuccess.value = "";
  console.log(config)
  try {
    const res = await axios.post("/api/auth/register", registration.value, config);
    if (res.data?.status === "success") {
      regSuccess.value = "报名成功！";
      registration.value.carName = "";
      registration.value.teamName = "";
      registration.value.account = "";
    } else {
      regError.value = res.data?.msg || "报名失败";
    }
  } catch (err) {
    regError.value = err.message || "报名请求失败";
  } finally {
    regLoading.value = false;
  }
}

onMounted(() => {
  if (route.name === "sub_eventDetail") {
    fetchSubEventDetail();
    fetchEventName();
  }
});
</script>

<template>
  <Header />
  <main class="subEventDetail">
    <h1 class="sub-event__title">{{ subEvent.sub_eventName }}</h1>

    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else>
      <table class="sub-event__table">
        <tr><th>名称</th><td>{{ subEvent.sub_eventName }}</td></tr>
        <tr><th>赛制</th><td>{{ enum2view(subEvent.sub_eventType) }}</td></tr>
        <tr><th>状态</th><td>{{ sub_enum2view(subEvent.status) }}</td></tr>
        <tr><th>报名截止时间</th><td>{{ formatTime(subEvent.registerEndDate) }}</td></tr>
        <tr><th>开始时间</th><td>{{ formatTime(subEvent.sub_eventStart) }}</td></tr>
        <tr><th>结束时间</th><td>{{ formatTime(subEvent.sub_eventEnd) }}</td></tr>
        <tr><th>赛场</th><td>{{ subEvent.description }}</td></tr>
      </table>

      <!-- 报名表 -->
      <section class="registration">
        <h2>报名信息</h2>
        <form @submit.prevent="submitRegistration">
          <div class="form-item">
            <label>邮箱：</label>
            <input type="email" v-model="registration.account" required />
          </div>
          <div class="form-item">
            <label>赛事名：</label>
            <input type="text" v-model="registration.eventName" readonly />
          </div>
          <div class="form-item">
            <label>分站赛名：</label>
            <input type="text" v-model="registration.sub_eventName" readonly />
          </div>
          <div class="form-item">
            <label>参赛用车名：</label>
            <input type="text" v-model="registration.carName" required />
          </div>
          <div class="form-item">
            <label>参赛队伍名：</label>
            <input type="text" v-model="registration.teamName" required />
          </div>
          <div class="form-actions">
            <button type="submit" :disabled="!isRegistrationOpen || regLoading">
              {{ !isRegistrationOpen ? '已过报名时间' : (regLoading ? '提交中...' : '提交报名') }}
            </button>
          </div>
          <div v-if="regError" class="error">{{ regError }}</div>
          <div v-if="regSuccess" class="success">{{ regSuccess }}</div>
        </form>
      </section>
    </div>
  </main>
  <Footer />
</template>

<style scoped>
.sub-event__title {
  text-align: center;
  margin: 20px 0;
}

.sub-event__table {
  width: 90%;
  max-width: 800px;
  margin: 0 auto 40px auto;
  border-collapse: collapse;
}

.sub-event__table th,
.sub-event__table td {
  border: 1px solid #ddd;
  padding: 12px;
  text-align: left;
}

.sub-event__table th {
  background: #f5f5f5;
  font-weight: bold;
}

.loading,
.error,
.success {
  text-align: center;
  font-size: 18px;
  margin-top: 20px;
}

.error {
  color: red;
}

.success {
  color: green;
}

.registration {
  width: 90%;
  max-width: 600px;
  padding: 20px;
  border: 1px solid #ccc;
  border-radius: 8px;
  margin: 0 auto 60px;
}

.registration h2 {
  text-align: center;
  margin-bottom: 20px;
}

.form-item {
  display: flex;
  flex-direction: column;
  margin-bottom: 12px;
}

.form-item label {
  font-weight: bold;
  margin-bottom: 4px;
}

.form-item input {
  padding: 8px;
  font-size: 16px;
}

.form-actions {
  text-align: center;
  margin-top: 12px;
}

.form-actions button {
  padding: 10px 20px;
  font-size: 16px;
  cursor: pointer;
  color: white;
  background-color: #ff4d4f;
  border: none;
}

.form-actions button:disabled {
  background-color: #ccc;
}
</style>
