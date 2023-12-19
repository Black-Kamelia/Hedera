export interface MessageKeyDTO {
  key: string
  parameters?: { [key: string]: MessageKeyDTO }
}

export interface MessageDTO<T> {
  title?: MessageKeyDTO
  message: MessageKeyDTO
  payload?: T
  fields: Map<string, string>
}
