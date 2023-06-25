<script lang="ts" setup>
</script>

<template>
  <div class="grid grid-cols-1 gap-3">
    <h2 class="text-2xl">
      Buttons
    </h2>
    <div class="flex gap-2">
      <PButton label="Primary" />
      <PButton label="Secondary" severity="secondary" />
      <PButton label="Success" severity="success" />
      <PButton label="Warning" severity="warning" />
      <PButton label="Danger" severity="danger" />
      <PButton label="Info" severity="info" />
    </div>

    <h2 class="text-2xl">
      Inputs
    </h2>
    <div class="flex gap-2">
      <PInputText placeholder="Text" />
      <PInputText placeholder="Disabled" disabled />
      <PInputText placeholder="Number" type="number" />
      <PInputText placeholder="Password" type="password" />
    </div>
  </div>
</template>
