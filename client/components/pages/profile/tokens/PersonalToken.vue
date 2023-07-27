<script lang="ts" setup>
import { DateTime } from 'luxon'

const { token } = defineProps<{ token: PersonalTokenDTO }>()

const { t, d } = useI18n()
const confirm = useConfirm()

// const recentlyCreated = computed(() => DateTime.fromISO(token.createdAt).diffNow().negate().as('hours') < 24)
const recentlyUsed = computed(() => token.lastUsed && DateTime.fromISO(token.lastUsed).diffNow().negate().as('weeks') < 1)

function deleteToken() {
  confirm.require({
    message: t('pages.profile.tokens.delete.warning'),
    header: t('pages.profile.tokens.delete.title'),
    acceptIcon: 'i-tabler-trash',
    acceptLabel: t('pages.profile.tokens.delete.submit'),
    acceptClass: 'p-button-danger',
    rejectLabel: t('pages.profile.tokens.delete.cancel'),
    accept: () => console.log('delete'),
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

        <span v-if="token.lastUsed" class="text-[--text-color-secondary]" :class="{ 'text-[--green-500]': recentlyUsed }">
          {{ t('pages.profile.tokens.last_used', { date: DateTime.fromISO(token.createdAt).toRelativeCalendar() }) }}
        </span>
        <span v-else class="text-[--orange-500]">
          {{ t('pages.profile.tokens.never_used') }}
        </span>
      </div>
      <div class="flex flex-row gap-3">
        <PButton icon="i-tabler-pencil" text rounded />
        <PButton icon="i-tabler-trash" severity="danger" text rounded @click="deleteToken" />
      </div>
    </div>
  </div>
</template>

<style scoped>
.p-inline-message {
  padding: 0.25rem 0.5rem;
}
</style>
