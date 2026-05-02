<template>
  <main class="stock-panel">
    <template v-if="selectedBk">
      <div class="panel-header">
        <div class="panel-title-group">
          <span class="panel-title">{{ selectedBk.bkName }}</span>
          <span class="panel-subtitle">共 {{ stockList.length }} 只股票</span>
        </div>
        <div class="panel-header-actions">
          <el-button size="small" @click="loadMoneyFlow" :loading="moneyFlowLoading">
            <el-icon><TrendCharts /></el-icon> 查看资金流向
          </el-button>
          <el-button type="primary" size="small" @click="openAddDialog">
            <el-icon><Plus /></el-icon> 添加股票
          </el-button>
        </div>
      </div>

      <!-- 资金流向面板 -->
      <div v-if="showMoneyFlow && moneyFlowList.length > 0" class="money-flow-section">
        <div class="section-label">最新资金流向</div>
        <el-table :data="moneyFlowList" size="small" stripe class="money-flow-table">
          <el-table-column prop="stockCode" label="代码" width="90" />
          <el-table-column prop="stockName" label="名称" width="100" />
          <el-table-column prop="stockPrice" label="现价" width="80" />
          <el-table-column prop="stockPriceRate" label="涨跌幅" width="90">
            <template #default="{ row }">
              <span :class="row.stockPriceRate >= 0 ? 'rise' : 'fall'">
                {{ row.stockPriceRate >= 0 ? '+' : '' }}{{ row.stockPriceRate }}%
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="mainNet" label="主力净流入" width="120">
            <template #default="{ row }">
              <span :class="row.mainNet >= 0 ? 'rise' : 'fall'">{{ formatAmount(row.mainNet) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="superNet" label="超大单净流入" width="120">
            <template #default="{ row }">
              <span :class="row.superNet >= 0 ? 'rise' : 'fall'">{{ formatAmount(row.superNet) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="largeNet" label="大单净流入" width="110">
            <template #default="{ row }">
              <span :class="row.largeNet >= 0 ? 'rise' : 'fall'">{{ formatAmount(row.largeNet) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="largeNet" label="小单净流入" width="110">
            <template #default="{ row }">
              <span :class="row.smallNet >= 0 ? 'rise' : 'fall'">{{ formatAmount(row.smallNet) }}</span>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 股票列表 -->
      <el-table :data="stockList" v-loading="stockLoading" class="stock-table">
        <el-table-column prop="stockCode" label="股票代码" width="110" />
        <el-table-column prop="stockName" label="股票名称" width="110" />
        <el-table-column prop="addReason" label="加入理由" min-width="180" show-overflow-tooltip />
        <el-table-column prop="remark" label="备注" min-width="140" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="createTime" label="加入时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link size="small" @click="openEditDialog(row)">编辑</el-button>
            <el-popconfirm title="确认从板块移除该股票？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button link size="small" type="danger">移除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </template>

    <div v-else class="no-selection">
      <el-icon size="60" color="#ddd"><Folder /></el-icon>
      <p>请在左侧选择一个板块</p>
    </div>

    <!-- 新增/编辑股票弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'add' ? '添加股票' : '编辑股票'"
      width="500px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="股票代码" prop="stockCode">
          <el-input v-model="form.stockCode" placeholder="请输入股票代码" :disabled="dialogMode === 'edit'" />
        </el-form-item>
        <el-form-item label="股票名称" prop="stockName">
          <el-input v-model="form.stockName" placeholder="请输入股票名称" />
        </el-form-item>
        <el-form-item label="加入理由">
          <el-input v-model="form.addReason" type="textarea" :rows="3" placeholder="记录加入该股票的理由（选填）" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="其他备注（选填）" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </main>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Folder, TrendCharts } from '@element-plus/icons-vue'
import { getStockList, addStock, updateStock, deleteStock, getMoneyFlow } from '../api/watchlist'

const props = defineProps({
  selectedBk: { type: Object, default: null },
})

// 股票列表
const stockList = ref([])
const stockLoading = ref(false)

const loadStockList = async (bkId) => {
  stockLoading.value = true
  try {
    const res = await getStockList(bkId)
    stockList.value = res.data
  } finally {
    stockLoading.value = false
  }
}

// 监听选中板块变化，自动加载股票列表
watch(() => props.selectedBk, (bk) => {
  if (bk) {
    showMoneyFlow.value = false
    moneyFlowList.value = []
    loadStockList(bk.id)
  } else {
    stockList.value = []
  }
})

// 删除股票
const handleDelete = async (id) => {
  await deleteStock(id)
  ElMessage.success('移除成功')
  await loadStockList(props.selectedBk.id)
}

// 弹窗
const dialogVisible = ref(false)
const dialogMode = ref('add')
const submitting = ref(false)
const formRef = ref()
const form = ref({ stockCode: '', stockName: '', addReason: '', remark: '', sort: 0 })
const rules = {
  stockCode: [{ required: true, message: '请输入股票代码', trigger: 'blur' }],
}

const openAddDialog = () => {
  dialogMode.value = 'add'
  dialogVisible.value = true
}

const openEditDialog = (row) => {
  dialogMode.value = 'edit'
  form.value = { ...row }
  dialogVisible.value = true
}

const resetForm = () => {
  form.value = { stockCode: '', stockName: '', addReason: '', remark: '', sort: 0 }
  formRef.value?.resetFields()
}

const submitForm = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (dialogMode.value === 'add') {
      const addFlag = await addStock({ ...form.value, bkId: props.selectedBk.id })
      console.log(addFlag.data);
      if (addFlag.data) {
        ElMessage.success('添加成功')
      } else {
        ElMessage.error('添加失败')
      }
    } else {
      await updateStock(form.value)
      ElMessage.success('修改成功')
    }
    dialogVisible.value = false
    await loadStockList(props.selectedBk.id)
  } finally {
    submitting.value = false
  }
}

// 资金流向
const showMoneyFlow = ref(false)
const moneyFlowList = ref([])
const moneyFlowLoading = ref(false)

const loadMoneyFlow = async () => {
  moneyFlowLoading.value = true
  try {
    const res = await getMoneyFlow(props.selectedBk.id)
    moneyFlowList.value = res.data
    showMoneyFlow.value = moneyFlowLoading.value
  } finally {
    moneyFlowLoading.value = false
  }
}

const formatAmount = (val) => {
  if (val === null || val === undefined) return '-'
  const abs = Math.abs(val)
  const sign = val >= 0 ? '+' : '-'
  if (abs >= 1e8) return `${sign}${(abs / 1e8).toFixed(2)}亿`
  if (abs >= 1e4) return `${sign}${(abs / 1e4).toFixed(2)}万`
  return `${sign}${abs}`
}
</script>

<style scoped>
.stock-panel {
  flex: 1;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.panel-title {
  font-size: 15px;
  font-weight: 600;
  color: #1a1a2e;
}

.panel-title-group {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.panel-subtitle {
  font-size: 12px;
  color: #999;
}

.panel-header-actions {
  display: flex;
  gap: 8px;
}

.money-flow-section {
  padding: 12px 16px;
  background: #fafbfc;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;
}

.section-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
  font-weight: 500;
}

.stock-table {
  flex: 1;
}

.rise {
  color: #f56c6c;
  font-weight: 500;
}

.fall {
  color: #67c23a;
  font-weight: 500;
}

.no-selection {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #ccc;
  gap: 12px;
  font-size: 14px;
}
</style>
