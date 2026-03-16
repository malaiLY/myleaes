<template>
  <van-skeleton :row="18" :loading="!apartmentDetailInfo?.id">
    <div class="appointment-detail-page">
      <section class="card appointment-section">
        <div class="appointment-section__head">
          <h2 class="appointment-section__title">预约公寓</h2>
          <span class="appointment-section__sub">公寓信息</span>
        </div>
        <ApartmentCard :data="apartmentDetailInfo" />
      </section>

      <section class="card appointment-section">
        <div class="appointment-section__head">
          <h2 class="appointment-section__title">预约信息</h2>
          <span class="appointment-section__sub">填写资料</span>
        </div>

        <van-form @submit="onSubmit" class="appointment-detail-page__form">
          <van-cell-group inset class="appointment-detail-page__form-group">
            <van-field
              v-model.trim="formData.name"
              name="name"
              label="姓名"
              autocomplete="off"
              placeholder="请输入姓名"
              :rules="[{ required: true, message: '请填写姓名' }]"
            />

            <van-field
              v-model.trim="formData.phone"
              name="phone"
              label="手机号"
              type="tel"
              autocomplete="off"
              placeholder="请输入手机号"
              :rules="[
                {
                  required: true,
                  pattern: /^1([3589]\\d|4[5-9]|6[1-2,4-7]|7[0-8])\\d{8}$/,
                  message: '请正确填写手机号'
                }
              ]"
            />

            <van-field
              v-model="formData.date"
              is-link
              readonly
              name="date"
              label="预约日期"
              placeholder="点击选择日期"
              @click="showDate = true"
              :rules="[{ required: true, message: '请选择预约日期' }]"
            />
            <van-popup v-model:show="showDate" position="bottom">
              <van-date-picker
                :min-date="new Date()"
                @confirm="onConfirmAppointmentDateHandle"
                @cancel="showDate = false"
              />
            </van-popup>

            <van-field
              v-model="formData.time"
              is-link
              readonly
              name="time"
              label="预约时间"
              placeholder="点击选择时间"
              @click="showTime = true"
              :rules="[{ required: true, message: '请选择预约时间' }]"
            />
            <van-popup v-model:show="showTime" position="bottom">
              <van-time-picker
                v-model="dateInfo.time"
                title="选择时间"
                :columns-type="['hour', 'minute', 'second']"
                @confirm="onConfirmAppointmentTimeHandle"
                @cancel="showDate = false"
              />
            </van-popup>

            <van-field
              v-model.trim="formData.additionalInfo"
              name="additionalInfo"
              label="备注信息"
              rows="3"
              autosize
              type="textarea"
              autocomplete="off"
              placeholder="请输入备注信息"
              show-word-limit
              maxlength="50"
            />
          </van-cell-group>

          <div class="appointment-detail-page__submit-wrap">
            <van-button
              round
              block
              type="primary"
              native-type="submit"
              class="appointment-detail-page__submit"
            >
              {{ formData.id ? "重新预约" : "预约看房" }}
            </van-button>
          </div>
        </van-form>
      </section>
    </div>
  </van-skeleton>
</template>

<script setup lang="ts">
import {
  getApartmentDetailById,
  getAppointmentDetailById,
  saveOrUpdateAppointment
} from "@/api/search";
import type {
  ApartmentInterface,
  AppointmentQueryInterface
} from "@/api/search/types";
import ApartmentCard from "@/components/ApartmentCard/ApartmentCard.vue";
import {
  AppointmentStatus,
  AppointmentStatusMap,
  getLabelByValue
} from "@/enums/constEnums";
import dayjs from "dayjs";
import { showToast } from "vant";
import { computed, onMounted, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();

const apartmentDetailInfo = ref<ApartmentInterface>({} as ApartmentInterface);

const getApartmentDetailHandle = async () => {
  const { data } = await getApartmentDetailById(route.query.apartmentId as string);
  apartmentDetailInfo.value = data;
};

const formData = ref<AppointmentQueryInterface & { date: string; time: string }>({
  id: "",
  name: "",
  phone: "",
  apartmentId: (route.query.apartmentId as string) || "",
  appointmentTime: "",
  additionalInfo: "",
  appointmentStatus: AppointmentStatus.WAITING,
  date: "",
  time: ""
});

const dateInfo = ref({
  date: [
    dayjs().get("year").toString(),
    (dayjs().get("month") + 1).toString().padStart(2, "0"),
    dayjs().get("date").toString().padStart(2, "0")
  ],
  time: [
    dayjs().get("hour").toString().padStart(2, "0"),
    dayjs().get("minute").toString().padStart(2, "0"),
    dayjs().get("second").toString().padStart(2, "0")
  ]
});

watch(
  dateInfo,
  newVal => {
    formData.value.date = newVal.date.join("-");
    formData.value.time = `${newVal.time.join(":")}`;
    formData.value.appointmentTime = `${formData.value.date} ${formData.value.time}`;
  },
  { immediate: true, deep: true }
);

const showDate = ref(false);
const showTime = ref(false);

const onConfirmAppointmentDateHandle = (date: any) => {
  dateInfo.value.date = date.selectedValues;
  showDate.value = false;
};

const onConfirmAppointmentTimeHandle = (_date: any) => {
  showTime.value = false;
};

async function onSubmit(values: any) {
  await saveOrUpdateAppointment(formData.value);
  showToast("操作成功");
  route.query.apartmentId
    ? await router.replace("/myAppointment")
    : await router.back();
}

async function getAppointmentDetailHandle() {
  const { data } = await getAppointmentDetailById(route.query.appointmentId as string);
  Object.keys(formData.value).forEach(key => {
    (formData.value as any)[key] = (data as any)[key];
  });
  dateInfo.value.date = data.appointmentTime.split(" ")[0].split("-");
  dateInfo.value.time = data.appointmentTime.split(" ")[1].split(":");
  apartmentDetailInfo.value = data.apartmentItemVo;
}

onMounted(async () => {
  route.query.apartmentId && (await getApartmentDetailHandle());
  route.query.appointmentId && (await getAppointmentDetailHandle());
});
</script>

<style scoped lang="less">
.appointment-detail-page {
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
    radial-gradient(circle at top, rgba(184, 135, 77, 0.14), transparent 30%),
    linear-gradient(180deg, #f8f3ea 0%, var(--appointment-bg) 100%);
}

.card {
  padding: 18px;
  border: 1px solid var(--appointment-border);
  border-radius: 24px;
  background: var(--appointment-surface);
  box-shadow:
    0 1px 2px rgba(35, 47, 36, 0.04),
    0 16px 34px rgba(35, 47, 36, 0.06);
}

.appointment-section {
  margin-top: 0;
}

.appointment-section__head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 14px;
}

.appointment-section__title {
  margin: 0;
  color: var(--appointment-ink);
  font-size: 18px;
  font-weight: 700;
}

.appointment-section__sub {
  color: rgba(109, 123, 115, 0.78);
  font-size: 12px;
}

.appointment-detail-page__form-group {
  overflow: hidden;
  border-radius: 20px;
}

.appointment-detail-page__form-group :deep(.van-cell-group--inset) {
  margin: 0;
}

.appointment-detail-page__submit-wrap {
  margin-top: 20px;
}

.appointment-detail-page__submit {
  height: 46px;
  border: none;
  background: linear-gradient(135deg, #204736, #2f684e);
  box-shadow: 0 10px 22px rgba(32, 71, 54, 0.2);
}

@media (max-width: 375px) {
  .appointment-detail-page {
    padding: 12px;
  }

}
</style>
