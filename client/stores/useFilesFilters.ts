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
  activeFilters: Ref<number>
  isEmpty: Ref<boolean>
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

  const activeFilters = computed(() => {
    return (
      (visibility.value.length > 0 ? 1 : 0)
      + (startingDate.value !== null ? 1 : 0)
      + (endingDate.value !== null ? 1 : 0)
      + (minimalSize.value !== null ? 1 : 0)
      + (maximalSize.value !== null ? 1 : 0)
      + (minimalViews.value !== null ? 1 : 0)
      + (maximalViews.value !== null ? 1 : 0)
      + (formats.value.length > 0 ? 1 : 0)
      + (owners.value.length > 0 ? 1 : 0)
    )
  })

  const isEmpty = computed(() => activeFilters.value === 0)

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
    activeFilters,
    isEmpty,
  }
}, {
  persist: {
    storage: persistedState.localStorage,
  },
}))
