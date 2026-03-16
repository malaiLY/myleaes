<!--
 * @Description: 
 * @Autor: 李海波
 * @Date: 2023-03-03 13:41:05
 * @LastEditors: 1547702880@@qq.com
 * @LastEditTime: 2023-03-17 11:42:52
-->
<template>
  <div class="btn">
    <el-tooltip effect="dark" content="系统设置">
      <el-button circle @click="onSetting">
        <IconifyIcon icon="ep:setting" height="16" />
      </el-button>
    </el-tooltip>
  </div>
</template>

<script lang="ts">
import { defineComponent } from 'vue'
import mittBus from '@/utils/mittBus'
export default defineComponent({
  setup() {
    const onSetting = () => {
      // 采用事件监听的方式打开ThemeDrawer
      mittBus.emit('openThemeDrawer')
    }
    return { onSetting }
  },
})
</script>

<style scoped lang="scss">
.btn {
  display: inline-flex;
  cursor: pointer;
}

.btn :deep(.el-button) {
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

.btn :deep(.el-button:hover) {
  color: var(--el-color-primary);
  border-color: #bfdbfe;
  box-shadow: 0 10px 22px rgb(59 130 246 / 0.12);
  transform: translateY(-1px);
}
</style>
