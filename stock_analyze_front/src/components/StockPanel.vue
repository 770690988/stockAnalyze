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
              <th width="80">现价</th>
              <th width="90">涨跌幅</th>
              <th width="120">主力净流入</th>
              <th width="120">超大单</th>
              <th width="110">大单</th>
              <th width="110">中单</th>
              <th width="110">小单</th>
              <th v-if="periodDay > 1" width="160">量比</th>
              <th width="220">量能</th>
              <th width="220">累计量能</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="row in moneyFlowList" :key="row.stockCode">
              <td>{{ row.stockCode }}</td>
              <td>{{ row.stockName }}</td>
              <td>{{ row.stockPrice }}</td>
              <td>
                <span :class="row.stockPriceRate >= 0 ? 'rise' : 'fall'"
                  >{{ row.stockPriceRate >= 0 ? "+" : ""
                  }}{{ row.stockPriceRate }}%</span
                >
              </td>
              <td>
                <span :class="row.mainNet >= 0 ? 'rise' : 'fall'">{{
                  formatAmount(row.mainNet)
                }}</span>
              </td>
              <td>
                <span :class="row.superNet >= 0 ? 'rise' : 'fall'">{{
                  formatAmount(row.superNet)
                }}</span>
              </td>
              <td>
                <span :class="row.largeNet >= 0 ? 'rise' : 'fall'">{{
                  formatAmount(row.largeNet)
                }}</span>
              </td>
              <td>
                <span :class="row.middleNet >= 0 ? 'rise' : 'fall'">{{
                  formatAmount(row.middleNet)
                }}</span>
              </td>
              <td>
                <span :class="row.smallNet >= 0 ? 'rise' : 'fall'">{{
                  formatAmount(row.smallNet)
                }}</span>
              </td>
              <td v-if="periodDay > 1" class="chart-cell chart-cell-sm">
                <div
                  :ref="
                    (el) => {
                      if (el) volumeRatioChartRefs[row.stockCode] = el;
                    }
                  "
                  class="mini-chart chart-clickable"
                  @click="openVolumeRatioZoomChart(row.stockCode)"
                ></div>
              </td>
              <td class="chart-cell">
                <div
                  :ref="
                    (el) => {
                      if (el) chartRefs[row.stockCode] = el;
                    }
                  "
                  class="mini-chart chart-clickable"
                  @click="openZoomChart(row.stockCode, false)"
                ></div>
              </td>
              <td class="chart-cell">
                <div
                  :ref="
                    (el) => {
                      if (el) cumChartRefs[row.stockCode] = el;
                    }
                  "
                  class="mini-chart chart-clickable"
                  @click="openZoomChart(row.stockCode, true)"
                ></div>
              </td>
            </tr>
            <!-- 合计行 -->
            <tr class="total-row">
              <td colspan="4">合计</td>
              <td>
                <span :class="totalMainNet >= 0 ? 'rise' : 'fall'">{{
                  formatAmount(totalMainNet)
                }}</span>
              </td>
              <td>
                <span :class="totalSuperNet >= 0 ? 'rise' : 'fall'">{{
                  formatAmount(totalSuperNet)
                }}</span>
              </td>
              <td>
                <span :class="totalLargeNet >= 0 ? 'rise' : 'fall'">{{
                  formatAmount(totalLargeNet)
                }}</span>
              </td>
              <td>
                <span :class="totalMiddleNet >= 0 ? 'rise' : 'fall'">{{
                  formatAmount(totalMiddleNet)
                }}</span>
              </td>
              <td>
                <span :class="totalSmallNet >= 0 ? 'rise' : 'fall'">{{
                  formatAmount(totalSmallNet)
                }}</span>
              </td>
              <td v-if="periodDay > 1" class="chart-cell chart-cell-sm">
                <div
                  ref="totalVolumeRatioChartRef"
                  class="mini-chart chart-clickable"
                  @click="openVolumeRatioZoomChart('__total__')"
                ></div>
              </td>
              <td class="chart-cell">
                <div
                  ref="totalChartRef"
                  class="mini-chart chart-clickable"
                  @click="openZoomChart('__total__', false)"
                ></div>
              </td>
              <td class="chart-cell">
                <div
                  ref="totalCumChartRef"
                  class="mini-chart chart-clickable"
                  @click="openZoomChart('__total__', true)"
                ></div>
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
      >
        <el-table-column prop="stockCode" label="股票代码" width="110" />
        <el-table-column prop="stockName" label="股票名称" width="110" />
        <el-table-column
          prop="addReason"
          label="加入理由"
          min-width="180"
          show-overflow-tooltip
        />
        <el-table-column
          prop="remark"
          label="备注"
          min-width="140"
          show-overflow-tooltip
        />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="createTime" label="加入时间" width="180" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link size="small" @click="openEditDialog(row)"
              >编辑</el-button
            >
            <el-popconfirm
              title="确认从板块移除该股票？"
              @confirm="handleDelete(row.id)"
            >
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
          <el-input
            v-model="form.stockCode"
            placeholder="请输入股票代码"
            :disabled="dialogMode === 'edit'"
          />
        </el-form-item>
        <el-form-item label="股票名称" prop="stockName">
          <el-input v-model="form.stockName" placeholder="请输入股票名称" />
        </el-form-item>
        <el-form-item label="加入理由">
          <el-input
            v-model="form.addReason"
            type="textarea"
            :rows="3"
            placeholder="记录加入该股票的理由（选填）"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="2"
            placeholder="其他备注（选填）"
          />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :max="999" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting"
          >确定</el-button
        >
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
import { Plus, Folder, TrendCharts, List } from "@element-plus/icons-vue";
import * as echarts from "echarts";
import {
  getStockList,
  addStock,
  updateStock,
  deleteStock,
  getMoneyFlowHistory,
} from "../api/watchlist";

const props = defineProps({
  selectedBk: { type: Object, default: null },
});

// ===================== 股票列表 =====================
const stockList = ref([]);
const stockLoading = ref(false);

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
      periodDay.value = 1;
      moneyFlowList.value = [];
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
const form = ref({
  stockCode: "",
  stockName: "",
  addReason: "",
  remark: "",
  sort: 0,
});
const rules = {
  stockCode: [{ required: true, message: "请输入股票代码", trigger: "blur" }],
};

const openAddDialog = () => {
  dialogMode.value = "add";
  dialogVisible.value = true;
};
const openEditDialog = (row) => {
  dialogMode.value = "edit";
  form.value = { ...row };
  dialogVisible.value = true;
};
const resetForm = () => {
  form.value = {
    stockCode: "",
    stockName: "",
    addReason: "",
    remark: "",
    sort: 0,
  };
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
const periodDay = ref(1);

const chartRefs = ref({});
const cumChartRefs = ref({});
const volumeRatioChartRefs = ref({});
const chartInstances = {};
const cumChartInstances = {};
const volumeRatioChartInstances = {};

// 合计
const totalMainNet = computed(() =>
  moneyFlowList.value.reduce((s, r) => s + (r.mainNet || 0), 0)
);
const totalSuperNet = computed(() =>
  moneyFlowList.value.reduce((s, r) => s + (r.superNet || 0), 0)
);
const totalLargeNet = computed(() =>
  moneyFlowList.value.reduce((s, r) => s + (r.largeNet || 0), 0)
);
const totalMiddleNet = computed(() =>
  moneyFlowList.value.reduce((s, r) => s + (r.middleNet || 0), 0)
);
const totalSmallNet = computed(() =>
  moneyFlowList.value.reduce((s, r) => s + (r.smallNet || 0), 0)
);

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
  Object.values(volumeRatioChartInstances).forEach((c) => c.dispose());
  Object.keys(volumeRatioChartInstances).forEach(
    (k) => delete volumeRatioChartInstances[k]
  );
  chartRefs.value = {};
  cumChartRefs.value = {};
  volumeRatioChartRefs.value = {};

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
        sortedList.push(records[records.length - 1]);
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
const totalVolumeRatioChartRef = ref(null);

// 逐步累加
const cumulate = (arr) =>
  arr.reduce((acc, val, i) => {
    acc.push(+((acc[i - 1] || 0) + val).toFixed(2));
    return acc;
  }, []);

// 当日取 HH:mm，多日取 yyyy-MM-dd
const formatDateLabel = (tradeDate) => {
  if (periodDay.value === 1) {
    return tradeDate.length > 10 ? tradeDate.substring(11, 16) : tradeDate;
  }
  return tradeDate.substring(0, 10);
};

// ===================== 当日合计分桶逻辑 =====================
// 标准时间桶：09:30 起每15分钟，下午 14:00 起每15分钟，收盘 15:00
const TIME_SLOTS = [
  "09:30",
  "09:45",
  "10:00",
  "10:15",
  "10:30",
  "10:45",
  "11:00",
  "11:15",
  "11:30",
  "14:00",
  "14:15",
  "14:30",
  "14:45",
  "15:00",
];

const toMinutes = (hhmm) => {
  const [h, m] = hhmm.split(":").map(Number);
  return h * 60 + m;
};

// 找到时间点归属的桶（落在 [slot, slot+15) 范围内）
const findSlot = (timeStr) => {
  const t = toMinutes(timeStr);
  for (let i = 0; i < TIME_SLOTS.length; i++) {
    const slotStart = toMinutes(TIME_SLOTS[i]);
    // 最后一个桶单独处理（15:00 精确匹配）
    const slotEnd =
      i < TIME_SLOTS.length - 1 ? toMinutes(TIME_SLOTS[i + 1]) : slotStart + 1;
    if (t >= slotStart && t < slotEnd) return TIME_SLOTS[i];
  }
  return null;
};

// 当日合计：按时间桶聚合所有股票数据
const buildTotalDataIntraday = () => {
  const sumBySlot = {};
  TIME_SLOTS.forEach((slot) => {
    sumBySlot[slot] = {
      mainNet: 0,
      superNet: 0,
      largeNet: 0,
      middleNet: 0,
      smallNet: 0,
      hasData: false,
    };
  });

  Object.values(historyData.value).forEach((records) => {
    records.forEach((r) => {
      const timeLabel =
        r.tradeDate.length > 10 ? r.tradeDate.substring(11, 16) : r.tradeDate;
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

  // 只保留有数据的桶
  const usedSlots = TIME_SLOTS.filter((s) => sumBySlot[s].hasData);
  return {
    allLabels: usedSlots,
    mainNet: usedSlots.map((s) => +(sumBySlot[s].mainNet / 10000).toFixed(2)),
    superNet: usedSlots.map((s) => +(sumBySlot[s].superNet / 10000).toFixed(2)),
    largeNet: usedSlots.map((s) => +(sumBySlot[s].largeNet / 10000).toFixed(2)),
    middleNet: usedSlots.map(
      (s) => +(sumBySlot[s].middleNet / 10000).toFixed(2)
    ),
    smallNet: usedSlots.map((s) => +(sumBySlot[s].smallNet / 10000).toFixed(2)),
  };
};

// 多日合计：按日期标签聚合
const buildTotalDataMultiDay = () => {
  const allLabels = [
    ...new Set(
      Object.values(historyData.value).flatMap((records) =>
        records.map((r) => r.tradeDate.substring(0, 10))
      )
    ),
  ].sort();

  const sumByLabel = {};
  allLabels.forEach((label) => {
    sumByLabel[label] = {
      mainNet: 0,
      superNet: 0,
      largeNet: 0,
      middleNet: 0,
      smallNet: 0,
    };
  });
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
    superNet: allLabels.map(
      (d) => +(sumByLabel[d].superNet / 10000).toFixed(2)
    ),
    largeNet: allLabels.map(
      (d) => +(sumByLabel[d].largeNet / 10000).toFixed(2)
    ),
    middleNet: allLabels.map(
      (d) => +(sumByLabel[d].middleNet / 10000).toFixed(2)
    ),
    smallNet: allLabels.map(
      (d) => +(sumByLabel[d].smallNet / 10000).toFixed(2)
    ),
  };
};

// 统一入口，根据 periodDay 选择对应方法
const buildTotalData = () => {
  return periodDay.value === 1
    ? buildTotalDataIntraday()
    : buildTotalDataMultiDay();
};

// 构建合计量比数据（多日用，每日取平均）
const buildTotalVolumeRatioData = () => {
  const allLabels = [
    ...new Set(
      Object.values(historyData.value).flatMap((records) =>
        records.map((r) => r.tradeDate.substring(0, 10))
      )
    ),
  ].sort();

  const byLabel = {};
  allLabels.forEach((label) => {
    byLabel[label] = { sum: 0, count: 0 };
  });
  Object.values(historyData.value).forEach((records) => {
    records.forEach((r) => {
      const label = r.tradeDate.substring(0, 10);
      if (byLabel[label] && r.volumeRatio != null) {
        byLabel[label].sum += r.volumeRatio;
        byLabel[label].count += 1;
      }
    });
  });

  return {
    allLabels,
    volumeRatio: allLabels.map((d) =>
      byLabel[d].count > 0
        ? +(byLabel[d].sum / byLabel[d].count).toFixed(2)
        : null
    ),
  };
};

// 量比 mini 图 option
const buildVolumeRatioOption = (labels, volumeRatio) => ({
  tooltip: {
    trigger: "axis",
    confine: true,
    formatter: (params) => {
      const p = params[0];
      if (p.value == null) return `${p.axisValue}<br/>量比：-`;
      const color = p.value >= 1 ? "#f56c6c" : "#67c23a";
      return `${p.axisValue}<br/>${p.marker}量比：<span style="color:${color}">${p.value}</span>`;
    },
  },
  legend: { show: false },
  grid: { left: 2, right: 2, top: 4, bottom: 16 },
  xAxis: {
    type: "category",
    data: labels,
    axisLabel: { fontSize: 9, rotate: 30 },
    axisLine: { show: false },
    axisTick: { show: false },
  },
  yAxis: {
    type: "value",
    show: false,
    min: (v) => Math.min(v.min * 0.95, 0.8),
  },
  series: [
    {
      name: "量比",
      type: "line",
      data: volumeRatio,
      smooth: true,
      symbol: "circle",
      symbolSize: 3,
      lineStyle: { width: 1.5 },
      itemStyle: {
        color: (p) => ((p.value ?? 0) >= 1 ? "#f56c6c" : "#67c23a"),
      },
      areaStyle: {
        color: {
          type: "linear",
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: "rgba(245,108,108,0.15)" },
            { offset: 1, color: "rgba(103,194,58,0.15)" },
          ],
        },
      },
      markLine: {
        silent: true,
        symbol: "none",
        lineStyle: { color: "#999", type: "dashed", width: 1 },
        data: [{ yAxis: 1 }],
        label: { show: false },
      },
    },
  ],
});

// 量比放大图 option
const buildVolumeRatioZoomOption = (labels, volumeRatio) => ({
  tooltip: {
    trigger: "axis",
    confine: true,
    formatter: (params) => {
      const p = params[0];
      if (p.value == null) return `${p.axisValue}<br/>量比：-`;
      const color = p.value >= 1 ? "#f56c6c" : "#67c23a";
      return `${p.axisValue}<br/>${p.marker}量比：<span style="color:${color}">${p.value}</span>`;
    },
  },
  legend: { show: false },
  grid: { left: 50, right: 20, top: 16, bottom: 50 },
  xAxis: {
    type: "category",
    data: labels,
    axisLabel: { fontSize: 11, rotate: 30 },
    axisLine: { show: true },
    axisTick: { show: true },
  },
  yAxis: {
    type: "value",
    show: true,
    axisLabel: { fontSize: 11 },
    splitLine: { lineStyle: { color: "#f0f0f0" } },
    min: (v) => Math.min(v.min * 0.95, 0.8),
  },
  series: [
    {
      name: "量比",
      type: "line",
      data: volumeRatio,
      smooth: true,
      symbol: "circle",
      symbolSize: 5,
      lineStyle: { width: 2 },
      itemStyle: {
        color: (p) => ((p.value ?? 0) >= 1 ? "#f56c6c" : "#67c23a"),
      },
      areaStyle: {
        color: {
          type: "linear",
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: "rgba(245,108,108,0.2)" },
            { offset: 1, color: "rgba(103,194,58,0.2)" },
          ],
        },
      },
      markLine: {
        silent: true,
        symbol: "none",
        lineStyle: { color: "#999", type: "dashed", width: 1 },
        data: [{ yAxis: 1 }],
        label: { formatter: "基准1", fontSize: 10, color: "#999" },
      },
    },
  ],
});

// 资金流向 mini 图 option
const buildChartOption = (
  labels,
  mainNet,
  superNet,
  largeNet,
  middleNet,
  smallNet
) => ({
  tooltip: {
    trigger: "axis",
    confine: false,
    position: (point, params, dom, rect, size) => {
      return [0, -size.contentSize[1] - 4];
    },
    formatter: (params) => {
      const [main, superN, large, middle, small] = params;
      const fmt = (p) => {
        const color = p.value >= 0 ? "#f56c6c" : "#67c23a";
        return `${p.marker}${p.seriesName}:<span style="color:${color}">${
          p.value >= 0 ? "+" : ""
        }${p.value}万</span>`;
      };
      return `<div style="font-size:11px;white-space:nowrap">
    ${params[0].axisValue}<br/>
    ${fmt(main)} ${fmt(superN)}<br/>
    ${fmt(large)} ${fmt(middle)} ${fmt(small)}
  </div>`;
    },
  },
  legend: { show: false },
  grid: { left: 2, right: 2, top: 4, bottom: 16 },
  xAxis: {
    type: "category",
    data: labels,
    axisLabel: { fontSize: 9, rotate: 30 },
    axisLine: { show: false },
    axisTick: { show: false },
  },
  yAxis: { type: "value", show: false },
  series: [
    {
      name: "主力",
      type: "bar",
      data: mainNet,
      itemStyle: { color: (p) => (p.value >= 0 ? "#f56c6c" : "#67c23a") },
      barMaxWidth: 8,
    },
    {
      name: "超大单",
      type: "line",
      data: superNet,
      smooth: true,
      lineStyle: { width: 1 },
      symbol: "none",
      color: "#f56c6c",
    },
    {
      name: "大单",
      type: "line",
      data: largeNet,
      smooth: true,
      lineStyle: { width: 1 },
      symbol: "none",
      color: "#e6a23c",
    },
    {
      name: "中单",
      type: "line",
      data: middleNet,
      smooth: true,
      lineStyle: { width: 1 },
      symbol: "none",
      color: "#e6d23c",
    },
    {
      name: "小单",
      type: "line",
      data: smallNet,
      smooth: true,
      lineStyle: { width: 1 },
      symbol: "none",
      color: "#67c23a",
    },
  ],
});

// 资金流向放大图 option
const buildZoomChartOption = (
  labels,
  mainNet,
  superNet,
  largeNet,
  middleNet,
  smallNet
) => ({
  tooltip: {
    trigger: "axis",
    confine: true,
    formatter: (params) => {
      let html = `${params[0].axisValue}<br/>`;
      params.forEach((p) => {
        const color = p.value >= 0 ? "#f56c6c" : "#67c23a";
        html += `${p.marker}${p.seriesName}：<span style="color:${color}">${
          p.value >= 0 ? "+" : ""
        }${p.value}万</span><br/>`;
      });
      return html;
    },
  },
  legend: {
    show: true,
    bottom: 0,
    data: ["主力", "超大单", "大单", "中单", "小单"],
    textStyle: { fontSize: 12 },
  },
  grid: { left: 70, right: 20, top: 16, bottom: 60 },
  xAxis: {
    type: "category",
    data: labels,
    axisLabel: { fontSize: 11, rotate: 30 },
    axisLine: { show: true },
    axisTick: { show: true },
  },
  yAxis: {
    type: "value",
    show: true,
    axisLabel: { formatter: (v) => `${v}万`, fontSize: 11 },
    splitLine: { lineStyle: { color: "#f0f0f0" } },
  },
  series: [
    {
      name: "主力",
      type: "bar",
      data: mainNet,
      itemStyle: { color: (p) => (p.value >= 0 ? "#f56c6c" : "#67c23a") },
      barMaxWidth: 20,
    },
    {
      name: "超大单",
      type: "line",
      data: superNet,
      smooth: true,
      lineStyle: { width: 2 },
      symbol: "circle",
      symbolSize: 4,
      color: "#f56c6c",
    },
    {
      name: "大单",
      type: "line",
      data: largeNet,
      smooth: true,
      lineStyle: { width: 2 },
      symbol: "circle",
      symbolSize: 4,
      color: "#e6a23c",
    },
    {
      name: "中单",
      type: "line",
      data: middleNet,
      smooth: true,
      lineStyle: { width: 2 },
      symbol: "circle",
      symbolSize: 4,
      color: "#e6d23c",
    },
    {
      name: "小单",
      type: "line",
      data: smallNet,
      smooth: true,
      lineStyle: { width: 2 },
      symbol: "circle",
      symbolSize: 4,
      color: "#67c23a",
    },
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
    const volumeRatio = records.map((r) =>
      r.volumeRatio != null ? +r.volumeRatio.toFixed(2) : null
    );

    // 量比图（仅多日）
    if (periodDay.value > 1) {
      const vrEl = volumeRatioChartRefs.value[code];
      if (vrEl) {
        const vrChart = echarts.init(vrEl);
        volumeRatioChartInstances[code] = vrChart;
        vrChart.setOption(buildVolumeRatioOption(labels, volumeRatio));
      }
    }

    // 量能图
    const el = chartRefs.value[code];
    if (el) {
      const chart = echarts.init(el);
      chartInstances[code] = chart;
      chart.setOption(
        buildChartOption(
          labels,
          mainNet,
          superNet,
          largeNet,
          middleNet,
          smallNet
        )
      );
    }

    // 累计量能图
    const cumEl = cumChartRefs.value[code];
    if (cumEl) {
      const cumChart = echarts.init(cumEl);
      cumChartInstances[code] = cumChart;
      cumChart.setOption(
        buildChartOption(
          labels,
          cumulate(mainNet),
          cumulate(superNet),
          cumulate(largeNet),
          cumulate(middleNet),
          cumulate(smallNet)
        )
      );
    }
  });

  if (Object.keys(historyData.value).length > 0) {
    // 合计量比图（仅多日）
    if (periodDay.value > 1 && totalVolumeRatioChartRef.value) {
      const { allLabels, volumeRatio } = buildTotalVolumeRatioData();
      const vrTotalChart = echarts.init(totalVolumeRatioChartRef.value);
      volumeRatioChartInstances["__total__"] = vrTotalChart;
      vrTotalChart.setOption(buildVolumeRatioOption(allLabels, volumeRatio));
    }

    // 合计量能图（当日用分桶，多日用日期）
    const { allLabels, mainNet, superNet, largeNet, middleNet, smallNet } =
      buildTotalData();

    if (totalChartRef.value) {
      const totalChart = echarts.init(totalChartRef.value);
      chartInstances["__total__"] = totalChart;
      totalChart.setOption(
        buildChartOption(
          allLabels,
          mainNet,
          superNet,
          largeNet,
          middleNet,
          smallNet
        )
      );
    }

    // 合计累计量能图
    if (totalCumChartRef.value) {
      const totalCumChart = echarts.init(totalCumChartRef.value);
      cumChartInstances["__total__"] = totalCumChart;
      totalCumChart.setOption(
        buildChartOption(
          allLabels,
          cumulate(mainNet),
          cumulate(superNet),
          cumulate(largeNet),
          cumulate(middleNet),
          cumulate(smallNet)
        )
      );
    }
  }
};

// ===================== 图表放大弹窗 =====================
const chartDialogVisible = ref(false);
const chartDialogTitle = ref("");
const zoomChartRef = ref(null);
let zoomChartInstance = null;
const pendingChartData = ref(null);

const openVolumeRatioZoomChart = (code) => {
  let labels, volumeRatio;
  if (code === "__total__") {
    const total = buildTotalVolumeRatioData();
    labels = total.allLabels;
    volumeRatio = total.volumeRatio;
    chartDialogTitle.value = "合计 — 量比";
  } else {
    const records = historyData.value[code];
    const stockName =
      moneyFlowList.value.find((r) => r.stockCode === code)?.stockName || code;
    labels = records.map((r) => formatDateLabel(r.tradeDate));
    volumeRatio = records.map((r) =>
      r.volumeRatio != null ? +r.volumeRatio.toFixed(2) : null
    );
    chartDialogTitle.value = `${stockName}（${code}）— 量比`;
  }
  pendingChartData.value = { type: "volumeRatio", labels, volumeRatio };
  chartDialogVisible.value = true;
};

const openZoomChart = (code, isCum) => {
  let labels, mainNet, superNet, largeNet, middleNet, smallNet;
  if (code === "__total__") {
    const total = buildTotalData();
    labels = total.allLabels;
    mainNet = total.mainNet;
    superNet = total.superNet;
    largeNet = total.largeNet;
    middleNet = total.middleNet;
    smallNet = total.smallNet;
    chartDialogTitle.value = isCum ? "合计 — 累计量能" : "合计 — 量能";
  } else {
    const records = historyData.value[code];
    const stockName =
      moneyFlowList.value.find((r) => r.stockCode === code)?.stockName || code;
    labels = records.map((r) => formatDateLabel(r.tradeDate));
    mainNet = records.map((r) => +(r.mainNet / 10000).toFixed(2));
    superNet = records.map((r) => +(r.superNet / 10000).toFixed(2));
    largeNet = records.map((r) => +(r.largeNet / 10000).toFixed(2));
    middleNet = records.map((r) => +(r.middleNet / 10000).toFixed(2));
    smallNet = records.map((r) => +(r.smallNet / 10000).toFixed(2));
    chartDialogTitle.value = isCum
      ? `${stockName}（${code}）— 累计量能`
      : `${stockName}（${code}）— 量能`;
  }
  if (isCum) {
    mainNet = cumulate(mainNet);
    superNet = cumulate(superNet);
    largeNet = cumulate(largeNet);
    middleNet = cumulate(middleNet);
    smallNet = cumulate(smallNet);
  }
  pendingChartData.value = {
    type: "flow",
    labels,
    mainNet,
    superNet,
    largeNet,
    middleNet,
    smallNet,
  };
  chartDialogVisible.value = true;
};

const initZoomChart = () => {
  if (!pendingChartData.value) return;
  if (zoomChartInstance) zoomChartInstance.dispose();
  zoomChartInstance = echarts.init(zoomChartRef.value);
  const d = pendingChartData.value;
  if (d.type === "volumeRatio") {
    zoomChartInstance.setOption(
      buildVolumeRatioZoomOption(d.labels, d.volumeRatio)
    );
  } else {
    zoomChartInstance.setOption(
      buildZoomChartOption(
        d.labels,
        d.mainNet,
        d.superNet,
        d.largeNet,
        d.middleNet,
        d.smallNet
      )
    );
  }
};

const disposeZoomChart = () => {
  if (zoomChartInstance) {
    zoomChartInstance.dispose();
    zoomChartInstance = null;
  }
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
  flex: 1;
  overflow-y: auto;
}

.flow-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 10px;
}

.section-label {
  font-size: 12px;
  color: #999;
  font-weight: 500;
}

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

.flow-table tr:hover td {
  background: #f5f7fa;
}

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

.chart-clickable:hover {
  opacity: 0.75;
}

.chart-loading {
  text-align: center;
  color: #999;
  padding: 20px 0;
  font-size: 13px;
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
