import type { FetchError } from 'ofetch'
import { blobToBase64 } from '~/utils/blobs'

/**
 * Composable for getting a thumbnail of a file. If the file type does not support thumbnails, the thumbnail will be null.
 *
 * MIME types supported:
 * - `image/*`
 *
 * @param code File code
 * @param mimeType File MIME type
 *
 * @returns Thumbnail, loading state and error state
 */
export function useThumbnail(code: string, mimeType: string) {
  const thumbnail = ref<string | null>(null)
  const isLoading = ref(true)
  const isError = ref(false)

  if (mimeType.startsWith('image/')) {
    $fetchAPI<Blob>(`/files/${code}`, { responseType: 'blob' })
      .then(response => blobToBase64(response))
      .then(base64 => thumbnail.value = base64)
      .catch((error: FetchError) => {
        if (error.response && error.response.status !== 200) {
          isError.value = true
        }
        thumbnail.value = null
      })
      .finally(() => isLoading.value = false)
  } else {
    isLoading.value = false
  }

  return {
    thumbnail,
    isLoading,
    isError,
  }
}
