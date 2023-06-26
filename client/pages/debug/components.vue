<script lang="ts" setup>
</script>

<template>
  <div class="grid grid-cols-1 gap-3">
    <div class="p-card p-5 flex flex-col gap-3">
      <h2 class="text-2xl">
        Buttons
      </h2>
      <div class="flex gap-2">
        <PButton size="small" label="Primary" />
        <PButton size="small" label="Secondary" severity="secondary" />
        <PButton size="small" label="Success" severity="success" />
        <PButton size="small" label="Warning" severity="warning" />
        <PButton size="small" label="Danger" severity="danger" />
        <PButton size="small" label="Info" severity="info" />
        <PButton size="small" label="Help" severity="help" />
      </div>
      <div class="flex gap-2">
        <PSplitButton size="small" label="Primary" icon="pi pi-plus" :model="[]" />
        <PSplitButton size="small" label="Secondary" icon="pi pi-plus" severity="secondary" :model="[]" />
        <PSplitButton size="small" label="Success" icon="pi pi-plus" severity="success" :model="[]" />
        <PSplitButton size="small" label="Warning" icon="pi pi-plus" severity="warning" :model="[]" />
        <PSplitButton size="small" label="Danger" icon="pi pi-plus" severity="danger" :model="[]" />
        <PSplitButton size="small" label="Info" icon="pi pi-plus" severity="info" :model="[]" />
        <PSplitButton size="small" label="Help" icon="pi pi-plus" severity="help" :model="[]" />
      </div>
    </div>

    <div class="p-card p-5 flex flex-col gap-3">
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

    <div class="p-card p-5 flex flex-col gap-3">
      <h2 class="text-2xl">
        Messages
      </h2>
      <div class="flex gap-2">
        <PMessage :closable="false" severity="success" icon="i-tabler-circle-check-filled">
          Success message
        </PMessage>
        <PMessage :closable="false" severity="warn" icon="i-tabler-alert-triangle-filled">
          Warning message
        </PMessage>
        <PMessage :closable="false" severity="error" icon="i-tabler-alert-circle-filled">
          Error message
        </PMessage>
        <PMessage :closable="false" severity="info" icon="i-tabler-info-circle-filled">
          Info message
        </PMessage>
      </div>
    </div>
  </div>
</template>
