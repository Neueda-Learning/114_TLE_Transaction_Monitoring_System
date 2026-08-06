import { beforeEach, describe, expect, it, vi } from 'vitest'

vi.mock('./api', () => ({
  apiClient: {
    post: vi.fn(),
  },
}))

import { apiClient } from './api'
import { getSession, login, logout } from './auth'

const SESSION_KEY = 'tm_auth_session'
const TEST_TOKEN = [
  'header',
  btoa(JSON.stringify({ exp: Math.floor(Date.now() / 1000) + 3600 })),
  'signature',
].join('.')

describe('auth service', () => {
  beforeEach(() => {
    localStorage.clear()
    sessionStorage.clear()
    vi.clearAllMocks()
  })

  it('stores remembered sessions in localStorage', async () => {
    apiClient.post.mockResolvedValue({
      data: {
        token: TEST_TOKEN,
        user: { username: 'admin', role: 'ADMIN' },
      },
    })

    const session = await login('admin', 'admin123', true)

    expect(apiClient.post).toHaveBeenCalledWith('/auth/login', {
      username: 'admin',
      password: 'admin123',
    })
    expect(session.user.username).toBe('admin')
    expect(JSON.parse(localStorage.getItem(SESSION_KEY))).toMatchObject({
      token: TEST_TOKEN,
    })
    expect(sessionStorage.getItem(SESSION_KEY)).toBeNull()
  })

  it('stores non-remembered sessions in sessionStorage', async () => {
    apiClient.post.mockResolvedValue({
      data: {
        token: TEST_TOKEN,
        user: { username: 'analyst', role: 'ANALYST' },
      },
    })

    await login('analyst', 'analyst123', false)

    expect(JSON.parse(sessionStorage.getItem(SESSION_KEY))).toMatchObject({
      token: TEST_TOKEN,
    })
    expect(localStorage.getItem(SESSION_KEY)).toBeNull()
  })

  it('returns the stored session and clears it on logout', async () => {
    apiClient.post.mockResolvedValue({
      data: {
        token: TEST_TOKEN,
        user: { username: 'admin', role: 'ADMIN' },
      },
    })

    await login('admin', 'admin123', true)
    expect(getSession()).toMatchObject({
      token: TEST_TOKEN,
      user: { username: 'admin', role: 'ADMIN' },
    })

    logout()
    expect(getSession()).toBeNull()
  })
})
