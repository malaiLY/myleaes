<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <div>
          <p class="card-title">支付方式管理</p>
        </div>
      </div>
    </template>

    <section class="payment-section">
      <div class="section-meta">
        <div>
          <p class="section-title">支付方式</p>
          <p class="section-subtitle">点击标签可修改或删除对应的支付规则</p>
        </div>
        <span class="section-count">{{ paymentTypeList.length }}</span>
      </div>

      <div class="payment-flow">
        <el-popconfirm
          v-for="item in paymentTypeList"
          :key="item.id"
          width="220"
          confirm-button-text="删除"
          cancel-button-text="修改"
          cancel-button-type="warning"
          :title="`修改或删除 ${item.name}(${item.additionalInfo})`"
          @confirm="deleteHandle(item)"
          @cancel="editHandle(item)"
        >
          <template #reference>
            <button type="button" class="payment-chip">
              <span>{{ `${item.name}(${item.additionalInfo})` }}</span>
            </button>
          </template>
        </el-popconfirm>

        <button type="button" class="payment-chip payment-chip-add" @click="addHandle">
          <el-icon :size="16">
            <CirclePlus />
          </el-icon>
          <span>新增方式</span>
        </button>
      </div>
    </section>

    <SupportPaymentDialog
      ref="supportPaymentDialog"
      :update="getPaymentInfoListHandle"
    />
  </el-card>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { PaymentInfoInterface } from '@/api/apartmentManagement/types'
import { deletePaymentById, getPaymentList } from '@/api/apartmentManagement'
import SupportPaymentDialog from '@/views/apartmentManagement/attributeManagement/components/SupportPaymentDialog.vue'

const supportPaymentDialog = ref<InstanceType<typeof SupportPaymentDialog>>()
const paymentTypeList = ref<PaymentInfoInterface[]>([])

async function getPaymentInfoListHandle() {
  try {
    const { data } = await getPaymentList()
    paymentTypeList.value = data
  } catch (error) {
    console.log(error)
  }
}

const deleteHandle = async (item: PaymentInfoInterface) => {
  try {
    await deletePaymentById(item.id)
    await getPaymentInfoListHandle()
    ElMessage.success('操作成功')
  } catch (error) {
    console.log(error)
  }
}

const editHandle = (item: PaymentInfoInterface) => {
  supportPaymentDialog.value?.show(item)
}

const addHandle = () => {
  supportPaymentDialog.value?.show()
}

onMounted(() => {
  getPaymentInfoListHandle()
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

.payment-flow {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  padding: 12px;
  background: linear-gradient(180deg, #f8fafc, #f1f5f9);
  border: 1px solid #e2e8f0;
  border-radius: 18px;
}

.payment-chip {
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

.payment-chip:hover {
  color: #1d4ed8;
  border-color: #93c5fd;
  box-shadow: 0 10px 18px rgb(59 130 246 / 0.12);
  transform: translateY(-1px);
}

.payment-chip-add {
  color: #1e40af;
  background: linear-gradient(180deg, #f8fbff, #eff6ff);
  border-style: dashed;
}

@media (max-width: 768px) {
  .card-header {
    flex-direction: column;
  }

  .payment-flow {
    gap: 8px;
  }

  .payment-chip {
    height: 36px;
    padding: 0 14px;
  }
}
</style>
