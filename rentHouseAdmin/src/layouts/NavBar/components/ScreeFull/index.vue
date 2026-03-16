<!--
 * @Description: 
 * @Autor: 李海波
 * @Date: 2023-03-03 13:39:38
 * @LastEditors: 1547702880@@qq.com
 * @LastEditTime: 2023-03-17 11:41:48
-->
<template>
  <div class="m-screenful">
    <el-tooltip
      effect="dark"
      :content="!isFullscreen ? '全屏' : '收起'"
      placement="bottom"
    >
      <el-button circle @click="toggle">
        <IconifyIcon
          v-if="!isFullscreen"
          icon="fluent:full-screen-maximize-24-filled"
          height="16"
        />
        <IconifyIcon
          v-else
          icon="fluent:full-screen-minimize-24-filled"
          height="18"
        />
      </el-button>
    </el-tooltip>
  </div>
</template>

<script lang="ts">
import { useFullscreen } from '@vueuse/core'
import { defineComponent } from 'vue'

export default defineComponent({
  setup() {
    const { toggle, isFullscreen } = useFullscreen()
    return {
      toggle,
      isFullscreen,
    }
  },
})
</script>

<style lang="scss" scoped>
.m-screenful {
  display: inline-flex;
  cursor: pointer;
}

.m-screenful :deep(.el-button) {
  width: 38px;
  height: 38px;
  padding: 0;
  color: #475569;
  background: linear-gradient(180deg, #fff, #f8fafc);
  border-color: #e2e8f0;
  box-shadow: none;
  transition:
    transform 0.2s ease,
    border-color 0.2s ease,
    box-shadow 0.2s ease,
    color 0.2s ease;
}

.m-screenful :deep(.el-button:hover) {
  color: var(--el-color-primary);
  border-color: #bfdbfe;
  box-shadow: 0 10px 22px rgb(59 130 246 / 0.12);
  transform: translateY(-1px);
}
</style>
