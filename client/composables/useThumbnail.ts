import { blobToBase64 } from '~/utils/blobs'

/**
 * Composable for getting a thumbnail of a file. If the file type does not support thumbnails, the thumbnail will be null.
 *
 * @param code File code
 * @param mimeType File MIME type
 *
 * @returns Thumbnail, loading state and error state
 */
export function useThumbnail(code: string, mimeType: string) {
  if (mimeTypeToMediaType(mimeType) === 'image') {
    const { data, pending, error } = useAsyncData(`${code}_preview`, () => {
      return $fetchAPI<Blob>(`/files/${code}/thumbnail`, { responseType: 'blob' })
        .then(response => blobToBase64(response))
        .catch(() => null)
    })

    return {
      thumbnail: data,
      loading: readonly(pending),
      error: readonly(error),
    }
  }

  return {
    thumbnail: ref(null),
    loading: readonly(ref(false)),
    error: readonly(ref(false)),
  }
}
