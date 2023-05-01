<template>
  <div class="flex flex-row" h-screen>
    <Sidebar />
    <div class="flex-grow flex flex-col">
      <Topbar />
      <div class="py-4 px-8 h-full overflow-y-auto w-full">
        <slot />
      </div>
    </div>
  </div>
</template>
