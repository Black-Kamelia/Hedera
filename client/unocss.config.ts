import {
  defineConfig,
  presetAttributify,
  presetIcons,
  presetTypography,
  presetUno,
  presetWebFonts,
  transformerDirectives,
  transformerVariantGroup,
} from 'unocss'

export default defineConfig({
  shortcuts: [
    ['btn', 'px-4 py-1 rounded inline-block hover:violet-500 dark:hover:emerald-500 text-white cursor-pointer disabled:cursor-default disabled:bg-gray-600 disabled:opacity-50'],
    ['icon-btn', 'hover:text-violet-500 dark:hover:text-emerald-500 inline-block cursor-pointer select-none opacity-75 transition duration-200 ease-in-out hover:opacity-100'],
    ['flex-center', 'flex items-center justify-center'],
    ['grid-center', 'grid place-items-center'],
  ],
  presets: [
    presetUno(),
    presetAttributify(),
    presetIcons({
      scale: 1.5,
      warn: true,
      extraProperties: {
        'background-color': 'currentColor',
        'color': 'inherit',
      },
      collections: {
        tabler: () => import('@iconify-json/tabler/icons.json').then(i => i.default),
        hedera: () => import('./public/assets/icons/hedera.json').then(i => i.default),
      },
    }),
    presetTypography(),
    presetWebFonts({
      provider: 'google',
      fonts: {
        display: 'Red Hat Display',
        text: 'Red Hat Text',
        mono: 'JetBrains Mono',
      },
    }),
  ],
  transformers: [
    transformerDirectives(),
    transformerVariantGroup(),
  ],
  safelist: [
    'prose',
    'prose-sm',
    'm-auto',
    'text-left',
    'i-tabler-circle-check-filled',
    'i-tabler-info-circle-filled',
    'i-tabler-alert-triangle-filled',
    'i-tabler-alert-circle-filled',
    'i-tabler-clipboard-check',
  ],
})
