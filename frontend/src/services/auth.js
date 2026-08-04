// Real authentication service — calls the Spring Boot /api/auth endpoints
// (BCrypt password hashing + JWT issued/verified server-side).
import { apiClient } from './api'

const SESSION_KEY = 'tm_auth_session'

const decodeJwtPayload = (token) => {
  const base64Url = token.split('.')[1]
  const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/')
  const padded = base64 + '='.repeat((4 - (base64.length % 4)) % 4)
  return JSON.parse(atob(padded))
}

const readSession = () => {
  const stored = localStorage.getItem(SESSION_KEY) || sessionStorage.getItem(SESSION_KEY)

  if (!stored) {
    return null
  }

  try {
    const session = JSON.parse(stored)

    if (session.expiresAt && session.expiresAt < Date.now()) {
      localStorage.removeItem(SESSION_KEY)
      sessionStorage.removeItem(SESSION_KEY)
      return null
    }

    return session
  } catch {
    localStorage.removeItem(SESSION_KEY)
    sessionStorage.removeItem(SESSION_KEY)
    return null
  }
}

const buildSession = (data, rememberMe) => {
  const { token, user } = data
  const payload = decodeJwtPayload(token)
  const session = { token, user, expiresAt: payload.exp * 1000 }

  const storage = rememberMe ? localStorage : sessionStorage
  const otherStorage = rememberMe ? sessionStorage : localStorage
  otherStorage.removeItem(SESSION_KEY)
  storage.setItem(SESSION_KEY, JSON.stringify(session))

  return session
}

export const login = async (username, password, rememberMe = false) => {
  let response

  try {
    response = await apiClient.post('/auth/login', { username, password })
  } catch (error) {
    if (error.response?.status === 401) {
      throw new Error(error.response.data?.message || 'Invalid username or password.', {
        cause: error,
      })
    }
    throw new Error('Unable to reach authentication service.', { cause: error })
  }

  return buildSession(response.data, rememberMe)
}

export const logout = () => {
  localStorage.removeItem(SESSION_KEY)
  sessionStorage.removeItem(SESSION_KEY)
}

export const isAuthenticated = () => Boolean(readSession()?.token)

export const getSession = () => readSession()

// Simplified training-project flow: verify the Employee ID exists, then set a new
// BCrypt-hashed password directly. No OTP/email/reset-token involved.
export const verifyEmployeeId = async (employeeId) => {
  try {
    const response = await apiClient.post('/auth/forgot-password', { employeeId })
    return response.data.message
  } catch (error) {
    throw new Error(error.response?.data?.message || 'Unable to verify Employee ID.', {
      cause: error,
    })
  }
}

export const updatePassword = async (employeeId, newPassword) => {
  try {
    await apiClient.post('/auth/reset-password', { employeeId, newPassword })
  } catch (error) {
    throw new Error(error.response?.data?.message || 'Unable to update password.', {
      cause: error,
    })
  }
}
