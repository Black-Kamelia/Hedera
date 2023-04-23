import type { AxiosError } from 'axios'

export interface ErrorDTO {
  key: string
  template?: Map<string, string>
}

export function getDTOFromError(error?: AxiosError): ErrorDTO {
  const data = error?.response?.data
  if (data)
    return data as ErrorDTO

  return { key: 'errors.unknown' }
}
