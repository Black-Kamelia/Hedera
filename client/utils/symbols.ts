import type { PContextMenu } from '#components'
import type { FilesTableContext } from '~/composables/fileTable/useFilesTable'
import type { UserSettingsContext } from '~/composables/settings/useSettingsPage'

export const FileTableKey = Symbol('fileTable') as InjectionKey<FilesTableContext>

export const FileTableContextMenuKey = Symbol('fileTableContextMenu') as InjectionKey<Ref<InstanceType<typeof PContextMenu> | null>>

export const UserSettingsKey = Symbol('settings') as InjectionKey<UserSettingsContext>
