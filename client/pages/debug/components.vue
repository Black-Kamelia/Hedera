<script setup lang="ts">
const toast = useToast()
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
        <PButton outlined size="small" label="Primary" />
        <PButton outlined size="small" label="Secondary" severity="secondary" />
        <PButton outlined size="small" label="Success" severity="success" />
        <PButton outlined size="small" label="Warning" severity="warning" />
        <PButton outlined size="small" label="Danger" severity="danger" />
        <PButton outlined size="small" label="Info" severity="info" />
        <PButton outlined size="small" label="Help" severity="help" />
      </div>
      <div class="flex gap-2">
        <PButton text size="small" label="Primary" />
        <PButton text size="small" label="Secondary" severity="secondary" />
        <PButton text size="small" label="Success" severity="success" />
        <PButton text size="small" label="Warning" severity="warning" />
        <PButton text size="small" label="Danger" severity="danger" />
        <PButton text size="small" label="Info" severity="info" />
        <PButton text size="small" label="Help" severity="help" />
      </div>
      <div class="flex gap-2">
        <PSplitButton size="small" label="Primary" :model="[]" />
        <PSplitButton size="small" label="Secondary" severity="secondary" :model="[]" />
        <PSplitButton size="small" label="Success" severity="success" :model="[]" />
        <PSplitButton size="small" label="Warning" severity="warning" :model="[]" />
        <PSplitButton size="small" label="Danger" severity="danger" :model="[]" />
        <PSplitButton size="small" label="Info" severity="info" :model="[]" />
        <PSplitButton size="small" label="Help" severity="help" :model="[]" />
      </div>
    </div>

    <div class="p-card p-5 flex flex-col gap-3">
      <h2 class="text-2xl">
        Labels
      </h2>
      <div class="flex gap-2">
        <PBadge value="Primary" />
        <PBadge value="Success" severity="success" />
        <PBadge value="Warning" severity="warning" />
        <PBadge value="Danger" severity="danger" />
        <PBadge value="Info" severity="info" />
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
        Toasts
      </h2>
      <div class="flex gap-2">
        <PButton
          size="small" label="Success" severity="success" @click="toast.add({
            severity: 'success',
            summary: 'Success',
            detail: 'Message Content',
            life: 3000,
          })"
        />
        <PButton
          size="small" label="Warning" severity="warning" @click="toast.add({
            severity: 'warn',
            summary: 'Warning',
            detail: 'Message Content',
            life: 3000,
          })"
        />
        <PButton
          size="small" label="Danger" severity="danger" @click="toast.add({
            severity: 'error',
            summary: 'Danger',
            detail: 'Message Content',
            life: 3000,
          })"
        />
        <PButton
          size="small" label="Info" severity="info" @click="toast.add({
            severity: 'info',
            summary: 'Info',
            detail: 'Message Content',
            life: 3000,
          })"
        />
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
