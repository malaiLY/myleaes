<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <div>
          <p class="card-title">标签信息管理</p>
        </div>
      </div>
    </template>

    <section
      v-for="item in labelTypeList"
      :key="item.value"
      class="label-section"
    >
      <div class="section-meta">
        <div>
          <p class="section-title">{{ item.label }}标签</p>
          <p class="section-subtitle">当前共 {{ item.children?.length || 0 }} 个标签</p>
        </div>
        <span class="section-count">{{ item.children?.length || 0 }}</span>
      </div>

      <div class="label-flow">
        <el-popconfirm
          v-for="item2 in item.children || []"
          :key="item2.id"
          width="220"
          confirm-button-text="删除"
          cancel-button-text="修改"
          cancel-button-type="warning"
          :title="`修改或删除 ${item2.name}`"
          @confirm="deleteLabelHandle(item2)"
          @cancel="editLabelHandle(item2)"
        >
          <template #reference>
            <button type="button" class="label-chip">
              <span>{{ item2.name }}</span>
            </button>
          </template>
        </el-popconfirm>

        <button
          type="button"
          class="label-chip label-chip-add"
          @click="addLabelHandle(item)"
        >
          <el-icon :size="16">
            <CirclePlus />
          </el-icon>
          <span>新增标签</span>
        </button>
      </div>
    </section>

    <SupportLabelDialog
      ref="supportLabelDialog"
      :updateLabel="getLabelInfoListHandle"
    />
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { LabelInfoInterface } from '@/api/apartmentManagement/types'
import {
  deleteLabelInfoById,
  getLabelInfoList,
} from '@/api/apartmentManagement'
import { BuildingTypeTypeMap } from '@/enums/constEnums'
import SupportLabelDialog from '@/views/apartmentManagement/attributeManagement/components/supportLabelDialog.vue'

type SupportLabelInterface = {
  label: string
  value: string
  children?: LabelInfoInterface[]
}

const supportLabelDialog = ref<InstanceType<typeof SupportLabelDialog>>()
const labelTypeList = ref<SupportLabelInterface[]>([])

async function getLabelInfoListHandle() {
  try {
    const { data } = await getLabelInfoList()
    labelTypeList.value = JSON.parse(JSON.stringify(BuildingTypeTypeMap))
    data.forEach((item) => {
      const targetType = labelTypeList.value.find(
        (item2) => item2.value === item.type,
      )
      if (targetType) {
        targetType.children = targetType.children || []
        targetType.children.push(item)
      }
    })
  } catch (error) {
    console.log(error)
  }
}

const deleteLabelHandle = async (item: LabelInfoInterface) => {
  try {
    await deleteLabelInfoById(item.id)
    await getLabelInfoListHandle()
    ElMessage.success('操作成功')
  } catch (error) {
    console.log(error)
  }
}

const editLabelHandle = (item: LabelInfoInterface) => {
  supportLabelDialog.value?.show(item)
}

const addLabelHandle = (item: SupportLabelInterface) => {
  supportLabelDialog.value?.show({ type: item.value })
}

onMounted(() => {
  getLabelInfoListHandle()
})
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
}

.label-section + .label-section {
  margin-top: 18px;
}

.section-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 10px;
}

.section-title {
  margin: 0;
  font-size: 15px;
  font-weight: 700;
  color: #0f172a;
}

.section-subtitle {
  margin: 6px 0 0;
  font-size: 12px;
  color: #64748b;
}

.section-count {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 32px;
  height: 32px;
  padding: 0 10px;
  font-size: 12px;
  font-weight: 700;
  color: #2563eb;
  background: linear-gradient(180deg, #eff6ff, #dbeafe);
  border: 1px solid #bfdbfe;
  border-radius: 999px;
}

.label-flow {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 12px;
  background: linear-gradient(180deg, #f8fafc, #f1f5f9);
  border: 1px solid #e2e8f0;
  border-radius: 18px;
}

.label-chip {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  height: 40px;
  padding: 0 16px;
  font: inherit;
  color: #2563eb;
  cursor: pointer;
  appearance: none;
  background: linear-gradient(180deg, #eff6ff, #e0ecff);
  border: 1px solid #bfdbfe;
  border-radius: 12px;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease,
    border-color 0.2s ease,
    color 0.2s ease;
}

.label-chip:hover {
  color: #1d4ed8;
  border-color: #93c5fd;
  box-shadow: 0 10px 18px rgb(59 130 246 / 0.12);
  transform: translateY(-1px);
}

.label-chip-add {
  color: #1e40af;
  background: linear-gradient(180deg, #f8fbff, #eff6ff);
  border-style: dashed;
}

@media (max-width: 768px) {
  .label-flow {
    gap: 8px;
  }

  .label-chip {
    height: 36px;
    padding: 0 14px;
  }
}
</style>
