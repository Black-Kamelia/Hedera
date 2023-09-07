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
