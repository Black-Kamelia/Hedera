class SharexConfiguration {
  private Version = '15.0.0'
  private Name: string
  private DestinationType = 'ImageUploader, TextUploader, FileUploader'
  private RequestMethod = 'POST'
  private RequestURL: string
  private Headers: object
  private Body = 'MultipartFormData'
  private FileFormName = 'file'
  private URL: string
  private ThumbnailURL: string
  private DeletionURL = ''
  private ErrorMessage = ''

  constructor(name: string, baseURL: string, token: string) {
    this.Name = name
    this.RequestURL = `${baseURL}/api/files/upload/token`
    this.URL = `${baseURL}/{json:code}`
    this.ThumbnailURL = `${baseURL}/{json:code}`
    this.Headers = {
      'Content-Type': 'multipart/form-data',
      'Upload-Token': token,
    }
  }
}

export default function getSharexConfiguration(name: string, token: string) {
  const baseURL = window.location.origin
  const config = JSON.stringify(new SharexConfiguration(`Hedera [${name}]`, baseURL, token))
  const blob = new Blob([config], { type: 'application/octet-stream' })
  downloadBlob(blob, 'hedera_sharex_config.sxcu')
}
