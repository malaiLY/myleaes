<template>
  <div class="layout-sidebar-container" :class="{ 'is-collapse': collapse }">
    <Logo />

    <el-scrollbar>
      <el-menu
        class="side-menu"
        :defaultActive="activeMenu"
        :collapse="collapse"
        :unique-opened="true"
        router
      >
        <el-menu-item index="/index">
          <el-icon>
            <component is="HomeFilled"></component>
          </el-icon>
          <template #title>
            <span>首页</span>
          </template>
        </el-menu-item>
        <el-sub-menu index="1">
          <template #title>
            <el-icon>
              <component is="Setting"></component>
            </el-icon>
            <span>系统管理</span>
          </template>
          <el-menu-item index="/system/user">
            <el-icon>
              <component is="UserFilled"></component>
            </el-icon>
            <template #title>
              <span>用户管理</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/system/post">
            <el-icon>
              <component is="Postcard"></component>
            </el-icon>
            <template #title>
              <span>岗位管理</span>
            </template>
          </el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="2">
          <template #title>
            <el-icon>
              <component is="OfficeBuilding"></component>
            </el-icon>
            <span>公寓管理</span>
          </template>
          <el-menu-item
            index="/apartmentManagement/apartmentManagement/apartmentManagement"
          >
            <el-icon>
              <component is="OfficeBuilding"></component>
            </el-icon>
            <template #title>
              <span>公寓管理</span>
            </template>
          </el-menu-item>
          <el-menu-item
            index="/apartmentManagement/roomManagement/roomManagement"
          >
            <el-icon>
              <component is="House"></component>
            </el-icon>
            <template #title>
              <span>房间管理</span>
            </template>
          </el-menu-item>
          <el-menu-item
            index="/apartmentManagement/attributeManagement/attributeManagement"
          >
            <el-icon>
              <component is="List"></component>
            </el-icon>
            <template #title>
              <span>属性管理</span>
            </template>
          </el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="3">
          <template #title>
            <el-icon>
              <component is="Management"></component>
            </el-icon>
            <span>租赁管理</span>
          </template>
          <el-menu-item index="/rentManagement/appointment/appointment">
            <el-icon>
              <component is="Clock"></component>
            </el-icon>
            <template #title>
              <span>看房预约管理</span>
            </template>
          </el-menu-item>
          <el-menu-item index="/agreementManagement/agreement/agreement">
            <el-icon>
              <component is="Document"></component>
            </el-icon>
            <template #title>
              <span>租约管理</span>
            </template>
          </el-menu-item>
        </el-sub-menu>
        <el-menu-item index="/userManagement/userManagement">
          <el-icon>
            <component is="User"></component>
          </el-icon>
          <span>用户管理</span>
        </el-menu-item>
      </el-menu>
    </el-scrollbar>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed } from 'vue'
import { useSettingsStore } from '@/store/modules/settings'
import { useRoute } from 'vue-router'
import Logo from '../Logo/index.vue'
export default defineComponent({
  components: {
    Logo,
  },
  setup() {
    const settingsStore = useSettingsStore()
    const route = useRoute()
    const collapse = computed(() => settingsStore.collapse)
    const themeConfig = computed(() => settingsStore.themeConfig)
    const activeMenu = computed(() =>
      route.meta.activeMenu ? (route.meta.activeMenu as string) : route.path,
    )
    return {
      collapse,
      activeMenu,
      themeConfig,
    }
  },
})
</script>

<style scoped lang="scss">
.layout-sidebar-container {
  position: fixed;
  top: 10px;
  bottom: 10px;
  left: 10px;
  z-index: $base-z-index;
  width: $base-left-menu-width;
  height: auto;
  padding: 8px 0;
  overflow: hidden;
  background:
    radial-gradient(circle at top left, rgb(59 130 246 / 0.12), transparent 24%),
    radial-gradient(circle at bottom right, rgb(56 189 248 / 0.08), transparent 18%),
    linear-gradient(180deg, #f4f8fc 0%, #e7eff8 100%);
  border: 1px solid rgb(203 213 225 / 0.82);
  border-radius: 26px;
  box-shadow:
    12px 0 30px rgb(148 163 184 / 0.14),
    inset 0 1px 0 rgb(255 255 255 / 0.56);
  transition: width $base-transition-time;

  &.is-collapse {
    width: $base-left-menu-width-min;
    border-right: 0;
  }

  :deep(.el-scrollbar) {
    height: calc(100% - 68px);
  }

  :deep(.el-scrollbar__wrap) {
    overflow-x: hidden;
  }

  :deep(.side-menu) {
    padding: 8px 0 16px;
    background: transparent;
    border: 0;

    .el-menu {
      border-right: none;
      background: transparent;
    }

    .el-menu-item,
    .el-sub-menu__title {
      height: $base-menu-item-height;
      margin: 6px 12px;
      padding-left: 16px !important;
      overflow: hidden;
      line-height: $base-menu-item-height;
      color: #334155;
      text-overflow: ellipsis;
      white-space: nowrap;
      border: 1px solid rgb(255 255 255 / 0);
      border-radius: 14px;
      transition:
        all 0.2s ease,
        transform 0.2s ease;
    }

    .el-menu-item:hover,
    .el-sub-menu__title:hover {
      color: #0f172a !important;
      background: linear-gradient(180deg, rgb(255 255 255 / 0.82), rgb(248 250 252 / 0.94)) !important;
      border-color: rgb(203 213 225 / 0.88);
      transform: translateX(2px);
    }

    .el-menu-item.is-active {
      color: $base-menu-color-active !important;
      background: linear-gradient(135deg, #4f7db8, #3b82b6) !important;
      border-color: rgb(125 166 214 / 0.82);
      box-shadow:
        0 10px 18px rgb(59 130 246 / 0.14),
        inset 0 1px 0 rgb(255 255 255 / 0.14);
    }

    .el-sub-menu.is-opened > .el-sub-menu__title {
      color: #0f172a !important;
      background: linear-gradient(180deg, rgb(248 250 252 / 0.92), rgb(241 245 249 / 0.98)) !important;
      border-color: rgb(203 213 225 / 0.76);
    }

    .el-sub-menu .el-menu-item {
      min-width: auto;
    }

    .el-sub-menu .el-menu .el-menu-item {
      margin-left: 18px;
      color: #475569;
      background: rgb(248 250 252 / 0.58);
      border-color: rgb(226 232 240 / 0.52);
    }

    .el-sub-menu .el-menu .el-menu-item:hover {
      color: #0f172a !important;
      background: #fff !important;
      border-color: rgb(203 213 225 / 0.72);
    }

    .el-sub-menu .el-menu .el-menu-item.is-active {
      color: #fff !important;
      background: linear-gradient(135deg, #5c88bf, #4f7db8) !important;
    }

    .el-icon {
      font-size: 16px;
      color: inherit;
    }

    .el-menu-item span,
    .el-sub-menu__title span {
      font-weight: 600;
    }
  }
}

@media (max-width: 768px) {
  .layout-sidebar-container {
    top: 8px;
    bottom: 8px;
    left: 8px;
    border-radius: 22px;
  }
}
</style>
