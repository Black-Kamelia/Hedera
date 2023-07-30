/**
 * This class represent the configuration file that is used to configure the *ShareX* app.
 */

class SharexConfiguration {
  private Version = '15.0.0'
  private Name: string
  private DestinationType = 'ImageUploader, TextUploader, FileUploader'
  private RequestMethod = 'POST'
  private RequestURL: string
  private Headers: object
  private Body = 'FormURLEncoded'
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
      'Content-Type': 'application/x-www-form-urlencoded',
      'Upload-Token': token,
    }
  }
}

export default function useSharexConfiguration() {
  const baseURL = window.location.origin

  function getConfig(name: string, token: string) {
    const config = JSON.stringify(new SharexConfiguration(`Hedera [${name}]`, baseURL, token))
    const blob = new Blob([config], { type: 'application/octet-stream' })
    downloadBlob(blob, 'hedera_sharex_config.sxcu')
  }

  return getConfig
}
