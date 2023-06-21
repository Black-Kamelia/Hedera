const fs = require('node:fs')
const {
  importDirectory,
  cleanupSVG,
  runSVGO,
} = require('@iconify/tools')

const iconSetPath = './public/assets/icons/'
const iconsPath = `${iconSetPath}files/`
const outputPath = `${iconSetPath}hedera.json`

async function exportIcons() {
  const iconSet = await importDirectory(iconsPath, {
    prefix: 'hedera',
  })

  await iconSet.forEach(async (name, type) => {
    if (type !== 'icon')
      return

    const svg = iconSet.toSVG(name)
    if (!svg) {
      iconSet.remove(name)
      return
    }

    try {
      cleanupSVG(svg)
      runSVGO(svg)
    }
    catch (err) {
      console.log('Error cleaning up SVG: ', err)
      iconSet.remove(name)
      return
    }

    iconSet.fromSVG(name, svg)
  })

  const exported = iconSet.export()
  console.log('Loaded icon set: ', exported)
  fs.writeFileSync(outputPath, `${JSON.stringify(exported, null, 2)}\n`)
}

exportIcons()
