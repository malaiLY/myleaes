<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <div>
          <p class="card-title">房间基本信息管理</p>
        </div>
      </div>
    </template>

    <section
      v-for="item in attrTypeList"
      :key="item.id"
      class="attr-section"
    >
      <div class="section-meta">
        <el-popconfirm
          width="220"
          confirm-button-text="删除"
          cancel-button-text="修改"
          cancel-button-type="warning"
          :title="`修改或删除 ${item.name}`"
          @confirm="deleteAttrHandle(item)"
          @cancel="editAttrHandle(item)"
        >
          <template #reference>
            <button type="button" class="attr-title-chip">
              <span>{{ item.name }}</span>
              <el-icon><InfoFilled /></el-icon>
            </button>
          </template>
        </el-popconfirm>

        <span class="section-count">{{ item.attrValueList?.length || 0 }}</span>
      </div>

      <div class="attr-flow">
        <el-popconfirm
          v-for="item2 in item.attrValueList || []"
          :key="item2.id"
          width="220"
          confirm-button-text="删除"
          cancel-button-text="修改"
          cancel-button-type="warning"
          :title="`修改或删除 ${item2.name}`"
          @confirm="deleteRoomBaseHandle(item2)"
          @cancel="editRoomBaseHandle(item2)"
        >
          <template #reference>
            <button type="button" class="attr-chip">
              <span>{{ item2.name }}</span>
            </button>
          </template>
        </el-popconfirm>

        <button
          type="button"
          class="attr-chip attr-chip-add"
          @click="addRoomBaseHandle(item)"
        >
          <el-icon :size="16">
            <CirclePlus />
          </el-icon>
          <span>新增属性值</span>
        </button>
      </div>
    </section>

    <SupportRoomBaseDialog
      ref="supportRoomBaseDialog"
      :updateRoomBase="getRoomBaseInfoListHandle"
    />
    <supportRoomBaseAddOrEditAttrDialog
      ref="supportRoomBaseAddOrEditAttrDialog"
      :updateRoomBaseAddOrEditAttr="getRoomBaseInfoListHandle"
    />
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  AttrInfoInfoInterface,
  AttrValueInfoInterface,
} from '@/api/apartmentManagement/types'
import {
  deleteAttrKeyById,
  deleteAttrValueById,
  getAttrInfoList,
} from '@/api/apartmentManagement'
import SupportRoomBaseDialog from '@/views/apartmentManagement/attributeManagement/components/supportRoomBaseDialog.vue'
import SupportRoomBaseAddOrEditAttrDialog from '@/views/apartmentManagement/attributeManagement/components/supportRoomBaseAddOrEditAttrDialog.vue'

const supportRoomBaseDialog = ref<InstanceType<typeof SupportRoomBaseDialog>>()
const supportRoomBaseAddOrEditAttrDialog =
  ref<InstanceType<typeof SupportRoomBaseAddOrEditAttrDialog>>()

const attrTypeList = ref<AttrInfoInfoInterface[]>([])

async function getRoomBaseInfoListHandle() {
  try {
    const { data } = await getAttrInfoList()
    data.forEach((item) => {
      item.attrValueList?.forEach((item2) => {
        item2.attrKeyName = item.name
      })
    })
    attrTypeList.value = data
  } catch (error) {
    console.log(error)
  }
}

const deleteRoomBaseHandle = async (item: AttrValueInfoInterface) => {
  try {
    await deleteAttrValueById(item.id)
    await getRoomBaseInfoListHandle()
    ElMessage.success('操作成功')
  } catch (error) {
    console.log(error)
  }
}

const editRoomBaseHandle = (item: AttrValueInfoInterface) => {
  supportRoomBaseDialog.value?.show(item)
}

const addRoomBaseHandle = (item: AttrInfoInfoInterface) => {
  supportRoomBaseDialog.value?.show({
    attrKeyId: item.id,
    attrKeyName: item.name,
  })
}

const addAttrHandle = () => {
  supportRoomBaseAddOrEditAttrDialog.value?.show()
}

const deleteAttrHandle = async (item: AttrInfoInfoInterface) => {
  try {
    await deleteAttrKeyById(item.id)
    await getRoomBaseInfoListHandle()
    ElMessage.success('操作成功')
  } catch (error) {
    console.log(error)
  }
}

const editAttrHandle = (item: AttrInfoInfoInterface) => {
  supportRoomBaseAddOrEditAttrDialog.value?.show(item)
}

onMounted(() => {
  getRoomBaseInfoListHandle()
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

.attr-section + .attr-section {
  margin-top: 18px;
}

.section-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 10px;
}

.attr-title-chip {
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

.attr-title-chip:hover {
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

.attr-flow {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 12px;
  background: linear-gradient(180deg, #f8fafc, #f1f5f9);
  border: 1px solid #e2e8f0;
  border-radius: 18px;
}

.attr-chip {
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

.attr-chip:hover {
  color: #1d4ed8;
  border-color: #93c5fd;
  box-shadow: 0 10px 18px rgb(59 130 246 / 0.12);
  transform: translateY(-1px);
}

.attr-chip-add {
  color: #1e40af;
  background: linear-gradient(180deg, #f8fbff, #eff6ff);
  border-style: dashed;
}

@media (max-width: 768px) {
  .card-header {
    flex-direction: column;
  }

  .attr-flow {
    gap: 8px;
  }

  .attr-chip,
  .attr-title-chip {
    height: 36px;
    padding: 0 14px;
  }
}
</style>
