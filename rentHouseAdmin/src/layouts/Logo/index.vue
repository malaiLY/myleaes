<template>
  <div class="logo-container flex-center">
    <a href="/">
      <h1 class="title" v-if="!collapse">
        {{ getEnvByName('VITE_APP_TITLE') }}
      </h1>
    </a>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed } from 'vue'
import { useSettingsStore } from '@/store/modules/settings'
import { getEnvByName } from '@/utils/getEnv'
export default defineComponent({
  methods: { getEnvByName },
  setup() {
    const settingsStore = useSettingsStore()
    const collapse = computed(() => settingsStore.collapse)
    return { collapse }
  },
})
</script>

<style scoped lang="scss">
.logo-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 64px;
  padding: 0 12px 6px;
  overflow: hidden;
  background: transparent;

  a {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    min-width: 0;
    height: 100%;
    padding: 0 12px;
    text-decoration: none;
    background: linear-gradient(180deg, rgb(255 255 255 / 0.72), rgb(237 242 247 / 0.94));
    border: 1px solid rgb(203 213 225 / 0.82);
    border-radius: 18px;
    box-shadow:
      inset 0 1px 0 rgb(255 255 255 / 0.56),
      0 8px 18px rgb(148 163 184 / 0.12);
  }

  .title {
    display: inline-block;
    overflow: hidden;
    font-family: 'Segoe UI', 'PingFang SC', Arial, sans-serif;
    font-size: 17px;
    font-weight: 700;
    color: #0f172a;
    letter-spacing: 0.5px;
    text-overflow: ellipsis;
    vertical-align: middle;
    white-space: nowrap;
  }

}
</style>
