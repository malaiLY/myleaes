<template>
  <van-skeleton :row="18" :loading="!apartmentDetailInfo?.id">
    <div class="apartment-detail-page">
      <section class="apartment-detail-page__hero">
        <template v-if="apartmentImages.length">
          <van-swipe
            class="apartment-detail-page__swipe"
            :autoplay="3000"
            indicator-color="rgba(255, 255, 255, 0.9)"
          >
            <van-swipe-item
              v-for="item in apartmentImages"
              :key="item.name || item.url"
            >
              <van-image
                class="apartment-detail-page__hero-image"
                fit="cover"
                :src="item.url"
                width="100vw"
                height="38vh"
              >
                <template #error>加载失败</template>
                <template #loading>
                  <div class="apartment-detail-page__hero-loading">
                    <van-loading type="spinner" size="24" />
                  </div>
                </template>
              </van-image>
            </van-swipe-item>
          </van-swipe>
          <div class="apartment-detail-page__hero-badge">
            {{ apartmentImages.length }} 张
          </div>
        </template>
        <div v-else class="apartment-detail-page__empty-image">暂无图片</div>
      </section>

      <section class="apartment-detail-page__content">
        <div class="apartment-detail-page__summary card">
          <div class="apartment-detail-page__summary-top">
            <div>
              <p class="apartment-detail-page__eyebrow">公寓详情</p>
              <h1 class="apartment-detail-page__title">
                {{ apartmentDetailInfo.name }}
              </h1>
              <p class="apartment-detail-page__address">
                <van-icon name="location-o" size="14" />
                {{ apartmentAddress }}
              </p>
            </div>
            <span class="apartment-detail-page__status">可预约</span>
          </div>

          <div
            v-if="apartmentDetailInfo.labelInfoList?.length"
            class="apartment-detail-page__tags"
          >
            <van-tag
              v-for="item in apartmentDetailInfo.labelInfoList"
              :key="item.id"
              plain
              type="primary"
              class="apartment-detail-page__tag"
            >
              {{ item.name }}
            </van-tag>
          </div>

          <div class="apartment-detail-page__price-row">
            <div>
              <span class="apartment-detail-page__price-mark">￥</span>
              <span class="apartment-detail-page__price-value">
                {{ apartmentDetailInfo.minRent }}
              </span>
              <span class="apartment-detail-page__price-unit">/月起</span>
            </div>
            <div class="apartment-detail-page__meta">
              公寓编号 {{ apartmentDetailInfo.id }}
            </div>
          </div>
        </div>

        <section class="card apartment-section">
          <div class="apartment-section__head">
            <h2 class="apartment-section__title">社区介绍</h2>
            <span class="apartment-section__sub">项目概览</span>
          </div>
          <p class="apartment-detail-page__intro">
            {{ apartmentDetailInfo.introduction || "暂无介绍" }}
          </p>
        </section>

        <section class="card apartment-section">
          <div class="apartment-section__head">
            <h2 class="apartment-section__title">配套说明</h2>
            <span class="apartment-section__sub">公共设施</span>
          </div>
          <div class="apartment-detail-page__facility-grid">
            <div
              v-for="item in apartmentDetailInfo.facilityInfoList"
              :key="item.id"
              class="apartment-detail-page__facility-item"
            >
              <div class="apartment-detail-page__facility-icon">
                <SvgIcon :name="item.icon" size="22" />
              </div>
              <span>{{ item.name }}</span>
            </div>
          </div>
        </section>

        <section class="card apartment-section">
          <div class="apartment-section__head">
            <h2 class="apartment-section__title">位置详情</h2>
            <span class="apartment-section__sub">地图定位</span>
          </div>
          <p class="apartment-detail-page__map-address">
            {{ apartmentDetailInfo.addressDetail || "暂无地址" }}
          </p>
          <div id="container" class="apartment-detail-page__map"></div>
        </section>

        <section class="card apartment-section apartment-section--rooms">
          <div class="apartment-section__head">
            <h2 class="apartment-section__title">可选房间</h2>
            <span class="apartment-section__sub">在租房源</span>
          </div>
          <PullDownRefreshContainer
            ref="pullDownRefreshContainerRef"
            :request="getRoomListHandler"
            class="apartment-detail-page__room-list"
          >
            <template v-if="roomList?.length">
              <RoomCard
                v-for="item in roomList"
                :key="item.id"
                :data="item"
                class="apartment-detail-page__room-card"
              />
            </template>
          </PullDownRefreshContainer>
        </section>
      </section>

      <van-sticky :offset-bottom="0" position="bottom">
        <div class="apartment-detail-page__action-bar">
          <div class="apartment-detail-page__action-price">
            <span class="apartment-detail-page__action-mark">￥</span>
            <span class="apartment-detail-page__action-value">
              {{ apartmentDetailInfo.minRent }}
            </span>
            <span class="apartment-detail-page__action-unit">/月起</span>
          </div>
          <van-button
            type="primary"
            class="apartment-detail-page__action-button"
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
import { getApartmentDetailById, getRoomListByApartmentId } from "@/api/search";
import type { ApartmentInterface, RoomInterface } from "@/api/search/types";
import poiMarkerRed from "@/assets/poi-marker-red.png";
import PullDownRefreshContainer from "@/components/PullDownRefreshContainer/PullDownRefreshContainer.vue";
import RoomCard from "@/components/RoomCard/RoomCard.vue";
import { useMap } from "@/hooks/useMap";
import type { ReqPage } from "@/api/types";
import { resolveImageUrl } from "@/utils/image";
import { computed, onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";

const router = useRouter();
const route = useRoute();

const apartmentDetailInfo = ref<ApartmentInterface>({} as ApartmentInterface);
const roomList = ref<RoomInterface[]>([]);
const pullDownRefreshContainerRef =
  ref<InstanceType<typeof PullDownRefreshContainer>>();

const apartmentImages = computed(() =>
  (apartmentDetailInfo.value.graphVoList || [])
    .map(item => ({
      ...item,
      url: resolveImageUrl(item.url)
    }))
    .filter(item => item.url)
);

const apartmentAddress = computed(() =>
  [
    apartmentDetailInfo.value.provinceName,
    apartmentDetailInfo.value.cityName,
    apartmentDetailInfo.value.districtName
  ]
    .filter(Boolean)
    .join(" ")
);

const getApartmentDetailHandle = async () => {
  const { data } = await getApartmentDetailById(route.query.id as string);
  apartmentDetailInfo.value = data;
};

async function getRoomListHandler(pageInfo: ReqPage) {
  const { data } = await getRoomListByApartmentId({
    id: route.query.id as string,
    ...pageInfo
  });

  data.records.forEach(item => {
    item.apartmentInfo = apartmentDetailInfo.value;
  });

  if (pageInfo.current === 1) {
    roomList.value = data.records;
  } else {
    roomList.value = [...roomList.value, ...data.records];
  }

  pullDownRefreshContainerRef.value?.setFinished(
    roomList.value.length >= data.total
  );
}

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
    query: { apartmentId: apartmentDetailInfo.value.id }
  });
};

onMounted(async () => {
  await getApartmentDetailHandle();
  await initMap();
  initMapPage({
    lng: +apartmentDetailInfo.value.longitude,
    lat: +apartmentDetailInfo.value.latitude
  });
});
</script>

<style scoped lang="less">
.apartment-detail-page {
  --apartment-primary: #204736;
  --apartment-primary-soft: #476c58;
  --apartment-accent: #b8874d;
  --apartment-ink: #1a2c22;
  --apartment-muted: #6d7b73;
  --apartment-border: rgba(32, 71, 54, 0.1);
  --apartment-surface: rgba(255, 252, 246, 0.96);
  --apartment-bg: #f4efe6;

  min-height: 100%;
  padding-bottom: 102px;
  background:
    radial-gradient(circle at top, rgba(184, 135, 77, 0.14), transparent 30%),
    linear-gradient(180deg, #f8f3ea 0%, var(--apartment-bg) 100%);
}

.apartment-detail-page__hero {
  position: relative;
}

.apartment-detail-page__swipe,
.apartment-detail-page__hero-image,
.apartment-detail-page__hero-image :deep(img) {
  width: 100%;
  height: 38vh;
}

.apartment-detail-page__hero-image :deep(img) {
  object-fit: cover;
}

.apartment-detail-page__hero::after {
  content: "";
  position: absolute;
  inset: auto 0 0;
  height: 88px;
  background: linear-gradient(180deg, transparent, rgba(244, 239, 230, 0.92));
  pointer-events: none;
}

.apartment-detail-page__hero-loading,
.apartment-detail-page__empty-image {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100vw;
  height: 38vh;
  color: rgba(255, 255, 255, 0.88);
  background: linear-gradient(135deg, #7f8c86, #4e5f57);
}

.apartment-detail-page__empty-image {
  color: var(--apartment-muted);
  background: linear-gradient(135deg, #ebe2d2, #f8f3ea);
}

.apartment-detail-page__hero-badge {
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

.apartment-detail-page__content {
  position: relative;
  z-index: 1;
  margin-top: -44px;
  padding: 0 16px;
}

.card {
  padding: 18px;
  border: 1px solid var(--apartment-border);
  border-radius: 24px;
  background: var(--apartment-surface);
  box-shadow:
    0 1px 2px rgba(35, 47, 36, 0.04),
    0 16px 34px rgba(35, 47, 36, 0.06);
}

.apartment-detail-page__summary {
  margin-bottom: 14px;
}

.apartment-detail-page__summary-top {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
}

.apartment-detail-page__eyebrow {
  margin: 0 0 6px;
  color: var(--apartment-accent);
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.12em;
}

.apartment-detail-page__title {
  margin: 0;
  color: var(--apartment-ink);
  font-size: 24px;
  line-height: 1.3;
}

.apartment-detail-page__address {
  display: flex;
  align-items: center;
  gap: 4px;
  margin: 10px 0 0;
  color: var(--apartment-muted);
  font-size: 13px;
  line-height: 1.5;
}

.apartment-detail-page__status {
  flex-shrink: 0;
  padding: 6px 12px;
  border-radius: 999px;
  background: rgba(32, 71, 54, 0.08);
  color: var(--apartment-primary-soft);
  font-size: 12px;
  font-weight: 700;
}

.apartment-detail-page__tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 14px;
}

.apartment-detail-page__tag {
  margin: 0;
  padding: 4px 10px;
  border-color: rgba(32, 71, 54, 0.14) !important;
  border-radius: 999px;
  color: var(--apartment-primary-soft) !important;
  background: rgba(32, 71, 54, 0.04) !important;
}

.apartment-detail-page__price-row {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 12px;
  margin-top: 18px;
}

.apartment-detail-page__price-mark,
.apartment-detail-page__action-mark {
  color: var(--apartment-accent);
  font-size: 16px;
  font-weight: 800;
}

.apartment-detail-page__price-value,
.apartment-detail-page__action-value {
  color: var(--apartment-primary);
  font-size: 32px;
  font-weight: 800;
  line-height: 1;
}

.apartment-detail-page__price-unit,
.apartment-detail-page__action-unit {
  color: var(--apartment-muted);
  font-size: 13px;
}

.apartment-detail-page__meta {
  color: var(--apartment-muted);
  font-size: 12px;
  white-space: nowrap;
}

.apartment-section {
  margin-top: 14px;
}

.apartment-section--rooms {
  padding-bottom: 12px;
}

.apartment-section__head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
}

.apartment-section__title {
  margin: 0;
  color: var(--apartment-ink);
  font-size: 18px;
  font-weight: 700;
}

.apartment-section__sub {
  color: rgba(109, 123, 115, 0.78);
  font-size: 12px;
}

.apartment-detail-page__intro {
  margin: 0;
  color: var(--apartment-ink);
  font-size: 14px;
  line-height: 1.8;
}

.apartment-detail-page__facility-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 12px 8px;
}

.apartment-detail-page__facility-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  min-height: 86px;
  padding: 12px 8px;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.56);
  color: var(--apartment-ink);
  font-size: 12px;
  text-align: center;
}

.apartment-detail-page__facility-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 42px;
  height: 42px;
  border-radius: 14px;
  background: rgba(32, 71, 54, 0.08);
  color: var(--apartment-primary);
}

.apartment-detail-page__map-address {
  margin: 0 0 14px;
  color: var(--apartment-muted);
  font-size: 13px;
  line-height: 1.7;
}

.apartment-detail-page__map {
  width: 100%;
  height: 30vh;
  min-height: 220px;
  border-radius: 20px;
  overflow: hidden;
}

.apartment-detail-page__room-list {
  min-height: 30vh;
}

.apartment-detail-page__room-card {
  margin-bottom: 14px;
}

.apartment-detail-page__action-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  padding: 12px 16px calc(12px + env(safe-area-inset-bottom));
  border-top: 1px solid rgba(32, 71, 54, 0.08);
  background: rgba(255, 252, 246, 0.96);
  backdrop-filter: blur(10px);
}

.apartment-detail-page__action-price {
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.apartment-detail-page__action-value {
  font-size: 28px;
}

.apartment-detail-page__action-button {
  min-width: 148px;
  height: 44px;
  padding: 0 24px;
  border: none;
  background: linear-gradient(135deg, #204736, #2f684e);
  box-shadow: 0 10px 22px rgba(32, 71, 54, 0.2);
}

@media (max-width: 375px) {
  .apartment-detail-page__content {
    padding: 0 12px;
  }

  .apartment-detail-page__title {
    font-size: 21px;
  }

  .apartment-detail-page__facility-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .apartment-detail-page__action-button {
    min-width: 132px;
  }
}
</style>
