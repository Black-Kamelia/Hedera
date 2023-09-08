// BEGIN SECTION: Utils
type _TupleOf<T, N extends number, R extends unknown[]> = R['length'] extends N ? R : _TupleOf<T, N, [T, ...R]>;

declare global {
  type Nullable<T> = T | null | undefined
  type Tuple<T, N extends number> = N extends N ? number extends N ? T[] : _TupleOf<T, N, []> : never;
  type KeysOf<T> = Array<
    T extends T // Include all keys of union types, not just common keys
    ? keyof T extends string
      ? keyof T
      : never
    : never
  >
  type PickFrom<T, K extends Array<string>> = T extends Array<any>
    ? T
    : T extends Record<string, any>
      ? keyof T extends K[number]
        ? T // Exact same keys as the target, skip Pick
        : K[number] extends never
          ? T
          : Pick<T, K[number]>
      : T
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

  interface PageableDTO<E> {
    page: PageDTO<E>
  }

  interface PageDefinitionDTO {
    filters?: FilterDefinitionDTO
    sorter?: SorterDefinitionDTO
  }

  type FilterDefinitionDTO = FilterGroupDTO[]
  type FilterGroupDTO = FilterObject[]

  interface FilterObject{
    field: string
    operator: string
    value: string
    type: FilterType
  }

  type SorterDefinitionDTO = SortObject[]

  interface SortObject {
    field: string
    direction: SortDirection
  }

  type FilterType = 'POSITIVE' | 'NEGATIVE'
  type SortDirection = 'ASC' | 'DESC'

  interface FileOwnerDTO {
    id: string
    username: string
  }

  interface FileRepresentationDTO {
    id: string,
    code: string,
    name: string,
    mimeType: string,
    size: number,
    visibility: string,
    owner: FileOwnerDTO,
    createdAt: string,
  }

  interface TokenData {
    accessToken: string
    accessTokenExpiration: number
    refreshToken: string
    refreshTokenExpiration: number
  }

  type Role = 'REGULAR' | 'ADMIN' | 'OWNER' | undefined

  interface UserRepresentationDTO {
    id: string
    username: string
    email: string
    role: Role
    enabled: boolean
    currentDiskQuota: number,
    currentDiskQuotaRatio: number,
    maximumDiskQuota: number,
    createdAt: string
  }

  interface UserCreationDTO {
    username: string
    email: string
    password: string
    role: Role
    diskQuota?: number
    forceChangePassword?: boolean
  }

  type FileVisibility = 'PUBLIC' | 'UNLISTED' | 'PRIVATE'
  type FileSizeScale = 'BINARY' | 'DECIMAL'
  type DateTimeStyle = 'SHORT' | 'MEDIUM' | 'LONG' | 'FULL'
  type Locale = 'en' | 'fr'
  type UploadBehavior = 'INSTANT' | 'MANUAL'

  interface UserSettings {
    defaultFileVisibility: FileVisibility
    autoRemoveFiles: boolean
    filesSizeScale: FileSizeScale
    preferredDateStyle: DateTimeStyle
    preferredTimeStyle: DateTimeStyle
    preferredLocale: Locale
    uploadBehavior: UploadBehavior
  }

  interface SessionOpeningDTO {
    tokens: TokenData
    user: UserRepresentationDTO
    userSettings: UserSettings
  }

  interface PersonalTokenDTO {
    id: string
    token?: string
    name: string
    createdAt: string
    lastUsed?: string
  }
}
// END SECTION: DTO


export {}
