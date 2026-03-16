<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <div>
          <p class="card-title">配套信息管理</p>
        </div>
      </div>
    </template>

    <section
      v-for="item in facilityTypeList"
      :key="item.value"
      class="facility-section"
    >
      <div class="section-meta">
        <div>
          <p class="section-title">{{ item.label }}配套</p>
          <p class="section-subtitle">当前共 {{ item.children?.length || 0 }} 项可维护配套</p>
        </div>
        <span class="section-count">{{ item.children?.length || 0 }}</span>
      </div>

      <div class="facility-grid">
        <el-popconfirm
          v-for="item2 in item.children || []"
          :key="item2.id"
          width="220"
          confirm-button-text="删除"
          cancel-button-text="修改"
          cancel-button-type="warning"
          :title="`修改或删除 ${item2.name}`"
          @confirm="deleteFacilityHandle(item2)"
          @cancel="editFacilityHandle(item2)"
        >
          <template #reference>
            <button type="button" class="facility-card">
              <span class="facility-icon">
                <SvgIcon size="28" color="#334155" :name="`rent-${item2.icon}`" />
              </span>
              <span class="facility-name">{{ item2.name }}</span>
            </button>
          </template>
        </el-popconfirm>

        <button
          type="button"
          class="facility-card facility-card-add"
          @click="addFacilityHandle(item)"
        >
          <span class="facility-icon facility-icon-add">
            <el-icon :size="30">
              <CirclePlus />
            </el-icon>
          </span>
          <span class="facility-name">新增配套</span>
        </button>
      </div>
    </section>

    <SupportFacilityDialog
      ref="supportFacilityDialog"
      :updateFacility="getFacilityInfoListHandle"
    />
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { FacilityInfoInterface } from '@/api/apartmentManagement/types'
import {
  deleteFacilityInfoById,
  getFacilityInfoList,
} from '@/api/apartmentManagement'
import { BuildingTypeTypeMap } from '@/enums/constEnums'
import SupportFacilityDialog from '@/views/apartmentManagement/attributeManagement/components/supportFacilityDialog.vue'

type SupportFacilityInterface = {
  label: string
  value: string
  children?: FacilityInfoInterface[]
}

const supportFacilityDialog = ref<InstanceType<typeof SupportFacilityDialog>>()
const facilityTypeList = ref<SupportFacilityInterface[]>([])

async function getFacilityInfoListHandle() {
  try {
    const { data } = await getFacilityInfoList()
    facilityTypeList.value = JSON.parse(JSON.stringify(BuildingTypeTypeMap))
    data.forEach((item) => {
      const targetType = facilityTypeList.value.find(
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

const deleteFacilityHandle = async (item: FacilityInfoInterface) => {
  try {
    await deleteFacilityInfoById(item.id)
    await getFacilityInfoListHandle()
    ElMessage.success('操作成功')
  } catch (error) {
    console.log(error)
  }
}

const editFacilityHandle = (item: FacilityInfoInterface) => {
  supportFacilityDialog.value?.show(item)
}

const addFacilityHandle = (item: SupportFacilityInterface) => {
  supportFacilityDialog.value?.show({ type: item.value })
}

onMounted(() => {
  getFacilityInfoListHandle()
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

.facility-section + .facility-section {
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

.facility-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.facility-card {
  display: flex;
  flex: 0 0 96px;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  min-height: 76px;
  padding: 10px 8px;
  font: inherit;
  text-align: center;
  cursor: pointer;
  appearance: none;
  background: linear-gradient(180deg, #fff, #f8fafc);
  border: 1px solid #e2e8f0;
  border-radius: 14px;
  box-shadow: 0 6px 16px rgb(15 23 42 / 0.04);
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease,
    border-color 0.2s ease;
}

.facility-card:hover {
  transform: translateY(-2px);
  border-color: #bfdbfe;
  box-shadow: 0 12px 24px rgb(59 130 246 / 0.1);
}

.facility-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 34px;
  height: 34px;
  background: linear-gradient(180deg, #f8fafc, #e2e8f0);
  border-radius: 12px;
}

.facility-name {
  font-size: 12px;
  font-weight: 700;
  line-height: 1.3;
  color: #0f172a;
  word-break: break-all;
}

.facility-card-add {
  border-style: dashed;
  border-color: #93c5fd;
  background: linear-gradient(180deg, #f8fbff, #eff6ff);
}

.facility-card-add .facility-name {
  color: #1d4ed8;
}

.facility-icon-add {
  color: #2563eb;
  background: linear-gradient(180deg, #eff6ff, #dbeafe);
}

@media (max-width: 768px) {
  .facility-card {
    flex-basis: calc(50% - 5px);
  }
}
</style>
