import React from 'react'
import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, expect, it, vi } from 'vitest'
import LoginCard from './LoginCard'

describe('LoginCard', () => {
  it('submits username, password, and rememberMe selection', async () => {
    const user = userEvent.setup()
    const onSubmit = vi.fn().mockResolvedValue(undefined)

    render(
      <LoginCard
        defaultUsername=""
        onSubmit={onSubmit}
        isSubmitting={false}
        error=""
        onForgotPassword={vi.fn()}
      />,
    )

    await user.type(screen.getByLabelText(/username/i), 'admin')
    await user.type(screen.getByLabelText(/^password$/i, { selector: 'input' }), 'admin123')
    await user.click(screen.getByRole('checkbox', { name: /remember me/i }))
    await user.click(screen.getByRole('button', { name: /sign in/i }))

    expect(onSubmit).toHaveBeenCalledWith({
      username: 'admin',
      password: 'admin123',
      rememberMe: true,
    })
  })

  it('renders an auth error message when provided', () => {
    render(
      <LoginCard
        defaultUsername="admin"
        onSubmit={vi.fn()}
        isSubmitting={false}
        error="Invalid username or password."
        onForgotPassword={vi.fn()}
      />,
    )

    expect(screen.getByRole('alert')).toHaveTextContent('Invalid username or password.')
  })
})
