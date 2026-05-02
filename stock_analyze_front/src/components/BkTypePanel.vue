<template>
  <div>
    <div class="panel-header">
      <span class="panel-title">板块类型管理</span>
      <el-button type="primary" size="small" @click="openAddDialog">
        <el-icon><Plus /></el-icon> 新增类型
      </el-button>
    </div>

    <el-table :data="typeList" v-loading="loading" size="small">
      <el-table-column prop="typeValue" label="类型值" width="90" />
      <el-table-column prop="typeLabel" label="类型名称" min-width="120" />
      <el-table-column prop="sort" label="排序" width="80" />
      <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button link size="small" @click="openEditDialog(row)">编辑</el-button>
          <el-popconfirm
            title="删除后使用该类型的板块将失去类型标签，确认删除？"
            width="260"
            @confirm="handleDelete(row.id)"
          >
            <template #reference>
              <el-button link size="small" type="danger">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogMode === 'add' ? '新增板块类型' : '编辑板块类型'"
      width="420px"
      @close="resetForm"
    >
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="类型名称" prop="typeLabel">
          <el-input v-model="form.typeLabel" placeholder="请输入类型名称" />
        </el-form-item>
        <el-form-item label="类型值" prop="typeValue">
          <el-input-number
            v-model="form.typeValue"
            :min="1"
            :disabled="dialogMode === 'edit'"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" :max="999" style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="备注（选填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import { getBkTypeList, addBkType, updateBkType, deleteBkType } from '../api/watchlist'

const emit = defineEmits(['change'])

const typeList = ref([])
const loading = ref(false)

const loadTypeList = async () => {
  loading.value = true
  try {
    const res = await getBkTypeList()
    typeList.value = res.data
    emit('change', res.data)
  } finally {
    loading.value = false
  }
}

const handleDelete = async (id) => {
  await deleteBkType(id)
  ElMessage.success('删除成功')
  await loadTypeList()
}

// 弹窗
const dialogVisible = ref(false)
const dialogMode = ref('add')
const submitting = ref(false)
const formRef = ref()
const form = ref({ typeLabel: '', typeValue: null, sort: 0, remark: '' })
const rules = {
  typeLabel: [{ required: true, message: '请输入类型名称', trigger: 'blur' }],
  typeValue: [{ required: true, message: '请输入类型值', trigger: 'blur' }],
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
  form.value = { typeLabel: '', typeValue: null, sort: 0, remark: '' }
  formRef.value?.resetFields()
}

const submitForm = async () => {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (dialogMode.value === 'add') {
      await addBkType(form.value)
      ElMessage.success('新增成功')
    } else {
      await updateBkType(form.value)
      ElMessage.success('修改成功')
    }
    dialogVisible.value = false
    await loadTypeList()
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadTypeList()
})

// 暴露给父组件调用
defineExpose({ loadTypeList })
</script>

<style scoped>
.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 0;
  margin-bottom: 8px;
}

.panel-title {
  font-size: 15px;
  font-weight: 600;
  color: #1a1a2e;
}
</style>
