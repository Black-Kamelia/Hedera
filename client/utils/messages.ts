import type { AxiosError } from 'axios'

export interface MessageDTO {
  key: string
  template?: Map<string, string>
}

export function getDTOFromError(error?: AxiosError): MessageDTO {
  const data = error?.response?.data
  if (data)
    return data as MessageDTO

  return { key: 'errors.unknown' }
}
