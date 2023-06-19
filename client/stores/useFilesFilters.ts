import type { Ref } from 'vue'

interface Size {
  value: number
  shift: 0 | 10 | 20 | 30 | 40 | 50 | 60 | 70 | 80
}

export interface FilesFilters {
  visibility: Ref<string[]>
  startingDate: Ref<number | null>
  endingDate: Ref<number | null>
  minimalSize: Ref<Size | null>
  maximalSize: Ref<Size | null>
  minimalViews: Ref<number | null>
  maximalViews: Ref<number | null>
  formats: Ref<string[]>
  owners: Ref<string[]>
  reset: () => void
}

export const useFilesFilters = s<FilesFilters>(defineStore('filesFilters', (): FilesFilters => {
  const visibility = ref<string[]>([])
  const startingDate = ref<number | null>(null)
  const endingDate = ref<number | null>(null)
  const minimalSize = ref<Size | null>(null)
  const maximalSize = ref<Size | null>(null)
  const minimalViews = ref<number | null>(null)
  const maximalViews = ref<number | null>(null)
  const formats = ref<string[]>([])
  const owners = ref<string[]>([])

  function reset() {
    visibility.value = []
    startingDate.value = null
    endingDate.value = null
    minimalSize.value = null
    maximalSize.value = null
    minimalViews.value = null
    maximalViews.value = null
    formats.value = []
    owners.value = []
  }

  return {
    visibility,
    startingDate,
    endingDate,
    minimalSize,
    maximalSize,
    minimalViews,
    maximalViews,
    formats,
    owners,
    reset,
  }
}, {
  persist: {
    storage: persistedState.localStorage,
  },
}))
