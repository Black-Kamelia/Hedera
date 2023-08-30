const MAX_BINARY_UNIT_SHIFT = 40
const MAX_DECIMAL_UNIT_SHIFT = 12

export interface FileSize {
  value: string
  shift: 0 | 10 | 20 | 30 | 40
}

export default function useHumanFileSize() {
  const { t, n } = useI18n()
  const { filesSizeScale } = storeToRefs(useUserSettings())

  function formatBinary(fileSize: number) {
    let value = fileSize
    let shift = 0
    while (value >= 1024 && shift < MAX_BINARY_UNIT_SHIFT) {
      value /= 1024
      shift += 10
    }

    return `${n(value, { style: 'decimal', minimumFractionDigits: 2, maximumFractionDigits: 2 })} ${t(`size_units.binary.${shift}`)}`
  }

  function formatDecimal(fileSize: number) {
    let value = fileSize
    let shift = 0
    while (value >= 1000 && shift < MAX_DECIMAL_UNIT_SHIFT) {
      value /= 1000
      shift += 3
    }

    return `${n(value, { style: 'decimal', minimumFractionDigits: 2, maximumFractionDigits: 2 })} ${t(`size_units.decimal.${shift}`)}`
  }

  function format(fileSize: number) {
    switch (filesSizeScale.value) {
      case 'BINARY': return formatBinary(fileSize)
      case 'DECIMAL': return formatDecimal(fileSize)
    }
  }

  function computeShift(fileSize: number): FileSize {
    let value = fileSize
    let shift = 0
    while (value >= 1024 && shift < MAX_BINARY_UNIT_SHIFT) {
      value /= 1024
      shift += 10
    }

    return {
      value: (Math.round((value + Number.EPSILON) * 100) / 100).toFixed(2),
      shift: shift as (0 | 10 | 20 | 30 | 40),
    }
  }

  return { format, computeShift }
}
