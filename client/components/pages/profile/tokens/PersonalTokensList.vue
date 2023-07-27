<script lang="ts" setup>
import { PersonalTokensListKey } from '~/utils/symbols'

const { data, pending, refresh } = useFetchAPI<PersonalTokenDTO[]>('/personalTokens')

const { t } = useI18n()

provide(PersonalTokensListKey, {
  refresh,
})
</script>

<template>
  <div v-if="data && !pending" class="flex flex-col gap-3">
    <PersonalToken v-for="token in data" :key="token.id" :token="token" />
  </div>
  <div v-if="pending" class="flex flex-col gap-3">
    <PersonalTokenSkeleton />
    <PersonalTokenSkeleton />
    <PersonalTokenSkeleton />
  </div>

  <div class="flex flex-row-reverse justify-between items-center my-3">
    <PButton :label="t('pages.profile.tokens.create')" icon="i-tabler-plus" outlined />
  </div>

  <PConfirmDialog
    :pt="{
      root: { class: 'max-w-75% xl:max-w-50%' },
      rejectButton: { icon: { class: 'display-none' } },
    }"
  />
</template>
