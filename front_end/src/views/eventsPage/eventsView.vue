<script setup>
import Header from "@/components/Header.vue";
import Footer from "@/components/Footer.vue";
import {onMounted, ref} from "vue";
import { Folder, FolderOpened } from "@element-plus/icons-vue";
import axios from "axios"

// 表单状态（分类cat）
const form = ref({
  cat: null
})

const listKey = ref(0)
const isTransitionEnabled = ref(false);

// 分类数据
const categories = ref([])
const categoriesLoading = ref(true)

// 赛事列表数据
const events = ref([])
const loading = ref(false)
const error = ref(null)

// 封面图片路径解析
function getPreviewPath(preview) {
  // 若为空，返回一个默认图片路径
  if (preview === 'none') {
    return '/img/NotFound.jpg'
  }
  return `http://localhost:8080/${preview.replace(/^\/?/, '')}`
}

// 分类选择
const selectCatag = (cat) => {
  form.value.cat = cat.catName
  fetchEvents()
  isTransitionEnabled.value = true;
  listKey.value = Date.now()
}

const clearCategory = () => {
  form.value.cat = null
  fetchEvents()
  isTransitionEnabled.value = true;
  listKey.value = Date.now()
}

// 获取分类（后端提供cat信息）
const fetchCategories = async () => {
  categoriesLoading.value = true
  try {
    const res = await axios.get("/api/source/categories")
    if (res.data?.status === "success" && Array.isArray(res.data.data)) {
      categories.value = res.data.data.map(cat => ({
        catId: cat.id,
        catName: cat.catName,
        description: cat.description
      }))
    } else {
      console.warn("后端返回格式异常:", res.data)
      categories.value = []
    }
  } catch (err) {
    console.error("获取标签失败:", err)
    error.value = err.message || "标签获取失败"
    categories.value = []
  } finally {
    categoriesLoading.value = false
  }
}

// 获取赛事资源（后端提供）
const fetchEvents = async () => {
  loading.value = true
  error.value = null
  try {
    const params = {
      catName: form.value.cat ?? ""
    }
    const res = await axios.get("/api/source/events", { params })
    if (res.data?.status === "success" && Array.isArray(res.data.data)) {
      events.value = res.data.data.map((item, index) => ({
        eventId: index + 1,
        eventName: item.eventName,
        description: item.description,
        eventAvatar: getPreviewPath(item.eventAvatar),
        linkURL: item.linkURL,
        type: item.type,
        status: item.status,
        num: item.num
      }))
    } else {
      events.value = []
      console.warn("events 返回格式异常", res.data)
    }
  } catch (err) {
    console.error("获取赛事资源失败:", err)
    error.value = err.message || "获取赛事资源失败"
    events.value = []
  } finally {
    loading.value = false
  }
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

// 初始化
onMounted( () => {
  fetchCategories()
  fetchEvents()
})
</script>

<template>
  <Header />
  <main class="Events">
    <div class="Events__container">
      <!-- 设定选择区 -->
      <section class="setting">
        <div class="setting__inner">
          <div class="setting__desc">
            <img src="../../components/icons/Logo.png" alt="LOGO" class="setting__img" />
            <p>在此处选择报名你要参加的赛事</p>
          </div>
          <form class="setting__sv" name="events sort">
            <div class="form_group">
              <div class="categories">
                <button
                  type="button"
                  class="category-chip"
                  :class="{ active: form.cat === null }"
                  @click="clearCategory">
                  <el-icon>
                    <FolderOpened v-if="form.cat === null" />
                    <Folder v-else />
                  </el-icon>
                  全部赛事</button>
                <template v-if="categoriesLoading">
                  <span class="cat-loading">正在加载赛事列表...</span>
                </template>
                <template v-else>
                  <button
                    v-for="cat in categories"
                    :key="cat.catName"
                    type="button"
                    class="category-chip"
                    :class="{ active: form.cat === cat.catName }"
                    @click="selectCatag(cat)">
                    <el-icon>
                      <FolderOpened v-if="form.cat === cat.catName" />
                      <Folder v-else />
                    </el-icon>
                    <span class="cat-name">{{ cat.catName }}</span>
                  </button>
                </template>
              </div>
            </div>
          </form>
        </div>
      </section>
      <!-- 列表展示区 -->
      <div class="Events__right-part">
        <section class="list">
          <div class="list__inner">
            <!-- 加载状态 -->
            <div v-if="loading" class="list__status list__loading">正在获取下分赛事列表</div>
            <!-- 错误状态 -->
            <div v-else-if="error" class="list__status list__error">出现错误：{{ error }}</div>
            <!-- 空数据状态 -->
            <div v-else-if="events.length === 0" class="list__status list__empty">
              <p>当前分类下暂无赛事</p>
            </div>
            <!-- 正常显示列表 -->
            <div v-else class="list__grid" :key="listKey" :class="{'fade-in-animation': isTransitionEnabled}">
              <div v-for="event in events" :key="event.eventId" class="events__card">
                <!-- 缩略图 -->
                <a :href="event.linkURL" target="_blank">
                  <img :src="event.eventAvatar" :alt="event.title" class="card__thumb" />
                  </a>
                <a :href="event.description" target="_blank" class="card__title">
                  {{ event.description }}
                </a>
                <!-- 点击数、点赞数、下载数、发布时间等元数据 -->
                <div class="card__meta">
                  <span>{{ event.eventName }}</span>
                  <span>{{ enum2view(event.type) }}</span>
                  <span>{{ enum2view(event.status) }}</span>
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>
  </main>
  <Footer />
</template>

<style scoped lang="scss">
.Events {
  position: relative;
  margin: 0;
  background: var(--color-setting-bg);

  &__container {
    display: flex;
    flex-direction: row;
    min-height: 100vh;
  }

  &__right-part {
    flex: 1;
    display: flex;
    flex-direction: column;
  }

  &__layout {
    display: flex;
    flex-direction: row;
    align-items: flex-start;
    gap: 1rem;
  }

  &__content {
    flex: 1; /* 占据剩余空间 */
    padding: 1rem;
    display: flex;
    flex-direction: column;
    gap: 1rem; /* 搜索栏和列表间距 */
  }
}

/* setting区 */
.setting {
  width: 260px; /* 设定区宽度，可调 */
  transform: translate(0, 5%);
  background: var(--color-setting-bg);
  border-left: 1px solid #ddd;
  padding: 1rem;
  flex-shrink: 0;
  height: 700px;

  &__inner {
    display: flex;
    flex-direction: column;
    gap: 1rem;
    animation: fadeInLeft 0.3s ease-out 0.3s both;
  }

  &__desc {
    text-align: center;

    .setting__img {
      width: 125px;
      height: auto;
      margin-bottom: 0.5rem;
      border-radius: 50%;
      border: 2px solid #666666;  /* 轮廓颜色 + 粗细 */
      object-fit: cover;          /* 图片裁剪填充 */
    }
  }

  .form_group {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;

    label {
      font-weight: bold;
      margin-top: 0.5rem;
    }

    .categories {
      display: flex;
      flex-direction: column; /* 垂直排列 */
      gap: 8px;              /* 标签之间的间距 */

      .category-chip {
        cursor: pointer;
        border-radius: 0 !important; /* 去掉圆角 */
        border: none !important;     /* 去掉边框 */
        background-color: #f5f5f5;
        color: #333;
        display: flex;
        align-items: center;
        gap: 6px; /* 图标和文字的间距 */
        width: 100%; /* 占满一行 */
        justify-content: flex-start;
        font-size: 14px;
        padding: 8px 12px;
        transition: transform 0.2s ease;

        /* 非选中时悬停效果 */
        &:hover {
          background-color: #e0e0e0;
          transform: translateX(5px)
        }

        &.active {
          background-color: #7e57c2;
          color: #fff;
          /* 选中时悬停效果 */
          &:hover {
            background-color: #5e35b1;
          }
        }
      }

      .cat-loading {
        font-size: 0.9rem;
        color: #666;
      }
    }
  }
}

/* list区 */
.list {
  flex: 1;
  padding: 20px 40px;
  background: #fafafa;
  animation: fadeInBottom 0.5s ease-out 0.5s both;
  background: var(--color-detail-bg);

  &__inner {
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }

  &__status {
    text-align: center;
    font-size: 16px;
    color: #666;
    padding: 40px 0;
  }

  &__error {
    color: #c00;
  }

  &__grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
    gap: 1rem;

    .events__card {
      border: 1px solid #ddd;
      border-radius: 8px;
      padding: 0.5rem;
      background: #fff;
      transition: transform 0.2s ease;
      background: var(--color-detail-bg);

      &:hover {
        transform: translateY(-3px);
      }

      .card__thumb {
        width: 100%;
        height: 150px;
        object-fit: cover;
        display: block;
        border-radius: 6px;
      }

      .card__title {
        display: block;
        font-weight: bold;
        margin: 0.5rem 0;
        text-decoration: none;
        color: #333;

        &:hover {
          color: #5a2a92;
        }
      }

      .card__meta {
        display: grid;
        grid-template-columns: 1fr 1fr; /* 两列平均分 */
        gap: 10px 28px; /* 行间距、列间距可调 */
        padding: 0 10px 10px 10px;
        font-size: 0.85rem;
        color: var(--color-list-text);
        text-align: center;
      }
    }
  }
}

/* 自定义 el-select 的悬停和过渡 */
.select-option {
  .el-select-dropdown__item,
  .el-select-option,
  [role="option"] {
    transition: transform .18s ease, color .18s ease, background-color .18s ease;
    transform: translateX(0);
    will-change: transform, color, background-color;
  }

  .el-select-dropdown__item:hover,
  .el-select-option:hover,
  [role="option"]:hover {
    transform: translateX(6px);
    color: #5a2a92;
    background-color: rgba(90,42,146,0.06);
  }

  .el-select-dropdown__item.selected,
  .el-select-option.is-selected,
  [role="option"][aria-selected="true"] {
    background-color: #7e57c2;
    color: #fff;
  }
}
:deep(.el-select__wrapper.is-focused) {
  box-shadow: 0 0 0 1px #7e57c2 !important;
}

.fade-in-animation {
  animation: fadeInBottom 0.5s ease-out 0.5s both;
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