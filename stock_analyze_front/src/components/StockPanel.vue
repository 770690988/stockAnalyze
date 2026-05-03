<template>
  <main class="stock-panel">
    <template v-if="selectedBk">
      <div class="panel-header">
        <div class="panel-title-group">
          <span class="panel-title">{{ selectedBk.bkName }}</span>
          <span class="panel-subtitle">共 {{ stockList.length }} 只股票</span>
        </div>
        <div class="panel-header-actions">
          <el-button size="small" @click="toggleMoneyFlow" :loading="moneyFlowLoading">
            <el-icon><TrendCharts /></el-icon> {{ showMoneyFlow ? '收起资金流向' : '查看资金流向' }}
          </el-button>
          <el-button type="primary" size="small" @click="openAddDialog">
            <el-icon><Plus /></el-icon> 添加股票
          </el-button>
        </div>
      </div>

      <!-- 资金流向面板 -->
      <div v-if="showMoneyFlow" class="money-flow-section">
        <div class="flow-toolbar">
          <span class="section-label">资金流向</span>
          <el-radio-group v-model="periodDay" size="small" @change="loadMoneyFlowHistory">
            <el-radio-button :value="1">当日</el-radio-button>
            <el-radio-button :value="5">5日</el-radio-button>
            <el-radio-button :value="10">10日</el-radio-button>
            <el-radio-button :value="30">30日</el-radio-button>
          </el-radio-group>
        </div>

        <div v-if="historyLoading" class="chart-loading">加载中...</div>

        <!-- 统一表格：当日和多日都用这个 -->
        <table v-else class="flow-table">
          <thead>
            <tr>
              <th width="90">代码</th>
              <th width="100">名称</th>
              <th width="80">现价</th>
              <th width="90">涨跌幅</th>
              <th width="120">主力净流入</th>
              <th width="120">超大单</th>
              <th width="110">大单</th>
              <th width="110">中单</th>
              <th width="110">小单</th>
              <th v-if="periodDay > 1">趋势</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in moneyFlowList" :key="row.stockCode">
              <td>{{ row.stockCode }}</td>
              <td>{{ row.stockName }}</td>
              <td>{{ row.stockPrice }}</td>
              <td><span :class="row.stockPriceRate >= 0 ? 'rise' : 'fall'">{{ row.stockPriceRate >= 0 ? '+' : '' }}{{ row.stockPriceRate }}%</span></td>
              <td><span :class="row.mainNet >= 0 ? 'rise' : 'fall'">{{ formatAmount(row.mainNet) }}</span></td>
              <td><span :class="row.superNet >= 0 ? 'rise' : 'fall'">{{ formatAmount(row.superNet) }}</span></td>
              <td><span :class="row.largeNet >= 0 ? 'rise' : 'fall'">{{ formatAmount(row.largeNet) }}</span></td>
              <td><span :class="row.middleNet >= 0 ? 'rise' : 'fall'">{{ formatAmount(row.middleNet) }}</span></td>
              <td><span :class="row.smallNet >= 0 ? 'rise' : 'fall'">{{ formatAmount(row.smallNet) }}</span></td>
              <td v-if="periodDay > 1" class="chart-cell">
                <div
                  :ref="el => { if(el) chartRefs[row.stockCode] = el }"
                  class="mini-chart"
                ></div>
              </td>
            </tr>
            <!-- 合计行 -->
            <tr class="total-row">
            <td colspan="4">合计</td>
            <td><span :class="totalMainNet >= 0 ? 'rise' : 'fall'">{{ formatAmount(totalMainNet) }}</span></td>
            <td><span :class="totalSuperNet >= 0 ? 'rise' : 'fall'">{{ formatAmount(totalSuperNet) }}</span></td>
            <td><span :class="totalLargeNet >= 0 ? 'rise' : 'fall'">{{ formatAmount(totalLargeNet) }}</span></td>
            <td><span :class="totalMiddleNet >= 0 ? 'rise' : 'fall'">{{ formatAmount(totalMiddleNet) }}</span></td>
            <td><span :class="totalSmallNet >= 0 ? 'rise' : 'fall'">{{ formatAmount(totalSmallNet) }}</span></td>
            <td v-if="periodDay > 1" class="chart-cell">
              <div ref="totalChartRef" class="mini-chart"></div>
            </td>
            </tr>
          </tbody>
        </table>
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
import { ref, watch, nextTick, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Folder, TrendCharts } from '@element-plus/icons-vue'
import * as echarts from 'echarts'
import { getStockList, addStock, updateStock, deleteStock, getMoneyFlow, getMoneyFlowHistory } from '../api/watchlist'

const props = defineProps({
  selectedBk: { type: Object, default: null },
})

// ===================== 股票列表 =====================
const stockList = ref([])
const stockLoading = ref(false)

const loadStockList = async (bkId) => {
  stockLoading.value = true
  try {
    stockList.value = await getStockList(bkId)
  } finally {
    stockLoading.value = false
  }
}

watch(() => props.selectedBk, (bk) => {
  if (bk) {
    showMoneyFlow.value = false
    moneyFlowList.value = []
    loadStockList(bk.id)
  } else {
    stockList.value = []
  }
})

const handleDelete = async (id) => {
  await deleteStock(id)
  ElMessage.success('移除成功')
  await loadStockList(props.selectedBk.id)
}

// ===================== 弹窗 =====================
const dialogVisible = ref(false)
const dialogMode = ref('add')
const submitting = ref(false)
const formRef = ref()
const form = ref({ stockCode: '', stockName: '', addReason: '', remark: '', sort: 0 })
const rules = {
  stockCode: [{ required: true, message: '请输入股票代码', trigger: 'blur' }],
}

const openAddDialog = () => { dialogMode.value = 'add'; dialogVisible.value = true }
const openEditDialog = (row) => { dialogMode.value = 'edit'; form.value = { ...row }; dialogVisible.value = true }
const resetForm = () => {
  form.value = { stockCode: '', stockName: '', addReason: '', remark: '', sort: 0 }
  formRef.value?.resetFields()
}

const submitForm = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (dialogMode.value === 'add') {
      const ok = await addStock({ ...form.value, bkId: props.selectedBk.id })
      ElMessage[ok ? 'success' : 'error'](ok ? '添加成功' : '添加失败')
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

// ===================== 资金流向 =====================
const showMoneyFlow = ref(false)
const moneyFlowLoading = ref(false)
const moneyFlowList = ref([])  // 当日或多日最新一天的数据，用于表格展示
const historyData = ref({})    // 多日历史数据，用于画图
const periodDay = ref(1)
const chartRefs = ref({})
const chartInstances = {}

// 合计
const totalMainNet = computed(() => moneyFlowList.value.reduce((s, r) => s + (r.mainNet || 0), 0))
const totalSuperNet = computed(() => moneyFlowList.value.reduce((s, r) => s + (r.superNet || 0), 0))
const totalLargeNet = computed(() => moneyFlowList.value.reduce((s, r) => s + (r.largeNet || 0), 0))
const totalMiddleNet = computed(() => moneyFlowList.value.reduce((s, r) => s + (r.middleNet || 0), 0))
const totalSmallNet = computed(() => moneyFlowList.value.reduce((s, r) => s + (r.smallNet || 0), 0))

const toggleMoneyFlow = async () => {
  showMoneyFlow.value = !showMoneyFlow.value
  if (showMoneyFlow.value) await loadMoneyFlowHistory()
}

const loadMoneyFlowHistory = async () => {
  // 销毁旧图表
  Object.values(chartInstances).forEach(c => c.dispose())
  Object.keys(chartInstances).forEach(k => delete chartInstances[k])
  chartRefs.value = {}

  if (periodDay.value === 1) {
    moneyFlowLoading.value = true
    try {
      moneyFlowList.value = await getMoneyFlow(props.selectedBk.id)
      historyData.value = {}
    } finally {
      moneyFlowLoading.value = false
    }
  } else {
    moneyFlowLoading.value = true
    try {
      const res = await getMoneyFlowHistory(props.selectedBk.id, periodDay.value)

      // 按股票分组
      const grouped = {}
      res.forEach(item => {
        if (!grouped[item.stockCode]) grouped[item.stockCode] = []
        grouped[item.stockCode].push(item)
      })

      // 按 stockList 顺序排列，取每只股票最新一天显示在表格
      const sortedList = []
      const sortedHistory = {}
      stockList.value.forEach(s => {
        const records = grouped[s.stockCode]
        if (records && records.length > 0) {
          sortedList.push(records[records.length - 1])  // 最新一天放表格
          sortedHistory[s.stockCode] = records
        }
      })

      moneyFlowList.value = sortedList
      historyData.value = sortedHistory

      await nextTick()
      setTimeout(() => renderMiniCharts(), 100)
    } finally {
      moneyFlowLoading.value = false
    }
  }
}
const totalChartRef = ref(null)

const renderMiniCharts = () => {
  // 各股票迷你图
  Object.entries(historyData.value).forEach(([code, records]) => {
    const el = chartRefs.value[code]
    if (!el) return

    const chart = echarts.init(el)
    chartInstances[code] = chart

    const dates = records.map(r => r.tradeDate.substring(0, 10))
    const mainNet = records.map(r => +(r.mainNet / 10000).toFixed(2))
    const superNet = records.map(r => +(r.superNet / 10000).toFixed(2))
    const largeNet = records.map(r => +(r.largeNet / 10000).toFixed(2))
    const middleNet = records.map(r => +(r.middleNet / 10000).toFixed(2))
    const smallNet = records.map(r => +(r.smallNet / 10000).toFixed(2))

    chart.setOption({
      tooltip: {
        trigger: 'axis', confine: true,
        formatter: (params) => {
          let html = `${params[0].axisValue}<br/>`
          params.forEach(p => {
            const color = p.value >= 0 ? '#f56c6c' : '#67c23a'
            html += `${p.marker}${p.seriesName}：<span style="color:${color}">${p.value >= 0 ? '+' : ''}${p.value}万</span><br/>`
          })
          return html
        }
      },
      legend: { show: false },
      grid: { left: 2, right: 2, top: 4, bottom: 16 },
      xAxis: {
        type: 'category', data: dates,
        axisLabel: { fontSize: 9, rotate: 30 },
        axisLine: { show: false }, axisTick: { show: false }
      },
      yAxis: { type: 'value', show: false },
      series: [
        { name: '主力', type: 'bar', data: mainNet, itemStyle: { color: p => p.value >= 0 ? '#f56c6c' : '#67c23a' }, barMaxWidth: 8 },
        { name: '超大单', type: 'line', data: superNet, smooth: true, lineStyle: { width: 1 }, symbol: 'none', color: '#e6a23c' },
        { name: '大单', type: 'line', data: largeNet, smooth: true, lineStyle: { width: 1 }, symbol: 'none', color: '#409eff' },
        { name: '中单', type: 'line', data: middleNet, smooth: true, lineStyle: { width: 1 }, symbol: 'none', color: '#9c27b0' },
        { name: '小单', type: 'line', data: smallNet, smooth: true, lineStyle: { width: 1 }, symbol: 'none', color: '#909399' },
      ]
    })
  })

  // 合计图
  if (totalChartRef.value && Object.keys(historyData.value).length > 0) {
    const allDates = [...new Set(
      Object.values(historyData.value).flatMap(records =>
        records.map(r => r.tradeDate.substring(0, 10))
      )
    )].sort()

    const sumByDate = {}
    allDates.forEach(date => {
      sumByDate[date] = { mainNet: 0, superNet: 0, largeNet: 0, middleNet: 0, smallNet: 0 }
    })
    Object.values(historyData.value).forEach(records => {
      records.forEach(r => {
        const date = r.tradeDate.substring(0, 10)
        if (sumByDate[date]) {
          sumByDate[date].mainNet += r.mainNet || 0
          sumByDate[date].superNet += r.superNet || 0
          sumByDate[date].largeNet += r.largeNet || 0
          sumByDate[date].middleNet += r.middleNet || 0
          sumByDate[date].smallNet += r.smallNet || 0
        }
      })
    })

    const totalChart = echarts.init(totalChartRef.value)
    chartInstances['__total__'] = totalChart

    totalChart.setOption({
      tooltip: {
        trigger: 'axis', confine: true,
        formatter: (params) => {
          let html = `${params[0].axisValue}<br/>`
          params.forEach(p => {
            const color = p.value >= 0 ? '#f56c6c' : '#67c23a'
            html += `${p.marker}${p.seriesName}：<span style="color:${color}">${p.value >= 0 ? '+' : ''}${p.value}万</span><br/>`
          })
          return html
        }
      },
      legend: { show: false },
      grid: { left: 2, right: 2, top: 4, bottom: 16 },
      xAxis: {
        type: 'category', data: allDates,
        axisLabel: { fontSize: 9, rotate: 30 },
        axisLine: { show: false }, axisTick: { show: false }
      },
      yAxis: { type: 'value', show: false },
      series: [
        { name: '主力', type: 'bar', data: allDates.map(d => +(sumByDate[d].mainNet / 10000).toFixed(2)), itemStyle: { color: p => p.value >= 0 ? '#f56c6c' : '#67c23a' }, barMaxWidth: 8 },
        { name: '超大单', type: 'line', data: allDates.map(d => +(sumByDate[d].superNet / 10000).toFixed(2)), smooth: true, lineStyle: { width: 1 }, symbol: 'none', color: '#e6a23c' },
        { name: '大单', type: 'line', data: allDates.map(d => +(sumByDate[d].largeNet / 10000).toFixed(2)), smooth: true, lineStyle: { width: 1 }, symbol: 'none', color: '#409eff' },
        { name: '中单', type: 'line', data: allDates.map(d => +(sumByDate[d].middleNet / 10000).toFixed(2)), smooth: true, lineStyle: { width: 1 }, symbol: 'none', color: '#9c27b0' },
        { name: '小单', type: 'line', data: allDates.map(d => +(sumByDate[d].smallNet / 10000).toFixed(2)), smooth: true, lineStyle: { width: 1 }, symbol: 'none', color: '#909399' },
      ]
    })
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

.panel-title { font-size: 15px; font-weight: 600; color: #1a1a2e; }
.panel-title-group { display: flex; align-items: baseline; gap: 8px; }
.panel-subtitle { font-size: 12px; color: #999; }
.panel-header-actions { display: flex; gap: 8px; }

.money-flow-section {
  padding: 12px 16px;
  background: #fafbfc;
  border-bottom: 1px solid #f0f0f0;
  flex-shrink: 0;
  max-height: 500px;
  overflow-y: auto;
}

.flow-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.section-label { font-size: 12px; color: #999; font-weight: 500; }

/* 自定义表格 */
.flow-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}

.flow-table th {
  background: #f5f7fa;
  color: #606266;
  font-weight: 500;
  padding: 8px 10px;
  text-align: left;
  border-bottom: 1px solid #ebeef5;
  white-space: nowrap;
}

.flow-table td {
  padding: 8px 10px;
  border-bottom: 1px solid #f0f0f0;
  white-space: nowrap;
  vertical-align: middle;
}

.flow-table tr:hover td { background: #f5f7fa; }

.total-row td {
  font-weight: 600;
  background: #f0f4ff;
  border-top: 2px solid #d0d7f0;
}

.chart-cell {
  min-width: 200px;
  padding: 4px 8px !important;
}

.mini-chart {
  width: 100%;
  height: 80px;
}

.chart-loading {
  text-align: center;
  color: #999;
  padding: 20px 0;
  font-size: 13px;
}

.stock-table { flex: 1; }
.rise { color: #f56c6c; font-weight: 500; }
.fall { color: #67c23a; font-weight: 500; }

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