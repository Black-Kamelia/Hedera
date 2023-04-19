const fs = require('node:fs')
const { ESLint } = require('eslint')

const fix = process.argv.includes('--fix') || process.argv.includes('-F')

const defaultOutput = 'eslint-report.html'
let outputIndex = process.argv.indexOf('-o')
if (outputIndex === -1)
  outputIndex = process.argv.indexOf('--output')
const output = outputIndex > -1 ? (process.argv[outputIndex + 1] ?? defaultOutput) : defaultOutput

const defaultExtensions = ['.js', '.ts', '.vue', '.json']
const extIndex = process.argv.indexOf('--ext')
const extensions = extIndex > -1 ? (process.argv[extIndex + 1]?.split(',') ?? defaultExtensions) : defaultExtensions

const eslint = new ESLint({
  extensions,
  ignore: true,
  useEslintrc: true,
  fix,
})

async function main() {
  const results = await eslint.lintFiles('.')
  await ESLint.outputFixes(results)
  const formatter = await eslint.loadFormatter('stylish')
  const resultText = formatter.format(results)
  console.log(resultText)
  const htmlFormatter = await eslint.loadFormatter('html')
  const html = htmlFormatter.format(results)
  fs.writeFileSync(output, html)
}

main()
