<template>
  <el-dialog
    v-model="visible"
    title="批量导入股票"
    width="700px"
    @close="handleClose"
  >
    <div class="import-steps">
      <!-- 步骤1：粘贴JSON -->
      <div v-if="step === 1">
        <div class="step-tip">
          粘贴股票 JSON 数组，格式：<code
            >[{"stockCode":"002466","addReason":"...","remark":"...","sort":0}]</code
          >
        </div>
        <el-input
          v-model="jsonText"
          type="textarea"
          :rows="12"
          placeholder="粘贴 JSON 内容..."
          @input="parseError = ''"
        />
        <div v-if="parseError" class="parse-error">{{ parseError }}</div>
      </div>

      <!-- 步骤2：预览 -->
      <div v-if="step === 2">
        <div class="preview-tip">
          共解析到 <b>{{ previewList.length }}</b> 条记录，确认后导入
        </div>
        <el-table :data="previewList" max-height="380" size="small" border>
          <el-table-column prop="stockCode" label="股票代码" width="100" />
          <el-table-column
            prop="addReason"
            label="加入理由"
            min-width="150"
            show-overflow-tooltip
          />
          <el-table-column
            prop="remark"
            label="备注"
            min-width="180"
            show-overflow-tooltip
          />
          <el-table-column prop="sort" label="排序" width="70" />
        </el-table>
      </div>

      <!-- 步骤3：结果 -->
      <div v-if="step === 3" class="result-wrap">
        <el-result
          :icon="resultIcon"
          :title="resultTitle"
          :sub-title="`成功 ${resultSuccess} 条，失败 ${resultFail} 条`"
        />
        <div v-if="failedCodes.length > 0" class="failed-codes">
          <div class="failed-label">失败股票代码：</div>
          <el-tag
            v-for="code in failedCodes"
            :key="code"
            type="danger"
            size="small"
            style="margin: 2px"
            >{{ code }}</el-tag
          >
        </div>
      </div>
    </div>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button v-if="step === 1" type="primary" @click="handleParse"
        >解析预览</el-button
      >
      <el-button v-if="step === 2" @click="step = 1">上一步</el-button>
      <el-button
        v-if="step === 2"
        type="primary"
        :loading="importing"
        @click="handleImport"
        >确认导入</el-button
      >
      <el-button v-if="step === 3" type="primary" @click="handleClose"
        >完成</el-button
      >
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed } from "vue";
import { ElMessage } from "element-plus";
import { addBatchStock } from "../api/watchlist";
import { WarningFilled } from "@element-plus/icons-vue";

const failedCodes = ref([]);

const props = defineProps({
  bkId: { type: Number, required: true },
});

const emit = defineEmits(["success"]);

const visible = ref(false);
const step = ref(1);
const jsonText = ref("");
const parseError = ref("");
const previewList = ref([]);
const importing = ref(false);
const resultSuccess = ref(0);
const resultFail = ref(0);

const resultIcon = computed(() =>
  resultFail.value === 0
    ? "success"
    : resultSuccess.value === 0
    ? "error"
    : "warning"
);
const resultTitle = computed(() =>
  resultFail.value === 0
    ? "全部导入成功"
    : resultSuccess.value === 0
    ? "全部导入失败"
    : "部分导入成功"
);

const open = () => {
  visible.value = true;
  step.value = 1;
  jsonText.value = "";
  parseError.value = "";
  previewList.value = [];
};

const handleParse = () => {
  if (!jsonText.value.trim()) {
    parseError.value = "请输入 JSON 内容";
    return;
  }
  try {
    const parsed = JSON.parse(jsonText.value.trim());
    if (!Array.isArray(parsed) || parsed.length === 0) {
      parseError.value = "格式错误：需要非空 JSON 数组";
      return;
    }
    // 校验每条必须有 stockCode
    const invalid = parsed.find((item) => !item.stockCode);
    if (invalid) {
      parseError.value = "存在缺少 stockCode 的记录，请检查";
      return;
    }
    previewList.value = parsed.map((item) => ({
      stockCode: item.stockCode,
      addReason: item.addReason || "",
      remark: item.remark || "",
      sort: item.sort ?? 0,
      bkId: props.bkId,
    }));
    step.value = 2;
  } catch (e) {
    parseError.value = "JSON 格式解析失败，请检查格式是否正确";
  }
};

const handleImport = async () => {
  importing.value = true;
  try {
    const res = await addBatchStock(previewList.value);
    resultSuccess.value = res.success;
    resultFail.value = res.fail;
    failedCodes.value = res.failedCodes || [];
    step.value = 3;
    if (res.success > 0) emit("success");
  } catch (e) {
    ElMessage.error("导入请求失败，请稍后重试");
  } finally {
    importing.value = false;
  }
};

const handleClose = () => {
  visible.value = false;
};

defineExpose({ open });
</script>

<style scoped>
.import-steps {
  min-height: 200px;
}

.step-tip {
  font-size: 12px;
  color: #909399;
  margin-bottom: 10px;
  line-height: 1.6;
}

.step-tip code {
  background: #f5f7fa;
  padding: 2px 6px;
  border-radius: 3px;
  font-size: 11px;
  color: #e6a23c;
}

.parse-error {
  color: #f56c6c;
  font-size: 12px;
  margin-top: 6px;
}

.preview-tip {
  font-size: 13px;
  color: #606266;
  margin-bottom: 10px;
}

.result-wrap {
  display: flex;
  flex-direction: column;  /* 改成纵向 */
  align-items: center;
  justify-content: center;
  padding: 20px 0;
}

.failed-codes {
  margin-top: 8px;
  text-align: center;
}

.failed-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 6px;
}

.failed-codes-title {
  font-size: 13px;
  color: #e6a23c;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.failed-codes-list {
  display: flex;
  flex-wrap: wrap;
}
</style>
