import type { AxiosError } from 'axios'

export interface MessageKeyDTO {
  key: string
  parameters?: Map<string, string>
}

export interface MessageDTO<T> {
  title?: MessageKeyDTO
  message: MessageKeyDTO
  payload?: T
}

export function getDTOFromError(error?: AxiosError): MessageKeyDTO {
  const data = error?.response?.data
  if (data)
    return data as MessageKeyDTO

  return { key: 'errors.unknown' }
}
