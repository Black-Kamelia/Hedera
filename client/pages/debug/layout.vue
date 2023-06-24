<script lang="ts" setup>
usePageName('Debug > Layout')
definePageMeta({
  layout: 'sidebar',
})
</script>

<template>
  <PButton icon="i-tabler-arrow-left" label="Back" @click="navigateTo('/debug')" />
  <div class="p-5 w-100% h-100 bg-orange">
    <h1 class="text-4xl font-bold">
      W-100% H-100px
    </h1>
  </div>
  <div class="p-5 w-100 h-100% bg-green">
    <h1 class="text-4xl font-bold">
      W-100px H-100%
    </h1>
  </div>
  <div class="p-5 w-150% h-50 bg-yellow">
    <h1 class="text-4xl font-bold">
      W-150% H-50px
    </h1>
  </div>
</template>
