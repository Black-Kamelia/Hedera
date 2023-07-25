export default function useHumanFileSize() {
  const { t, n } = useI18n()

  function format(fileSize: FileSize) {
    return `${n(fileSize.value, { style: 'decimal', minimumFractionDigits: 2, maximumFractionDigits: 2 })} ${t(`size_units.binary.${fileSize.shift}`)}`
  }

  return { format }
}
