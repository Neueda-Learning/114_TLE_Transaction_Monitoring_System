import { useMemo } from 'react'
import { Link } from 'react-router-dom'
import Card from '../components/Common/Card'
import Table from '../components/Common/Table'
import Badge from '../components/Common/Badge'
import AlertChart from '../components/Charts/AlertChart'
import TransactionChart from '../components/Charts/TransactionChart'
import { useAppData } from '../context/useAppData'
import {
  formatCompactNumber,
  formatCurrency,
  formatDateTime,
} from '../utils/formatters'

const statusOrder = ['OPEN', 'ACKNOWLEDGED', 'INVESTIGATING', 'CLOSED', 'DISMISSED']

function DashboardPage() {
  const { transactions, alerts, isLoading, error } = useAppData()

  const summary = useMemo(() => {
    const activeAlerts = alerts.filter((alert) =>
      ['OPEN', 'ACKNOWLEDGED', 'INVESTIGATING'].includes(alert.status),
    )

    const highRiskAlerts = alerts.filter((alert) => alert.riskLevel === 'HIGH')
    const resolvedAlerts = alerts.filter((alert) =>
      ['CLOSED', 'DISMISSED'].includes(alert.status),
    )

    return {
      totalTransactions: transactions.length,
      activeAlerts: activeAlerts.length,
      highRiskAlerts: highRiskAlerts.length,
      resolvedAlerts: resolvedAlerts.length,
      avgResolutionTime: '2h 14m',
    }
  }, [transactions, alerts])

  const severityChartData = useMemo(
    () =>
      ['HIGH', 'MEDIUM', 'LOW'].map((level) => ({
        name: level,
        value: alerts.filter((alert) => alert.riskLevel === level).length,
      })),
    [alerts],
  )

  const statusChartData = useMemo(
    () =>
      statusOrder.map((status) => ({
        name: status,
        value: alerts.filter((alert) => alert.status === status).length,
      })),
    [alerts],
  )

  const trendData = useMemo(() => {
    const byHour = {}

    transactions.forEach((transaction) => {
      const hour = new Date(transaction.timestamp).getHours()
      const label = `${String(hour).padStart(2, '0')}:00`
      if (!byHour[label]) {
        byHour[label] = { time: label, volume: 0, alerts: 0 }
      }
      byHour[label].volume += 1
      if (transaction.riskStatus !== 'NORMAL') {
        byHour[label].alerts += 1
      }
    })

    return Object.values(byHour)
  }, [transactions])

  const suspiciousRows = useMemo(
    () =>
      alerts
        .filter((alert) => ['HIGH', 'MEDIUM'].includes(alert.riskLevel))
        .slice(0, 6),
    [alerts],
  )

  const recentColumns = [
    { key: 'id', label: 'Alert ID' },
    { key: 'transactionId', label: 'Transaction ID' },
    { key: 'ruleName', label: 'Rule Triggered' },
    {
      key: 'riskLevel',
      label: 'Risk Level',
      render: (value) => <Badge label={value} variant={value} />,
    },
    {
      key: 'amount',
      label: 'Amount',
      render: (value) => formatCurrency(value),
    },
    {
      key: 'status',
      label: 'Status',
      render: (value) => <Badge label={value} variant={value} />,
    },
    {
      key: 'createdTime',
      label: 'Time',
      render: (value) => formatDateTime(value),
    },
  ]

  if (error) {
    return (
      <section className="state-panel surface-elevated">
        <h2>Unable to load monitoring dashboard</h2>
        <p>{error}</p>
      </section>
    )
  }

  return (
    <section className="page-wrap">
      <div className="cards-grid">
        <Card
          title="Total Transactions"
          value={formatCompactNumber(summary.totalTransactions)}
          subtitle="Across monitored channels"
          trend={{ type: 'up', label: '+8.2%' }}
        />
        <Card
          title="Active Alerts"
          value={summary.activeAlerts}
          subtitle="Requires analyst attention"
          trend={{ type: 'down', label: '-3.1%' }}
        />
        <Card
          title="High Risk Alerts"
          value={summary.highRiskAlerts}
          subtitle="Immediate escalation"
          trend={{ type: 'up', label: '+1.7%' }}
        />
        <Card
          title="Resolved Alerts"
          value={summary.resolvedAlerts}
          subtitle="Closed or dismissed"
        />
        <Card
          title="Avg Resolution Time"
          value={summary.avgResolutionTime}
          subtitle="Last 24 hours"
        />
      </div>

      <div className="chart-grid">
        <article className="chart-card surface-elevated">
          <div className="block-head">
            <h2>Alert Severity Distribution</h2>
          </div>
          <AlertChart type="severity" data={severityChartData} />
        </article>

        <article className="chart-card surface-elevated">
          <div className="block-head">
            <h2>Alert Status Breakdown</h2>
          </div>
          <AlertChart type="status" data={statusChartData} />
        </article>
      </div>

      <article className="chart-card surface-elevated">
        <div className="block-head">
          <h2>Transaction Volume Trend</h2>
        </div>
        <TransactionChart data={trendData} />
      </article>

      <article className="section-card">
        <div className="block-head">
          <h2>Recent Suspicious Activity</h2>
          <Link className="inline-link" to="/alerts">
            View all alerts
          </Link>
        </div>

        <Table
          columns={recentColumns}
          data={suspiciousRows}
          rowKey="id"
          isLoading={isLoading}
          emptyMessage="No suspicious activity in this period."
        />
      </article>
    </section>
  )
}

export default DashboardPage
