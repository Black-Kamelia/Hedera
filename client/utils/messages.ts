export interface MessageKeyDTO {
  key: string
  parameters?: Record<string, MessageKeyDTO>
}

export interface MessageDTO<T> {
  title?: MessageKeyDTO
  message: MessageKeyDTO
  payload?: T
  fields: Map<string, string>
}
