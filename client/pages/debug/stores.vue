<script lang="ts" setup>
const auth = reactive(storeToRefs(useAuth()))
const color = useColorMode()
const filters = reactive(storeToRefs(useFilesFilters()))
const sidebar = useLocalStorage('sidebar', { open: true })
const settings = reactive(storeToRefs(useUserSettings()))
const configuration = reactive(storeToRefs(useGlobalConfiguration()))
</script>

<template>
  <div class="flex flex-col gap-3">
    <div class="p-card p-5 grid grid-cols-2 gap-6">
      <div class="flex flex-col gap-6">
        <PPanel class="max-w-100% w-100%" header="Authentication" toggleable>
          <pre class="overflow-y-auto">{{ auth }}</pre>
        </PPanel>

        <PPanel class="max-w-100% w-100%" header="Theme" toggleable>
          <pre class="overflow-y-auto">{{ color }}</pre>
        </PPanel>

        <PPanel class="max-w-100% w-100%" header="Configuration" toggleable>
          <pre class="overflow-y-auto">{{ configuration }}</pre>
        </PPanel>
      </div>

      <div class="flex flex-col gap-6">
        <PPanel class="max-w-100% w-100%" header="Sidebar" toggleable>
          <pre class="overflow-y-auto">{{ sidebar }}</pre>
        </PPanel>

        <PPanel class="max-w-100% w-100%" header="Filters" toggleable>
          <pre class="overflow-y-auto">{{ filters }}</pre>
        </PPanel>

        <PPanel class="max-w-100% w-100%" header="User settings" toggleable>
          <pre class="overflow-y-auto">{{ settings }}</pre>
        </PPanel>
      </div>
    </div>
  </div>
</template>
