const MAX_UNIT_SHIFT = 40

export interface FileSize {
  value: string
  shift: 0 | 10 | 20 | 30 | 40
}

export default function useHumanFileSize() {
  const { t, n } = useI18n()

  function format(fileSize: number) {
    let value = fileSize
    let shift = 0
    while (value >= 1024 && shift < MAX_UNIT_SHIFT) {
      value /= 1024
      shift += 10
    }

    return `${n(value, { style: 'decimal', minimumFractionDigits: 2, maximumFractionDigits: 2 })} ${t(`size_units.binary.${shift}`)}`
  }

  function computeShift(fileSize: number): FileSize {
    let value = fileSize
    let shift = 0
    while (value >= 1024 && shift < MAX_UNIT_SHIFT) {
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
