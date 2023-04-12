export const OTP_LENGTH = 6

export const OTP_REGEX = new RegExp(`^\\d{${OTP_LENGTH}}$`)

export default function createOTPArray(): Ref<OTP> {
  const otp: OTP = Array(6).fill(null) as OTP
  return ref(otp)
}
