<script setup lang="ts">
import HorizontalActionPanel from '~/components/ui/settings/HorizontalActionPanel.vue'
import FileSizeScale from '~/components/pages/profile/settings/FileSizeScale.vue'

const selectedCity = ref({ icon: 'i-tabler-link', name: 'Unlisted', value: 'UNLISTED' })
const cities = ref([
  { icon: 'i-tabler-world', name: 'Public', value: 'PUBLIC' },
  { icon: 'i-tabler-link', name: 'Unlisted', value: 'UNLISTED' },
  { icon: 'i-tabler-eye-off', name: 'Private', value: 'PRIVATE' },
])

const test = ref('BINARY')
</script>

<template>
  <div class="flex flex-col gap-3">
    <FileSizeScale v-model="test" />

    <h1 class="text-2xl">
      File uploading
    </h1>

    <HorizontalActionPanel
      header="Default files visibility"
      description="Choose a visibility to apply to every file you upload. You can change the visibility of each individual file at any time."
    >
      <PDropdown v-model="selectedCity" :options="cities" option-label="name" placeholder="Select a City" class="w-full md:w-14rem">
        <template #value="slotProps">
          <div v-if="slotProps.value" class="flex align-items-center">
            <i :class="`${slotProps.value.icon} mr-2`" />
            <div>{{ slotProps.value.name }}</div>
          </div>
          <span v-else>
            {{ slotProps.placeholder }}
          </span>
        </template>
        <template #option="slotProps">
          <div class="flex align-items-center">
            <i :class="`${slotProps.option.icon} mr-2`" />
            <div>{{ slotProps.option.name }}</div>
          </div>
        </template>
      </PDropdown>
    </HorizontalActionPanel>

    <HorizontalActionPanel
      header="Automatically delete oldest files"
      description="When you are about to exceed your quota, Hedera will delete some of your oldest files to free some space. Please note that if you try to upload a file that is larger than your quota, it will be rejected without deleting any files."
    >
      <PInputSwitch />
    </HorizontalActionPanel>
    <h1 class="text-2xl mt-6">
      Display and animations
    </h1>

    <HorizontalActionPanel
      header="Enabling animations and transitions"
      description="Hedera uses animations and transitions to make the user experience more enjoyable. If you are experiencing performance issues, you can disable them here."
    >
      <PInputSwitch />
    </HorizontalActionPanel>
  </div>
</template>
