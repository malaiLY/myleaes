<template>
  <van-card @click="goRoomDetail" class="rounded-xl shadow">
    <template #title>
      <slot name="title">
        <span class="text-[14px] font-bold">{{
          `${data.apartmentInfo?.name || ""} ${data.roomNumber}房间`
        }}</span>
      </slot>
    </template>

    <template #desc>
      <slot name="desc">
        <div>
          <span class="text-[12px] --van-gray-6">{{
            `${data?.apartmentInfo?.provinceName || ""} ${
              data?.apartmentInfo?.cityName || ""
            } ${data?.apartmentInfo?.districtName || ""}`
          }}</span>
        </div>
      </slot>
    </template>

    <template #price>
      <slot name="price">
        <span class="text-red-500 text-[14px]">￥</span>
        <span class="text-red-500 text-[20px]">{{ data.rent }}</span>
      </slot>
    </template>

    <template #thumb>
      <slot name="thumb">
        <van-image
          class="w-full h-full object-cover"
          :src="resolveImageUrl(data.graphVoList?.[0]?.url)"
        >
          <template #error>加载失败</template>
          <template #loading>
            <van-loading type="spinner" size="20" />
          </template>
        </van-image>
      </slot>
    </template>

    <template #tags>
      <slot name="tags">
        <van-tag
          v-for="item in data?.labelInfoList"
          :key="item.id"
          class="last:mr-0 mr-[5px]"
          plain
          type="primary"
        >
          {{ item.name }}
        </van-tag>
      </slot>
    </template>
  </van-card>
</template>

<script setup lang="ts">
import type { RoomInterface } from "@/api/search/types";
import { resolveImageUrl } from "@/utils/image";
import type { PropType } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

const props = defineProps({
  data: {
    type: Object as PropType<RoomInterface>,
    default: () => ({}),
    readOnly: true
  }
});

const goRoomDetail = () => {
  router.push({ path: "/roomDetail", query: { id: props.data.id } });
};
</script>

<style scoped></style>
