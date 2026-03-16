<template>
  <el-dialog
    v-model="dialogFormVisible"
    class="attribute-dialog"
    :destroy-on-close="true"
    :title="title"
    style="max-width: 620px"
  >
    <div class="dialog-shell">
      <div class="dialog-tip">租期会直接出现在租约选择项中，建议统一使用月作为核心计量口径。</div>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-position="top"
        class="dialog-form"
      >
        <el-form-item label="租期数值" prop="monthCount">
          <el-input
            v-model.trim="formData.monthCount"
            autocomplete="off"
            placeholder="请输入租期数值，例如 12"
          />
        </el-form-item>

        <el-form-item label="租期单位" prop="unit">
          <el-input
            v-model.trim="formData.unit"
            autocomplete="off"
            placeholder="请输入租期单位，例如 月"
          />
        </el-form-item>
      </el-form>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="close">取消</el-button>
        <el-button type="primary" @click="submitHandle">确定</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { TermInfoInterface } from '@/api/apartmentManagement/types'
import { saveOrUpdateTerm } from '@/api/apartmentManagement'

const props = defineProps({
  updateTerm: {
    type: Function,
    default: () => ({}),
  },
})

const defaultFormData = {
  id: '',
  unit: '',
  monthCount: '',
}

const formRef = ref<FormInstance>()
const dialogFormVisible = ref(false)
const formData = ref<TermInfoInterface>({
  ...defaultFormData,
})

const rules = ref<FormRules>({
  monthCount: [{ required: true, message: '请输入租期数值', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入租期单位', trigger: 'blur' }],
})

const title = computed(() => {
  return `${formData.value.id ? '修改' : '新增'}租期`
})

const show = (data: Partial<TermInfoInterface> = defaultFormData) => {
  formData.value = Object.assign({}, defaultFormData, data)
  dialogFormVisible.value = true
}

const close = () => {
  dialogFormVisible.value = false
}

const submitHandle = () => {
  formRef.value?.validate(async (valid) => {
    if (!valid) {
      ElMessage.error('表单填写有误，请检查后重试')
      return false
    }

    await saveOrUpdateTerm(formData.value)
    await props.updateTerm()
    ElMessage.success('操作成功')
    close()
  })
}

defineExpose({
  show,
  close,
})
</script>

<style scoped lang="scss">
.dialog-shell {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.dialog-tip {
  padding: 12px 14px;
  font-size: 13px;
  line-height: 1.6;
  color: #475569;
  background: linear-gradient(180deg, #f8fbff, #f1f5f9);
  border: 1px solid #dbeafe;
  border-radius: 14px;
}

.dialog-form {
  :deep(.el-form-item) {
    margin-bottom: 18px;
  }

  :deep(.el-form-item__label) {
    margin-bottom: 8px;
    font-size: 13px;
    font-weight: 700;
    color: #0f172a;
  }

  :deep(.el-input__wrapper) {
    min-height: 42px;
    background: #f8fafc;
    border-radius: 12px;
    box-shadow: 0 0 0 1px #dbe3ee inset;
  }
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

:deep(.attribute-dialog .el-dialog) {
  border-radius: 24px;
}

:deep(.attribute-dialog .el-dialog__header) {
  padding: 22px 24px 10px;
}

:deep(.attribute-dialog .el-dialog__title) {
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
}

:deep(.attribute-dialog .el-dialog__body) {
  padding: 10px 24px 8px;
}

:deep(.attribute-dialog .el-dialog__footer) {
  padding: 8px 24px 24px;
}
</style>
