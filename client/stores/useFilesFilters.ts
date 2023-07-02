import type { Ref } from 'vue'

export interface FileFilters {
  visibility: string[]
  startingDate: Date | null
  endingDate: Date | null
  minimalSize: FileSize | null
  maximalSize: FileSize | null
  minimalViews: number | null
  maximalViews: number | null
  formats: string[]
  owners: string[]
}

export interface FilesFiltersStore {
  visibility: Ref<string[]>
  startingDate: Ref<Date | null>
  endingDate: Ref<Date | null>
  minimalSize: Ref<FileSize | null>
  maximalSize: Ref<FileSize | null>
  minimalViews: Ref<number | null>
  maximalViews: Ref<number | null>
  formats: Ref<string[]>
  owners: Ref<string[]>
  activeFilters: Ref<number>
  isEmpty: Ref<boolean>
  updateFilters: (filters: Partial<FileFilters>) => void
  reset: () => void
}

export const useFilesFilters = defineStore('filesFilters', (): FilesFiltersStore => {
  const visibility = ref<string[]>([])
  const startingDate = ref<Date | null>(null)
  const endingDate = ref<Date | null>(null)
  const minimalSize = ref<FileSize | null>(null)
  const maximalSize = ref<FileSize | null>(null)
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

  function updateFilters(filters: Partial<FileFilters>) {
    if (filters.visibility !== undefined)
      visibility.value = filters.visibility

    if (filters.startingDate !== undefined)
      startingDate.value = filters.startingDate

    if (filters.endingDate !== undefined)
      endingDate.value = filters.endingDate

    if (filters.minimalSize !== undefined)
      minimalSize.value = filters.minimalSize

    if (filters.maximalSize !== undefined)
      maximalSize.value = filters.maximalSize

    if (filters.minimalViews !== undefined)
      minimalViews.value = filters.minimalViews

    if (filters.maximalViews !== undefined)
      maximalViews.value = filters.maximalViews

    if (filters.formats !== undefined)
      formats.value = filters.formats

    if (filters.owners !== undefined)
      owners.value = filters.owners
  }

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
    activeFilters,
    isEmpty,
    updateFilters,
    reset,
  }
}, {
  persist: {
    storage: persistedState.localStorage,
    serializer: jsonDateSerializer,
  },
})

export function reactiveFilters(filters: ReturnType<typeof useFilesFilters>) {
  return reactive({
    visibility: filters.visibility,
    startingDate: filters.startingDate,
    endingDate: filters.endingDate,
    minimalSize: filters.minimalSize,
    maximalSize: filters.maximalSize,
    minimalViews: filters.minimalViews,
    maximalViews: filters.maximalViews,
    formats: filters.formats,
    owners: filters.owners,
  })
}

export function loadFilters(localFilters: FileFilters, filters: ReturnType<typeof useFilesFilters>) {
  localFilters.visibility = filters.visibility
  localFilters.startingDate = filters.startingDate
  localFilters.endingDate = filters.endingDate
  localFilters.minimalSize = filters.minimalSize
  localFilters.maximalSize = filters.maximalSize
  localFilters.minimalViews = filters.minimalViews
  localFilters.maximalViews = filters.maximalViews
  localFilters.formats = filters.formats
  localFilters.owners = filters.owners
}
