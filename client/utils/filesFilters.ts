import type { useFilesFilters } from '~/stores/useFilesFilters'

export function filtersToDefinition(filters: ReturnType<typeof useFilesFilters>, query?: string): FilterDefinitionDTO {
  const filtersDefinition: FilterDefinitionDTO = []

  if (filters.visibility.length > 0) {
    filtersDefinition.push(filters.visibility.map(visibility => ({
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
      value: filters.minimalSize.toString(),
      type: 'POSITIVE',
    }])
  }
  if (filters.maximalSize !== null) {
    filtersDefinition.push([{
      field: 'size',
      operator: 'le',
      value: filters.maximalSize.toString(),
      type: 'POSITIVE',
    }])
  }

  if (filters.formats.length > 0) {
    filtersDefinition.push(filters.formats.map(format => ({
      field: 'mimeType',
      operator: 'eq',
      value: format,
      type: 'POSITIVE',
    })))
  }

  if (filters.tokens.length > 0) {
    const tokensFilter: FilterObject[] = filters.tokens.map(token => ({
      field: 'token',
      operator: 'eq',
      value: token,
      type: 'POSITIVE',
    }))

    if (filters.noToken) {
      tokensFilter.push({
        field: 'token',
        operator: 'eqn',
        value: '',
        type: 'POSITIVE',
      })
    }

    filtersDefinition.push(tokensFilter)
  } else if (filters.noToken) {
    filtersDefinition.push([{
      field: 'token',
      operator: 'eqn',
      value: '',
      type: 'POSITIVE',
    }])
  }

  if (query !== undefined && query.length > 0) {
    filtersDefinition.push([{
      field: 'name',
      operator: 'fuzzy',
      value: query,
      type: 'POSITIVE',
    }])
  }

  return filtersDefinition
}
