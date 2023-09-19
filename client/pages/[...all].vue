<script setup>
const { isAuthenticated } = storeToRefs(useAuth())

usePageName(() => '404')
watch(isAuthenticated, (val) => {
  setPageLayout(val ? 'main' : 'centercard')
}, { immediate: true })

// definePageMeta({ layout })
</script>

<template>
  <div class="h-full w-full flex justify-center items-center">
    <h1 class="text-5xl">
      404
    </h1>
  </div>
</template>
