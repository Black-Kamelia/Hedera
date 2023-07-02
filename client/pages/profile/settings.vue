<script setup lang="ts">
import HorizontalActionPanel from '~/components/ui/settings/HorizontalActionPanel.vue'
import RadioCard from '~/components/ui/settings/RadioCard.vue'

const selectedCity = ref({ icon: 'i-tabler-link', name: 'Unlisted', value: 'UNLISTED' })
const cities = ref([
  { icon: 'i-tabler-world', name: 'Public', value: 'PUBLIC' },
  { icon: 'i-tabler-link', name: 'Unlisted', value: 'UNLISTED' },
  { icon: 'i-tabler-eye-off', name: 'Private', value: 'PRIVATE' },
])

const test = ref('binary')
</script>

<template>
  <div class="flex flex-col gap-3">
    <VerticalActionPanel
      header="Files size representation"
      description="Choose to display file sizes in decimal or binary scale. This will affect the way quotas can be interpreted."
    >
      <div class="flex flex-row gap-3 justify-center">
        <RadioCard
          v-model="test"
          title="Binary scale"
          subtitle="Unit are based on powers of 2"
          radio-name="size-scale"
          value="binary"
        >
          <p class="mt-3 text-lg text-center">
            1 KiB = 1 024 bytes
          </p>
        </RadioCard>
        <RadioCard
          v-model="test"
          title="Decimal scale"
          subtitle="Unit are based on powers of 10"
          radio-name="size-scale"
          value="decimal"
        >
          <p class="mt-3 text-lg text-center">
            1 KB = 1 000 bytes
          </p>
        </radiocard>
      </div>
    </VerticalActionPanel>
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
