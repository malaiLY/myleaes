<template>
  <van-skeleton :row="18" :loading="!roomDetailInfo?.id">
    <div class="room-detail-page">
      <section class="room-detail-page__hero">
        <template v-if="roomImages.length">
          <van-swipe
            class="room-detail-page__swipe"
            :autoplay="3000"
            indicator-color="rgba(255, 255, 255, 0.9)"
          >
            <van-swipe-item
              v-for="item in roomImages"
              :key="item.name || item.url"
            >
              <van-image
                class="room-detail-page__hero-image"
                fit="cover"
                :src="item.url"
                width="100vw"
                height="38vh"
              >
                <template #error>加载失败</template>
                <template #loading>
                  <div class="room-detail-page__hero-loading">
                    <van-loading type="spinner" size="24" />
                  </div>
                </template>
              </van-image>
            </van-swipe-item>
          </van-swipe>
          <div class="room-detail-page__hero-badge">
            {{ roomImages.length }} 张
          </div>
        </template>
        <div v-else class="room-detail-page__empty-image">暂无图片</div>
      </section>

      <section class="room-detail-page__content">
        <div class="room-detail-page__summary card">
          <div class="room-detail-page__summary-top">
            <div>
              <p class="room-detail-page__eyebrow">房源详情</p>
              <h1 class="room-detail-page__title">{{ roomTitle }}</h1>
              <p class="room-detail-page__address">
                <van-icon name="location-o" size="14" />
                {{ roomAddress }}
              </p>
            </div>
            <span class="room-detail-page__status">在租中</span>
          </div>

          <div v-if="roomDetailInfo.labelInfoList?.length" class="room-detail-page__tags">
            <van-tag
              v-for="item in roomDetailInfo.labelInfoList"
              :key="item.id"
              plain
              type="primary"
              class="room-detail-page__tag"
            >
              {{ item.name }}
            </van-tag>
          </div>

          <div class="room-detail-page__price-row">
            <div>
              <span class="room-detail-page__price-mark">￥</span>
              <span class="room-detail-page__price-value">{{ roomDetailInfo.rent }}</span>
              <span class="room-detail-page__price-unit">/月</span>
            </div>
            <div class="room-detail-page__room-no">
              房号 {{ roomDetailInfo.roomNumber }}
            </div>
          </div>
        </div>

        <section class="card room-section">
          <div class="room-section__head">
            <h2 class="room-section__title">基本信息</h2>
            <span class="room-section__sub">房间概览</span>
          </div>
          <div class="room-detail-page__info-grid">
            <div
              v-for="item in roomDetailInfo.attrValueVoList"
              :key="item.id"
              class="room-detail-page__info-item"
            >
              <span class="room-detail-page__info-label">{{ item.attrKeyName }}</span>
              <span class="room-detail-page__info-value">{{ item.name }}</span>
            </div>
          </div>
        </section>

        <section class="card room-section">
          <div class="room-section__head">
            <h2 class="room-section__title">配套信息</h2>
            <span class="room-section__sub">日常设施</span>
          </div>
          <div class="room-detail-page__facility-grid">
            <div
              v-for="item in roomDetailInfo.facilityInfoList"
              :key="item.id"
              class="room-detail-page__facility-item"
            >
              <div class="room-detail-page__facility-icon">
                <SvgIcon :name="item.icon" size="22" />
              </div>
              <span>{{ item.name }}</span>
            </div>
          </div>
        </section>

        <section class="card room-section">
          <div class="room-section__head">
            <h2 class="room-section__title">位置详情</h2>
            <span class="room-section__sub">地图定位</span>
          </div>
          <p class="room-detail-page__map-address">
            {{ roomDetailInfo.apartmentItemVo?.addressDetail || "暂无地址" }}
          </p>
          <div id="container" class="room-detail-page__map"></div>
        </section>

        <section class="card room-section">
          <div class="room-section__head">
            <h2 class="room-section__title">费用明细</h2>
            <span class="room-section__sub">收费标准</span>
          </div>
          <div class="room-detail-page__table">
            <div class="room-detail-page__table-head">
              <span>费用科目</span>
              <span>收费标准</span>
            </div>
            <div
              v-for="item in roomDetailInfo.feeValueVoList"
              :key="item.id"
              class="room-detail-page__table-row"
            >
              <span>{{ item.feeKeyName }}</span>
              <span>{{ `￥${item.name}${item.unit}` }}</span>
            </div>
          </div>
        </section>

        <section class="card room-section">
          <div class="room-section__head">
            <h2 class="room-section__title">付款方式</h2>
            <span class="room-section__sub">支持选项</span>
          </div>
          <div class="room-detail-page__table">
            <div class="room-detail-page__table-head">
              <span>付款方式</span>
              <span>说明</span>
            </div>
            <div
              v-for="item in roomDetailInfo.paymentTypeList"
              :key="item.id"
              class="room-detail-page__table-row"
            >
              <span>{{ item.name }}</span>
              <span>{{ item.additionalInfo }}</span>
            </div>
          </div>
        </section>

        <section class="card room-section">
          <div class="room-section__head">
            <h2 class="room-section__title">可选租期</h2>
            <span class="room-section__sub">签约周期</span>
          </div>
          <div class="room-detail-page__table">
            <div class="room-detail-page__table-head">
              <span>租期</span>
              <span>说明</span>
            </div>
            <div
              v-for="item in roomDetailInfo.leaseTermList"
              :key="item.id"
              class="room-detail-page__table-row"
            >
              <span>{{ item.monthCount + item.unit }}</span>
              <span>到期可续</span>
            </div>
          </div>
        </section>

        <section class="card room-section room-section--apartment">
          <div class="room-section__head">
            <h2 class="room-section__title">所属公寓</h2>
            <span class="room-section__sub">同社区房源</span>
          </div>
          <ApartmentCard :data="roomDetailInfo.apartmentItemVo" />
        </section>
      </section>

      <van-sticky :offset-bottom="0" position="bottom">
        <div class="room-detail-page__action-bar">
          <div class="room-detail-page__action-price">
            <span class="room-detail-page__action-mark">￥</span>
            <span class="room-detail-page__action-value">{{ roomDetailInfo.rent }}</span>
            <span class="room-detail-page__action-unit">/月</span>
          </div>
          <van-button
            type="primary"
            class="room-detail-page__action-button"
            round
            @click="appointmentToViewHandle"
          >
            预约看房
          </van-button>
        </div>
      </van-sticky>
    </div>
  </van-skeleton>
</template>

<script setup lang="ts">
import { getRoomDetailById } from "@/api/search";
import type { RoomDetailInterface } from "@/api/search/types";
import poiMarkerRed from "@/assets/poi-marker-red.png";
import ApartmentCard from "@/components/ApartmentCard/ApartmentCard.vue";
import { useMap } from "@/hooks/useMap";
import { resolveImageUrl } from "@/utils/image";
import { computed, onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

const router = useRouter();
const route = useRoute();

const roomDetailInfo = ref<RoomDetailInterface>({} as RoomDetailInterface);

const roomImages = computed(() =>
  (roomDetailInfo.value.graphVoList || [])
    .map(item => ({
      ...item,
      url: resolveImageUrl(item.url)
    }))
    .filter(item => item.url)
);

const roomTitle = computed(() => {
  const apartmentName = roomDetailInfo.value.apartmentItemVo?.name || "";
  const roomNumber = roomDetailInfo.value.roomNumber || "";

  return `${apartmentName} ${roomNumber}房间`.trim();
});

const roomAddress = computed(() => {
  const apartment = roomDetailInfo.value.apartmentItemVo;

  return [
    apartment?.provinceName,
    apartment?.cityName,
    apartment?.districtName
  ]
    .filter(Boolean)
    .join(" ");
});

const getRoomDetailHandle = async () => {
  const { data } = await getRoomDetailById(route.query.id as string);
  roomDetailInfo.value = data;
};

const { AMap, initMap } = useMap();

function initMapPage(opts?: { lng: number; lat: number }) {
  const map = new AMap.value.Map("container", {
    zoom: 19,
    center: [opts?.lng, opts?.lat],
    showIndoorMap: false
  });

  const icon = new AMap.value.Icon({
    size: new AMap.value.Size(25, 34),
    image: poiMarkerRed,
    imageSize: new AMap.value.Size(25, 34)
  });

  const marker = new AMap.value.Marker({
    icon,
    position: [opts?.lng, opts?.lat],
    anchor: "bottom-center"
  });

  map.add(marker);
  map.setFitView();
}

const appointmentToViewHandle = () => {
  router.push({
    path: "/appointment",
    query: { apartmentId: roomDetailInfo.value.apartmentId }
  });
};

onMounted(async () => {
  await getRoomDetailHandle();
  await initMap();
  initMapPage({
    lng: +roomDetailInfo.value.apartmentItemVo.longitude,
    lat: +roomDetailInfo.value.apartmentItemVo.latitude
  });
});
</script>

<style scoped lang="less">
.room-detail-page {
  --room-primary: #204736;
  --room-primary-soft: #476c58;
  --room-accent: #b8874d;
  --room-ink: #1a2c22;
  --room-muted: #6d7b73;
  --room-border: rgba(32, 71, 54, 0.1);
  --room-surface: rgba(255, 252, 246, 0.96);
  --room-bg: #f4efe6;

  min-height: 100%;
  padding-bottom: 102px;
  background:
    radial-gradient(circle at top, rgba(184, 135, 77, 0.14), transparent 30%),
    linear-gradient(180deg, #f8f3ea 0%, var(--room-bg) 100%);
}

.room-detail-page__hero {
  position: relative;
}

.room-detail-page__swipe,
.room-detail-page__hero-image,
.room-detail-page__hero-image :deep(img) {
  width: 100%;
  height: 38vh;
}

.room-detail-page__hero-image :deep(img) {
  object-fit: cover;
}

.room-detail-page__hero::after {
  content: "";
  position: absolute;
  inset: auto 0 0;
  height: 88px;
  background: linear-gradient(180deg, transparent, rgba(244, 239, 230, 0.92));
  pointer-events: none;
}

.room-detail-page__hero-loading,
.room-detail-page__empty-image {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100vw;
  height: 38vh;
  color: rgba(255, 255, 255, 0.88);
  background: linear-gradient(135deg, #7f8c86, #4e5f57);
}

.room-detail-page__empty-image {
  color: var(--room-muted);
  background: linear-gradient(135deg, #ebe2d2, #f8f3ea);
}

.room-detail-page__hero-badge {
  position: absolute;
  right: 18px;
  bottom: 18px;
  z-index: 1;
  padding: 6px 12px;
  border: 1px solid rgba(255, 255, 255, 0.22);
  border-radius: 999px;
  background: rgba(18, 28, 23, 0.48);
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  backdrop-filter: blur(8px);
}

.room-detail-page__content {
  position: relative;
  z-index: 1;
  margin-top: -44px;
  padding: 0 16px;
}

.card {
  padding: 18px;
  border: 1px solid var(--room-border);
  border-radius: 24px;
  background: var(--room-surface);
  box-shadow:
    0 1px 2px rgba(35, 47, 36, 0.04),
    0 16px 34px rgba(35, 47, 36, 0.06);
}

.room-detail-page__summary {
  margin-bottom: 14px;
}

.room-detail-page__summary-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.room-detail-page__eyebrow {
  margin: 0 0 6px;
  color: var(--room-accent);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.12em;
}

.room-detail-page__title {
  margin: 0;
  color: var(--room-ink);
  font-size: 24px;
  line-height: 1.3;
}

.room-detail-page__address {
  display: flex;
  align-items: center;
  gap: 4px;
  margin: 10px 0 0;
  color: var(--room-muted);
  font-size: 13px;
  line-height: 1.5;
}

.room-detail-page__status {
  flex-shrink: 0;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(32, 71, 54, 0.08);
  color: var(--room-primary-soft);
  font-size: 12px;
  font-weight: 700;
}

.room-detail-page__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 14px;
}

.room-detail-page__tag {
  margin: 0;
  padding: 4px 10px;
  border-color: rgba(32, 71, 54, 0.14) !important;
  border-radius: 999px;
  color: var(--room-primary-soft) !important;
  background: rgba(32, 71, 54, 0.04) !important;
}

.room-detail-page__price-row {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 12px;
  margin-top: 18px;
}

.room-detail-page__price-mark,
.room-detail-page__action-mark {
  color: var(--room-accent);
  font-size: 16px;
  font-weight: 800;
}

.room-detail-page__price-value,
.room-detail-page__action-value {
  color: var(--room-primary);
  font-size: 32px;
  font-weight: 800;
  line-height: 1;
}

.room-detail-page__price-unit,
.room-detail-page__action-unit {
  color: var(--room-muted);
  font-size: 13px;
}

.room-detail-page__room-no {
  color: var(--room-muted);
  font-size: 12px;
  white-space: nowrap;
}

.room-section {
  margin-top: 14px;
}

.room-section--apartment {
  padding-bottom: 12px;
}

.room-section__head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
}

.room-section__title {
  margin: 0;
  color: var(--room-ink);
  font-size: 18px;
  font-weight: 700;
}

.room-section__sub {
  color: rgba(109, 123, 115, 0.78);
  font-size: 12px;
}

.room-detail-page__info-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.room-detail-page__info-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 14px;
  border: 1px solid rgba(32, 71, 54, 0.08);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.5);
}

.room-detail-page__info-label {
  color: var(--room-primary-soft);
  font-size: 12px;
  font-weight: 700;
}

.room-detail-page__info-value {
  color: var(--room-ink);
  font-size: 15px;
  font-weight: 600;
}

.room-detail-page__facility-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px 8px;
}

.room-detail-page__facility-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  min-height: 86px;
  padding: 12px 8px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.56);
  color: var(--room-ink);
  font-size: 12px;
  text-align: center;
}

.room-detail-page__facility-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  border-radius: 14px;
  background: rgba(32, 71, 54, 0.08);
  color: var(--room-primary);
}

.room-detail-page__map-address {
  margin: 0 0 14px;
  color: var(--room-muted);
  font-size: 13px;
  line-height: 1.7;
}

.room-detail-page__map {
  width: 100%;
  height: 30vh;
  min-height: 220px;
  border-radius: 20px;
  overflow: hidden;
}

.room-detail-page__table {
  overflow: hidden;
  border: 1px solid rgba(32, 71, 54, 0.08);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.52);
}

.room-detail-page__table-head,
.room-detail-page__table-row {
  display: grid;
  grid-template-columns: minmax(0, 110px) minmax(0, 1fr);
  gap: 16px;
  padding: 13px 14px;
}

.room-detail-page__table-head {
  background: rgba(32, 71, 54, 0.06);
  color: var(--room-primary-soft);
  font-size: 12px;
  font-weight: 700;
}

.room-detail-page__table-row {
  color: var(--room-ink);
  font-size: 13px;
  line-height: 1.6;
}

.room-detail-page__table-row + .room-detail-page__table-row {
  border-top: 1px solid rgba(32, 71, 54, 0.08);
}

.room-detail-page__action-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  padding: 12px 16px calc(12px + env(safe-area-inset-bottom));
  border-top: 1px solid rgba(32, 71, 54, 0.08);
  background: rgba(255, 252, 246, 0.96);
  backdrop-filter: blur(10px);
}

.room-detail-page__action-price {
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.room-detail-page__action-value {
  font-size: 28px;
}

.room-detail-page__action-button {
  min-width: 148px;
  height: 44px;
  padding: 0 24px;
  border: none;
  background: linear-gradient(135deg, #204736, #2f684e);
  box-shadow: 0 10px 22px rgba(32, 71, 54, 0.2);
}

@media (max-width: 375px) {
  .room-detail-page__content {
    padding: 0 12px;
  }

  .room-detail-page__title {
    font-size: 21px;
  }

  .room-detail-page__facility-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .room-detail-page__table-head,
  .room-detail-page__table-row {
    grid-template-columns: minmax(0, 94px) minmax(0, 1fr);
    gap: 12px;
  }

  .room-detail-page__action-button {
    min-width: 132px;
  }
}
</style>
