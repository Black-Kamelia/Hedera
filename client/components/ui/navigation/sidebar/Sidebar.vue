<script lang="ts" setup>
const { t } = useI18n()
const color = useColorMode()
const { user } = storeToRefs(useAuth())

const sidebar = toReactive(useLocalStorage('sidebar', { open: true }))
const sidebarRef = ref<HTMLElement | null>(null)

const isSidebarHovered = useElementHover(sidebarRef)
const isAdmin = computed(() => user.value?.role === 'ADMIN' || user.value?.role === 'OWNER')
const { isDebugEnabled } = useDebug()

function toggleSidebar() {
  sidebar.open = !sidebar.open
}
</script>

<template>
  <aside
    ref="sidebarRef"
    class="sidebar flex flex-col p-0 h-full"
    :class="{ expanded: sidebar.open, dark: color.value === 'dark' }"
  >
    <div class="header flex flex-row items-center">
      <div class="flex-grow overflow-hidden">
        <Transition>
          <h1 v-show="sidebar.open" class="text-4xl ml-2">
            {{ t('app_name') }}
          </h1>
        </Transition>
      </div>
      <div>
        <SidebarButton
          icon=""
          label=""
          :open="false"
          @click="toggleSidebar()"
        >
          <template #icon>
            <Transition mode="out-in" name="fade">
              <i v-if="isSidebarHovered && !sidebar.open" class="i-tabler-menu-2" />
              <i v-else-if="sidebar.open" class="i-tabler-indent-decrease" />
              <i v-else class="i-hedera-logo" />
            </Transition>
          </template>
        </SidebarButton>
      </div>
    </div>
    <div class="items flex flex-col justify-between h-full overflow-y-auto overflow-x-hidden">
      <div class="flex flex-col justify-between grow overflow-y-auto">
        <div class="flex flex-col justify-start gap-2 p-4">
          <SidebarButton
            icon="i-tabler-file" :label="t('pages.files.title')" :open="sidebar.open"
            :active="route => route.startsWith('/files')" @click="navigateTo('/files')"
          />
          <SidebarButton
            icon="i-tabler-upload" :label="t('pages.upload.title')" :open="sidebar.open"
            :active="route => route.startsWith('/upload')" @click="navigateTo('/upload')"
          />
          <!--
          <SidebarButton
            icon="i-tabler-star" :label="t('pages.favorites.title')" :open="sidebar.open"
            :active="route => route.startsWith('/favorites')" @click="navigateTo('/favorites')"
          />
          <SidebarButton
            icon="i-tabler-timeline" :label="t('pages.analytics.title')" :open="sidebar.open"
            :active="route => route.startsWith('/analytics')" @click="navigateTo('/analytics')"
          />
          -->
          <SidebarButton
            v-if="isAdmin" icon="i-tabler-tool" :label="t('pages.configuration.title')" :open="sidebar.open"
            :active="route => route.startsWith('/configuration')" @click="navigateTo('/configuration')"
          />
          <SidebarButton
            v-if="isDebugEnabled" icon="i-tabler-traffic-cone" :label="t('pages.debug.title')" :open="sidebar.open"
            :active="route => route.startsWith('/debug')" @click="navigateTo('/debug')"
          />
        </div>
        <div class="flex flex-col p-4 gap-2">
          <SidebarButton
            icon="i-tabler-help-circle"
            end-icon="i-tabler-external-link"
            :label="t('sidebar.docs')"
            :open="sidebar.open"
            @click="navigateTo('https://github.com/Black-Kamelia/Hedera', { external: true, open: { target: '_blank' } })"
          />
        </div>
      </div>
      <HorizontalSeparator />
      <div class="flex flex-col justify-start gap-2 p-4">
        <SidebarButton
          icon="i-tabler-user-circle" :label="user?.username ?? ''" :open="sidebar.open"
          :active="route => route.startsWith('/profile')" @click="navigateTo('/profile')"
        />
      </div>
    </div>
  </aside>
</template>

<style scoped lang="scss">
.sidebar {
  width: var(--sidebar-width-collapsed);
  min-width: var(--sidebar-width-collapsed);
  height: 100%;
  transition: min-width 0.3s ease, width 0.3s ease;
  overflow: hidden;
  background-color: var(--primary-500);
  color: var(--primary-color-text);

  &.expanded {
    width: var(--sidebar-width-open);
    min-width: var(--sidebar-width-open);
  }

  > .header {
    display: flex;
    padding: 1rem;
    background-color: var(--primary-600);

    .dark & {
      background-color: var(--primary-900);
    }
  }

  &.dark {
    background-color: var(--primary-800);
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.v-enter-active,
.v-leave-active {
  transition: opacity .3s ease;
}

.v-enter-from,
.v-leave-to {
  opacity: 0;
}
</style>
