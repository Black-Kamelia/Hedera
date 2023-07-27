import type { PContextMenu } from '#components'
import type { FilesTableContext } from '~/composables/fileTable/useFilesTable'
import type { PersonalTokensListContext } from '~/composables/tokens/usePersonalTokensList'

export const FileTableKey = Symbol('fileTable') as InjectionKey<FilesTableContext>

export const PersonalTokensListKey = Symbol('personalTokensList') as InjectionKey<PersonalTokensListContext>

export const FileTableContextMenuKey = Symbol('fileTableContextMenu') as InjectionKey<Ref<InstanceType<typeof PContextMenu> | null>>
