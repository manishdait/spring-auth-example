export type AuthRequest = {
  username: string,
  password: string,
  email: string
}

export type AuthResponse = {
  user: string,
  authToken: string,
  refreshToken: string
}

export type TokenRequest = {
  token: string
}

export type SignUpResponse = {
  user: string
}

export type ForgotPasswordRequest = {
  password: string;
}
