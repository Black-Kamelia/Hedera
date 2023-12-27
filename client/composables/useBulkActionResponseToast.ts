import type { MessageDTO } from '~/utils/messages'

export default function useBulkActionResponseToast() {
  const toast = useToast()
  const { m } = useI18n()

  function handleResponse(response: MessageDTO<BulkActionSummaryDTO<any>>) {
    if (response.payload!.success > 0 && response.payload!.fail === 0) {
      toast.add({
        severity: 'success',
        summary: m(response.title),
        detail: { text: response.message ? m(response.message) : null },
        life: 5000,
      })
    } else if (response.payload!.success > 0 && response.payload!.fail > 0) {
      toast.add({
        severity: 'warn',
        summary: m(response.title),
        detail: { text: response.message ? m(response.message) : null },
        life: 5000,
      })
    } else if (response.payload!.success === 0 && response.payload!.fail > 0) {
      toast.add({
        severity: 'error',
        summary: m(response.title),
        detail: { text: response.message ? m(response.message) : null },
        life: 5000,
      })
    }
  }

  return handleResponse
}
