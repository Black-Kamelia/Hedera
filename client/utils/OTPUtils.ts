export const OTP_LENGTH = 6

export const OTP_REGEX = new RegExp(`^\\d{${OTP_LENGTH}}$`)

export function createOTPFromArray(array: Nullable<number>[]): OTP {
  if (array.length !== OTP_LENGTH) throw new Error(`Array length is not ${OTP_LENGTH}, but ${array.length}`)

  return array as OTP
}

export function createOTPFromString(str: string): OTP {
  return createOTPFromArray(str.split('').map(c => Number.parseInt(c)))
}

export function createEmptyOTP(): OTP {
  return createOTPFromArray(Array(OTP_LENGTH).fill(null))
}

/**
 * Converts a function which returns an array of nullable numbers (Nullable<number>[]) and with some arguments,
 * to a function which returns an OTP and with the same arguments.
 *
 * @param fn The function to convert
 * @returns The converted function
 */
export function asOTPFn<T extends (...args: any[]) => Nullable<number>[]>(fn: T): (...args: Parameters<T>) => OTP {
  return function wrapped(...args: Parameters<T>) {
    const otp = fn(...args)
    if (otp.length !== OTP_LENGTH) throw new Error(`OTP length is not ${OTP_LENGTH}, but ${otp.length}`)

    return otp as OTP
  }
}

export const OTPWith = asOTPFn(replacedAt<Nullable<number>>)
