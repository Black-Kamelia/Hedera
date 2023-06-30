import type { AxiosResponse } from 'axios/index'

export default function useDeleteFile(onSuccess?: (response: AxiosResponse) => void) {
  const { t, m } = useI18n()
  const toast = useToast()
  const axios = useAxiosFactory()

  return function deleteFile(fileId: string) {
    return axios().delete(`/files/${fileId}`)
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
        summary: t('pages.files.delete.error'),
        detail: { text: m(error) },
        life: 5000,
      }))
  }
}
