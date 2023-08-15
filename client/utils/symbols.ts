import type { PContextMenu } from '#components'
import type { FilesTableContext } from '~/composables/fileTable/useFilesTable'
import type { UsersTableContext } from '~/composables/usersTable/useUsersTable'

export const FileTableKey = Symbol('fileTable') as InjectionKey<FilesTableContext>
export const UsersTableKey = Symbol('usersTable') as InjectionKey<UsersTableContext>

export const FileTableContextMenuKey = Symbol('fileTableContextMenu') as InjectionKey<Ref<InstanceType<typeof PContextMenu> | null>>
export const UsersTableContextMenuKey = Symbol('usersTableContextMenu') as InjectionKey<Ref<InstanceType<typeof PContextMenu> | null>>
