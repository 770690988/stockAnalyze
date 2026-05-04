<template>
  <div class="watchlist-page">
    <header class="page-header">
      <div class="header-left">
        <span class="header-icon">📊</span>
        <h1 class="header-title">自选板块管理</h1>
      </div>
      <div class="header-right">
        <el-button size="small" @click="typeDrawerVisible = true">
          <el-icon><Setting /></el-icon> 板块类型管理
        </el-button>
        <el-button type="danger" plain size="small" @click="handleLogout">退出登录</el-button>
        <span style="font-size:13px;color:#606266;">👤 {{ username }}</span>
      </div>
    </header>

    <div class="main-layout">
      <BkPanel
        :bkList="bkList"
        :selectedId="selectedBk?.id"
        @select="handleSelectBk"
        @refresh="loadBkList"
        @deleted="handleBkDeleted"
      />
      <StockPanel :selectedBk="selectedBk" />
    </div>

    <!-- 板块类型管理抽屉 -->
    <el-drawer
      v-model="typeDrawerVisible"
      title="板块类型管理"
      size="500px"
      direction="rtl"
    >
      <BkTypePanel @change="loadBkList" />
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Setting } from '@element-plus/icons-vue'
import BkPanel from '../components/BkPanel.vue'
import StockPanel from '../components/StockPanel.vue'
import BkTypePanel from '../components/BkTypePanel.vue'
import { getBkList } from '../api/watchlist'
import { useRouter } from 'vue-router'

const username = localStorage.getItem('username')

const bkList = ref([])
const selectedBk = ref(null)
const typeDrawerVisible = ref(false)

const loadBkList = async () => {
  const res = await getBkList()
  bkList.value = res
}

const handleSelectBk = (bk) => {
  selectedBk.value = bk
}

const handleBkDeleted = (id) => {
  if (selectedBk.value?.id === id) {
    selectedBk.value = null
  }
}

const router = useRouter()

const handleLogout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  router.push('/login')
}

onMounted(() => {
  loadBkList()
})
</script>

<style scoped>
.watchlist-page {
  min-height: 100vh;
  background: #f0f2f5;
  font-family: 'PingFang SC', 'Microsoft YaHei', sans-serif;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 56px;
  background: #fff;
  border-bottom: 1px solid #e8eaed;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-icon {
  font-size: 22px;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
  margin: 0;
}

.main-layout {
  display: flex;
  gap: 16px;
  padding: 16px;
  height: calc(100vh - 56px);
  box-sizing: border-box;
}
</style>
