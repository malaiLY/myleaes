<template>
  <van-card
    class="rounded-xl shadow"
    :title="`${data.name}`"
    :desc="`${data.provinceName} ${data.cityName} ${data.districtName}`"
    @click="goApartmentDetail"
  >
    <template #price>
      <span class="text-red-500 text-[14px]">￥</span>
      <span class="text-red-500 text-[16px]">{{ data.minRent }}/月起</span>
    </template>

    <template #thumb>
      <van-image
        class="w-full h-full object-cover"
        :src="resolveImageUrl(data.graphVoList?.[0]?.url)"
      >
        <template #error>加载失败</template>
        <template #loading>
          <van-loading type="spinner" size="20" />
        </template>
      </van-image>
    </template>

    <template #tags>
      <van-tag
        v-for="item in data.labelInfoList"
        :key="item.id"
        class="last:mr-0 mr-[5px]"
        plain
        type="primary"
      >
        {{ item.name }}
      </van-tag>
    </template>
  </van-card>
</template>

<script setup lang="ts">
import type { ApartmentInterface } from "@/api/search/types";
import { resolveImageUrl } from "@/utils/image";
import type { PropType } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

const props = defineProps({
  data: {
    type: Object as PropType<ApartmentInterface>,
    default: () => ({}),
    readOnly: true
  }
});

const goApartmentDetail = () => {
  router.push({ path: "/apartmentDetail", query: { id: props.data.id } });
};
</script>

<style scoped></style>
