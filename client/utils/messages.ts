export interface MessageKeyDTO {
  key: string
  parameters?: Map<string, string>
}

export interface MessageDTO<T> {
  title?: MessageKeyDTO
  message: MessageKeyDTO
  payload?: T
}
