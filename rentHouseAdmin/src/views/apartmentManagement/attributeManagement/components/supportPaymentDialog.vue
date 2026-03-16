<template>
  <el-dialog
    v-model="dialogFormVisible"
    class="attribute-dialog"
    :destroy-on-close="true"
    :title="title"
    style="max-width: 660px"
  >
    <div class="dialog-shell">
      <div class="dialog-tip">
        支付方式会用于租约签订流程，名称、付租周期和说明建议与业务规则保持一致。
      </div>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-position="top"
        class="dialog-form"
      >
        <el-form-item label="支付方式名称" prop="name">
          <el-input
            v-model.trim="formData.name"
            autocomplete="off"
            placeholder="请输入支付方式名称，例如 月付"
          />
        </el-form-item>

        <el-form-item label="每次支付月数" prop="payMonthCount">
          <el-input-number
            v-model="formData.payMonthCount"
            :min="1"
            :step="1"
            step-strictly
            controls-position="right"
            class="number-input"
          />
        </el-form-item>

        <el-form-item label="支付方式说明" prop="additionalInfo">
          <el-input
            v-model.trim="formData.additionalInfo"
            autocomplete="off"
            placeholder="请输入支付说明，例如 押一付一"
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
import { PaymentInfoInterface } from '@/api/apartmentManagement/types'
import { saveOrUpdatePayment } from '@/api/apartmentManagement'

const props = defineProps({
  update: {
    type: Function,
    default: () => ({}),
  },
})

const defaultFormData = {
  id: '',
  name: '',
  payMonthCount: 1,
  additionalInfo: '',
}

const formRef = ref<FormInstance>()
const dialogFormVisible = ref(false)
const formData = ref<PaymentInfoInterface>({
  ...defaultFormData,
})

const rules = ref<FormRules>({
  name: [{ required: true, message: '请输入支付方式名称', trigger: 'blur' }],
  payMonthCount: [{ required: true, message: '请输入每次支付月数', trigger: 'change' }],
  additionalInfo: [{ required: true, message: '请输入支付方式说明', trigger: 'blur' }],
})

const title = computed(() => {
  return `${formData.value.id ? '修改' : '新增'}支付方式`
})

const show = (data: Partial<PaymentInfoInterface> = defaultFormData) => {
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

    await saveOrUpdatePayment(formData.value)
    await props.update()
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

  :deep(.el-input__wrapper),
  :deep(.el-input-number),
  :deep(.el-input-number .el-input__wrapper) {
    min-height: 42px;
    border-radius: 12px;
  }

  :deep(.el-input__wrapper),
  :deep(.el-input-number .el-input__wrapper) {
    background: #f8fafc;
    box-shadow: 0 0 0 1px #dbe3ee inset;
  }
}

.number-input {
  width: 100%;
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
