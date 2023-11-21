<script lang="ts" setup>
const color = useColorMode()
const { t } = useI18n()

const child = ref(null)
const cardTransition = ref(false)

// Since we cannot access transition hooks here, we have to listen to DOM mutations
// to determine when the card is transitioning. When the card is doing so, a child
// is added, and when the transition is over, the child is removed.
// This should work as long as we don't mess with the DOM in other ways.
useMutationObserver(child, (mutations) => {
  if (!mutations || mutations.length === 0) return
  if (mutations[0].addedNodes.length > 0) cardTransition.value = true
  if (mutations[0].removedNodes.length > 0) cardTransition.value = false
}, { childList: true })
</script>

<template>
  <div class="flex items-center justify-center w-screen h-screen p-10">
    <div class="background" :class="color.value" />

    <div class="p-card main-card overflow-x-hidden scroll max-h-full">
      <h1 class="text-center font-600 text-5xl mb-1">
        {{ t('app_name') }}
      </h1>

      <MorphAnimatedContainer :prevent-transition="!cardTransition" transition-duration="0.5s">
        <div ref="child">
          <slot />
        </div>
      </MorphAnimatedContainer>
    </div>
  </div>
</template>

<style scoped lang="scss">
.background {
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  height: 100%;
  left: 0;
  position: fixed;
  right: 0;
  top: 0;
  z-index: -1;

  &.light {
    background-image: url("/assets/img/06-Blue_Purple_LM-4K.png");
  }

  &.dark {
    background-image: url("/assets/img/06-Blue_Purple_DM-4K.png");
  }
}
</style>
