<script lang="ts" setup>
const { t } = useI18n()
const { format } = useHumanFileSize()

const { data, pending } = useFetchAPI<number>('/configuration/maintenance/thumbnail-cache-size')
</script>

<template>
  <div class="flex flex-col gap-3 relative">
    <h1 class="text-2xl">
      {{ t('pages.configuration.maintenance.title') }}
    </h1>

    <HorizontalActionPanel
      header="Vider le cache des miniatures"
      description="Supprimer toutes les miniatures de fichiers stockées en cache. Les miniatures seront recréées dès lors que les fichiers seront à nouveau listés."
    >
      <PButton
        label="Vider le cache"
        class="min-w-12em"
        icon="i-tabler-trash"
        @click="() => {}"
      />

      <template #cta>
        <div class="flex flew-row gap-1 items-center">
          <span class="inline-block">Taille actuelle du cache :</span>
          <PSkeleton v-if="pending" height="1rem" width="5rem" />
          <span v-else>{{ format(data) }}</span>
        </div>
      </template>
    </HorizontalActionPanel>
  </div>
</template>
