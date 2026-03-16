<template>
  <el-dropdown @visible-change="onChange" @command="handleCommand">
    <div class="avatar-dropdown">
      <img v-if="avatarUrl" class="user-avatar" :src="avatarUrl" alt="" />
      <span v-else class="user-avatar user-avatar-fallback">
        <el-icon><UserFilled /></el-icon>
      </span>
      <div class="user-name">{{ name }}</div>
      <el-icon class="up-down">
        <component :is="visible ? 'ArrowUp' : 'ArrowDown'" />
      </el-icon>
    </div>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item command="logout">
          <svg-icon name="logout" size="16px" />
          <span>退出登陆</span>
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<script lang="ts">
import { defineComponent, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { ArrowUp, ArrowDown, UserFilled } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { LOGIN_URL } from '@/config/config'
export default defineComponent({
  components: {
    ArrowUp,
    ArrowDown,
    UserFilled,
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const userStore = useUserStore()

    const { name, avatarUrl } = userStore.userInfo || {}
    const visible = ref(false)

    function onChange(show: boolean) {
      visible.value = show
    }

    function handleCommand(command: string) {
      if (command === 'logout') {
        logout()
      }
    }

    // 退出登陆
    function logout() {
      ElMessageBox.confirm('您确定要退出吗？', '提示', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning',
      }).then(async () => {
        await userStore.Logout()
        router.push({ path: LOGIN_URL, query: { redirect: route.fullPath } })
        ElMessage.success('退出登录成功！')
      })
    }
    return {
      name,
      avatarUrl,
      visible,
      onChange,
      handleCommand,
    }
  },
})
</script>

<style scoped lang="scss">
.avatar-dropdown {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 42px;
  padding: 4px 10px 4px 6px;
  background: linear-gradient(180deg, #fff, #f8fafc);
  border: 1px solid #e2e8f0;
  border-radius: 999px;
  cursor: pointer;
  transition:
    transform 0.2s ease,
    border-color 0.2s ease,
    box-shadow 0.2s ease;
}

.avatar-dropdown:hover {
  transform: translateY(-1px);
  border-color: #bfdbfe;
  box-shadow: 0 10px 22px rgb(59 130 246 / 0.12);
}

.user-avatar {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  object-fit: cover;
}

.user-avatar-fallback {
  color: #2563eb;
  background: linear-gradient(180deg, #eff6ff, #dbeafe);
  box-shadow: inset 0 0 0 1px rgb(191 219 254 / 0.95);
}

.user-avatar-fallback :deep(svg) {
  width: 16px;
  height: 16px;
}

.user-name {
  max-width: 88px;
  overflow: hidden;
  font-size: 13px;
  font-weight: 600;
  color: #334155;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.up-down {
  color: #94a3b8;
  transition: transform 0.2s ease, color 0.2s ease;
}

.avatar-dropdown:hover .up-down {
  color: var(--el-color-primary);
}
</style>
