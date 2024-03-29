const fs = require('node:fs')
const CleanCSS = require('clean-css')

const instance = new CleanCSS()

function minify(input, output) {
  const minifiedCode = instance.minify([input])
  fs.writeFileSync(output, minifiedCode.styles)
}

minify('.output/public/assets/css/general.css', '.output/public/assets/css/general.css')
minify('.output/public/assets/css/light-theme.css', '.output/public/assets/css/light-theme.css')
minify('.output/public/assets/css/light-theme-custom.css', '.output/public/assets/css/light-theme-custom.css')
minify('.output/public/assets/css/dark-theme.css', '.output/public/assets/css/dark-theme.css')
minify('.output/public/assets/css/dark-theme-custom.css', '.output/public/assets/css/dark-theme-custom.css')
