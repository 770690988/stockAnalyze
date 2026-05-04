<template>
  <main class="stock-panel">
    <template v-if="selectedBk">
      <div class="panel-header">
        <div class="panel-title-group">
          <span class="panel-title">{{ selectedBk.bkName }}</span>
          <span class="panel-subtitle">共 {{ stockList.length }} 只股票</span>
        </div>
        <div class="panel-header-actions">
          <el-button
            size="small"
            @click="toggleMoneyFlow"
            :loading="moneyFlowLoading"
          >
            <el-icon><TrendCharts /></el-icon>
            {{ showMoneyFlow ? "收起资金流向" : "查看资金流向" }}
          </el-button>
          <el-button size="small" @click="showStockList = !showStockList">
            <el-icon><List /></el-icon>
            {{ showStockList ? "收起列表" : "展开列表" }}
          </el-button>
          <el-button type="primary" size="small" @click="openAddDialog">
            <el-icon><Plus /></el-icon> 添加股票
          </el-button>
          <el-button size="small" @click="openBatchImport" :disabled="!selectedBk">
            <el-icon><Upload /></el-icon> 批量导入
          </el-button>
          <el-popconfirm
            v-if="selectedRows.length > 0"
            :title="`确认移除选中的 ${selectedRows.length} 只股票？`"
            @confirm="handleBatchDelete"
          >
            <template #reference>
              <el-button size="small" type="danger">
                <el-icon><Delete /></el-icon> 批量移除({{ selectedRows.length }})
              </el-button>
            </template>
          </el-popconfirm>
        </div>
      </div>

      <!-- 资金流向面板 -->
      <div v-if="showMoneyFlow" class="money-flow-section">
        <div class="flow-toolbar">
          <span class="section-label">资金流向</span>
          <el-radio-group
            v-model="periodDay"
            size="small"
            @change="loadMoneyFlowHistory"
          >
            <el-radio-button :value="1">当日</el-radio-button>
            <el-radio-button :value="5">5日</el-radio-button>
            <el-radio-button :value="10">10日</el-radio-button>
            <el-radio-button :value="30">30日</el-radio-button>
          </el-radio-group>
        </div>

        <div v-if="historyLoading" class="chart-loading">加载中...</div>

        <table v-else class="flow-table">
          <thead>
            <tr>
              <th width="90">代码</th>
              <th width="100">名称</th>
              <th width="150">加入理由</th>
              <th width="80">现价</th>
              <th width="90">涨跌幅</th>
              <th width="120">主力净流入</th>
              <th width="120">超大单</th>
              <th width="110">大单</th>
              <th width="110">中单</th>
              <th width="110">小单</th>
              <th v-if="periodDay > 1" width="160">换手率</th>
              <th width="220">量能</th>
              <th v-if="periodDay > 1" width="220">累计量能</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in moneyFlowList" :key="row.stockCode">
              <td>{{ row.stockCode }}</td>
              <td>{{ row.stockName }}</td>
              <td
                style="max-width: 150px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;"
                :title="getAddReason(row.stockCode)"
              >
                {{ getAddReason(row.stockCode) }}
              </td>
              <td>{{ row.stockPrice }}</td>
              <td>
                <span :class="row.stockPriceRate >= 0 ? 'rise' : 'fall'"
                  >{{ row.stockPriceRate >= 0 ? "+" : "" }}{{ row.stockPriceRate }}%</span>
              </td>
              <td><span :class="row.mainNet >= 0 ? 'rise' : 'fall'">{{ formatAmount(row.mainNet) }}</span></td>
              <td><span :class="row.superNet >= 0 ? 'rise' : 'fall'">{{ formatAmount(row.superNet) }}</span></td>
              <td><span :class="row.largeNet >= 0 ? 'rise' : 'fall'">{{ formatAmount(row.largeNet) }}</span></td>
              <td><span :class="row.middleNet >= 0 ? 'rise' : 'fall'">{{ formatAmount(row.middleNet) }}</span></td>
              <td><span :class="row.smallNet >= 0 ? 'rise' : 'fall'">{{ formatAmount(row.smallNet) }}</span></td>
              <td v-if="periodDay > 1" class="chart-cell chart-cell-sm">
                <div
                  :ref="(el) => { if (el) turnoverRateChartRefs[row.stockCode] = el; }"
                  class="mini-chart chart-clickable"
                  @click="openTurnoverRateZoomChart(row.stockCode)"
                ></div>
              </td>
              <td class="chart-cell">
                <div
                  :ref="(el) => { if (el) chartRefs[row.stockCode] = el; }"
                  class="mini-chart chart-clickable"
                  @click="openZoomChart(row.stockCode, false)"
                ></div>
              </td>
              <td v-if="periodDay > 1" class="chart-cell">
                <div
                  :ref="(el) => { if (el) cumChartRefs[row.stockCode] = el; }"
                  class="mini-chart chart-clickable"
                  @click="openZoomChart(row.stockCode, true)"
                ></div>
              </td>
            </tr>
            <!-- 合计行 -->
            <tr class="total-row">
              <td colspan="5">合计</td>
              <td><span :class="totalMainNet >= 0 ? 'rise' : 'fall'">{{ formatAmount(totalMainNet) }}</span></td>
              <td><span :class="totalSuperNet >= 0 ? 'rise' : 'fall'">{{ formatAmount(totalSuperNet) }}</span></td>
              <td><span :class="totalLargeNet >= 0 ? 'rise' : 'fall'">{{ formatAmount(totalLargeNet) }}</span></td>
              <td><span :class="totalMiddleNet >= 0 ? 'rise' : 'fall'">{{ formatAmount(totalMiddleNet) }}</span></td>
              <td><span :class="totalSmallNet >= 0 ? 'rise' : 'fall'">{{ formatAmount(totalSmallNet) }}</span></td>
              <td v-if="periodDay > 1" class="chart-cell chart-cell-sm">
                <div ref="totalTurnoverRateChartRef" class="mini-chart chart-clickable" @click="openTurnoverRateZoomChart('__total__')"></div>
              </td>
              <td class="chart-cell">
                <div ref="totalChartRef" class="mini-chart chart-clickable" @click="openZoomChart('__total__', false)"></div>
              </td>
              <td v-if="periodDay > 1" class="chart-cell">
                <div ref="totalCumChartRef" class="mini-chart chart-clickable" @click="openZoomChart('__total__', true)"></div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 股票列表 -->
      <el-table
        :data="stockList"
        v-loading="stockLoading"
        v-show="showStockList"
        class="stock-table"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="45" />
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

      <!-- 批量导入弹窗 -->
      <BatchImportDialog
        ref="batchImportRef"
        :bkId="selectedBk.id"
        @success="loadStockList(selectedBk.id)"
      />
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

    <!-- 图表放大弹窗 -->
    <el-dialog
      v-model="chartDialogVisible"
      :title="chartDialogTitle"
      width="860px"
      class="chart-zoom-dialog"
      @opened="initZoomChart"
      @closed="disposeZoomChart"
    >
      <div ref="zoomChartRef" style="width: 100%; height: 420px"></div>
    </el-dialog>
  </main>
</template>

<script setup>
import { ref, watch, nextTick, computed } from "vue";
import { ElMessage } from "element-plus";
import { Plus, Folder, TrendCharts, List, Upload, Delete } from '@element-plus/icons-vue'
import * as echarts from "echarts";
import {
  getStockList,
  addStock,
  updateStock,
  deleteStock,
  deleteBatchStock,
  getMoneyFlowHistory,
} from "../api/watchlist";
import BatchImportDialog from './BatchImportDialog.vue';

const props = defineProps({
  selectedBk: { type: Object, default: null },
});

const batchImportRef = ref(null);
const openBatchImport = () => {
  batchImportRef.value?.open();
};

// ===================== 股票列表 =====================
const stockList = ref([]);
const stockLoading = ref(false);
const selectedRows = ref([]);

const handleSelectionChange = (rows) => {
  selectedRows.value = rows;
};

const handleBatchDelete = async () => {
  const ids = selectedRows.value.map((r) => r.id);
  const count = await deleteBatchStock(ids);
  ElMessage.success(`已移除 ${count} 只股票`);
  selectedRows.value = [];
  await loadStockList(props.selectedBk.id);
};

const loadStockList = async (bkId) => {
  stockLoading.value = true;
  try {
    stockList.value = await getStockList(bkId);
  } finally {
    stockLoading.value = false;
  }
};

watch(
  () => props.selectedBk,
  (bk) => {
    if (bk) {
      showMoneyFlow.value = false;
      showStockList.value = true;
      periodDay.value = 5;
      moneyFlowList.value = [];
      selectedRows.value = [];
      loadStockList(bk.id);
    } else {
      stockList.value = [];
    }
  }
);

const handleDelete = async (id) => {
  await deleteStock(id);
  ElMessage.success("移除成功");
  await loadStockList(props.selectedBk.id);
};

// ===================== 弹窗 =====================
const dialogVisible = ref(false);
const dialogMode = ref("add");
const submitting = ref(false);
const formRef = ref();
const form = ref({ stockCode: "", stockName: "", addReason: "", remark: "", sort: 0 });
const rules = {
  stockCode: [{ required: true, message: "请输入股票代码", trigger: "blur" }],
};

const openAddDialog = () => { dialogMode.value = "add"; dialogVisible.value = true; };
const openEditDialog = (row) => { dialogMode.value = "edit"; form.value = { ...row }; dialogVisible.value = true; };
const resetForm = () => {
  form.value = { stockCode: "", stockName: "", addReason: "", remark: "", sort: 0 };
  formRef.value?.resetFields();
};

const submitForm = async () => {
  await formRef.value.validate();
  submitting.value = true;
  try {
    if (dialogMode.value === "add") {
      const ok = await addStock({ ...form.value, bkId: props.selectedBk.id });
      ElMessage[ok ? "success" : "error"](ok ? "添加成功" : "添加失败");
    } else {
      await updateStock(form.value);
      ElMessage.success("修改成功");
    }
    dialogVisible.value = false;
    await loadStockList(props.selectedBk.id);
  } finally {
    submitting.value = false;
  }
};

// ===================== 资金流向 =====================
const showMoneyFlow = ref(false);
const showStockList = ref(true);
const moneyFlowLoading = ref(false);
const moneyFlowList = ref([]);
const historyData = ref({});
const periodDay = ref(5);

const chartRefs = ref({});
const cumChartRefs = ref({});
const turnoverRateChartRefs = ref({});
const chartInstances = {};
const cumChartInstances = {};
const turnoverRateChartInstances = {};

const totalMainNet = computed(() => moneyFlowList.value.reduce((s, r) => s + (r.mainNet || 0), 0));
const totalSuperNet = computed(() => moneyFlowList.value.reduce((s, r) => s + (r.superNet || 0), 0));
const totalLargeNet = computed(() => moneyFlowList.value.reduce((s, r) => s + (r.largeNet || 0), 0));
const totalMiddleNet = computed(() => moneyFlowList.value.reduce((s, r) => s + (r.middleNet || 0), 0));
const totalSmallNet = computed(() => moneyFlowList.value.reduce((s, r) => s + (r.smallNet || 0), 0));

const toggleMoneyFlow = async () => {
  showMoneyFlow.value = !showMoneyFlow.value;
  if (showMoneyFlow.value) {
    showStockList.value = false;
    await loadMoneyFlowHistory();
  } else {
    showStockList.value = true;
  }
};

const loadMoneyFlowHistory = async () => {
  Object.values(chartInstances).forEach((c) => c.dispose());
  Object.keys(chartInstances).forEach((k) => delete chartInstances[k]);
  Object.values(cumChartInstances).forEach((c) => c.dispose());
  Object.keys(cumChartInstances).forEach((k) => delete cumChartInstances[k]);
  Object.values(turnoverRateChartInstances).forEach((c) => c.dispose());
  Object.keys(turnoverRateChartInstances).forEach((k) => delete turnoverRateChartInstances[k]);
  chartRefs.value = {};
  cumChartRefs.value = {};
  turnoverRateChartRefs.value = {};

  moneyFlowLoading.value = true;
  try {
    const res = await getMoneyFlowHistory(props.selectedBk.id, periodDay.value);

    const grouped = {};
    res.forEach((item) => {
      if (!grouped[item.stockCode]) grouped[item.stockCode] = [];
      grouped[item.stockCode].push(item);
    });

    const sortedList = [];
    const sortedHistory = {};
    stockList.value.forEach((s) => {
      const records = grouped[s.stockCode];
      if (records && records.length > 0) {
        if (periodDay.value === 1) {
          sortedList.push(records[records.length - 1]);
        } else {
          const first = records[0];
          const latest = records[records.length - 1];
          const summed = {
            ...latest,
            mainNet: records.reduce((s, r) => s + (r.mainNet || 0), 0),
            superNet: records.reduce((s, r) => s + (r.superNet || 0), 0),
            largeNet: records.reduce((s, r) => s + (r.largeNet || 0), 0),
            middleNet: records.reduce((s, r) => s + (r.middleNet || 0), 0),
            smallNet: records.reduce((s, r) => s + (r.smallNet || 0), 0),
            stockPriceRate: first.stockPrice && latest.stockPrice && first.stockPrice !== 0
              ? +((latest.stockPrice - first.stockPrice) / first.stockPrice * 100).toFixed(2)
              : latest.stockPriceRate,
          };
          sortedList.push(summed);
        }
        sortedHistory[s.stockCode] = records;
      }
    });

    moneyFlowList.value = sortedList;
    historyData.value = sortedHistory;

    await nextTick();
    setTimeout(() => renderMiniCharts(), 100);
  } finally {
    moneyFlowLoading.value = false;
  }
};

const totalChartRef = ref(null);
const totalCumChartRef = ref(null);
const totalTurnoverRateChartRef = ref(null);

const cumulate = (arr) => arr.reduce((acc, val, i) => { acc.push(+((acc[i - 1] || 0) + val).toFixed(2)); return acc; }, []);

const formatDateLabel = (tradeDate) => {
  if (periodDay.value === 1) return tradeDate.length > 10 ? tradeDate.substring(11, 16) : tradeDate;
  return tradeDate.substring(0, 10);
};

const TIME_SLOTS = ["09:30","09:45","10:00","10:15","10:30","10:45","11:00","11:15","11:30","14:00","14:15","14:30","14:45","15:00"];
const toMinutes = (hhmm) => { const [h, m] = hhmm.split(":").map(Number); return h * 60 + m; };
const findSlot = (timeStr) => {
  const t = toMinutes(timeStr);
  for (let i = 0; i < TIME_SLOTS.length; i++) {
    const slotStart = toMinutes(TIME_SLOTS[i]);
    const slotEnd = i < TIME_SLOTS.length - 1 ? toMinutes(TIME_SLOTS[i + 1]) : slotStart + 1;
    if (t >= slotStart && t < slotEnd) return TIME_SLOTS[i];
  }
  return null;
};

const buildTotalDataIntraday = () => {
  const sumBySlot = {};
  TIME_SLOTS.forEach((slot) => { sumBySlot[slot] = { mainNet: 0, superNet: 0, largeNet: 0, middleNet: 0, smallNet: 0, hasData: false }; });
  Object.values(historyData.value).forEach((records) => {
    records.forEach((r) => {
      const timeLabel = r.tradeDate.length > 10 ? r.tradeDate.substring(11, 16) : r.tradeDate;
      const slot = findSlot(timeLabel);
      if (slot) {
        sumBySlot[slot].mainNet += r.mainNet || 0;
        sumBySlot[slot].superNet += r.superNet || 0;
        sumBySlot[slot].largeNet += r.largeNet || 0;
        sumBySlot[slot].middleNet += r.middleNet || 0;
        sumBySlot[slot].smallNet += r.smallNet || 0;
        sumBySlot[slot].hasData = true;
      }
    });
  });
  const usedSlots = TIME_SLOTS.filter((s) => sumBySlot[s].hasData);
  return {
    allLabels: usedSlots,
    mainNet: usedSlots.map((s) => +(sumBySlot[s].mainNet / 10000).toFixed(2)),
    superNet: usedSlots.map((s) => +(sumBySlot[s].superNet / 10000).toFixed(2)),
    largeNet: usedSlots.map((s) => +(sumBySlot[s].largeNet / 10000).toFixed(2)),
    middleNet: usedSlots.map((s) => +(sumBySlot[s].middleNet / 10000).toFixed(2)),
    smallNet: usedSlots.map((s) => +(sumBySlot[s].smallNet / 10000).toFixed(2)),
  };
};

const buildTotalDataMultiDay = () => {
  const allLabels = [...new Set(Object.values(historyData.value).flatMap((records) => records.map((r) => r.tradeDate.substring(0, 10))))].sort();
  const sumByLabel = {};
  allLabels.forEach((label) => { sumByLabel[label] = { mainNet: 0, superNet: 0, largeNet: 0, middleNet: 0, smallNet: 0 }; });
  Object.values(historyData.value).forEach((records) => {
    records.forEach((r) => {
      const label = r.tradeDate.substring(0, 10);
      if (sumByLabel[label]) {
        sumByLabel[label].mainNet += r.mainNet || 0;
        sumByLabel[label].superNet += r.superNet || 0;
        sumByLabel[label].largeNet += r.largeNet || 0;
        sumByLabel[label].middleNet += r.middleNet || 0;
        sumByLabel[label].smallNet += r.smallNet || 0;
      }
    });
  });
  return {
    allLabels,
    mainNet: allLabels.map((d) => +(sumByLabel[d].mainNet / 10000).toFixed(2)),
    superNet: allLabels.map((d) => +(sumByLabel[d].superNet / 10000).toFixed(2)),
    largeNet: allLabels.map((d) => +(sumByLabel[d].largeNet / 10000).toFixed(2)),
    middleNet: allLabels.map((d) => +(sumByLabel[d].middleNet / 10000).toFixed(2)),
    smallNet: allLabels.map((d) => +(sumByLabel[d].smallNet / 10000).toFixed(2)),
  };
};

const buildTotalData = () => periodDay.value === 1 ? buildTotalDataIntraday() : buildTotalDataMultiDay();

const buildTotalTurnoverRateData = () => {
  const allLabels = [...new Set(Object.values(historyData.value).flatMap((records) => records.map((r) => r.tradeDate.substring(0, 10))))].sort();
  const byLabel = {};
  allLabels.forEach((label) => { byLabel[label] = { sum: 0, count: 0 }; });
  Object.values(historyData.value).forEach((records) => {
    records.forEach((r) => {
      const label = r.tradeDate.substring(0, 10);
      if (byLabel[label] && r.turnoverRate != null) { byLabel[label].sum += r.turnoverRate; byLabel[label].count += 1; }
    });
  });
  return {
    allLabels,
    turnoverRate: allLabels.map((d) => byLabel[d].count > 0 ? +(byLabel[d].sum / byLabel[d].count).toFixed(2) : null),
  };
};

const buildTurnoverRateOption = (labels, turnoverRate) => ({
  tooltip: { trigger: "axis", confine: true, formatter: (params) => { const p = params[0]; if (p.value == null) return `${p.axisValue}<br/>换手率：-`; return `${p.axisValue}<br/>${p.marker}换手率：<span style="color:#409eff">${p.value}%</span>`; } },
  legend: { show: false },
  grid: { left: 2, right: 2, top: 4, bottom: 16 },
  xAxis: { type: "category", data: labels, axisLabel: { fontSize: 9, rotate: 30 }, axisLine: { show: false }, axisTick: { show: false } },
  yAxis: { type: "value", show: false, min: (v) => +(v.min * 0.9).toFixed(2) },
  series: [{ name: "换手率", type: "line", data: turnoverRate, smooth: true, connectNulls: true, symbol: "circle", symbolSize: 3, lineStyle: { width: 1.5, color: "#409eff" }, itemStyle: { color: "#409eff" }, areaStyle: { color: { type: "linear", x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: "rgba(64,158,255,0.2)" }, { offset: 1, color: "rgba(64,158,255,0.02)" }] } } }],
});

const buildTurnoverRateZoomOption = (labels, turnoverRate) => ({
  tooltip: { trigger: "axis", confine: true, formatter: (params) => { const p = params[0]; if (p.value == null) return `${p.axisValue}<br/>换手率：-`; return `${p.axisValue}<br/>${p.marker}换手率：<span style="color:#409eff">${p.value}%</span>`; } },
  legend: { show: false },
  grid: { left: 60, right: 20, top: 16, bottom: 50 },
  xAxis: { type: "category", data: labels, axisLabel: { fontSize: 11, rotate: 30 }, axisLine: { show: true }, axisTick: { show: true } },
  yAxis: { type: "value", show: true, axisLabel: { fontSize: 11, formatter: (v) => `${+v.toFixed(2)}%` }, splitLine: { lineStyle: { color: "#f0f0f0" } }, min: (v) => +(v.min * 0.9).toFixed(2) },
  series: [{ name: "换手率", type: "line", data: turnoverRate, smooth: true, connectNulls: true, symbol: "circle", symbolSize: 5, lineStyle: { width: 2, color: "#409eff" }, itemStyle: { color: "#409eff" }, areaStyle: { color: { type: "linear", x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: "rgba(64,158,255,0.2)" }, { offset: 1, color: "rgba(64,158,255,0.02)" }] } } }],
});

const buildChartOption = (labels, mainNet, superNet, largeNet, middleNet, smallNet) => ({
  tooltip: {
    trigger: "axis", confine: false,
    position: (point, params, dom, rect, size) => [0, -size.contentSize[1] - 4],
    formatter: (params) => {
      const [main, superN, large, middle, small] = params;
      const fmt = (p) => { const color = p.value >= 0 ? "#f56c6c" : "#67c23a"; return `${p.marker}${p.seriesName}:<span style="color:${color}">${p.value >= 0 ? "+" : ""}${p.value}万</span>`; };
      return `<div style="font-size:11px;white-space:nowrap">${params[0].axisValue}<br/>${fmt(main)} ${fmt(superN)}<br/>${fmt(large)} ${fmt(middle)} ${fmt(small)}</div>`;
    },
  },
  legend: { show: false },
  grid: { left: 2, right: 2, top: 4, bottom: 16 },
  xAxis: { type: "category", data: labels, axisLabel: { fontSize: 9, rotate: 30 }, axisLine: { show: false }, axisTick: { show: false } },
  yAxis: { type: "value", show: false },
  series: [
    { name: "主力", type: "bar", data: mainNet, itemStyle: { color: (p) => (p.value >= 0 ? "#f56c6c" : "#67c23a") }, barMaxWidth: 8 },
    { name: "超大单", type: "line", data: superNet, smooth: true, lineStyle: { width: 1 }, symbol: "none", color: "#f56c6c" },
    { name: "大单", type: "line", data: largeNet, smooth: true, lineStyle: { width: 1 }, symbol: "none", color: "#e6a23c" },
    { name: "中单", type: "line", data: middleNet, smooth: true, lineStyle: { width: 1 }, symbol: "none", color: "#e6d23c" },
    { name: "小单", type: "line", data: smallNet, smooth: true, lineStyle: { width: 1 }, symbol: "none", color: "#67c23a" },
  ],
});

const buildZoomChartOption = (labels, mainNet, superNet, largeNet, middleNet, smallNet) => ({
  tooltip: {
    trigger: "axis", confine: true,
    formatter: (params) => { let html = `${params[0].axisValue}<br/>`; params.forEach((p) => { const color = p.value >= 0 ? "#f56c6c" : "#67c23a"; html += `${p.marker}${p.seriesName}：<span style="color:${color}">${p.value >= 0 ? "+" : ""}${p.value}万</span><br/>`; }); return html; },
  },
  legend: { show: true, bottom: 0, data: ["主力", "超大单", "大单", "中单", "小单"], textStyle: { fontSize: 12 } },
  grid: { left: 70, right: 20, top: 16, bottom: 60 },
  xAxis: { type: "category", data: labels, axisLabel: { fontSize: 11, rotate: 30 }, axisLine: { show: true }, axisTick: { show: true } },
  yAxis: { type: "value", show: true, axisLabel: { formatter: (v) => `${v}万`, fontSize: 11 }, splitLine: { lineStyle: { color: "#f0f0f0" } } },
  series: [
    { name: "主力", type: "bar", data: mainNet, itemStyle: { color: (p) => (p.value >= 0 ? "#f56c6c" : "#67c23a") }, barMaxWidth: 20 },
    { name: "超大单", type: "line", data: superNet, smooth: true, lineStyle: { width: 2 }, symbol: "circle", symbolSize: 4, color: "#f56c6c" },
    { name: "大单", type: "line", data: largeNet, smooth: true, lineStyle: { width: 2 }, symbol: "circle", symbolSize: 4, color: "#e6a23c" },
    { name: "中单", type: "line", data: middleNet, smooth: true, lineStyle: { width: 2 }, symbol: "circle", symbolSize: 4, color: "#e6d23c" },
    { name: "小单", type: "line", data: smallNet, smooth: true, lineStyle: { width: 2 }, symbol: "circle", symbolSize: 4, color: "#67c23a" },
  ],
});

const renderMiniCharts = () => {
  Object.entries(historyData.value).forEach(([code, records]) => {
    const labels = records.map((r) => formatDateLabel(r.tradeDate));
    const mainNet = records.map((r) => +(r.mainNet / 10000).toFixed(2));
    const superNet = records.map((r) => +(r.superNet / 10000).toFixed(2));
    const largeNet = records.map((r) => +(r.largeNet / 10000).toFixed(2));
    const middleNet = records.map((r) => +(r.middleNet / 10000).toFixed(2));
    const smallNet = records.map((r) => +(r.smallNet / 10000).toFixed(2));
    const turnoverRate = records.map((r) => r.turnoverRate != null ? +r.turnoverRate.toFixed(2) : null);

    if (periodDay.value > 1) {
      const trEl = turnoverRateChartRefs.value[code];
      if (trEl) { const trChart = echarts.init(trEl); turnoverRateChartInstances[code] = trChart; trChart.setOption(buildTurnoverRateOption(labels, turnoverRate)); }
    }

    const el = chartRefs.value[code];
    if (el) { const chart = echarts.init(el); chartInstances[code] = chart; chart.setOption(buildChartOption(labels, mainNet, superNet, largeNet, middleNet, smallNet)); }

    if (periodDay.value > 1) {
      const cumEl = cumChartRefs.value[code];
      if (cumEl) { const cumChart = echarts.init(cumEl); cumChartInstances[code] = cumChart; cumChart.setOption(buildChartOption(labels, cumulate(mainNet), cumulate(superNet), cumulate(largeNet), cumulate(middleNet), cumulate(smallNet))); }
    }
  });

  if (Object.keys(historyData.value).length > 0) {
    if (periodDay.value > 1 && totalTurnoverRateChartRef.value) {
      const { allLabels, turnoverRate } = buildTotalTurnoverRateData();
      const trTotalChart = echarts.init(totalTurnoverRateChartRef.value);
      turnoverRateChartInstances["__total__"] = trTotalChart;
      trTotalChart.setOption(buildTurnoverRateOption(allLabels, turnoverRate));
    }

    const { allLabels, mainNet, superNet, largeNet, middleNet, smallNet } = buildTotalData();

    if (totalChartRef.value) { const totalChart = echarts.init(totalChartRef.value); chartInstances["__total__"] = totalChart; totalChart.setOption(buildChartOption(allLabels, mainNet, superNet, largeNet, middleNet, smallNet)); }

    if (periodDay.value > 1 && totalCumChartRef.value) { const totalCumChart = echarts.init(totalCumChartRef.value); cumChartInstances["__total__"] = totalCumChart; totalCumChart.setOption(buildChartOption(allLabels, cumulate(mainNet), cumulate(superNet), cumulate(largeNet), cumulate(middleNet), cumulate(smallNet))); }
  }
};

// ===================== 图表放大弹窗 =====================
const chartDialogVisible = ref(false);
const chartDialogTitle = ref("");
const zoomChartRef = ref(null);
let zoomChartInstance = null;
const pendingChartData = ref(null);

const openTurnoverRateZoomChart = (code) => {
  let labels, turnoverRate;
  if (code === "__total__") {
    const total = buildTotalTurnoverRateData();
    labels = total.allLabels; turnoverRate = total.turnoverRate;
    chartDialogTitle.value = "合计 — 换手率";
  } else {
    const records = historyData.value[code];
    const stockName = moneyFlowList.value.find((r) => r.stockCode === code)?.stockName || code;
    labels = records.map((r) => formatDateLabel(r.tradeDate));
    turnoverRate = records.map((r) => r.turnoverRate != null ? +r.turnoverRate.toFixed(2) : null);
    chartDialogTitle.value = `${stockName}（${code}）— 换手率`;
  }
  pendingChartData.value = { type: "turnoverRate", labels, turnoverRate };
  chartDialogVisible.value = true;
};

const openZoomChart = (code, isCum) => {
  let labels, mainNet, superNet, largeNet, middleNet, smallNet;
  if (code === "__total__") {
    const total = buildTotalData();
    labels = total.allLabels; mainNet = total.mainNet; superNet = total.superNet;
    largeNet = total.largeNet; middleNet = total.middleNet; smallNet = total.smallNet;
    chartDialogTitle.value = isCum ? "合计 — 累计量能" : "合计 — 量能";
  } else {
    const records = historyData.value[code];
    const stockName = moneyFlowList.value.find((r) => r.stockCode === code)?.stockName || code;
    labels = records.map((r) => formatDateLabel(r.tradeDate));
    mainNet = records.map((r) => +(r.mainNet / 10000).toFixed(2));
    superNet = records.map((r) => +(r.superNet / 10000).toFixed(2));
    largeNet = records.map((r) => +(r.largeNet / 10000).toFixed(2));
    middleNet = records.map((r) => +(r.middleNet / 10000).toFixed(2));
    smallNet = records.map((r) => +(r.smallNet / 10000).toFixed(2));
    chartDialogTitle.value = isCum ? `${stockName}（${code}）— 累计量能` : `${stockName}（${code}）— 量能`;
  }
  if (isCum) { mainNet = cumulate(mainNet); superNet = cumulate(superNet); largeNet = cumulate(largeNet); middleNet = cumulate(middleNet); smallNet = cumulate(smallNet); }
  pendingChartData.value = { type: "flow", labels, mainNet, superNet, largeNet, middleNet, smallNet };
  chartDialogVisible.value = true;
};

const initZoomChart = () => {
  if (!pendingChartData.value) return;
  if (zoomChartInstance) zoomChartInstance.dispose();
  zoomChartInstance = echarts.init(zoomChartRef.value);
  const d = pendingChartData.value;
  if (d.type === "turnoverRate") {
    zoomChartInstance.setOption(buildTurnoverRateZoomOption(d.labels, d.turnoverRate));
  } else {
    zoomChartInstance.setOption(buildZoomChartOption(d.labels, d.mainNet, d.superNet, d.largeNet, d.middleNet, d.smallNet));
  }
};

const disposeZoomChart = () => {
  if (zoomChartInstance) { zoomChartInstance.dispose(); zoomChartInstance = null; }
  pendingChartData.value = null;
};

const formatAmount = (val) => {
  if (val === null || val === undefined) return "-";
  const abs = Math.abs(val);
  const sign = val >= 0 ? "+" : "-";
  if (abs >= 1e8) return `${sign}${(abs / 1e8).toFixed(2)}亿`;
  if (abs >= 1e4) return `${sign}${(abs / 1e4).toFixed(2)}万`;
  return `${sign}${abs}`;
};

const getAddReason = (stockCode) => {
  return stockList.value.find((s) => s.stockCode === stockCode)?.addReason || "-";
};
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
.panel-header-actions { display: flex; gap: 8px; align-items: center; }

.money-flow-section {
  padding: 12px 16px;
  background: #fafbfc;
  border-bottom: 1px solid #f0f0f0;
  flex: 1;
  overflow-y: auto;
}

.flow-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.section-label { font-size: 12px; color: #999; font-weight: 500; }

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
  max-width: 220px;
  width: 220px;
  padding: 4px 8px !important;
}

.chart-cell-sm {
  min-width: 140px;
  max-width: 160px;
  width: 160px;
}

.mini-chart {
  width: 100%;
  height: 80px;
}

.chart-clickable {
  cursor: zoom-in;
  transition: opacity 0.15s;
}

.chart-clickable:hover { opacity: 0.75; }

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