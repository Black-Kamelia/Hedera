import type { AxiosResponse } from 'axios'

export default function useRenameFile(onSuccess?: (response: AxiosResponse) => void) {
  const { t, m } = useI18n()
  const toast = useToast()
  const axios = useAxiosFactory()

  return function renameFile(fileId: string, newName: string) {
    return axios().put(`/files/${fileId}/name`, { name: newName })
      .then((response) => {
        toast.add({
          severity: 'success',
          summary: m(response.data.title),
          detail: { text: m(response.data.message) },
          life: 5000,
        })
        return response
      })
      .then(onSuccess)
      .catch(error => toast.add({
        severity: 'error',
        summary: t('pages.files.rename.error'),
        detail: { text: m(error) },
        life: 5000,
      }))
  }
}
