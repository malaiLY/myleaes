// 高德地图hooks
import AMapLoader from '@amap/amap-jsapi-loader'
import { AMAP_MAP_KEY, AMAP_MAP_SECRET_KEY } from '@/config/config'
import { ElMessage } from 'element-plus'
import { onMounted, onUnmounted, ref } from 'vue'

export const useMap = (
  {
    domId,
    autoInit = true,
    showError = true,
  }: { domId?: string; autoInit?: boolean; showError?: boolean } = {},
) => {
  const AMap = ref()
  const map = ref()
  async function initMap() {
    if (AMap.value) return AMap.value
    try {
      ;(window as any)._AMapSecurityConfig = {
        securityJsCode: AMAP_MAP_SECRET_KEY,
        // serviceHost: AMAP_MAP_SERVICE_HOST,
      }
      AMap.value = await AMapLoader.load({
        key: AMAP_MAP_KEY, // 申请好的Web端开发者Key，首次调用 load 时必填
        version: '2.0', // 指定要加载的 JS API 的版本，缺省时默认为 1.4.15
        plugins: [], // 需要使用的的插件列表，如比例尺'AMap.Scale'等
      })
      map.value = domId && new AMap.value.Map(domId)
      return AMap.value
    } catch (error) {
      console.log(error)
      if (showError) {
        ElMessage.error((error as any)?.message || '高德地图初始化失败')
      }
      return null
    }
  }
  function destroyMap() {
    map.value && map.value.destroy()
  }
  onMounted(() => {
    if (autoInit) initMap()
  })
  onUnmounted(() => {
    destroyMap()
  })
  return {
    map,
    AMap,
    initMap,
  }
}
