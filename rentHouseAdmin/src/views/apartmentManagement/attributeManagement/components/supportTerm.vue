<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <div>
          <p class="card-title">租期管理</p>
        </div>
      </div>
    </template>

    <section class="term-section">
      <div class="section-meta">
        <div>
          <p class="section-title">租期选项</p>
          <p class="section-subtitle">点击标签可修改或删除租期配置</p>
        </div>
        <span class="section-count">{{ termTypeList.length }}</span>
      </div>

      <div class="term-flow">
        <el-popconfirm
          v-for="item in termTypeList"
          :key="item.id"
          width="220"
          confirm-button-text="删除"
          cancel-button-text="修改"
          cancel-button-type="warning"
          :title="`修改或删除 ${item.monthCount + item.unit}`"
          @confirm="deleteTermHandle(item)"
          @cancel="editTermHandle(item)"
        >
          <template #reference>
            <button type="button" class="term-chip">
              <span>{{ item.monthCount + item.unit }}</span>
            </button>
          </template>
        </el-popconfirm>

        <button type="button" class="term-chip term-chip-add" @click="addTermHandle">
          <el-icon :size="16">
            <CirclePlus />
          </el-icon>
          <span>新增租期</span>
        </button>
      </div>
    </section>

    <SupportTermDialog
      ref="supportTermDialog"
      :updateTerm="getTermInfoListHandle"
    />
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { TermInfoInterface } from '@/api/apartmentManagement/types'
import { deleteTermById, getTermList } from '@/api/apartmentManagement'
import SupportTermDialog from '@/views/apartmentManagement/attributeManagement/components/SupportTermDialog.vue'

const supportTermDialog = ref<InstanceType<typeof SupportTermDialog>>()
const termTypeList = ref<TermInfoInterface[]>([])

async function getTermInfoListHandle() {
  try {
    const { data } = await getTermList()
    termTypeList.value = data
  } catch (error) {
    console.log(error)
  }
}

const deleteTermHandle = async (item: TermInfoInterface) => {
  try {
    await deleteTermById(item.id)
    await getTermInfoListHandle()
    ElMessage.success('操作成功')
  } catch (error) {
    console.log(error)
  }
}

const editTermHandle = (item: TermInfoInterface) => {
  supportTermDialog.value?.show(item)
}

const addTermHandle = () => {
  supportTermDialog.value?.show()
}

onMounted(() => {
  getTermInfoListHandle()
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
  margin: 4px 0 0;
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

.term-flow {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 12px;
  background: linear-gradient(180deg, #f8fafc, #f1f5f9);
  border: 1px solid #e2e8f0;
  border-radius: 18px;
}

.term-chip {
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

.term-chip:hover {
  color: #1d4ed8;
  border-color: #93c5fd;
  box-shadow: 0 10px 18px rgb(59 130 246 / 0.12);
  transform: translateY(-1px);
}

.term-chip-add {
  color: #1e40af;
  background: linear-gradient(180deg, #f8fbff, #eff6ff);
  border-style: dashed;
}

@media (max-width: 768px) {
  .card-header {
    flex-direction: column;
  }

  .term-flow {
    gap: 8px;
  }

  .term-chip {
    height: 36px;
    padding: 0 14px;
  }
}
</style>
