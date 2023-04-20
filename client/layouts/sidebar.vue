<template>
  <div class="flex flex-row" h-screen>
    <Sidebar />
    <div class="flex-grow flex flex-col">
      <Topbar />
      <div class="py-4 px-8">
        <slot />
      </div>
    </div>
  </div>
</template>
