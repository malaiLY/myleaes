<template>
  <div class="layout-admin-wrapper">
    <div class="layout-container-vertical fixed">
      <!-- sideBar -->
      <LayoutSideBar />
      <div class="layout-main" :class="{ 'is-collapse': collapse }">
        <!-- Header -->
        <div
          class="layout-header fixed-header"
          :class="{ 'is-collapse': collapse }"
        >
          <LayoutNavBar />
          <LayoutTabsBar />
        </div>
        <div class="app-main-container">
          <!-- Main -->
          <LayoutMain />
          <!-- Footer -->
          <LayoutFooter />
        </div>
      </div>
    </div>
    <!-- 主题切换 -->
    <ThemeDrawer />
  </div>
</template>

<script lang="ts">
import { defineComponent, onBeforeUnmount, computed, ref } from 'vue'
import { useSettingsStore } from '@/store/modules/settings'
import { useDebounceFn } from '@vueuse/core'
import LayoutFooter from './Footer/index.vue'
import LayoutMain from './Main/index.vue'
import LayoutSideBar from './SideBar/index.vue'
import LayoutNavBar from './NavBar/index.vue'
import LayoutTabsBar from './TabsBar/index.vue'
import ThemeDrawer from './NavBar/components/ThemeDrawer/index.vue'
export default defineComponent({
  components: {
    LayoutFooter,
    LayoutMain,
    LayoutSideBar,
    LayoutNavBar,
    LayoutTabsBar,
    ThemeDrawer,
  },
  setup() {
    const settingsStore = useSettingsStore()
    const collapse = computed(() => settingsStore.collapse)
    // 监听窗口大小变化，折叠侧边栏
    const screenWidth = ref(0)
    const listeningWindow = useDebounceFn(() => {
      screenWidth.value = document.body.clientWidth
      if (!collapse.value && screenWidth.value < 1200)
        settingsStore.changeCollapse()
      if (collapse.value && screenWidth.value > 1200)
        settingsStore.changeCollapse()
    }, 100)

    window.addEventListener('resize', listeningWindow, false)

    onBeforeUnmount(() => {
      window.removeEventListener('resize', listeningWindow)
    })
    return {
      collapse,
    }
  },
})
</script>

<style scoped lang="scss">
@mixin fix-header {
  position: fixed;
  top: 0;
  right: 10px;
  z-index: $base-z-index - 2;
  box-sizing: border-box;
  width: calc(100% - #{$base-left-menu-width} - 20px);
}

.layout-admin-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: auto;
  background:
    radial-gradient(circle at top left, rgb(59 130 246 / 0.12), transparent 20%),
    linear-gradient(180deg, #f8fbff 0%, #eef4fb 100%);

  .layout-container-vertical {
    &.fixed {
      padding-top: calc(#{$base-top-bar-height} + #{$base-tabs-bar-height});
    }

    .layout-main {
      min-height: 100%;
      margin-left: calc(#{$base-left-menu-width} + 20px);
      transition: all $base-transition-time ease;

      &.is-collapse {
        margin-left: calc(#{$base-left-menu-width-min} + 20px);
        border-right: 0;
      }

      .layout-header {
        &.fixed-header {
          @include fix-header;
          padding: 12px 20px 0;
        }

        &.is-collapse {
          width: calc(100% - #{$base-left-menu-width-min} - 20px);
        }
      }

      .app-main-container {
        display: flex;
        flex-direction: column;
        gap: 18px;
        min-height: calc(100vh - #{$base-header-height});
        padding: 20px;
        background: linear-gradient(180deg, #f8fbff 0%, #f1f5f9 100%);
      }
    }
  }
}

@media (max-width: 768px) {
  .layout-admin-wrapper {
    .layout-container-vertical {
      .layout-main {
        .layout-header {
          &.fixed-header {
            padding: 10px 12px 0;
          }
        }

        .app-main-container {
          padding: 12px;
          gap: 12px;
        }
      }
    }
  }
}
</style>
