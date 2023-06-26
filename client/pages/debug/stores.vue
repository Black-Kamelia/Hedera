<script lang="ts" setup>
import { useStorage } from '@vueuse/core'

const auth = useAuth()
const dark = useDark()
const filters = 'Empty.'
const sidebar = toReactive(useStorage('sidebar', { open: true }))
</script>

<template>
  <div class="flex flex-col gap-3">
    <div class="p-card p-5 grid grid-cols-2 gap-6">
      <div class="flex flex-col gap-6">
        <PPanel class="max-w-100% w-100%" header="Authentication" toggleable>
          <pre class="overflow-y-auto">{{ auth }}</pre>
        </PPanel>

        <PPanel class="max-w-100% w-100%" header="Theme" toggleable>
          <pre class="overflow-y-auto">{{ dark }}</pre>
        </PPanel>
      </div>

      <div class="flex flex-col gap-6">
        <PPanel class="max-w-100% w-100%" header="Sidebar" toggleable>
          <pre class="overflow-y-auto">{{ sidebar }}</pre>
        </PPanel>

        <PPanel class="max-w-100% w-100%" header="Filters" toggleable>
          <pre class="overflow-y-auto">{{ filters }}</pre>
        </PPanel>
      </div>
    </div>
  </div>
</template>
