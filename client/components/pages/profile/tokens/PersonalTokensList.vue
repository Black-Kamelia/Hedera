<script lang="ts" setup>
const { t } = useI18n()
const { data, pending } = useFetchAPI<PersonalTokenDTO[]>('/personalTokens')

const empty = computed(() => !pending.value && data.value?.length === 0)

const createTokenDialog = ref<boolean>(false)

function handleCreate(token: PersonalTokenDTO) {
  if (!data.value) return
  data.value.unshift(token)
}

function handleDelete(tokenId: string) {
  if (!data.value) return
  data.value = data.value.filter(token => token.id !== tokenId)
}
</script>

<template>
  <div v-if="pending" class="flex flex-col gap-3">
    <PersonalTokenSkeleton />
    <PersonalTokenSkeleton />
    <PersonalTokenSkeleton />
  </div>

  <div v-else-if="data && !empty">
    <div class="flex flex-col gap-3">
      <PersonalToken
        v-for="token in data"
        :key="token.id"
        :token="token"
        @delete="handleDelete"
      />
    </div>
    <div class="flex flex-row-reverse justify-between items-center my-3">
      <PButton :label="t('pages.profile.tokens.create')" icon="i-tabler-plus" outlined @click="createTokenDialog = true" />
    </div>
  </div>
  <div v-else class="p-card p-7 py-15 h-full w-full flex flex-col justify-center items-center">
    <!-- TODO: Empty state illustration -->
    <img class="w-10em" src="/assets/img/new_file.png" alt="New token">
    <h1 class="text-2xl">
      {{ t('pages.profile.tokens.empty.title') }}
    </h1>
    <p class="pb-10">
      {{ t('pages.profile.tokens.empty.description') }}
    </p>
    <PButton rounded :label="t('pages.profile.tokens.empty.create_token')" @click="createTokenDialog = true" />
  </div>

  <CreateTokenDialog v-model:visible="createTokenDialog" @completed="handleCreate" />
  <PConfirmDialog
    :pt="{
      root: { class: 'max-w-75% xl:max-w-50%' },
      rejectButton: { icon: { class: 'display-none' } },
    }"
  />
</template>
