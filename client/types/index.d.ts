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

  interface TokenData {
    accessToken: string
    accessTokenExpiration: number
    refreshToken: string
    refreshTokenExpiration: number
  }

  type Role = 'REGULAR' | 'ADMIN' | 'OWNER'

  interface UserRepresentationDTO {
    id: string
    username: string
    email: string
    role: Role
    enabled: boolean
  }

  type FileVisibility = 'PUBLIC' | 'UNLISTED' | 'PRIVATE'
  type FileSizeScale = 'BINARY' | 'DECIMAL'
  type DateTimeStyle = 'SHORT' | 'MEDIUM' | 'LONG' | 'FULL'
  type Locale = 'en' | 'fr'

  interface UserSettings {
    defaultFileVisibility: FileVisibility
    autoRemoveFiles: boolean
    filesSizeScale: FileSizeScale
    preferredDateStyle: DateTimeStyle
    preferredTimeStyle: DateTimeStyle
    preferredLocale: Locale
  }

  interface SessionOpeningDTO {
    tokens: TokenData
    user: UserRepresentationDTO
    userSettings: UserSettings
  }
}
// END SECTION: DTO


export {}
