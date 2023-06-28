<script setup>
const { isAuthenticated } = useAuth()

watch(isAuthenticated, (val) => {
  setPageLayout(val ? 'sidebar' : 'centercard')
  usePageName(() => '404')
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
