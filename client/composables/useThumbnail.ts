import type { FetchError } from 'ofetch'
import type { MaybeRef } from 'vue'

const SUPPORTED_IMAGE_TYPES = ['image/png', 'image/jpeg', 'image/gif']
const CACHE_LIMIT = 50

const thumbnails: Record<string, string> = {}
const queue: string[] = []

/**
 * Composable for getting a thumbnail of a file. If the file type does not support thumbnails, the thumbnail will be null.
 *
 * @param code File code
 * @param mimeType File MIME type
 *
 * @returns Thumbnail, loading state and error state
 */
export function useThumbnail(code: MaybeRef<string>, mimeType: MaybeRef<string>) {
  const data = ref<string | null>(null)
  const loading = ref(false)
  const error = ref<FetchError | null>(null)

  watchEffect((onCleanup) => {
    const rawCode = unref(code)
    const rawMimeType = unref(mimeType)

    if (!SUPPORTED_IMAGE_TYPES.includes(rawMimeType)) /* mime type is not supported */ return

    const thumbnail = thumbnails[rawCode]
    if (thumbnail) {
      data.value = thumbnail
      return
    }

    if (queue.length >= CACHE_LIMIT) {
      const oldest = queue.pop()
      if (oldest) delete thumbnails[oldest]
    }

    const abortController = new AbortController()
    loading.value = true
    $fetchAPI<Blob>(`/files/${rawCode}/thumbnail`, { responseType: 'blob', signal: abortController.signal })
      .then(data => blobToBase64(data))
      .then((base64) => {
        thumbnails[rawCode] = base64
        queue.unshift(rawCode)
        data.value = base64
      })
      .catch((err) => {
        error.value = err
      })
      .finally(() => {
        loading.value = false
      })
    onCleanup(() => abortController.abort())
  })

  return { thumbnail: data, loading: readonly(loading), error: readonly(error) }
}
