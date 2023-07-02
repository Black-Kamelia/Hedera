<script lang="ts" setup>
const { t } = useI18n()
const { isDark, toggle } = useDark()
const { user } = useAuth()

const themeIcon = computed(() => isDark.value ? 'i-tabler-sun' : 'i-tabler-moon')
const themeName = computed(() => isDark.value ? t('sidebar.light_mode') : t('sidebar.dark_mode'))

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
    :class="{ expanded: sidebar.open, dark: isDark }"
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
        <PButton rounded @click="toggleSidebar()">
          <Transition mode="out-in" name="fade">
            <i v-if="isSidebarHovered && !sidebar.open" class="i-tabler-menu-2" />
            <i v-else-if="sidebar.open" class="i-tabler-indent-decrease" />
            <i v-else class="i-hedera-logo" />
          </Transition>
        </PButton>
      </div>
    </div>
    <div class="items flex flex-col justify-between h-full overflow-y-auto overflow-x-hidden">
      <div class="flex flex-col justify-start gap-2 p-4 grow">
        <SidebarButton
          icon="i-tabler-file" :label="t('pages.files.title')" :open="sidebar.open"
          :active="route => route.startsWith('/files')" @click="navigateTo('/files')"
        />
        <SidebarButton
          icon="i-tabler-star" :label="t('pages.favorites.title')" :open="sidebar.open"
          :active="route => route.startsWith('/favorites')" @click="navigateTo('/favorites')"
        />
        <SidebarButton
          icon="i-tabler-upload" :label="t('pages.upload.title')" :open="sidebar.open"
          :active="route => route.startsWith('/upload')" @click="navigateTo('/upload')"
        />
        <SidebarButton
          icon="i-tabler-timeline" :label="t('pages.analytics.title')" :open="sidebar.open"
          :active="route => route.startsWith('/analytics')" @click="navigateTo('/analytics')"
        />
        <SidebarButton
          v-if="isAdmin" icon="i-tabler-settings" :label="t('pages.configuration.title')" :open="sidebar.open"
          :active="route => route.startsWith('/configuration')" @click="navigateTo('/configuration')"
        />
        <SidebarButton
          v-if="isDebugEnabled" icon="i-tabler-tool" :label="t('pages.debug.title')" :open="sidebar.open"
          :active="route => route.startsWith('/debug')" @click="navigateTo('/debug')"
        />
      </div>
      <div class="flex flex-col p-4 gap-2">
        <SidebarButton icon="i-tabler-help-circle" :label="t('sidebar.docs')" :open="sidebar.open" />
        <SidebarButton :icon="themeIcon" :label="themeName" :open="sidebar.open" @click="toggle()" />
        <div class="sep" />
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

  .p-button {
    background-color: transparent;
  }

  > .header {
    display: flex;
    padding: 1rem;
    background-color: var(--primary-600);

    .p-button {
      &:hover {
        background-color: rgba(29, 30, 39, 0.1);
      }

      &:active {
        background-color: rgba(29, 30, 39, 0.2);
      }
    }
  }

  > .items {
    .p-button:hover {
      background-color: rgba(29, 30, 39, 0.1);
    }

    .p-button:active {
      background-color: rgba(29, 30, 39, 0.2);
    }

    .p-button.active {
      background-color: var(--primary-color-text);
      color: var(--primary-500);
    }
  }

  .p-button {
    padding: .75rem;
    transition: background-color .2s, color .2s, border-color .2s, box-shadow .2s, padding 0.3s ease;

    &.open {
      padding: .75rem 1.25rem;
    }
  }

  .p-button, .p-button:active, .p-button:hover {
    border: none;
    outline: none;
    box-shadow: none;
  }

  &.dark {
    background-color: var(--primary-800);

    *, *:hover, *:active {
      color: var(--text-color);
    }

    > .header {
      background-color: var(--primary-900);

      .p-button {
        &:hover {
          background-color: rgba(232, 233, 233, 0.05);
        }

        &:active {
          background-color: rgba(232, 233, 233, 0.15);
        }
      }
    }

    > .items {
      .p-button:hover {
        background-color: rgba(232, 233, 233, 0.05);
      }

      .p-button:active {
        background-color: rgba(232, 233, 233, 0.15);
      }

      .p-button.active {
        background-color: var(--text-color);
        color: var(--primary-800);
      }
    }

    .sep {
      margin: .5rem 0;
      border-top: 1px solid var(--primary-700);
      border-left: 1px solid var(--primary-700);
    }
  }
}

.sep {
  margin: .5rem 0;
  border-top: 1px solid var(--primary-400);
  border-left: 1px solid var(--primary-400);
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
