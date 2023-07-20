import type { useFilesFilters } from '~/stores/useFilesFilters'

export function filtersToDefinition(filters: ReturnType<typeof useFilesFilters>): FilterDefinitionDTO {
  const filtersDefinition: FilterDefinitionDTO = []

  if (filters.visibility.length > 0) {
    filtersDefinition.push(filters.visibility.map<FilterObject>(visibility => ({
      field: 'visibility',
      operator: 'eq',
      value: visibility,
      type: 'POSITIVE',
    })))
  }

  if (filters.startingDate !== null) {
    filtersDefinition.push([{
      field: 'createdAt',
      operator: 'ge',
      value: filters.startingDate.toISOString(),
      type: 'POSITIVE',
    }])
  }
  if (filters.endingDate !== null) {
    filtersDefinition.push([{
      field: 'createdAt',
      operator: 'le',
      value: filters.endingDate.toISOString(),
      type: 'POSITIVE',
    }])
  }

  if (filters.minimalSize !== null) {
    filtersDefinition.push([{
      field: 'size',
      operator: 'ge',
      value: `${filters.minimalSize.value};${filters.minimalSize.shift}`,
      type: 'POSITIVE',
    }])
  }
  if (filters.maximalSize !== null) {
    filtersDefinition.push([{
      field: 'size',
      operator: 'le',
      value: `${filters.maximalSize.value};${filters.maximalSize.shift}`,
      type: 'POSITIVE',
    }])
  }

  if (filters.formats.length > 0) {
    filtersDefinition.push(filters.formats.map<FilterObject>(format => ({
      field: 'mimeType',
      operator: 'eq',
      value: format,
      type: 'POSITIVE',
    })))
  }

  return filtersDefinition
}
