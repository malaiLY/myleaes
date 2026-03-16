<template>
  <el-dialog
    v-model="dialogFormVisible"
    class="attribute-dialog"
    :destroy-on-close="true"
    :title="title"
    style="max-width: 620px"
  >
    <div class="dialog-shell">
      <div class="dialog-tip">
        属性值会归属到对应属性组下，房间录入时会直接引用这里的可选项。
      </div>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-position="top"
        class="dialog-form"
      >
        <el-form-item label="属性值名称" prop="name">
          <el-input
            v-model.trim="formData.name"
            autocomplete="off"
            placeholder="请输入属性值名称"
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
import { AttrValueInfoInterface } from '@/api/apartmentManagement/types'
import { saveOrUpdateAttrValue } from '@/api/apartmentManagement'

const props = defineProps({
  updateRoomBase: {
    type: Function,
    default: () => ({}),
  },
})

const defaultFormData = {
  id: '',
  attrKeyId: '',
  name: '',
  attrKeyName: '',
}

const formRef = ref<FormInstance>()
const dialogFormVisible = ref(false)
const formData = ref<AttrValueInfoInterface & { attrKeyName: string }>({
  ...defaultFormData,
})

const rules = ref<FormRules>({
  name: [{ required: true, message: '请输入属性值名称', trigger: 'blur' }],
})

const title = computed(() => {
  return `${formData.value.id ? '修改' : '新增'}${formData.value.attrKeyName}属性值`
})

const show = (
  data: Partial<AttrValueInfoInterface & { attrKeyName: string }> = defaultFormData,
) => {
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

    await saveOrUpdateAttrValue(formData.value)
    await props.updateRoomBase()
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
