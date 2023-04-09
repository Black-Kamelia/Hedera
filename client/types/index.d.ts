declare global {
  type HIconClassName = `i-${string}`
  type Nullable<T> = T | null | undefined
  type CompElement<T = {}> = { $el: HTMLElement } & T
}

export {}
