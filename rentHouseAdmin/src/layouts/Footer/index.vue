<template>
  <div class="layout-footer-container">
    Copyright
    <svg-icon name="copyright" size="16px" color="rgba(0, 0, 0, 0.45)" />
    {{ fullYear }}尚硅谷教学
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue'

export default defineComponent({
  setup() {
    const fullYear = ref(new Date().getFullYear())
    return {
      fullYear,
    }
  },
})
</script>

<style lang="scss" scoped>
.layout-footer-container {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: $base-app-footer-height;
  margin-top: auto;
  padding: 0 20px;
  color: #64748b;
  background: rgb(255 255 255 / 0.72);
  border: 1px solid rgb(255 255 255 / 0.78);
  border-radius: 20px;
  box-shadow: 0 12px 32px rgb(15 23 42 / 0.05);
  backdrop-filter: blur(14px);
}
</style>
