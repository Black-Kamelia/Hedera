<script lang="ts" setup>
</script>

<template>
  <div class="grid grid-cols-1 gap-3">
    <div class="p-card p-5 flex flex-col gap-3">
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
        <PButton label="Help" severity="help" />
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
