<template>
  <el-dialog
    v-model="dialogFormVisible"
    class="attribute-dialog"
    :destroy-on-close="true"
    :title="title"
    style="max-width: 720px"
  >
    <div class="dialog-shell">
      <div class="dialog-tip">
        维护配套名称和图标，列表页会直接使用这里的配置结果进行展示。
      </div>

      <el-form
        ref="formRef"
        :model="formData"
        :rules="rules"
        label-position="top"
        class="dialog-form"
      >
        <el-form-item label="配套名称" prop="name">
          <el-input
            v-model.trim="formData.name"
            autocomplete="off"
            placeholder="请输入配套名称"
          />
        </el-form-item>

        <el-form-item label="配套图标" prop="icon">
          <div class="icon-field">
            <el-select
              v-model="formData.icon"
              placeholder="请选择配套图标"
              clearable
              filterable
              class="icon-select"
            >
              <el-option
                v-for="item in icons"
                :key="item"
                :value="item"
                class="icon-option"
              >
                <SvgIcon size="20" color="black" :name="`rent-${item}`" />
                <span>{{ item }}</span>
              </el-option>
            </el-select>

            <div v-if="formData.icon" class="icon-preview">
              <SvgIcon size="22" color="black" :name="`rent-${formData.icon}`" />
              <span>{{ formData.icon }}</span>
            </div>
          </div>
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
import ids from 'virtual:svg-icons-names'
import { computed, ref } from 'vue'
import { ElMessage, FormInstance, FormRules } from 'element-plus'
import { FacilityInfoInterface } from '@/api/apartmentManagement/types'
import { BuildingTypeTypeMap, getLabelByValue } from '@/enums/constEnums'
import { saveOrUpdateFacilityInfo } from '@/api/apartmentManagement'

const props = defineProps({
  updateFacility: {
    type: Function,
    default: () => ({}),
  },
})

const defaultFormData = {
  id: '',
  type: '',
  name: '',
  icon: '',
}

const formRef = ref<FormInstance>()
const dialogFormVisible = ref(false)
const formData = ref<FacilityInfoInterface>({
  ...defaultFormData,
})

const rules = ref<FormRules>({
  name: [{ required: true, message: '请输入配套名称', trigger: 'blur' }],
  icon: [{ required: true, message: '请选择配套图标', trigger: 'change' }],
})

const title = computed(() => {
  return `${formData.value.id ? '修改' : '新增'}${getLabelByValue(BuildingTypeTypeMap, formData.value.type)}配套`
})

const icons = ids
  .filter((id) => id.startsWith('icon-rent-'))
  .map((id) => id.replace('icon-rent-', ''))

const show = (data: Partial<FacilityInfoInterface> = defaultFormData) => {
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

    await saveOrUpdateFacilityInfo(formData.value)
    await props.updateFacility()
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
  :deep(.el-select__wrapper),
  :deep(.el-textarea__inner) {
    min-height: 42px;
    background: #f8fafc;
    border-radius: 12px;
    box-shadow: 0 0 0 1px #dbe3ee inset;
  }
}

.icon-field {
  display: flex;
  align-items: center;
  gap: 12px;
}

.icon-select {
  flex: 1;
}

.icon-option {
  display: flex;
  align-items: center;
  gap: 10px;
}

.icon-preview {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-width: 120px;
  height: 42px;
  padding: 0 14px;
  color: #0f172a;
  background: linear-gradient(180deg, #fff, #f8fafc);
  border: 1px solid #dbe3ee;
  border-radius: 12px;
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

@media (max-width: 768px) {
  .icon-field {
    flex-direction: column;
    align-items: stretch;
  }

  .icon-preview {
    justify-content: center;
  }
}
</style>
