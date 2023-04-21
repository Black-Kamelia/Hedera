<script lang="ts" setup>
import { useStorage } from '@vueuse/core'

const { t } = useI18n()
const { isDark, toggle } = useDark()

const themeIcon = computed(() => isDark.value ? 'i-tabler-sun' : 'i-tabler-moon')
const themeName = computed(() => isDark.value ? t('sidebar.light_mode') : t('sidebar.dark_mode'))

const sidebar = useStorage('sidebar', { open: true })
const sidebarOpen = computed(() => sidebar.value.open)
const sidebarRef = ref<HTMLElement | null>(null)

const isSidebarHovered = useElementHover(sidebarRef)
function toggleSidebar() {
  sidebar.value = { ...sidebar.value, open: !sidebar.value.open }
}
</script>

<template>
  <aside ref="sidebarRef" class="sidebar flex flex-col p-0" :class="{ expanded: sidebarOpen }">
    <div class="header flex flex-row items-center">
      <div class="flex-grow overflow-hidden">
        <Transition>
          <h1 v-show="sidebarOpen" class="text-4xl ml-2">
            {{ t('app_name') }}
          </h1>
        </Transition>
      </div>
      <div>
        <PButton rounded @click="toggleSidebar()">
          <Transition mode="out-in" name="fade">
            <i v-if="isSidebarHovered && !sidebarOpen" class="i-tabler-menu-2" />
            <i v-else-if="sidebarOpen" class="i-tabler-indent-decrease" />
            <i v-else class="i-hedera-logo" />
          </Transition>
        </PButton>
      </div>
    </div>
    <div class="items flex flex-col justify-between" style="flex-grow: 1;">
      <div class="flex flex-col justify-start gap-2 p-4">
        <SidebarButton
          icon="i-tabler-file" :label="t('pages.files.page_name')" :open="sidebarOpen"
          :active="route => route.startsWith('/files')" @click="navigateTo('/files')"
        />
        <SidebarButton
          icon="i-tabler-star" :label="t('pages.favorites.page_name')" :open="sidebarOpen"
          :active="route => route.startsWith('/favorites')" @click="navigateTo('/favorites')"
        />
        <SidebarButton
          icon="i-tabler-upload" :label="t('pages.upload.page_name')" :open="sidebarOpen"
          :active="route => route.startsWith('/upload')" @click="navigateTo('/upload')"
        />
        <SidebarButton
          icon="i-tabler-timeline" :label="t('pages.analytics.page_name')" :open="sidebarOpen"
          :active="route => route.startsWith('/analytics')" @click="navigateTo('/analytics')"
        />
        <SidebarButton
          icon="i-tabler-settings" :label="t('pages.configuration.page_name')" :open="sidebarOpen"
          :active="route => route.startsWith('/configuration')" @click="navigateTo('/configuration')"
        />
      </div>
      <div class="flex flex-col p-4 gap-2">
        <SidebarButton icon="i-tabler-help-circle" :label="t('sidebar.docs')" :open="sidebarOpen" />
        <SidebarButton :icon="themeIcon" :label="themeName" :open="sidebarOpen" @click="toggle()" />
        <div class="sep" />
        <SidebarButton icon="i-tabler-user-circle" label="{Username goes here}" :open="sidebarOpen" />
      </div>
    </div>
  </aside>
</template>

<!-- TODO: you can use SCSS if you want ; but you could also use UNO; the importants are weird -->
<style scoped lang="scss">
.sidebar {
  width: 5em;
  min-width: 5em;
  height: 100%;
  transition: min-width 0.3s ease, width 0.3s ease;
  overflow: hidden;
  background-color: var(--primary-color);
  color: var(--primary-color-text);

  &.expanded {
    width: 19em;
    min-width: 19em;
  }

  > .header {
    display: flex;
    padding: 1rem;
    background-color: var(--primary-600);

    .p-button {
      background-color: var(--primary-600);

      &:hover {
        background-color: var(--primary-500);
      }

      &:active {
        background-color: var(--primary-700);
      }
    }
  }

  > .items {
    .p-button:hover {
      background-color: var(--primary-400);
    }

    .p-button:active {
      background-color: var(--primary-600);
    }

    .p-button.active {
      background-color: var(--primary-700);
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
    border: none !important;
    outline: none !important;
    box-shadow: none !important;
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
