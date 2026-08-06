import React from 'react'
import { render, screen } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { describe, expect, it, vi } from 'vitest'
import DashboardPage from './DashboardPage'

vi.mock('../context/useAppData', () => ({
  useAppData: vi.fn(),
}))

import { useAppData } from '../context/useAppData'

describe('DashboardPage', () => {
  it('renders live summary cards from app data', () => {
    useAppData.mockReturnValue({
      isLoading: false,
      error: '',
      transactions: [
        { id: '1', amount: 1000, timestamp: '2026-08-06T09:00:00Z', riskStatus: 'NORMAL' },
        { id: '2', amount: 2000, timestamp: '2026-08-06T10:00:00Z', riskStatus: 'SUSPICIOUS' },
      ],
      alerts: [
        {
          id: 'A1',
          transactionId: '2',
          ruleName: 'Amount Threshold',
          riskLevel: 'HIGH',
          amount: 2000,
          status: 'OPEN',
          createdTime: '2026-08-06T10:05:00Z',
          timeline: [],
        },
      ],
    })

    render(
      <MemoryRouter>
        <DashboardPage />
      </MemoryRouter>,
    )

    expect(screen.getByText('Total Transactions')).toBeInTheDocument()
    expect(screen.getByText('2', { selector: '.card-value' })).toBeInTheDocument()
    expect(screen.getByText('Active Alerts')).toBeInTheDocument()
    expect(screen.getByText('High Risk Alerts')).toBeInTheDocument()
    expect(screen.getByText('Recent Suspicious Activity')).toBeInTheDocument()
    expect(screen.getByText('Amount Threshold')).toBeInTheDocument()
  })

  it('shows dashboard error state when loading fails', () => {
    useAppData.mockReturnValue({
      isLoading: false,
      error: 'Unable to load dashboard data.',
      transactions: [],
      alerts: [],
    })

    render(
      <MemoryRouter>
        <DashboardPage />
      </MemoryRouter>,
    )

    expect(screen.getByText('Unable to load monitoring dashboard')).toBeInTheDocument()
    expect(screen.getByText('Unable to load dashboard data.')).toBeInTheDocument()
  })
})
