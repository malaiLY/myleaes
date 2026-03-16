<template>
  <div class="search-page">
    <div class="search-page__filters">
      <SearchBar :confirm-callback="confirmCallback" />
    </div>

    <!-- ═══ 房源列表 ═══ -->
    <section class="search-page__results">
      <div class="search-page__results-head">
        <div class="search-page__results-label">
          <span class="search-page__results-icon">•</span>
          房源列表
        </div>
        <div class="search-page__results-count">
          <span class="search-page__results-num">{{ total }}</span> 套
        </div>
      </div>

      <PullDownRefreshContainer
        :request="getRoomListHandler"
        ref="pullDownRefreshContainerRef"
        class="search-page__feed"
      >
        <template v-if="roomList?.length">
          <RoomCard
            v-for="item in roomList"
            :key="item.id"
            :data="item"
            class="search-room-card"
          >
            <template #thumb>
              <div class="search-room-card__thumb">
                <van-image
                  class="search-room-card__image"
                  fit="cover"
                  :src="item.graphVoList?.[0]?.url || 'failed'"
                >
                  <template #error>
                    <div class="search-room-card__image-fallback">
                      <span>暂无图片</span>
                    </div>
                  </template>
                  <template #loading>
                    <div class="search-room-card__image-loading">
                      <van-loading type="spinner" size="20" color="#476c58" />
                    </div>
                  </template>
                </van-image>
                <span class="search-room-card__badge">
                  {{ item.roomNumber }} 室
                </span>
                <div class="search-room-card__img-overlay"></div>
              </div>
            </template>

            <template #title>
              <div class="search-room-card__title-wrap">
                <span class="search-room-card__title">
                  {{ `${item.apartmentInfo?.name || ""} ${item.roomNumber}房间` }}
                </span>
                <span class="search-room-card__status">在租</span>
              </div>
            </template>

            <template #desc>
              <div class="search-room-card__desc">
                <p class="search-room-card__location">
                  <van-icon name="location-o" size="12" />
                  {{
                    `${item.apartmentInfo?.provinceName || ""} ${
                      item.apartmentInfo?.cityName || ""
                    } ${item.apartmentInfo?.districtName || ""}`
                  }}
                </p>
                <p class="search-room-card__meta">
                  编号 · {{ item.apartmentId }}-{{ item.roomNumber }}
                </p>
              </div>
            </template>

            <template #tags>
              <div class="search-room-card__tags">
                <van-tag
                  v-for="label in item.labelInfoList"
                  :key="label.id"
                  plain
                  type="primary"
                  class="search-room-card__tag"
                >
                  {{ label.name }}
                </van-tag>
              </div>
            </template>

            <template #price>
              <div class="search-room-card__price-wrap">
                <span class="search-room-card__price-unit">¥</span>
                <span class="search-room-card__price">{{ item.rent }}</span>
                <span class="search-room-card__price-suffix">/月</span>
              </div>
            </template>
          </RoomCard>
        </template>

        <div v-else class="search-empty">
          <div class="search-empty__icon">
            <van-icon name="search" size="36" color="#b8874d" />
          </div>
          <h3 class="search-empty__title">暂无符合条件的房源</h3>
          <p class="search-empty__text">换个区域、价格或付款方式再试试。</p>
        </div>
      </PullDownRefreshContainer>
    </section>

  </div>
</template>

<script setup lang="ts" name="Search">
import SearchBar from "@/components/SearchBar/SearchBar.vue";
import type { RoomInterface, RoomListQueryInterface } from "@/api/search/types";
import { ref } from "vue";
import { getRoomList } from "@/api/search";
import PullDownRefreshContainer from "@/components/PullDownRefreshContainer/PullDownRefreshContainer.vue";
import type { ReqPage } from "@/api/types";
import RoomCard from "@/components/RoomCard/RoomCard.vue";

const searchParams = ref<Partial<RoomListQueryInterface>>({});
const pullDownRefreshContainerRef =
  ref<InstanceType<typeof PullDownRefreshContainer>>();
const roomList = ref<RoomInterface[]>([]);
const total = ref(0);

function confirmCallback(params: RoomListQueryInterface) {
  const targetObj = {} as Partial<RoomListQueryInterface>;
  const source = params as any;

  for (const key in params) {
    if (source[key] !== "") {
      (targetObj as any)[key] = source[key];
    }
  }

  searchParams.value = targetObj;
  pullDownRefreshContainerRef.value?.onRefresh();
}

async function getRoomListHandler(pageInfo: ReqPage) {
  const { data } = await getRoomList({ ...searchParams.value, ...pageInfo });
  total.value = data.total;

  if (pageInfo.current === 1) {
    roomList.value = data.records;
  } else {
    roomList.value = [...roomList.value, ...data.records];
  }

  pullDownRefreshContainerRef.value?.setFinished(
    roomList.value.length >= data.total
  );
}
</script>

<style scoped lang="less">
/* ─────────────────────────────────────────
   全局变量 & 基础
───────────────────────────────────────── */
.search-page {
  --c-primary: #204736;
  --c-primary-soft: #476c58;
  --c-accent: #b8874d;
  --c-ink: #1a2c22;
  --c-muted: #6d7b73;
  --c-border: rgba(32, 71, 54, 0.1);
  --c-surface: rgba(255, 252, 246, 0.96);
  --c-bg: #f4efe6;

  min-height: 100%;
  padding-top: 10px;
  padding-bottom: 96px;
  background:
    radial-gradient(circle at top, rgba(186, 136, 77, 0.08), transparent 32%),
    linear-gradient(180deg, #f8f3ea 0%, var(--c-bg) 100%);
  font-family: -apple-system, "PingFang SC", "Microsoft YaHei", sans-serif;
}

/* ─────────────────────────────────────────
   粘性搜索栏
───────────────────────────────────────── */
.search-page__filters {
  position: sticky;
  top: 0;
  z-index: 20;
  padding: 0 16px 10px;
  background: linear-gradient(
    180deg,
    rgba(248, 243, 234, 0.98),
    rgba(244, 239, 230, 0.88)
  );
}

/* ─────────────────────────────────────────
   结果区域
───────────────────────────────────────── */
.search-page__results {
  padding: 14px 16px 0;
}

.search-page__results-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 14px;
  padding: 0 2px;
}

.search-page__results-label {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--c-ink);
  font-size: 14px;
  font-weight: 700;
  letter-spacing: 0.08em;
}

.search-page__results-icon {
  color: var(--c-accent);
  font-size: 16px;
  line-height: 1;
}

.search-page__results-count {
  display: flex;
  align-items: baseline;
  gap: 3px;
  padding: 6px 12px;
  border: 1px solid var(--c-border);
  border-radius: 999px;
  background: rgba(255, 251, 243, 0.84);
  color: var(--c-muted);
  font-size: 12px;
  box-shadow: 0 8px 20px rgba(35, 47, 36, 0.04);
}

.search-page__results-num {
  color: var(--c-primary);
  font-size: 15px;
  font-weight: 800;
}

.search-page__feed {
  min-height: 60vh;
}

/* ─────────────────────────────────────────
   空状态
───────────────────────────────────────── */
.search-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px 24px;
  margin-top: 12px;
  border: 1px dashed rgba(32, 71, 54, 0.18);
  border-radius: 24px;
  background: rgba(255, 252, 246, 0.92);
  animation: fade-up 0.4s ease-out;
}

.search-empty__icon {
  width: 72px; height: 72px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 20px;
  background: linear-gradient(135deg, #f4ead7, #efe5d2);
  margin-bottom: 18px;
}

.search-empty__title {
  margin: 0;
  color: var(--c-ink);
  font-size: 17px;
  font-weight: 700;
}

.search-empty__text {
  margin: 8px 0 0;
  color: var(--c-muted);
  font-size: 13px;
  text-align: center;
  line-height: 1.6;
}

/* ─────────────────────────────────────────
   房源卡片
───────────────────────────────────────── */
.search-room-card {
  margin-bottom: 16px;
  animation: fade-up 0.4s ease-out both;
}

/* 图片区 */
.search-room-card__thumb {
  position: relative;
  width: 100%;
  height: 100%;
  border-radius: 16px;
  overflow: hidden;
}

.search-room-card__image,
.search-room-card__image :deep(img) {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.search-room-card__img-overlay {
  position: absolute;
  inset: 0;
  background: linear-gradient(to bottom, transparent 35%, rgba(26, 44, 34, 0.3));
  pointer-events: none;
}

.search-room-card__image-fallback,
.search-room-card__image-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #f1e8d8, #f8f3ea);
  color: var(--c-primary-soft);
  font-size: 12px;
  font-weight: 500;
  gap: 6px;
}

.search-room-card__badge {
  position: absolute;
  right: 8px;
  bottom: 8px;
  padding: 4px 8px;
  border-radius: 999px;
  background: rgba(26, 44, 34, 0.68);
  color: rgba(255, 252, 246, 0.92);
  font-size: 10px;
  font-weight: 700;
  letter-spacing: 0.02em;
  backdrop-filter: blur(6px);
  z-index: 1;
}

/* 标题行 */
.search-room-card__title-wrap {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 8px;
}

.search-room-card__title {
  color: var(--c-ink);
  font-size: 16px;
  font-weight: 700;
  line-height: 1.4;
}

.search-room-card__status {
  flex-shrink: 0;
  padding: 2px 8px;
  border: 1px solid rgba(32, 71, 54, 0.14);
  border-radius: 999px;
  background: rgba(255, 252, 246, 0.8);
  color: var(--c-primary-soft);
  font-size: 10px;
  font-weight: 700;
}

/* 描述 */
.search-room-card__desc {
  display: grid;
  gap: 4px;
}

.search-room-card__location {
  display: flex;
  align-items: center;
  gap: 3px;
  margin: 0;
  color: var(--c-muted);
  font-size: 12px;
  line-height: 1.5;
}

.search-room-card__meta {
  margin: 0;
  color: rgba(109, 123, 115, 0.78);
  font-size: 11px;
  letter-spacing: 0.02em;
}

/* 标签 */
.search-room-card__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  margin-top: 8px;
}

.search-room-card__tag {
  margin: 0;
  padding: 2px 8px;
  border-color: rgba(32, 71, 54, 0.14) !important;
  border-radius: 999px;
  color: var(--c-primary-soft) !important;
  background: rgba(32, 71, 54, 0.04) !important;
  font-size: 11px;
}

/* 价格 */
.search-room-card__price-wrap {
  display: flex;
  align-items: baseline;
  gap: 3px;
  margin-top: 12px;
}

.search-room-card__price-unit {
  color: var(--c-accent);
  font-size: 13px;
  font-weight: 700;
}

.search-room-card__price {
  color: var(--c-primary);
  font-size: 26px;
  font-weight: 800;
  line-height: 1;
}

.search-room-card__price-suffix {
  color: rgba(109, 123, 115, 0.82);
  font-size: 12px;
  font-weight: 500;
}

/* ─────────────────────────────────────────
   :deep 覆写 van-card
───────────────────────────────────────── */
:deep(.search-room-card.van-card) {
  margin-top: 0;
  padding: 14px;
  border: 1px solid var(--c-border);
  border-radius: 22px;
  background: var(--c-surface);
  box-shadow:
    0 1px 2px rgba(35, 47, 36, 0.04),
    0 14px 32px rgba(35, 47, 36, 0.06);
  transition: box-shadow 0.2s ease, transform 0.2s ease;

  &:active {
    transform: translateY(1px);
    box-shadow: 0 6px 18px rgba(35, 47, 36, 0.05);
  }

  /* 顶部渐变高亮细条 */
  &::before {
    content: none;
  }
}

:deep(.search-room-card .van-card__header) {
  display: grid;
  grid-template-columns: 110px minmax(0, 1fr);
  gap: 14px;
  align-items: stretch;
}

:deep(.search-room-card .van-card__thumb) {
  width: 110px;
  height: 142px;
  margin: 0;
  border-radius: 16px;
  overflow: hidden;
  background: #efe5d2;
}

:deep(.search-room-card .van-card__content) {
  min-height: auto;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

:deep(.search-room-card .van-card__title) {
  white-space: normal;
}

:deep(.search-room-card .van-card__desc) {
  margin-top: 8px;
}

:deep(.search-page__feed .van-pull-refresh) {
  overflow: visible;
}

:deep(.search-page__feed .van-loading) {
  color: var(--c-primary);
}

/* ─────────────────────────────────────────
   动画
───────────────────────────────────────── */
@keyframes fade-up {
  from { opacity: 0; transform: translateY(16px); }
  to   { opacity: 1; transform: translateY(0); }
}
</style>
