<script lang="ts" setup>
const store = useCounterStore()
const { count } = storeToRefs(store)
const { increment } = store
</script>

<template>
  <div>
    <h1>Counter: {{ count }}</h1>
    <button @click="increment">
      Increment
    </button>
  </div>
</template>
