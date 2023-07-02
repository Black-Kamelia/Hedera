import type { FilesTableContext } from '~/composables/fileTable/useFilesTable'

export const FileTableKey = Symbol('fileTable') as InjectionKey<FilesTableContext>
