export default function useDownloadFile() {
  const { selectedRow, unselectRow } = useFilesTable()

  return function deleteFile() {
    if (!selectedRow.value)
      return

    $fetchAPI<Blob>(`/files/${selectedRow.value!.code}`, { responseType: 'blob' })
      .then(response => downloadBlob(response, selectedRow.value!.name))
      .finally(unselectRow)
  }
}
