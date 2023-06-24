<script lang="ts" setup>
usePageName('Debug')
definePageMeta({
  layout: 'sidebar',
})
</script>

<template>
  <PButton icon="i-tabler-dimensions" label="Layout" @click="navigateTo('/debug/layout')" />
</template>
