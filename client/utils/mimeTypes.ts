export type MediaType = 'image' | 'video' | 'audio' | 'document' | 'text' | 'zip' | 'unknown'

export function mimeTypeToMediaType(mimeType: string): MediaType {
  switch (mimeType) {
    case 'image/jpeg':
    case 'image/png':
    case 'image/gif':
    case 'image/svg+xml':
    case 'image/webp':
      return 'image'
    case 'video/mp4':
    case 'video/ogg':
    case 'video/webm':
    case 'video/quicktime':
      return 'video'
    case 'audio/mpeg':
    case 'audio/ogg':
    case 'audio/wav':
      return 'audio'
    case 'application/pdf':
      return 'document'
    case 'text/plain':
      return 'text'
    case 'application/zip':
      return 'zip'
    default:
      return 'unknown'
  }
}

export function mimeTypeToIcon(mimeType: string): string {
  switch (mimeTypeToMediaType(mimeType)) {
    case 'image':
      return 'i-tabler-photo'
    case 'audio':
      return 'i-tabler-music'
    case 'video':
      return 'i-tabler-video'
    case 'text':
      return 'i-tabler-file-text'
    case 'zip':
      return 'i-tabler-file-zip'
    case 'document':
      return 'i-tabler-file-text'
    case 'unknown':
      return 'i-tabler-file-unknown'
    default:
      return 'i-tabler-file'
  }
}
