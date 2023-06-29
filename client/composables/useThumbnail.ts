import { blobToBase64 } from '~/utils/blobs'

export default function useThumbnail(code: string) {
  const thumbnail = ref<string | null>(null)
  const isLoading = ref(true)
  const isError = ref(false)
  const axios = useAxiosFactory()

  axios().get(`/files/${code}`, { responseType: 'blob' })
    .then(response => blobToBase64(response.data))
    .then(base64 => thumbnail.value = base64)
    .catch((error) => {
      if (error.response && error.response.status !== 200)
        isError.value = true
      thumbnail.value = null
    })
    .finally(() => isLoading.value = false)

  return {
    thumbnail,
    isLoading,
    isError,
  }
}
