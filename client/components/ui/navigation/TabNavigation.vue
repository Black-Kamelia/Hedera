<script lang="ts" setup>
export interface NavigationItem {
  label: string
  icon: string
  path: string
}

export interface TabNavigationProps {
  items: NavigationItem[]
  routeMatch: (path: string, item: NavigationItem) => boolean
}

const { items, routeMatch } = defineProps<TabNavigationProps>()
const { currentRoute } = useRouter()
</script>

<template>
  <div class="flex gap-3 self-start">
    <PButton
      v-for="item in items"
      :key="item.path"
      :label="item.label"
      :icon="item.icon"
      :text="!routeMatch(currentRoute.fullPath, item)"
      :pt="{ label: { class: 'text-nowrap' } }"
      @click="navigateTo(item.path)"
    />
  </div>
</template>
