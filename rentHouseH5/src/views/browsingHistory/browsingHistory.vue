<template>
  <div class="browsing-history-page">
    <PullDownRefreshContainer
      :request="getHistoryListHandler"
      ref="pullDownRefreshContainerRef"
      class="browsing-history-page__feed"
    >
      <template v-if="roomList?.length">
        <RoomCard
          v-for="item in roomList"
          :key="item.id"
          :data="item"
          class="history-room-card"
        >
          <template #thumb>
            <div class="history-room-card__thumb">
              <van-image
                class="history-room-card__image"
                fit="cover"
                :src="resolveImageUrl(item.graphVoList?.[0]?.url)"
              >
                <template #error>
                  <div class="history-room-card__image-fallback">暂无图片</div>
                </template>
                <template #loading>
                  <div class="history-room-card__image-loading">
                    <van-loading type="spinner" size="20" color="#476c58" />
                  </div>
                </template>
              </van-image>
              <span class="history-room-card__thumb-badge">
                房号 {{ item.roomNumber }}
              </span>
            </div>
          </template>

          <template #title>
            <div class="history-room-card__title-wrap">
              <span class="history-room-card__title">
                {{ `${item.apartmentInfo?.name || ""} ${item.roomNumber}房间` }}
              </span>
              <span class="history-room-card__status">最近浏览</span>
            </div>
          </template>

          <template #desc>
            <div class="history-room-card__desc">
              <p class="history-room-card__location">
                <van-icon name="location-o" size="12" />
                {{
                  `${item.apartmentInfo?.provinceName || ""} ${
                    item.apartmentInfo?.cityName || ""
                  } ${item.apartmentInfo?.districtName || ""}`
                }}
              </p>
              <p class="history-room-card__meta">浏览时间 {{ item.browseTime }}</p>
            </div>
          </template>

          <template #tags>
            <div class="history-room-card__tags">
              <span class="history-room-card__time-chip">
                {{ item.browseTime }}
              </span>
            </div>
          </template>

          <template #price>
            <div class="history-room-card__price-wrap">
              <span class="history-room-card__price-mark">￥</span>
              <span class="history-room-card__price">{{ item.rent }}</span>
              <span class="history-room-card__price-unit">/月</span>
            </div>
          </template>
        </RoomCard>
      </template>

      <div v-else class="history-empty">
        <div class="history-empty__icon">
          <van-icon name="underway-o" size="34" color="#476c58" />
        </div>
        <h3 class="history-empty__title">还没有浏览记录</h3>
        <p class="history-empty__text">去首页看看房源，浏览过的房间会出现在这里。</p>
      </div>
    </PullDownRefreshContainer>
  </div>
</template>

<script setup lang="ts">
import { getHistoryList } from "@/api/search";
import type { RoomInterface } from "@/api/search/types";
import type { ReqPage } from "@/api/types";
import PullDownRefreshContainer from "@/components/PullDownRefreshContainer/PullDownRefreshContainer.vue";
import RoomCard from "@/components/RoomCard/RoomCard.vue";
import { resolveImageUrl } from "@/utils/image";
import { ref } from "vue";

const pullDownRefreshContainerRef =
  ref<InstanceType<typeof PullDownRefreshContainer>>();

type RoomInterfaceWithBrowseTime = RoomInterface & { browseTime: string };

const roomList = ref<RoomInterfaceWithBrowseTime[]>([]);

async function getHistoryListHandler(pageInfo: ReqPage) {
  const { data } = await getHistoryList(pageInfo);

  const targetRecords = data.records.map(item => {
    return {
      id: item.roomId,
      roomNumber: item.roomNumber,
      rent: item.rent,
      graphVoList: item.roomGraphVoList,
      labelInfoList: [],
      apartmentInfo: {
        name: item.apartmentName || "",
        provinceName: item.provinceName || "",
        cityName: item.cityName || "",
        districtName: item.districtName || ""
      },
      browseTime: item.browseTime
    };
  }) as unknown as RoomInterfaceWithBrowseTime[];

  if (pageInfo.current === 1) {
    roomList.value = targetRecords;
  } else {
    roomList.value = [...roomList.value, ...targetRecords];
  }

  pullDownRefreshContainerRef.value?.setFinished(
    roomList.value.length >= data.total
  );
}
</script>

<style scoped lang="less">
.browsing-history-page {
  --history-primary: #204736;
  --history-primary-soft: #476c58;
  --history-accent: #b8874d;
  --history-ink: #1a2c22;
  --history-muted: #6d7b73;
  --history-border: rgba(32, 71, 54, 0.1);
  --history-surface: rgba(255, 252, 246, 0.96);
  --history-bg: #f4efe6;

  min-height: 100%;
  padding: 16px;
  padding-bottom: calc(24px + env(safe-area-inset-bottom));
  background:
    radial-gradient(circle at top, rgba(184, 135, 77, 0.12), transparent 28%),
    linear-gradient(180deg, #f8f3ea 0%, var(--history-bg) 100%);
}

.browsing-history-page__feed {
  min-height: 70vh;
}

.history-room-card {
  margin-bottom: 16px;
}

.history-room-card__thumb {
  position: relative;
  width: 100%;
  height: 100%;
  border-radius: 16px;
  overflow: hidden;
}

.history-room-card__image,
.history-room-card__image :deep(img) {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.history-room-card__image-fallback,
.history-room-card__image-loading {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #f1e8d8, #f8f3ea);
  color: var(--history-primary-soft);
  font-size: 12px;
  font-weight: 500;
}

.history-room-card__thumb-badge {
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

.history-room-card__title-wrap {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 8px;
}

.history-room-card__title {
  color: var(--history-ink);
  font-size: 16px;
  font-weight: 700;
  line-height: 1.4;
}

.history-room-card__status {
  flex-shrink: 0;
  padding: 2px 8px;
  border-radius: 999px;
  background: rgba(32, 71, 54, 0.08);
  color: var(--history-primary-soft);
  font-size: 10px;
  font-weight: 700;
}

.history-room-card__desc {
  display: grid;
  gap: 4px;
}

.history-room-card__location,
.history-room-card__meta {
  margin: 0;
  color: var(--history-muted);
  font-size: 12px;
  line-height: 1.6;
}

.history-room-card__location {
  display: flex;
  align-items: center;
  gap: 4px;
}

.history-room-card__tags {
  margin-top: 8px;
}

.history-room-card__time-chip {
  display: inline-flex;
  align-items: center;
  padding: 5px 10px;
  border-radius: 999px;
  background: rgba(184, 135, 77, 0.1);
  color: #8d6536;
  font-size: 11px;
  font-weight: 600;
}

.history-room-card__price-wrap {
  display: flex;
  align-items: baseline;
  gap: 2px;
  margin-top: 12px;
}

.history-room-card__price-mark {
  color: var(--history-accent);
  font-size: 13px;
  font-weight: 700;
}

.history-room-card__price {
  color: var(--history-primary);
  font-size: 26px;
  font-weight: 800;
  line-height: 1;
}

.history-room-card__price-unit {
  color: rgba(109, 123, 115, 0.82);
  font-size: 12px;
}

.history-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 64px 24px;
  margin-top: 12px;
  border: 1px dashed rgba(32, 71, 54, 0.18);
  border-radius: 24px;
  background: rgba(255, 252, 246, 0.92);
}

.history-empty__icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 72px;
  height: 72px;
  margin-bottom: 18px;
  border-radius: 20px;
  background: linear-gradient(135deg, #f4ead7, #efe5d2);
}

.history-empty__title {
  margin: 0;
  color: var(--history-ink);
  font-size: 17px;
  font-weight: 700;
}

.history-empty__text {
  margin: 8px 0 0;
  color: var(--history-muted);
  font-size: 13px;
  line-height: 1.6;
  text-align: center;
}

:deep(.history-room-card.van-card) {
  margin-top: 0;
  padding: 14px;
  border: 1px solid var(--history-border);
  border-radius: 22px;
  background: var(--history-surface);
  box-shadow:
    0 1px 2px rgba(35, 47, 36, 0.04),
    0 14px 32px rgba(35, 47, 36, 0.06);
}

:deep(.history-room-card .van-card__header) {
  display: grid;
  grid-template-columns: 110px minmax(0, 1fr);
  gap: 14px;
  align-items: stretch;
}

:deep(.history-room-card .van-card__thumb) {
  width: 110px;
  height: 142px;
  margin: 0;
  border-radius: 16px;
  overflow: hidden;
  background: #efe5d2;
}

:deep(.history-room-card .van-card__content) {
  min-height: auto;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

:deep(.history-room-card .van-card__title) {
  white-space: normal;
}

:deep(.history-room-card .van-card__desc) {
  margin-top: 8px;
}

@media (max-width: 375px) {
  .browsing-history-page {
    padding: 12px;
  }
}
</style>
