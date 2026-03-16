<template>
  <div class="home-page" v-loading="loading">
    <section class="metrics-grid">
      <el-card
        v-for="item in metricCards"
        :key="item.label"
        class="metric-card"
        :class="item.tone"
        shadow="hover"
        @click="openRoute(item.path)"
      >
        <div class="metric-head">
          <p class="metric-label">{{ item.label }}</p>
        </div>
        <strong class="metric-value">{{ item.value }}</strong>
        <p class="metric-desc">{{ item.desc }}</p>
        <div class="metric-foot">
          <span>{{ item.footLabel }}</span>
          <strong>{{ item.footValue }}</strong>
        </div>
        <div class="metric-progress">
          <span :style="{ width: `${item.progress}%` }"></span>
        </div>
      </el-card>
    </section>

    <section class="board-grid">
      <el-card class="panel-card chart-panel panel-major" shadow="hover">
        <template #header>
          <div class="panel-header">
            <div>
              <p class="panel-title">房间状态分布</p>
              <p class="panel-subtitle">首页主图只保留最核心的库存结构判断</p>
            </div>
            <button
              class="panel-extra panel-action"
              type="button"
              @click="openRoute('/apartmentManagement/roomManagement/roomManagement')"
            >
              查看房间
            </button>
          </div>
        </template>

        <VChart class="dashboard-chart dashboard-chart-major" :option="roomStatusChartOption" autoresize />
        <div class="chart-summary chart-summary-split">
          <div class="chart-stat">
            <span>发布率</span>
            <strong>{{ formatPercent(roomReleaseRate) }}</strong>
            <p>已发布 {{ releasedRoomCount }} 间，未发布 {{ unreleasedRoomCount }} 间</p>
          </div>
          <div class="chart-stat">
            <span>入住率</span>
            <strong>{{ formatPercent(occupancyRate) }}</strong>
            <p>已入住 {{ checkedInRoomCount }} 间，空置 {{ vacantRoomCount }} 间</p>
          </div>
        </div>
      </el-card>

      <el-card class="panel-card chart-panel panel-side panel-side-top" shadow="hover">
        <template #header>
          <div class="panel-header">
            <div>
              <p class="panel-title">近 7 天预约趋势</p>
              <p class="panel-subtitle">看房节奏变化</p>
            </div>
            <button
              class="panel-extra panel-action"
              type="button"
              @click="openRoute('/rentManagement/appointment/appointment')"
            >
              查看预约
            </button>
          </div>
        </template>

        <VChart class="dashboard-chart dashboard-chart-compact" :option="appointmentTrendChartOption" autoresize />
        <div class="chart-summary">
          <div class="chart-stat">
            <span>今日预约</span>
            <strong>{{ todayAppointmentCount }}</strong>
            <p>今日新增预约记录</p>
          </div>
          <div class="chart-stat">
            <span>待看预约</span>
            <strong>{{ waitingAppointmentCount }}</strong>
            <p>仍需继续跟进处理</p>
          </div>
        </div>
      </el-card>

      <el-card class="panel-card chart-panel panel-side panel-side-bottom" shadow="hover">
        <template #header>
          <div class="panel-header">
            <div>
              <p class="panel-title">未来 30 天租约到期分布</p>
              <p class="panel-subtitle">到期压力分布</p>
            </div>
            <button
              class="panel-extra panel-action"
              type="button"
              @click="openRoute('/agreementManagement/agreement/agreement')"
            >
              查看租约
            </button>
          </div>
        </template>

        <VChart
          class="dashboard-chart dashboard-chart-compact"
          :option="agreementExpiryChartOption"
          autoresize
        />
        <div class="chart-summary chart-summary-split">
          <div class="chart-stat">
            <span>待确认租约</span>
            <strong>{{ pendingAgreementCount }}</strong>
            <p>待确认、待续约确认的租约总量</p>
          </div>
          <div class="chart-stat">
            <span>30 天内到期</span>
            <strong>{{ expiringAgreementCount }}</strong>
            <p>未来 30 天需重点关注的租约数量</p>
          </div>
        </div>
      </el-card>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import VChart from 'vue-echarts'
import { useRouter } from 'vue-router'
import { use } from 'echarts/core'
import type { EChartsOption } from 'echarts'
import { BarChart, LineChart } from 'echarts/charts'
import { GridComponent, LegendComponent, TooltipComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import {
  getApartmentList,
  getRoomList,
} from '@/api/apartmentManagement'
import type {
  ApartmentInterface,
  RoomInterface,
} from '@/api/apartmentManagement/types'
import {
  getAgreementInfoList,
  getAppointmentInfoList,
} from '@/api/rentManagement'
import type {
  AgreementInfoInterface,
  AppointmentInfoInterface,
} from '@/api/rentManagement/types'
import {
  AgreementStatus,
  AppointmentStatus,
} from '@/enums/constEnums'

use([BarChart, LineChart, GridComponent, LegendComponent, TooltipComponent, CanvasRenderer])

type PageLike<T> = {
  records?: T[] | T
  total?: number
}

type AppointmentTrendPoint = {
  key: string
  label: string
  total: number
  waiting: number
}

type ExpiryBucket = {
  label: string
  count: number
}

const router = useRouter()

const loading = ref(false)

const apartments = ref<ApartmentInterface[]>([])
const rooms = ref<RoomInterface[]>([])
const appointments = ref<AppointmentInfoInterface[]>([])
const agreements = ref<AgreementInfoInterface[]>([])

function normalizeRecords<T>(records: T[] | T | undefined): T[] {
  if (Array.isArray(records)) return records
  return records ? [records] : []
}

async function fetchAllPages<T>(
  fetchPage: (pageNum: number, pageSize: number) => Promise<{ data: PageLike<T> }>,
  pageSize = 200,
) {
  const first = await fetchPage(1, pageSize)
  const firstRecords = normalizeRecords<T>(first.data.records)
  const total = Number(first.data.total ?? firstRecords.length)
  const totalPages = total > 0 ? Math.ceil(total / pageSize) : 1

  if (totalPages === 1) return firstRecords

  const rest = await Promise.all(
    Array.from({ length: totalPages - 1 }, (_, index) =>
      fetchPage(index + 2, pageSize),
    ),
  )

  return rest.reduce<T[]>(
    (all, current) => all.concat(normalizeRecords<T>(current.data.records)),
    firstRecords,
  )
}

function toNumber(value: string | number | undefined) {
  return Number(value || 0)
}

function getPercent(numerator: number, denominator: number) {
  if (!denominator) return 0
  return Math.round((numerator / denominator) * 100)
}

function formatPercent(value: number) {
  return `${Math.max(0, Math.min(100, Math.round(value)))}%`
}

function startOfDay(date: Date) {
  return new Date(date.getFullYear(), date.getMonth(), date.getDate())
}

function parseDate(value?: string) {
  if (!value) return null
  const date = new Date(value)
  return Number.isNaN(date.getTime()) ? null : date
}

function formatDateKey(date: Date) {
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${date.getFullYear()}-${month}-${day}`
}

function formatDateLabel(date: Date) {
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${month}.${day}`
}

function openRoute(path: string) {
  router.push(path)
}

function isToday(dateText?: string) {
  const target = parseDate(dateText)
  if (!target) return false
  return startOfDay(target).getTime() === startOfDay(new Date()).getTime()
}

function getFutureDayDiff(dateText?: string) {
  const target = parseDate(dateText)
  if (!target) return null
  return Math.ceil(
    (startOfDay(target).getTime() - startOfDay(new Date()).getTime()) /
      (1000 * 60 * 60 * 24),
  )
}

const releasedApartmentCount = computed(
  () => apartments.value.filter((item) => toNumber(item.isRelease) === 1).length,
)
const releasedRoomCount = computed(
  () => rooms.value.filter((item) => toNumber(item.isRelease) === 1).length,
)
const unreleasedRoomCount = computed(
  () => rooms.value.filter((item) => toNumber(item.isRelease) !== 1).length,
)
const checkedInRoomCount = computed(
  () => rooms.value.filter((item) => item.isCheckIn).length,
)
const vacantRoomCount = computed(
  () => rooms.value.filter((item) => !item.isCheckIn).length,
)

const waitingAppointmentCount = computed(
  () =>
    appointments.value.filter(
      (item) => Number(item.appointmentStatus) === AppointmentStatus.WAITING,
    ).length,
)
const todayAppointmentCount = computed(
  () => appointments.value.filter((item) => isToday(item.appointmentTime)).length,
)

const expiringAgreementCount = computed(
  () =>
    agreements.value.filter((item) => {
      const dayDiff = getFutureDayDiff(item.leaseEndDate)
      return dayDiff !== null && dayDiff >= 0 && dayDiff <= 30
    }).length,
)
const pendingAgreementCount = computed(
  () =>
    agreements.value.filter((item) =>
      [
        AgreementStatus.WAITING,
        AgreementStatus.TO_BE_CONFIRMED,
        AgreementStatus.RENEW_TO_BE_CONFIRMED,
      ].includes(Number(item.status) as AgreementStatus),
    ).length,
)
const apartmentReleaseRate = computed(() =>
  getPercent(releasedApartmentCount.value, apartments.value.length),
)
const roomReleaseRate = computed(() =>
  getPercent(releasedRoomCount.value, rooms.value.length),
)
const occupancyRate = computed(() =>
  getPercent(checkedInRoomCount.value, rooms.value.length),
)

const metricCards = computed(() => [
  {
    label: '公寓总数',
    value: apartments.value.length,
    desc: `已发布 ${releasedApartmentCount.value} 个公寓`,
    footLabel: '发布率',
    footValue: formatPercent(apartmentReleaseRate.value),
    progress: apartmentReleaseRate.value,
    tone: 'tone-blue',
    path: '/apartmentManagement/apartmentManagement/apartmentManagement',
  },
  {
    label: '房间总数',
    value: rooms.value.length,
    desc: `空置 ${vacantRoomCount.value} 间房间`,
    footLabel: '入住率',
    footValue: formatPercent(occupancyRate.value),
    progress: occupancyRate.value,
    tone: 'tone-cyan',
    path: '/apartmentManagement/roomManagement/roomManagement',
  },
  {
    label: '待看预约',
    value: waitingAppointmentCount.value,
    desc: `今日预约 ${todayAppointmentCount.value} 条`,
    footLabel: '处理状态',
    footValue: waitingAppointmentCount.value ? '需跟进' : '平稳',
    progress: waitingAppointmentCount.value
      ? Math.min(waitingAppointmentCount.value * 18, 100)
      : 8,
    tone: 'tone-amber',
    path: '/rentManagement/appointment/appointment',
  },
  {
    label: '到期租约',
    value: expiringAgreementCount.value,
    desc: `待确认 ${pendingAgreementCount.value} 条`,
    footLabel: '处理状态',
    footValue: pendingAgreementCount.value ? '待处理' : '平稳',
    progress: expiringAgreementCount.value
      ? Math.min(expiringAgreementCount.value * 16, 100)
      : 10,
    tone: 'tone-rose',
    path: '/agreementManagement/agreement/agreement',
  },
])

const appointmentTrend = computed<AppointmentTrendPoint[]>(() => {
  const today = startOfDay(new Date())
  const days = Array.from({ length: 7 }, (_, index) => {
    const date = new Date(today)
    date.setDate(today.getDate() - (6 - index))
    return {
      key: formatDateKey(date),
      label: formatDateLabel(date),
      total: 0,
      waiting: 0,
    }
  })

  const dayMap = new Map(days.map((item) => [item.key, item]))

  appointments.value.forEach((item) => {
    const target = parseDate(item.appointmentTime)
    if (!target) return
    const current = dayMap.get(formatDateKey(target))
    if (!current) return
    current.total += 1
    if (Number(item.appointmentStatus) === AppointmentStatus.WAITING) {
      current.waiting += 1
    }
  })

  return days
})

const agreementExpiryBuckets = computed<ExpiryBucket[]>(() => {
  const buckets = [
    { label: '7天内', min: 0, max: 6, count: 0 },
    { label: '8-14天', min: 7, max: 13, count: 0 },
    { label: '15-21天', min: 14, max: 20, count: 0 },
    { label: '22-30天', min: 21, max: 30, count: 0 },
  ]

  agreements.value.forEach((item) => {
    const dayDiff = getFutureDayDiff(item.leaseEndDate)
    if (dayDiff === null || dayDiff < 0 || dayDiff > 30) return
    const targetBucket = buckets.find(
      (bucket) => dayDiff >= bucket.min && dayDiff <= bucket.max,
    )
    if (targetBucket) targetBucket.count += 1
  })

  return buckets.map(({ label, count }) => ({ label, count }))
})

const roomStatusChartOption = computed<EChartsOption>(() => ({
  animationDuration: 500,
  color: ['#2563eb', '#38bdf8', '#10b981', '#f59e0b'],
  tooltip: {
    trigger: 'axis',
    axisPointer: { type: 'shadow' },
  },
  grid: {
    top: 24,
    right: 20,
    bottom: 24,
    left: 24,
    containLabel: true,
  },
  xAxis: {
    type: 'category',
    data: ['已发布', '未发布', '已入住', '空置'],
    axisTick: { show: false },
    axisLine: { lineStyle: { color: '#dbe4ef' } },
    axisLabel: { color: '#64748b' },
  },
  yAxis: {
    type: 'value',
    minInterval: 1,
    axisLabel: { color: '#94a3b8' },
    splitLine: { lineStyle: { color: '#e2e8f0', type: 'dashed' } },
  },
  series: [
    {
      type: 'bar',
      barWidth: 28,
      data: [
        releasedRoomCount.value,
        unreleasedRoomCount.value,
        checkedInRoomCount.value,
        vacantRoomCount.value,
      ],
      label: {
        show: true,
        position: 'top',
        color: '#0f172a',
        fontWeight: 700,
      },
      itemStyle: {
        borderRadius: [10, 10, 0, 0],
      },
    },
  ],
}))

const appointmentTrendChartOption = computed<EChartsOption>(() => ({
  animationDuration: 500,
  color: ['#2563eb', '#f59e0b'],
  tooltip: {
    trigger: 'axis',
  },
  legend: {
    top: 0,
    right: 0,
    icon: 'roundRect',
    itemWidth: 10,
    itemHeight: 10,
    textStyle: {
      color: '#64748b',
    },
  },
  grid: {
    top: 42,
    right: 18,
    bottom: 24,
    left: 24,
    containLabel: true,
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: appointmentTrend.value.map((item) => item.label),
    axisTick: { show: false },
    axisLine: { lineStyle: { color: '#dbe4ef' } },
    axisLabel: { color: '#64748b' },
  },
  yAxis: {
    type: 'value',
    minInterval: 1,
    axisLabel: { color: '#94a3b8' },
    splitLine: { lineStyle: { color: '#e2e8f0', type: 'dashed' } },
  },
  series: [
    {
      name: '预约总量',
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      data: appointmentTrend.value.map((item) => item.total),
      lineStyle: { width: 3 },
      areaStyle: {
        color: 'rgba(37, 99, 235, 0.12)',
      },
    },
    {
      name: '待看预约',
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      data: appointmentTrend.value.map((item) => item.waiting),
      lineStyle: { width: 3 },
      areaStyle: {
        color: 'rgba(245, 158, 11, 0.12)',
      },
    },
  ],
}))

const agreementExpiryChartOption = computed<EChartsOption>(() => ({
  animationDuration: 500,
  color: ['#e11d48'],
  tooltip: {
    trigger: 'axis',
    axisPointer: { type: 'shadow' },
  },
  grid: {
    top: 24,
    right: 18,
    bottom: 24,
    left: 24,
    containLabel: true,
  },
  xAxis: {
    type: 'category',
    data: agreementExpiryBuckets.value.map((item) => item.label),
    axisTick: { show: false },
    axisLine: { lineStyle: { color: '#dbe4ef' } },
    axisLabel: { color: '#64748b' },
  },
  yAxis: {
    type: 'value',
    minInterval: 1,
    axisLabel: { color: '#94a3b8' },
    splitLine: { lineStyle: { color: '#e2e8f0', type: 'dashed' } },
  },
  series: [
    {
      type: 'bar',
      barWidth: 42,
      data: agreementExpiryBuckets.value.map((item) => item.count),
      label: {
        show: true,
        position: 'top',
        color: '#0f172a',
        fontWeight: 700,
      },
      itemStyle: {
        borderRadius: [14, 14, 0, 0],
        color: '#f43f5e',
      },
    },
  ],
}))

async function loadDashboard() {
  loading.value = true

  const tasks = await Promise.allSettled([
    fetchAllPages<ApartmentInterface>((pageNum, pageSize) =>
      getApartmentList({ pageNum, pageSize }),
    ),
    fetchAllPages<RoomInterface>((pageNum, pageSize) =>
      getRoomList({ pageNum, pageSize }),
    ),
    fetchAllPages<AppointmentInfoInterface>((pageNum, pageSize) =>
      getAppointmentInfoList({ pageNum, pageSize }),
    ),
    fetchAllPages<AgreementInfoInterface>((pageNum, pageSize) =>
      getAgreementInfoList({ pageNum, pageSize }),
    ),
  ])

  if (tasks[0].status === 'fulfilled') apartments.value = tasks[0].value
  if (tasks[1].status === 'fulfilled') rooms.value = tasks[1].value
  if (tasks[2].status === 'fulfilled') appointments.value = tasks[2].value
  if (tasks[3].status === 'fulfilled') agreements.value = tasks[3].value
  loading.value = false
}

onMounted(() => {
  loadDashboard()
})
</script>

<style scoped lang="scss">
.home-page {
  min-height: 100%;
  padding: 24px;
  background:
    radial-gradient(circle at top left, rgb(59 130 246 / 0.16), transparent 28%),
    radial-gradient(circle at bottom right, rgb(14 165 233 / 0.14), transparent 24%),
    linear-gradient(180deg, #f8fbff 0%, #f1f5f9 100%);
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 18px;
}

.metric-card {
  position: relative;
  overflow: hidden;
  border: 1px solid rgb(226 232 240 / 0.95);
  border-radius: 22px;
  box-shadow: 0 14px 32px rgb(15 23 42 / 0.06);
  cursor: pointer;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease,
    border-color 0.2s ease;
}

.metric-card:hover {
  transform: translateY(-2px);
  border-color: #bfdbfe;
  box-shadow: 0 18px 34px rgb(59 130 246 / 0.12);
}

.metric-card::after {
  position: absolute;
  inset: auto -28px -32px auto;
  width: 120px;
  height: 120px;
  content: '';
  background: radial-gradient(circle, rgb(255 255 255 / 0.8), transparent 68%);
  opacity: 0.5;
}

.metric-head,
.metric-foot {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.metric-label {
  margin: 0;
  font-size: 13px;
  color: #64748b;
}

.metric-value {
  position: relative;
  z-index: 1;
  display: block;
  margin-top: 12px;
  font-size: 30px;
  line-height: 1;
  color: #0f172a;
}

.metric-desc {
  position: relative;
  z-index: 1;
  margin: 12px 0 0;
  font-size: 13px;
  line-height: 1.6;
  color: #64748b;
}

.metric-foot {
  margin-top: 16px;
  font-size: 12px;
  color: #64748b;
}

.metric-foot strong {
  font-size: 13px;
  color: #0f172a;
}

.metric-progress {
  position: relative;
  z-index: 1;
  height: 8px;
  margin-top: 12px;
  overflow: hidden;
  background: rgb(255 255 255 / 0.7);
  border-radius: 999px;
}

.metric-progress span {
  display: block;
  height: 100%;
  border-radius: inherit;
}

.tone-blue {
  background: linear-gradient(180deg, #f8fbff, #eef6ff);
}

.tone-blue .metric-progress span {
  background: linear-gradient(90deg, #38bdf8, #2563eb);
}

.tone-cyan {
  background: linear-gradient(180deg, #f5fcff, #edfafe);
}

.tone-cyan .metric-progress span {
  background: linear-gradient(90deg, #22d3ee, #0891b2);
}

.tone-green {
  background: linear-gradient(180deg, #f4fdf7, #ecfdf5);
}

.tone-green .metric-progress span {
  background: linear-gradient(90deg, #34d399, #059669);
}

.tone-amber {
  background: linear-gradient(180deg, #fffaf0, #fffbeb);
}

.tone-amber .metric-progress span {
  background: linear-gradient(90deg, #fbbf24, #d97706);
}

.tone-rose {
  background: linear-gradient(180deg, #fff5f7, #fff1f2);
}

.tone-rose .metric-progress span {
  background: linear-gradient(90deg, #fb7185, #e11d48);
}

.board-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.3fr) minmax(320px, 0.9fr);
  grid-template-areas:
    'major side-top'
    'major side-bottom';
  gap: 20px;
  margin-top: 20px;
}

.panel-card {
  overflow: hidden;
  border: 1px solid rgb(226 232 240 / 0.95);
  border-radius: 24px;
  box-shadow: 0 14px 32px rgb(15 23 42 / 0.06);
}

.panel-major {
  grid-area: major;
}

.panel-side-top {
  grid-area: side-top;
}

.panel-side-bottom {
  grid-area: side-bottom;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.panel-title {
  margin: 0;
  font-size: 18px;
  font-weight: 700;
  color: #0f172a;
}

.panel-subtitle {
  margin: 6px 0 0;
  font-size: 13px;
  color: #64748b;
}

.panel-extra {
  flex-shrink: 0;
  padding: 8px 12px;
  font-size: 12px;
  color: #0369a1;
  background: #e0f2fe;
  border: none;
  border-radius: 999px;
}

.panel-action {
  cursor: pointer;
  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease,
    background-color 0.2s ease;
}

.panel-action:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 20px rgb(14 165 233 / 0.18);
}

.chart-panel :deep(.el-card__body) {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.dashboard-chart {
  width: 100%;
  height: 300px;
}

.dashboard-chart-major {
  height: 360px;
}

.dashboard-chart-compact {
  height: 220px;
}

.chart-summary {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.chart-stat {
  padding: 16px 18px;
  background: linear-gradient(180deg, #fff, #f8fafc);
  border: 1px solid #e2e8f0;
  border-radius: 18px;
}

.chart-stat span {
  display: block;
  font-size: 13px;
  color: #64748b;
}

.chart-stat strong {
  display: block;
  margin-top: 10px;
  font-size: 28px;
  line-height: 1;
  color: #0f172a;
}

.chart-stat p {
  margin: 10px 0 0;
  font-size: 13px;
  line-height: 1.6;
  color: #64748b;
}

@media (max-width: 1400px) {
  .metrics-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 1200px) {
  .board-grid {
    grid-template-columns: 1fr;
    grid-template-areas:
      'major'
      'side-top'
      'side-bottom';
  }
}

@media (max-width: 768px) {
  .chart-summary {
    grid-template-columns: 1fr;
  }

  .dashboard-chart,
  .dashboard-chart-major,
  .dashboard-chart-compact {
    height: 280px;
  }

  .panel-header {
    align-items: flex-start;
    flex-direction: column;
  }
}

@media (max-width: 768px) {
  .home-page {
    padding: 16px;
  }

  .metrics-grid {
    grid-template-columns: 1fr;
  }
}
</style>
