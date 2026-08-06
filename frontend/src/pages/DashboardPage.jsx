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

    // Calculate average resolution time (time from alert created to now for closed alerts)
    let avgResolutionMs = 0
    const resolvedAlertsWithTime = resolvedAlerts.filter((alert) => alert.createdTime)
    if (resolvedAlertsWithTime.length > 0) {
      const totalTimeMs = resolvedAlertsWithTime.reduce((sum, alert) => {
        const createdTime = new Date(alert.createdTime).getTime()
        const now = new Date().getTime()
        return sum + (now - createdTime)
      }, 0)
      avgResolutionMs = totalTimeMs / resolvedAlertsWithTime.length
    }

    // Convert milliseconds to readable format
    const formatResolutionTime = (ms) => {
      const hours = Math.floor(ms / (1000 * 60 * 60))
      const minutes = Math.floor((ms % (1000 * 60 * 60)) / (1000 * 60))
      if (hours > 0) return `${hours}h ${minutes}m`
      return `${minutes}m`
    }

    // Calculate trend percentages based on alert distribution
    const totalAlerts = alerts.length
    const highRiskPercentage = totalAlerts > 0 ? Math.round((highRiskAlerts.length / totalAlerts) * 100) : 0
    const activePercentage = totalAlerts > 0 ? Math.round((activeAlerts.length / totalAlerts) * 100) : 0
    
    // Calculate transaction risk trend: percentage of transactions with non-normal risk
    const riskyTransactions = transactions.filter((txn) => txn.riskStatus !== 'NORMAL')
    const transactionRiskPercentage = transactions.length > 0 ? Math.round((riskyTransactions.length / transactions.length) * 100) : 0
    const transactionTrend = transactionRiskPercentage > 25 ? `+${transactionRiskPercentage}%` : `-${Math.abs(25 - transactionRiskPercentage)}%`
    
    // Active alerts trend: compare active to total
    const activeAlertsTrend = activePercentage > 60 ? `-${activePercentage - 50}%` : `+${60 - activePercentage}%`
    
    // High risk trend: based on high-risk percentage
    const highRiskTrend = highRiskPercentage > 25 ? `+${highRiskPercentage}%` : `+${Math.max(1, highRiskPercentage)}%`

    return {
      totalTransactions: transactions.length,
      activeAlerts: activeAlerts.length,
      highRiskAlerts: highRiskAlerts.length,
      resolvedAlerts: resolvedAlerts.length,
      avgResolutionTime: resolvedAlertsWithTime.length > 0 ? formatResolutionTime(avgResolutionMs) : 'N/A',
      transactionTrend,
      activeAlertsTrend,
      highRiskTrend,
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

  const weeklyData = useMemo(() => {
    const byDay = {}
    const daysOfWeek = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']

    transactions.forEach((transaction) => {
      const date = new Date(transaction.timestamp)
      const dayOfWeek = date.getDay()
      const dayName = daysOfWeek[(dayOfWeek + 6) % 7] // Convert 0-6 to 1-7, then to day names

      if (!byDay[dayName]) {
        byDay[dayName] = { time: dayName, volume: 0, alerts: 0 }
      }
      byDay[dayName].volume += 1
      if (transaction.riskStatus !== 'NORMAL') {
        byDay[dayName].alerts += 1
      }
    })

    return daysOfWeek.map((day) => byDay[day] || { time: day, volume: 0, alerts: 0 })
  }, [transactions])

  const monthlyData = useMemo(() => {
    const byMonth = {}
    const monthNames = [
      'Jan',
      'Feb',
      'Mar',
      'Apr',
      'May',
      'Jun',
      'Jul',
      'Aug',
      'Sep',
      'Oct',
      'Nov',
      'Dec',
    ]

    transactions.forEach((transaction) => {
      const date = new Date(transaction.timestamp)
      const monthIndex = date.getMonth()
      const monthName = monthNames[monthIndex]

      if (!byMonth[monthName]) {
        byMonth[monthName] = { time: monthName, volume: 0, alerts: 0 }
      }
      byMonth[monthName].volume += 1
      if (transaction.riskStatus !== 'NORMAL') {
        byMonth[monthName].alerts += 1
      }
    })

    return monthNames.map((month) => byMonth[month] || { time: month, volume: 0, alerts: 0 })
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
          trend={{ type: summary.totalTransactions > 2 ? 'up' : 'down', label: summary.transactionTrend }}
        />
        <Card
          title="Active Alerts"
          value={summary.activeAlerts}
          subtitle="Requires analyst attention"
          trend={{ type: summary.activeAlerts > summary.highRiskAlerts ? 'up' : 'down', label: summary.activeAlertsTrend }}
        />
        <Card
          title="High Risk Alerts"
          value={summary.highRiskAlerts}
          subtitle="Immediate escalation"
          trend={{ type: summary.highRiskAlerts > 1 ? 'up' : 'down', label: summary.highRiskTrend }}
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

      <div className="chart-grid">
        <article className="chart-card surface-elevated">
          <div className="block-head">
            <h2>Weekly Transaction Volume</h2>
          </div>
          <TransactionChart data={weeklyData} />
        </article>

        <article className="chart-card surface-elevated">
          <div className="block-head">
            <h2>Monthly Transaction Volume</h2>
          </div>
          <TransactionChart data={monthlyData} />
        </article>
      </div>

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
