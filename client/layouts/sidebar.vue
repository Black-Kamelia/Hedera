<template>
  <div class="flex flex-row" h-screen w-screen>
    <Sidebar />
    <div class="flex-grow flex flex-col">
      <Topbar />
      <div class="py-4 px-8 w-full h-full overflow-y-auto">
        <slot />
      </div>
    </div>
  </div>
</template>
