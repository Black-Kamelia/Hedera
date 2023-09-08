const MAX_BINARY_UNIT_SHIFT = 40
const MAX_DECIMAL_UNIT_SHIFT = 12

export type FileSizeShift = 0 | 3 | 6 | 9 | 12 | 10 | 20 | 30 | 40

export interface FileSize {
  value: string
  shift: FileSizeShift
}

export default function useHumanFileSize() {
  const { t, n } = useI18n()
  const { filesSizeScale } = storeToRefs(useUserSettings())

  function _format(fileSize: number, scale: string, scaleValue: number, scaleShift: number, maxScaleUnitShift: number) {
    let value = fileSize
    let shift = 0
    while (value >= scaleValue && shift < maxScaleUnitShift) {
      value /= scaleValue
      shift += scaleShift
    }

    return `${n(value, { style: 'decimal', minimumFractionDigits: 2, maximumFractionDigits: 2 })} ${t(`size_units.${scale}.${shift}`)}`
  }

  function format(fileSize: number) {
    switch (filesSizeScale.value) {
      case 'BINARY': return _format(fileSize, 'binary', 1024, 10, MAX_BINARY_UNIT_SHIFT)
      case 'DECIMAL': return _format(fileSize, 'decimal', 1000, 3, MAX_DECIMAL_UNIT_SHIFT)
    }
  }

  function _computeShift(fileSize: number, scaleValue: number, scaleShift: number, maxScaleUnitShift: number): FileSize {
    let value = fileSize
    let shift = 0
    while (value >= scaleValue && shift < maxScaleUnitShift) {
      value /= scaleValue
      shift += scaleShift
    }

    return {
      value: (Math.round((value + Number.EPSILON) * 100) / 100).toFixed(2),
      shift: shift as FileSizeShift,
    }
  }

  function computeShift(fileSize: number): FileSize {
    switch (filesSizeScale.value) {
      case 'BINARY': return _computeShift(fileSize, 1024, 10, MAX_BINARY_UNIT_SHIFT)
      case 'DECIMAL': return _computeShift(fileSize, 1000, 3, MAX_DECIMAL_UNIT_SHIFT)
    }
  }

  return { format, computeShift }
}
