<template>
  <PConfirmDialog
    :pt="{
      root: { class: 'max-w-75% xl:max-w-50%' },
      rejectButton: { icon: { class: 'display-none' } },
      message: { class: 'text-[--text-color-secondary]' },
    }"
  />
</template>
