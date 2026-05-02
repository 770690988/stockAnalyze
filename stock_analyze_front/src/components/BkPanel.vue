<template>
  <aside class="bk-panel">
    <div class="panel-header">
      <span class="panel-title">板块列表</span>
      <el-button type="primary" size="small" @click="openAddDialog">
        <el-icon><Plus /></el-icon> 新增板块
      </el-button>
    </div>

    <div class="bk-list">
      <div
        v-for="bk in bkList"
        :key="bk.id"
        class="bk-item"
        :class="{ active: selectedId === bk.id }"
        @click="$emit('select', bk)"
      >
        <div class="bk-item-left">
          <span class="bk-id-tag">{{ bk.id }}</span>
          <div class="bk-info">
            <span class="bk-name">{{ bk.bkName }}</span>
            <span class="bk-type">{{ getTypeLabel(bk.type) }}</span>
          </div>
        </div>
        <div class="bk-item-actions" @click.stop>
          <el-button link size="small" @click="openEditDialog(bk)">
            <el-icon><Edit /></el-icon>
          </el-button>
          <el-popconfirm
            title="删除后板块内股票将一并删除，确认删除？"
            width="220"
            @confirm="handleDelete(bk.id)"
          >
            <template #reference>
              <el-button link size="small" type="danger">
                <el-icon><Delete /></el-icon>
              </el-button>
            </template>
          </el-popconfirm>
        </div>
      </div>

      <div v-if="bkList.length === 0" class="empty-tip">
        暂无板块，点击右上角新增
      </div>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'add' ? '新增板块' : '编辑板块'"
      width="460px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="板块名称" prop="bkName">
          <el-input v-model="form.bkName" placeholder="请输入板块名称" />
        </el-form-item>
        <el-form-item label="板块类型" prop="type">
          <!-- 动态从接口读取类型选项 -->
          <el-select v-model="form.type" placeholder="请选择类型" style="width: 100%">
            <el-option
              v-for="t in typeList"
              :key="t.typeValue"
              :label="t.typeLabel"
              :value="t.typeValue"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="板块备注（选填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </aside>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { addBk, updateBk, deleteBk, getBkTypeList } from '../api/watchlist'

const props = defineProps({
  bkList: { type: Array, default: () => [] },
  selectedId: { type: Number, default: null },
})

const emit = defineEmits(['select', 'refresh', 'deleted'])

// 动态板块类型列表
const typeList = ref([])

const loadTypeList = async () => {
  const res = await getBkTypeList()
  typeList.value = res.data
}

const getTypeLabel = (typeValue) => {
  const found = typeList.value.find(t => t.typeValue === typeValue)
  return found ? found.typeLabel : '未知'
}

// 弹窗
const dialogVisible = ref(false)
const dialogMode = ref('add')
const submitting = ref(false)
const formRef = ref()
const form = ref({ bkName: '', type: null, remark: '' })
const rules = {
  bkName: [{ required: true, message: '请输入板块名称', trigger: 'blur' }],
  type: [{ required: true, message: '请选择板块类型', trigger: 'change' }],
}

const openAddDialog = () => {
  dialogMode.value = 'add'
  loadTypeList()  // 打开弹窗时刷新类型列表
  dialogVisible.value = true
}

const openEditDialog = (bk) => {
  dialogMode.value = 'edit'
  loadTypeList()  // 打开弹窗时刷新类型列表
  form.value = { id: bk.id, bkName: bk.bkName, type: bk.type, remark: bk.remark }
  dialogVisible.value = true
}

const resetForm = () => {
  form.value = { bkName: '', type: null, remark: '' }
  formRef.value?.resetFields()
}

const submitForm = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (dialogMode.value === 'add') {
      await addBk(form.value)
      ElMessage.success('新增成功')
    } else {
      await updateBk(form.value)
      ElMessage.success('修改成功')
    }
    dialogVisible.value = false
    emit('refresh')
  } finally {
    submitting.value = false
  }
}

const handleDelete = async (id) => {
  await deleteBk(id)
  ElMessage.success('删除成功')
  emit('deleted', id)
  emit('refresh')
}

onMounted(() => {
  loadTypeList()
})
</script>

<style scoped>
.bk-panel {
  width: 260px;
  flex-shrink: 0;
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

.bk-list {
  flex: 1;
  overflow-y: auto;
  padding: 8px 0;
}

.bk-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 14px;
  cursor: pointer;
  transition: background 0.15s;
  border-left: 3px solid transparent;
}

.bk-item:hover {
  background: #f5f7fa;
}

.bk-item.active {
  background: #ecf5ff;
  border-left-color: #409eff;
}

.bk-item-left {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  min-width: 0;
}

.bk-id-tag {
  font-size: 11px;
  color: #fff;
  background: #909399;
  border-radius: 4px;
  padding: 1px 5px;
  flex-shrink: 0;
}

.bk-item.active .bk-id-tag {
  background: #409eff;
}

.bk-info {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.bk-name {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.bk-type {
  font-size: 11px;
  color: #999;
  margin-top: 2px;
}

.bk-item-actions {
  display: flex;
  gap: 2px;
  opacity: 0;
  transition: opacity 0.15s;
}

.bk-item:hover .bk-item-actions {
  opacity: 1;
}

.empty-tip {
  text-align: center;
  color: #bbb;
  font-size: 13px;
  padding: 40px 0;
}
</style>
