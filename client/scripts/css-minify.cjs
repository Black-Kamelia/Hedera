const fs = require('node:fs');
const CleanCSS = require("clean-css");

function minify(input, output) {
  const minifiedCode = new CleanCSS().minify([input]);
  fs.writeFileSync(output, minifiedCode.styles);

}

minify('.output/public/assets/css/light-theme.css', '.output/public/assets/css/light-theme.css')
minify('.output/public/assets/css/dark-theme.css', '.output/public/assets/css/dark-theme.css')
