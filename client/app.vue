<template>
  <div font="text">
    <NuxtLoadingIndicator />
    <NuxtLayout>
      <NuxtPage />
    </NuxtLayout>
  </div>
</template>

<style>
.p-component {
  font-family: "Red Hat Text", sans-serif;
}

.p-input-icon-left > i,
.p-input-icon-right > i {
  margin-top: -12px;
}

.p-input-icon-left > .p-inputtext {
    padding-left: calc(1.25rem + 24px);
}

.p-input-icon-right > .p-inputtext {
    padding-right: calc(1.25rem + 24px);
}
</style>
