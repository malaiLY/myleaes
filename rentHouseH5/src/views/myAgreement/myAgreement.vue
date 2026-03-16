<template>
  <van-skeleton :row="16" :loading="!agreementList">
    <div class="agreement-page">
      <template v-if="agreementList?.length">
        <div
          v-for="item in agreementList"
          :key="item.id"
          class="agreement-card"
          @click="goAgreementDetail(item)"
        >
          <div class="agreement-card__thumb">
            <van-image
              class="agreement-card__image"
              fit="cover"
              :src="resolveImageUrl(item.roomGraphVoList?.[0]?.url)"
            >
              <template #error>
                <div class="agreement-card__image-fallback">暂无图片</div>
              </template>
              <template #loading>
                <div class="agreement-card__image-loading">
                  <van-loading type="spinner" size="20" color="#476c58" />
                </div>
              </template>
            </van-image>
            <span class="agreement-card__thumb-badge">
              {{ getLabelByValue(AgreementSourceMap, item.sourceType) }}
            </span>
          </div>

          <div class="agreement-card__content">
            <div class="agreement-card__head">
              <h2 class="agreement-card__title">
                {{ `${item.apartmentName} ${item.roomNumber}房间` }}
              </h2>
              <span
                class="agreement-card__status"
                :class="statusClassName(item.leaseStatus)"
              >
                {{ getLabelByValue(AgreementStatusMap, item.leaseStatus) }}
              </span>
            </div>

            <div class="agreement-card__period">
              <span class="agreement-card__period-label">租约周期</span>
              <span class="agreement-card__period-value">
                {{ `${item.leaseStartDate} 至 ${item.leaseEndDate}` }}
              </span>
            </div>

            <div class="agreement-card__footer">
              <div class="agreement-card__price">
                <span class="agreement-card__price-mark">￥</span>
                <span class="agreement-card__price-value">{{ item.rent }}</span>
                <span class="agreement-card__price-unit">/月</span>
              </div>

              <div class="agreement-card__actions" @click.stop>
                <van-button
                  v-if="item.leaseStatus === AgreementStatus.SIGNED"
                  size="mini"
                  plain
                  type="primary"
                  @click="
                    goAgreementDetail(item, {
                      isEdit: true,
                      isRenew: true,
                      isAdd: true
                    })
                  "
                >
                  续约
                </van-button>
                <van-button
                  v-if="item.leaseStatus === AgreementStatus.RENEW_TO_BE_CONFIRMED"
                  size="mini"
                  type="primary"
                  plain
                  @click="
                    goAgreementDetail(item, {
                      isRenew: true,
                      isEdit: true,
                      isAdd: false
                    })
                  "
                >
                  修改
                </van-button>
                <van-button
                  v-if="item.leaseStatus === AgreementStatus.WAITING"
                  size="mini"
                  type="primary"
                  plain
                  @click="goAgreementDetail(item, { isEdit: true, isConfirm: true })"
                >
                  确认
                </van-button>
                <van-button
                  v-if="item.leaseStatus === AgreementStatus.SIGNED"
                  size="mini"
                  plain
                  type="danger"
                  @click="rentRefundHandle(item)"
                >
                  提前退租
                </van-button>
              </div>
            </div>
          </div>
        </div>
      </template>

      <van-empty
        v-else
        class="agreement-page__empty"
        description="暂无租约记录"
      />
    </div>
  </van-skeleton>
</template>

<script setup lang="ts">
import { getMyAgreementList, saveOrUpdateAgreement } from "@/api/search";
import type { AgreementItemInterface } from "@/api/search/types";
import {
  AgreementSourceMap,
  AgreementStatus,
  AgreementStatusMap,
  getLabelByValue
} from "@/enums/constEnums";
import { resolveImageUrl } from "@/utils/image";
import { showConfirmDialog, showToast } from "vant";
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const agreementList = ref<AgreementItemInterface[]>();

const getAgreementListHandle = async () => {
  const { data } = await getMyAgreementList();
  agreementList.value = data;
};

const goAgreementDetail = (item: AgreementItemInterface, query: object = {}) => {
  router.push({
    path: "/agreement",
    query: { id: item.id, ...query }
  });
};

const rentRefundHandle = (item: AgreementItemInterface) => {
  showConfirmDialog({
    title: "提前退租",
    message: "确定要提前退租吗？",
    cancelButtonText: "取消",
    confirmButtonText: "确定"
  })
    .then(async () => {
      await saveOrUpdateAgreement({
        id: item.id,
        status: AgreementStatus.TO_BE_CONFIRMED
      });

      showToast({
        type: "success",
        message: "操作成功"
      });

      item.leaseStatus = AgreementStatus.TO_BE_CONFIRMED;
    })
    .catch(() => {});
};

const statusClassName = (status: number | string) => {
  if (
    status === AgreementStatus.WAITING ||
    status === AgreementStatus.RENEW_TO_BE_CONFIRMED
  ) {
    return "agreement-card__status--primary";
  }

  if (status === AgreementStatus.SIGNED) {
    return "agreement-card__status--success";
  }

  if (status === AgreementStatus.TO_BE_CONFIRMED) {
    return "agreement-card__status--danger";
  }

  return "agreement-card__status--default";
};

onMounted(async () => {
  await getAgreementListHandle();
});
</script>

<style scoped lang="less">
.agreement-page {
  --agreement-primary: #204736;
  --agreement-primary-soft: #476c58;
  --agreement-accent: #b8874d;
  --agreement-ink: #1a2c22;
  --agreement-muted: #6d7b73;
  --agreement-border: rgba(32, 71, 54, 0.1);
  --agreement-surface: rgba(255, 252, 246, 0.96);
  --agreement-bg: #f4efe6;

  min-height: 100vh;
  padding: 16px;
  padding-bottom: calc(24px + env(safe-area-inset-bottom));
  background:
    radial-gradient(circle at top, rgba(184, 135, 77, 0.12), transparent 28%),
    linear-gradient(180deg, #f8f3ea 0%, var(--agreement-bg) 100%);
}

.agreement-card {
  display: grid;
  grid-template-columns: 112px minmax(0, 1fr);
  gap: 14px;
  margin-bottom: 16px;
  padding: 14px;
  border: 1px solid var(--agreement-border);
  border-radius: 22px;
  background: var(--agreement-surface);
  box-shadow:
    0 1px 2px rgba(35, 47, 36, 0.04),
    0 14px 32px rgba(35, 47, 36, 0.06);
}

.agreement-card__thumb {
  position: relative;
  height: 144px;
  border-radius: 16px;
  overflow: hidden;
  background: #efe5d2;
}

.agreement-card__image,
.agreement-card__image :deep(img) {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.agreement-card__image-fallback,
.agreement-card__image-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #f1e8d8, #f8f3ea);
  color: var(--agreement-primary-soft);
  font-size: 12px;
  font-weight: 500;
}

.agreement-card__thumb-badge {
  position: absolute;
  left: 8px;
  bottom: 8px;
  padding: 4px 8px;
  border-radius: 999px;
  background: rgba(26, 44, 34, 0.66);
  color: rgba(255, 252, 246, 0.94);
  font-size: 10px;
  font-weight: 700;
}

.agreement-card__content {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-width: 0;
}

.agreement-card__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 10px;
}

.agreement-card__title {
  margin: 0;
  color: var(--agreement-ink);
  font-size: 17px;
  line-height: 1.4;
}

.agreement-card__status {
  flex-shrink: 0;
  padding: 4px 10px;
  border-radius: 999px;
  font-size: 11px;
  font-weight: 700;
}

.agreement-card__status--primary {
  background: rgba(32, 71, 54, 0.08);
  color: var(--agreement-primary-soft);
}

.agreement-card__status--success {
  background: rgba(44, 132, 77, 0.12);
  color: #2c844d;
}

.agreement-card__status--danger {
  background: rgba(219, 73, 51, 0.1);
  color: #db4933;
}

.agreement-card__status--default {
  background: rgba(109, 123, 115, 0.12);
  color: var(--agreement-muted);
}

.agreement-card__period {
  display: grid;
  gap: 6px;
  margin-top: 14px;
  padding: 12px 14px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.54);
}

.agreement-card__period-label {
  color: var(--agreement-primary-soft);
  font-size: 12px;
  font-weight: 700;
}

.agreement-card__period-value {
  color: var(--agreement-ink);
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}

.agreement-card__footer {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 12px;
  margin-top: 14px;
}

.agreement-card__price {
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.agreement-card__price-mark {
  color: var(--agreement-accent);
  font-size: 13px;
  font-weight: 700;
}

.agreement-card__price-value {
  color: var(--agreement-primary);
  font-size: 26px;
  font-weight: 800;
  line-height: 1;
}

.agreement-card__price-unit {
  color: rgba(109, 123, 115, 0.82);
  font-size: 12px;
}

.agreement-card__actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 8px;
}

.agreement-page__empty {
  margin-top: 64px;
}

@media (max-width: 375px) {
  .agreement-page {
    padding: 12px;
  }

  .agreement-card {
    grid-template-columns: 98px minmax(0, 1fr);
    gap: 12px;
    padding: 12px;
  }

  .agreement-card__thumb {
    height: 132px;
  }

  .agreement-card__title {
    font-size: 16px;
  }

  .agreement-card__footer {
    flex-direction: column;
    align-items: stretch;
  }

  .agreement-card__actions {
    justify-content: flex-start;
  }
}
</style>
