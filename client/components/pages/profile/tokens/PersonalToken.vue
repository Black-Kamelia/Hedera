<script lang="ts" setup>
import { DateTime } from 'luxon'

const { token } = defineProps<{ token: PersonalTokenDTO }>()
const emit = defineEmits<{
  (event: 'delete', id: string): void
}>()
const { t, d } = useI18n()

const recentlyUsed = computed(() => {
  return token.lastUsed && DateTime.fromISO(token.lastUsed).diffNow().negate().as('weeks') < 1
})

const deleteToken = useDeleteToken()
function handleDelete() {
  deleteToken(token.id).then((response) => {
    if (response) emit('delete', token.id)
  })
}
</script>

<template>
  <div class="p-card p-7 flex flex-row items-center gap-7 w-full">
    <i
      class="i-tabler-key text-3xl ml-3" :class="{
        'text-[--green-500]': recentlyUsed,
        'text-[--orange-500]': !token.lastUsed,
      }"
    />

    <div class="flex flex-row justify-between items-center flex-grow">
      <div class="flex flex-col">
        <div class="flex flex-row items-center mb-1">
          <h2 class="text-lg mr-3">
            {{ token.name }}
          </h2>
        </div>

        <span class="text-[--text-color-secondary]">
          {{ t('pages.profile.tokens.created_at', { date: d(token.createdAt, { dateStyle: "long", timeStyle: "short" }) }) }}
        </span>

        <span
          v-if="token.lastUsed" class="text-[--text-color-secondary]"
          :class="{ 'text-[--green-500]': recentlyUsed }"
        >
          {{ t('pages.profile.tokens.last_used', { date: DateTime.fromISO(token.lastUsed).toRelativeCalendar() }) }}
        </span>
        <span v-else class="text-[--orange-500]">
          {{ t('pages.profile.tokens.never_used') }}
        </span>
      </div>
      <div class="flex flex-row gap-3">
        <PButton icon="i-tabler-trash" severity="danger" text rounded @click="handleDelete" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.p-inline-message {
  padding: 0.25rem 0.5rem;
}
</style>
