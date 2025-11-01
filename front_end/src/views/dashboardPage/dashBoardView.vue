<script setup>
import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";
import {DataBoard, DataAnalysis} from "@element-plus/icons-vue";
import { useAlertStore } from "@/stores/alert.js";
import { useUserStore } from "@/stores/user.js";
import {ref, computed, onMounted} from "vue";
import {useRouter} from "vue-router";
import axios from "axios";

const userStore = useUserStore();
const alertStore = useAlertStore();
const router = useRouter();
const userId = computed(() => userStore.user.userId);
const userName = computed(() => userStore.user.username);
const lastUpdated = ref(null)

// 分页优化加载相关
const pagination = ref({
  currentPage: 1,
  pageSize: 20,
  total: 0
})

const rankLoading = ref(false)

// 侧边栏选项
const endpoints = ref([
  {title: "个人信息", desc: "view your profile", selected: true },
  {title: "Rating榜", desc: "to be the best racer", selected: false },
  {title: "赛事结果", desc: "review and be better", selected: false }
]);

// 选项选择
const selectEndpoint = (clicked) => {
  endpoints.value.forEach(e => e.selected = false);
  clicked.selected = true;
}

// 当前选中模块
const currentEndpoint = computed(() => endpoints.value.find(e => e.selected));

// 初始化数据
const userInfo = ref({
  userName: "default",
  avatar: "default",
  email: "default@example.com",
  realName: "default",
  teams: {
    teamName: "default"
  },
  rating: 0,
  firstRace: "default",
  firstPodium: "default",
  firstPole: "default",
  firstWin: "default",
  totalRace: 0,
  totalPodium: 0,
  totalPole: 0,
  totalWin: 0,
  registerTime: "default",
  lastConnected: "default"
})

const RPlist = ref([])
const ratingRank = ref([])
const subRank = ref([])
const currentSubEvent = ref('')             // 分站名
const currentSubType = ref('PRACTICE')      // 比赛类型
const currentEvent = ref('')                // 赛事名
const currentTotalType = ref('INDIVIDUAL')  // 显示类型（车手 / 车队）
const racerRank = ref([])
const teamRank = ref([])

// 新增：存储完整数据用于前端分页
const allSubRankData = ref([])
const allRacerRankData = ref([])
const allTeamRankData = ref([])

// 新增：存储赛事类型
const subRankType = ref('INDIVIDUAL') // 分站赛事类型
const totalRankType = ref('INDIVIDUAL') // 总榜赛事类型

const selectOptions4total = [
  { label: "车手积分榜", value: "INDIVIDUAL" },
  { label: "车队积分榜", value: "TEAM" }
]

const selectOptions4sub = [
  { label: "练习赛排名", value: "PRACTICE" },
  { label: "排位赛排名", value: "QUALIFY" },
  { label: "正赛排名", value: "RACE" }
]

// 新增：选择数据模式（分站/总榜）以及用于第一个下拉的选项
const dataMode = ref('SUB') // 'SUB' = 分站数据, 'TOTAL' = 总榜数据
const dataModeOptions = [
  { label: '分站数据', value: 'SUB' },
  { label: '总榜数据', value: 'TOTAL' }
]

// 计算用于第二个下拉的选项以及双向绑定
const optionsForSecondSelect = computed(() => dataMode.value === 'SUB' ? selectOptions4sub : selectOptions4total)

const secondSelectModel = computed({
  get() {
    return dataMode.value === 'SUB' ? currentSubType.value : currentTotalType.value
  },
  set(val) {
    if (dataMode.value === 'SUB') currentSubType.value = val
    else currentTotalType.value = val
  }
})

// 登录拦截判断
function getAuthConfigOrThrow() {
  const token = userStore?.token || localStorage.getItem('token')
  if (!token) {
    alertStore.showAlertMessage("warning", "请先登录 ꒰ঌ(🎀 ᗜ`˰´ᗜ 🌸)໒꒱ ❌");
    router.push("/Welcome/logIn")
  }
  return { Authorization: `${token}` }
}

// 封面图片路径解析
function getPreviewPath(preview) {
  // 若为空，返回一个默认图片路径
  if (preview[0] !== '/') return preview
  if (preview === 'none') {
    return '/img/NotFound.jpg'
  }
  return `http://localhost:8080/${preview.replace(/^\/?/, '')}`
}

// 时间格式转换
function formatTime(time) {
  if (time === "1970-01-01T00:00:00") return "none"
  return typeof time === "string" ? time.replace("T", " ") : "未定";
}

// 奖惩分类转换
function RPView(type) {
  switch (type) {
    case "REWARD": return "奖励"
    case "PUNISHMENT": return "判罚"
    default: return "未定"
  }
}

// 名次转换函数
function formatPosition(pos) {
  if (pos === -1) return 'DNF';
  if (pos === -2) return 'DSQ';
  return pos;
}

// 从后端获取个人信息数据
const fetchUserInfo = async () => {
  try {
    console.log(userId.value, userName.value)
    const config = {}
    config.headers = getAuthConfigOrThrow();
    const res = await axios.get(`/api/auth/${encodeURIComponent(userId.value)}/info`, config);
    if (res.data?.status === "success") {
      userInfo.value = res.data.data;
      console.log(userInfo.value)
      lastUpdated.value = new Date().toLocaleString()
    } else {
      console.warn('userInfo 接口返回异常', res);
      alertStore.showAlertMessage && alertStore.showAlertMessage("warning", "用户信息返回非预期数据")
    }
  } catch (e) {
    console.error('获取用户个人信息失败：', e)
    alertStore.showAlertMessage && alertStore.showAlertMessage("error", "获取用户个人信息失败")
  }
}

const fetchRPList = async () => {
  try {
    const config = {}
    config.headers = getAuthConfigOrThrow();
    const res = await axios.get(`/api/auth/${encodeURIComponent(userName.value)}/reward_punishment`, config);
    if (res.data?.status === "success" && Array.isArray(res.data.data)) {
      RPlist.value = res.data.data.map((rp, idx) => ({
        sub_eventName: rp.sub_eventName,
        userName: rp.userName,
        teamName: rp.teamName,
        type: rp.type,
        description: rp.description,
        // 前端自增id
        frontId: idx + 1
      }))
      lastUpdated.value = new Date().toLocaleString()
    } else {
      console.warn('RPList 接口返回异常：', res)
      alertStore.showAlertMessage && alertStore.showAlertMessage("warning", "奖惩列表返回非预期数据")
    }
  } catch (e) {
    console.error('获取奖惩表信息失败：', e)
    alertStore.showAlertMessage && alertStore.showAlertMessage("error", "获取奖惩表信息失败")
  }
}

// 获取rating榜
const fetchRatingRank = async () => {
  try {
    const res = await axios.get('/api/source/rating');
    if (res.data?.status === "success" && Array.isArray(res.data.data)) {
      ratingRank.value = res.data.data.map((r, idx) => ({
        userName: r.userName,
        rating: r.rating,
        // 前端自增id
        frontId: idx + 1
      }));
      lastUpdated.value = new Date().toLocaleString()
    } else {
      console.warn('RatingRank 接口返回异常：', res)
      alertStore.showAlertMessage && alertStore.showAlertMessage("warning", "Rating表返回非预期数据")
    }
  } catch (e) {
    console.error('获取Rating表信息失败：', e)
    alertStore.showAlertMessage && alertStore.showAlertMessage("error", "获取Rating表信息失败")
  }
}

// 前端分页函数
const updatePagedData = () => {
  const startIndex = (pagination.value.currentPage - 1) * pagination.value.pageSize;
  const endIndex = startIndex + pagination.value.pageSize;

  if (dataMode.value === 'SUB') {
    subRank.value = allSubRankData.value.slice(startIndex, endIndex).map((s, idx) => ({
      frontId: startIndex + idx + 1,
      pos: s.pos,
      userName: s.userName,
      teamName: s.teamName,
      pts: s.pts
    }));
  } else {
    if (allRacerRankData.value.length > 0) {
      racerRank.value = allRacerRankData.value.slice(startIndex, endIndex).map((r, idx) => ({
        id: startIndex + idx + 1,
        userName: r.userName,
        teamName: r.teamName,
        pts: r.pts
      }));
    } else if (allTeamRankData.value.length > 0) {
      teamRank.value = allTeamRankData.value.slice(startIndex, endIndex).map((r, idx) => ({
        id: startIndex + idx + 1,
        teamName: r.teamName,
        pts: r.pts
      }));
    }
  }
}

// 获取分站赛排名 - 前端分页
const fetchSubRank = async () => {
  try {
    if (!currentSubEvent.value.trim()) {
      alertStore.showAlertMessage && alertStore.showAlertMessage("warning", "请输入分站名")
      return
    }
    rankLoading.value = true;
    const res = await axios.get(`/api/source/sub_event/${encodeURIComponent(currentSubEvent.value)}/rank`, {
      params: {
        type: currentSubType.value
      }
    })
    if (res.data?.status === "success" && Array.isArray(res.data.data)) {
      // 对数据进行排序：正常名次在前，DNF和DSQ在后
      const sortedData = res.data.data.sort((a, b) => {
        // 如果两个都是正常名次，按名次排序
        if (a.pos >= 1 && b.pos >= 1) {
          return a.pos - b.pos;
        }
        // 如果a是正常名次，b是DNF/DSQ，a在前
        if (a.pos >= 1) return -1;
        // 如果b是正常名次，a是DNF/DSQ，b在前
        if (b.pos >= 1) return 1;
        // 两个都是DNF/DSQ，按原始顺序保持
        return 0;
      });

      allSubRankData.value = sortedData;
      subRankType.value = res.data.type; // 存储分站赛事类型
      pagination.value.total = allSubRankData.value.length;
      pagination.value.currentPage = 1;
      updatePagedData();
      lastUpdated.value = new Date().toLocaleString()
    } else {
      console.warn('SubRank 接口返回异常：', res)
      alertStore.showAlertMessage && alertStore.showAlertMessage("warning", "分站排名返回非预期数据")
    }
  } catch (e) {
    console.error('获取分站排名信息失败：', e)
    alertStore.showAlertMessage && alertStore.showAlertMessage("error", "获取分站排名信息失败")
  } finally {
    rankLoading.value = false;
  }
}

// 获取赛事总排名 - 前端分页
const fetchTotalRank = async () => {
  try {
    if (!currentEvent.value.trim()) {
      alertStore.showAlertMessage && alertStore.showAlertMessage("warning", "请输入赛事名")
      return
    }
    rankLoading.value = true;
    const res = await axios.get(`/api/source/event/${encodeURIComponent(currentEvent.value)}/rank`, {
      params: {
        type: currentTotalType.value
      }
    })
    if (res && res.data && res.data.status === 'success') {
      const data = res.data.data
      totalRankType.value = res.data.type; // 存储总榜赛事类型

      if (!Array.isArray(data) || !data.length) {
        alertStore.showAlertMessage && alertStore.showAlertMessage("info", "暂无排名数据")
        allRacerRankData.value = [];
        allTeamRankData.value = [];
        racerRank.value = []
        teamRank.value = []
        pagination.value.total = 0;
        lastUpdated.value = new Date().toLocaleString()
        return
      }

      if (data[0].userName && data[0].teamName) {
        allRacerRankData.value = data;
        allTeamRankData.value = [];
      } else if (data[0].teamName && !data[0].userName) {
        allTeamRankData.value = data;
        allRacerRankData.value = [];
      }

      pagination.value.total = data.length;
      pagination.value.currentPage = 1;
      updatePagedData();
      lastUpdated.value = new Date().toLocaleString()
    } else {
      console.warn('TotalRank 接口返回异常：', res)
      alertStore.showAlertMessage && alertStore.showAlertMessage("warning", "赛事总排名返回非预期数据")
    }
  } catch (e) {
    console.error('获取赛事总排名信息失败：', e)
    alertStore.showAlertMessage && alertStore.showAlertMessage("error", "获取赛事总排名信息失败")
  } finally {
    rankLoading.value = false;
  }
}

// 一键查询：根据 dataMode 决定调用哪个接口
const performRankQuery = async () => {
  if (dataMode.value === 'SUB') {
    await fetchSubRank()
  } else {
    await fetchTotalRank()
  }
}

// 分页处理
const handlePageChange = (page) => {
  pagination.value.currentPage = page;
  updatePagedData();
}

const handleSizeChange = (size) => {
  pagination.value.pageSize = size;
  pagination.value.currentPage = 1;
  updatePagedData();
}

const fetchAll = async () => {
  await fetchUserInfo();
  await fetchRPList();
  await fetchRatingRank();
}

onMounted(() => {
  fetchUserInfo();
  fetchRPList();
})
</script>

<template>
  <Header />
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
  <main class="dashBoard">
    <section class="bg">
      <div class="bg__container">
        <div class="bg__image">
          <el-image style="width: 100%; height: 100%;" fit="contain" src="Home.png"/>
        </div>
      </div>
    </section>
    <div class="dashBoard__container">
      <!-- 左侧 -->
      <aside class="sidebar glass">
        <h2 class="sidebar__title">管理面板</h2>
        <ul class="sidebar__endpoint-list">
          <li
              class="sidebar__endpoint-item"
              v-for="e in endpoints"
              :key="e.desc"
              :class="{ selected: e.selected }"
              @click="selectEndpoint(e)"
          >
            <el-icon>
              <DataAnalysis v-if="e.selected" />
              <DataBoard v-else />
            </el-icon>
            <div class="sidebar__endpoint-item-info">
              <div class="sidebar__endpoint-item-title">{{ e.title }}</div>
              <div class="sidebar__endpoint-item-desc">{{ e.desc }}</div>
            </div>
            <span class="sidebar__dot" :class="{ selected: e.selected }" :title="e.selected ? '已选择' : '未选择'"></span>
          </li>
        </ul>
        <div class="sidebar__footer">
          <button class="sidebar__btn" @click="fetchAll">获取数据</button>
          <div class="sidebar__small-note">最后更新: {{ lastUpdated || "—" }}</div>
        </div>
      </aside>
      <!-- 右侧工作区 -->
      <section class="workplace glass">
        <div class="workplace__container">
          <template v-if="currentEndpoint">
            <h2 class="workplace__title">{{ currentEndpoint.title }}</h2>
            <p class="workplace__desc">{{ currentEndpoint.desc }}</p>

            <div class="workplace__content">
              <!-- 个人信息 -->
              <div v-if="currentEndpoint.title === '个人信息'" class="personal-info">
                <div class="personal-info__layout">
                  <!-- 左侧：基本信息 -->
                  <el-card shadow="hover" class="info-card">
                    <div class="info-top-row">
                      <div class="avatar-section">
                        <img :src="getPreviewPath(userInfo.avatar || 'none')" alt="avatar" class="avatar"/>
                        <div class="basic-info">
                          <h3 class="username">{{ userInfo.userName }}</h3>
                          <p class="real-name">{{ userInfo.realName }}</p>
                        </div>
                      </div>
                      <div class="contact-info">
                        <p><strong>邮箱:</strong> {{ userInfo.email }}</p>
                        <p>
                          <strong>车队:</strong>
                          {{
                            Array.isArray(userInfo.teams)
                                ? (userInfo.teams.length > 0 ? userInfo.teams[0].teamName : '未加入车队')
                                : (userInfo.teams?.teamName || '未加入车队')
                          }}
                        </p>
                        <p><strong>积分:</strong> {{ userInfo.rating }}</p>
                      </div>
                    </div>

                    <!-- 统计数据网格 -->
                    <div class="stats-grid">
                      <div class="stat-item">
                        <div class="stat-number">{{ userInfo.totalRace }}</div>
                        <div class="stat-label">参赛场次</div>
                      </div>
                      <div class="stat-item">
                        <div class="stat-number">{{ userInfo.totalWin }}</div>
                        <div class="stat-label">胜利次数</div>
                      </div>
                      <div class="stat-item">
                        <div class="stat-number">{{ userInfo.totalPole }}</div>
                        <div class="stat-label">杆位次数</div>
                      </div>
                      <div class="stat-item">
                        <div class="stat-number">{{ userInfo.totalPodium }}</div>
                        <div class="stat-label">领奖台</div>
                      </div>
                    </div>

                    <!-- 时间信息两列布局 -->
                    <div class="time-info-columns">
                      <div class="time-column">
                        <div class="time-item">
                          <span class="time-label">首次参赛:</span>
                          <span class="time-value">{{ formatTime(userInfo.firstRace) }}</span>
                        </div>
                        <div class="time-item">
                          <span class="time-label">首次登台:</span>
                          <span class="time-value">{{ formatTime(userInfo.firstPodium) }}</span>
                        </div>
                        <div class="time-item">
                          <span class="time-label">首次杆位:</span>
                          <span class="time-value">{{ formatTime(userInfo.firstPole) }}</span>
                        </div>
                      </div>
                      <div class="time-column">
                        <div class="time-item">
                          <span class="time-label">首次胜利:</span>
                          <span class="time-value">{{ formatTime(userInfo.firstWin) }}</span>
                        </div>
                        <div class="time-item">
                          <span class="time-label">注册时间:</span>
                          <span class="time-value">{{ formatTime(userInfo.registerTime) }}</span>
                        </div>
                        <div class="time-item">
                          <span class="time-label">上次登录:</span>
                          <span class="time-value">{{ formatTime(userInfo.lastConnected) }}</span>
                        </div>
                      </div>
                    </div>
                  </el-card>

                  <!-- 右侧：奖惩信息 -->
                  <el-card shadow="hover" class="rp-card">
                    <h3 class="rp-card__title">奖惩记录</h3>
                    <div class="rp-table-container">
                      <el-table :data="RPlist" stripe style="width: 100%" height="auto" v-if="RPlist.length">
                        <el-table-column prop="frontId" label="#" width="50"/>
                        <el-table-column prop="sub_eventName" label="分站名称"/>
                        <el-table-column prop="userName" label="用户名"/>
                        <el-table-column prop="teamName" label="车队"/>
                        <el-table-column prop="type" label="类型">
                          <template #default="scope">
                            {{ RPView(scope.row.type) }}
                          </template>
                        </el-table-column>
                        <el-table-column prop="description" label="内容"/>
                      </el-table>
                      <div v-else class="rp-empty">
                        <p>暂无奖惩记录</p>
                      </div>
                    </div>
                  </el-card>
                </div>
              </div>
              <!-- Rating榜 -->
              <div v-else-if="currentEndpoint.title === 'Rating榜'">
                <div class="rank-table-container">
                  <el-table :data="ratingRank" stripe style="width: 100%" height="auto">
                    <el-table-column prop="frontId" label="#" width="50"/>
                    <el-table-column prop="userName" label="用户名"/>
                    <el-table-column prop="rating" label="积分"/>
                  </el-table>
                </div>
              </div>
              <!-- 赛事结果 -->
              <div v-else-if="currentEndpoint.title === '赛事结果'">
                <!-- 数据模式选择（分站 / 总榜）以及第二个下拉（动态） -->
                <div style="display:flex; align-items:center; gap:10px; margin-bottom:10px; flex-wrap:wrap">
                  <el-select v-model="dataMode" placeholder="选择数据类型" style="width:150px">
                    <el-option v-for="opt in dataModeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
                  </el-select>

                  <el-select v-model="secondSelectModel" placeholder="选择筛选条件" style="width:180px">
                    <el-option
                        v-for="option in optionsForSecondSelect"
                        :key="option.value"
                        :label="option.label"
                        :value="option.value"
                    />
                  </el-select>

                  <!-- 根据选择显示对应的输入框 -->
                  <div v-if="dataMode === 'SUB'" style="display:flex; gap:10px; align-items:center">
                    <el-input
                        v-model="currentSubEvent"
                        placeholder="请输入分站名"
                        style="width: 220px;"
                    />
                    <el-button type="primary" @click="performRankQuery" :loading="rankLoading">
                      {{ rankLoading ? '查询中...' : '查询分站排名' }}
                    </el-button>
                  </div>

                  <div v-else style="display:flex; gap:10px; align-items:center">
                    <el-input
                        v-model="currentEvent"
                        placeholder="请输入赛事名"
                        style="width: 220px;"
                    />
                    <el-button type="primary" @click="performRankQuery" :loading="rankLoading">
                      {{ rankLoading ? '查询中...' : '查询赛事总榜' }}
                    </el-button>
                  </div>
                </div>

                <!-- 分页控件 -->
                <div v-if="pagination.total > 0" style="margin-bottom: 10px;">
                  <el-pagination
                      v-model:current-page="pagination.currentPage"
                      v-model:page-size="pagination.pageSize"
                      :page-sizes="[10, 20, 50, 100]"
                      :total="pagination.total"
                      layout="total, sizes, prev, pager, next"
                      @current-change="handlePageChange"
                      @size-change="handleSizeChange"
                      background
                  />
                </div>

                <!-- 分站排名表 -->
                <div v-if="dataMode === 'SUB'" class="rank-table-container">
                  <el-table :data="subRank" stripe style="width: 100%" height="auto" v-loading="rankLoading">
                    <el-table-column prop="frontId" label="#" width="50"/>
                    <el-table-column prop="userName" label="用户名"/>
                    <el-table-column v-if="subRankType === 'TEAM'" prop="teamName" label="车队"/>
                    <el-table-column prop="pts" label="积分"/>
                    <el-table-column prop="pos" label="名次">
                      <template #default="scope">
                        {{ formatPosition(scope.row.pos) }}
                      </template>
                    </el-table-column>
                  </el-table>
                </div>

                <!-- 总榜（车手或车队）表 -->
                <div v-else class="rank-table-container">
                  <!-- 车手榜 -->
                  <el-table v-if="currentTotalType === 'INDIVIDUAL' && racerRank.length" :data="racerRank" stripe style="width:100%" height="auto" v-loading="rankLoading">
                    <el-table-column prop="id" label="#" width="50"/>
                    <el-table-column prop="userName" label="用户名"/>
                    <el-table-column v-if="totalRankType === 'TEAM'" prop="teamName" label="车队"/>
                    <el-table-column prop="pts" label="积分"/>
                  </el-table>

                  <!-- 车队榜 -->
                  <div v-else-if="currentTotalType === 'TEAM'">
                    <div v-if="totalRankType === 'INDIVIDUAL'" style="padding:20px; text-align:center; color: rgba(255,255,255,0.7)">
                      非团队赛，无车队积分榜
                    </div>
                    <el-table v-else-if="teamRank.length" :data="teamRank" stripe style="width:100%" height="auto" v-loading="rankLoading">
                      <el-table-column prop="id" label="#" width="50"/>
                      <el-table-column prop="teamName" label="车队"/>
                      <el-table-column prop="pts" label="积分"/>
                    </el-table>
                  </div>

                  <div v-else style="padding:12px; color: rgba(255,255,255,0.7)">
                    {{ rankLoading ? '加载中...' : '暂无排行榜数据' }}
                  </div>
                </div>
              </div>
            </div>
          </template>
        </div>
      </section>
    </div>
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

.dashBoard {
  position: relative;
  margin: 0;
  display: block;
  flex-direction: column;

  &__container {
    position: absolute;
    z-index: 2; /* 在蒙层之上 */
    display: flex;
    gap: 20px;
    padding: 36px;
    align-items: flex-start;
    justify-content: center;
    /* 将 transform 换成 margin-top 固定偏移，避免 reflow 导致跳动 */
    margin-top: -800px;
  }

  .glass {
    background: rgba(255,255,255,0.03);
    border: 1px solid rgba(255,255,255,0.06);
    box-shadow: 0 8px 30px rgba(2,6,23,0.55);
    backdrop-filter: blur(10px) saturate(120%);
    -webkit-backdrop-filter: blur(10px) saturate(120%);
    border-radius: 12px;
    padding: 16px;
    color: #eaf2ff;
  }
}

.bg {
  flex: 1;
  height: 100vh;
  position: relative;
  overflow: hidden;
  inset: 0;

  &__container {
    position: absolute;
    inset: 0;
    z-index: 0;
    animation: fadeInBg 0.8s ease-out 0s both;
    &::after {
      content: '';
      position: absolute;
      inset: 0;
      background: rgba(0, 0, 0, 0.45);
    }
  }

  &__image {
    width: 100%;
    height: 100%;
  }
}

/* 侧栏样式保留 */
.sidebar {
  width: 320px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-height: 360px;

  &__title {
    font-weight: 600;
    display:flex;
    align-items:center;
    gap:8px;
    color: #fff;
  }

  &__endpoint-list {
    list-style: none;
    padding: 0;
    margin: 8px 0 0 0;
    display:flex;
    flex-direction:column;
    gap:8px;
  }

  &__endpoint-item {
    overflow: visible;
    display:flex;
    align-items:center;
    gap:12px;
    padding:10px;
    border-radius:8px;
    transition: background .18s;
    &:hover {
      background: rgba(94,53,177,0.6);
      cursor:pointer;
    }
    &.selected {
      background: rgba(94,53,177,1);
    }
  }

  &__endpoint-item-desc {
    font-size:12px;
    color: rgba(255,255,255,0.6);
    margin-top:4px;
  }

  &__dot {
    position: absolute;
    right: 20px;
    width:10px;
    height:10px;
    border-radius:50%;
    background: rgba(255,255,255,0.14);
    &.selected {
      background: #9ff0b5;
      box-shadow: 0 0 8px rgba(159,240,181,0.12);
    }
  }

  &__footer {
    margin-top:auto;
    display:flex;
    flex-direction:column;
    gap:8px;
    align-items:flex-start;
  }

  &__btn {
    background: transparent;
    border: 1px solid rgba(255,255,255,0.06);
    color: #dfeef0;
    padding:6px 10px;
    border-radius:8px;
    transition: background .18s;
    &:hover {
      background: rgba(94,53,177,0.6);
      cursor:pointer;
    }
  }

  &__small-note {
    font-size:12px;
    color: rgba(255,255,255,0.6);
  }
}

/* 确保工作区容器有合适的高度 */
.workplace {
  &__content {
    min-height: 400px;
    display: flex;
    flex-direction: column;
    min-width: 900px;
  }
}

/* 固定高度排行列表容器（约显示10行，按行高估算） */
.rank-table-container {
  max-height: 480px; /* 10 rows * ~48px each (根据表格行高调整) */
  overflow-y: auto;
  padding-right: 8px; /* 给滚动条留出空间 */
}

.personal-info {
  .personal-info__layout {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 20px;
    align-items: start;
  }

  .info-card {
    max-width: 100%;
    min-height: 400px;

    .info-top-row {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 20px;
      gap: 20px;

      .avatar-section {
        display: flex;
        align-items: center;
        gap: 16px;
        flex: 1;

        .avatar {
          width: 80px;
          height: 80px;
          border-radius: 50%;
          object-fit: cover;
          border: 3px solid #673ab7;
        }

        .basic-info {
          .username {
            margin: 0 0 8px 0;
            font-size: 1.5em;
            font-weight: 600;
            color: #333;
          }

          .real-name {
            margin: 0;
            color: #666;
            font-size: 1em;
          }
        }
      }

      .contact-info {
        flex: 1;
        text-align: right;
        p {
          margin: 6px 0;
          color: #333;
          line-height: 1.4;
        }
      }
    }

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 12px;
      margin: 20px 0;
      padding: 16px;
      background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
      border-radius: 8px;
      border: 1px solid #e9ecef;

      .stat-item {
        text-align: center;
        padding: 8px;

        .stat-number {
          font-size: 1.8em;
          font-weight: 700;
          color: #673ab7;
          margin-bottom: 4px;
        }

        .stat-label {
          font-size: 0.85em;
          color: #666;
          font-weight: 500;
        }
      }
    }

    .time-info-columns {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 20px;
      margin-top: 16px;

      .time-column {
        .time-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 8px 0;
          border-bottom: 1px solid #f0f0f0;

          &:last-child {
            border-bottom: none;
          }

          .time-label {
            font-weight: 500;
            color: #666;
            font-size: 0.9em;
          }

          .time-value {
            color: #333;
            font-size: 0.9em;
            text-align: right;
          }
        }
      }
    }
  }

  .rp-card {
    max-width: 100%;
    min-height: 400px;

    &__title {
      margin: 0 0 16px 0;
      font-size: 1.25em;
      font-weight: 600;
      color: #333;
      text-align: center;
    }

    .rp-table-container {
      max-height: 350px;
      overflow-y: auto;
    }

    .rp-empty {
      text-align: center;
      padding: 40px 0;
      color: #666;
    }
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .personal-info {
    .personal-info__layout {
      grid-template-columns: 1fr;
      gap: 16px;
    }

    .info-card {
      .info-top-row {
        flex-direction: column;
        text-align: center;

        .contact-info {
          text-align: center;
          width: 100%;
        }
      }

      .stats-grid {
        grid-template-columns: repeat(2, 1fr);
        gap: 10px;
      }

      .time-info-columns {
        grid-template-columns: 1fr;
        gap: 10px;
      }
    }
  }
}

/* 响应式：小屏幕时改为上下布局 */
@media (max-width: 1024px) {
  .Events__container {
    flex-direction: column; /* 垂直排列 */
    min-height: auto;
  }

  .setting {
    width: 100%;  /* 设置宽度为100% */
    border-left: none;
    border-top: 1px solid #ddd;
  }

  .Events__right-part {
    width: 100%;  /* 确保列表区在小屏时也占满宽度 */
    padding: 0 10px;  /* 适当的内边距，避免内容贴边 */
  }
}
@keyframes fadeInLeft {
  0%   { opacity: 0; transform: translate(0, 0) translateX(-0.5rem); }
  100% { opacity: 1; transform: translate(0, 0) translateX(0); }
}
@keyframes fadeInBottom {
  0%   { opacity: 0; transform: translate(0, 0) translateY(0.5rem); }
  100% { opacity: 1; transform: translate(0, 0) translateY(0); }
}
</style>