<script lang="ts" setup>
const { toggle } = useDark()
const { locale } = useI18n()
</script>

<template>
  <div flex-center h-screen>
    <div class="absolute top-10 right-10 flex flex-row gap-2">
      <select v-model="locale" class="px-2 py-1 rounded-lg">
        <option value="en">
          en
        </option>
        <option value="fr">
          fr
        </option>
      </select>
      <button class="icon-btn text-8" @click="toggle()">
        <div i="tabler-sun dark:tabler-moon" />
      </button>
    </div>
    <p class="absolute top-10 left-10 text-8 font-text">
      {{ $colorMode.value === 'dark' ? $t('theme.dark') : $t('theme.light') }}
    </p>
    <n-button>
      Commencer
    </n-button>
  </div>
</template>
