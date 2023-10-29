export const CREATE_USER_FORM = {
  username: {
    min: 8,
    max: 128,
  },
  password: {
    min: 8,
    max: 128,
  },
}

export const UPDATE_PASSWORD_FORM = {
  password: {
    min: 8,
    max: 128,
  },
}

export const UPDATE_FILE_FORM = {
  filename: {
    min: 1,
    max: 255,
  },
}
