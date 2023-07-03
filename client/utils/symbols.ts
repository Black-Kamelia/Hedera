import type { PContextMenu } from '~/.nuxt/components'
import type { FilesTableContext } from '~/composables/fileTable/useFilesTable'

export const FileTableKey = Symbol('fileTable') as InjectionKey<FilesTableContext>

export const FileTableContextMenuKey = Symbol('fileTableContextMenu') as InjectionKey<Ref<InstanceType<typeof PContextMenu> | null>>
