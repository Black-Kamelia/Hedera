<script lang="ts" setup>
const { toggle } = useDark()
</script>

<template>
  <div class="flex-center h-screen">
    <div class="absolute top-20 left-20 flex flex-row gap-2">
      <button class="icon-btn text-8" @click="toggle()">
        <div i="tabler-sun dark:tabler-moon" />
      </button>
    </div>
    <div class="flex flex-row items-start gap-4">
      <h-button size="sm">
        Button
      </h-button>
      <h-button>
        Button
      </h-button>
      <h-button size="lg">
        Button
      </h-button>
      <h-button size="xl">
        Button
      </h-button>
    </div>
    <div class="display-flex flex-row items-start gap-4 mt-6">
      <h-button color="primary">
        Button
      </h-button>
      <h-button color="warning">
        Button
      </h-button>
      <h-button color="error">
        Button
      </h-button>
      <h-button color="success">
        Button
      </h-button>
      <h-button color="info">
        Button
      </h-button>
      <h-button color="light">
        Button
      </h-button>
      <h-button color="dark">
        Button
      </h-button>
    </div>
    <div class="display-flex flex-row items-start gap-4 mt-6">
      <h-button color="primary" type="secondary">
        Button
      </h-button>
      <h-button color="warning" type="secondary">
        Button
      </h-button>
      <h-button color="error" type="secondary">
        Button
      </h-button>
      <h-button color="success" type="secondary">
        Button
      </h-button>
      <h-button color="info" type="secondary">
        Button
      </h-button>
      <h-button color="light" type="secondary">
        Button
      </h-button>
      <h-button color="dark" type="secondary">
        Button
      </h-button>
    </div>
    <div class="display-flex flex-row items-start gap-4 mt-6">
      <h-button color="dark" left-icon="i-tabler-download">
        Télécharger la configuration ShareX
      </h-button>
      <h-button color="light" left-icon="i-tabler-download">
        Télécharger la configuration ShareX
      </h-button>
    </div>
    <div class="flex flex-row mt-6">
      <p-button label="Button" icon="i-tabler-check stroke-1" icon-pos="left" />
    </div>
  </div>
</template>

<style scoped>
h1 {
  font-size: 2rem;
  font-weight: 700;
}
</style>
