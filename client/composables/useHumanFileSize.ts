const shifts: [0, 10, 20, 30, 40, 50] = [0, 10, 20, 30, 40, 50]

export default function useHumanFileSize() {
  const { t, n } = useI18n()
  const { filesSizeScale } = storeToRefs(useUserSettings())
  const base = computed(() => filesSizeScale.value === 'BINARY' ? 1024 : 1000)

  function format(fileSize: FileSize) {
    return `${n(fileSize.value, { style: 'decimal', minimumFractionDigits: 2, maximumFractionDigits: 2 })} ${t(`size_units.binary.${fileSize.shift}`)}`
  }

  function autoFormat(bytes: number) {
    const shift = Math.floor(Math.log(bytes) / Math.log(base.value))
    return format({ value: bytes / base.value ** shift, shift: shifts[shift] })
  }

  return { format, autoFormat }
}
