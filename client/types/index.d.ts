// BEGIN SECTION: Utils
type _TupleOf<T, N extends number, R extends unknown[]> = R['length'] extends N ? R : _TupleOf<T, N, [T, ...R]>;

declare global {
  type Nullable<T> = T | null | undefined
  type Tuple<T, N extends number> = N extends N ? number extends N ? T[] : _TupleOf<T, N, []> : never;
}
// END SECTION: Utils


// BEGIN SECTION: DOM
declare global {
  type CompElement<T = {}> = { $el: HTMLElement } & T
  type OnlyProps<T> = Omit<T, "$attrs">
}
// END SECTION: DOM


// BEGIN SECTION: Others
declare global {
  type OTP_LENGTH_T = 6
  type OTP = Tuple<Nullable<number>, OTP_LENGTH_T>

  interface FileSize {
    value: number
    shift: 0 | 10 | 20 | 30 | 40 | 50
  }
}
// END SECTION: Others


// BEGIN SECTION: DTO
declare global {
  interface PageDTO<E> {
    items: Array<E>,
    page: number,
    pageSize: number,
    totalPages: number,
    totalItems: number,
  }

  interface PageableDTO {
    page: PageDTO<FileRepresentationDTO>
  }

  interface FileOwnerDTO {
    id: string
    username: string
  }

  interface FileRepresentationDTO {
    id: string,
    code: string,
    name: string,
    mimeType: string,
    size: FileSize,
    visibility: string,
    owner: FileOwnerDTO,
    creationDate: string,
  }
}
// END SECTION: DTO


export {}
