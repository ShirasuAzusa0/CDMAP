<script setup>
import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";
import { Management } from "@element-plus/icons-vue";
import { useAlertStore } from "@/stores/alert.js";
import { useUserStore } from '@/stores/user.js'
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";

const userStore = useUserStore()
const alertStore = useAlertStore();
const router = useRouter()

// 侧栏项
const endpoints = ref([
  { title: "处理报名信息", desc: "handle the register application", selected: false },
  { title: "上传奖惩信息", desc: "upload the reward and punishments records", selected: false }
]);

const selectEndpoint = (clicked) => {
  endpoints.value.forEach(e => e.selected = false)
  clicked.selected = true
}

// 当前选中模块
const currentEndpoint = computed(() => endpoints.value.find(e => e.selected))

/* -----------------------------
   报名信息（处理报名）
   ----------------------------- */
const registers = ref([]) // each item from backend + local editing fields
const lastUpdated = ref(null)

const statusOptions = [
  { label: "待确认", value: "UNCONFIRMED" },
  { label: "已通过", value: "CONFIRMED" },
  { label: "被驳回", value: "REJECTED" }
]

function getAuthConfigOrThrow() {
  const token = userStore?.token || localStorage.getItem('token')
  if (!token) {
    alertStore.showAlertMessage("warning", "请先登录 ꒰ঌ(🎀 ᗜ`˰´ᗜ 🌸)໒꒱ ❌");
    router.push("/Welcome/logIn")
  }
  return { Authorization: `${token}` }
}

const selectedStatusFilter = ref('UNCONFIRMED')

// fetch registers from backend
const fetchRegisters = async () => {
  try {
    const config = {}
    config.headers = getAuthConfigOrThrow()
    config.params = { status: selectedStatusFilter.value }
    const res = await axios.get('/api/admin/register', config)
    // assume res.data is an array of objects from backend
    console.log(res.data.data)
    if (res && res.data && Array.isArray(res.data.data)) {
      registers.value = res.data.data.map((r, idx) => ({
        // 获取后端数据
        registerId: r.registerId,
        userName: r.userName,
        teamName: r.teamName,
        eventName: r.eventName,
        sub_eventName: r.sub_eventName,
        carName: r.carName,
        // 可编辑项
        statusEditable: (r.status || 'UNCONFIRMED'),
        descriptionEditable: r.description || '',
        // 前端自增id
        frontId: idx + 1
      }))
      lastUpdated.value = new Date().toLocaleString()
      alertStore.showAlertMessage && alertStore.showAlertMessage("success", "报名信息已刷新")
    } else {
      console.warn('register 接口返回异常：', res)
      alertStore.showAlertMessage && alertStore.showAlertMessage("warning", "刷新报名信息返回非预期数据")
    }
  } catch (err) {
    console.error('获取报名信息失败：', err)
    alertStore.showAlertMessage && alertStore.showAlertMessage("error", "获取报名信息失败")
  }
}

// send updates to backend: only send registerId, status (uppercase), description
const updateRegisters = async () => {
  try {
    const config = {}
    config.headers = getAuthConfigOrThrow()
    const payload = registers.value.map(r => ({
      registerId: r.registerId,
      status: (r.statusEditable || 'unconfirmed').toUpperCase(),
      description: r.descriptionEditable || ''
    }))
    const res = await axios.post('/api/admin/handle', payload, config)
    // 以后端返回判断成功与否（假设成功）
    alertStore.showAlertMessage && alertStore.showAlertMessage("success", "报名处理已提交")
    // 刷新数据
    await fetchRegisters()
  } catch (err) {
    console.error('提交报名处理失败：', err)
    alertStore.showAlertMessage && alertStore.showAlertMessage("error", "提交报名处理失败")
  }
}

/* -----------------------------
   奖惩信息（上传）逻辑
   ----------------------------- */
// 填写表单模型
const rpForm = ref({
  sub_eventName: '',
  userName: '',
  teamName: '',
  type: 'REWARD', // 'REWARD' or 'PUNISHMENT'
  description: ''
})

// 队列（保存的、待提交的项）
const rpQueue = ref([]) // each item: { sub_eventName, userName, teamName, type, description }

// 保存当前表单到队列（或编辑队列项）
const selectedQueueIndex = ref(-1) // -1 表示新建
const saveToQueue = () => {
  // basic validation
  if (!rpForm.value.sub_eventName || !rpForm.value.userName) {
    alertStore.showAlertMessage && alertStore.showAlertMessage("warning", "请填写 sub_eventName 和 userName")
    return
  }
  const copy = {
    sub_eventName: rpForm.value.sub_eventName,
    userName: rpForm.value.userName,
    teamName: rpForm.value.teamName,
    type: rpForm.value.type,
    description: rpForm.value.description
  }
  if (selectedQueueIndex.value >= 0 && selectedQueueIndex.value < rpQueue.value.length) {
    // 编辑现有项
    rpQueue.value[selectedQueueIndex.value] = copy
    selectedQueueIndex.value = -1
  } else {
    // 新增
    rpQueue.value.push(copy)
  }
  // 清空表单（可按需保留）
  rpForm.value = { sub_eventName: '', userName: '', teamName: '', type: 'REWARD', description: '' }
  alertStore.showAlertMessage && alertStore.showAlertMessage("success", "已保存到队列")
}

// 选中队列某项，载入表单
const editQueueItem = (idx) => {
  const item = rpQueue.value[idx]
  if (!item) return
  rpForm.value = { ...item }
  selectedQueueIndex.value = idx
}

// 从队列删除
const removeQueueItem = (idx) => {
  rpQueue.value.splice(idx, 1)
  if (selectedQueueIndex.value === idx) selectedQueueIndex.value = -1
}

// 提交队列到后端
const submitQueue = async () => {
  try {
    if (rpQueue.value.length === 0) {
      alertStore.showAlertMessage && alertStore.showAlertMessage("warning", "队列为空")
      return
    }
    const payload = rpQueue.value.map(i => ({
      sub_eventName: i.sub_eventName,
      userName: i.userName,
      teamName: i.teamName,
      type: (i.type || 'REWARD').toUpperCase(),
      description: i.description || ''
    }))

    const config = {}
      config.headers = getAuthConfigOrThrow()
    const res = await axios.post('/api/admin/reward_punishment_update', payload, config)
    alertStore.showAlertMessage && alertStore.showAlertMessage("success", "奖惩信息已提交")
    rpQueue.value = []
    selectedQueueIndex.value = -1
  } catch (err) {
    console.error('提交奖惩信息失败：', err)
    alertStore.showAlertMessage && alertStore.showAlertMessage("error", "提交奖惩信息失败")
  }
}

/* -----------------------------
   刷新全部（侧栏按钮）
   ----------------------------- */
const fetchAll = async () => {
  // 目前只从后端拉报名表
  await fetchRegisters()
  // 若需拉取奖惩已有记录，可在此调用对应接口（如果存在）
}
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

  <main class="manage">
    <section class="bg">
      <div class="bg__container">
        <div class="bg__image">
          <el-image style="width: 100%; height: 100%;" fit="contain" src="Home.png"/>
        </div>
      </div>
    </section>

    <div class="manage__container">
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
            <el-icon><Management /></el-icon>
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
              <!-- 处理报名信息 -->
              <div v-if="currentEndpoint.title === '处理报名信息'">
                <!-- 状态筛选下拉 -->
                <div style="margin-bottom: 12px; display: flex; align-items: center; gap: 8px;">
                  <span>筛选状态：</span>
                  <el-select v-model="selectedStatusFilter" placeholder="选择状态" size="small" style="width: 160px;">
                    <el-option label="待确认" value="UNCONFIRMED" />
                    <el-option label="已通过" value="CONFIRMED" />
                    <el-option label="被驳回" value="REJECTED" />
                  </el-select>
                </div>
                <div style="display:flex; justify-content:flex-end; gap:8px; margin-bottom:12px;">
                  <el-button size="small" type="primary" @click="fetchRegisters">刷新</el-button>
                  <el-button size="small" type="success" @click="updateRegisters">更新</el-button>
                </div>

                <div class="register-table-container">
                  <el-table :data="registers" style="width:100%" stripe height="auto">
                    <el-table-column prop="frontId" label="#" width="60" />
                    <el-table-column prop="userName" label="用户名" />
                    <el-table-column prop="teamName" label="队伍" />
                    <el-table-column prop="eventName" label="赛事" />
                    <el-table-column prop="sub_eventName" label="子项目" />
                    <el-table-column prop="carName" label="车辆" />
                    <!-- status editable -->
                    <el-table-column label="状态" width="160">
                      <template #default="{ row }">
                        <el-select v-model="row.statusEditable" placeholder="status" clearable>
                          <el-option
                              v-for="opt in statusOptions"
                              :key="opt.value"
                              :label="opt.label"
                              :value="opt.value"
                          />
                        </el-select>
                      </template>
                    </el-table-column>
                    <!-- description editable -->
                    <el-table-column label="说明">
                      <template #default="{ row }">
                        <el-input v-model="row.descriptionEditable" placeholder="说明" />
                      </template>
                    </el-table-column>
                  </el-table>
                </div>
              </div>

              <!-- 上传奖惩信息 -->
              <div v-else-if="currentEndpoint.title === '上传奖惩信息'">
                <div class="rp-layout">
                  <!-- 左：填写表单 -->
                  <div class="rp-form">
                    <div class="panel">
                      <h3>填写奖惩信息</h3>
                      <el-form label-position="top" :model="rpForm">
                        <el-form-item label="sub_eventName">
                          <el-input v-model="rpForm.sub_eventName" />
                        </el-form-item>
                        <el-form-item label="userName">
                          <el-input v-model="rpForm.userName" />
                        </el-form-item>
                        <el-form-item label="teamName">
                          <el-input v-model="rpForm.teamName" />
                        </el-form-item>
                        <el-form-item label="type">
                          <el-select v-model="rpForm.type">
                            <el-option label="REWARD" value="REWARD" />
                            <el-option label="PUNISHMENT" value="PUNISHMENT" />
                          </el-select>
                        </el-form-item>
                        <el-form-item label="description">
                          <el-input v-model="rpForm.description" type="textarea" rows="3" />
                        </el-form-item>
                        <div style="display:flex; gap:8px; margin-top:8px;">
                          <el-button type="primary" @click="saveToQueue">保存到队列</el-button>
                          <el-button @click="() => { rpForm.sub_eventName=''; rpForm.userName=''; rpForm.teamName=''; rpForm.type='REWARD'; rpForm.description=''; selectedQueueIndex=-1 }">重置</el-button>
                        </div>
                      </el-form>
                    </div>
                  </div>

                  <!-- 右：队列 -->
                  <div class="rp-queue">
                    <div class="panel">
                      <h3>当前保存队列</h3>
                      <div class="scroll-table">
                        <el-table :data="rpQueue" style="width:100%" stripe>
                          <el-table-column type="index" label="#" width="60" />
                          <el-table-column prop="sub_eventName" label="子项目" />
                          <el-table-column prop="userName" label="用户名" />
                          <el-table-column prop="teamName" label="队伍" />
                          <el-table-column prop="type" label="类型" width="110" />
                          <el-table-column prop="description" label="说明" />
                          <el-table-column label="操作" width="160">
                            <template #default="{ row, $index }">
                              <el-button size="mini" @click="editQueueItem($index)">编辑</el-button>
                              <el-button size="mini" type="danger" @click="removeQueueItem($index)">删除</el-button>
                            </template>
                          </el-table-column>
                        </el-table>
                      </div>
                    </div>

                    <div style="display:flex; gap:8px; margin-top:12px;">
                      <el-button type="primary" @click="submitQueue">提交</el-button>
                      <el-button @click="rpQueue = []">清空队列</el-button>
                    </div>
                  </div>
                </div>
              </div>
            </div>

          </template>

          <template v-else>
            <el-empty description="请选择左侧的一个管理功能" />
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

/* 少量 layout 调整，移除不稳定 transform 改为 margin-top 固定偏移，避免点击导致位置跳动 */
.manage {
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

/* 省略 bg 部分（与你原来一致） */
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

/* 工作区 */
.workplace {
  flex: 1;
  min-width: 950px;

  &__title {
    font-size: 20px;
    font-weight: 600;
    color: #fff;
    margin-bottom: 10px;
  }

  &__desc {
    color: rgba(255, 255, 255, 0.6);
    margin-bottom: 20px;
  }

  &__content {
    margin-top: 10px;
  }
}

/* 奖惩两栏布局 */
.rp-layout {
  display: flex;
  gap: 16px;
}

.rp-form {
  flex: 1 1 45%;
}

.rp-queue {
  flex: 1 1 55%;
  max-height: 62vh;
  overflow: auto;
}

.workplace.glass .rp-form,
.workplace.glass .rp-queue {
  background: transparent;   /* 透明，避免双重背景 */
  border: none;              /* 去掉边框 */
  box-shadow: none;          /* 去掉阴影 */
  padding: 0;                /* 内部使用 .panel 控制内边距 */
}

/* 轻量内部卡片（用于表单/队列），视觉上与外层区分但不重复 heavy glass */
.rp-form .panel,
.rp-queue .panel {
  background: rgba(255,255,255,0.03); /* 更轻的玻璃感（可调整） */
  border: 1px solid rgba(255,255,255,0.04);
  border-radius: 10px;
  padding: 12px;
  box-shadow: 0 6px 18px rgba(2,6,23,0.28); /* 轻微阴影 - 可删 */
}

.scroll-table {
  max-height: 480px; /* 约可显示 10 行 */
  overflow-y: auto;
  padding-right: 8px;
}

.register-table-container {
  max-height: 480px; /* 10 rows * ~48px each (根据表格行高调整) */
  overflow-y: auto;
  padding-right: 8px; /* 给滚动条留出空间 */
}
</style>
