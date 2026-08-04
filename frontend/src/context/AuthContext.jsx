import { useCallback, useEffect, useState } from 'react'
import {
  getSession,
  login as loginRequest,
  logout as logoutSession,
  updatePassword,
  verifyEmployeeId,
} from '../services/auth'
import { AuthContext } from './authContextObject'

export function AuthProvider({ children }) {
  const [session, setSession] = useState(getSession)
  const [authError, setAuthError] = useState('')

  const user = session?.user || null
  const token = session?.token || null
  const role = user?.role || null

  // Keeps auth state in sync if the user logs out from another tab.
  useEffect(() => {
    const handleStorage = () => setSession(getSession())
    window.addEventListener('storage', handleStorage)
    return () => window.removeEventListener('storage', handleStorage)
  }, [])

  const login = useCallback(async (username, password, rememberMe) => {
    setAuthError('')

    try {
      const newSession = await loginRequest(username, password, rememberMe)
      setSession(newSession)
      return true
    } catch (error) {
      setAuthError(error.message || 'Login failed.')
      return false
    }
  }, [])

  const logout = useCallback(() => {
    logoutSession()
    setSession(null)
  }, [])

  const value = {
    user,
    token,
    role,
    isAuthenticated: Boolean(token),
    isAuthLoading: false,
    authError,
    login,
    logout,
    verifyEmployeeId,
    updatePassword,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
