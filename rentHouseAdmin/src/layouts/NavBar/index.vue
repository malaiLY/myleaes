<!--
 * @Description: 
 * @Autor: 李海波
 * @Date: 2023-03-03 13:41:05
 * @LastEditors: 1547702880@@qq.com
 * @LastEditTime: 2023-03-15 17:20:10
-->
<template>
  <div class="nav-bar-container">
    <div class="left-panel">
      <div class="collapse-trigger" @click="handleCollapse">
        <el-icon class="fold-unfold">
          <component :is="collapse ? 'Expand' : 'Fold'"></component>
        </el-icon>
      </div>
      <Breadcrumb />
    </div>
    <div class="right-panel">
      <Refresh />
      <ScreenFull />
      <Settings />
      <User />
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed } from 'vue'
import { useSettingsStore } from '@/store/modules/settings'
import User from './components/User/index.vue'
import Breadcrumb from './components/Breadcrumb/index.vue'
import Refresh from './components/Refresh/index.vue'
import ScreenFull from './components/ScreeFull/index.vue'
import Settings from './components/Settings/index.vue'
export default defineComponent({
  components: {
    Refresh,
    User,
    ScreenFull,
    Breadcrumb,
    Settings,
  },
  setup() {
    const settingsStore = useSettingsStore()

    const collapse = computed(() => settingsStore.collapse)

    function handleCollapse() {
      settingsStore.changeCollapse()
    }
    return {
      collapse,
      handleCollapse,
    }
  },
})
</script>

<style scoped lang="scss">
.nav-bar-container {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  height: $base-nav-bar-height;
  padding: 0 18px;
  user-select: none;
  background: rgb(255 255 255 / 0.76);
  backdrop-filter: blur(18px);
  border: 1px solid rgb(255 255 255 / 0.78);
  border-radius: 22px 22px 0 0;
  box-shadow: 0 12px 36px rgb(15 23 42 / 0.06);

  .left-panel {
    display: flex;
    align-items: center;
    gap: 14px;
    min-width: 0;
    height: 100%;

    .collapse-trigger {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 38px;
      height: 38px;
      background: linear-gradient(180deg, #fff, #f8fafc);
      border: 1px solid #e2e8f0;
      border-radius: 12px;
      cursor: pointer;
      transition:
        transform 0.2s ease,
        border-color 0.2s ease,
        box-shadow 0.2s ease;
    }

    .collapse-trigger:hover {
      transform: translateY(-1px);
      border-color: #bfdbfe;
      box-shadow: 0 10px 24px rgb(59 130 246 / 0.12);
    }

    .fold-unfold {
      font-size: 18px;
      color: #475569;
      transition: color 0.2s ease;
    }

    .collapse-trigger:hover .fold-unfold {
      color: var(--el-color-primary);
    }
  }

  .right-panel {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    gap: 10px;
    height: 100%;
    padding-left: 12px;
  }
}

@media (max-width: 768px) {
  .nav-bar-container {
    gap: 10px;
    padding: 0 12px;
    border-radius: 18px 18px 0 0;

    .left-panel {
      flex: 1;
      min-width: 0;
    }

    .right-panel {
      gap: 6px;
      padding-left: 0;
    }
  }
}
</style>
