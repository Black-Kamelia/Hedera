/**
 * Returns a human-readable string representing the given number of bytes.
 * @param bytes The number of bytes to convert.
 * @param t The translation function.
 */
export function humanSize(bytes: number, locale: string, t: (s: string) => string) {
  const shift = bytesToBiggestUnitShift(bytes)
  return `${(bytes / (1 << shift)).toLocaleString(locale, { minimumFractionDigits: 2 })} ${t(`size_units.binary.${shift}`)}`
}

/**
 * Returns a human-readable string representing the given file size structure.
 * @param size The structure containing the number to display and the shift of the unit.
 * @param locale The locale to use for the number formatting.
 * @param t The translation function.
 */
export function humanSizeStructure(size: FileSize, locale: string, t: (s: string) => string) {
  return `${size.value.toLocaleString(locale, { minimumFractionDigits: 2 })} ${t(`size_units.binary.${size.shift}`)}`
}

/**
 * Returns the biggest unit that can be used to represent the given number of bytes.
 * The returned value is a multiple of 10, so that it can be used to shift the bytes value.
 *
 * @param bytes The number of bytes to convert.
 */
function bytesToBiggestUnitShift(bytes: number) {
  let unitIndex = 0
  while (bytes >= 1024) {
    bytes >>= 10
    unitIndex++
  }
  return unitIndex * 10
}