<template>
  <van-skeleton :row="16" :loading="!appointmentList">
    <div class="appointment-page">
      <template v-if="appointmentList?.length">
        <div
          v-for="item in appointmentList"
          :key="item.id"
          class="appointment-card"
          @click="goAppointmentDetail(item)"
        >
          <div class="appointment-card__thumb">
            <van-image
              class="appointment-card__image"
              fit="cover"
              :src="resolveImageUrl(item.graphVoList?.[0]?.url)"
            >
              <template #error>
                <div class="appointment-card__image-fallback">暂无图片</div>
              </template>
              <template #loading>
                <div class="appointment-card__image-loading">
                  <van-loading type="spinner" size="20" color="#476c58" />
                </div>
              </template>
            </van-image>
            <span class="appointment-card__thumb-badge">预约单</span>
          </div>

          <div class="appointment-card__content">
            <div class="appointment-card__head">
              <h2 class="appointment-card__title">{{ item.apartmentName }}</h2>
              <span
                class="appointment-card__status"
                :class="{
                  'appointment-card__status--waiting':
                    item.appointmentStatus === AppointmentStatus.WAITING
                }"
              >
                {{ getLabelByValue(AppointmentStatusMap, item.appointmentStatus) }}
              </span>
            </div>

            <div class="appointment-card__section">
              <span class="appointment-card__label">预约时间</span>
              <span class="appointment-card__value">
                {{ item.appointmentTime }}
              </span>
            </div>

            <div class="appointment-card__footer">
              <span class="appointment-card__hint">点击查看预约详情</span>
              <van-icon name="arrow" size="14" color="#476c58" />
            </div>
          </div>
        </div>
      </template>

      <van-empty
        v-else
        class="appointment-page__empty"
        description="暂无预约记录"
      />
    </div>
  </van-skeleton>
</template>

<script setup lang="ts">
import { getMyAppointmentList } from "@/api/search";
import type { AppointmentItemInterface } from "@/api/search/types";
import {
  AppointmentStatus,
  AppointmentStatusMap,
  getLabelByValue
} from "@/enums/constEnums";
import { resolveImageUrl } from "@/utils/image";
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();
const appointmentList = ref<AppointmentItemInterface[]>();

const getAppointmentListHandle = async () => {
  const { data } = await getMyAppointmentList();
  appointmentList.value = data;
};

const goAppointmentDetail = (item: AppointmentItemInterface) => {
  router.push({
    path: "/appointment",
    query: { appointmentId: item.id }
  });
};

onMounted(async () => {
  await getAppointmentListHandle();
});
</script>

<style scoped lang="less">
.appointment-page {
  --appointment-primary: #204736;
  --appointment-primary-soft: #476c58;
  --appointment-accent: #b8874d;
  --appointment-ink: #1a2c22;
  --appointment-muted: #6d7b73;
  --appointment-border: rgba(32, 71, 54, 0.1);
  --appointment-surface: rgba(255, 252, 246, 0.96);
  --appointment-bg: #f4efe6;

  min-height: 100vh;
  padding: 16px;
  padding-bottom: calc(24px + env(safe-area-inset-bottom));
  background:
    radial-gradient(circle at top, rgba(184, 135, 77, 0.12), transparent 28%),
    linear-gradient(180deg, #f8f3ea 0%, var(--appointment-bg) 100%);
}

.appointment-card {
  display: grid;
  grid-template-columns: 112px minmax(0, 1fr);
  gap: 14px;
  margin-bottom: 16px;
  padding: 14px;
  border: 1px solid var(--appointment-border);
  border-radius: 22px;
  background: var(--appointment-surface);
  box-shadow:
    0 1px 2px rgba(35, 47, 36, 0.04),
    0 14px 32px rgba(35, 47, 36, 0.06);
}

.appointment-card__thumb {
  position: relative;
  height: 144px;
  border-radius: 16px;
  overflow: hidden;
  background: #efe5d2;
}

.appointment-card__image,
.appointment-card__image :deep(img) {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.appointment-card__image-fallback,
.appointment-card__image-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #f1e8d8, #f8f3ea);
  color: var(--appointment-primary-soft);
  font-size: 12px;
  font-weight: 500;
}

.appointment-card__thumb-badge {
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

.appointment-card__content {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-width: 0;
}

.appointment-card__head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 10px;
}

.appointment-card__title {
  margin: 0;
  color: var(--appointment-ink);
  font-size: 17px;
  line-height: 1.4;
}

.appointment-card__status {
  flex-shrink: 0;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(109, 123, 115, 0.12);
  color: var(--appointment-muted);
  font-size: 11px;
  font-weight: 700;
}

.appointment-card__status--waiting {
  background: rgba(32, 71, 54, 0.08);
  color: var(--appointment-primary-soft);
}

.appointment-card__section {
  display: grid;
  gap: 6px;
  margin-top: 14px;
  padding: 12px 14px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.54);
}

.appointment-card__label {
  color: var(--appointment-primary-soft);
  font-size: 12px;
  font-weight: 700;
}

.appointment-card__value {
  color: var(--appointment-ink);
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}

.appointment-card__footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 14px;
}

.appointment-card__hint {
  color: var(--appointment-accent);
  font-size: 12px;
  font-weight: 600;
}

.appointment-page__empty {
  margin-top: 64px;
}

@media (max-width: 375px) {
  .appointment-page {
    padding: 12px;
  }

  .appointment-card {
    grid-template-columns: 98px minmax(0, 1fr);
    gap: 12px;
    padding: 12px;
  }

  .appointment-card__thumb {
    height: 132px;
  }

  .appointment-card__title {
    font-size: 16px;
  }
}
</style>
