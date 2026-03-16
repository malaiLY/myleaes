<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <div>
          <p class="card-title">杂费信息管理</p>
        </div>
      </div>
    </template>

    <section
      v-for="item in attrTypeList"
      :key="item.id"
      class="fee-section"
    >
      <div class="section-meta">
        <el-popconfirm
          width="220"
          confirm-button-text="删除"
          cancel-button-text="修改"
          cancel-button-type="warning"
          :title="`修改或删除 ${item.name}`"
          @confirm="deleteFeeKeyHandle(item)"
          @cancel="editFeeKeyHandle(item)"
        >
          <template #reference>
            <button type="button" class="fee-title-chip">
              <span>{{ item.name }}</span>
              <el-icon><InfoFilled /></el-icon>
            </button>
          </template>
        </el-popconfirm>

        <span class="section-count">{{ item.feeValueList?.length || 0 }}</span>
      </div>

      <div class="fee-flow">
        <el-popconfirm
          v-for="item2 in item.feeValueList || []"
          :key="item2.id"
          width="220"
          confirm-button-text="删除"
          cancel-button-text="修改"
          cancel-button-type="warning"
          :title="`修改或删除 ${item2.name}`"
          @confirm="deleteFeeValueHandle(item2)"
          @cancel="editFeeValueHandle(item2)"
        >
          <template #reference>
            <button type="button" class="fee-chip">
              <span>{{ item2.name + item2.unit }}</span>
            </button>
          </template>
        </el-popconfirm>

        <button
          type="button"
          class="fee-chip fee-chip-add"
          @click="addFeeValueHandle(item)"
        >
          <el-icon :size="16">
            <CirclePlus />
          </el-icon>
          <span>新增费用值</span>
        </button>
      </div>
    </section>

    <SupportFeeDialog ref="supportFeeDialog" :updateFee="getFeeInfoListHandle" />
    <supportFeeAddOrEditAttrDialog
      ref="supportFeeAddOrEditFeeDialog"
      :updateFeeAddOrEditFee="getFeeInfoListHandle"
    />
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  FeeValueInfoInterface,
  FeeInfoInterface,
} from '@/api/apartmentManagement/types'
import {
  deleteFeeKeyById,
  deleteFeeValueById,
  getFeeInfoList,
} from '@/api/apartmentManagement'
import SupportFeeDialog from '@/views/apartmentManagement/attributeManagement/components/supportFeeDialog.vue'
import SupportFeeAddOrEditAttrDialog from '@/views/apartmentManagement/attributeManagement/components/supportFeeAddOrEditAttrDialog.vue'

const supportFeeDialog = ref<InstanceType<typeof SupportFeeDialog>>()
const supportFeeAddOrEditFeeDialog =
  ref<InstanceType<typeof SupportFeeAddOrEditAttrDialog>>()

const attrTypeList = ref<FeeInfoInterface[]>([])

async function getFeeInfoListHandle() {
  try {
    const { data } = await getFeeInfoList()
    data.forEach((item) => {
      item.feeValueList?.forEach((item2) => {
        item2.feeKeyName = item.name
      })
    })
    attrTypeList.value = data
  } catch (error) {
    console.log(error)
  }
}

const deleteFeeValueHandle = async (item: FeeValueInfoInterface) => {
  try {
    await deleteFeeValueById(item.id)
    await getFeeInfoListHandle()
    ElMessage.success('操作成功')
  } catch (error) {
    console.log(error)
  }
}

const editFeeValueHandle = (item: FeeValueInfoInterface) => {
  supportFeeDialog.value?.show(item)
}

const addFeeValueHandle = (item: FeeInfoInterface) => {
  supportFeeDialog.value?.show({
    feeKeyId: item.id,
    feeKeyName: item.name,
  })
}

const addFeeKeyHandle = () => {
  supportFeeAddOrEditFeeDialog.value?.show()
}

const deleteFeeKeyHandle = async (item: FeeInfoInterface) => {
  try {
    await deleteFeeKeyById(item.id)
    await getFeeInfoListHandle()
    ElMessage.success('操作成功')
  } catch (error) {
    console.log(error)
  }
}

const editFeeKeyHandle = (item: FeeInfoInterface) => {
  supportFeeAddOrEditFeeDialog.value?.show(item)
}

onMounted(() => {
  getFeeInfoListHandle()
})
</script>

<style scoped lang="scss">
.card-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.card-title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
}

.fee-section + .fee-section {
  margin-top: 18px;
}

.section-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 10px;
}

.fee-title-chip {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  height: 38px;
  padding: 0 14px;
  font: inherit;
  font-weight: 700;
  color: #0f172a;
  cursor: pointer;
  appearance: none;
  background: linear-gradient(180deg, #fff, #f8fafc);
  border: 1px solid #dbe3ee;
  border-radius: 12px;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease,
    border-color 0.2s ease;
}

.fee-title-chip:hover {
  border-color: #bfdbfe;
  box-shadow: 0 10px 18px rgb(59 130 246 / 0.1);
  transform: translateY(-1px);
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

.fee-flow {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 12px;
  background: linear-gradient(180deg, #f8fafc, #f1f5f9);
  border: 1px solid #e2e8f0;
  border-radius: 18px;
}

.fee-chip {
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

.fee-chip:hover {
  color: #1d4ed8;
  border-color: #93c5fd;
  box-shadow: 0 10px 18px rgb(59 130 246 / 0.12);
  transform: translateY(-1px);
}

.fee-chip-add {
  color: #1e40af;
  background: linear-gradient(180deg, #f8fbff, #eff6ff);
  border-style: dashed;
}

@media (max-width: 768px) {
  .card-header {
    flex-direction: column;
  }

  .fee-flow {
    gap: 8px;
  }

  .fee-chip,
  .fee-title-chip {
    height: 36px;
    padding: 0 14px;
  }
}
</style>
