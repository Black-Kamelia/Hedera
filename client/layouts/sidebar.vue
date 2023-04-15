<script setup lang="ts">
import SidebarButton from '~/components/ui/SidebarButton.vue'

const { t } = useI18n()

const { isDark, toggle } = useDark()
const themeIcon = computed(() => {
  return isDark.value ? 'i-tabler-sun' : 'i-tabler-moon'
})
const themeName = computed(() => {
  return isDark.value ? t('sidebar.light_mode') : t('sidebar.dark_mode')
})

const open = ref<Boolean>(true)

const sidebarRef = ref()
const isSidebarHovered = useElementHover(sidebarRef)

function toggleSidebar() {
  open.value = !open.value
}

const op = ref()
function toggleOp(event) {
  op.value.toggle(event)
}

const { logout } = useAuth()

function doLogout() {
  logout()
  navigateTo('/login')
}
</script>

<template>
  <div class="flex flex-row" h-screen>
    <aside ref="sidebarRef" class="sidebar flex flex-col p-0" :class="{ expanded: open }">
      <div class="header flex flex-row items-center">
        <div class="flex-grow overflow-hidden">
          <Transition>
            <h1 v-show="open" class="text-4xl ml-3">
              Hedera
            </h1>
          </Transition>
        </div>
        <div>
          <PButton rounded @click="toggleSidebar()">
            <Transition mode="out-in" name="fade">
              <i v-if="isSidebarHovered && !open" class="i-tabler-menu-2" />
              <i v-else-if="open" class="i-tabler-indent-decrease" />
              <i v-else class="i-tabler-leaf" />
            </Transition>
          </PButton>
        </div>
      </div>
      <div class="items flex flex-col justify-between" style="flex-grow: 1;">
        <div class="flex flex-col justify-start gap-2 p-4">
          <SidebarButton
            icon="i-tabler-file" :label="t('sidebar.files')" :open="open"
            :active="route => route.startsWith('/files')" @click="navigateTo('/files')"
          />
          <SidebarButton
            icon="i-tabler-upload" :label="t('sidebar.upload')" :open="open"
            :active="route => route.startsWith('/upload')" @click="navigateTo('/upload')"
          />
          <SidebarButton
            icon="i-tabler-settings" :label="t('sidebar.config')" :open="open"
            :active="route => route.startsWith('/configuration')" @click="navigateTo('/configuration')"
          />
        </div>
        <div class="flex flex-col-reverse p-4">
          <div class="flex top-sep pt-4">
            <PButton rounded class="flex-grow flex flex-row gap-4 items-start" :class="{ open }">
              <div class="flex">
                <i class="i-tabler-user-circle" />
              </div>
              <Transition>
                <div v-show="open" class="flex-grow flex flex-row">
                  <span class="flex-grow overflow-hidden text-left whitespace-nowrap">
                    Slama
                  </span>
                  <i class="i-tabler-chevron-up" />
                </div>
              </Transition>
            </PButton>
          </div>
          <div class="flex flex-col-reverse pb-4 gap-2">
            <SidebarButton :icon="themeIcon" :label="themeName" :open="open" @click="toggle()" />
            <SidebarButton icon="i-tabler-help-circle" :label="t('sidebar.docs')" :open="open" />
          </div>
        </div>
      </div>
    </aside>
    <div class="flex-grow flex flex-col">
      <div class="toolbar flex items-center p-5 h-20 w-full" style="background-color: var(--surface-overlay);">
        <span class="flex-grow p-input-icon-left">
          <i class="i-tabler-search" />
          <PInputText class="search p-inputtext-lg w-full" placeholder="Recherche..." />
        </span>
        <div class="flex flex-row gap-4">
          <div class="card flex justify-content-center">
            <PButton icon="i-tabler-bell" text rounded />
          </div>
          <PButton icon="i-tabler-logout" text rounded @click="doLogout()" />
        </div>
      </div>
      <div class="p-5">
        <slot />
      </div>
    </div>
  </div>
</template>

<style scoped>
.search,
.search:hover,
.search:focus {
  outline: none!important;
  box-shadow: none!important;
  border: none!important;
  background-color: transparent!important;
  padding-left: 3.5rem!important;
}

.toolbar {
  border-bottom: 1px solid var(--surface-border);
}

.sidebar {
  width: 5em;
  min-width: 5em;
  height: 100%;
  transition: min-width 0.3s ease, width 0.3s ease;
  overflow: hidden;
  background-color: var(--primary-color);
  color: var(--primary-color-text);
}

.sidebar.expanded {
  width: 19em;
  min-width: 19em;
}

.sidebar > .header {
  display: flex;
  padding: 1rem;
  background-color: var(--primary-600);
}

.sidebar > .header .p-button {
  background-color: var(--primary-600);
}

.sidebar > .header .p-button:hover {
  background-color: var(--primary-500);
}
.sidebar > .header .p-button:active {
  background-color: var(--primary-700);
}

.sidebar .p-button {
  padding: .75rem;
  transition: background-color .2s, color .2s, border-color .2s, box-shadow .2s, padding 0.3s ease;
}

.sidebar .p-button,
.sidebar .p-button:active,
.sidebar .p-button:hover {
  border: none !important;
  outline: none !important;
  box-shadow: none !important;
}

.sidebar .p-button.open {
  padding: .75rem 1.25rem;
}

.sidebar > .items .p-button:hover {
  background-color: var(--primary-400);
}
.sidebar > .items .p-button:active {
  background-color: var(--primary-600);
}

.sidebar > .items .p-button.active {
  background-color: var(--primary-700);
}

.top-sep {
  border-top: 1px solid var(--primary-400);
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
