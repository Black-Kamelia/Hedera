/**
 * These classes represent the configuration file that is used to configure the *uPic* app.
 *
 * Please note that weird practices like nested JSON.stringify or typos in the class fields names are intentional
 * and are required to generate the correct configuration file that can be used by the *uPic* app.
 */

class UpicConfigurationData {
  private bodys = JSON.stringify([])
  private domain: string
  private field = 'file'
  private headers: string
  private method = 'POST'
  private resultPath = '["payload.code"]'
  private saveKeyPath = ''
  private suffix = ''
  private url: string
  private useBase64 = false

  constructor(baseURL: string, token: string) {
    this.domain = baseURL
    this.headers = JSON.stringify([
      { key: 'Content-Type', value: 'multipart/form-data' },
      { key: 'Upload-Token', value: token },
    ])
    this.url = `${baseURL}/api/files/upload/token`
  }
}

class UpicConfiguration {
  private id: string
  private name: string
  private data: string
  private type = 'custom'

  constructor(name: string, baseURL: string, token: string) {
    this.id = Math.ceil(new Date().getTime() / 1000).toString()
    this.name = name
    this.data = JSON.stringify(new UpicConfigurationData(baseURL, token))
  }
}

export default function getUpicConfiguration(name: string, token: string) {
  const baseURL = window.location.origin
  const config = JSON.stringify([JSON.stringify(new UpicConfiguration(`Hedera [${name}]`, baseURL, token))])
  const blob = new Blob([config], { type: 'application/octet-stream' })
  downloadBlob(blob, 'hedera_upic_config.json')
}
