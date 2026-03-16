<template>
  <div class="search-filter-shell">
    <div class="search-filter-shell__intro">
      <span class="search-filter-shell__eyebrow">Quick Filter</span>
      <p class="search-filter-shell__text">
        按地区、预算、付款方式和排序快速定位理想房源
      </p>
    </div>

    <van-dropdown-menu ref="menuRef" class="search-filter-shell__menu">
      <van-dropdown-item title="地区">
        <div class="filter-panel">
          <div class="filter-panel__areas">
            <div class="filter-column">
              <div
                v-for="item in areaInfo.provinceList"
                :key="item.id"
                class="filter-option"
                :class="{ 'filter-option--active': item.isCheck }"
                @click="clickAreaItemHandle(item, 0)"
              >
                {{ item.name }}
              </div>
            </div>

            <div class="filter-column">
              <div
                v-for="item in areaInfo.cityList"
                :key="item.id"
                class="filter-option"
                :class="{ 'filter-option--active': item.isCheck }"
                @click="clickAreaItemHandle(item, 1)"
              >
                {{ item.name }}
              </div>
            </div>

            <div class="filter-column">
              <div
                v-for="item in areaInfo.districtList"
                :key="item.id"
                class="filter-option"
                :class="{ 'filter-option--active': item.isCheck }"
                @click="clickAreaItemHandle(item, 2)"
              >
                {{ item.name }}
              </div>
            </div>
          </div>

          <div class="filter-actions">
            <van-button
              type="default"
              size="small"
              class="filter-actions__button"
              round
              @click="onCancelHandle"
            >
              取消
            </van-button>
            <van-button
              type="primary"
              size="small"
              class="filter-actions__button"
              round
              @click="onConfirmHandle"
            >
              查找
            </van-button>
          </div>
        </div>
      </van-dropdown-item>

      <van-dropdown-item title="价格">
        <div class="filter-panel">
          <div class="filter-chip-grid">
            <div
              v-for="item in rentInfoList"
              :key="item.label"
              class="filter-chip"
              :class="{ 'filter-chip--active': item.isCheck }"
              @click="rentClickHandle(item)"
            >
              {{ item.label }}
            </div>
          </div>

          <div class="filter-actions">
            <van-button
              type="default"
              size="small"
              class="filter-actions__button"
              round
              @click="onCancelHandle"
            >
              取消
            </van-button>
            <van-button
              type="primary"
              size="small"
              class="filter-actions__button"
              round
              @click="onConfirmHandle"
            >
              查找
            </van-button>
          </div>
        </div>
      </van-dropdown-item>

      <van-dropdown-item title="付款方式">
        <div class="filter-panel">
          <div class="filter-chip-grid">
            <div
              v-for="item in paymentInfoList"
              :key="item.label"
              class="filter-chip"
              :class="{ 'filter-chip--active': item.isCheck }"
              @click="paymentClickHandle(item)"
            >
              {{ item.label }}
            </div>
          </div>

          <div class="filter-actions">
            <van-button
              type="default"
              size="small"
              class="filter-actions__button"
              round
              @click="onCancelHandle"
            >
              取消
            </van-button>
            <van-button
              type="primary"
              size="small"
              class="filter-actions__button"
              round
              @click="onConfirmHandle"
            >
              查找
            </van-button>
          </div>
        </div>
      </van-dropdown-item>

      <van-dropdown-item title="排序">
        <div class="filter-panel">
          <div class="filter-chip-grid">
            <div
              v-for="item in orderTypeInfoList"
              :key="item.label"
              class="filter-chip"
              :class="{ 'filter-chip--active': item.isCheck }"
              @click="orderTypeClickHandle(item)"
            >
              {{ item.label }}
            </div>
          </div>

          <div class="filter-actions">
            <van-button
              type="default"
              size="small"
              class="filter-actions__button"
              round
              @click="onCancelHandle"
            >
              取消
            </van-button>
            <van-button
              type="primary"
              size="small"
              class="filter-actions__button"
              round
              @click="onConfirmHandle"
            >
              查找
            </van-button>
          </div>
        </div>
      </van-dropdown-item>
    </van-dropdown-menu>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import type { DropdownMenuInstance } from "vant";
import type {
  LabelValueInterface,
  RegionInterface,
  RoomListQueryInterface
} from "@/api/search/types";
import {
  getCityList,
  getDistrictList,
  getPaymentTypeList,
  getProvinceList
} from "@/api/search";
import { SearchOrderType, SearchOrderTypeMap } from "@/enums/constEnums";

const props = defineProps({
  confirmCallback: {
    type: Function,
    default: () => ({})
  }
});

type FilterSnapshot = {
  formData: Omit<RoomListQueryInterface, "current" | "size">;
  areaInfo: {
    provinceList: RegionInterface[];
    cityList: RegionInterface[];
    oldCityListInfo: {
      provinceId: string;
      cityList: RegionInterface[];
    };
    districtList: RegionInterface[];
    oldDistrictList: {
      cityId: string;
      districtList: RegionInterface[];
    };
  };
  rentInfoList: LabelValueInterface<(number | string)[]>[];
  paymentInfoList: LabelValueInterface[];
  orderTypeInfoList: LabelValueInterface[];
};

const menuRef = ref<DropdownMenuInstance>();
const formData = ref<Omit<RoomListQueryInterface, "current" | "size">>({
  provinceId: "",
  cityId: "",
  districtId: "",
  minRent: "",
  maxRent: "",
  paymentTypeId: "",
  orderType: ""
});

const areaInfo = ref({
  provinceList: [] as RegionInterface[],
  cityList: [] as RegionInterface[],
  oldCityListInfo: {
    provinceId: "",
    cityList: [] as RegionInterface[]
  },
  districtList: [] as RegionInterface[],
  oldDistrictList: {
    cityId: "",
    districtList: [] as RegionInterface[]
  }
});

const lastConfirmedFilters = ref<FilterSnapshot | null>(null);

function cloneData<T>(value: T): T {
  return JSON.parse(JSON.stringify(value));
}

const clickAreaItemHandle = (item: RegionInterface, type: number) => {
  if (type === 0) {
    if (item.isCheck) {
      item.isCheck = !item.isCheck;
      formData.value.provinceId = "";
      formData.value.cityId = "";
      formData.value.districtId = "";
      areaInfo.value.cityList.forEach(city => {
        city.isCheck = false;
      });
      areaInfo.value.districtList.forEach(district => {
        district.isCheck = false;
      });
      return;
    }

    areaInfo.value.provinceList.forEach(province => {
      province.isCheck = false;
    });
    item.isCheck = !item.isCheck;
    formData.value.provinceId = item.id;
    provinceChangeCallback();
  } else if (type === 1) {
    if (item.isCheck) {
      item.isCheck = !item.isCheck;
      formData.value.cityId = "";
      formData.value.districtId = "";
      areaInfo.value.districtList.forEach(district => {
        district.isCheck = false;
      });
      return;
    }

    areaInfo.value.cityList.forEach(city => {
      city.isCheck = false;
    });
    item.isCheck = !item.isCheck;
    formData.value.cityId = item.id;
    cityChangeCallback();
  } else if (type === 2) {
    areaInfo.value.districtList.forEach(district => {
      if (district.id !== item.id) {
        district.isCheck = false;
      } else {
        item.isCheck = !item.isCheck;
      }
    });
    formData.value.districtId = item.isCheck ? item.id : "";
  }
};

async function getProvinceListHandle() {
  try {
    const { data } = await getProvinceList();
    areaInfo.value.provinceList = data;
  } catch (error) {
    console.log(error);
  }
}

async function getCityListHandle(
  provinceId: number | string = formData.value.provinceId
) {
  if (!provinceId) return;

  try {
    if (
      areaInfo.value.oldCityListInfo.provinceId === provinceId &&
      areaInfo.value.oldCityListInfo.cityList.length
    ) {
      areaInfo.value.cityList = JSON.parse(
        JSON.stringify(areaInfo.value.oldCityListInfo.cityList)
      );
      return;
    }

    const { data } = await getCityList(provinceId);
    areaInfo.value.oldCityListInfo.provinceId = provinceId as string;
    areaInfo.value.oldCityListInfo.cityList = JSON.parse(JSON.stringify(data));
    areaInfo.value.cityList = data;
  } catch (error) {
    console.log(error);
  }
}

async function getDistrictListHandle(
  cityId: number | string = formData.value.cityId
) {
  if (!cityId) return;

  try {
    if (
      areaInfo.value.oldDistrictList.cityId === cityId &&
      areaInfo.value.oldDistrictList.districtList.length
    ) {
      areaInfo.value.districtList = JSON.parse(
        JSON.stringify(areaInfo.value.oldDistrictList.districtList)
      );
      return;
    }

    const { data } = await getDistrictList(cityId);
    areaInfo.value.oldDistrictList.cityId = cityId as string;
    areaInfo.value.oldDistrictList.districtList = JSON.parse(
      JSON.stringify(data)
    );
    areaInfo.value.districtList = data;
  } catch (error) {
    console.log(error);
  }
}

function resetCity() {
  formData.value.cityId = "";
  areaInfo.value.cityList = [];
}

function resetDistrict() {
  formData.value.districtId = "";
  areaInfo.value.districtList = [];
}

const provinceChangeCallback = async () => {
  const provinceId = formData.value.provinceId;

  if (provinceId) {
    resetCity();
    resetDistrict();
    await getCityListHandle(provinceId);
  }
};

const cityChangeCallback = async () => {
  const cityId = formData.value.cityId;

  if (cityId) {
    resetDistrict();
    await getDistrictListHandle(cityId);
  }
};

const rentInfoList = ref<LabelValueInterface<(number | string)[]>[]>([
  {
    label: "不限",
    value: ["", ""],
    isCheck: true
  },
  {
    label: "0-1500元",
    value: [0, 1500],
    isCheck: false
  },
  {
    label: "1500-3000元",
    value: [1500, 3000],
    isCheck: false
  },
  {
    label: "3000-6000元",
    value: [3000, 6000],
    isCheck: false
  },
  {
    label: "6000-9000元",
    value: [6000, 9000],
    isCheck: false
  },
  {
    label: "9000-12000元",
    value: [9000, 12000],
    isCheck: false
  }
]);

function rentClickHandle(item: LabelValueInterface<(number | string)[]>) {
  rentInfoList.value.forEach(rent => {
    if (item.label === rent.label) {
      item.isCheck = !item.isCheck;
      formData.value.minRent = item.isCheck ? item.value[0] : "";
      formData.value.maxRent = item.isCheck ? item.value[1] : "";
    } else {
      rent.isCheck = false;
    }
  });
}

const paymentInfoList = ref<LabelValueInterface[]>([]);

async function getPaymentListHandle() {
  try {
    const { data } = await getPaymentTypeList();
    paymentInfoList.value =
      data?.map(item => ({
        label: item.name,
        value: item.id,
        isCheck: false
      })) || [];
  } catch (error) {
    console.log(error);
  }
}

function paymentClickHandle(item: LabelValueInterface) {
  paymentInfoList.value.forEach(payment => {
    if (item.label === payment.label) {
      item.isCheck = !item.isCheck;
      formData.value.paymentTypeId = item.isCheck ? item.value : "";
    } else {
      payment.isCheck = false;
    }
  });
}

const orderTypeInfoList = ref<LabelValueInterface[]>(
  SearchOrderTypeMap.map(item => ({
    ...item,
    isCheck: false
  }))
);

function orderTypeClickHandle(item: LabelValueInterface) {
  orderTypeInfoList.value.forEach(order => {
    if (item.label === order.label) {
      item.isCheck = !item.isCheck;
      formData.value.orderType = item.isCheck
        ? (item.value as SearchOrderType)
        : "";
    } else {
      order.isCheck = false;
    }
  });
}

function saveCurrentFilters() {
  lastConfirmedFilters.value = {
    formData: cloneData(formData.value),
    areaInfo: cloneData(areaInfo.value),
    rentInfoList: cloneData(rentInfoList.value),
    paymentInfoList: cloneData(paymentInfoList.value),
    orderTypeInfoList: cloneData(orderTypeInfoList.value)
  };
}

function restoreLastConfirmedFilters() {
  if (!lastConfirmedFilters.value) return;

  formData.value = cloneData(lastConfirmedFilters.value.formData);
  areaInfo.value = cloneData(lastConfirmedFilters.value.areaInfo);
  rentInfoList.value = cloneData(lastConfirmedFilters.value.rentInfoList);
  paymentInfoList.value = cloneData(lastConfirmedFilters.value.paymentInfoList);
  orderTypeInfoList.value = cloneData(
    lastConfirmedFilters.value.orderTypeInfoList
  );
}

const onConfirmHandle = () => {
  saveCurrentFilters();
  menuRef.value?.close();
  props.confirmCallback && props.confirmCallback(formData.value);
};

const onCancelHandle = () => {
  restoreLastConfirmedFilters();
  menuRef.value?.close();
};

onMounted(async () => {
  await Promise.all([getProvinceListHandle(), getPaymentListHandle()]);
  saveCurrentFilters();
});
</script>

<style scoped lang="less">
.search-filter-shell {
  position: relative;
  z-index: 1;
  padding: 0;
  border: 0;
  border-radius: 0;
  background: transparent;
  box-shadow: none;
}

.search-filter-shell__intro {
  display: none;
}

.search-filter-shell__eyebrow {
  color: #29523d;
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.18em;
  text-transform: uppercase;
}

.search-filter-shell__text {
  margin: 0;
  color: rgba(17, 41, 31, 0.66);
  font-size: 12px;
  line-height: 1.6;
}

.filter-panel {
  padding: 16px;
  background: linear-gradient(180deg, #fffdf9, #f5eee2);
}

.filter-panel__areas {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
  height: 34vh;
}

.filter-column {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding-right: 4px;
  overflow: auto;
}

.filter-option {
  padding: 10px 12px;
  border: 1px solid rgba(17, 41, 31, 0.08);
  border-radius: 16px;
  background: rgba(255, 252, 246, 0.9);
  color: rgba(17, 41, 31, 0.72);
  font-size: 12px;
  transition: all 0.2s ease;
}

.filter-option--active {
  border-color: rgba(32, 71, 54, 0.22);
  background: #163626;
  color: #f7f0df;
  box-shadow: 0 8px 18px rgba(22, 54, 38, 0.12);
}

.filter-chip-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.filter-chip {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 46px;
  padding: 10px 8px;
  border: 1px solid rgba(17, 41, 31, 0.08);
  border-radius: 18px;
  background: rgba(255, 252, 246, 0.9);
  color: rgba(17, 41, 31, 0.72);
  font-size: 12px;
  line-height: 1.4;
  text-align: center;
  transition: all 0.2s ease;
}

.filter-chip--active {
  border-color: rgba(32, 71, 54, 0.22);
  background: #163626;
  color: #f7f0df;
  box-shadow: 0 8px 18px rgba(22, 54, 38, 0.12);
}

.filter-actions {
  display: flex;
  gap: 12px;
  margin-top: 18px;
}

.filter-actions__button {
  flex: 1;
}

:deep(.search-filter-shell__menu .van-dropdown-menu) {
  background: transparent;
}

:deep(.search-filter-shell__menu .van-dropdown-menu__bar) {
  height: 56px;
  border-radius: 22px;
  background: rgba(255, 252, 246, 0.96);
  box-shadow:
    0 6px 18px rgba(35, 47, 36, 0.06),
    inset 0 0 0 1px rgba(17, 41, 31, 0.06);
  pointer-events: auto;
}

:deep(.search-filter-shell__menu .van-dropdown-menu__item) {
  color: #11291f;
  pointer-events: auto;
}

:deep(.search-filter-shell__menu .van-dropdown-menu__title) {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  color: #11291f;
  font-size: 14px;
  font-weight: 700;
  transition: color 0.2s ease;
}

:deep(.search-filter-shell__menu .van-dropdown-menu__title::after) {
  border-color: transparent transparent #987145 #987145;
}

:deep(.search-filter-shell__menu .van-dropdown-menu__title--active) {
  color: #29523d;
  text-shadow: 0 0 0 currentColor;
}

:deep(.search-filter-shell__menu .van-dropdown-item__content) {
  overflow: hidden;
  border: 1px solid rgba(17, 41, 31, 0.08);
  border-radius: 22px;
  box-shadow: 0 18px 34px rgba(35, 47, 36, 0.1);
}
</style>
