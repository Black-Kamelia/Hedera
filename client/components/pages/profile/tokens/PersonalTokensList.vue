<script lang="ts" setup>
const token = {
  id: '1',
  name: 'test',
  creationDate: '2021-10-10',
  lastUsed: '2021-10-10',
}
</script>

<template>
  <div class="p-card p-7">
    <div class="flex flex-row justify-between items-center mb-3">
      <h1 class="text-2xl">
        Personal tokens
      </h1>
      <PButton label="Add new token" icon="i-tabler-plus" />
    </div>

    <div class="flex flex-col gap-2">
      <PersonalToken :token="token" />
      <PersonalToken :token="token" />
      <PersonalToken :token="token" />
      <PersonalToken :token="token" />
    </div>
  </div>
</template>
