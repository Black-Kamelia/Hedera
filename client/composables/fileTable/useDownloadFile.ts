export default function useDownloadFile() {
  const axios = useAxiosFactory()
  const { selectedRow, unselectRow } = useFilesTable()

  return function deleteFile() {
    if (!selectedRow.value)
      return

    axios().get(`/files/${selectedRow.value!.code}`, { responseType: 'blob' })
      .then(response => downloadBlob(response.data, selectedRow.value!.name))
      .finally(unselectRow)
  }
}
